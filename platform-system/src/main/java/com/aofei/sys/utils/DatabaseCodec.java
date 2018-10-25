package com.aofei.sys.utils;

import com.aofei.sys.model.request.RepositoryDatabaseAttributeRequest;
import com.aofei.sys.model.request.RepositoryDatabaseRequest;
import com.aofei.sys.model.response.RepositoryDatabaseAttributeResponse;
import com.aofei.sys.model.response.RepositoryDatabaseResponse;
import com.google.common.collect.Lists;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.encryption.Encr;
import org.pentaho.di.core.exception.KettleDatabaseException;

import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @auther Tony
 * @create 2018-10-21 13:56
 */
public class DatabaseCodec extends com.aofei.kettle.core.database.DatabaseCodec {

    public static DatabaseMeta decode(RepositoryDatabaseResponse databaseResponse) throws KettleDatabaseException {
        DatabaseMeta databaseMeta = null;
        if(databaseResponse !=null){
            databaseMeta = new DatabaseMeta();

            databaseMeta.setDatabaseInterface(DatabaseMeta.getDatabaseInterface(databaseResponse.getDatabaseType()));
            databaseMeta.setAttributes(new Properties()); // new attributes

            databaseMeta.setName( databaseResponse.getRepositoryConnectionName() );

            databaseMeta.setAccessType( databaseResponse.getDatabaseContype());


            databaseMeta.setHostname( Const.NVL(databaseResponse.getHostName(), ""));
            databaseMeta.setDBName(Const.NVL(databaseResponse.getDatabaseName(), ""));
            databaseMeta.setDBPort( Const.NVL(databaseResponse.getPort(), "") );
            databaseMeta.setUsername( Const.NVL(databaseResponse.getUsername(), "") );
            databaseMeta.setPassword( Encr.decryptPasswordOptionallyEncrypted( Const.NVL(databaseResponse.getPassword(), "") ) );
            databaseMeta.setServername( Const.NVL(databaseResponse.getServername(), ""));
            databaseMeta.setDataTablespace(Const.NVL(databaseResponse.getDataTbs(), "") );
            databaseMeta.setIndexTablespace( Const.NVL(databaseResponse.getIndexTbs(), "") );

            // Also, load all the properties we can find...
            List<RepositoryDatabaseAttributeResponse> attrs = databaseResponse.getAttrs();
            for (RepositoryDatabaseAttributeResponse row : attrs)
            {
                String code = Const.NVL(row.getCode(), "");
                String attribute = Const.NVL(row.getValueStr(), "");
                // System.out.println("Attributes: "+(getAttributes()!=null)+", code: "+(code!=null)+", attribute: "+(attribute!=null));
                databaseMeta.getAttributes().put(code, Const.NVL(attribute, ""));
            }
        }

        return databaseMeta;
    }

    public static RepositoryDatabaseRequest decode(DatabaseMeta databaseMeta) throws KettleDatabaseException {
        RepositoryDatabaseRequest databaseRequest = null;
        if(databaseMeta !=null){
            databaseRequest = new RepositoryDatabaseRequest();
            databaseRequest.setDatabaseType(databaseMeta.getDatabaseInterface().getPluginId());
            databaseRequest.setRepositoryConnectionName(databaseMeta.getName());
            databaseRequest.setDatabaseContype(databaseMeta.getAccessType());
            databaseRequest.setHostName(databaseMeta.getHostname());
            databaseRequest.setDatabaseName(databaseMeta.getDatabaseName());
            databaseRequest.setPort(databaseMeta.getDatabaseInterface().getDatabasePortNumberString());
            databaseRequest.setUsername(databaseMeta.getUsername() );
            databaseRequest.setPassword( Encr.encryptPasswordIfNotUsingVariables( databaseMeta.getPassword()) );
            databaseRequest.setServername( databaseMeta.getServername());
            databaseRequest.setDataTbs(databaseMeta.getDataTablespace() );
            databaseRequest.setIndexTbs( databaseMeta.getIndexTablespace() );

            List<RepositoryDatabaseAttributeRequest> attrs = Lists.newArrayList();
            Map<String, String> connectionExtraOptions = databaseMeta.getExtraOptions();
            for(String key : connectionExtraOptions.keySet()){
                attrs.add(new RepositoryDatabaseAttributeRequest(key,connectionExtraOptions.get(key)));
            }
            databaseRequest.setAttrs(attrs);

        }

        return databaseRequest;
    }
}
