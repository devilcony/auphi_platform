package com.aofei.kettle;

import com.aofei.base.common.Const;
import com.aofei.kettle.core.PropsUI;
import org.pentaho.di.core.DBCache;
import org.pentaho.di.core.RowMetaAndData;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.*;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.job.JobExecutionConfiguration;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.repository.kdr.KettleDatabaseRepository;
import org.pentaho.di.trans.TransExecutionConfiguration;
import org.pentaho.metastore.stores.delegate.DelegatingMetaStore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class App {

	private static App app;
	private LogChannelInterface log;

	private Map<String,Repository> repositorys;

	private TransExecutionConfiguration transExecutionConfiguration;
	private TransExecutionConfiguration transPreviewExecutionConfiguration;
	private TransExecutionConfiguration transDebugExecutionConfiguration;
	private JobExecutionConfiguration jobExecutionConfiguration;

	public PropsUI props;

	private App() {
		props = PropsUI.getInstance();
		log = new LogChannel( PropsUI.getAppName());
		loadSettings();

		transExecutionConfiguration = new TransExecutionConfiguration();
	    transExecutionConfiguration.setGatheringMetrics( true );
	    transPreviewExecutionConfiguration = new TransExecutionConfiguration();
	    transPreviewExecutionConfiguration.setGatheringMetrics( true );
	    transDebugExecutionConfiguration = new TransExecutionConfiguration();
	    transDebugExecutionConfiguration.setGatheringMetrics( true );

	    jobExecutionConfiguration = new JobExecutionConfiguration();

	    variables = new RowMetaAndData( new RowMeta() );
	}

	public void loadSettings() {
		LogLevel logLevel = LogLevel.getLogLevelForCode(props.getLogLevel());
		DefaultLogLevel.setLogLevel(logLevel);
		log.setLogLevel(logLevel);
		KettleLogStore.getAppender().setMaxNrLines(props.getMaxNrLinesInLog());

		// transMeta.setMaxUndo(props.getMaxUndo());
		DBCache.getInstance().setActive(props.useDBCache());
	}

	public static App getInstance() {
		if (app == null) {
			app = new App();
		}
		return app;
	}

	private Repository repository;

	public Repository getRepository() {

		return repository;
	}

	public void setRepository(Repository repository) {
		this.repository = repository;
	}

	private Map<String, Repository> repositories = Collections.synchronizedMap(new HashMap<String, Repository>());



//	private Repository defaultRepository;
//
//	public void initDefault(Repository defaultRepo) {
//		if(this.defaultRepository == null)
//			this.defaultRepository = defaultRepo;
//		this.repository = defaultRepo;
//	}
//
//	public Repository getDefaultRepository() {
//		return this.defaultRepository;
//	}

	public void selectRepository(Repository repo) {
		if(repository != null) {
			repository.disconnect();
		}
		repository = repo;
	}

	public void addRepository(Repository repository) {
		repositories.put(repository.getName(), repository);
	}


	private DelegatingMetaStore metaStore;

	public DelegatingMetaStore getMetaStore() {
		return metaStore;
	}

	public LogChannelInterface getLog() {
		return log;
	}

	private RowMetaAndData variables = null;
	private ArrayList<String> arguments = new ArrayList<String>();

	public String[] getArguments() {
		return arguments.toArray(new String[arguments.size()]);
	}

	public JobExecutionConfiguration getJobExecutionConfiguration() {
		return jobExecutionConfiguration;
	}

	public TransExecutionConfiguration getTransDebugExecutionConfiguration() {
		return transDebugExecutionConfiguration;
	}

	public TransExecutionConfiguration getTransPreviewExecutionConfiguration() {
		return transPreviewExecutionConfiguration;
	}

	public TransExecutionConfiguration getTransExecutionConfiguration() {
		return transExecutionConfiguration;
	}

	public RowMetaAndData getVariables() {
		return variables;
	}

	public Map<String, Repository> getRepositories() {
		return repositories;
	}

	public void setRepositories(Map<String, Repository> repositories) {
		this.repositories = repositories;
	}

	public Repository getRepository(String repositoryName) throws KettleException {
		KettleDatabaseRepository repository = (KettleDatabaseRepository) repositorys.get(repositoryName);
		if(repository!=null && !repository.isConnected()){
			repository.connect(Const.REPOSITORY_USERNAME,Const.REPOSITORY_PASSWORD);
		}

		return repository;
	}

	public void setRepository(String repositoryName, Repository repository) {
		repositories.put(repositoryName,repository);
	}



	//	public JSONArray encodeVariables() {
//		Object[] data = variables.getData();
//		String[] fields = variables.getRowMeta().getFieldNames();
//		JSONArray jsonArray = new JSONArray();
//		for (int i = 0; i < fields.length; i++) {
//			JSONObject jsonObject = new JSONObject();
//			jsonObject.put("name", fields[i]);
//			jsonObject.put("value", data[i].toString());
//			jsonArray.add(jsonObject);
//		}
//		return jsonArray;
//	}

}
