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
package com.auphi.ktrl.schedule.util;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.SimpleTrigger;

import com.alibaba.fastjson.JSON;
import com.auphi.ktrl.conn.util.ConnectionPool;
import com.auphi.ktrl.engine.KettleEngine;
import com.auphi.ktrl.schedule.bean.ScheduleBean;
import com.auphi.ktrl.schedule.view.FastConfigView;
import com.auphi.ktrl.system.user.bean.UserBean;
import com.auphi.ktrl.system.user.util.UserUtil;
import com.auphi.ktrl.util.StringUtil;

import com.auphi.data.hub.domain.FTP;
import com.auphi.data.hub.domain.Hadoop;

public class ScheduleUtil {
	private static Logger logger = Logger.getLogger(ScheduleUtil.class);
	
	public static final int MODE_ONCE = 1;
	public static final int MODE_SECOND = 2;
	public static final int MODE_MINUTE = 3;
	public static final int MODE_HOUR = 4;
	public static final int MODE_DAY = 5;
	public static final int MODE_WEEK = 6;
	public static final int MODE_MONTH = 7;
	public static final int MODE_YEAR = 8;
	
	/**
	 * get parameters from request and set to monitorBean
	 * @param request
	 * @return monitorBean
	 */
	public static ScheduleBean createScheduleBeanFromRequest(HttpServletRequest request, UserBean userBean){
		ScheduleBean scheduleBean = new ScheduleBean();
		
		try {
            scheduleBean.setUserId(String.valueOf(userBean.getUser_id()));
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
			// TODO Auto-generated catch block
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
		if(StringUtils.isEmpty(haveEndDate)){
			haveEndDate = StringUtils.isEmpty(endDate)? "0" : "1";
		}
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
	
	/**
	 * get scheduleBean from quartz job
	 * @param jobName
	 * @return
	 */
	public static ScheduleBean getScheduleBeanByJobName(String jobName,String jobgroup){
		logger.debug("jobName=" + jobName);
		logger.debug("jobgroup=" + jobgroup);
		ScheduleBean scheduleBean = new ScheduleBean();
		
		JobDetail jobDetail = QuartzUtil.findByJobName(jobName,jobgroup);
		
		if(jobDetail != null){
			JobDataMap data = jobDetail.getJobDataMap();
			
			//scheduleBean = (ScheduleBean)data.get("scheduleBean");
			if(data.getBoolean("isFastConfig"))
			{
				String fastConfigJson = data.getString("fastConfigJson");
				FastConfigView fastConfigView = JSON.parseObject(fastConfigJson, FastConfigView.class);
				String middlePath = "Template" + fastConfigView.getIdSourceType() + fastConfigView.getIdDestType() + fastConfigView.getLoadType();
				scheduleBean.setActionPath("/Template/"+middlePath);
				scheduleBean.setActionRef("job");
				scheduleBean.setFileType("kjb");
				scheduleBean.setRepName("Default");
				scheduleBean.setVersion( KettleEngine.VERSION_4_3);
			}
			else
			{
				scheduleBean.setActionPath(data.getString("actionPath"));
				scheduleBean.setActionRef(data.getString("actionRef"));
				scheduleBean.setCronString(data.getString("cronString"));
				scheduleBean.setCycle(data.get("cycle")==null?0:data.getInt("cycle"));
				scheduleBean.setCycleNum(data.getString("cycleNum"));
				scheduleBean.setDayNum(data.getString("dayNum"));
				scheduleBean.setDayType(data.getString("dayType"));
				scheduleBean.setDescription(data.getString("description"));
				scheduleBean.setEndDate(data.getString("endDate"));
				scheduleBean.setFileType(data.getString("fileType"));
				scheduleBean.setHaveEndDate(data.getString("haveEndDate"));
				scheduleBean.setJobGroup(data.getString("jobGroup")==null?data.getString("joGroup"):data.getString("jobGroup"));
				scheduleBean.setJobName(data.getString("jobName"));
				scheduleBean.setMonthNum(data.getString("monthNum"));
				scheduleBean.setMonthType(data.getString("monthType"));
				scheduleBean.setNextFireTime(data.getString("nextFireTime"));
				scheduleBean.setPrevFireTime(data.getString("prevFireTime"));
				scheduleBean.setRepeatCount(data.get("repeatCount")==null?0:data.getInt("repeatCount"));
				scheduleBean.setRepeatInterval(data.get("repeatInterval")==null?0:data.getLong("repeatInterval"));
				scheduleBean.setRepName(data.getString("repName"));
				scheduleBean.setStartDate(data.getString("startDate"));
				scheduleBean.setStartTime(data.getString("startTime"));
				scheduleBean.setTriggerGroup(data.getString("triggerGroup"));
				scheduleBean.setTriggerName(data.getString("triggerName"));
				scheduleBean.setTriggerState(data.get("triggerState")==null?0:data.getInt("triggerState"));
				scheduleBean.setVersion(data.getString("version"));
				scheduleBean.setWeekNum(data.getString("weekNum"));
				scheduleBean.setYearType(data.getString("yearType"));
				scheduleBean.setUserId(data.getString("userId"));
				scheduleBean.setExecType(data.getString("execType"));
				scheduleBean.setRemoteServer(data.getString("remoteServer"));
				scheduleBean.setHa(data.getString("ha"));
				scheduleBean.setErrorNoticeUserId(data.getString("errorNoticeUserId"));
				String errorNoticeUserName = UserUtil.getErrorNoticeUserName(data.getString("errorNoticeUserId"));
				scheduleBean.setErrorNoticeUserName(errorNoticeUserName);
			}
		}
		
		return scheduleBean;
	}
	
	/**
	 * get fastconfig hadoop configuration
	 * @param idHadoop
	 * @return
	 */
	public static Hadoop getHadoopConfig(int idHadoop){
		Hadoop hadoopConfig = new Hadoop();
		
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			String querySQL = "SELECT * FROM KDI_T_HADOOP WHERE ID=" + idHadoop;
			conn = ConnectionPool.getConnection();
			stmt = conn.createStatement();
			
			rs = stmt.executeQuery(querySQL);
			
			if(rs.next()){
				hadoopConfig.setId(idHadoop);
				hadoopConfig.setServer(rs.getString("server"));
				hadoopConfig.setPort(rs.getInt("port"));
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			ConnectionPool.freeConn(rs, stmt, null, conn);
		}
		
		return hadoopConfig;
	}
	
	/**
	 * get parameters from kdi_t_properties
	 * @return
	 */
	public static Properties getParameter(){
		Properties properties = new Properties();
		
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			String querySQL = "SELECT * FROM KDI_T_PARAMETER";
			conn = ConnectionPool.getConnection();
			stmt = conn.createStatement();
			
			rs = stmt.executeQuery(querySQL);
			
			while(rs.next()){
				properties.put(rs.getString("KEY")==null?"":rs.getString("KEY"), rs.getString("VALUE")==null?"":rs.getString("VALUE"));
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			ConnectionPool.freeConn(rs, stmt, null, conn);
		}
		
		return properties;
	}
	
	/**
	 * get fastconfig hadoop configuration
	 * @param idHadoop
	 * @return
	 */
	public static FTP getFTPConfig(int idFTP){
		FTP ftpConfig = new FTP();
		
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			String querySQL = "SELECT * FROM KDI_T_FTP WHERE ID_FTP=" + idFTP;
			conn = ConnectionPool.getConnection();
			stmt = conn.createStatement();
			
			rs = stmt.executeQuery(querySQL);
			
			if(rs.next()){
				ftpConfig.setId_ftp(idFTP);
				ftpConfig.setHost_name(rs.getString("host_name"));
				ftpConfig.setPort(rs.getInt("port"));
				ftpConfig.setUsername(rs.getString("username"));
				ftpConfig.setPassword(rs.getString("password"));
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			ConnectionPool.freeConn(rs, stmt, null, conn);
		}
		
		return ftpConfig;
	}
}
