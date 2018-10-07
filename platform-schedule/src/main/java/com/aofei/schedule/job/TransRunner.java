package com.aofei.schedule.job;


import com.alibaba.fastjson.JSONObject;
import com.aofei.base.common.Const;
import com.aofei.kettle.core.trans.TransExecutionConfigurationCodec;
import com.aofei.kettle.executor.TransExecutor;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.repository.RepositoryDirectoryInterface;
import org.pentaho.di.trans.TransExecutionConfiguration;
import org.pentaho.di.trans.TransMeta;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

public class TransRunner extends QuartzJobBean {

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
			
			TransMeta transMeta = repository.loadTransformation(name, directory, null, true, null);
			JSONObject jsonObject = JSONObject.parseObject(context.getMergedJobDataMap().getString("executionConfiguration"));
			TransExecutionConfiguration transExecutionConfiguration = TransExecutionConfigurationCodec.decode(jsonObject, transMeta);
		    
		    TransExecutor transExecutor = TransExecutor.initExecutor(repository,transExecutionConfiguration, transMeta);
		    Thread tr = new Thread(transExecutor, "TransExecutor_" + transExecutor.getExecutionId());
		    tr.start();
		    
		    while(!transExecutor.isFinished()) {
		    	Thread.sleep(1000);
		    }
		    
		    JSONObject result = new JSONObject();
		    result.put("finished", transExecutor.isFinished());
			if(transExecutor.isFinished()) {
				result.put("stepMeasure", transExecutor.getStepMeasure());
				result.put("log", transExecutor.getExecutionLog());
				result.put("stepStatus", transExecutor.getStepStatus());
			} else {
				result.put("stepMeasure", transExecutor.getStepMeasure());
				result.put("log", transExecutor.getExecutionLog());
				result.put("stepStatus", transExecutor.getStepStatus());
			}
			String execMethod = "本地";
			if(transExecutionConfiguration.isExecutingRemotely())
				execMethod = "远程:" + transExecutionConfiguration.getRemoteServer().getName();
			else if(transExecutionConfiguration.isExecutingClustered())
				execMethod = "集群";
			context.getMergedJobDataMap().put("execMethod",  execMethod );
			context.getMergedJobDataMap().put("error", transExecutor.getErrCount());
			context.getMergedJobDataMap().put("executionLog", result.toString());
		    
		} catch(Exception e) {
			throw new JobExecutionException(e);
		}
		
	}

}
