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
package com.auphi.ktrl.system.role.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.auphi.ktrl.conn.util.ConnectionPool;
import com.auphi.ktrl.system.priviledge.bean.PriviledgeType;
import com.auphi.ktrl.system.role.bean.RoleBean;
import com.auphi.ktrl.system.user.bean.UserBean;
import com.auphi.ktrl.system.user.util.UMStatus;
import com.auphi.ktrl.util.DBColumns;

public class RoleUtil 
{
	public static String SUPER_ADMIN = "SuperAdmin";

	private static Logger logger = Logger.getLogger(RoleUtil.class);
    
    public synchronized static UMStatus createRole(RoleBean roleBean)
    {
        final String query_sql_prefix = "select count(*) from " + DBColumns.TABLE_ROLE + 
                                        " where " + DBColumns.COLUMN_ROLE_NAME + " = " ;
        final String query_max_user_id = "select max(" + DBColumns.COLUMN_ROLE_ID + ") from " + DBColumns.TABLE_ROLE ;
        final String insert_sql_prefix =    "insert into  " + DBColumns.TABLE_ROLE  +
                                            "("+DBColumns.COLUMN_ROLE_ID+","+
                                            DBColumns.COLUMN_ROLE_NAME+","+
                                            DBColumns.COLUMN_ROLE_DESCRIPTION+","+
                                            DBColumns.COLUMN_ROLE_PRIVILEDGES+
                                            ") values(";
        
        UMStatus status = UMStatus.SUCCESS ;
        
        Connection conn = null ;
        
        try
        {
            ResultSet rs = null ;
            Statement stt = null ;
            conn = ConnectionPool.getConnection() ;
            
            stt = conn.createStatement() ;
            StringBuffer querySql = new StringBuffer(512) ;
            querySql.append(query_sql_prefix) ;
            querySql.append("'").append(roleBean.getRole_name()).append("'") ;
            
            rs = stt.executeQuery(querySql.toString()) ;
            
            if (rs.next() && rs.getInt(1) > 0)
            {
                status = UMStatus.ROLE_NAME_EXIST ;
                rs.close() ;
                stt.close() ;
                return status ;
            }
        
            rs = stt.executeQuery(query_max_user_id) ;
            int max_role_id = 0 ;
            if (rs.next())
                max_role_id = rs.getInt(1) ;
            rs.close();
            
            StringBuffer insertSql = new StringBuffer(512) ;
            insertSql.append(insert_sql_prefix) ;
            insertSql.append(max_role_id+1).append(",") ;
            insertSql.append("'").append(roleBean.getRole_name()).append("',") ;
            insertSql.append("'").append(roleBean.getDescription()).append("',");
            insertSql.append(roleBean.getPriviledges()).append(")");            
            
            stt.executeUpdate(insertSql.toString()) ;
            stt.close() ;
            
            return status ;
        
        }
        catch (Exception e)
        {
        	logger.error(e.getMessage(), e) ;
            status = UMStatus.DATABASE_EXCEPTION ;
        }
        finally
        {
            ConnectionPool.freeConn(null, null, null, conn) ;
        }
        return status ;        
    }

    public static RoleBean getRoleById(String role_id)
    {
        final String query_sql_prefix =  " select * from " + DBColumns.TABLE_ROLE + 
                                         " where " + DBColumns.COLUMN_ROLE_ID + " = ";
        
        RoleBean RoleBean = null ;
        Connection conn = null ;
        try
        {
            ResultSet rs = null ;
            Statement stt = null ;
            conn = ConnectionPool.getConnection() ;
            
            stt = conn.createStatement() ;
            rs = stt.executeQuery(query_sql_prefix+role_id) ;
            if (rs.next())
                RoleBean = getRoleBean(rs) ;
            rs.close() ;
            stt.close() ;
            return RoleBean ;
        }
        catch (Exception e)
        {
        	logger.error(e.getMessage(), e) ;
            return null ;
        }
        finally
        {
            ConnectionPool.freeConn(null, null, null, conn) ;
        }
    }

