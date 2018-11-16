package com.aofei.kettle.factorys;

import java.io.File;

import org.pentaho.di.repository.Repository;
import org.pentaho.di.repository.filerep.KettleFileRepository;
import org.pentaho.di.repository.filerep.KettleFileRepositoryMeta;
import org.springframework.beans.factory.FactoryBean;

public class RepositoryFileFactory implements FactoryBean<Repository> {

	private String filePath = null;

	private String repositoryName;

	public String getRepositoryName() {
		return repositoryName;
	}

	public void setRepositoryName(String repositoryName) {
		this.repositoryName = repositoryName;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	@Override
	public Repository getObject() throws Exception {

		KettleFileRepositoryMeta meta = new KettleFileRepositoryMeta();
		File path = new File(filePath);
		meta.setBaseDirectory(path.getAbsolutePath());
		meta.setDescription(repositoryName);
		meta.setName(repositoryName);
		meta.setReadOnly(false);
		meta.setHidingHiddenFiles(true);

		KettleFileRepository repository = new KettleFileRepository();
		repository.init(meta);

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
