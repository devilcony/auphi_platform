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
package com.auphi.ktrl.system.repository.util;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.auphi.ktrl.conn.bean.ConnConfigBean;
import com.auphi.ktrl.conn.util.ConnectionPool;
import com.auphi.ktrl.conn.util.DataBaseUtil;
import com.auphi.ktrl.engine.KettleEngine;
import com.auphi.ktrl.engine.impl.KettleEngineImpl4_3;
import com.auphi.ktrl.system.organizer.bean.OrganizerBean;
import com.auphi.ktrl.system.repository.CreateRepositoryThread;
import com.auphi.ktrl.system.repository.bean.RepositoryBean;
import com.auphi.ktrl.system.user.bean.UserBean;
import com.auphi.ktrl.util.Constants;
import com.auphi.ktrl.util.DBColumns;

public class RepositoryUtil 
{
	private static Logger logger = Logger.getLogger(RepositoryUtil.class);
    
    public static List<RepositoryBean> getRepository(int start,int end, UserBean userBean){
		String sql = " select * from " + DBColumns.TABLE_REPOSITORY + " where 1=1";
		if(!userBean.isSuperAdmin()){
			sql = sql + " and " + DBColumns.COLUMN_REP_ORGANIZERID + "=" + userBean.getOrgId();
		}
				 
		Connection conn = null;
		try {
			ResultSet rs = null;
			Statement stt = null;
			conn = ConnectionPool.getConnection();
			stt = conn.createStatement();
			rs = stt.executeQuery(sql);
			if (start > 0)
				rs.absolute(start);
			int count = end - start + 1;
			List<RepositoryBean> repositoryList = new ArrayList<RepositoryBean>();
			RepositoryBean repBean = null;
			while (count > 0 && rs.next()) {
				repBean = getRepositoryBean(rs);
				repositoryList.add(repBean);
				count--;
			}
			rs.close();
			stt.close();
			return repositoryList;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return null;
		} finally {
			ConnectionPool.freeConn(null, null, null, conn);
		}
	}
    public static RepositoryBean getRepositoryBean(ResultSet rs){
    	RepositoryBean repBean = new RepositoryBean();
    	try {
			repBean.setRepositoryID(rs.getInt(DBColumns.COLUMN_REP_ID ));
			repBean.setRepositoryName(rs.getString(DBColumns.COLUMN_REP_NAME));
			repBean.setUserName(rs.getString(DBColumns.COLUMN_REP_USERNAME));
			repBean.setPassword(rs.getString(DBColumns.COLUMN_REP_PASSWORD ));
			repBean.setVersion(rs.getString(DBColumns.COLUMN_REP_VERSION));
			repBean.setDbAccess(rs.getString(DBColumns.COLUMN_REP_DBACCESS)) ;
			repBean.setDbHost(rs.getString(DBColumns.COLUMN_REP_DBHOST)) ;
			repBean.setDbPort(rs.getString(DBColumns.COLUMN_REP_DBPORT)) ;
			repBean.setDbName(rs.getString(DBColumns.COLUMN_REP_DBNAME)) ;
			repBean.setDbType(rs.getString(DBColumns.COLUMN_REP_DBTYPE)) ;
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
		}
		return repBean;
    }
    /**
     * Get repository by repository's id
     * @param repositoryID
     * @return
     */
   public static RepositoryBean getRepositoryByID(int repositoryID){
		String sql = "select * from " + DBColumns.TABLE_REPOSITORY + " where "
				+ DBColumns.COLUMN_REP_ID + " = " + repositoryID;
		RepositoryBean repBean = null;
		Connection conn=null;
		try {
			conn = ConnectionPool.getConnection();
			ResultSet rs = conn.createStatement().executeQuery(sql);
			if (rs.next())
				repBean = getRepositoryBean(rs);
			rs.close();
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
		}finally{
			ConnectionPool.freeConn(null, null, null, conn);
		}
	    return repBean;
   }
   public static void createRepository(RepositoryBean repBean){
	   String maxRepIDSql = "select max("+DBColumns.COLUMN_REP_ID+") from "+ DBColumns.TABLE_REPOSITORY;
	   Connection conn=null;
	   int maxRepositoryID=1;
		try {
			conn = ConnectionPool.getConnection();
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(maxRepIDSql);
			//get max id of repository_id
			if(rs.next())
				maxRepositoryID=rs.getInt(1);
			rs.close();
			//make insert sql
			String insertSql = "insert into "+DBColumns.TABLE_REPOSITORY+"("
			                        +DBColumns.COLUMN_REP_DBACCESS+","+DBColumns.COLUMN_REP_DBHOST+","+DBColumns.COLUMN_REP_DBNAME+","
			                        +DBColumns.COLUMN_REP_DBPORT+","+DBColumns.COLUMN_REP_DBTYPE+","+DBColumns.COLUMN_REP_PASSWORD+","
			                        +DBColumns.COLUMN_REP_ID+","+DBColumns.COLUMN_REP_NAME+","
			                        +DBColumns.COLUMN_USER_NAME+","+DBColumns.COLUMN_REP_VERSION+","+DBColumns.COLUMN_REP_ORGANIZERID
			                        +") values( '"
			                        +repBean.getDbAccess()+"','"+repBean.getDbHost()+"','"+repBean.getDbName()+"','"
			                        +repBean.getDbPort()+"','"+repBean.getDbType()+"','"+repBean.getPassword()+"',"
			                        +(maxRepositoryID+1)+",'"+repBean.getRepositoryName()+"','"
			                        +repBean.getUserName()+"','"+repBean.getVersion()+"'," + repBean.getOrgId() + ")" ;

			st.execute(insertSql);
			st.close();
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
		}finally{
			ConnectionPool.freeConn(null, null, null, conn);
		}	   
   }
   public static void updateRepository(RepositoryBean repBean){
	   String updateSql = "update "+DBColumns.TABLE_REPOSITORY+" set "+DBColumns.COLUMN_REP_NAME+"='"+repBean.getRepositoryName()
	   				+"',"+DBColumns.COLUMN_REP_PASSWORD +"='"+repBean.getPassword()
	   				+"',"+DBColumns.COLUMN_REP_USERNAME+"='"+repBean.getUserName()
	   				+"',"+DBColumns.COLUMN_REP_DBHOST+"='"+repBean.getDbHost()
	   				+"',"+DBColumns.COLUMN_REP_DBPORT+"='"+repBean.getDbPort()
	   				+"',"+DBColumns.COLUMN_REP_DBNAME+"='"+repBean.getDbName()
	   				+"',"+DBColumns.COLUMN_REP_DBACCESS+"='"+repBean.getDbAccess()
	   				+"',"+DBColumns.COLUMN_REP_DBTYPE+"='"+repBean.getDbType()
	   				+"',"+DBColumns.COLUMN_REP_VERSION+"='"+repBean.getVersion() 
	   				+"' where "+DBColumns.COLUMN_REP_ID+"="+repBean.getRepositoryID();
		Connection conn = null;
		Statement stmt = null;
		try {
			conn = ConnectionPool.getConnection();
			stmt = conn.createStatement();
			stmt.execute(updateSql);			
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
		}finally{
			ConnectionPool.freeConn(null, stmt, null, conn);
		}
	}
   public static int getRepositoryCount(){
	   int count=0;
	   String sql = "select count(*) from "+DBColumns.TABLE_REPOSITORY;
	   Connection conn = null;
	   try{
		   conn = ConnectionPool.getConnection();
		   ResultSet rs = conn.createStatement().executeQuery(sql);
		   if(rs.next())
			   count = rs.getInt(1);
		   rs.close();
	   }catch(Exception e){
		   logger.error(e.getMessage(), e);
	   }finally{
		   ConnectionPool.freeConn(null, null, null, conn);
	   }
	   return count;
   }
   public static void deleteRepository(String repIDs){
	   String[] repID = repIDs.split(",");
	   Connection conn = null;
		try {
			conn = ConnectionPool.getConnection();
			Statement st = conn.createStatement();
			for(int i=0;i<repID.length;i++){
				String deleteSql = "delete from "+DBColumns.TABLE_REPOSITORY+" where "+
						DBColumns.COLUMN_REP_ID+"="+Integer.parseInt(repID[i]);
				st.execute(deleteSql);				
			}
			st.close();
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
		}finally{
			 ConnectionPool.freeConn(null, null, null, conn);
		}
	   
   }
   public static List<String> getAllRepNameList()
   {
       List<String> repNameList = new ArrayList<String>(8) ;
       final String query_sql = " select " + DBColumns.COLUMN_REP_NAME + " from " + DBColumns.TABLE_REPOSITORY ;
       Connection conn = null ;
       try
       {
           conn = ConnectionPool.getConnection() ;
           Statement stt = conn.createStatement() ;
           ResultSet rs = stt.executeQuery(query_sql) ;
           
           while(rs.next())
           {
               repNameList.add(rs.getString(1)) ;
           }
           rs.close() ;
           stt.close() ;
           return repNameList ;
       }
       catch (SQLException e)
       {
    	   logger.error(e.getMessage(), e) ;
           return null ;
       }
       finally
       {
           ConnectionPool.freeConn(null,null, null, conn) ;
       }       
   }
   public static List<RepositoryBean> getAllRepositories()
   {
       List<RepositoryBean> repositoryList = new ArrayList<RepositoryBean>(8) ;
       final String query_sql = " select * from " + DBColumns.TABLE_REPOSITORY ;
       Connection conn = null ;
       try
       {
           conn = ConnectionPool.getConnection() ;
           Statement stt = conn.createStatement() ;
           ResultSet rs = stt.executeQuery(query_sql) ;
           
           while(rs.next())
           {
               RepositoryBean bean = getRepositoryBean(rs) ;
               repositoryList.add(bean) ;
           }
           rs.close() ;
           stt.close() ;
           return repositoryList ;
       }
       catch (SQLException e)
       {
    	   logger.error(e.getMessage(), e) ;
           return null ;
       }
       finally
       {
           ConnectionPool.freeConn(null,null, null, conn) ;
       }
   }
   
