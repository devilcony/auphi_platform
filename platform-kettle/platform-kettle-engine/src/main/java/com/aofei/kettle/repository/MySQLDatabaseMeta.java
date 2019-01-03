package com.aofei.kettle.repository;

public class MySQLDatabaseMeta extends org.pentaho.di.core.database.MySQLDatabaseMeta {

	@Override
	public boolean isMySQLVariant() {
		return false;
	}

}
