package com.aofei.schedule.job;


import com.aofei.kettle.App;
import com.aofei.kettle.JobExecutor;
import com.aofei.kettle.utils.JSONObject;
import com.aofei.kettle.utils.StringEscapeHelper;
import org.pentaho.di.core.RowMetaAndData;
import org.pentaho.di.core.logging.DefaultLogLevel;
import org.pentaho.di.job.JobExecutionConfiguration;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.repository.RepositoryDirectoryInterface;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.HashMap;
import java.util.Map;

public class JobRunner extends QuartzJobBean {

	@Override
	public void executeInternal(JobExecutionContext context) throws JobExecutionException {
		try {
			String path = context.getJobDetail().getKey().getName();
			String dir = path.substring(0, path.lastIndexOf("/"));
			String name = path.substring(path.lastIndexOf("/") + 1);

			Repository repository = App.getInstance().getRepository();
			RepositoryDirectoryInterface directory = repository.findDirectory(dir);
			if(directory == null)
				directory = repository.getUserHomeDirectory();

			JobMeta jobMeta = repository.loadJob(name, directory, null, null);


			JobExecutionConfiguration executionConfiguration = App.getInstance().getJobExecutionConfiguration();

			// Remember the variables set previously
			//
			RowMetaAndData variables = App.getInstance().getVariables();
			Object[] data = variables.getData();
			String[] fields = variables.getRowMeta().getFieldNames();
			Map<String, String> variableMap = new HashMap<String, String>();
			for ( int idx = 0; idx < fields.length; idx++ ) {
				variableMap.put( fields[idx], data[idx].toString() );
			}

			executionConfiguration.setVariables( variableMap );
			executionConfiguration.getUsedVariables( jobMeta );
			executionConfiguration.setReplayDate( null );
			executionConfiguration.setRepository( App.getInstance().getRepository() );
			executionConfiguration.setSafeModeEnabled( false );
			executionConfiguration.setStartCopyName( null );
			executionConfiguration.setStartCopyNr( 0 );

			executionConfiguration.setLogLevel( DefaultLogLevel.getLogLevel() );

			// Fill the parameters, maybe do this in another place?
			Map<String, String> params = executionConfiguration.getParams();
			params.clear();
			String[] paramNames = jobMeta.listParameters();
			for (String key : paramNames) {
				params.put(key, "");
			}

		    JobExecutor jobExecutor = JobExecutor.initExecutor(executionConfiguration, jobMeta);
		    Thread tr = new Thread(jobExecutor, "JobExecutor_" + jobExecutor.getExecutionId());
		    tr.start();

		    while(!jobExecutor.isFinished()) {
		    	Thread.sleep(1000);
		    }

		    JSONObject result = new JSONObject();
		    result.put("finished", jobExecutor.isFinished());
			if(jobExecutor.isFinished()) {
				result.put("jobMeasure", jobExecutor.getJobMeasure());
				result.put("log", StringEscapeHelper.encode(jobExecutor.getExecutionLog()));
			} else {
				result.put("jobMeasure", jobExecutor.getJobMeasure());
				result.put("log", StringEscapeHelper.encode(jobExecutor.getExecutionLog()));
			}
			context.getMergedJobDataMap().put("execMethod", executionConfiguration.isExecutingLocally() ? "本地" : "远程:" + executionConfiguration.getRemoteServer().getName());
			context.getMergedJobDataMap().put("error", jobExecutor.getErrCount());
			context.getMergedJobDataMap().put("executionLog", result.toString());

		} catch(Exception e) {
			throw new JobExecutionException(e);
		}
	}

}
