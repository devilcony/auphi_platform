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
package com.auphi.ktrl.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.auphi.ktrl.conn.bean.ConnConfigBean;
import com.auphi.ktrl.conn.util.ConnectionPool;
import com.auphi.ktrl.conn.util.DataBaseUtil;
import com.auphi.ktrl.engine.KettleEngine;
import com.auphi.ktrl.engine.impl.KettleEngineImpl4_3;
import com.auphi.ktrl.ha.ServerStatusThreadMain;
import com.auphi.ktrl.metadata.util.MetadataUtil;
import com.auphi.ktrl.monitor.util.MonitorUtil;
import com.auphi.ktrl.schedule.util.QuartzUtil;
import com.auphi.ktrl.system.repository.CreateRepositoryThread;
import com.auphi.ktrl.system.repository.bean.RepositoryBean;
import com.auphi.ktrl.system.repository.util.RepositoryUtil;

/**
 * Servlet implementation class InitServlet
 */
public class InitServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static Logger logger = Logger.getLogger(InitServlet.class);
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public InitServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		System.out.println(InitServlet.class.getResource("/log4j.properties"));
		PropertyConfigurator.configure(InitServlet.class.getResource("/log4j_auphi.properties"));
		
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try{
			//init kettle engine 
			//ClassLoader classLoader_servlet = Thread.currentThread().getContextClassLoader();
			//KettleEngineImpl2_3.init();
			//System.out.println("KingbaseDI V2.0 Engine initialized successfully!");
			//KettleEngineImpl3_2.init();
			//System.out.println("Kettle 3.2 Engine initialized successfully!");
			KettleEngineImpl4_3.init();
			logger.info("AKettle Engine initialized successfully!");
			
			//init connection pool
			ConnectionPool.init();			
			conn = ConnectionPool.getConnection();
			System.out.println("conn--------"+conn);
			String querySQL = "SELECT * FROM QRTZ_TRIGGERS";
			
			stmt = conn.createStatement();
			rs = stmt.executeQuery(querySQL);
		}catch(Exception e){
			try{
				//get conn and init quartz tables
				ConnConfigBean connConfig = DataBaseUtil.getQuartzConfig();
				String filePath = getFilePath(connConfig.getDbms());
				logger.info("Init tables for database:" + connConfig.getDbms());
				logger.info("Execute sql file start:" + filePath);
				DataBaseUtil.executeSQLScript(stmt, filePath);
				logger.info("Execute sql file end:" + filePath);
				
				RepositoryBean repBean = new RepositoryBean();
				repBean.setRepositoryID(0) ;
		        repBean.setUserName(connConfig.getUsername()) ;
		        repBean.setPassword(connConfig.getPassword()) ;
		        repBean.setDbAccess("Native") ;
		        repBean.setDbHost(connConfig.getIp());
				repBean.setDbName(connConfig.getDatabase());
				repBean.setDbPort(connConfig.getPort());
				repBean.setDbType(connConfig.getDbms());
		        repBean.setRepositoryName("Default") ;
		        repBean.setVersion(KettleEngine.VERSION_4_3) ;
		        repBean.setOrgId(1);
		        
		        CreateRepositoryThread createRepThread = new CreateRepositoryThread(repBean);
		        createRepThread.run();
				
			}catch(Exception e1){
				logger.error(e1.getMessage(), e1);
			}
		}finally{
			ConnectionPool.freeConn(rs, stmt, null, conn);
		}
		
		try{
			//init repository
			RepositoryUtil.initDefaultRepository();
			
			//init metadata engine
			MetadataUtil.init();
			//Thread.currentThread().setContextClassLoader(classLoader_servlet);
			
			//init quartz engine
			QuartzUtil.init();
			
//			FastConfigScheduleUtil.init();
			//start ha server monitor
			ServerStatusThreadMain.start_minitor();
			logger.info("AKettle HA server monitor started!");
			
			//set monitor status running to stopped
			MonitorUtil.updateRunningToStoppedInStartUp();
		}catch(Exception e){
			logger.error(e.getMessage(), e);
		}
	}
	
	private String getFilePath(String dbms){
		String filePath = "dbTables/";
		
		if(DataBaseUtil.MYSQL.equals(dbms)){
			filePath = filePath + "tables_mysql.sql";
		}else if(DataBaseUtil.ORACLE.equals(dbms)){
			filePath = filePath + "tables_oracle.sql";
		}else if(DataBaseUtil.SQLSERVER.equals(dbms)){
			filePath = filePath + "tables_sqlServer.sql";
		}else if(DataBaseUtil.DB2.equals(dbms)){
			filePath = filePath + "tables_db2.sql";
		}else if(DataBaseUtil.KINGBASE.equals(dbms)){
			filePath = filePath + "tables_kingbase.sql";
		}else if(DataBaseUtil.POSTGRESQL.equals(dbms)){
			filePath = filePath + "tables_postgres.sql";
		}else {
			filePath = filePath + "tables_normal.sql";
		}
		
		return filePath;
	}
}
