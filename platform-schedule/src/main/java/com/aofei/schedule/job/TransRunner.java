package com.aofei.schedule.job;


import com.alibaba.fastjson.JSON;
import com.aofei.base.common.Const;
import com.aofei.kettle.App;
import com.aofei.kettle.TransExecutor;
import com.aofei.schedule.model.request.GeneralScheduleRequest;
import com.aofei.translog.task.TransLogTimerTask;
import org.pentaho.di.core.RowMetaAndData;
import org.pentaho.di.core.logging.DefaultLogLevel;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.repository.RepositoryDirectoryInterface;
import org.pentaho.di.trans.TransExecutionConfiguration;
import org.pentaho.di.trans.TransMeta;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

public class TransRunner extends QuartzJobBean {

	private static Logger logger = LoggerFactory.getLogger(TransRunner.class);

	@Override
	public void executeInternal(JobExecutionContext context) throws JobExecutionException {
		try {
            String json = (String) context.getJobDetail().getJobDataMap().get(Const.GENERAL_SCHEDULE_KEY);

            GeneralScheduleRequest request = JSON.parseObject(json,GeneralScheduleRequest.class);

			String dir = request.getFilePath();
			String name = request.getFile();

			Repository repository = App.getInstance().getRepository();
			repository.connect(Const.REPOSITORY_USERNAME,Const.REPOSITORY_PASSWORD);
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
			repository.disconnect();
		    TransExecutor transExecutor = TransExecutor.initExecutor(executionConfiguration, transMeta);
			Timer logTimer = new Timer();

			Thread tr = new Thread(transExecutor, "TransExecutor_" + transExecutor.getExecutionId());
		    tr.start();
            TransLogTimerTask transTimerTask = new TransLogTimerTask(transExecutor);
			logTimer.schedule(transTimerTask, 0,10000);
		} catch(Exception e) {
			throw new JobExecutionException(e);
		}

	}

}
