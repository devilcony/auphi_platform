package com.aofei.sys.utils;

import com.aofei.sys.model.response.RepositoryDatabaseResponse;
import com.aofei.sys.model.response.RepositoryResponse;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.plugins.PluginRegistry;
import org.pentaho.di.core.plugins.RepositoryPluginType;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.repository.RepositoryMeta;
import org.pentaho.di.repository.kdr.KettleDatabaseRepository;
import org.pentaho.di.repository.kdr.KettleDatabaseRepositoryMeta;

public class RepositoryCodec extends com.aofei.kettle.core.repository.RepositoryCodec{



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
}
