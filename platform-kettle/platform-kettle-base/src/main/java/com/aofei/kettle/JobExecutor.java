package com.aofei.kettle;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.pentaho.di.cluster.SlaveServer;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.Result;
import org.pentaho.di.core.gui.JobTracker;
import org.pentaho.di.core.logging.KettleLogLayout;
import org.pentaho.di.core.logging.KettleLogStore;
import org.pentaho.di.core.logging.KettleLoggingEvent;
import org.pentaho.di.core.logging.LoggingObjectType;
import org.pentaho.di.core.logging.LoggingRegistry;
import org.pentaho.di.core.logging.SimpleLoggingObject;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.job.Job;
import org.pentaho.di.job.JobEntryResult;
import org.pentaho.di.job.JobExecutionConfiguration;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.job.entry.JobEntryCopy;
import org.pentaho.di.ui.spoon.job.JobEntryCopyResult;
import org.pentaho.di.www.SlaveServerJobStatus;

import com.aofei.kettle.utils.JSONArray;
import com.aofei.kettle.utils.JSONObject;

public class JobExecutor implements Runnable {

	private String executionId;
	private JobExecutionConfiguration executionConfiguration;
	private JobMeta jobMeta = null;
	private Job job = null;
	private static final Class PKG = JobEntryCopyResult.class;
//	private Map<StepMeta, String> stepLogMap = new HashMap<StepMeta, String>();
	
	private JobExecutor(JobExecutionConfiguration executionConfiguration, JobMeta jobMeta) {
		this.executionId = UUID.randomUUID().toString().replaceAll("-", "");
		this.executionConfiguration = executionConfiguration;
		this.jobMeta = jobMeta;
	}
	
	private static Hashtable<String, JobExecutor> executors = new Hashtable<String, JobExecutor>();
	
	public static synchronized JobExecutor initExecutor(JobExecutionConfiguration executionConfiguration, JobMeta jobMeta) {
		JobExecutor jobExecutor = new JobExecutor(executionConfiguration, jobMeta);
		executors.put(jobExecutor.getExecutionId(), jobExecutor);
		return jobExecutor;
	}

	public String getExecutionId() {
		return executionId;
	}
	
	private boolean finished = false;
	private long errCount = 0;

	@Override
	public void run() {
		try {
			for (String varName : executionConfiguration.getVariables().keySet()) {
				String varValue = executionConfiguration.getVariables().get(varName);
				jobMeta.setVariable(varName, varValue);
			}
			
			for (String paramName : executionConfiguration.getParams().keySet()) {
				String paramValue = executionConfiguration.getParams().get(paramName);
				jobMeta.setParameterValue(paramName, paramValue);
			}
			
			if (executionConfiguration.isExecutingLocally()) {
				 SimpleLoggingObject spoonLoggingObject = new SimpleLoggingObject( "SPOON", LoggingObjectType.SPOON, null );
			     spoonLoggingObject.setContainerObjectId( executionId );
			     spoonLoggingObject.setLogLevel( executionConfiguration.getLogLevel() );
			     job = new Job( App.getInstance().getRepository(), jobMeta, spoonLoggingObject );
				
				job.setLogLevel(executionConfiguration.getLogLevel());
				job.shareVariablesWith(jobMeta);
				job.setInteractive(true);
				job.setGatheringMetrics(executionConfiguration.isGatheringMetrics());
				job.setArguments(executionConfiguration.getArgumentStrings());

				job.getExtensionDataMap().putAll(executionConfiguration.getExtensionOptions());

				// If there is an alternative start job entry, pass it to the job
	            //
	            if ( !Const.isEmpty( executionConfiguration.getStartCopyName() ) ) {
	            	JobEntryCopy startJobEntryCopy = jobMeta.findJobEntry( executionConfiguration.getStartCopyName(), executionConfiguration.getStartCopyNr(), false );
	            	job.setStartJobEntryCopy( startJobEntryCopy );
	            }

	            // Set the named parameters
	            Map<String, String> paramMap = executionConfiguration.getParams();
	            Set<String> keys = paramMap.keySet();
				for (String key : keys) {
					job.getJobMeta().setParameterValue(key, Const.NVL(paramMap.get(key), ""));
				}
	            job.getJobMeta().activateParameters();

	            job.start();
				
				while(!job.isFinished()) {
					Thread.sleep(500);
				}
				
				errCount = job.getErrors();
			} else if (executionConfiguration.isExecutingRemotely()) {
				try {
					carteObjectId = Job.sendToSlaveServer( jobMeta, executionConfiguration, App.getInstance().getRepository(), App.getInstance().getMetaStore() );
					SlaveServer remoteSlaveServer = executionConfiguration.getRemoteServer();
					
					boolean running = true;
					while(running) {
						SlaveServerJobStatus jobStatus = remoteSlaveServer.getJobStatus(jobMeta.getName(), carteObjectId, 0);
						running = jobStatus.isRunning();
						
						if(!running && jobStatus.getResult() != null) {
							errCount = jobStatus.getResult().getNrErrors();
						}
							
						Thread.sleep(500);
					}
				} catch(Exception e) {
					e.printStackTrace();
					errCount = 1000;
				}
			}
			
		} catch(Exception e) {
			e.printStackTrace();
			App.getInstance().getLog().logError("执行失败！", e);
		} finally {
			finished = true;
		}
	}
	
