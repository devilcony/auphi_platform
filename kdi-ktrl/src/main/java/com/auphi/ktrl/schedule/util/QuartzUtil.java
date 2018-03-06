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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;

import javax.servlet.ServletException;

import org.apache.log4j.Logger;
import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

import com.alibaba.fastjson.JSON;
import com.auphi.ktrl.conn.util.ConnectionPool;
import com.auphi.ktrl.conn.util.DataBaseUtil;
import com.auphi.ktrl.schedule.bean.ScheduleBean;
import com.auphi.ktrl.schedule.dependency.DScheduleUtil;
import com.auphi.ktrl.schedule.view.DispatchingModeView;
import com.auphi.ktrl.schedule.view.FastConfigView;
import com.auphi.ktrl.schedule.view.FieldMappingView;
import com.auphi.ktrl.schedule.view.QrtzJobDetailsView;
import com.auphi.ktrl.system.user.bean.UserBean;
import com.auphi.ktrl.system.user.util.UserUtil;
import com.auphi.ktrl.util.Constants;
import com.auphi.ktrl.util.PageInfo;
import com.auphi.ktrl.util.PageList;
import com.auphi.ktrl.util.StringUtil;

public class QuartzUtil {
	/**
	 * 全局调度器scheduler
	 */
	public static Scheduler sched = null;
	
	/**
	 * 日志logger
	 */
	private static Logger logger = Logger.getLogger(QuartzUtil.class);
	
	/**
	 * 每页条数
	 */
	private static final int PAGE_SIZE = Integer.parseInt(Constants.get("PageSize", "15"));
	
	public static final int MODE_HOUR = 1;
	public static final int MODE_DAY = 2;
	public static final int MODE_MONTH = 3;
	
