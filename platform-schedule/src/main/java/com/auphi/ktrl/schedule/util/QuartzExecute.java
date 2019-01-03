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


import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;




public class QuartzExecute implements Job {

	private static Logger logger = Logger.getLogger(QuartzExecute.class);
	@Override
	public synchronized void execute(JobExecutionContext arg0) {
		// TODO Auto-generated method stub
		logger.info("=============================");
		logger.info(arg0);
		JobDetail jobDetail = arg0.getJobDetail();
		JobDataMap data = jobDetail.getJobDataMap();

		Boolean isFastConfig = data.getBoolean("isFastConfig");

		if(isFastConfig){
			executeFastConfig(data, jobDetail.getKey().getName());
		}else {
			executeNormal(data, jobDetail.getKey().getName(), jobDetail.getKey().getGroup());
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



		try{


		}catch(Exception e){

		}finally{

		}
	}

	/**
	 * run as fastconfig
	 * @param data
	 * @param jobDetailName
	 */
	public void executeFastConfig(JobDataMap data, String jobDetailName){
		String fastConfigJson = data.getString("fastConfigJson");
		String fieldMappingJson = data.getString("fieldMappingJson");
		String dispatchingModeJosn = data.getString("dispatchingModeJosn");


		try{

		}catch (Exception e){


		}finally{

		}
	}
}
