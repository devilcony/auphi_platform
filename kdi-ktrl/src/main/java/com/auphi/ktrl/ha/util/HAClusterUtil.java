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

import org.apache.log4j.Logger;

import com.auphi.ktrl.conn.util.ConnectionPool;
import com.auphi.ktrl.conn.util.DataBaseUtil;
import com.auphi.ktrl.ha.bean.HAClusterBean;
import com.auphi.ktrl.i18n.Messages;
import com.auphi.ktrl.util.PageInfo;
import com.auphi.ktrl.util.PageList;
import com.auphi.ktrl.util.StringUtil;

public class HAClusterUtil {
	
	public static final String TABLE_HA_CLUSTER             		= "KDI_T_HA_CLUSTER";
    public static final String COLUMN_HA_C_ID_CLUSTER        		= "ID_CLUSTER";
	public static final String COLUMN_HA_C_NAME 					= "NAME";
	public static final String COLUMN_HA_C_BASE_PORT		    	= "BASE_PORT";
	public static final String COLUMN_HA_C_SOCKETS_BUFFER_SIZE 		= "SOCKETS_BUFFER_SIZE";
	public static final String COLUMN_HA_C_SOCKETS_FLUSH_INTERVAL	= "SOCKETS_FLUSH_INTERVAL";
	public static final String COLUMN_HA_C_SOCKETS_COMPRESSED		= "SOCKETS_COMPRESSED";
	public static final String COLUMN_HA_C_DYNAMIC_CLUSTER			= "DYNAMIC_CLUSTER";
	
	public static final String TABLE_HA_CLUSTER_SLAVE          		= "KDI_T_HA_CLUSTER_SLAVE";
	public static final String COLUMN_HA_CS_ID_CLUSTER_SLAVE		= "ID_CLUSTER_SLAVE";
	public static final String COLUMN_HA_CS_ID_CLUSTER				= "ID_CLUSTER";
	public static final String COLUMN_HA_CS_ID_SLAVE				= "ID_SLAVE";

	private static Logger logger = Logger.getLogger(HAClusterUtil.class);
	