	public boolean isFinished() {
		return finished;
	}
	
	public long getErrCount() {
		return errCount;
	}

	private String carteObjectId = null;
	
	public int previousNrItems;
	public JSONArray getJobMeasure() throws Exception {
    	JSONArray jsonArray = new JSONArray();
    	if(executionConfiguration.isExecutingLocally()) {
    		JobTracker jobTracker = job.getJobTracker();
        	int nrItems = jobTracker.getTotalNumberOfItems();
        	if ( nrItems != previousNrItems ) {
                // Re-populate this...
                String jobName = jobTracker.getJobName();

    			if (Const.isEmpty(jobName)) {
    				if (!Const.isEmpty(jobTracker.getJobFilename())) {
    					jobName = jobTracker.getJobFilename();
    				} else {
    					jobName = BaseMessages.getString(PKG, "JobLog.Tree.StringToDisplayWhenJobHasNoName");
    				}
    			}
    			
    			JSONObject jsonObject = new JSONObject();
    			jsonObject.put("name", jobName);
    			jsonObject.put("expanded", true);

    			JSONArray children = new JSONArray();
                for ( int i = 0; i < jobTracker.nrJobTrackers(); i++ ) {
                	JSONObject jsonObject2 = addTrackerToTree(jobTracker.getJobTracker(i));
                	if(jsonObject2 != null)
                		children.add(jsonObject2);
                }
                jsonObject.put("children", children);
                jsonArray.add(jsonObject);
                
                previousNrItems = nrItems;
        	}
    	}
    	return jsonArray;
	}
	
