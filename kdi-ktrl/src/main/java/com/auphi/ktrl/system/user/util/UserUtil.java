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
package com.auphi.ktrl.system.user.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.auphi.ktrl.conn.util.ConnectionPool;
import com.auphi.ktrl.system.repository.bean.RepositoryBean;
import com.auphi.ktrl.system.repository.util.RepositoryUtil;
import com.auphi.ktrl.system.user.bean.LoginResponse;
import com.auphi.ktrl.system.user.bean.UserBean;
import com.auphi.ktrl.util.Constants;
import com.auphi.ktrl.util.DBColumns;

public class UserUtil
{
	public static int STATUS_ACTIVE = 1;
	public static int STATUS_NOT_ACTIVE = 0;
	
	private static Logger logger = Logger.getLogger(UserUtil.class);
    
    public static UMStatus updateUser(UserBean userBean)
    {
        final String update_sql =  "update " + DBColumns.TABLE_USER  + 
                                            " set " +
                                            DBColumns.COLUMN_USER_NAME + " = ?,"+
                                            DBColumns.COLUMN_USER_PASSWORD + "= ? ,"+
                                            DBColumns.COLUMN_USER_NICKNAME + " = ? ,"+
                                            DBColumns.COLUMN_USER_EMAIL+" = ? ,"+
                                            DBColumns.COLUMN_USER_MOBILEPHONE+" = ?,"+
                                            DBColumns.COLUMN_USER_DESCRIPTION + " = ?"+
                                            "where " + DBColumns.COLUMN_USER_ID + " = ?";
        
        final String query_sql = "select count(*) from " + DBColumns.TABLE_USER + 
                " where " + DBColumns.COLUMN_USER_NICKNAME + " = ? and " + DBColumns.COLUMN_USER_ID + " != ?" ;
        
        UMStatus status = UMStatus.SUCCESS ;
        Connection conn = null ;
        try
        {
            PreparedStatement ps = null ;
            ResultSet rs = null;
            conn = ConnectionPool.getConnection() ;
            
            //  1. Check if nick name exist
            ps = conn.prepareStatement(query_sql) ;
            ps.setString(1, userBean.getNick_name()) ;
            ps.setInt(2, userBean.getUser_id()) ;
            rs = ps.executeQuery() ;
            if (rs.next() && rs.getInt(1) > 0)
            {
                status = UMStatus.NICK_NAME_EXIST ;
                rs.close() ;
                ps.close();
                return status ;
            }else {
            	rs.close() ;
                ps.close() ;
            }
            
            //update it
            ps = conn.prepareStatement(update_sql) ;
            
            ps.setString(1, userBean.getUser_name()) ;
            ps.setString(2, userBean.getPassword()) ;
            ps.setString(3, userBean.getNick_name()) ;
            ps.setString(4, userBean.getEmail()) ;
            ps.setString(5, userBean.getMobilephone()) ;
            ps.setString(6, userBean.getDescription()) ;
            ps.setInt(7, userBean.getUser_id()) ;
            
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
    public static void updateRole()
    {
        
    }
    public static void searchUsers()
    {
        
    }
    public static void logout()
    {
        
    }
    public static UMStatus login(String userName, String password, UserBean userBean)
    {
        
        final String query_sql_prefix =  " select * from " + DBColumns.TABLE_USER 
                                 +" where " + DBColumns.COLUMN_USER_NAME + " = '"  ;
        
        UMStatus status = UMStatus.SUCCESS ;
        Connection conn = null ;
        ResultSet rs = null ;
        Statement stt = null ;
        try
        {
            conn = ConnectionPool.getConnection() ;
            stt = conn.createStatement() ;
            
            rs = stt.executeQuery(query_sql_prefix+userName+"'") ;
            
            if(rs.next())
                getUserBean(rs,userBean) ;
            else
                return UMStatus.USER_NOT_EXIST ;
            
            if (!userBean.getPassword().equals(password))
                return UMStatus.WRONG_PASSWORD ;
            
            if (!(userBean.getStatus()==1)){
            	return UMStatus.USER_NOT_ACTIVE ;
            }
            
            return status ;
        
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
            status = UMStatus.DATABASE_EXCEPTION ;
        }
        finally
        {
            ConnectionPool.freeConn(rs, stt, null, conn) ;
        }
        return status ;  
        
    }
    public static void login(String userName, String password, LoginResponse lps)
    {
        UMStatus ums = UMStatus.SUCCESS ;
        
        if (lps == null)
        {
            lps = new LoginResponse() ;
        }
        
        UserBean userBean = new UserBean() ;
        
        ums = login(userName,password,userBean) ;
        if (ums != UMStatus.SUCCESS)
        {
            lps.setStatus(ums) ;
            return ;
        }
        
        List<RepositoryBean> repList = RepositoryUtil.getAllRepositories(userBean) ;
        long priviledges = getPriviledgesOfUser(userBean.getUser_id()) ;
        lps.setPriviledges(priviledges) ;
        lps.setRep_list(repList) ;
        lps.setUser_id(String.valueOf(userBean.getUser_id())) ;
        lps.setUser_name(userName) ;
        lps.setStatus(ums) ;
    }
    public static boolean isAdmin(int user_id)
    {
        long priviledges = getPriviledgesOfUser(user_id);
        if(priviledges == Long.parseLong(Constants.get("ADMIN_PRIVILEDGES")==null?"0":Constants.get("ADMIN_PRIVILEDGES"))){
        	return true;
        }else {
        	return false;
        }
    }
    public static boolean isSuperAdmin(int user_id)
    {
        if(user_id==0){
        	return true;
        }else {
        	return false;
        }
    }
    public static void getUsersOfRole()
    {
        
    }
    public static String getNonSystemUsers()
    {
        final String sql =  " select " + DBColumns.COLUMN_USER_ID + ","+ DBColumns.COLUMN_USER_NAME +
        " from " + DBColumns.TABLE_USER  + 
        " where " + DBColumns.COLUMN_USER_ISSYSTEMUSER + " <> 1 or " +
        DBColumns.COLUMN_USER_ISSYSTEMUSER + " is NULL ";
        Connection conn = null ;
        try
        {
            ResultSet rs = null ;
            Statement stt = null ;
            StringBuffer sb = new StringBuffer(1024) ;
            conn = ConnectionPool.getConnection() ;
            
            stt = conn.createStatement() ;
            rs = stt.executeQuery(sql) ;
            
            sb.append("[") ;
            int count = 0 ;
            while(rs.next())
            {
                if (count > 0)
                sb.append(",") ;
                sb.append("{id:'").append(rs.getInt(1)).
                append("',text:'").append(rs.getString(2)).
                append("',checked:false,leaf:true}") ;
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
            logger.error(e.getMessage(), e);
            return "" ;
        }
        finally
        {
            ConnectionPool.freeConn(null, null, null, conn) ;
        }
    }
    public static String getAllUsers()
    {
        final String sql =  " select " + DBColumns.COLUMN_USER_ID + ","+ DBColumns.COLUMN_USER_NAME +
                            " from " + DBColumns.TABLE_USER  ;
        Connection conn = null ;
        try
        {
            ResultSet rs = null ;
            Statement stt = null ;
            StringBuffer sb = new StringBuffer(1024) ;
            conn = ConnectionPool.getConnection() ;
        
            stt = conn.createStatement() ;
            rs = stt.executeQuery(sql) ;
            
            sb.append("[") ;
            int count = 0 ;
            while(rs.next())
            {
                if (count > 0)
                    sb.append(",") ;
                sb.append("{id:'").append(rs.getInt(1)).
                    append("',text:'").append(rs.getString(2)).
                    append("',checked:false,leaf:true}") ;
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
            logger.error(e.getMessage(), e);
            return "" ;
        }
        finally
        {
            ConnectionPool.freeConn(null, null, null, conn) ;
        }
    }
    /**
     * get all users
     * @return
     */
    public static List<UserBean> getUsers()
    {
    	List<UserBean> listUsers = new ArrayList<UserBean>();
        final String sql =  " select " + DBColumns.COLUMN_USER_ID + ","+ DBColumns.COLUMN_USER_NAME + 
        		"," + DBColumns.COLUMN_USER_NICKNAME + "," + DBColumns.COLUMN_USER_EMAIL + 
                " from " + DBColumns.TABLE_USER  ;
        Connection conn = null ;
        ResultSet rs = null ;
        Statement stmt = null ;
        try
        {
            conn = ConnectionPool.getConnection() ;
            stmt = conn.createStatement() ;
            rs = stmt.executeQuery(sql) ;
            
            while(rs.next())
            {
            	UserBean userBean = new UserBean();
            	userBean.setUser_id(rs.getInt(1));
            	userBean.setUser_name(rs.getString(2));
            	userBean.setNick_name(rs.getString(3));
            	userBean.setEmail(rs.getString(4));
            	
            	listUsers.add(userBean);
            }
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
        }
        finally
        {
            ConnectionPool.freeConn(rs, stmt, null, conn) ;
        }
        
        return listUsers;
    }
    
    /**
     * get all users
     * @return
     */
    public static List<UserBean> getUsersByOrg(int orgId)
    {
    	List<UserBean> listUsers = new ArrayList<UserBean>();
        final String sql =  " select " + DBColumns.COLUMN_USER_ID + ","+ DBColumns.COLUMN_USER_NAME + 
        		"," + DBColumns.COLUMN_USER_NICKNAME + "," + DBColumns.COLUMN_USER_EMAIL + 
                " from " + DBColumns.TABLE_USER + " where " + DBColumns.COLUMN_USER_ORGANIZERID + "=" + orgId;
        Connection conn = null ;
        ResultSet rs = null ;
        Statement stmt = null ;
        try
        {
            conn = ConnectionPool.getConnection() ;
            stmt = conn.createStatement() ;
            rs = stmt.executeQuery(sql) ;
            
            while(rs.next())
            {
            	UserBean userBean = new UserBean();
            	userBean.setUser_id(rs.getInt(1));
            	userBean.setUser_name(rs.getString(2));
            	userBean.setNick_name(rs.getString(3));
            	userBean.setEmail(rs.getString(4));
            	
            	listUsers.add(userBean);
            }
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
        }
        finally
        {
            ConnectionPool.freeConn(rs, stmt, null, conn) ;
        }
        
        return listUsers;
    }
    
    /**
     * get user emails
     * @return
     */
    public static String[] getUserEmails(String user_ids)
    {
    	String[] user_mails = new String[]{""};
    	if(user_ids != null && !"".equals(user_ids)){
    		user_mails = new String[user_ids.split(",").length];
    		String sql = " select " + DBColumns.COLUMN_USER_EMAIL + " from " + DBColumns.TABLE_USER
    				+ " where " + DBColumns.COLUMN_USER_ID + " in (" + user_ids + ")";
            Connection conn = null ;
            ResultSet rs = null ;
            Statement stmt = null ;
            try
            {
                conn = ConnectionPool.getConnection() ;
                stmt = conn.createStatement() ;
                rs = stmt.executeQuery(sql) ;
                
                int num =0;
                while(rs.next())
                {
                	user_mails[num] = rs.getString(1);
                	num++;
                }
            }
            catch (Exception e)
            {
                logger.error(e.getMessage(), e);
            }
            finally
            {
                ConnectionPool.freeConn(rs, stmt, null, conn) ;
            }
    	}
        
        return user_mails;
    }
    /**
     * get notice user name
     * @return
     */
    public static String getErrorNoticeUserName(String user_ids)
    {
    	String notice_username = "";  
    	if(user_ids != null && !"".equals(user_ids)){
			String sql = " select " + DBColumns.COLUMN_USER_EMAIL + "," + DBColumns.COLUMN_USER_NICKNAME
					+ " from " + DBColumns.TABLE_USER + " where " + DBColumns.COLUMN_USER_ID
					+ " in (" + user_ids + ")";
			Connection conn = null;
			ResultSet rs = null;
			Statement stmt = null;
			try {
				conn = ConnectionPool.getConnection();
				stmt = conn.createStatement();
				rs = stmt.executeQuery(sql);

				while (rs.next()) {
					if ("".equals(notice_username)) {
						notice_username = rs.getString(1) + "("
								+ rs.getString(2) + ")";
					} else {
						notice_username = notice_username + ","
								+ rs.getString(1) + "(" + rs.getString(2) + ")";
					}
				}
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			} finally {
				ConnectionPool.freeConn(rs, stmt, null, conn);
			}
    	}
    	
        return notice_username;
    }
    public static int getUserCount()
    {
        final String sql =  " select count(*) from " + DBColumns.TABLE_USER  ;
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
            logger.error(e.getMessage(), e);
  
        }
        finally
        {
            ConnectionPool.freeConn(null, null, null, conn) ;
        }
        return count ;
    }
    /**
     *  Get users range from 'start' to 'end', order by user_id.
     *  @param start Rank of the first user 
     *  @param end  Rank of the last user
     * */
    public static List<UserBean> getUsers(int start, int end, UserBean loginUserBean)
    {
        String sql =  " select A." + DBColumns.COLUMN_LOGIN_TIME + ",B.* "+
                      " from " + DBColumns.TABLE_USER + " B left join "+
                      "      ( select " + DBColumns.COLUMN_USER_ID + ", max(" + DBColumns.COLUMN_LOGIN_TIME+") "+ DBColumns.COLUMN_LOGIN_TIME + 
                      "          from "+ DBColumns.TABLE_LOGIN_LOG + 
                      "         group by " + DBColumns.COLUMN_USER_ID +") A " +
                      "            on A." + DBColumns.COLUMN_USER_ID + " = B." + DBColumns.COLUMN_USER_ID;
        if(!loginUserBean.isSuperAdmin()){
        	sql = sql + " where B." + DBColumns.COLUMN_USER_ORGANIZERID + "=" + loginUserBean.getOrgId();
        }
        
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
            List<UserBean> userList = new ArrayList<UserBean>(10) ;
            UserBean userBean = null ;
            while(count > 0 && rs.next())
            {
                userBean = getUserBean(rs) ;
                userBean.setLastLogin(rs.getTimestamp(DBColumns.COLUMN_LOGIN_TIME)) ;
                userBean.setAdmin(isAdmin(userBean.getUser_id()));
                userList.add(userBean) ;
                count -- ;
            }
            
            rs.close() ;
            stt.close() ;
            
            return userList ;
            
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
            return null ;
        }
        finally
        {
            ConnectionPool.freeConn(null, null, null, conn) ;
        }
        
    }
//    public static long getUserPriviledgesById(String user_id)
//    {
//        final String query_sql = " select "+ COLUMN_PRIVILEDGES + 
//                                 " from " +TABLE_USER_ROLE + " A ," +TABLE_ROLE +" B "+
//                                 " where A." + COLUMN_ROLE_ID + " = B." + COLUMN_ROLE_ID;
//        return 0 ;
//    }
    public static UserBean getUserById(String user_id)
    {
        final String query_sql_prefix =  " select * from " + DBColumns.TABLE_USER + 
                                         " where " + DBColumns.COLUMN_USER_ID + " = ";

        UserBean userBean = null ;
        Connection conn = null ;
        try
        {
            ResultSet rs = null ;
            Statement stt = null ;
            conn = ConnectionPool.getConnection() ;
            
            stt = conn.createStatement() ;
            rs = stt.executeQuery(query_sql_prefix+user_id) ;
            if (rs.next())
                userBean = getUserBean(rs) ;
            rs.close() ;
            stt.close() ;
            return userBean ;
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
            return null ;
        }
        finally
        {
            ConnectionPool.freeConn(null, null, null, conn) ;
        }
    }
    
    public static UserBean getUserByName(String user_name)
    {
        final String query_sql_prefix =  " select * from " + DBColumns.TABLE_USER + 
                                         " where " + DBColumns.COLUMN_USER_NAME + " = '";

        UserBean userBean = null ;
        Connection conn = null ;
        try
        {
            ResultSet rs = null ;
            Statement stt = null ;
            conn = ConnectionPool.getConnection() ;
            
            stt = conn.createStatement() ;
            rs = stt.executeQuery(query_sql_prefix+user_name+"'") ;
            if (rs.next())
                userBean = getUserBean(rs) ;
            rs.close() ;
            stt.close() ;
            return userBean ;
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
            return null ;
        }
        finally
        {
            ConnectionPool.freeConn(null, null, null, conn) ;
        }
    }    
    
    /**
     * Get basic attributes of user from JDBC result set,
     * except user's last login time and status. 
     * 
     * */
    private static UserBean getUserBean(ResultSet rs) throws SQLException
    {
        UserBean userBean = new UserBean() ;
        userBean.setUser_id(rs.getInt(DBColumns.COLUMN_USER_ID)) ;
        userBean.setUser_name(rs.getString(DBColumns.COLUMN_USER_NAME)) ;
        userBean.setPassword(rs.getString(DBColumns.COLUMN_USER_PASSWORD)) ;
        userBean.setNick_name(rs.getString(DBColumns.COLUMN_USER_NICKNAME)) ;
        userBean.setEmail(rs.getString(DBColumns.COLUMN_USER_EMAIL)) ;
        userBean.setMobilephone(rs.getString(DBColumns.COLUMN_USER_MOBILEPHONE)) ;
        userBean.setDescription(rs.getString(DBColumns.COLUMN_USER_DESCRIPTION)) ;
        userBean.setIsSystemUser(rs.getInt(DBColumns.COLUMN_USER_ISSYSTEMUSER)) ;
        userBean.setOrgId(rs.getInt(DBColumns.COLUMN_USER_ORGANIZERID));
        userBean.setStatus(rs.getInt(DBColumns.COLUMN_USER_STATUS));
        boolean isAdmin = isAdmin(userBean.getUser_id());
        boolean isSuperAdmin = isSuperAdmin(userBean.getUser_id());
        userBean.setAdmin(isAdmin);
        userBean.setSuperAdmin(isSuperAdmin);
        return userBean ;
    }

    private static UserBean getUserBean(ResultSet rs, UserBean userBean) throws SQLException
    {
        userBean.setUser_id(rs.getInt(DBColumns.COLUMN_USER_ID)) ;
        userBean.setUser_name(rs.getString(DBColumns.COLUMN_USER_NAME)) ;
        userBean.setPassword(rs.getString(DBColumns.COLUMN_USER_PASSWORD)) ;
        userBean.setNick_name(rs.getString(DBColumns.COLUMN_USER_NICKNAME)) ;
        userBean.setEmail(rs.getString(DBColumns.COLUMN_USER_EMAIL)) ;
        userBean.setMobilephone(rs.getString(DBColumns.COLUMN_USER_MOBILEPHONE)) ;
        userBean.setDescription(rs.getString(DBColumns.COLUMN_USER_DESCRIPTION)) ;
        userBean.setIsSystemUser(rs.getInt(DBColumns.COLUMN_USER_ISSYSTEMUSER)) ;
        userBean.setStatus(rs.getInt(DBColumns.COLUMN_USER_STATUS)) ;
        userBean.setOrgId(rs.getInt(DBColumns.COLUMN_USER_ORGANIZERID));
        boolean isAdmin = isAdmin(userBean.getUser_id());
        boolean isSuperAdmin = isSuperAdmin(userBean.getUser_id());
        userBean.setAdmin(isAdmin);
        userBean.setSuperAdmin(isSuperAdmin);
        return userBean ;
    }
    public static void getAllRoles()
    {
        
    }
    public static void getRoleById()
    {
        
    }
    public static void getAllPriviledges()
    {
        
    }
    public static void getPriviledgesOfRole()
    {
        
    }
    public static void getPriviledgesOfUser()
    {
        
    }
    public static long getPriviledgesOfUser(int user_id)
    {
        final String query_sql_prefix =  "select " + DBColumns.COLUMN_ROLE_PRIVILEDGES 
                                        +" from  " + DBColumns.TABLE_USER_ROLE + " a , " + DBColumns.TABLE_ROLE + " b "  
                                        +" where a." + DBColumns.COLUMN_ROLE_ID + " = b." + DBColumns.COLUMN_ROLE_ID
                                        +"  and " + DBColumns.COLUMN_USER_ID + " = " ;
        long priviledges = 0 ;
        Connection conn = null ;
        try
        {
            ResultSet rs = null ;
            Statement stt = null ;
            conn = ConnectionPool.getConnection() ;
            
            conn.setAutoCommit(false) ;
            stt = conn.createStatement() ;
            rs = stt.executeQuery(query_sql_prefix+user_id) ;
            
            while(rs.next())
            {
                priviledges = priviledges | rs.getLong(1) ;
            }
            
            rs.close() ;
            stt.close() ;
        
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
        }
        finally
        {
            ConnectionPool.freeConn(null, null, null, conn) ;
        }                
        return priviledges ;
    }
    public static String getRolesOfUser(String user_id)
    {
        final String query_sql_prefix =  "select " + DBColumns.COLUMN_ROLE_ID + " from  " + DBColumns.TABLE_USER_ROLE + 
                                         " where " + DBColumns.COLUMN_USER_ID + " = " ;

        Connection conn = null ;
        try
        {
            StringBuffer sb = new StringBuffer(1024) ;
            ResultSet rs = null ;
            Statement stt = null ;
            conn = ConnectionPool.getConnection() ;
            
            conn.setAutoCommit(false) ;
            stt = conn.createStatement() ;
            rs = stt.executeQuery(query_sql_prefix+user_id) ;
            
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
            logger.error(e.getMessage(), e);
            return "" ;
        }
        finally
        {
            ConnectionPool.freeConn(null, null, null, conn) ;
        }        
    }
    public static void deleteUsers(String user_ids)
    {
        final String delete_sql_prefix =  "delete from  " + DBColumns.TABLE_USER + 
                                          " where " + DBColumns.COLUMN_USER_ID + " = " ;
        
        if (user_ids == null)
            return ;
        
        String[] userIDs = user_ids.split(",") ;
        if (userIDs == null || userIDs.length == 0)
            return ;

        Connection conn = null ;
        try
        {
            Statement stt = null ;
            conn = ConnectionPool.getConnection() ;
            
            conn.setAutoCommit(false) ;
            
            stt = conn.createStatement() ;
            
            for (int i = 0 ; i < userIDs.length ; i ++)
            {
                stt.executeUpdate(delete_sql_prefix + userIDs[i]) ;
            }
            
            conn.commit() ;
            conn.setAutoCommit(true) ;
            stt.close() ;
        
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
        }
        finally
        {
            ConnectionPool.freeConn(null, null, null, conn) ;
        }        
    }
    public static void deleteUser(String user_id)
    {
        deleteUsers(user_id) ;
    }
    public static void deleteRole()
    {
        
    }
    public static synchronized UMStatus createUser(UserBean userBean)
    {
        final String query_max_user_id = "select max(" + DBColumns.COLUMN_USER_ID + ") from " + DBColumns.TABLE_USER ;
        final String query_sql = "select count(*) from " + DBColumns.TABLE_USER + 
                                 " where " + DBColumns.COLUMN_USER_NAME + " = ? " ;
        
        final String query_sql_nickname = "select count(*) from " + DBColumns.TABLE_USER + 
                " where " + DBColumns.COLUMN_USER_NICKNAME + " = ?" ;
        
        final String insert_sql = "insert into  " + DBColumns.TABLE_USER  +
                                         "("+DBColumns.COLUMN_USER_ID+","+
                                         DBColumns.COLUMN_USER_NAME+","+
                                         DBColumns.COLUMN_USER_PASSWORD+","+
                                         DBColumns.COLUMN_USER_NICKNAME+","+
                                         DBColumns.COLUMN_USER_EMAIL+","+
                                         DBColumns.COLUMN_USER_MOBILEPHONE+","+
                                         DBColumns.COLUMN_USER_DESCRIPTION+","+
                                         DBColumns.COLUMN_USER_ORGANIZERID+","+
                                         DBColumns.COLUMN_USER_STATUS+
                                         ") values(?,?,?,?,?,?,?,?,?)";
        UMStatus status = UMStatus.SUCCESS ;

        Connection conn = null ;

        try
        {
            ResultSet rs = null ;
            Statement stt = null ;
            PreparedStatement ps = null ;
            conn = ConnectionPool.getConnection() ;
            
            //  1. Check if user name exist
            ps = conn.prepareStatement(query_sql) ;
            ps.setString(1, userBean.getUser_name()) ;
            rs = ps.executeQuery() ;
            if (rs.next() && rs.getInt(1) > 0)
            {
                status = UMStatus.USER_NAME_EXIST ;
                rs.close() ;
                ps.close();
                return status ;
            }else {
            	rs.close() ;
                ps.close() ;
            }
            
            //  2. Check if nick name exist
//            ps = conn.prepareStatement(query_sql_nickname) ;
//            ps.setString(1, userBean.getNick_name()) ;
//            rs = ps.executeQuery() ;
//            if (rs.next() && rs.getInt(1) > 0)
//            {
//                status = UMStatus.NICK_NAME_EXIST ;
//                rs.close() ;
//                ps.close();
//                return status ;
//            }else {
//            	rs.close() ;
//                ps.close() ;
//            }
            
            // 2. Get current max user id
            stt = conn.createStatement() ;
            rs = stt.executeQuery(query_max_user_id) ;
            int max_user_id = 0 ;
            if (rs.next())
                max_user_id = rs.getInt(1) ;
            rs.close() ;
            stt.close() ;
            
            // 3. Insert new user
            ps = conn.prepareStatement(insert_sql) ;
            ps.setInt(1, max_user_id+1) ;
            ps.setString(2, userBean.getUser_name()) ;
            ps.setString(3, userBean.getPassword()) ;
            ps.setString(4, userBean.getNick_name()) ;
            ps.setString(5, userBean.getEmail()) ;
            ps.setString(6, userBean.getMobilephone()) ;
            ps.setString(7, userBean.getDescription()) ;
            ps.setInt(8, userBean.getOrgId()) ;
            ps.setInt(9, userBean.getStatus()) ;
            ps.executeUpdate() ;
            ps.close() ;
        
            return status ;
        
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
            status = UMStatus.DATABASE_EXCEPTION ;
        }
        finally
        {
            ConnectionPool.freeConn(null, null, null, conn) ;
        }
        return status ;        
    }
    public static void createRole()
    {
        
    }
    public static void assignUsersToRole()
    {
        
    }
    public static void assignRolesToUser()
    {
        
    }
    public synchronized static void assignRolesToUser(String role_ids, String user_id)
    {
        final String insert_sql_prefix =  " insert into  " + DBColumns.TABLE_USER_ROLE + "(" + DBColumns.COLUMN_USER_ID + "," + DBColumns.COLUMN_ROLE_ID+ 
                                          " )values ( " ;
        final String delete_sql_prefix = " delete from " + DBColumns.TABLE_USER_ROLE + " where " + DBColumns.COLUMN_USER_ID + " = " ;
        
        if (role_ids == null || user_id == null)
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
            stt.addBatch(delete_sql_prefix+user_id) ;
            for (int i = 0 ; i < roleIDs.length ; i ++)
            {
                if (roleIDs[i].length() == 0)
                    continue ;
                stt.addBatch(insert_sql_prefix + user_id+","+roleIDs[i]+")") ;
  
            }
            stt.executeBatch() ;
            stt.close() ;
            conn.commit() ;
            conn.setAutoCommit(true) ;

        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
        }
        finally
        {
            ConnectionPool.freeConn(null, null, null, conn) ;
        }           
    }
}
