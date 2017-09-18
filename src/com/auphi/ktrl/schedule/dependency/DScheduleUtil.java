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
package com.auphi.ktrl.schedule.dependency;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;

import com.alibaba.fastjson.JSON;
import com.auphi.ktrl.conn.util.ConnectionPool;
import com.auphi.ktrl.conn.util.DataBaseUtil;
import com.auphi.ktrl.schedule.bean.ScheduleBean;
import com.auphi.ktrl.schedule.util.QuartzExecute;
import com.auphi.ktrl.schedule.util.QuartzUtil;
import com.auphi.ktrl.schedule.util.ScheduleUtil;
import com.auphi.ktrl.schedule.view.FastConfigView;
import com.auphi.ktrl.system.user.bean.UserBean;
import com.auphi.ktrl.system.user.util.UserUtil;
import com.auphi.ktrl.util.Constants;
import com.auphi.ktrl.util.PageInfo;
import com.auphi.ktrl.util.PageList;
import com.auphi.ktrl.util.StringUtil;

public class DScheduleUtil 
{
	private static Logger logger = Logger.getLogger(DScheduleUtil.class);
	private static Scheduler sched = QuartzUtil.sched ;
	private static final int PAGE_SIZE = Integer.parseInt(Constants.get("PageSize", "15"));
	
	public static final int MODE_ONCE = 1;
	public static final int MODE_SECOND = 2;
	public static final int MODE_MINUTE = 3;
	public static final int MODE_HOUR = 4;
	public static final int MODE_DAY = 5;
	public static final int MODE_WEEK = 6;
	public static final int MODE_MONTH = 7;
	public static final int MODE_YEAR = 8;
	
	/**
	 * get parameters from request 
	 * @param request
	 */
	public static ScheduleBean createScheduleBeanFromRequest(HttpServletRequest request, UserBean userBean){
		ScheduleBean scheduleBean = new ScheduleBean();
		
		try {
			scheduleBean.setJobName(request.getParameter("jobname")==null?"":new String(request.getParameter("jobname").getBytes("ISO8859-1"), "UTF-8"));
			scheduleBean.setJobGroup(String.valueOf(userBean.getOrgId()));
			scheduleBean.setTriggerName(request.getParameter("jobname")==null?"":new String((request.getParameter("jobname") + "_trigger").getBytes("ISO8859-1"), "UTF-8"));
			scheduleBean.setTriggerGroup(String.valueOf(userBean.getOrgId()));
			scheduleBean.setDescription(request.getParameter("description")==null?"":new String(request.getParameter("description").getBytes("ISO8859-1"), "UTF-8"));
			scheduleBean.setActionRef(request.getParameter("file")==null?"":new String(request.getParameter("file").getBytes("ISO8859-1"), "UTF-8"));
			scheduleBean.setActionPath(request.getParameter("filepath")==null?"":new String(request.getParameter("filepath").getBytes("ISO8859-1"), "UTF-8"));
			scheduleBean.setRepName(request.getParameter("repository")==null?"":new String(request.getParameter("repository").getBytes("ISO8859-1"), "UTF-8"));
			scheduleBean.setRemoteServer(request.getParameter("remoteServer")==null?"":new String(request.getParameter("remoteServer").getBytes("ISO8859-1"), "UTF-8"));
			scheduleBean.setHa(request.getParameter("ha")==null?"":new String(request.getParameter("ha").getBytes("ISO8859-1"), "UTF-8"));
			scheduleBean.setErrorNoticeUserName(request.getParameter("errorNoticeUserName")==null?"":new String(request.getParameter("errorNoticeUserName").getBytes("ISO8859-1"), "UTF-8"));
			scheduleBean.setErrorNoticeUserId(request.getParameter("errorNoticeUserId")==null?"":new String(request.getParameter("errorNoticeUserId").getBytes("ISO8859-1"), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage(),e);
		}
		//scheduleBean.setTriggerState(request.getParameter("triggerstate")==null?0:Integer.parseInt(request.getParameter("triggerstate")));
		scheduleBean.setExecType(request.getParameter("execType")==null?"":request.getParameter("execType"));
		scheduleBean.setVersion(request.getParameter("version")==null?"":request.getParameter("version"));
		
		String startTime = request.getParameter("starttime")==null?"":request.getParameter("starttime");
		int cycle = Integer.parseInt(request.getParameter("cycle")==null?"1":request.getParameter("cycle"));
		String startDate = request.getParameter("startdate")==null?"":request.getParameter("startdate");
		String haveEndDate = request.getParameter("haveenddate")==null?"":request.getParameter("haveenddate");
		String endDate = request.getParameter("enddate")==null?"":request.getParameter("enddate");
		String cyclenum = request.getParameter("cyclenum")==null?"":request.getParameter("cyclenum");
		
		scheduleBean.setCycle(cycle);
		scheduleBean.setCycleNum(cyclenum);
		scheduleBean.setStartTime(startTime);
		scheduleBean.setStartDate(startDate);
		scheduleBean.setHaveEndDate(haveEndDate);
		scheduleBean.setEndDate(endDate);
		scheduleBean.setNextFireTime(startDate + " " + startTime);
		scheduleBean.setFileType(request.getParameter("filetype")==null?"":request.getParameter("filetype"));
		
		String cronString = "";
		Calendar ca = Calendar.getInstance();
		ca.setTime(StringUtil.StringToDate(startDate + " " + startTime, "yyyy-MM-dd HH:mm:ss"));
		switch (cycle) {
		case MODE_ONCE:
			scheduleBean.setRepeatCount(0);
			long repeatInterval = StringUtil.StringToDate(startDate + " " + startTime, "yyyy-MM-dd HH:mm:ss").getTime() - new Date().getTime();
			if(repeatInterval > 0){
				scheduleBean.setRepeatInterval(repeatInterval);
			}else {
				scheduleBean.setRepeatInterval(-repeatInterval);
			}
			break;
		case MODE_SECOND:
			scheduleBean.setRepeatInterval(Long.parseLong(cyclenum) * 1000);
			scheduleBean.setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);
			if("1".equals(haveEndDate)){
				scheduleBean.setEndDate(endDate);
			}
			
			break;
		case MODE_MINUTE:
			scheduleBean.setRepeatInterval(Long.parseLong(cyclenum) * 60 * 1000);
			scheduleBean.setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);
			if("1".equals(haveEndDate)){
				scheduleBean.setEndDate(endDate);
			}
			
			break;
		case MODE_HOUR:
			scheduleBean.setRepeatInterval(Long.parseLong(cyclenum) * 60 * 60 * 1000);
			scheduleBean.setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);
			if("1".equals(haveEndDate)){
				scheduleBean.setEndDate(endDate);
			}
			