	private JSONObject addTrackerToTree( JobTracker jobTracker ) {
		JSONObject jsonObject = new JSONObject();
		if ( jobTracker != null ) {
			if ( jobTracker.nrJobTrackers() > 0 ) {
	    		  // This is a sub-job: display the name at the top of the list...
	    		  jsonObject.put("name", BaseMessages.getString( PKG, "JobLog.Tree.JobPrefix" ) + jobTracker.getJobName() );
	    		  jsonObject.put("expanded", true);
	    		  JSONArray children = new JSONArray();
	    		  // then populate the sub-job entries ...
	    		  for ( int i = 0; i < jobTracker.nrJobTrackers(); i++ ) {
	    			  JSONObject jsonObject2 = addTrackerToTree( jobTracker.getJobTracker( i ) );
	    			  if(jsonObject2 != null)
	    				  children.add(jsonObject2);
	    		  }
	    		  jsonObject.put("children", children);
			} else {
	        	JobEntryResult result = jobTracker.getJobEntryResult();
	        	if ( result != null ) {
	        		String jobEntryName = result.getJobEntryName();
					if (!Const.isEmpty(jobEntryName)) {
						jsonObject.put("name", jobEntryName);
						jsonObject.put("fileName", Const.NVL(result.getJobEntryFilename(), ""));
					} else {
						jsonObject.put("name", BaseMessages.getString(PKG, "JobLog.Tree.JobPrefix2") + jobTracker.getJobName());
					}
					String comment = result.getComment();
					if (comment != null) {
						jsonObject.put("comment", comment);
					}
					Result res = result.getResult();
					if ( res != null ) {
						jsonObject.put("result",  res.getResult() ? BaseMessages.getString( PKG, "JobLog.Tree.Success" ) : BaseMessages.getString(PKG, "JobLog.Tree.Failure" ));
	              		jsonObject.put("number", Long.toString( res.getEntryNr()));
					}
					String reason = result.getReason();
					if (reason != null) {
						jsonObject.put("reason", reason);
					}
					Date logDate = result.getLogDate();
					if (logDate != null) {
						jsonObject.put("logDate", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(logDate));
					}
					jsonObject.put("leaf", true);
	          } else 
	        	  return null;
	        }
	      } else 
	    	  return null;
		return jsonObject;
	}
	
	
	public String getExecutionLog() throws Exception {
		if(executionConfiguration.isExecutingLocally()) {
			StringBuffer sb = new StringBuffer();
			KettleLogLayout logLayout = new KettleLogLayout( true );
			List<String> childIds = LoggingRegistry.getInstance().getLogChannelChildren( job.getLogChannelId() );
			List<KettleLoggingEvent> logLines = KettleLogStore.getLogBufferFromTo( childIds, true, -1, KettleLogStore.getLastBufferLineNr() );
			 for ( int i = 0; i < logLines.size(); i++ ) {
	             KettleLoggingEvent event = logLines.get( i );
	             String line = logLayout.format( event ).trim();
	             sb.append(line).append("\n");
			 }
			 return sb.toString();
    	} else {
    		SlaveServer remoteSlaveServer = executionConfiguration.getRemoteServer();
			SlaveServerJobStatus jobStatus = remoteSlaveServer.getJobStatus(jobMeta.getName(), carteObjectId, 0);
			return jobStatus.getLoggingString();
    	}
		
	}
	
public JSONArray getStepStatus() throws Exception {
		
		JSONArray jsonArray = new JSONArray();
		if(executionConfiguration.isExecutingLocally()) {
			HashSet<String> finishEntries = new HashSet<String>();
			List<JobEntryResult> jobEntryResults = job.getJobEntryResults();
			for (JobEntryResult jobEntryResult : jobEntryResults) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("stepName", jobEntryResult.getJobEntryName());
				
				if(jobEntryResult.getResult().getResult())
					jsonObject.put("stepStatus", 0);
				else
					jsonObject.put("stepStatus", 1);
				
				finishEntries.add(jobEntryResult.getJobEntryName());
				jsonArray.add(jsonObject);
			}
			
			Set<JobEntryCopy> busyEntries = new HashSet<JobEntryCopy>();
			if (job.getActiveJobEntryJobs().size() > 0) {
				busyEntries.addAll(job.getActiveJobEntryJobs().keySet());
			}
			if (job.getActiveJobEntryTransformations().size() > 0) {
				busyEntries.addAll(job.getActiveJobEntryTransformations().keySet());
			}
			
			for(JobEntryCopy entry : jobMeta.getJobCopies()) {
				if(busyEntries.contains(entry)) {
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("stepName", entry.getName());
					jsonObject.put("stepStatus", -1);
					jsonArray.add(jsonObject);
				} else {
					if(!finishEntries.contains(entry.getName())) {
						JSONObject jsonObject = new JSONObject();
						jsonObject.put("stepName", entry.getName());
						jsonObject.put("stepStatus", -2);
						jsonArray.add(jsonObject);
					}
				}
			}
		}
		return jsonArray;
	}
	
	public static JobExecutor getExecutor(String executionId) {
		return executors.get(executionId);
	}
	
	public static void remove(String executionId) {
		executors.remove(executionId);
	}
	
	public void stop() throws Exception {
		if (executionConfiguration.isExecutingLocally()) {
			job.stopAll();
		} else if (executionConfiguration.isExecutingRemotely()) {
			SlaveServer remoteSlaveServer = executionConfiguration.getRemoteServer();
			remoteSlaveServer.stopJob(jobMeta.getName(), carteObjectId);
		}
		
	}

	public Job getJob() {
		return job;
	}
	
}
