package com.auphi.ktrl.schedule.util;

import org.pentaho.di.core.database.Database;
import org.pentaho.di.core.exception.KettleDatabaseException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @auther Tony
 * @create 2017-12-17 13:16
 */
public class DatabaseUtil {


    /**
     * Return primary key column names ...
     * @param tablename
     * @throws KettleDatabaseException
     */
    public static String[] getPrimaryKeyColumnNames(Database database, String schema, String tablename) throws KettleDatabaseException, SQLException {
        List<String> names = new ArrayList<String>();
        ResultSet allkeys=null;
        try {
            allkeys=database.getDatabaseMetaData().getPrimaryKeys(null, schema, tablename);
            while (allkeys.next()) {
                String keyname=allkeys.getString("PK_NAME");
                String col_name=allkeys.getString("COLUMN_NAME");
                if(!names.contains(col_name)) names.add(col_name);

            }
        }
        catch(SQLException e) {
            throw e;
        }
        finally {
            try {
                if (allkeys!=null) allkeys.close();
            } catch(SQLException e) {
                throw new KettleDatabaseException("Error closing connection while searching primary keys in table ["+tablename+"]", e);
            }
        }
        return names.toArray(new String[names.size()]);
    }
}
