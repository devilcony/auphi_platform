package com.aofei.sys.utils;

import com.alibaba.druid.pool.DruidDataSource;
import com.aofei.sys.model.response.RepositoryDatabaseResponse;
import com.aofei.sys.model.response.RepositoryResponse;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.database.GenericDatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettlePluginException;
import org.pentaho.di.core.plugins.PluginRegistry;
import org.pentaho.di.core.plugins.RepositoryPluginType;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.repository.RepositoryMeta;
import org.pentaho.di.repository.kdr.KettleDatabaseRepository;
import org.pentaho.di.repository.kdr.KettleDatabaseRepositoryMeta;

import java.util.Properties;

public class RepositoryCodec extends com.aofei.kettle.repository.RepositoryCodec{



	public static Repository decode(RepositoryResponse jsonObject, RepositoryDatabaseResponse databaseResponse) throws KettleException {
		KettleDatabaseRepositoryMeta repositoryMeta = (KettleDatabaseRepositoryMeta) PluginRegistry.getInstance().loadClass( RepositoryPluginType.class, "KettleDatabaseRepository", RepositoryMeta.class );
		repositoryMeta.setName(jsonObject.getRepositoryName());
		repositoryMeta.setDescription(jsonObject.getDescription());
		repositoryMeta.setDefault(jsonObject.getIsDefault() !=null && jsonObject.getIsDefault() ==1);

		DatabaseMeta databaseMeta = DatabaseCodec.decode(databaseResponse);
		repositoryMeta.setConnection(databaseMeta);

		KettleDatabaseRepository databaseRepository = new KettleDatabaseRepository();
		databaseRepository.init(repositoryMeta);

		return databaseRepository;
	}

	public static KettleDatabaseRepository decodeDefault(DruidDataSource dataSource) throws KettlePluginException {
		GenericDatabaseMeta nativeMeta = new GenericDatabaseMeta();
		nativeMeta.setAccessType(DatabaseMeta.TYPE_ACCESS_NATIVE);
		nativeMeta.setUsername(dataSource.getUsername());
		nativeMeta.setPassword(dataSource.getPassword());
		Properties attrs = new Properties();
		attrs.put( GenericDatabaseMeta.ATRRIBUTE_CUSTOM_DRIVER_CLASS, dataSource.getDriverClassName() );
		attrs.put( GenericDatabaseMeta.ATRRIBUTE_CUSTOM_URL, dataSource.getUrl() );
		nativeMeta.setAttributes( attrs );

		DatabaseMeta databaseMeta = new DatabaseMeta();
		databaseMeta.setAccessType(DatabaseMeta.TYPE_ACCESS_NATIVE);
		databaseMeta.setDatabaseInterface(nativeMeta);
		databaseMeta.setUsername(dataSource.getUsername());
		databaseMeta.setPassword(dataSource.getPassword());
		KettleDatabaseRepositoryMeta repositoryMeta = (KettleDatabaseRepositoryMeta) PluginRegistry.getInstance().loadClass( RepositoryPluginType.class, "KettleDatabaseRepository", RepositoryMeta.class );
		repositoryMeta.setName("Default");
		repositoryMeta.setDescription("Default");
		repositoryMeta.setDefault(Boolean.TRUE);

		repositoryMeta.setConnection(databaseMeta);
		KettleDatabaseRepository databaseRepository = new KettleDatabaseRepository();
		databaseRepository.init(repositoryMeta);

		return databaseRepository;

	}
}
