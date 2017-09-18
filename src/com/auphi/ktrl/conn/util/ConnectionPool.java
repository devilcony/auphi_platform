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
package com.auphi.ktrl.conn.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbcp.BasicDataSourceFactory;
import org.apache.log4j.Logger;

import com.auphi.ktrl.schedule.tools.ConnectionPoolTools;
import com.auphi.ktrl.util.InitServlet;

public class ConnectionPool {
	private static Logger logger = Logger.getLogger(ConnectionPool.class);
	
	private static BasicDataSource dataSource = null;

	/**
	 * initialize connection pool
	 */
	public static void init() {
	    if (dataSource != null) {
	        try {
	            dataSource.close();
	        } catch (Exception e) {
	        	logger.error(e.getMessage(), e);
	        }
	        dataSource = null;
	    }

	    try {
	        Properties p = new Properties();
	        p.setProperty("driverClassName", DataBaseUtil.connConfig.getDriverclass());
	        p.setProperty("url", DataBaseUtil.connConfig.getUrl());
	        p.setProperty("password", DataBaseUtil.connConfig.getPassword());
	        p.setProperty("username", DataBaseUtil.connConfig.getUsername());
	        p.setProperty("maxActive", DataBaseUtil.connConfig.getMaxconn());
	        p.setProperty("maxIdle", DataBaseUtil.connConfig.getMaxidle());
	        p.setProperty("maxWait", DataBaseUtil.connConfig.getMaxwait());

	        
            p.setProperty("testOnBorrow", "true");
            p.setProperty("validationQuery", DataBaseUtil.connConfig.getValidateQuery());
            p.setProperty("timeBetweenEvictionRunsMillis", "5000");
            p.setProperty("minEvictableIdleTimeMillis", "600000");
            p.setProperty("removeAbandoned", "true");
            p.setProperty("removeAbandonedTimeout", "600");
            p.setProperty("logAbandoned", "true");
            
            System.out.println("driverClassName = "+p.getProperty("driverClassName"));
            System.out.println("userName = "+p.getProperty("username"));
            
	        dataSource = (BasicDataSource) BasicDataSourceFactory.createDataSource(p);
	        
	        logger.info("Connection Pool initialized successfully!");
	    } catch (Exception e) {
	    	logger.error(e.getMessage(), e);
        }
    }
 
	/**
	 * get connection from connection pool
	 * @return connection
	 * @throws SQLException
	 */
    public static synchronized Connection getConnection(){
        Connection conn = null;
       try{
        	if (dataSource != null) {
                conn = dataSource.getConnection();
            }
        }catch(Exception e){
        	logger.error(e.getMessage(), e);
        }
      //  conn= ConnectionPoolTools.getConnection();  
       /// System.out.println("=============conn 2"+conn);  
        return conn;
    }
    
    /**
     * close all the used db object
     * @param rs
     * @param stmt
     * @param pstmt
     * @param conn
     */
    public static synchronized void freeConn(ResultSet rs, Statement stmt, PreparedStatement pstmt, Connection conn){
    	try{
    		if(rs != null){
				rs.close();
				rs = null;
			}
			if(stmt != null){
				stmt.close();
				stmt = null;
			}
			if(pstmt != null){
				pstmt.close();
				pstmt = null;
			}
			if(conn != null){
				conn.close();
				conn = null;
			}
    	}catch(Exception e){
    		logger.error(e.getMessage(), e);
    	}
    }
}
