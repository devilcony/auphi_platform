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
package com.auphi.ktrl.ha.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.swing.text.TabExpander;

import org.apache.log4j.Logger;

import com.auphi.ktrl.conn.util.ConnectionPool;
import com.auphi.ktrl.conn.util.DataBaseUtil;
import com.auphi.ktrl.ha.bean.ServerStatusBean;
import com.auphi.ktrl.ha.bean.SlaveServerBean;
import com.auphi.ktrl.i18n.Messages;
import com.auphi.ktrl.util.PageInfo;
import com.auphi.ktrl.util.PageList;
import com.auphi.ktrl.util.StringUtil;

public class SlaveServerUtil {
	
	private static Logger logger = Logger.getLogger(SlaveServerUtil.class);
	
	public static final String TABLE_HA_SLAVE           	= "KDI_T_HA_SLAVE";
	public static final String COLUMN_HA_S_ID_SLAVE 		= "ID_SLAVE";
	public static final String COLUMN_HA_S_NAME 			= "NAME";
	public static final String COLUMN_HA_S_HOST_NAME 		= "HOST_NAME";
	public static final String COLUMN_HA_S_PORT 			= "PORT";
	public static final String COLUMN_HA_S_WEB_APP_NAME 	= "WEB_APP_NAME";
	public static final String COLUMN_HA_S_USERNAME 		= "USERNAME";
	public static final String COLUMN_HA_S_PASSWORD 		= "PASSWORD";
	public static final String COLUMN_HA_S_PROXY_HOST_NAME 	= "PROXY_HOST_NAME";
	public static final String COLUMN_HA_S_PROXY_PORT 		= "PROXY_PORT";
	public static final String COLUMN_HA_S_NON_PROXY_HOSTS 	= "NON_PROXY_HOSTS";
	public static final String COLUMN_HA_S_MASTER 			= "MASTER";

	public static final String TABLE_HA_SLAVE_STATUS       	= "KDI_T_HA_SLAVE_STATUS";
	public static final String COLUMN_HA_SS_ID_STATUS 		= "ID_STATUS";
	public static final String COLUMN_HA_SS_ID_SLAVE		= "ID_SLAVE";
	public static final String COLUMN_HA_SS_IS_RUNING		= "IS_RUNING";
	public static final String COLUMN_HA_SS_CPU_USAGE		= "CPU_USAGE";
	public static final String COLUMN_HA_SS_MEMORY_USAGE	= "MEMORY_USAGE";
	public static final String COLUMN_HA_SS_RUNING_JOBS_NUM	= "RUNING_JOBS_NUM";

	/**
	 * get all slave servers by page
	 * @param page
	 * @return
	 */
	public static PageList findAllSlaveServers(int page){
		PageList pageList = new PageList();
		List<SlaveServerBean> listSlaveServer = new ArrayList<SlaveServerBean>();
		int count = 0;
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			String sql = "SELECT * FROM " + TABLE_HA_SLAVE;
			String sqlData = DataBaseUtil.generatePagingSQL(sql, page, "NAME", "ASC");
			String sqlCount = "SELECT COUNT(*) FROM (" + sql + ") A";
			
			conn = ConnectionPool.getConnection();
			stmt = conn.createStatement();
			
			rs = stmt.executeQuery(sqlData);
			
			while(rs.next()){
				SlaveServerBean slaveServerBean = new SlaveServerBean();
				
				slaveServerBean.setId_slave(rs.getInt(COLUMN_HA_S_ID_SLAVE));
				slaveServerBean.setName(rs.getString(COLUMN_HA_S_NAME));
				slaveServerBean.setHost_name(rs.getString(COLUMN_HA_S_HOST_NAME));
				slaveServerBean.setPort(rs.getString(COLUMN_HA_S_PORT));
				slaveServerBean.setWeb_app_name(rs.getString(COLUMN_HA_S_WEB_APP_NAME));
				slaveServerBean.setUsername(rs.getString(COLUMN_HA_S_USERNAME));
				slaveServerBean.setPassword(rs.getString(COLUMN_HA_S_PASSWORD));
				slaveServerBean.setProxy_host_name(rs.getString(COLUMN_HA_S_PROXY_HOST_NAME));
				slaveServerBean.setProxy_port(rs.getString(COLUMN_HA_S_PROXY_PORT));
				String is_proxy = rs.getString(COLUMN_HA_S_NON_PROXY_HOSTS);
				if("0".equals(is_proxy)){
					is_proxy = Messages.getString("Default.Jsp.No");
				}else if("1".equals(is_proxy)){
					is_proxy = Messages.getString("Default.Jsp.Yes");
				}
				slaveServerBean.setNon_proxy_hosts(is_proxy);
				String is_master = rs.getString(COLUMN_HA_S_MASTER);
				if("0".equals(is_master)){
					is_master = Messages.getString("Default.Jsp.No");
				}else if("1".equals(is_master)){
					is_master = Messages.getString("Default.Jsp.Yes");
				}
				slaveServerBean.setMaster(is_master);
				
				listSlaveServer.add(slaveServerBean);
			}
			
			ResultSet rs_count = stmt.executeQuery(sqlCount);
			while(rs_count.next()){
				count = rs_count.getInt(1);
			}
			rs_count.close();
			
			pageList.setList(listSlaveServer);
			PageInfo pageInfo = new PageInfo(page, count);
			pageList.setPageInfo(pageInfo);
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			ConnectionPool.freeConn(rs, stmt, null, conn);
		}
		
