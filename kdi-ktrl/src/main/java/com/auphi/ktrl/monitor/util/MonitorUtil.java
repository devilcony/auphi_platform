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
package com.auphi.ktrl.monitor.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.auphi.ktrl.conn.util.ConnectionPool;
import com.auphi.ktrl.conn.util.DataBaseUtil;
import com.auphi.ktrl.i18n.Messages;
import com.auphi.ktrl.monitor.bean.MonitorScheduleBean;
import com.auphi.ktrl.schedule.bean.ScheduleBean;
import com.auphi.ktrl.schedule.template.Template;
import com.auphi.ktrl.system.user.bean.UserBean;
import com.auphi.ktrl.util.PageInfo;
import com.auphi.ktrl.util.PageList;
import com.auphi.ktrl.util.StringUtil;

public class MonitorUtil {
	
	private static Logger logger = Logger.getLogger(MonitorUtil.class);
	
	/**
	 * 监控状态,正在运行
	 */
	public static final String STATUS_RUNNING = Messages.getString("Monitor.Status.Running");
	/**
	 * 监控状态,成功
	 */
	public static final String STATUS_FINISHED = Messages.getString("Monitor.Status.Finished");
	/**
	 * 监控状态,失败
	 */
	public static final String STATUS_ERROR = Messages.getString("Monitor.Status.Error");
	
