package com.aofei.kettle.factorys;

import javax.sql.DataSource;

import com.aofei.kettle.App;
import com.aofei.kettle.core.database.Database;
import com.aofei.kettle.repository.KettleDataSourceRepository;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.logging.KettleLogStore;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.repository.kdr.KettleDatabaseRepositoryMeta;
import org.springframework.beans.factory.FactoryBean;

public class RepositoryDataSourceFactory implements FactoryBean<Repository> {

	private String repositoryName;

	private DataSource dataSource;

	public String getRepositoryName() {
		return repositoryName;
	}

	public void setRepositoryName(String repositoryName) {
		this.repositoryName = repositoryName;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public Repository getObject() throws Exception {
		DatabaseMeta databaseMeta = new DatabaseMeta();
		databaseMeta.setDatabaseType("MYSQL");

		Database database = new Database(databaseMeta);
		database.setDataSource(dataSource);
		database.connect();

		KettleDatabaseRepositoryMeta repositoryMeta = new KettleDatabaseRepositoryMeta(repositoryName, repositoryName, "", databaseMeta);

		KettleDataSourceRepository repository = new KettleDataSourceRepository(database);
		repository.init(repositoryMeta);

		return repository;
	}

	@Override
	public Class<?> getObjectType() {
		return Repository.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

}
