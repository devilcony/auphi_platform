package com.aofei.schedule.job;


import com.alibaba.fastjson.JSON;
import com.aofei.base.common.Const;
import com.aofei.kettle.App;
import com.aofei.kettle.TransExecutor;
import com.aofei.kettle.utils.JSONObject;
import com.aofei.schedule.model.request.GeneralScheduleRequest;
import org.pentaho.di.core.RowMetaAndData;
import org.pentaho.di.core.logging.DefaultLogLevel;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.repository.RepositoryDirectoryInterface;
import org.pentaho.di.trans.TransExecutionConfiguration;
import org.pentaho.di.trans.TransMeta;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.HashMap;
import java.util.Map;

public class TransRunner extends QuartzJobBean {

	@Override
	public void executeInternal(JobExecutionContext context) throws JobExecutionException {
		try {
            String json = (String) context.getJobDetail().getJobDataMap().get(Const.GENERAL_SCHEDULE_KEY);

            GeneralScheduleRequest request = JSON.parseObject(json,GeneralScheduleRequest.class);

			String dir = request.getFilePath();
			String name = request.getFile();

			Repository repository = App.getInstance().getRepository();

			RepositoryDirectoryInterface directory = repository.findDirectory(dir);
			if(directory == null)
				directory = repository.getUserHomeDirectory();

			TransMeta transMeta = repository.loadTransformation(name, directory, null, true, null);

			TransExecutionConfiguration executionConfiguration = App.getInstance().getTransExecutionConfiguration();

			if (transMeta.findFirstUsedClusterSchema() != null) {
				executionConfiguration.setExecutingLocally(false);
				executionConfiguration.setExecutingRemotely(false);
				executionConfiguration.setExecutingClustered(true);
			} else {
				executionConfiguration.setExecutingLocally(true);
				executionConfiguration.setExecutingRemotely(false);
				executionConfiguration.setExecutingClustered(false);
			}

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
			executionConfiguration.getUsedVariables( transMeta );
			executionConfiguration.getUsedArguments(transMeta, App.getInstance().getArguments());
			executionConfiguration.setReplayDate( null );
			executionConfiguration.setRepository( App.getInstance().getRepository() );
			executionConfiguration.setSafeModeEnabled( false );

			executionConfiguration.setLogLevel( DefaultLogLevel.getLogLevel() );

			// Fill the parameters, maybe do this in another place?
			Map<String, String> params = executionConfiguration.getParams();
			params.clear();
			String[] paramNames = transMeta.listParameters();
			for (String key : paramNames) {
				params.put(key, "");
			}

		    TransExecutor transExecutor = TransExecutor.initExecutor(executionConfiguration, transMeta);
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
			if(executionConfiguration.isExecutingRemotely())
				execMethod = "远程:" + executionConfiguration.getRemoteServer().getName();
			else if(executionConfiguration.isExecutingClustered())
				execMethod = "集群";
			context.getMergedJobDataMap().put("execMethod",  execMethod );
			context.getMergedJobDataMap().put("error", transExecutor.getErrCount());
			context.getMergedJobDataMap().put("executionLog", result.toString());

		} catch(Exception e) {
			throw new JobExecutionException(e);
		}

	}

}
