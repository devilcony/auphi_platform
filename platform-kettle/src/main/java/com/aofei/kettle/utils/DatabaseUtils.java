package com.aofei.kettle.utils;

import com.aofei.kettle.App;
import org.pentaho.di.core.database.Database;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;

/**
 * @auther Tony
 * @create 2018-10-19 15:37
 */
public class DatabaseUtils {



    public static Database getDatabase(String repositoryName, String databaseName) throws KettleException {
        Repository repository = App.getInstance().getRepository(repositoryName);
        ObjectId objectId =  repository.getDatabaseID(databaseName);
        DatabaseMeta databaseMeta = repository.loadDatabaseMeta(objectId,null);
        Database database = new Database(databaseMeta);
        repository.getRepositoryMeta();
        return database;


    }
}