	/**
	 * 监控状态,异常中止运行
	 */
	public static final String STATUS_STOPPED = Messages.getString("Monitor.Status.Stopped"); 
	
	
	/**
	 * 获取监控列表，每个调度对应一个监控
	 * @param page 页码
	 * @param orderby 排序字段
	 * @param order 升降序
	 * @param search_text 搜索词
	 * @return List<MonitorScheduleBean> 监控记录列表list
	 */
	public static PageList findAll(int page, String orderby, String order, String search_start_date, 
			String search_end_date, String search_text,String jobStatus, int user_id, String jobName, 
			UserBean userBean) {
		PageList pageList = new PageList();
		String sql = "SELECT a.ID,a.JOBNAME,a.JOBGROUP,a.JOBFILE,a.JOBSTATUS,a.START_TIME,a.END_TIME"
				+ ",a.CONTINUED_TIME,a.ID_BATCH,a.ID_LOGCHANNEL,LINES_ERROR"
				+ ", b.NAME as CLUSTER_NAME, c.NAME as SERVER_NAME FROM KDI_T_MONITOR a " 
				+ "LEFT JOIN KDI_T_HA_CLUSTER b ON a.ID_CLUSTER=b.ID_CLUSTER " 
				+ "LEFT JOIN KDI_T_HA_SLAVE c ON a.ID_SLAVE=c.ID_SLAVE WHERE 1=1";
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		if(!userBean.isSuperAdmin()){
			sql = sql + " AND a.JOBGROUP='" + userBean.getOrgId() + "'";
		}
		
		if(!"".equals(search_start_date)){
			if(DataBaseUtil.ORACLE.equals(DataBaseUtil.connConfig.getDbms())){
				sql = sql + " AND START_TIME>=TO_DATE('" + search_start_date + " 00:00:00', 'yyyy-MM-dd HH24:mi:ss')";
			}else {
				sql = sql + " AND START_TIME>='" + search_start_date + " 00:00:00'";
			}
		}
		
		if(!"".equals(search_end_date)){
			if(DataBaseUtil.ORACLE.equals(DataBaseUtil.connConfig.getDbms())){
				sql = sql + " AND END_TIME<=TO_DATE('" + search_end_date + " 23:59:59', 'yyyy-MM-dd HH24:mi:ss')";
			}else {
				sql = sql + " AND END_TIME<='" + search_end_date + " 23:59:59'";
			}
			
		}
		
		if(!"".equals(search_text)){
			sql = sql + " AND (JOBNAME LIKE '%" + search_text + "%' OR JOBFILE LIKE '%" + search_text + "%' OR ERRMSG LIKE '%" + search_text + "%')";
		}
		
		if(!"".equals(jobName)){
			sql = sql + " AND JOBNAME='" + jobName + "'";
		}
		if(!"".equals(jobStatus)){
			sql = sql + " AND jobStatus='" + jobStatus + "'";
		}
		int count = 0;
		
		List<MonitorScheduleBean> listMonitorSchedule = new ArrayList<MonitorScheduleBean>();
		try{
			String sqlData = DataBaseUtil.generatePagingSQL(sql, page, orderby, order);
			String sqlCount = "SELECT COUNT(*) FROM (" + sql + ") A";
			
			conn = ConnectionPool.getConnection();
			stmt = conn.createStatement();
			
			rs = stmt.executeQuery(sqlData);
			
			while(rs.next()){
				MonitorScheduleBean monitorScheduleBean = new MonitorScheduleBean();
				
				monitorScheduleBean.setId(rs.getInt("ID"));
				monitorScheduleBean.setJobName(rs.getString("JOBNAME"));
				monitorScheduleBean.setJobGroup(rs.getString("JOBGROUP"));
				monitorScheduleBean.setJobFile(rs.getString("JOBFILE"));
				monitorScheduleBean.setJobStatus(rs.getString("JOBSTATUS"));
				monitorScheduleBean.setStartTime(StringUtil.DateToString(rs.getTimestamp("START_TIME"), "yyyy-MM-dd HH:mm:ss"));
				monitorScheduleBean.setEndTime(StringUtil.DateToString(rs.getTimestamp("END_TIME"), "yyyy-MM-dd HH:mm:ss"));
				monitorScheduleBean.setContinuedTime(rs.getFloat("CONTINUED_TIME"));
				monitorScheduleBean.setHaName(rs.getString("CLUSTER_NAME")==null?"":rs.getString("CLUSTER_NAME"));
				monitorScheduleBean.setServerName(rs.getString("SERVER_NAME")==null?"":rs.getString("SERVER_NAME"));
				monitorScheduleBean.setId_batch(rs.getInt("ID_BATCH"));
				monitorScheduleBean.setId_logchannel(rs.getString("ID_LOGCHANNEL"));
				monitorScheduleBean.setLines_error(rs.getInt("LINES_ERROR"));
				
				listMonitorSchedule.add(monitorScheduleBean);
			}
			
			ResultSet rs_count = stmt.executeQuery(sqlCount);
			while(rs_count.next()){
				count = rs_count.getInt(1);
			}
			rs_count.close();
			
			pageList.setList(listMonitorSchedule);
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
	 * 获取调度的转换/作业的日志记录信息
	 * @param int 调度名称
	 * @return MonitorDataBean 详细日志信息列表List
	 */
	public static MonitorScheduleBean getMonitorData(String id){
		MonitorScheduleBean monitorScheduleBean = new MonitorScheduleBean();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionPool.getConnection();
			stmt = conn.createStatement();
			
			rs = stmt.executeQuery("SELECT * FROM KDI_T_MONITOR WHERE ID=" + id);
			if(rs.next()){
				monitorScheduleBean.setStartTime(rs.getString("START_TIME"));
				monitorScheduleBean.setJobName(rs.getString("JOBNAME"));
				monitorScheduleBean.setJobGroup(rs.getString("JOBGROUP"));
				monitorScheduleBean.setJobFile(rs.getString("JOBFILE"));
				monitorScheduleBean.setJobStatus(rs.getString("JOBSTATUS"));
				monitorScheduleBean.setId_logchannel(rs.getString("ID_LOGCHANNEL"));
				monitorScheduleBean.setLines_error(rs.getInt("LINES_ERROR"));
				monitorScheduleBean.setLines_input(rs.getInt("LINES_INPUT"));
				monitorScheduleBean.setLines_output(rs.getInt("LINES_OUTPUT"));
				monitorScheduleBean.setLines_updated(rs.getInt("LINES_UPDATED"));
				monitorScheduleBean.setLines_read(rs.getInt("LINES_READ"));
				monitorScheduleBean.setLines_written(rs.getInt("LINES_WRITTEN"));
				monitorScheduleBean.setLines_deleted(rs.getInt("LINES_DELETED"));
				monitorScheduleBean.setLogMsg(rs.getString("LOGMSG")==null?"":rs.getString("LOGMSG"));
			}
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			ConnectionPool.freeConn(rs, stmt, null, conn);
		}
		
		return monitorScheduleBean;
	}
	
	/**
	 * 获取调度中监控到的错误信息
	 * @param id 监控记录的ID
	 * @return String 调度运行过程中出现的错误信息
	 */
	public static String getErrorMessage(String id){
		String errorMessage = "";
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionPool.getConnection();
			stmt = conn.createStatement();
			
			rs = stmt.executeQuery("SELECT ERRMSG FROM KDI_T_MONITOR WHERE ID=" + id);
			if(rs.next()){
				errorMessage = rs.getString("ERRMSG")==null?"":rs.getString("ERRMSG");
			}
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			ConnectionPool.freeConn(rs, stmt, null, conn);
		}
		
		return errorMessage;
	}
	