	/**
	 * 初始化并启动全局调度器
	 * @throws ServletException
	 */
	public static void init() throws ServletException {
		try {
			//get quartz props
			Properties quartzProps = new Properties();
			quartzProps.load(Constants.class.getResourceAsStream("/quartz.properties"));
			
			String dataSource = quartzProps.get("org.quartz.jobStore.dataSource")==null?"":quartzProps.get("org.quartz.jobStore.dataSource").toString();
			String urlPropName = "org.quartz.dataSource." + dataSource + ".URL";
			quartzProps.setProperty(urlPropName, DataBaseUtil.connConfig.getUrl());
			
			SchedulerFactory schedFact = new StdSchedulerFactory(quartzProps);
			sched = schedFact.getScheduler();
			sched.start();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	
	
	/**
	 * 获取普通调度
	 * 
	 * **/
	public static PageList findAllSchedule(int page, UserBean userBean ,String sort,String sortby
			,String search_text,String trigger_state){
//		PageList pageList = new PageList();
//		int start = PAGE_SIZE * (page - 1);
//		int end = PAGE_SIZE * page;
		
//		int count = 0;
		
		List<ScheduleBean> listSchedule = new ArrayList<ScheduleBean>();
		
		try 
		{
			PageList pl = DScheduleUtil.findJobFullnames(page,String.valueOf(userBean.getOrgId()),QuartzExecute.class.getName()
					,sort,sortby,search_text,trigger_state) ;
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
				
				if(String.valueOf(userBean.getUser_id()).equals(data.getString("userId"))){
					scheduleBean.setEdit(true);
				}else {
					scheduleBean.setEdit(false);
				}
				
//				if(count++ >= start)
				listSchedule.add(scheduleBean);
//				if(count>=end)
//					break ;
			}
			
			pl.setList(listSchedule) ;
			return pl ;
//			pageList.setList(listSchedule);
//			PageInfo pageInfo = new PageInfo(page, count);
//			pageList.setPageInfo(pageInfo);
//			
//			return pageList;
		} catch (SchedulerException e) {
			logger.error(e.getMessage(),e);
			return null;
		}		
	}
	
//	/**
//	 * 获取调度列表
//	 * @return PageList 调度分页列表
//	 */
//	public static PageList findAll(int page, String user_id,String jobgroup){
//		PageList pageList = new PageList();
//		int start = PAGE_SIZE * (page - 1);
//		int end = PAGE_SIZE * page;
//		
//		int count = 0;
//		if(jobgroup == null)
//			jobgroup = Scheduler.DEFAULT_GROUP ;
//		List<ScheduleBean> listSchedule = new ArrayList<ScheduleBean>();
//		try {
//			String[] jobNames = sched.getJobNames(jobgroup);
//			
//			count = jobNames.length;
//			end = jobNames.length > end ? end : jobNames.length;
//			
//			for(int i=start;i<end;i++){
//				JobDetail jobDetail = sched.getJobDetail(jobNames[i], jobgroup);
//				JobDataMap data = jobDetail.getJobDataMap();
//				
//				Trigger[] triggers = sched.getTriggersOfJob(jobNames[i], jobgroup);
//				
//				ScheduleBean scheduleBean = new ScheduleBean();
//				scheduleBean.setActionPath(data.getString("actionPath"));
//				scheduleBean.setActionRef(data.getString("actionRef"));
//				scheduleBean.setFileType(data.getString("fileType"));
//				String fastConfigJson = data.getString("fastConfigJson");
//				FastConfigView fastConfigView = JSON.parseObject(fastConfigJson, FastConfigView.class);
//				String middlePath=null;
//				if(fastConfigView!=null)
//				{
//				   middlePath = "Template" + fastConfigView.getIdSourceType() + fastConfigView.getIdDestType() + fastConfigView.getLoadType();
//					
//				}
//                scheduleBean.setMiddlePath(middlePath);
//				scheduleBean.setVersion(data.getString("version"));
//				scheduleBean.setRepName(data.getString("repName")==null?"Default":data.getString("repName"));
//				scheduleBean.setErrorNoticeUserId(data.getString("errorNoticeUserId"));
//				String errorNoticeUserName = UserUtil.getErrorNoticeUserName(data.getString("errorNoticeUserId"));
//				scheduleBean.setErrorNoticeUserName(errorNoticeUserName);
//				
//				if(triggers.length>0){
//					scheduleBean.setJobGroup(triggers[0].getJobGroup());
//					scheduleBean.setJobName(triggers[0].getJobName());
//					scheduleBean.setTriggerGroup(triggers[0].getGroup());
//					scheduleBean.setTriggerName(triggers[0].getName());
//					scheduleBean.setDescription(jobDetail.getDescription());
//					scheduleBean.setTriggerState(sched.getTriggerState(triggers[0].getName(), triggers[0].getGroup()));
//					scheduleBean.setNextFireTime(StringUtil.DateToString(triggers[0].getNextFireTime(), "yyyy-MM-dd HH:mm:ss"));
//					scheduleBean.setPrevFireTime(StringUtil.DateToString(triggers[0].getPreviousFireTime(), "yyyy-MM-dd HH:mm:ss"));
//					scheduleBean.setStartDate(StringUtil.DateToString(triggers[0].getStartTime(), "yyyy-MM-dd HH:mm:ss"));
//					scheduleBean.setEndDate(StringUtil.DateToString(triggers[0].getEndTime(), "yyyy-MM-dd HH:mm:ss"));
//					
//					if (triggers[0] instanceof CronTrigger) {
//						scheduleBean.setCronString(((CronTrigger) triggers[0]).getCronExpression());
//			        } else if (triggers[0] instanceof SimpleTrigger) {
//			        	scheduleBean.setRepeatInterval(((SimpleTrigger) triggers[0]).getRepeatInterval());
//			        	scheduleBean.setRepeatCount(((SimpleTrigger) triggers[0]).getRepeatCount());
//			        } 
//				}else {
//					scheduleBean.setJobGroup(jobDetail.getGroup());
//					scheduleBean.setJobName(jobDetail.getName());
//					scheduleBean.setDescription(jobDetail.getDescription());
//					scheduleBean.setTriggerState(Trigger.STATE_NONE);
//				}
//				
//				if(user_id.equals(data.getString("userId"))){
//					scheduleBean.setEdit(true);
//				}else {
//					scheduleBean.setEdit(false);
//				}
//				
//				listSchedule.add(scheduleBean);	
//			}	
//			
//			pageList.setList(listSchedule);
//			PageInfo pageInfo = new PageInfo(page, count);
//			pageList.setPageInfo(pageInfo);
//			
//			return pageList;
//		} catch (SchedulerException e) {
//			logger.error(e.getMessage(),e);
//			return null;
//		}
//	}
	
	/**
	 * 根据调度名称获取调度详细信息
	 * @param jobName 调度名称
	 * @return JobDetail 调度详细信息
	 */
	public static JobDetail findByJobName(String jobName, UserBean userBean){
		try {
			return sched.getJobDetail(jobName, String.valueOf(userBean.getOrgId()));
		} catch (SchedulerException e) {
			logger.error(e.getMessage(),e);
			return null;
		}
	}
	
	/**
	 * 根据调度名称获取调度详细信息
	 * @param jobName 调度名称
	 * @return JobDetail 调度详细信息
	 */
	public static JobDetail findByJobName(String jobName, String groupName){
		try {
			return sched.getJobDetail(jobName, groupName);
		} catch (SchedulerException e) {
			logger.error(e.getMessage(),e);
			return null;
		}
	}
	
	public static void create(ScheduleBean scheduleBean){
		create(scheduleBean,null) ;
	}
	
	public static List<String> getJobgroups(){
		try {
			return Arrays.asList(sched.getJobGroupNames())  ;
		} catch (SchedulerException e) {
			e.printStackTrace();
			logger.error(e.getMessage(),e) ;
		}
		return null ;
	}
	
	public static Map<String,List<String>> jobnameTree(){
		Map<String,List<String>> tree = new HashMap<String,List<String>>() ;
		List<String> groups = getJobgroups() ;
		for(String group: groups){
			List<String> jobnames = getJobnames(group);
			tree.put(group, jobnames) ;
		}
		return tree ;
	}
	
	
	public static List<String> getJobnames(String jobgroup){
		
		try {
			return Arrays.asList(sched.getJobNames(jobgroup))  ;
		} catch (SchedulerException e) {
			e.printStackTrace();
			logger.error(e.getMessage(),e) ;
		}
		return null ;
	}
	
	/**
	 * 创建调度
	 * @param scheduleBean 调度Bean
	 */
	public static void create(ScheduleBean scheduleBean,Class<?> jobExecClass){
		Trigger trigger = null;
		if(jobExecClass == null)
			jobExecClass = QuartzExecute.class ;
		JobDetail jobDetail = new JobDetail(scheduleBean.getJobName(), scheduleBean.getJobGroup(), jobExecClass);
		
		jobDetail.setDurability(true);
		
		if (null != scheduleBean.getDescription()) {
			jobDetail.setDescription(scheduleBean.getDescription());
		}
		JobDataMap data = jobDetail.getJobDataMap();

		data.put("isFastConfig", false);
		data.put("actionRef", scheduleBean.getActionRef());
		data.put("actionPath", scheduleBean.getActionPath());
		data.put("repeat-time-millisecs", scheduleBean.getRepeatInterval());
		data.put("joGroup", scheduleBean.getJobGroup());
		data.put("repeat-count", scheduleBean.getRepeatCount());
		data.put("start-date-time", scheduleBean.getStartDate());
		data.put("requestedMimeType", "text/xml");
		data.put("description", scheduleBean.getDescription());
		data.put("jobName", scheduleBean.getJobName());
		data.put("version", scheduleBean.getVersion());
		data.put("fileType", scheduleBean.getFileType());
		data.put("repName", scheduleBean.getRepName());
		data.put("version", scheduleBean.getVersion());
		data.put("startTime", scheduleBean.getStartTime());
		data.put("startDate", scheduleBean.getStartDate());
		data.put("haveEndDate", scheduleBean.getHaveEndDate());
		data.put("endDate", scheduleBean.getEndDate());
		data.put("cycle", scheduleBean.getCycle());
		data.put("cycleNum", scheduleBean.getCycleNum());
		data.put("dayType", scheduleBean.getDayType());
		data.put("monthType", scheduleBean.getMonthType());
		data.put("yearType", scheduleBean.getYearType());
		data.put("dayNum", scheduleBean.getDayNum());
		data.put("weekNum", scheduleBean.getWeekNum());
		data.put("monthNum", scheduleBean.getMonthNum());
		data.put("userId", scheduleBean.getUserId());
		data.put("execType", scheduleBean.getExecType());
		data.put("remoteServer", scheduleBean.getRemoteServer());
		data.put("ha", scheduleBean.getHa());
		data.put("errorNoticeUserId", scheduleBean.getErrorNoticeUserId());
		data.put("errorNoticeUserName", scheduleBean.getErrorNoticeUserName());
		//data.put("scheduleBean", scheduleBean);
		
		data.put("background_action_name", "");
		data.put("processId", QuartzUtil.class.getName()); //$NON-NLS-1$
		data.put("background_user_name", "");
		data.put("background_output_location", "background/" + StringUtil.createNumberString(16)); //$NON-NLS-1$ 
		data.put("background_submit_time", StringUtil.DateToString(new Date(), "yyyy-MM-dd"));

		// This tells our execution component (QuartzExecute) that we're running
		// a background job instead of
		// a standard quartz execution.
		data.put("backgroundExecution", "true"); //$NON-NLS-1$
		
		Date endDate = ("".equals(scheduleBean.getEndDate()))?null:StringUtil.StringToDate(scheduleBean.getEndDate() + " 23:59:59", "yyyy-MM-dd HH:mm:ss");
		
		try {
			if(scheduleBean.getCronString() == null || "".equals(scheduleBean.getCronString())){//sampleTrigger
				trigger = new SimpleTrigger(scheduleBean.getTriggerName(), scheduleBean.getTriggerGroup(), 
						scheduleBean.getJobName(), scheduleBean.getJobGroup(), StringUtil.StringToDate(scheduleBean.getStartDate() + " " + scheduleBean.getStartTime(), "yyyy-MM-dd HH:mm:ss"), 
						endDate, scheduleBean.getRepeatCount(), scheduleBean.getRepeatInterval());
			}else {//cronTrigger
				trigger = new CronTrigger(scheduleBean.getTriggerName(), scheduleBean.getTriggerGroup(),
						scheduleBean.getJobName(), scheduleBean.getJobGroup(), StringUtil.StringToDate(scheduleBean.getStartDate() + " " + scheduleBean.getStartTime(), "yyyy-MM-dd HH:mm:ss"), 
						endDate, scheduleBean.getCronString(), TimeZone.getDefault());
			}
			
			sched.scheduleJob(jobDetail, trigger);
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}
	
	/**
	 * 删除调度 
	 * @param jobNames 调度名称数组 
	 */
	public static void delete(String[] names, UserBean userBean){
		try{
			for(int i=0;i<names.length;i++){
				sched.unscheduleJob(names[i] + "_trigger", String.valueOf(userBean.getOrgId()));
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
	}
	
	/**
	 * 彻底删除调度 
	 * @param jobNames 调度名称数组 
	 */
	public static void completedelete(String[] names, UserBean userBean){
		try{
			for(int i=0;i<names.length;i++){
				sched.deleteJob(names[i], String.valueOf(userBean.getOrgId()));
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
	}
	
	/**
	 * 更新调度
	 * @param scheduleBean 调度Bean
	 */
	public static void update(ScheduleBean scheduleBean, String checked_job, UserBean userBean){
		try{
			logger.debug("jobName=" + checked_job);
			logger.debug("jobGroup=" + userBean.getOrgId());
			JobDetail jobDetail = sched.getJobDetail(checked_job, String.valueOf(userBean.getOrgId()));
			
			jobDetail.setName(scheduleBean.getJobName());
			jobDetail.setDurability(true);
			if (null != scheduleBean.getDescription()) {
				jobDetail.setDescription(scheduleBean.getDescription());
			}
			
			JobDataMap data = jobDetail.getJobDataMap();
			data.put("isFastConfig", false);
			data.put("actionRef", scheduleBean.getActionRef());
			data.put("actionPath", scheduleBean.getActionPath());
			data.put("repeat-time-millisecs", scheduleBean.getRepeatInterval());
			data.put("joGroup", scheduleBean.getJobGroup());
			data.put("repeat-count", scheduleBean.getRepeatCount());
			data.put("start-date-time", scheduleBean.getStartDate());
			data.put("requestedMimeType", "text/xml");
			data.put("description", scheduleBean.getDescription());
			data.put("jobName", scheduleBean.getJobName());
			data.put("version", scheduleBean.getVersion());
			data.put("fileType", scheduleBean.getFileType());
			data.put("repName", scheduleBean.getRepName());
			data.put("version", scheduleBean.getVersion());
			data.put("startTime", scheduleBean.getStartTime());
			data.put("startDate", scheduleBean.getStartDate());
			data.put("haveEndDate", scheduleBean.getHaveEndDate());
			data.put("endDate", scheduleBean.getEndDate());
			data.put("cycle", scheduleBean.getCycle());
			data.put("cycleNum", scheduleBean.getCycleNum());
			data.put("dayType", scheduleBean.getDayType());
			data.put("monthType", scheduleBean.getMonthType());
			data.put("yearType", scheduleBean.getYearType());
			data.put("dayNum", scheduleBean.getDayNum());
			data.put("weekNum", scheduleBean.getWeekNum());
			data.put("monthNum", scheduleBean.getMonthNum());
			data.put("execType", scheduleBean.getExecType());
			data.put("remoteServer", scheduleBean.getRemoteServer());
			data.put("ha", scheduleBean.getHa());
			data.put("errorNoticeUserId", scheduleBean.getErrorNoticeUserId());
			data.put("errorNoticeUserName", scheduleBean.getErrorNoticeUserName());
			
			data.put("background_action_name", "");
			data.put("processId", QuartzUtil.class.getName()); //$NON-NLS-1$
			data.put("background_user_name", "");
			data.put("background_output_location", "background/" + StringUtil.createNumberString(16)); //$NON-NLS-1$ 
			data.put("background_submit_time", StringUtil.DateToString(new Date(), "yyyy-MM-dd"));

			// This tells our execution component (QuartzExecute) that we're running
			// a background job instead of
			// a standard quartz execution.
			data.put("backgroundExecution", "true"); //$NON-NLS-1$
			
			Trigger newTrigger;
			
			Date endDate = ("".equals(scheduleBean.getEndDate()))?null:StringUtil.StringToDate(scheduleBean.getEndDate() + " 23:59:59", "yyyy-MM-dd HH:mm:ss");
			
			if(scheduleBean.getCronString() == null || "".equals(scheduleBean.getCronString())){//sampleTrigger
				newTrigger = new SimpleTrigger(scheduleBean.getTriggerName(), scheduleBean.getTriggerGroup(), 
						scheduleBean.getJobName(), scheduleBean.getJobGroup(), StringUtil.StringToDate(scheduleBean.getStartDate() + " " + scheduleBean.getStartTime(), "yyyy-MM-dd HH:mm:ss"), 
						endDate, scheduleBean.getRepeatCount(), scheduleBean.getRepeatInterval());
			}else {//cronTrigger
				newTrigger = new CronTrigger(scheduleBean.getTriggerName(), scheduleBean.getTriggerGroup(),
						scheduleBean.getJobName(), scheduleBean.getJobGroup(), StringUtil.StringToDate(scheduleBean.getStartDate() + " " + scheduleBean.getStartTime(), "yyyy-MM-dd HH:mm:ss"), 
						endDate, scheduleBean.getCronString(), TimeZone.getDefault());
			}
			sched.deleteJob(checked_job, String.valueOf(userBean.getOrgId()));
			sched.scheduleJob(jobDetail, newTrigger);
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
	}
	
	/**
	 * 执行调度
	 * @param names 调度名称数组
	 */
	public static void execute(String[] names, UserBean userBean){
		System.out.println("============QuartzUtil");
		try{
			for(int i=0;i<names.length;i++){
				sched.triggerJob(names[i], String.valueOf(userBean.getOrgId()));
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
	}
	
	public static void execute(String jobname, String jobgroup){
		try {
			sched.triggerJob(jobname, jobgroup) ;
		} catch (SchedulerException e) {
			logger.error(e.getMessage(),e) ;
		}
	}
	
	/**
	 * 暂停调度
	 * @param names 调度名称数组
	 */
	public static void pause(String[] names, UserBean userBean){
		try{
			for(int i=0;i<names.length;i++){
				sched.pauseJob(names[i], String.valueOf(userBean.getOrgId()));
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
	}
	
	/**
	 * 还原调度
	 * @param names 调度名称数组
	 */
	public static void resume(String[] names, UserBean userBean){
		try{
			for(int i=0;i<names.length;i++){
				sched.resumeJob(names[i], String.valueOf(userBean.getOrgId()));
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
	}
	
	/**
	 * 检查调度是否存在
	 * @param jobName 调度名称
	 * @return boolean 调度是否存在
	 */
	public static boolean checkJobExist(String jobName, UserBean userBean){
		try{
			JobDetail job = sched.getJobDetail(jobName, String.valueOf(userBean.getOrgId()));
			if(job == null){
				return false;
			}else {
				return true;
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
			return false;
		}
	}
	
	public static void saveDispatch(String fastConfigJson,
			String fieldMappingJson, String dispatchingModeJosn,
			String jobName, boolean check, UserBean userBean) {
		DispatchingModeView dispatchingModeView = JSON.parseObject(
				dispatchingModeJosn, DispatchingModeView.class);
		Trigger trigger = null;
		JobDetail jobDetail = new JobDetail(jobName, String.valueOf(userBean.getOrgId()),
				QuartzExecute.class);
		jobDetail.setDurability(true);

		/*
		 * if (null != scheduleBean.getDescription()) {
		 * jobDetail.setDescription(scheduleBean.getDescription());//描述信息不要了 }
		 */
		JobDataMap data = jobDetail.getJobDataMap();
		data.put("isFastConfig", true);
		data.put("fastConfigJson", fastConfigJson);
		data.put("fieldMappingJson", fieldMappingJson);
		data.put("dispatchingModeJosn", dispatchingModeJosn);
		data.put("joGroup", String.valueOf(userBean.getOrgId()));
		data.put("requestedMimeType", "text/xml");

		String cronString = "";
		Integer repeatCount = 0;
		Long repeatInterval = null;
		Calendar ca = Calendar.getInstance();
		ca.setTime(StringUtil.StringToDate(dispatchingModeView.getRunDate()
				+ " " + dispatchingModeView.getBeginTime(),
				"yyyy-MM-dd HH:mm:ss"));

		switch (dispatchingModeView.getScheduletype()) {
		case MODE_HOUR:
			repeatInterval = Long.parseLong(dispatchingModeView.getCycleMode()) * 60 * 60 * 1000;
			repeatCount = SimpleTrigger.REPEAT_INDEFINITELY;
			if (1 == dispatchingModeView.getEndTimeType()) {
				dispatchingModeView.setEndTime("");
			}

			break;
		case MODE_DAY:
			repeatInterval = Long.parseLong(dispatchingModeView.getCycleMode())
					* 24 * 60 * 60 * 1000;
			repeatCount = SimpleTrigger.REPEAT_INDEFINITELY;
			if (1 == dispatchingModeView.getEndTimeType()) {
				dispatchingModeView.setEndTime("");
			}
			break;
		case MODE_MONTH:

			// 如果选择的是L 则为32，将获得此月的最后一天
			// if(dispatchingModeView.getCycleModeMonth().equals("L"))
			// {
			// Calendar calendar = Calendar.getInstance();
			// // 设置时间,当前时间不用设置
			// // calendar.setTime(new Date());
			// // 设置日期为本月最大日期
			// calendar.set(Calendar.DATE,
			// calendar.getActualMaximum(Calendar.DATE));
			// // 打印
			// DateFormat format = new SimpleDateFormat("dd");
			// dispatchingModeView.setCycleModeMonth(format.format(calendar.getTime()));
			// }
			cronString = ca.get(Calendar.SECOND) + " "
					+ ca.get(Calendar.MINUTE) + " "
					+ ca.get(Calendar.HOUR_OF_DAY) + " "
					+ dispatchingModeView.getCycleModeMonth() + " * ?";
			repeatCount = SimpleTrigger.REPEAT_INDEFINITELY;
			if (1 == dispatchingModeView.getEndTimeType()) {
				dispatchingModeView.setEndTime("");
			}
			break;
		default:

			break;
		}

		Date endDate = ("".equals(dispatchingModeView.getEndTime())) ? null
				: StringUtil.StringToDate(dispatchingModeView.getEndTime()
						+ " 23:59:59", "yyyy-MM-dd HH:mm:ss");

		try {
			if (cronString == null || "".equals(cronString)) {// sampleTrigger
				trigger = new SimpleTrigger(jobName, String.valueOf(userBean.getOrgId()),
						jobName, String.valueOf(userBean.getOrgId()),
						StringUtil.StringToDate(
								dispatchingModeView.getRunDate() + " "
										+ dispatchingModeView.getBeginTime(),
								"yyyy-MM-dd HH:mm:ss"), endDate, repeatCount,
						repeatInterval);
			} else {// cronTrigger
				trigger = new CronTrigger(jobName, String.valueOf(userBean.getOrgId()),
						jobName, String.valueOf(userBean.getOrgId()),
						StringUtil.StringToDate(
								dispatchingModeView.getRunDate() + " "
										+ dispatchingModeView.getBeginTime(),
								"yyyy-MM-dd HH:mm:ss"), endDate, cronString,
						TimeZone.getDefault());
			}
			// sched.deleteJob("111", Scheduler.DEFAULT_GROUP);
			if (check) {
				sched.deleteJob(jobName, String.valueOf(userBean.getOrgId()));
			}
			sched.scheduleJob(jobDetail, trigger);

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
	    
	public static QrtzJobDetailsView findQrtzJobDetailsView(String jobName, UserBean userBean) {
		JobDetail jobDetail = QuartzUtil.findByJobName(jobName, userBean);
		QrtzJobDetailsView qrtzJobDetailsView = new QrtzJobDetailsView();
		if (jobDetail != null) {
			JobDataMap data = jobDetail.getJobDataMap();
			String content = data.getString("fastConfigJson");
			FastConfigView fastConfigView = JSON.parseObject(content,
					FastConfigView.class);
			content = data.getString("fieldMappingJson");
			List<FieldMappingView> fieldMappingView = JSON.parseArray(content,
					FieldMappingView.class);
			content = data.getString("dispatchingModeJosn");
			DispatchingModeView dispatchingModeView = JSON.parseObject(content,
					DispatchingModeView.class);
			qrtzJobDetailsView.setFastConfigView(fastConfigView);
			qrtzJobDetailsView.setFieldMappingView(fieldMappingView);
			qrtzJobDetailsView.setDispatchingModeView(dispatchingModeView);
			qrtzJobDetailsView.setJobName(jobName);
			if (fastConfigView != null) {
				switch (fastConfigView.getIdSourceDatabase()) {
				case 1:
					// 数据库
					switch (fastConfigView.getIdDestDatabase()) {
					case 1:
						// 数据库
						break;
					case 2:
						// ftp
						break;
					case 3:
						// hadoop
						break;
					case 4:
						// 数据集市
						break;
					default:
						break;
					}
					break;
				case 2:

					break;
				case 3:
					break;
				case 4:
					break;
				default:
					break;
				}
			}
		}

		return qrtzJobDetailsView;
	}
	

	public static boolean deleteJob(String jobName) {
		Connection connection = null ;
		PreparedStatement smt = null;
		Integer row = 0;
		String sql = "DELETE FROM QRTZ_JOB_DETAILS WHERE JOB_NAME=?";
		try {
			connection = ConnectionPool.getConnection();
			smt = connection.prepareStatement(sql);
			smt.setString(1, jobName);
			row = smt.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			ConnectionPool.freeConn(null, smt, null, connection);
		}
		return row > 0 ? true : false;

	}
}