    public static synchronized UMStatus updateRole(RoleBean roleBean)
    {
        final String update_sql =   "update " + DBColumns.TABLE_ROLE  + 
                                    " set " +
                                    DBColumns.COLUMN_ROLE_NAME + " = ?,"+
                                    DBColumns.COLUMN_ROLE_DESCRIPTION + " = ?,"+
                                    DBColumns.COLUMN_ROLE_PRIVILEDGES + " = ?" +
                                    " where " + DBColumns.COLUMN_ROLE_ID + " = ?";
        UMStatus status = UMStatus.SUCCESS ;
        Connection conn = null ;
        try
        {
            PreparedStatement ps = null ;
            conn = ConnectionPool.getConnection() ;
            
            ps = conn.prepareStatement(update_sql) ;
            
            ps.setString(1, roleBean.getRole_name()) ;
            ps.setString(2, roleBean.getDescription()) ;
            ps.setLong(3, roleBean.getPriviledges()) ;
            ps.setInt(4, Integer.parseInt(roleBean.getRole_id())) ;
   
            ps.execute() ;
            ps.close() ;
            
            return status ;
        
        }
        catch (Exception e)
        {
        	logger.error(e.getMessage(), e) ;
            status = UMStatus.DATABASE_EXCEPTION ;
        }
        finally
        {
            ConnectionPool.freeConn(null, null, null, conn) ;
        }
        return status ;     
    }

    public static synchronized void deleteRoles(String role_ids)
    {
        final String delete_sql_prefix =  "delete from  " + DBColumns.TABLE_ROLE + 
                                          " where " + DBColumns.COLUMN_ROLE_ID + " = " ;
        final String delete_user_role_sql_prefix = " delete from " + DBColumns.TABLE_USER_ROLE 
                                                  +" where " + DBColumns.COLUMN_ROLE_ID + " = " ;
        
        if (role_ids == null)
            return ;
        
        String[] roleIDs = role_ids.split(",") ;
        if (roleIDs == null || roleIDs.length == 0)
            return ;
        
        Connection conn = null ;
        try
        {
            Statement stt = null ;
            conn = ConnectionPool.getConnection() ;
            
            conn.setAutoCommit(false) ;
            
            stt = conn.createStatement() ;
            
            for (int i = 0 ; i < roleIDs.length ; i ++)
            {
                stt.addBatch(delete_user_role_sql_prefix+roleIDs[i]) ;
                stt.addBatch(delete_sql_prefix + roleIDs[i]) ;
                //stt.executeUpdate() ;
            }
            stt.executeBatch() ;
            
            conn.commit() ;
            conn.setAutoCommit(true) ;
            stt.close() ;
        
        }
        catch (Exception e)
        {
        	logger.error(e.getMessage(), e) ;
        }
        finally
        {
            ConnectionPool.freeConn(null, null, null, conn) ;
        }  
                
    }

    public static List<RoleBean> getRoles(int start, int end)
    {
        final String sql =  "select * from " + DBColumns.TABLE_ROLE  ;

        Connection conn = null ;
        try
        {
            ResultSet rs = null ;
            Statement stt = null ;
            conn = ConnectionPool.getConnection() ;
            
            stt = conn.createStatement() ;
            rs = stt.executeQuery(sql) ;
            if (start > 0)
                rs.absolute(start) ;
            int count = end -start +1 ;
            List<RoleBean> roleList = new ArrayList<RoleBean>(10) ;
            RoleBean roleBean = null ;
            while(count > 0 && rs.next())
            {
                roleBean = getRoleBean(rs) ;
                roleList.add(roleBean) ;
                count -- ;
            }
            
            rs.close() ;
            stt.close() ;
            
            return roleList ;
        
        }
        catch (Exception e)
        {
        	logger.error(e.getMessage(), e) ;
            return null ;
        }
        finally
        {
            ConnectionPool.freeConn(null, null, null, conn) ;
        }
    }

    public static int getRoleCount()
    {
        final String sql =  " select count(*) from " + DBColumns.TABLE_ROLE  ;
        Connection conn = null ;
        int count = 0 ;
        try
        {
            ResultSet rs = null ;
            Statement stt = null ;
            conn = ConnectionPool.getConnection() ;
        
            stt = conn.createStatement() ;
            rs = stt.executeQuery(sql) ;
            
            if (rs.next())
                count = rs.getInt(1) ;
        
            rs.close() ;
            stt.close() ;
        
            return count ;
        
        }
        catch (Exception e)
        {
        	logger.error(e.getMessage(), e) ;
  
        }
        finally
        {
            ConnectionPool.freeConn(null, null, null, conn) ;
        }
        return count ;
    }
    
    private static RoleBean getRoleBean(ResultSet rs) throws SQLException 
    {
        RoleBean roleBean = new RoleBean() ;
        roleBean.setRole_id(String.valueOf(rs.getInt(DBColumns.COLUMN_ROLE_ID))) ;
        roleBean.setRole_name(rs.getString(DBColumns.COLUMN_ROLE_NAME)) ;
        roleBean.setDescription(rs.getString(DBColumns.COLUMN_ROLE_DESCRIPTION)) ;
        roleBean.setPriviledges(rs.getLong(DBColumns.COLUMN_ROLE_PRIVILEDGES)) ;
        roleBean.setIsSystemRole(rs.getInt(DBColumns.COLUMN_ROLE_ISSYSTEMROLE)) ;
        return roleBean ;
    }

