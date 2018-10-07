package com.aofei.schedule.job;


import com.alibaba.fastjson.JSONObject;
import com.aofei.base.common.Const;
import com.aofei.kettle.utils.StringEscapeHelper;
import org.pentaho.di.job.JobExecutionConfiguration;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.repository.RepositoryDirectoryInterface;
import org.pentaho.di.trans.steps.jobexecutor.JobExecutor;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

public class JobRunner extends QuartzJobBean {

	@Override
	public void executeInternal(JobExecutionContext context) throws JobExecutionException {
		try {
			String path = context.getJobDetail().getKey().getName();
			String dir = path.substring(0, path.lastIndexOf("/"));
			String name = path.substring(path.lastIndexOf("/") + 1);
			
			Repository repository = Const.repositorys.get("");
			RepositoryDirectoryInterface directory = repository.findDirectory(dir);
			if(directory == null)
				directory = repository.getUserHomeDirectory();
			
			JobMeta jobMeta = repository.loadJob(name, directory, null, null);
			
			JSONObject jsonObject = JSONObject.fromObject(context.getMergedJobDataMap().getString("executionConfiguration"));
			JobExecutionConfiguration jobExecutionConfiguration = JobExecutionConfigurationCodec.decode(jsonObject, jobMeta);
		    
		    JobExecutor jobExecutor = JobExecutor.initExecutor(jobExecutionConfiguration, jobMeta);
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
			context.getMergedJobDataMap().put("execMethod", jobExecutionConfiguration.isExecutingLocally() ? "本地" : "远程:" + jobExecutionConfiguration.getRemoteServer().getName());
			context.getMergedJobDataMap().put("error", jobExecutor.getErrCount());
			context.getMergedJobDataMap().put("executionLog", result.toString());
		    
		} catch(Exception e) {
			throw new JobExecutionException(e);
		}
	}

}
