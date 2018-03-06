/*******************************************************************************
 *
 * Auphi Data Integration PlatformKettle Platform
 * Copyright C 2011-2017 by Auphi BI : http://www.doetl.com 

 * Support：support@pentahochina.com
 *
 *******************************************************************************
 *
 * Licensed under the LGPL License, Version 3.0 the "License";
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    https://opensource.org/licenses/LGPL-3.0 

 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ******************************************************************************/
package com.auphi.ktrl.system.resourceAuth.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.log4j.Logger;

import com.auphi.ktrl.conn.util.ConnectionPool;
import com.auphi.ktrl.engine.KettleEngine;
import com.auphi.ktrl.engine.impl.KettleEngineImpl2_3;
import com.auphi.ktrl.engine.impl.KettleEngineImpl3_2;
import com.auphi.ktrl.engine.impl.KettleEngineImpl4_3;
import com.auphi.ktrl.system.repository.util.RepositoryUtil;
import com.auphi.ktrl.system.user.util.UMStatus;
import com.auphi.ktrl.util.DBColumns;

public class ResourceAuthUtil 
{
	private static Logger logger = Logger.getLogger(ResourceAuthUtil.class);
    
    /**
     * Get version of repository.
     * 
     * */
    public static String getRepVersion(String repName)
    {
        final String query_prefix = "select " + DBColumns.COLUMN_REP_VERSION 
        + " from " + DBColumns.TABLE_REPOSITORY
        + " where " + DBColumns.COLUMN_REP_NAME + " = '";
        Connection conn = null ;
        String version = null ;
        try
        {
            conn = ConnectionPool.getConnection() ;
            Statement stt = conn.createStatement() ;
            ResultSet rs = stt.executeQuery(query_prefix + repName + "'") ;
            if (rs.next())
            version = rs.getString(1) ;
            
            rs.close() ;
            stt.close() ;
        }
        catch (Exception e)
        {
        	logger.error(e.getMessage(), e) ;
            return null ;
        }
        finally
        {
            ConnectionPool.freeConn(null,null,null,conn) ;
        }        
        return version ;
    }
    
    /**
     * Get kettle engine of repository.
     * 
     * 
     * */
    private static KettleEngine getKettleEngine(String repName)
    {
        
        String version = getRepVersion(repName) ;
        
        if (version == null)
            return null ;
        
        if (KettleEngine.VERSION_4_3.equals(version))
            return new KettleEngineImpl4_3() ;
        if (KettleEngine.VERSION_3_2.equals(version))
            return new KettleEngineImpl3_2();
        if (KettleEngine.VERSION_2_3.equals(version))
            return new KettleEngineImpl2_3();
        
        return null ;
    }
    
    
    public static String getRepList()
    {
        StringBuffer sb = new StringBuffer(512) ;
        Connection conn = null ;
        final String sql = " select " + DBColumns.COLUMN_REP_NAME + " from " + DBColumns.TABLE_REPOSITORY ;
        try
        {
            conn = ConnectionPool.getConnection() ;
            Statement stt = conn.createStatement() ;
            ResultSet rs = stt.executeQuery(sql) ;
            int count = 0 ;
            while(rs.next())
            {
                if (count > 0)
                    sb.append(",") ;
                sb.append(rs.getString(1)) ;
                count ++ ;
            }
            
            rs.close();
            stt.close();
        }
        catch (Exception e)
        {
        	logger.error(e.getMessage(), e) ;
        }
        finally
        {
            ConnectionPool.freeConn(null, null, null, conn) ;
        }
        return sb.toString();
    }

    public static String getResourceTreeJson(String user_id, String rep_name)
    {
        KettleEngine ke = getKettleEngine(rep_name) ;
        if (ke == null)
            return "" ;
        return ke.getResourceTreeJSON(rep_name, user_id) ;
    }
    
    public static UMStatus authResourcesToUser(String user_id, String rep_name, String resource_ids)
    {
        KettleEngine ke = getKettleEngine(rep_name) ;
        
        ke.authResourcesToUser(rep_name, user_id, resource_ids) ;
        
        return UMStatus.SUCCESS ;
    }
}