	/**
	 * get all clusters by page
	 * @param page
	 * @return
	 */
	public static PageList findAllClusters(int page){
		PageList pageList = new PageList();
		List<HAClusterBean> listClusters = new ArrayList<HAClusterBean>();
		int count = 0;
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			String sql = "SELECT * FROM " + TABLE_HA_CLUSTER;
			String sqlData = DataBaseUtil.generatePagingSQL(sql, page, COLUMN_HA_C_NAME, "ASC");
			String sqlCount = "SELECT COUNT(*) FROM (" + sql + ") A";
			
			conn = ConnectionPool.getConnection();
			stmt = conn.createStatement();
			
			rs = stmt.executeQuery(sqlData);
			
			while(rs.next()){
				HAClusterBean haClusterBean = new HAClusterBean();
				
				haClusterBean.setId_cluster(rs.getInt(COLUMN_HA_C_ID_CLUSTER));
				haClusterBean.setName(rs.getString(COLUMN_HA_C_NAME));
				haClusterBean.setBase_port(rs.getString(COLUMN_HA_C_BASE_PORT));
				haClusterBean.setSockets_buffer_size(rs.getString(COLUMN_HA_C_SOCKETS_BUFFER_SIZE));
				haClusterBean.setSockets_flush_interval(rs.getString(COLUMN_HA_C_SOCKETS_FLUSH_INTERVAL));
				String sockets_compressed = rs.getString(COLUMN_HA_C_SOCKETS_COMPRESSED);
				if("0".equals(sockets_compressed)){
					sockets_compressed = Messages.getString("Default.Jsp.No");
				}else if("1".equals(sockets_compressed)){
					sockets_compressed = Messages.getString("Default.Jsp.Yes");
				}
				haClusterBean.setSockets_compressed(sockets_compressed);
				String dynamic_cluster = rs.getString(COLUMN_HA_C_DYNAMIC_CLUSTER);
				if("0".equals(dynamic_cluster)){
					dynamic_cluster = Messages.getString("Default.Jsp.No");
				}else if("1".equals(dynamic_cluster)){
					dynamic_cluster = Messages.getString("Default.Jsp.Yes");
				}
				haClusterBean.setDynamic_cluster(dynamic_cluster);
				haClusterBean.setSlaves(getSlaves(conn, rs.getInt(COLUMN_HA_C_ID_CLUSTER)));
				
				listClusters.add(haClusterBean);
			}
			
			ResultSet rs_count = stmt.executeQuery(sqlCount);
			while(rs_count.next()){
				count = rs_count.getInt(1);
			}
			rs_count.close();
			
			pageList.setList(listClusters);
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
	 * get all clusters
	 * @return
	 */
	public static List<HAClusterBean> findAll(){
		List<HAClusterBean> listClusters = new ArrayList<HAClusterBean>();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			String sql = "SELECT * FROM "+ TABLE_HA_CLUSTER;
			
			conn = ConnectionPool.getConnection();
			stmt = conn.createStatement();
			
			rs = stmt.executeQuery(sql);
			
			while(rs.next()){
				HAClusterBean haClusterBean = new HAClusterBean();
				
				haClusterBean.setId_cluster(rs.getInt(COLUMN_HA_C_ID_CLUSTER));
				haClusterBean.setName(rs.getString(COLUMN_HA_C_NAME));
				haClusterBean.setBase_port(rs.getString(COLUMN_HA_C_BASE_PORT));
				haClusterBean.setSockets_buffer_size(rs.getString(COLUMN_HA_C_SOCKETS_BUFFER_SIZE));
				haClusterBean.setSockets_flush_interval(rs.getString(COLUMN_HA_C_SOCKETS_FLUSH_INTERVAL));
				String sockets_compressed = rs.getString(COLUMN_HA_C_SOCKETS_COMPRESSED);
				if("0".equals(sockets_compressed)){
					sockets_compressed = Messages.getString("Default.Jsp.No");
				}else if("1".equals(sockets_compressed)){
					sockets_compressed = Messages.getString("Default.Jsp.Yes");
				}
				haClusterBean.setSockets_compressed(sockets_compressed);
				String dynamic_cluster = rs.getString(COLUMN_HA_C_DYNAMIC_CLUSTER);
				if("0".equals(dynamic_cluster)){
					dynamic_cluster = Messages.getString("Default.Jsp.No");
				}else if("1".equals(dynamic_cluster)){
					dynamic_cluster = Messages.getString("Default.Jsp.Yes");
				}
				haClusterBean.setDynamic_cluster(dynamic_cluster);
				haClusterBean.setSlaves(getSlaves(conn, rs.getInt(COLUMN_HA_C_ID_CLUSTER)));
				
				listClusters.add(haClusterBean);
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			ConnectionPool.freeConn(rs, stmt, null, conn);
		}
		
		return listClusters;
	}
	
	/**
	 * add ha cluster
	 * @param haClusterBean
	 */
	public static void addCluster(HAClusterBean haClusterBean){
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		try{
			
			conn = ConnectionPool.getConnection();
			
			String sql = "INSERT INTO " + 
					TABLE_HA_CLUSTER + 
					"(" + 
					COLUMN_HA_C_ID_CLUSTER + "," + 
					COLUMN_HA_C_NAME + "," + 
					COLUMN_HA_C_BASE_PORT + "," + 
					COLUMN_HA_C_SOCKETS_BUFFER_SIZE + "," + 
					COLUMN_HA_C_SOCKETS_FLUSH_INTERVAL + "," + 
					COLUMN_HA_C_SOCKETS_COMPRESSED + "," + 
					COLUMN_HA_C_DYNAMIC_CLUSTER + 
					") VALUES(?,?,?,?,?,?,?)";  
			pstmt = conn.prepareStatement(sql);
			String id_cluster = StringUtil.createNumberString(8);
			pstmt.setInt(1, Integer.parseInt(id_cluster));
			pstmt.setString(2, haClusterBean.getName());
			pstmt.setString(3, haClusterBean.getBase_port());
			pstmt.setString(4, haClusterBean.getSockets_buffer_size());
			pstmt.setString(5, haClusterBean.getSockets_flush_interval());
			pstmt.setString(6, haClusterBean.getSockets_compressed());
			pstmt.setString(7, haClusterBean.getDynamic_cluster());
			
			pstmt.execute();
			
			addSlaves(conn, Integer.parseInt(id_cluster), haClusterBean.getSlaves()[0]);
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			ConnectionPool.freeConn(null, null, pstmt, conn);
		}
	}
	
	/**
	 * get single cluster info
	 * @param cluster_id
	 * @return
	 */
	public static HAClusterBean getCluster(int cluster_id){
		HAClusterBean haClusterBean = new HAClusterBean();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionPool.getConnection();
			stmt = conn.createStatement();
			
			rs = stmt.executeQuery("SELECT * FROM " + TABLE_HA_CLUSTER + " WHERE " + COLUMN_HA_C_ID_CLUSTER + "=" + cluster_id);
			if(rs.next()){
				haClusterBean.setId_cluster(rs.getInt(COLUMN_HA_C_ID_CLUSTER));
				haClusterBean.setName(rs.getString(COLUMN_HA_C_NAME)==null?"":rs.getString(COLUMN_HA_C_NAME));
				haClusterBean.setBase_port(rs.getString(COLUMN_HA_C_BASE_PORT)==null?"":rs.getString(COLUMN_HA_C_BASE_PORT));
				haClusterBean.setSockets_buffer_size(rs.getString(COLUMN_HA_C_SOCKETS_BUFFER_SIZE)==null?"":rs.getString(COLUMN_HA_C_SOCKETS_BUFFER_SIZE));
				haClusterBean.setSockets_flush_interval(rs.getString(COLUMN_HA_C_SOCKETS_FLUSH_INTERVAL)==null?"":rs.getString(COLUMN_HA_C_SOCKETS_FLUSH_INTERVAL));
				haClusterBean.setSockets_compressed(rs.getString(COLUMN_HA_C_SOCKETS_COMPRESSED)==null?"":rs.getString(COLUMN_HA_C_SOCKETS_COMPRESSED));
				haClusterBean.setDynamic_cluster(rs.getString(COLUMN_HA_C_DYNAMIC_CLUSTER)==null?"":rs.getString(COLUMN_HA_C_DYNAMIC_CLUSTER));
				haClusterBean.setSlaves(getSlaves(conn, cluster_id));
			}
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			ConnectionPool.freeConn(rs, stmt, null, conn);
		}
		
		return haClusterBean;
	}
	