			break;
		case MODE_DAY:
			String daytype = request.getParameter("daytype")==null?"":request.getParameter("daytype");
			scheduleBean.setDayType(daytype);
			if("0".equals(daytype)){
				scheduleBean.setRepeatInterval(Long.parseLong(cyclenum) * 24 * 60 * 60 * 1000);
			}else if("1".equals(daytype)){//work days 
				cronString = ca.get(Calendar.SECOND) + " " + ca.get(Calendar.MINUTE) + 
						" " + ca.get(Calendar.HOUR_OF_DAY) + " ? * MON-FRI";
				scheduleBean.setCronString(cronString);
			}
			scheduleBean.setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);
			if("1".equals(haveEndDate)){
				scheduleBean.setEndDate(endDate);
			}
			
			break;
		case MODE_WEEK:
			scheduleBean.setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);
			
			//create cron string
			cronString = ca.get(Calendar.SECOND) + " " + ca.get(Calendar.MINUTE) + 
					" " + ca.get(Calendar.HOUR_OF_DAY) + " ? * " + cyclenum;
			scheduleBean.setCronString(cronString);
			
			if("1".equals(haveEndDate)){
				scheduleBean.setEndDate(endDate);
			}
			
			break;
		case MODE_MONTH:
			String monthtype = request.getParameter("monthtype")==null?"":request.getParameter("monthtype");
			scheduleBean.setMonthType(monthtype);
			if("0".equals(monthtype)){
				cronString = ca.get(Calendar.SECOND) + " " + ca.get(Calendar.MINUTE) + 
						" " + ca.get(Calendar.HOUR_OF_DAY) + " " + cyclenum + " * ?";
			}else if("1".equals(monthtype)){
				String weeknum = request.getParameter("weeknum")==null?"":request.getParameter("weeknum");
				String daynum = request.getParameter("daynum")==null?"":request.getParameter("daynum");
				scheduleBean.setWeekNum(weeknum);
				scheduleBean.setDayNum(daynum);
				
				if(!"L".equals(weeknum)){
					weeknum = "#" + weeknum;
				}
				
				cronString = ca.get(Calendar.SECOND) + " " + ca.get(Calendar.MINUTE) + 
						" " + ca.get(Calendar.HOUR_OF_DAY) + " ? * " + daynum + weeknum;
			}
			scheduleBean.setCronString(cronString);
			scheduleBean.setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);
			if("1".equals(haveEndDate)){
				scheduleBean.setEndDate(endDate);
			}
			
			break;
		case MODE_YEAR:
			String yeartype = request.getParameter("yeartype")==null?"":request.getParameter("yeartype");
			scheduleBean.setYearType(yeartype);
			if("0".equals(yeartype)){//month and day
				String[] monthAndDay = cyclenum.split("-"); 
				
				cronString = ca.get(Calendar.SECOND) + " " + ca.get(Calendar.MINUTE) + 
						" " + ca.get(Calendar.HOUR_OF_DAY) + " " + monthAndDay[1] + " " + monthAndDay[0] + " ?";
			}else if("1".equals(yeartype)){//month week and day
				String monthnum = request.getParameter("monthnum")==null?"":request.getParameter("monthnum");
				String weeknum = request.getParameter("weeknum")==null?"":request.getParameter("weeknum");
				String daynum = request.getParameter("daynum")==null?"":request.getParameter("daynum");
				scheduleBean.setMonthNum(monthnum);
				scheduleBean.setWeekNum(weeknum);
				scheduleBean.setDayNum(daynum);
				
				if(!"L".equals(weeknum)){
					weeknum = "#" + weeknum;
				}
				
				cronString = ca.get(Calendar.SECOND) + " " + ca.get(Calendar.MINUTE) + 
						" " + ca.get(Calendar.HOUR_OF_DAY) + " ? " + monthnum + " " + daynum + weeknum;
			}
			scheduleBean.setCronString(cronString);
			scheduleBean.setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);
			if("1".equals(haveEndDate)){
				scheduleBean.setEndDate(endDate);
			}
			
			break;
		}
		
		return scheduleBean;
	}
	
	
	public static Map<String,ArrayList<String>> getDependencies(String jobname, String jobgroup){
		Map<String,ArrayList<String>> dependency = new HashMap<String,ArrayList<String>>() ;
		
		Connection conn = null;
		
		if(jobgroup == null)
			jobgroup = Scheduler.DEFAULT_GROUP ;
		
		try{
			String sql = "SELECT *" +
					" FROM KDI_T_JOB_DEPENDENCIES" +
					" WHERE JOB_NAME= ? and JOB_GROUP = ? " +
					" ORDER BY JOB_NAME";
			conn = ConnectionPool.getConnection();
			PreparedStatement ps = conn.prepareCall(sql) ;
			ps.setString(1, jobname) ;
			ps.setString(2, jobgroup) ;
			ResultSet rs = ps.executeQuery() ;
			
			while(rs.next()){
				String jobfullname = rs.getString("JOB_FULLNAME") ;
				String djobfullname = rs.getString("DJOB_FULLNAME") ;
				ArrayList<String> dp = dependency.get(jobfullname) ;
				if(dp == null){
					dp = new ArrayList<String>() ;
					dependency.put(jobfullname, dp) ;
				}
				
				if(djobfullname != null && djobfullname.length() > 0
						&& !dp.contains(djobfullname))
					dp.add(djobfullname) ;
			}
			rs.close() ;
			ps.close() ;
			
			return dependency ;
		}catch(Exception e){
			logger.error(e.getMessage(),e);
			return null ;
		}finally{
			ConnectionPool.freeConn(null, null, null, conn);
		}
		
	}
	
	public static boolean deleteDependency(
			String job_name,String job_group,
			String jobfullname,String djobfullname)
	{
		
		String sql = "DELETE FROM KDI_T_JOB_DEPENDENCIES " +
					"WHERE JOB_NAME = ? and JOB_GROUP = ? and JOB_FULLNAME = ? " +
					"and DJOB_FULLNAME = ?" ;
		Connection connection = null ;
		PreparedStatement smt = null;
	
		try {
			connection = ConnectionPool.getConnection();
			smt = connection.prepareStatement(sql);
		
			smt.setString(1, job_name);
			smt.setString(2, job_group) ;
			smt.setString(3, jobfullname) ;
			smt.setString(4, djobfullname) ;
			smt.executeUpdate() ;
			smt.close() ;
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false ;
		} finally {
			ConnectionPool.freeConn(null, smt, null, connection);
		}
		return true ;
	}
	

	public static boolean deleteDSchedule(String jobName,String jobgroup){
		Connection conn = null ;
		PreparedStatement smt = null;
	
		try 
		{
			
			String sql = "DELETE FROM KDI_T_JOB_DEPENDENCIES WHERE JOB_NAME= ? and JOB_GROUP=?";
			conn = ConnectionPool.getConnection();
			boolean autoCommit = conn.getAutoCommit() ;
			conn.setAutoCommit(false) ;
			
			smt = conn.prepareStatement(sql);
			smt.setString(1, jobName);
			smt.setString(2, jobgroup) ;
			smt.executeUpdate();
			smt.close() ;
			
			conn.commit() ;
			conn.setAutoCommit(autoCommit) ;
			return true ;
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error(e.getMessage(),e) ;
		} finally {
			ConnectionPool.freeConn(null, smt, null, conn);
		}
		return false ;
	}
	
	public static String getJobname(String fullname){
		if(fullname == null || fullname.length() == 0)
			return fullname ;
		return fullname.split("\\.",2)[1] ;
	}
	public static String getJobgroup(String fullname){
		if(fullname == null || fullname.length() == 0)
			return fullname ;
		return fullname.split("\\.",2)[0] ;
	}
	
	private static boolean dependency(Map<String,ArrayList<String>> trees, String parent, String child){
		return true ;
	}
	
	