    public static String getAllPrivileges()
    {
        return PriviledgeType.toJsonString() ;
    }

    public synchronized static UMStatus assignUsersToRole(String role_id, String user_ids)
    {
        final String insert_sql_prefix =  " insert into  " + DBColumns.TABLE_USER_ROLE + "(" + DBColumns.COLUMN_USER_ID+","+DBColumns.COLUMN_ROLE_ID+ 
                                          " )values ( " ;
        final String delete_sql_prefix = " delete from " + DBColumns.TABLE_USER_ROLE + " where " + DBColumns.COLUMN_ROLE_ID + " = " ;
        
        if (role_id == null || user_ids == null)
            return UMStatus.UNKNOWN_ERROR;
        
        String[] userIDs = user_ids.split(",") ;
        if (userIDs == null || userIDs.length == 0)
            return UMStatus.SUCCESS;
        
        Connection conn = null ;
        try
        {
            Statement stt = null ;
            conn = ConnectionPool.getConnection() ;
            conn.setAutoCommit(false) ;
            
            stt = conn.createStatement() ;
            stt.addBatch(delete_sql_prefix+role_id) ;
            for (int i = 0 ; i < userIDs.length ; i ++)
            {
                if (userIDs[i].length() == 0)
                    continue ;
                stt.addBatch(insert_sql_prefix + userIDs[i]+","+role_id+")") ;
            }
            stt.executeBatch() ;
            stt.close() ;
            conn.commit() ;
            conn.setAutoCommit(true) ;
            return UMStatus.SUCCESS ;

        }
        catch (Exception e)
        {
        	logger.error(e.getMessage(), e) ;
            return UMStatus.DATABASE_EXCEPTION;
        }
        finally
        {
            ConnectionPool.freeConn(null, null, null, conn) ;
        }           
    }


    public static String getAllRoles(UserBean userBean)
    {
        String query_sql =  " select " + DBColumns.COLUMN_ROLE_ID + "," + DBColumns.COLUMN_ROLE_NAME +
                                         " from " + DBColumns.TABLE_ROLE ;
        if(!userBean.isSuperAdmin()){
        	query_sql = query_sql + " where " + DBColumns.COLUMN_ROLE_NAME + " != '" + SUPER_ADMIN + "'";
        }

        Connection conn = null ;
        StringBuffer sb = new StringBuffer(1024);
        try
        {
            ResultSet rs = null ;
            Statement stt = null ;
            conn = ConnectionPool.getConnection() ;
            stt = conn.createStatement() ;
            rs = stt.executeQuery(query_sql) ;
            
            sb.append("[") ;
            int count = 0 ;
            while(rs.next())
            {
                if (count>0)
                    sb.append(",") ;
                sb.append("{") ;
                sb.append("id:'").append(rs.getInt(DBColumns.COLUMN_ROLE_ID)) ;
                sb.append("',text:'").append(rs.getString(DBColumns.COLUMN_ROLE_NAME))
                    .append("',checked:false,leaf:true");
                sb.append("}") ;
                count ++ ;
            }
            
            sb.append("]") ;
//            System.out.println(sb.toString()) ;
            rs.close() ;
            stt.close() ;
            return sb.toString() ;
        }
        catch (Exception e)
        {
        	logger.error(e.getMessage(), e) ;
            return null ;
        }
        finally
        {
            ConnectionPool.freeConn(null, null, null, conn) ;
        }
    }

    public static String getUsersOfRole(String role_id)
    {
        
        final String query_sql_prefix =  "select " + DBColumns.COLUMN_USER_ID + " from  " + DBColumns.TABLE_USER_ROLE + 
                                         " where " + DBColumns.COLUMN_ROLE_ID + " = " ;
        
        Connection conn = null ;
        try
        {
            StringBuffer sb = new StringBuffer(1024) ;
            ResultSet rs = null ;
            Statement stt = null ;
            conn = ConnectionPool.getConnection() ;
            
            conn.setAutoCommit(false) ;
            stt = conn.createStatement() ;
            rs = stt.executeQuery(query_sql_prefix+role_id) ;
            
            int count = 0 ;
            while(rs.next())
            {
                if (count>0)
                sb.append(",") ;
                sb.append(rs.getInt(1)) ;
                count ++ ;
            }
            
            rs.close() ;
            stt.close() ;
            
            return sb.toString() ;
        }
        catch (Exception e)
        {
        	logger.error(e.getMessage(), e) ;
            return "" ;
        }
        finally
        {
            ConnectionPool.freeConn(null, null, null, conn) ;
        }  
    }

}
