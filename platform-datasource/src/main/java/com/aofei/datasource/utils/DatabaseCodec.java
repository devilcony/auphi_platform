package com.aofei.datasource.utils;

import com.aofei.datasource.model.response.DatabaseResponse;
import org.pentaho.di.core.database.DatabaseMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * @auther Tony
 * @create 2018-10-21 13:56
 */
public class DatabaseCodec extends com.aofei.kettle.core.database.DatabaseCodec {

    public static List<DatabaseResponse> decode(List<DatabaseMeta> databaseMetaList) {
        List<DatabaseResponse> list = new ArrayList<>();
        if(databaseMetaList==null || databaseMetaList.isEmpty()){
            return list;
        }
        for(DatabaseMeta databaseMeta : databaseMetaList){

        }

        return list;
    }
}