//	public static boolean checkCylicDependency(String jobfullname,ArrayList<String> djobfullnames)
//	{
//		
//		Connection conn = null;
//		Map<String,ArrayList<String>> dependency = new HashMap<String,ArrayList<String>>() ;
//		try{
//			String sql = "SELECT *" +
//					" FROM KDI_T_JOB_DEPENDENCIES" +
//					" ORDER BY JOB_NAME";
//			conn = ConnectionPool.getConnection();
//			PreparedStatement ps = conn.prepareCall(sql) ;
//			ResultSet rs = ps.executeQuery() ;
//			
//			while(rs.next()){
//				String job_fullname = rs.getString("JOB_FULLNAME") ;
//				String djob_fullname = rs.getString("DJOB_FULLNAME") ;
//				
//				if(!dependency(dependency,job_fullname,djob_fullname)){
//					throw new Exception("") ;
//				} ;
//				
//				ArrayList<String> dp = dependency.get(jobfullname) ;
//				if(dp == null){
//					dp = new ArrayList<String>() ;
//					dependency.put(jobfullname, dp) ;
//				}
//				
//				if(djob_fullname != null && djob_fullname.length() > 0
//						&& !dp.contains(djob_fullname))
//					dp.add(djob_fullname) ;
//				
//			}
//			rs.close() ;
//			ps.close() ;
//			
//			return true ;
//		}catch(Exception e){
//			logger.error(e.getMessage(),e);
//			return false ;
//		}finally{
//			ConnectionPool.freeConn(null, null, null, conn);
//		}
//	}
	
	public static boolean addDependency(
			String job_name,String job_group,
			String jobfullname,
			String[] djobfullnames)
	{
		
		String sql = "INSERT INTO KDI_T_JOB_DEPENDENCIES (JOB_NAME,JOB_GROUP,JOB_FULLNAME,DJOB_FULLNAME)" +
				" values(?,?,?,?)" ;
		Connection connection = null ;
		PreparedStatement smt = null;

		try 
		{
			connection = ConnectionPool.getConnection();
			smt = connection.prepareStatement(sql);
			  
			if(!jobExists(jobfullname.split("\\.",2)[1],jobfullname.split("\\.",2)[0]))
				throw new Exception("Not exists "+jobfullname) ;
			
			for(int i = 0 ; i < djobfullnames.length ; i++)
			{				

				if(djobfullnames[i].length() > 0)
					if(!jobExists(djobfullnames[i].split("\\.",2)[1],djobfullnames[i].split("\\.",2)[0]))
						throw new Exception("Not exists "+djobfullnames[i]) ;
				
				smt.setString(1, job_name);
				smt.setString(2, job_group) ;
				smt.setString(3, jobfullname) ;
				smt.setString(4, djobfullnames[i]) ;
				smt.addBatch() ;
			}
			smt.executeBatch() ;
			smt.close() ;
			return true ;
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(),e) ;
			
		} finally {
			ConnectionPool.freeConn(null, smt, null, connection);
		}
		return false ;
	}


	public static ScheduleBean getScheduleBeanByJobName(String jobName,
			String jobgroup) {
		return ScheduleUtil.getScheduleBeanByJobName(jobName, jobgroup) ;
	}
	
	public static boolean jobExists(String jobname,String jobgroup){
		return QuartzUtil.findByJobName(jobname, jobgroup) != null ;
	}
	
	/**
	 * 是否事件依赖
	 * 
	 * **/
	public static boolean isDSchedule(String jobName,String jobgroup){
		JobDetail jd = QuartzUtil.findByJobName(jobName, jobgroup) ;
		return (jd != null && jd.getJobClass() == DSchedule.class) ;
	}
	
	
	/**
	 * 获取事件调度直接依赖的所有事件调度
	 * 
	 * */
	public static ArrayList<String> getDependentEventSchedules(String jobname, String jobgroup){
		List<String> eventSchedules = getAllEventSchedules() ;
		
		String querySQL = " SELECT * FROM KDI_T_JOB_DEPENDENCIES WHERE JOB_NAME = '"+jobname+"'" +
						" AND JOB_GROUP = '"+jobgroup + "'"  ;
		Connection conn = null ;
		try{
			conn = ConnectionPool.getConnection() ;
			Statement stt = conn.createStatement() ;
			ResultSet rs = stt.executeQuery(querySQL) ;
			ArrayList<String> fullnames = new ArrayList<String>() ;
			while(rs.next()){
				String jobfullname = rs.getString("JOB_FULLNAME") ;
				String djobfullname = rs.getString("DJOB_FULLNAME") ;
				if(eventSchedules.contains(jobfullname) && !fullnames.contains(jobfullname))
					fullnames.add(jobfullname) ;
				if(eventSchedules.contains(djobfullname) && !fullnames.contains(djobfullname))
					fullnames.add(djobfullname) ;
			}
			rs.close() ;
			stt.close() ;
			
			return fullnames ;
		}
		catch(Exception e){
			logger.error(e.getMessage(),e) ;
			
		}
		finally{
			ConnectionPool.freeConn(null, null, null, conn) ;
		}
		return null ;
			
	}
	
	/**
	 * 获取所有的事件调度
	 * 
	 * */
	public static ArrayList<String> getAllEventSchedules(){
		
		String querySQL = " SELECT * FROM QRTZ_JOB_DETAILS WHERE " ;
		querySQL += " JOB_CLASS_NAME = '"+DSchedule.class.getName() +"'" ;
//		querySQL += " AND JOB_GROUP = '"+jobgroup+"'";
		
		Connection conn = null ;
		try{
			conn = ConnectionPool.getConnection() ;
			Statement stt = conn.createStatement() ;
			ResultSet rs = stt.executeQuery(querySQL) ;
			ArrayList<String> fullnames = new ArrayList<String>() ;
			while(rs.next()){
				String job_name = rs.getString("JOB_NAME") ;
				String job_group = rs.getString("JOB_GROUP") ;
				fullnames.add(job_group+"."+job_name) ;
			}
			rs.close() ;
			stt.close() ;
			
			return fullnames ;
		}
		catch(Exception e){
			logger.error(e.getMessage(),e) ;
			
		}
		finally{
			ConnectionPool.freeConn(null, null, null, conn) ;
		}
		return null ;		
	}
	
	
	/**
	 * 判断新增加的依赖是否导致循环依赖，2种情况:
	 * 	内部的调度之间存在循环依赖；
	 * 	内部的事件调度递归导致循环依赖
	 * 
	 * */
	public static boolean cylicleDependency(String jobname,String jobgroup,
			String jobfullname,String djobfullname, StringBuffer result)
	{
		// 判断事件调度内部的调度之间依赖关系
		Map<String,ArrayList<String>> dtree = getDependencies(jobname,jobgroup) ;
		
		ArrayList<String> ls = dtree.get(jobfullname) ;
		if(ls == null){
			ls = new ArrayList<String>() ;
			dtree.put(jobfullname, ls) ;
		}
		if(djobfullname.length() > 0)
			ls.add(djobfullname) ;
		
		try {
			TSortUtils.tSortFix(dtree) ;
			TSortUtils.tSort(dtree) ;
		} catch (Exception e) {
			logger.error(e.getMessage(),e) ;
			result.append(e.getMessage()) ;
			return true ;
		}
		
		ArrayList<String> newEDS = new ArrayList<String>() ;
		if(isDSchedule(getJobname(jobfullname),getJobgroup(jobfullname)))
			newEDS.add(jobfullname) ;
		if(isDSchedule(getJobname(djobfullname),getJobgroup(djobfullname)))
			newEDS.add(djobfullname) ;
		
//		if(newEDS.size() == 0)
//			return false ;

		// 判断事件调度内部的事件依赖，递归判断依赖关系		
		List<String> queue = new ArrayList<String>() ;
		queue.add(jobgroup+"."+jobname) ;
		
		Map<String,ArrayList<String>> dependencies = new HashMap<String,ArrayList<String>>() ;
		dependencies.put(jobgroup+"."+jobname, newEDS) ;
		while(queue.size() > 0){
			String fullname = queue.get(0);
			jobgroup = getJobgroup(fullname) ;
			jobname = getJobname(fullname) ;
			ArrayList<String> dependentES = getDependentEventSchedules(jobname,jobgroup) ;
			ArrayList<String> oldDependentES = dependencies.get(jobgroup+"."+jobname);
			if(oldDependentES == null)
			{
				dependencies.put(jobgroup+"."+jobname, dependentES) ;
				oldDependentES = dependentES ;
			}
			else
			{
				for(String ES: dependentES)
					if(!oldDependentES.contains(ES))
						oldDependentES.add(ES) ;
			}
			
			for(String schedule: oldDependentES)
			{
//				if(dependencies.get(schedule) != null)
//				{
//					logger.error("Cylicle dependent:"+schedule+","+fullname) ;
//					result.append(schedule+","+fullname) ;
//					return true ;
//				}
				if(dependencies.get(schedule) == null)
					queue.add(schedule) ;
			}
			
			queue.remove(0) ;
			
		}
		
		try {
			TSortUtils.tSortFix(dependencies) ;
			TSortUtils.tSort(dependencies) ;
		} catch (Exception e) {
			logger.error(e.getMessage(),e) ;
			result.append(e.getMessage()) ;
			return true ;
		}
		
		
		return false ;
	}
	
	/**
	 * 返回任务当天执行的状态，
	 * 如果有多个，返回最晚执行的状态;
	 * 如果不存在返回null
	 * 
	 * */
	public static  String getJobStatusToday(String jobname,String jobgroup) throws Exception{
		String sql = " SELECT JOBSTATUS FROM KDI_T_MONITOR WHERE JOBNAME = ?" +
				" AND JOBGROUP = ? AND START_TIME >= ? AND END_TIME <= ?"+
				" ORDER BY END_TIME desc";
		Connection conn = null ;
		String result = null ;
		try{
			conn = ConnectionPool.getConnection() ;
			PreparedStatement ps = conn.prepareStatement(sql) ;
			ps.setString(1, jobname) ;
			ps.setString(2, jobgroup) ;
			Timestamp starttime = new Timestamp(System.currentTimeMillis()) ;
			ps.setTimestamp(4, starttime) ;
			starttime.setHours(0) ;
			starttime.setMinutes(0) ;
			starttime.setSeconds(0) ;
			starttime.setNanos(0) ;
			ps.setTimestamp(3, starttime) ;
			
			ResultSet rs = ps.executeQuery() ;
			if(rs.next())
				result = rs.getString(1) ;
			
			rs.close() ;
			ps.close() ;
			
			return result ;
		}
//		catch(Exception e){
//			logger.error(e.getMessage(),e) ;
//			
//		}
		finally{
			ConnectionPool.freeConn(null, null, null, conn) ;
		}
	}
	
	
	/**
	 * 获取所有的调度的全名：jobgroup.jobname
	 * 
	 * **/
	public static PageList findJobFullnames(int page, String jobgroup, String job_class_name,
			 String order,String orderby,String search_text,String trigger_state)
	{
		String querySQL = "SELECT distinct a.JOB_NAME,a.JOB_GROUP FROM QRTZ_JOB_DETAILS  a left join qrtz_triggers b  "
				+ "on a.job_name =b.job_name WHERE 1=1 " ;
		
		String condition = "" ;
		if(jobgroup != null && jobgroup.trim().length() > 0)
			condition += " AND a.JOB_GROUP = '"+jobgroup+"' " ;
		if(job_class_name != null && job_class_name.trim().length() > 0)
			condition += "AND JOB_CLASS_NAME = '"+job_class_name +"' " ;
		if(!"".equals(search_text)){
			condition+= " AND a.job_name LIKE '%" + search_text + "%' ";
		}
		if(!"".equals(trigger_state)){
			condition+= " AND TRIGGER_STATE='" + trigger_state + "'";
		}
		
		querySQL += condition ;

		String countSQL="SELECT COUNT(*) FROM (" + querySQL + ") A";
		
//		querySQL += " ORDER BY JOB_GROUP " ;
		//TODO: 需要适配其他数据库的limit 
		querySQL = DataBaseUtil.generatePagingSQL(querySQL, page, orderby, order);
//		querySQL += " LIMIT " + (PAGE_SIZE*(page-1))+"," + PAGE_SIZE ;
		
		
		Connection conn = null ;
		int count = 0 ;
		try{
			conn = ConnectionPool.getConnection() ;
			Statement stt = conn.createStatement() ;
			ResultSet rs = stt.executeQuery(countSQL) ;
			if(rs.next())
				count = rs.getInt(1) ;
			rs.close() ;
			rs = stt.executeQuery(querySQL) ;
			
			ArrayList<String> fullnames = new ArrayList<String>() ;
			while(rs.next()){
				String job_name = rs.getString("JOB_NAME") ;
				String job_group = rs.getString("JOB_GROUP") ;
				fullnames.add(job_group+"."+job_name) ;
			}
			rs.close() ;
			stt.close() ;
			PageList pl = new PageList(new PageInfo(page,count),fullnames) ;
			return pl ;
		}
		catch(Exception e){
			logger.error(e.getMessage(),e) ;
			
		}
		finally{
			ConnectionPool.freeConn(null, null, null, conn) ;
		}
		return null ;
	}
	
	/**
	 * 获取调度列表
	 * @return PageList 调度分页列表
	 */
	public static PageList findAll(int page, UserBean userBean,String order,String orderby,String search_text,String trigger_state){
		PageList pageList = new PageList();
		int start = PAGE_SIZE * (page - 1);
		int end = PAGE_SIZE * page;
		
		int count = 0;
		
		List<ScheduleBean> listSchedule = new ArrayList<ScheduleBean>();
		
		try 
		{
			PageList pl = findJobFullnames(page,String.valueOf(userBean.getOrgId()),DSchedule.class.getName(),order,orderby, search_text,trigger_state) ;
			List<String> fullnames = (List<String>)pl.getList() ;
			for(String fullname:fullnames)
			{
				String jobgroup = fullname.split("\\.",2)[0] ;
				String jobname = fullname.split("\\.",2)[1] ;
				JobDetail jobDetail = sched.getJobDetail(jobname, jobgroup);
				JobDataMap data = jobDetail.getJobDataMap();
				
				Trigger[] triggers = sched.getTriggersOfJob(jobname, jobgroup);
				
				ScheduleBean scheduleBean = new ScheduleBean();
				scheduleBean.setActionPath(data.getString("actionPath"));
				scheduleBean.setActionRef(data.getString("actionRef"));
				scheduleBean.setFileType(data.getString("fileType"));
				String fastConfigJson = data.getString("fastConfigJson");
				FastConfigView fastConfigView = JSON.parseObject(fastConfigJson, FastConfigView.class);
				String middlePath=null;
				if(fastConfigView!=null)
				{
				   middlePath = "Template" + fastConfigView.getIdSourceType() + fastConfigView.getIdDestType() + fastConfigView.getLoadType();
				}
                scheduleBean.setMiddlePath(middlePath);
				scheduleBean.setVersion(data.getString("version"));
				scheduleBean.setRepName(data.getString("repName")==null?"Default":data.getString("repName"));
				scheduleBean.setErrorNoticeUserId(data.getString("errorNoticeUserId"));
				String errorNoticeUserName = UserUtil.getErrorNoticeUserName(data.getString("errorNoticeUserId"));
				scheduleBean.setErrorNoticeUserName(errorNoticeUserName);
				
				if(triggers.length>0){
					scheduleBean.setJobGroup(triggers[0].getJobGroup());
					scheduleBean.setJobName(triggers[0].getJobName());
					scheduleBean.setTriggerGroup(triggers[0].getGroup());
					scheduleBean.setTriggerName(triggers[0].getName());
					scheduleBean.setDescription(jobDetail.getDescription());
					scheduleBean.setTriggerState(sched.getTriggerState(triggers[0].getName(), triggers[0].getGroup()));
					scheduleBean.setNextFireTime(StringUtil.DateToString(triggers[0].getNextFireTime(), "yyyy-MM-dd HH:mm:ss"));
					scheduleBean.setPrevFireTime(StringUtil.DateToString(triggers[0].getPreviousFireTime(), "yyyy-MM-dd HH:mm:ss"));
					scheduleBean.setStartDate(StringUtil.DateToString(triggers[0].getStartTime(), "yyyy-MM-dd HH:mm:ss"));
					scheduleBean.setEndDate(StringUtil.DateToString(triggers[0].getEndTime(), "yyyy-MM-dd HH:mm:ss"));
					
					if (triggers[0] instanceof CronTrigger) {
						scheduleBean.setCronString(((CronTrigger) triggers[0]).getCronExpression());
			        } else if (triggers[0] instanceof SimpleTrigger) {
			        	scheduleBean.setRepeatInterval(((SimpleTrigger) triggers[0]).getRepeatInterval());
			        	scheduleBean.setRepeatCount(((SimpleTrigger) triggers[0]).getRepeatCount());
			        } 
				}else {
					scheduleBean.setJobGroup(jobDetail.getGroup());
					scheduleBean.setJobName(jobDetail.getName());
					scheduleBean.setDescription(jobDetail.getDescription());
					scheduleBean.setTriggerState(Trigger.STATE_NONE);
				}
				
				if(String.valueOf(userBean.getOrgId()).equals(data.getString("userId"))){
					scheduleBean.setEdit(true);
				}else {
					scheduleBean.setEdit(false);
				}
				
				if(count++ >= start)
					listSchedule.add(scheduleBean);
				if(count>=end)
					break ;
			}
			
			pageList.setList(listSchedule);
			PageInfo pageInfo = new PageInfo(page, count);
			pageList.setPageInfo(pageInfo);
			
			return pageList;
		} catch (SchedulerException e) {
			logger.error(e.getMessage(),e);
			return null;
		}
	}


	public static Map<String, ArrayList<String>> getSchedules(UserBean userBean) {
		Map<String,ArrayList<String>> schedules = new HashMap<String,ArrayList<String>>() ;
		
		Connection conn = null;
		
		try{
			String sql = "SELECT *" +
					" FROM QRTZ_JOB_DETAILS ";
			if(!userBean.isSuperAdmin()){
				sql = sql + " WHERE JOB_GROUP='" + userBean.getOrgId() +"'";
			}
			sql = sql + " GROUP BY JOB_GROUP,JOB_NAME" ;
			conn = ConnectionPool.getConnection();
			PreparedStatement ps = conn.prepareCall(sql) ;
			ResultSet rs = ps.executeQuery() ;
			
			String key_schedule = "schedule" ;
			String key_dschedule = "dschedule" ;
			
			schedules.put(key_schedule, new ArrayList<String>()) ;
			schedules.put(key_dschedule, new ArrayList<String>()) ;
			
			while(rs.next()){
				String jobname = rs.getString("JOB_NAME") ;
				String jobgroup = rs.getString("JOB_GROUP") ;
				String job_class_name = rs.getString("JOB_CLASS_NAME") ;
				
				if(DSchedule.class.getName().equals(job_class_name))
					schedules.get(key_dschedule).add(jobgroup+"."+jobname) ;
				else if(QuartzExecute.class.getName().equals(job_class_name))
					schedules.get(key_schedule).add(jobgroup+"."+jobname) ;
				
			}
			rs.close() ;
			ps.close() ;
			
			return schedules ;
		}catch(Exception e){
			logger.error(e.getMessage(),e);
			return null ;
		}finally{
			ConnectionPool.freeConn(null, null, null, conn);
		}
	}


	

}