	/**
	 * update cluster
	 * @param haClusterBean
	 * @param cluster_id
	 */
	public static void updateCluster(HAClusterBean haClusterBean){
		String sql = "UPDATE " + TABLE_HA_CLUSTER + " SET " + 
				COLUMN_HA_C_NAME + "=?," + 
				COLUMN_HA_C_BASE_PORT + "=?," + 
				COLUMN_HA_C_SOCKETS_BUFFER_SIZE + "=?," + 
				COLUMN_HA_C_SOCKETS_FLUSH_INTERVAL + "=?," + 
				COLUMN_HA_C_SOCKETS_COMPRESSED + "=?," + 
				COLUMN_HA_C_DYNAMIC_CLUSTER + "=? WHERE " + 
				COLUMN_HA_C_ID_CLUSTER + "=?";
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		try{
			conn = ConnectionPool.getConnection();
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, haClusterBean.getName());
			pstmt.setString(2, haClusterBean.getBase_port());
			pstmt.setString(3, haClusterBean.getSockets_buffer_size());
			pstmt.setString(4, haClusterBean.getSockets_flush_interval());
			pstmt.setString(5, haClusterBean.getSockets_compressed());
			pstmt.setString(6, haClusterBean.getDynamic_cluster());
			pstmt.setInt(7, haClusterBean.getId_cluster());
			
			pstmt.executeUpdate();
			
			deleteSlaves(conn, String.valueOf(haClusterBean.getId_cluster()));
			addSlaves(conn, haClusterBean.getId_cluster(), haClusterBean.getSlaves()[0]);
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			ConnectionPool.freeConn(null, null, pstmt, conn);
		}
	}
	
	/**
	 * 名称是否存在
	 * @param cluster_name 集群名称
	 * @param cluster_id 集群id
	 * @return
	 */
	public static boolean nameExists(String cluster_name, String cluster_id){
		boolean exists = false;
		String sql = "SELECT " + COLUMN_HA_C_ID_CLUSTER + " FROM " + TABLE_HA_CLUSTER + " WHERE " + COLUMN_HA_C_NAME + "='" + cluster_name + "'";
		if(cluster_id!=null && !"".equals(cluster_id)){
			sql = sql + " AND " + COLUMN_HA_C_ID_CLUSTER + "!=" + cluster_id;
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
	
	public static String getSlavesInUse(String slave_ids, String cluster_id){
		String slaves_in_use = "";
		String sql = "SELECT A." + COLUMN_HA_CS_ID_CLUSTER + ",B." + SlaveServerUtil.COLUMN_HA_S_NAME + " FROM " + 
				TABLE_HA_CLUSTER_SLAVE + " A," + SlaveServerUtil.TABLE_HA_SLAVE + 
				" B WHERE A." + COLUMN_HA_CS_ID_SLAVE + "=B." + SlaveServerUtil.COLUMN_HA_S_ID_SLAVE + " AND B." + SlaveServerUtil.COLUMN_HA_S_ID_SLAVE + " IN (" + slave_ids + ")";
		if(cluster_id != null && !"".equals(cluster_id)){
			sql = sql + "AND A." + COLUMN_HA_CS_ID_CLUSTER + "!='" + cluster_id + "'";
		}
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		try {
			conn = ConnectionPool.getConnection();
			stmt = conn.createStatement();
			
			rs = stmt.executeQuery(sql);
			
			while(rs.next()){
				if("".equals(slaves_in_use)){
					slaves_in_use = rs.getString(SlaveServerUtil.COLUMN_HA_S_NAME);
				}else {
					slaves_in_use = slaves_in_use + "," + rs.getString(SlaveServerUtil.COLUMN_HA_S_NAME);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage(),e);
		}finally{
			ConnectionPool.freeConn(rs, stmt, null, conn);
		}
		
		return slaves_in_use;
	}
	
	/**
	 * delete cluster
	 * @param sel_ids
	 */
	public static void deleteCluster(String sel_ids){
		String sql = "DELETE FROM " + TABLE_HA_CLUSTER + " WHERE " + COLUMN_HA_C_ID_CLUSTER + " IN (" + sel_ids + ")";
		Connection conn = null;
		Statement stmt = null;
		
		try {
			conn = ConnectionPool.getConnection();
			stmt = conn.createStatement();
			
			stmt.executeUpdate(sql);
			
			deleteSlaves(conn, sel_ids);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage(),e);
		}finally{
			ConnectionPool.freeConn(null, stmt, null, conn);
		}
	}
	
	/**
	 * get slaves id and name
	 * @return
	 */
	public static String[] getSlavesNotUsed(String cluster_id){
		String[] slaves_not_used = new String[]{"", ""};
		String sql = "SELECT " + SlaveServerUtil.COLUMN_HA_S_ID_SLAVE + "," + SlaveServerUtil.COLUMN_HA_S_NAME + 
						" FROM " + SlaveServerUtil.TABLE_HA_SLAVE + 
						" WHERE " + SlaveServerUtil.COLUMN_HA_S_ID_SLAVE + " NOT IN (" + 
							"SELECT " +	COLUMN_HA_CS_ID_SLAVE + 
							" FROM " + TABLE_HA_CLUSTER_SLAVE + 
							" WHERE " + COLUMN_HA_CS_ID_CLUSTER + "!='" + cluster_id + 
						"')";
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			conn = ConnectionPool.getConnection();
			stmt = conn.createStatement();
			
			rs = stmt.executeQuery(sql);
			
			while(rs.next()){
				if("".equals(slaves_not_used[0])){
					slaves_not_used[0] = String.valueOf(rs.getInt("ID_SLAVE"));
				}else {
					slaves_not_used[0] = slaves_not_used[0] + "," + rs.getInt("ID_SLAVE");
				}
				
				if("".equals(slaves_not_used[1])){
					slaves_not_used[1] = rs.getString("NAME");
				}else {
					slaves_not_used[1] = slaves_not_used[1] + "," + rs.getString("NAME");
				}
			}
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}finally{
			ConnectionPool.freeConn(rs, stmt, null, conn);
		}
		
		return slaves_not_used;
	}
	
	/**
	 * get slave names by cluster id
	 * @param cluster_id
	 * @return
	 */
	private static String[] getSlaves(Connection conn, int cluster_id){
		Statement stmt = null;
		ResultSet rs = null;
		String[] slaves = new String[]{"", ""};
		
		try{
			String sql = "SELECT A." + COLUMN_HA_CS_ID_SLAVE + ",B." + SlaveServerUtil.COLUMN_HA_S_NAME + 
							" FROM " + TABLE_HA_CLUSTER_SLAVE + " A," + SlaveServerUtil.TABLE_HA_SLAVE + 
							" B WHERE A." + COLUMN_HA_CS_ID_SLAVE + "=B." + SlaveServerUtil.COLUMN_HA_S_ID_SLAVE + 
							" AND A." + COLUMN_HA_CS_ID_CLUSTER + "=" + cluster_id;
			
			stmt = conn.createStatement();
			
			rs = stmt.executeQuery(sql);
			
			while(rs.next()){
				if(!"".equals(slaves[0])){
					slaves[0] = slaves[0] + "," + rs.getString(COLUMN_HA_CS_ID_SLAVE);
					slaves[1] = slaves[1] + "," + rs.getString(SlaveServerUtil.COLUMN_HA_S_NAME);
				}else {
					slaves[0] = rs.getString(COLUMN_HA_CS_ID_SLAVE);
					slaves[1] = rs.getString(SlaveServerUtil.COLUMN_HA_S_NAME);
				}
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			ConnectionPool.freeConn(rs, stmt, null, null);
		}
		
		return slaves;
	}
	
	/**
	 * save data to kdi_t_ha_cluster_slave
	 * @param cluster_id
	 * @param slave_ids
	 * @return
	 */
	private static void addSlaves(Connection conn, int cluster_id, String slave_ids){
		PreparedStatement pstmt = null;
		
		try{
			String sql = "INSERT INTO " + TABLE_HA_CLUSTER_SLAVE + " VALUES(?,?,?)";
			
			pstmt = conn.prepareStatement(sql);
			
			String[] slave_id = slave_ids.split(",");
			for(int i=0;i<slave_id.length;i++){
				String id_cluster_slave = StringUtil.createNumberString(8);
				
				pstmt.setInt(1, Integer.parseInt(id_cluster_slave));
				pstmt.setInt(2, cluster_id);
				pstmt.setInt(3, Integer.parseInt(slave_id[i]));
				
				pstmt.addBatch();
			}
			
			pstmt.executeBatch();
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			ConnectionPool.freeConn(null, null, pstmt, null);
		}
	}
	
	/**
	 * save data to kdi_t_ha_cluster_slave
	 * @param cluster_id
	 * @param slave_ids
	 * @return
	 */
	private static void deleteSlaves(Connection conn, String cluster_ids){
		Statement stmt = null;
		
		try{
			String sql = "DELETE FROM " + TABLE_HA_CLUSTER_SLAVE + " WHERE " + COLUMN_HA_CS_ID_CLUSTER + " IN (" + cluster_ids + ")";
			
			stmt = conn.createStatement();
			
			stmt.execute(sql);
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			ConnectionPool.freeConn(null, stmt, null, null);
		}
	}
}