   public static List<RepositoryBean> getAllRepositories(UserBean userBean)
   {
       List<RepositoryBean> repositoryList = new ArrayList<RepositoryBean>(8) ;
       String query_sql = " select * from " + DBColumns.TABLE_REPOSITORY;
       if(!userBean.isSuperAdmin()){
    	   query_sql = query_sql + " where " + DBColumns.COLUMN_REP_ORGANIZERID + "=" + userBean.getOrgId();
		}
       Connection conn = null ;
       try
       {
           conn = ConnectionPool.getConnection() ;
           Statement stt = conn.createStatement() ;
           ResultSet rs = stt.executeQuery(query_sql) ;
           
           while(rs.next())
           {
               RepositoryBean bean = getRepositoryBean(rs) ;
               repositoryList.add(bean) ;
           }
           rs.close() ;
           stt.close() ;
           return repositoryList ;
       }
       catch (SQLException e)
       {
    	   logger.error(e.getMessage(), e) ;
           return null ;
       }
       finally
       {
           ConnectionPool.freeConn(null,null, null, conn) ;
       }
   }
   
   public static boolean checkRepositoryNameExist(String repositoryName){
	   boolean isExist=false;
	   String sql = "select * from "+ DBColumns.TABLE_REPOSITORY +
			   " where " + DBColumns.COLUMN_REP_NAME+" ='"+repositoryName+"'";
	   Connection conn = null;
	   try{
           conn = ConnectionPool.getConnection() ;
           Statement stt = conn.createStatement() ;
           ResultSet rs = stt.executeQuery(sql) ;	
           if(rs.next())
        	   isExist = true;
           
           rs.close();
           stt.close();
	   }catch(Exception e){
		   logger.error(e.getMessage(), e);
	   }finally
       {
           ConnectionPool.freeConn(null,null, null, conn) ;
       }
	   return isExist;
   }
   
