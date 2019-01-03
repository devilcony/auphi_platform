package com.aofei.kettle.repository;

import java.util.List;

import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.repository.BaseRepositorySecurityProvider;
import org.pentaho.di.repository.IUser;
import org.pentaho.di.repository.RepositorySecurityProvider;

public class KettleDataSourceRepositorySecurityProvider extends BaseRepositorySecurityProvider implements RepositorySecurityProvider {

	private KettleDataSourceRepository repository;

	public KettleDataSourceRepositorySecurityProvider(KettleDataSourceRepository repository, IUser userInfo) {
		super(repository.getRepositoryMeta(), userInfo);
		this.repository = repository;
	}

	@Override
	public boolean isReadOnly() {
		return getRepositoryMeta().getRepositoryCapabilities().isReadOnly();
	}

	@Override
	public boolean isLockingPossible() {
		return false;
	}

	@Override
	public boolean allowsVersionComments(String fullPath) {
		return false;
	}

	@Override
	public boolean isVersionCommentMandatory() {
		return false;
	}

	@Override
	public List<String> getAllUsers() throws KettleException {
		return null;
	}

	@Override
	public List<String> getAllRoles() throws KettleException {
		return null;
	}

	@Override
	public String[] getUserLogins() throws KettleException {
		return null;
	}

	@Override
	public boolean isVersioningEnabled(String fullPath) {
		return false;
	}

}