		return pageList;
	}
	
	/**
	 * get all slave servers by page
	 * @param page
	 * @return
	 */
	public static List<SlaveServerBean> findAll(){
		List<SlaveServerBean> listSlaveServer = new ArrayList<SlaveServerBean>();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			String sql = "SELECT * FROM " + TABLE_HA_SLAVE;
			
			conn = ConnectionPool.getConnection();
			stmt = conn.createStatement();
			
			rs = stmt.executeQuery(sql);
			
			while(rs.next()){
				SlaveServerBean slaveServerBean = new SlaveServerBean();
				
				slaveServerBean.setId_slave(rs.getInt(COLUMN_HA_S_ID_SLAVE));
				slaveServerBean.setName(rs.getString(COLUMN_HA_S_NAME));
				slaveServerBean.setHost_name(rs.getString(COLUMN_HA_S_HOST_NAME));
				slaveServerBean.setPort(rs.getString(COLUMN_HA_S_PORT));
				slaveServerBean.setWeb_app_name(rs.getString(COLUMN_HA_S_WEB_APP_NAME));
				slaveServerBean.setUsername(rs.getString(COLUMN_HA_S_USERNAME));
				slaveServerBean.setPassword(rs.getString(COLUMN_HA_S_PASSWORD));
				slaveServerBean.setProxy_host_name(rs.getString(COLUMN_HA_S_PROXY_HOST_NAME));
				slaveServerBean.setProxy_port(rs.getString(COLUMN_HA_S_PROXY_PORT));
				String is_proxy = rs.getString(COLUMN_HA_S_NON_PROXY_HOSTS);
				if("0".equals(is_proxy)){
					is_proxy = Messages.getString("Default.Jsp.No");
				}else if("1".equals(is_proxy)){
					is_proxy = Messages.getString("Default.Jsp.Yes");
				}
				slaveServerBean.setNon_proxy_hosts(is_proxy);
				String is_master = rs.getString(COLUMN_HA_S_MASTER);
				if("0".equals(is_master)){
					is_master = Messages.getString("Default.Jsp.No");
				}else if("1".equals(is_master)){
					is_master = Messages.getString("Default.Jsp.Yes");
				}
				slaveServerBean.setMaster(is_master);
				
				listSlaveServer.add(slaveServerBean);
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			ConnectionPool.freeConn(rs, stmt, null, conn);
		}
		
		return listSlaveServer;
	}
	
	/**
	 * 增加远程ETL子服务器
	 * @param slaveServerBean 远程ETL服务器Bean
	 */
	public static void addSlaveServer(SlaveServerBean slaveServerBean){
		Connection conn = null;
		Statement stmt = null;
		PreparedStatement pstmt = null;
		try{
			
			conn = ConnectionPool.getConnection();
			stmt = conn.createStatement();
			
			String sql = "INSERT INTO " + TABLE_HA_SLAVE + "(" + 
							COLUMN_HA_S_ID_SLAVE + "," + 
							COLUMN_HA_S_NAME + "," + 
							COLUMN_HA_S_HOST_NAME + "," + 
							COLUMN_HA_S_PORT + "," + 
							COLUMN_HA_S_WEB_APP_NAME + "," + 
							COLUMN_HA_S_USERNAME + "," + 
							COLUMN_HA_S_PASSWORD + "," + 
							COLUMN_HA_S_PROXY_HOST_NAME + "," + 
							COLUMN_HA_S_PROXY_PORT + "," + 
							COLUMN_HA_S_NON_PROXY_HOSTS + "," + 
							COLUMN_HA_S_MASTER + ") VALUES(?,?,?,?,?,?,?,?,?,?,?)";
			pstmt = conn.prepareStatement(sql);
			String id_slave = StringUtil.createNumberString(8);
			pstmt.setInt(1, Integer.parseInt(id_slave));
			pstmt.setString(2, slaveServerBean.getName());
			pstmt.setString(3, slaveServerBean.getHost_name());
			pstmt.setString(4, slaveServerBean.getPort());
			pstmt.setString(5, slaveServerBean.getWeb_app_name());
			pstmt.setString(6, slaveServerBean.getUsername());
			pstmt.setString(7, slaveServerBean.getPassword());
			pstmt.setString(8, slaveServerBean.getProxy_host_name());
			pstmt.setString(9, slaveServerBean.getProxy_port());
			pstmt.setString(10, slaveServerBean.getNon_proxy_hosts());
			pstmt.setString(11, slaveServerBean.getMaster());
			
			pstmt.execute();
			
			String sql_slave_status = "INSERT INTO " + TABLE_HA_SLAVE_STATUS + "(" + COLUMN_HA_SS_ID_SLAVE + ") VALUES(" + id_slave + ")";
			stmt.executeUpdate(sql_slave_status);
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			ConnectionPool.freeConn(null, stmt, pstmt, conn);
		}
	}
	
	/**
	 * 获取子服务器
	 * @param id 子服务器id
	 * @return SlaveServerBean 子服务器信息bean
	 */
	public static SlaveServerBean getSlaveServer(String id){
		SlaveServerBean slaveServerBean = new SlaveServerBean();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionPool.getConnection();
			stmt = conn.createStatement();
			
			rs = stmt.executeQuery("SELECT * FROM " + TABLE_HA_SLAVE + " WHERE " + COLUMN_HA_S_ID_SLAVE + "=" + id);
			if(rs.next()){
				slaveServerBean.setId_slave(rs.getInt(COLUMN_HA_S_ID_SLAVE));
				slaveServerBean.setName(rs.getString(COLUMN_HA_S_NAME)==null?"":rs.getString(COLUMN_HA_S_NAME));
				slaveServerBean.setHost_name(rs.getString(COLUMN_HA_S_HOST_NAME)==null?"":rs.getString(COLUMN_HA_S_HOST_NAME));
				slaveServerBean.setPort(rs.getString(COLUMN_HA_S_PORT)==null?"":rs.getString(COLUMN_HA_S_PORT));
				slaveServerBean.setWeb_app_name(rs.getString(COLUMN_HA_S_WEB_APP_NAME)==null?"":rs.getString(COLUMN_HA_S_WEB_APP_NAME));
				slaveServerBean.setUsername(rs.getString(COLUMN_HA_S_USERNAME)==null?"":rs.getString(COLUMN_HA_S_USERNAME));
				slaveServerBean.setPassword(rs.getString(COLUMN_HA_S_PASSWORD)==null?"":rs.getString(COLUMN_HA_S_PASSWORD));
				slaveServerBean.setProxy_host_name(rs.getString(COLUMN_HA_S_PROXY_HOST_NAME)==null?"":rs.getString(COLUMN_HA_S_PROXY_HOST_NAME));
				slaveServerBean.setProxy_port(rs.getString(COLUMN_HA_S_PROXY_PORT)==null?"":rs.getString(COLUMN_HA_S_PROXY_PORT));
				slaveServerBean.setNon_proxy_hosts(rs.getString(COLUMN_HA_S_NON_PROXY_HOSTS)==null?"":rs.getString(COLUMN_HA_S_NON_PROXY_HOSTS));
				slaveServerBean.setMaster(rs.getString(COLUMN_HA_S_MASTER)==null?"":rs.getString(COLUMN_HA_S_MASTER));
			}
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			ConnectionPool.freeConn(rs, stmt, null, conn);
		}
		
		return slaveServerBean;
	}
	
	/**
	 * 更新子服务器信息
	 * @param slaveServerBean 子服务器bean
	 */
	public static void updateSlaveServer(SlaveServerBean slaveServerBean){
		String sql = "UPDATE " + TABLE_HA_SLAVE + " SET " + 
						COLUMN_HA_S_NAME + "=?," + COLUMN_HA_S_HOST_NAME + "=?," + 
						COLUMN_HA_S_PORT + "=?," + COLUMN_HA_S_WEB_APP_NAME + "=?," + 
						COLUMN_HA_S_USERNAME + "=?," + COLUMN_HA_S_PASSWORD + "=?," + 
						COLUMN_HA_S_PROXY_HOST_NAME + "=?," + COLUMN_HA_S_PROXY_PORT + "=?," + 
						COLUMN_HA_S_NON_PROXY_HOSTS + "=?," + COLUMN_HA_S_MASTER + "=? WHERE " + COLUMN_HA_S_ID_SLAVE + "=?";
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		try{
			conn = ConnectionPool.getConnection();
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, slaveServerBean.getName());
			pstmt.setString(2, slaveServerBean.getHost_name());
			pstmt.setString(3, slaveServerBean.getPort());
			pstmt.setString(4, slaveServerBean.getWeb_app_name());
			pstmt.setString(5, slaveServerBean.getUsername());
			pstmt.setString(6, slaveServerBean.getPassword());
			pstmt.setString(7, slaveServerBean.getProxy_host_name());
			pstmt.setString(8, slaveServerBean.getProxy_port());
			pstmt.setString(9, slaveServerBean.getNon_proxy_hosts());
			pstmt.setString(10, slaveServerBean.getMaster());
			pstmt.setInt(11, slaveServerBean.getId_slave());
			
			pstmt.executeUpdate();
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			ConnectionPool.freeConn(null, null, pstmt, conn);
		}
	}
	
	/**
	 * 删除子服务器
	 * @param sel_ids 所选子服务器id
	 */
	public static void deleteSlaveServer(String sel_ids){
		String sql = "DELETE FROM " + TABLE_HA_SLAVE + " WHERE " + COLUMN_HA_S_ID_SLAVE + " IN (" + sel_ids + ")";
		String sql_status = "DELETE FROM " + TABLE_HA_SLAVE_STATUS + " WHERE " + COLUMN_HA_SS_ID_SLAVE + " IN (" + sel_ids + ")";
		String sql_cluster_slave = "DELETE FROM " + HAClusterUtil.TABLE_HA_CLUSTER_SLAVE + " WHERE " + HAClusterUtil.COLUMN_HA_CS_ID_SLAVE + " IN (" + sel_ids + ")";
		Connection conn = null;
		Statement stmt = null;
		
		try {
			conn = ConnectionPool.getConnection();
			stmt = conn.createStatement();
			
			stmt.executeUpdate(sql);
			stmt.executeUpdate(sql_status);
			stmt.executeUpdate(sql_cluster_slave);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage(),e);
		}finally{
			ConnectionPool.freeConn(null, stmt, null, conn);
		}
	}
	
	/**
	 * 名称是否存在
	 * @param slave_name 子服务器名称
	 * @param old_slave_id 子服务器id
	 * @return
	 */
	public static boolean nameExists(String slave_name, String old_slave_id){
		boolean exists = false;
		String sql = "SELECT " + COLUMN_HA_S_ID_SLAVE + " FROM " + TABLE_HA_SLAVE + " WHERE " + COLUMN_HA_S_NAME + "='" + slave_name + "'";
		if(!"".equals(old_slave_id)){
			sql = sql + " AND " + COLUMN_HA_S_ID_SLAVE + "!=" + old_slave_id;
		}
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			conn = ConnectionPool.getConnection();
			stmt = conn.createStatement();
			
			rs = stmt.executeQuery(sql);
			
			if(rs.next()){
				exists = true;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage(),e);
		}finally{
			ConnectionPool.freeConn(rs, stmt, null, conn);
		}
		
		return exists;
	}
	
	/**
	 * 主机名端口是否存在
	 * @param host 子服务器主机名
	 * @param port 子服务器端口
	 * @param old_slave_id 子服务器id
	 * @return
	 */
	public static boolean hostAndPortExists(String host, String port, String old_slave_id){
		boolean exists = false;
		String sql = "SELECT " + COLUMN_HA_S_ID_SLAVE + " FROM " + TABLE_HA_SLAVE + " WHERE " + COLUMN_HA_S_HOST_NAME + "='" + host + "' AND " + COLUMN_HA_S_PORT + "='" + port + "'";
		if(!"".equals(old_slave_id)){
			sql = sql + " AND " + COLUMN_HA_S_ID_SLAVE + "!=" + old_slave_id;
		}
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			conn = ConnectionPool.getConnection();
			stmt = conn.createStatement();
			
			rs = stmt.executeQuery(sql);
			
			if(rs.next()){
				exists = true;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage(),e);
		}finally{
			ConnectionPool.freeConn(rs, stmt, null, conn);
		}
		
		return exists;
	}
	
	/**
	 * 更新子服务器信息
	 * @param slaveServerBean 子服务器bean
	 */
	public synchronized static void updateServerStatus(ServerStatusBean serverStatusBean){
		String sql = "UPDATE " + TABLE_HA_SLAVE_STATUS + " SET " + 
						COLUMN_HA_SS_IS_RUNING + "=?," + 
						COLUMN_HA_SS_CPU_USAGE + "=?," + 
						COLUMN_HA_SS_MEMORY_USAGE + "=?," + 
						COLUMN_HA_SS_RUNING_JOBS_NUM + "=? WHERE " + COLUMN_HA_SS_ID_SLAVE + "=?";
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		try{
			conn = ConnectionPool.getConnection();
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, serverStatusBean.getIs_running());
			pstmt.setFloat(2, serverStatusBean.getCpu_usage());
			pstmt.setFloat(3, serverStatusBean.getMemory_usage());
			pstmt.setInt(4, serverStatusBean.getRunning_jobs_num());
			pstmt.setInt(5, serverStatusBean.getId_slave());
			
			pstmt.executeUpdate();
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			ConnectionPool.freeConn(null, null, pstmt, conn);
		}
	}
	
	/**
	 * get best slave server form cluster
	 * @param id_cluster
	 * @return
	 */
	public static SlaveServerBean getBestServerFromCluster(String id_cluster){
		SlaveServerBean slaveServerBean = new SlaveServerBean();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		String sql = "SELECT b.* from " + HAClusterUtil.TABLE_HA_CLUSTER_SLAVE + " a " +
					 " LEFT JOIN " + TABLE_HA_SLAVE + " b ON a." + HAClusterUtil.COLUMN_HA_CS_ID_SLAVE + "=b." + COLUMN_HA_S_ID_SLAVE +  
					 " LEFT JOIN " + TABLE_HA_SLAVE_STATUS + " c ON a." + HAClusterUtil.COLUMN_HA_CS_ID_SLAVE + "=c." + COLUMN_HA_SS_ID_SLAVE +  
					 " WHERE c." + COLUMN_HA_SS_IS_RUNING + "=1 AND a." + HAClusterUtil.COLUMN_HA_CS_ID_CLUSTER + "=" + id_cluster + 
					 " ORDER BY c." + COLUMN_HA_SS_RUNING_JOBS_NUM + ",c." + COLUMN_HA_SS_CPU_USAGE + ",c." + COLUMN_HA_SS_MEMORY_USAGE;
		
		try{
			conn = ConnectionPool.getConnection();
			stmt = conn.createStatement();
			
			rs = stmt.executeQuery(sql);
			if(rs.next()){
				slaveServerBean.setId_slave(rs.getInt(COLUMN_HA_S_ID_SLAVE));
				slaveServerBean.setName(rs.getString(COLUMN_HA_S_NAME)==null?"":rs.getString(COLUMN_HA_S_NAME));
				slaveServerBean.setHost_name(rs.getString("HOST_NAME")==null?"":rs.getString(COLUMN_HA_S_HOST_NAME));
				slaveServerBean.setPort(rs.getString(COLUMN_HA_S_PORT)==null?"":rs.getString(COLUMN_HA_S_PORT));
				slaveServerBean.setWeb_app_name(rs.getString(COLUMN_HA_S_WEB_APP_NAME)==null?"":rs.getString(COLUMN_HA_S_WEB_APP_NAME));
				slaveServerBean.setUsername(rs.getString(COLUMN_HA_S_USERNAME)==null?"":rs.getString(COLUMN_HA_S_USERNAME));
				slaveServerBean.setPassword(rs.getString(COLUMN_HA_S_PASSWORD)==null?"":rs.getString(COLUMN_HA_S_PASSWORD));
				slaveServerBean.setProxy_host_name(rs.getString(COLUMN_HA_S_PROXY_HOST_NAME)==null?"":rs.getString(COLUMN_HA_S_PROXY_HOST_NAME));
				slaveServerBean.setProxy_port(rs.getString(COLUMN_HA_S_PROXY_PORT)==null?"":rs.getString(COLUMN_HA_S_PROXY_PORT));
				slaveServerBean.setNon_proxy_hosts(rs.getString(COLUMN_HA_S_NON_PROXY_HOSTS)==null?"":rs.getString(COLUMN_HA_S_NON_PROXY_HOSTS));
				slaveServerBean.setMaster(rs.getString(COLUMN_HA_S_MASTER)==null?"":rs.getString(COLUMN_HA_S_MASTER));
			}
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			ConnectionPool.freeConn(rs, stmt, null, conn);
		}
		
		return slaveServerBean;
	}
	
	/**
	 * get best slave server form cluster
	 * @param id_cluster
	 * @return
	 */
	public static List<ServerStatusBean> getServerStatusFromCluster(String id_cluster){
		List<ServerStatusBean> listServerStatus = new ArrayList<ServerStatusBean>();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		String sql = "SELECT a.*,c." + COLUMN_HA_S_NAME + " FROM " + TABLE_HA_SLAVE_STATUS + " a " + 
					 " LEFT JOIN " + HAClusterUtil.TABLE_HA_CLUSTER_SLAVE + " b ON a." + COLUMN_HA_SS_ID_SLAVE + "=b." + HAClusterUtil.COLUMN_HA_CS_ID_SLAVE +   
					 " LEFT JOIN " + TABLE_HA_SLAVE + " c ON a." + COLUMN_HA_SS_ID_SLAVE + "=c." + COLUMN_HA_S_ID_SLAVE +  
					 " WHERE b." + HAClusterUtil.COLUMN_HA_CS_ID_CLUSTER + "=" + id_cluster;
		
		try{
			conn = ConnectionPool.getConnection();
			stmt = conn.createStatement();
			
			rs = stmt.executeQuery(sql);
			while(rs.next()){
				ServerStatusBean serverStatus = new ServerStatusBean();
				serverStatus.setId_slave(rs.getInt(COLUMN_HA_SS_ID_SLAVE));
				serverStatus.setIs_running(rs.getInt(COLUMN_HA_SS_IS_RUNING));
				serverStatus.setCpu_usage(rs.getFloat(COLUMN_HA_SS_CPU_USAGE));
				serverStatus.setMemory_usage(rs.getFloat(COLUMN_HA_SS_MEMORY_USAGE));
				serverStatus.setRunning_jobs_num(rs.getInt(COLUMN_HA_SS_RUNING_JOBS_NUM));
				serverStatus.setName_slave(rs.getString(COLUMN_HA_S_NAME));
				
				listServerStatus.add(serverStatus);
			}
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			ConnectionPool.freeConn(rs, stmt, null, conn);
		}
		
		return listServerStatus;
	}
}