   public static List<RepositoryBean> getRepByVersionAndOrg(String version, int orgId)
   {
       List<RepositoryBean> repositoryList = new ArrayList<RepositoryBean>(8) ;
       final String query_sql = "SELECT * FROM " + DBColumns.TABLE_REPOSITORY  + 
    		   " WHERE " + DBColumns.COLUMN_REP_VERSION + "='" + version + "'" + 
    		   " AND " + DBColumns.COLUMN_REP_ORGANIZERID + "=" + orgId ;
       Connection conn = null ;
       try
       {
           conn = ConnectionPool.getConnection() ;
           Statement stt = conn.createStatement() ;
           ResultSet rs = stt.executeQuery(query_sql) ;
           
           while(rs.next())
           {
               RepositoryBean bean = getRepositoryBean(rs) ;
               repositoryList.add(bean) ;
           }
           rs.close() ;
           stt.close() ;
           return repositoryList ;
       }
       catch (SQLException e)
       {
    	   logger.error(e.getMessage(), e) ;
           return null ;
       }
       finally
       {
           ConnectionPool.freeConn(null,null, null, conn) ;
       }
   }
   
   public static void initDefaultRepository(){
	   if(!checkRepositoryNameExist("Default")){
		   KettleEngine kettleEngine = new KettleEngineImpl4_3();
		   RepositoryBean repBean = new RepositoryBean();
		   ConnConfigBean connConfig = DataBaseUtil.getQuartzConfig();
		   repBean.setDbAccess("Native");
		   repBean.setDbHost(connConfig.getIp());
		   repBean.setDbName(connConfig.getDatabase());
		   repBean.setDbPort(connConfig.getPort());
		   repBean.setDbType(connConfig.getDbms());
		   repBean.setPassword(connConfig.getPassword());
		   repBean.setRepositoryName("Default");
		   repBean.setUserName(connConfig.getUsername());
		   repBean.setVersion(KettleEngine.VERSION_4_3);
		   repBean.setOrgId(1);
		   createRepository(repBean);
		   kettleEngine.createRepository(repBean, false);
	   }
   }
   