	/**
	 * 在调度执行之前新增本次的监控
	 * @param scheduleBean 调度Bean
	 * @param nextFireTime 下次启动时间
	 */
	public static void addMonitorBeforeRun(int id, ScheduleBean scheduleBean){
		Connection conn = null;
		PreparedStatement pstmt = null;
		try{
			
			conn = ConnectionPool.getConnection();
			
			String sql = "INSERT INTO KDI_T_MONITOR(ID,JOBNAME,JOBGROUP,JOBFILE,JOBSTATUS,START_TIME,END_TIME,CONTINUED_TIME,LOGMSG,ERRMSG,USERID,ID_CLUSTER,ID_SLAVE) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)"; 
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, id);
			pstmt.setString(2, scheduleBean.getJobName());
			pstmt.setString(3, scheduleBean.getJobGroup());
			String path = "/".equals(scheduleBean.getActionPath())?scheduleBean.getActionPath():scheduleBean.getActionPath() + "/";
			pstmt.setString(4, path + scheduleBean.getActionRef() + "." + scheduleBean.getFileType());
			pstmt.setString(5, STATUS_RUNNING);
			pstmt.setTimestamp(6, new Timestamp(new Date().getTime()));
			pstmt.setTimestamp(7, null);
			pstmt.setInt(8, 0);
			pstmt.setString(9, "");
			pstmt.setString(10, "");
			pstmt.setString(11, scheduleBean.getUserId());
			int id_cluster = 0;
			if(scheduleBean.getHa() != null && !"".equals(scheduleBean.getHa())){
				id_cluster = Integer.parseInt(scheduleBean.getHa());
			}
			pstmt.setInt(12, id_cluster);
			int id_server = 0;
			if(scheduleBean.getRemoteServer() != null && !"".equals(scheduleBean.getRemoteServer())){
				id_server = Integer.parseInt(scheduleBean.getRemoteServer());
			}
			pstmt.setInt(13, id_server);
			
			pstmt.execute();
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			ConnectionPool.freeConn(null, null, pstmt, conn);
		}
	}
	
	/**
	 * 
	 * @param id
	 * @param jobDetailName
	 * @param jobName
	 * @param userId
	 * @param id_cluster
	 * @param id_server
	 */
	public static void addMonitorBeforeRun(int id, String jobDetailName, String middlePath, String userId, String remoteServer, String ha){
		Connection conn = null;
		PreparedStatement pstmt = null;
		try{
			
			conn = ConnectionPool.getConnection();
			
			String sql = "INSERT INTO KDI_T_MONITOR(ID,JOBNAME,JOBGROUP,JOBFILE,JOBSTATUS,START_TIME,END_TIME,CONTINUED_TIME,LOGMSG,ERRMSG,USERID,ID_CLUSTER,ID_SLAVE) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)"; 
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, id);
			pstmt.setString(2, jobDetailName);
			pstmt.setString(3, "FASTCONFIG");
			pstmt.setString(4, Template.TEMPLATE_PATH + "/" + middlePath + "/" + Template.JOB_NAME);
			pstmt.setString(5, STATUS_RUNNING);
			pstmt.setTimestamp(6, new Timestamp(new Date().getTime()));
			pstmt.setTimestamp(7, null);
			pstmt.setInt(8, 0);
			pstmt.setString(9, "");
			pstmt.setString(10, "");
			pstmt.setString(11, userId);
			int id_cluster = 0;
			if(ha != null && !"".equals(ha)){
				id_cluster = Integer.parseInt(ha);
			}
			pstmt.setInt(12, id_cluster);
			int id_server = 0;
			if(remoteServer != null && !"".equals(remoteServer)){
				id_server = Integer.parseInt(remoteServer);
			}
			pstmt.setInt(13, id_server);
			
			pstmt.execute();
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			ConnectionPool.freeConn(null, null, pstmt, conn);
		}
	}
	
	/**
	 * 在调度本地运行时更新logchannel id
	 * @param id
	 * @param id_batch
	 */
	public static void updateMonitorInLocalRun(int id, String id_logchannel){
		String sql = "";
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		try{
			conn = ConnectionPool.getConnection();
			
			sql = "UPDATE KDI_T_MONITOR SET ID_LOGCHANNEL=? WHERE ID=?";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, id_logchannel);
			pstmt.setInt(2, id);
			
			pstmt.executeUpdate();
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			ConnectionPool.freeConn(null, null, pstmt, conn);
		}
	}
	
	/**
	 * 在调度本地运行时更新batchid
	 * @param id
	 * @param id_batch
	 */
	public static void updateMonitorInLocalRun(int id, int id_batch){
		String sql = "";
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		try{
			conn = ConnectionPool.getConnection();
			
			sql = "UPDATE KDI_T_MONITOR SET ID_BATCH=? WHERE ID=?";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setInt(1, id_batch);
			pstmt.setInt(2, id);
			
			pstmt.executeUpdate();
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			ConnectionPool.freeConn(null, null, pstmt, conn);
		}
	}
	
	/**
	 * 在调度文件执行之后更新监控信息
	 * @param id monitor id
	 * @param start 开始时间
	 * @param logMessage 执行日志
	 * @param status 当前状态
	 * @param nrErrors 错误条数
	 * @param nrLinesInput 输入条数
	 * @param nrLinesOutput 输出条数
	 * @param nrLinesUpdated 更新条数
	 * @param nrLinesRead 读取条数
	 * @param nrLinesWritten 写入条数
	 * @param nrLinesDeleted 删除条数
	 */
	public static void updateMonitorAfterRun(int id, Date start, String logMessage, String status, long nrErrors, long nrLinesInput, long nrLinesOutput, long nrLinesUpdated, long nrLinesRead, long nrLinesWritten, long nrLinesDeleted, int id_server, int id_batch){
		String sql = "";
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		try{
			conn = ConnectionPool.getConnection();
			
			sql = "UPDATE KDI_T_MONITOR SET END_TIME=?,JOBSTATUS=?,CONTINUED_TIME=?,LOGMSG=?,LINES_ERROR=?,LINES_INPUT=?,LINES_OUTPUT=?,LINES_UPDATED=?,LINES_READ=?,LINES_WRITTEN=?,LINES_DELETED=?,ID_SLAVE=?,ID_BATCH=? WHERE ID=?";
			pstmt = conn.prepareStatement(sql);
			
			Date end = new Date();
			pstmt.setTimestamp(1, new Timestamp(end.getTime()));
			pstmt.setString(2, status);
			float continuedTime = ((float)(end.getTime() - start.getTime()))/1000;
			pstmt.setFloat(3, continuedTime);
			pstmt.setString(4, logMessage);
			pstmt.setInt(5, (int)nrErrors);
			pstmt.setInt(6, (int)nrLinesInput);
			pstmt.setInt(7, (int)nrLinesOutput);
			pstmt.setInt(8, (int)nrLinesUpdated);
			pstmt.setInt(9, (int)nrLinesRead);
			pstmt.setInt(10, (int)nrLinesWritten);
			pstmt.setInt(11, (int)nrLinesDeleted);
			pstmt.setInt(12, id_server);
			pstmt.setInt(13, id_batch);
			pstmt.setInt(14, id);
			
			pstmt.executeUpdate();
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			ConnectionPool.freeConn(null, null, pstmt, conn);
		}
	}
	
	/**
	 * 更新监控的错误信息
	 * @param errMsg 错误信息
	 * @param jobName 调度名称
	 */
	public static void updateMonitorAfterError(int id, String errMsg){
		String sql = "UPDATE KDI_T_MONITOR SET ERRMSG=? ,JOBSTATUS=? WHERE ID=?";
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		try{
			if(errMsg.length() >= 3950){
				errMsg = errMsg.substring(0, 3949) + "<br>......";
			}
			
			conn = ConnectionPool.getConnection();
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, StringUtil.DateToString(new Date(), "yyyy-MM-dd HH:mm:ss") + ":<br>" + errMsg);
			pstmt.setString(2, STATUS_ERROR);
			pstmt.setInt(3, id);
			
			pstmt.executeUpdate();
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			ConnectionPool.freeConn(null, null, pstmt, conn);
		}
	}
	
	/**
	 * 删除监控信息
	 * @param ids 监控id
	 */
	public static void deleteMonitor(String ids){
		String sql = "DELETE FROM KDI_T_MONITOR WHERE ID IN (" + ids + ")";
		Connection conn = null;
		Statement stmt = null;
		
		try {
			conn = ConnectionPool.getConnection();
			stmt = conn.createStatement();
			
			stmt.executeUpdate(sql);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage(),e);
		}finally{
			ConnectionPool.freeConn(null, stmt, null, conn);
		}
	}
	
	/**
	 * 删除监控信息
	 * @param ids 监控id
	 */
	public static void clearMonitor(String start_date){
		String sql = "DELETE FROM KDI_T_MONITOR WHERE START_TIME<='" + start_date + " 23:59:59'";
		Connection conn = null;
		Statement stmt = null;
		
		try {
			conn = ConnectionPool.getConnection();
			stmt = conn.createStatement();
			
			stmt.executeUpdate(sql);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage(),e);
		}finally{
			ConnectionPool.freeConn(null, stmt, null, conn);
		}
	}
	
	/**
	 * 在启动时，将正在运行的状态改为中止运行
	 */
	public static void updateRunningToStoppedInStartUp(){
		String sql = "UPDATE KDI_T_MONITOR SET JOBSTATUS='" + STATUS_STOPPED + "' WHERE JOBSTATUS='" + STATUS_RUNNING + "'";
		Connection conn = null;
		Statement stmt = null;
		
		try {
			conn = ConnectionPool.getConnection();
			stmt = conn.createStatement();
			
			stmt.executeUpdate(sql);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage(),e);
		}finally{
			ConnectionPool.freeConn(null, stmt, null, conn);
		}
	}
}
