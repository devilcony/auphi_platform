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
package com.auphi.ktrl.monitor;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;

import org.apache.log4j.Logger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;

import com.alibaba.fastjson.JSON;
import com.auphi.ktrl.monitor.bean.MonitorScheduleBean;
import com.auphi.ktrl.monitor.util.MonitorUtil;
import com.auphi.ktrl.schedule.template.Template;
import com.auphi.ktrl.schedule.template.TemplateFactory;
import com.auphi.ktrl.schedule.util.QuartzUtil;
import com.auphi.ktrl.schedule.view.DispatchingModeView;
import com.auphi.ktrl.schedule.view.FastConfigView;
import com.auphi.ktrl.system.user.bean.UserBean;
import com.auphi.ktrl.util.StringUtil;

public class MonitorReloadThread extends Thread {
	private static Logger logger = Logger.getLogger(MonitorReloadThread.class);
	
	private Template template;
	private int id;
	private int execType;
	private String ha;
	private boolean isFastConfig;
	private UserBean userBean;
	private String jobName;
	
	public MonitorReloadThread(String monitorId, Date date, UserBean userBean){
		this.userBean = userBean;
		MonitorScheduleBean monitorScheduleBean = MonitorUtil.getMonitorData(monitorId);
		JobDetail jobDetail = QuartzUtil.findByJobName(monitorScheduleBean.getJobName(), userBean);
		JobDataMap data = jobDetail.getJobDataMap();
		jobName = monitorScheduleBean.getJobName();
		
		isFastConfig = data.getBoolean("isFastConfig");
		if(isFastConfig){
			String fastConfigJson = data.getString("fastConfigJson");
			String fieldMappingJson = data.getString("fieldMappingJson");
			String dispatchingModeJosn = data.getString("dispatchingModeJosn");
			
			FastConfigView fastConfigView = JSON.parseObject(fastConfigJson, FastConfigView.class);
			DispatchingModeView dispatchingModeView= JSON.parseObject(dispatchingModeJosn, DispatchingModeView.class);
			
			String repName = "Default";
			//runmode:1 本地运行,2集群运行，对应到execType:1本地运行,4ha运行
			execType = "1".equals(dispatchingModeView.getRunMode())?1:4;
			//目前还没有开放2集群运行，未设置ha
			String ha = "";
			String middlePath = "Template" + fastConfigView.getIdSourceType() + fastConfigView.getIdDestType() + fastConfigView.getLoadType();
			id = Integer.parseInt(StringUtil.createNumberString(9));
			
			try{
				template = TemplateFactory.createTemplate(repName, middlePath, date, true);
				System.out.println("============executeFastConfig==template"+ template);
				if(template==null) {
					logger.error("template not found："+middlePath);
					return;
				}else {
					template.bind(fastConfigJson, fieldMappingJson);
				}
				
				MonitorUtil.addMonitorBeforeRun(id, monitorScheduleBean.getJobName(), middlePath, data.getString("userId"), "", ha);
			} catch(Exception e){
				logger.error(e.getMessage(), e);
			}
		}else {
			
		}
	}
	
	@Override
	public void run() {
		try{
			if(isFastConfig){
				template.execute(id, execType, "", ha);
			}else {
				QuartzUtil.execute(new String[]{jobName}, userBean);
			}
		}catch (Exception e){
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			String errMsg = sw.toString();
			
			logger.error(e.getMessage(), e);
			MonitorUtil.updateMonitorAfterError(id, errMsg);
		}
	}
}