   /**
	 * 创建资源库数据库
	 * @param orgBean 机构bean
	 */
	public static void createRepDB(OrganizerBean orgBean){
		Connection conn=null;
		Statement st = null;
		try{
			conn = ConnectionPool.getConnection();
			st = conn.createStatement();
			String repDBName = "rep_" + orgBean.getOrganizer_id();
			String passwd = orgBean.getOrganizer_passwd();
			//create database
			String createDBSQL = "CREATE DATABASE IF NOT EXISTS " + repDBName + " DEFAULT CHARSET utf8";
			st.execute(createDBSQL);
			//create user
			String createUserSQL = "CREATE USER " + repDBName + "@'%' IDENTIFIED BY '" + passwd + "'";
			st.execute(createUserSQL);
			//grant authority
			String grantUserSQL_1 = "GRANT ALL PRIVILEGES ON " + repDBName + ".* TO " + DataBaseUtil.connConfig.getUsername();
			String grantUserSQL = "GRANT ALL PRIVILEGES ON " + repDBName + ".* TO " + repDBName;
			st.execute(grantUserSQL);
			st.execute(grantUserSQL_1);
		}catch (Exception e) {
			logger.error(e.getMessage(), e);
		}finally {
			ConnectionPool.freeConn(null, st, null, conn);
		}	
	}
	
	/**
	 * 创建资源库
	 * @param orgBean 机构bean
	 */
	
	public static void createRepository(OrganizerBean orgBean) {
		String repDBName = "rep_" + orgBean.getOrganizer_id();
		String passwd = orgBean.getOrganizer_passwd();
		ConnConfigBean connConfig = DataBaseUtil.getQuartzConfig();
		
		RepositoryBean repBean = new RepositoryBean();
		try{
			repBean.setRepositoryID(0) ;
	        repBean.setUserName(repDBName) ;
	        repBean.setPassword(passwd) ;
	        repBean.setDbAccess("Native") ;
	        repBean.setDbHost(Constants.get(Constants.WEB_IP));
			repBean.setDbName(repDBName);
			repBean.setDbPort(connConfig.getPort());
			repBean.setDbType(connConfig.getDbms());
	        repBean.setRepositoryName(repDBName) ;
	        repBean.setVersion(KettleEngine.VERSION_4_3) ;
	        repBean.setOrgId(orgBean.getOrganizer_id());
		}catch (Exception e){
			logger.error(e.getMessage(), e);
		}
		
		CreateRepositoryThread createRepThread = new CreateRepositoryThread(repBean);
		createRepThread.start();
	}
}
