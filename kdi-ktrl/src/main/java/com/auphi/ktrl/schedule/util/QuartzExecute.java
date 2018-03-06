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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;

import com.alibaba.fastjson.JSON;
import com.auphi.ktrl.engine.KettleEngine;
import com.auphi.ktrl.engine.impl.KettleEngineImpl2_3;
import com.auphi.ktrl.engine.impl.KettleEngineImpl4_3;
import com.auphi.ktrl.monitor.bean.MonitorScheduleBean;
import com.auphi.ktrl.monitor.util.MonitorUtil;
import com.auphi.ktrl.schedule.bean.ScheduleBean;
import com.auphi.ktrl.schedule.template.Template;
import com.auphi.ktrl.schedule.template.TemplateFactory;
import com.auphi.ktrl.schedule.view.DispatchingModeView;
import com.auphi.ktrl.schedule.view.FastConfigView;
import com.auphi.ktrl.system.mail.util.MailUtil;
import com.auphi.ktrl.system.user.util.UserUtil;
import com.auphi.ktrl.util.StringUtil;

public class QuartzExecute implements Job {
	private static Logger logger = Logger.getLogger(QuartzExecute.class);

	@Override
	public synchronized void execute(JobExecutionContext arg0) {
		// TODO Auto-generated method stub

		JobDetail jobDetail = arg0.getJobDetail();
		JobDataMap data = jobDetail.getJobDataMap();
		
		Boolean isFastConfig = data.getBoolean("isFastConfig");
		
		if(isFastConfig){
			executeFastConfig(data, jobDetail.getName());
		}else {
			executeNormal(data, jobDetail.getName(), jobDetail.getGroup());
		}
	}
	
	/**
	 * run as normal
	 * @param data jobDataMap
	 * @param jobDetailName
	 */
	public void executeNormal(JobDataMap data, String jobDetailName, String jobGroup){
		String version = data.getString("version");
		String actionRef = data.getString("actionRef");
		String actionPath = data.getString("actionPath");
		String fileType = data.getString("fileType");
		String repName = data.getString("repName");
		int execType = Integer.parseInt(data.getString("execType")==null?"1":data.getString("execType"));
		String remoteServer = data.getString("remoteServer");
		String ha = data.getString("ha");
		
		KettleEngine kettleEngine = null;
		
		//run kettle engine for different version
		if(KettleEngine.VERSION_2_3.equals(version)){
			kettleEngine = new KettleEngineImpl2_3();
		}else if(KettleEngine.VERSION_4_3.equals(version)){
			kettleEngine = new KettleEngineImpl4_3();
		}
		
		int id = Integer.parseInt(StringUtil.createNumberString(9));
		
		try{
			ScheduleBean scheduleBean = ScheduleUtil.getScheduleBeanByJobName(jobDetailName, jobGroup);
			MonitorUtil.addMonitorBeforeRun(id, scheduleBean);
			
			kettleEngine.execute(repName, actionPath, actionRef, fileType, id, execType, remoteServer, ha);
			
		}catch(Exception e){
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			String errMsg = sw.toString();
			
			logger.error(e.getMessage(), e);
			MonitorUtil.updateMonitorAfterError(id, errMsg);
		}finally{
			MonitorScheduleBean monitorData = MonitorUtil.getMonitorData(String.valueOf(id));
			if(MonitorUtil.STATUS_ERROR.equals(monitorData.getJobStatus())){
				String title = "[ScheduleError][" + StringUtil.DateToString(new Date(), "yyyy-MM-dd HH:mm:ss") + "][" + jobDetailName + "]"; 
				String errorNoticeUserId = data.getString("errorNoticeUserId");
				String[] user_mails = UserUtil.getUserEmails(errorNoticeUserId);
				MailUtil.sendMail(user_mails, title, monitorData.getLogMsg());
			}
		}
	}
	
	/**
	 * run as fastconfig
	 * @param data jobDataMap
	 * @param middlePath 
	 */
	public void executeFastConfig(JobDataMap data, String jobDetailName){
		String fastConfigJson = data.getString("fastConfigJson");
		String fieldMappingJson = data.getString("fieldMappingJson");
		String dispatchingModeJosn = data.getString("dispatchingModeJosn");
		
		FastConfigView fastConfigView = JSON.parseObject(fastConfigJson, FastConfigView.class);
		DispatchingModeView dispatchingModeView= JSON.parseObject(dispatchingModeJosn, DispatchingModeView.class);
		
		String repName = "Default";
		//runmode:1 本地运行,2集群运行，对应到execType:1本地运行,4ha运行
		int execType = "1".equals(dispatchingModeView.getRunMode())?1:4;
		//目前还没有开放2集群运行，未设置ha
		String ha = dispatchingModeView.getRunCluster();
		String middlePath = "Template" + fastConfigView.getIdSourceType() + fastConfigView.getIdDestType() +"1";
		int id = Integer.parseInt(StringUtil.createNumberString(9));
		Date date =new Date();
		long time = System.currentTimeMillis()-24*60*60*1000;//yesterday
		date.setTime(time);
		try{
			Template template = TemplateFactory.createTemplate(repName, middlePath, date, false);
			if(template==null)
			{
				logger.error("template not found："+middlePath);
				return;
			}
				else
			template.bind(fastConfigJson, fieldMappingJson);
			
			
			MonitorUtil.addMonitorBeforeRun(id, jobDetailName, middlePath, data.getString("userId"), "", ha);
			template.execute(id, execType, "", ha);
		}catch (Exception e){
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			String errMsg = sw.toString();
			
			logger.error(e.getMessage(), e);
			MonitorUtil.updateMonitorAfterError(id, errMsg);
			
		}finally{
			MonitorScheduleBean monitorData = MonitorUtil.getMonitorData(String.valueOf(id));
			if(MonitorUtil.STATUS_ERROR.equals(monitorData.getJobStatus())){
				String title = "[ScheduleError][" + StringUtil.DateToString(new Date(), "yyyy-MM-dd HH:mm:ss") + "][" + jobDetailName + "]"; 
				String errorNoticeUserId = data.getString("errorNoticeUserId");
				String[] user_mails = UserUtil.getUserEmails(errorNoticeUserId);
				MailUtil.sendMail(user_mails, title, monitorData.getLogMsg());
			}
		}
	}
}
