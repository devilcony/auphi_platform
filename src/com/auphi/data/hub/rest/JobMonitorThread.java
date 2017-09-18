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
package com.auphi.data.hub.rest;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.auphi.data.hub.service.JobService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.spi.LoggingEvent;
import org.pentaho.di.core.Result;
import org.pentaho.di.core.logging.CentralLogStore;
import org.pentaho.di.job.Job;
import org.pentaho.di.job.JobExecutionConfiguration;

import com.auphi.data.hub.rest.TaskStatus.Status;

/**
 * JOB任务监控线程
 * 
 * @author zhangfeng
 *
 */
public class JobMonitorThread implements Runnable {
	private static Log logger = LogFactory.getLog(JobMonitorThread.class);
	
	private String jobTaskName ;
	
	private String jobConfigId ;
	
	private Job job;
	
	private JobService jobService;
	
	private String carteObjectId;
	
	private JobExecutionConfiguration config;
	
	private String indetify;
	
	private String userName;
	
	private String parameterValueIdentify = null;

	private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public JobMonitorThread(Job job,String jobTaskName,JobExecutionConfiguration config,
			String jobConfigId,JobService jobService,String carteObjectId,String indetify,String userName,String parameterValueIdentify){
		this.job = job;
		this.jobTaskName = jobTaskName;
		this.jobConfigId = jobConfigId;
		this.jobService = jobService;
		this.carteObjectId = carteObjectId;
		this.config = config;
		this.indetify = indetify;
		this.userName = userName;
		this.parameterValueIdentify = parameterValueIdentify;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		try {
			Map<String,String> jobObject = new HashMap<String,String>();
			jobObject.put("STARTDATE",format.format(new Date()));
			jobObject.put("LOGDATE", format.format(new Date()));
			Result res = job.getResult();
			
			this.generalJobLogObject(jobObject, res, carteObjectId);
			this.jobService.saveJobLog(jobObject);
			//设置状态是完成
			//String key = this.userName + "@"+ this.indetify;
			String key = this.userName + "@"+ this.indetify+":"+parameterValueIdentify;
			TaskStatus status = RestTaskStatusManager.getInstance().getTaskStatus(key);
			status.setStatus(Status.Completed);
			status.setMessage("Data generation to complete");
			RestTaskStatusManager.getInstance().setTaskStatus(key, status);			
			//更新服务接口监控信息表的结束时间
			//
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 生成转换运行对象信息
	 * @param transLog
	 * @param status
	 * @param slave
	 * @param carteObjectId
	 * @param execType 执行类型，2表示远程，3表示集群
	 * @throws Exception
	 */
	private void generalJobLogObject(Map<String,String> transLog,Result res,String carteObjectId) throws Exception{
		transLog.put("JOB_CONFIG_ID", this.jobConfigId);
		transLog.put("CHANNEL_ID", this.carteObjectId);
		transLog.put("JOB_NAME", job.getJobname());
		transLog.put("STATUS", job.getStatus());
		transLog.put("LINES_READ", res.getNrLinesRead() +"");
		transLog.put("LINES_WRITTEN",res.getNrLinesWritten() +"");
		transLog.put("LINES_UPDATED", res.getNrLinesUpdated() +"");
		transLog.put("LINES_INPUT", res.getNrLinesInput() +"");
		transLog.put("LINES_OUTPUT", res.getNrLinesOutput() +"");
		transLog.put("LINES_REJECTED", res.getNrLinesRejected() +"");
		transLog.put("ERRORS",res.getNrErrors() +"");
		transLog.put("ENDDATE", format.format(new Date()));
		transLog.put("DEPDATE", format.format(new Date()));
		transLog.put("REPLAYDATE",format.format(new Date()));
		transLog.put("LOG_FIELD","");		
		transLog.put("EXECUTING_SERVER", config.getRemoteServer().getServerAndPort());
		//xnren start
		//transLog.put("EXECUTING_USER", job.getExecutingUser());
		transLog.put("EXECUTING_USER", "");
		transLog.put("EXCUTOR_TYPE", "2");
		String jobLogs = "";
		List<LoggingEvent> logs = CentralLogStore.getLogBufferFromTo(job.getLogChannelId(), false, 0, 2000);
		for(LoggingEvent event : logs){
			jobLogs += event.getMessage().toString() +"\n";
		}
		logger.info("Job Log : " + jobLogs);
		transLog.put("JOB_LOG", jobLogs);
		transLog.put("JOB_CN_NAME", this.jobTaskName);
	}

	public String getParameterValueIdentify() {
		return parameterValueIdentify;
	}

	public void setParameterValueIdentify(String parameterValueIdentify) {
		this.parameterValueIdentify = parameterValueIdentify;
	}
	

}
