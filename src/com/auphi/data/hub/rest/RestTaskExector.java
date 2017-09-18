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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.spi.LoggingEvent;
import org.codehaus.jackson.map.ObjectMapper;
import org.pentaho.di.cluster.SlaveServer;
import org.pentaho.di.core.Result;
import org.pentaho.di.core.logging.CentralLogStore;
import org.pentaho.di.job.Job;
import org.pentaho.di.job.JobExecutionConfiguration;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.repository.RepositoryDirectoryInterface;
import org.pentaho.di.repository.kdr.KettleDatabaseRepository;
import org.pentaho.di.trans.TransMeta;
import org.springframework.beans.factory.annotation.Autowired;

import com.auphi.ktrl.engine.impl.KettleEngineImpl4_3;

import com.auphi.data.hub.core.struct.BaseDto;
import com.auphi.data.hub.core.struct.Dto;
import com.auphi.data.hub.core.util.SpringBeanLoader;
import com.auphi.data.hub.domain.Service;
import com.auphi.data.hub.rest.TaskStatus.Status;
import com.auphi.data.hub.service.InterfaceService;
import com.auphi.data.hub.service.JobService;
import com.auphi.data.hub.service.MyKettleDatabaseRepositoryMeta;


/**
 * Rest客户端请求任务执行
 * 
 * @author zhangfeng
 *
 */
public class RestTaskExector implements Runnable{
	

	private static final String TEMPLATE_DIR = "/Template/Template121";
	private static final String TEMPLATE_FILE_NAME = "job";

	private static Log logger = LogFactory.getLog(RestTaskExector.class);
	
	@Autowired
	private InterfaceService interfaceService;
	
	private Service service;

	private String userName;	
	private String password;
	private JobService jobService;
	
	private MyKettleDatabaseRepositoryMeta kettleDatabaseRepositoryMeta;

	
	private String ftpIp;
	private String ftpPath;
	
	private String ftpUserName;
	
	private String ftpPassword;
	
	private String systemName;
	
	private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private String sql;
	private String parameterValueIdentify;
	private String md5FileName;
	
	public RestTaskExector(Service service,String userName,String password,String ftpIp,String ftpPath,String ftpUserName,String ftpPassword,String systemName, String sql,String parameterValueIdentify){
		this.service = service;
		this.ftpIp=ftpIp;
		this.userName = userName;
		this.password = password;
		this.ftpPath = ftpPath;
		this.ftpUserName = ftpUserName;
		this.ftpPassword = ftpPassword;
		this.systemName = systemName;
		this.sql = sql;
		this.parameterValueIdentify = parameterValueIdentify;
		this.md5FileName=getRandomMd5FileName();
		kettleDatabaseRepositoryMeta = (MyKettleDatabaseRepositoryMeta) SpringBeanLoader.getInstance().getSpringBean(MyKettleDatabaseRepositoryMeta.class);
		jobService = (JobService) SpringBeanLoader.getInstance().getSpringBean(JobService.class);
		interfaceService = (InterfaceService) SpringBeanLoader.getInstance().getSpringBean(InterfaceService.class);
	}
	
	private String getRandomMd5FileName() {
		String random = new Long(System.currentTimeMillis()).toString();
		return random+".md5";
	}

	/**
	 * @throws Exception 
	 * 
	 */
	public TaskStatus exectorTask() throws Exception{
		TaskStatus status = new TaskStatus();
		//获取接口信息
		String key = RestServiceController.generateIdentifier(this.service.getServiceIdentify(), userName, parameterValueIdentify );
		if(service != null){
			status.setStatus(TaskStatus.Status.Running);
			status.setStatusCode(201);
			status.setMessage("runing!");
			status.setFtpPath(ftpPath+"/data.txt");
			RestTaskStatusManager.getInstance().setTaskStatus(key, status);
			exectorJobTask(service);
		} else {
			status.setStatus(TaskStatus.Status.NotFound);
			status.setStatusCode(404);
			status.setMessage("Service indetify not found!");
			status.setFtpPath(null);
			RestTaskStatusManager.getInstance().setTaskStatus(key, status);
		}
		return status;
	}
	
	/**
	 * 执行Kettle的job任务
	 * @param service
	 * @throws Exception 
	 */
	private void exectorJobTask(Service service) throws Exception{
		//String name = service.getTransName();
		String jobTaskName = service.getServiceName();
		String jobConfigId = service.getJobConfigId();
		//String slave = this.jobService.getSlaveHosts().iterator().next().getAsString("slave");
		String idDatabase = null;
		if (service.getIdDatabase()!=null)
			idDatabase = service.getIdDatabase().toString();
		else
			throw new Exception("there is no database id in the service"+service.getServiceName());
		
		try{
			KettleEngineImpl4_3 kettleEngine = new KettleEngineImpl4_3();
			Repository repository = (Repository)kettleEngine.getRep("Default");
			
			RepositoryDirectoryInterface directoryInterface = repository.loadRepositoryDirectoryTree();
			directoryInterface = directoryInterface.findDirectory(TEMPLATE_DIR.toString());
			JobMeta jobMeta=null;
			if (directoryInterface != null) {
				jobMeta = repository.loadJob(TEMPLATE_FILE_NAME, directoryInterface, null, null); // reads
			}
			Job job = new Job(repository, jobMeta);
			ObjectMapper mapper = new ObjectMapper();
			String[]  arguments = new String[9];
			arguments[0]=ftpIp;
			arguments[1]="21";
			arguments[2]=ftpUserName;
			arguments[3]=ftpPassword;
			arguments[4]=ftpPath;
			arguments[5]=sql;
			arguments[6]=idDatabase; 
			arguments[7]=password;
			arguments[8]=md5FileName;
			jobMeta.setArguments(arguments);
			logger.info("1.job start!");
			job.start();
			job.waitUntilFinished();
			logger.info("2.job finished!");
			
			//Result result = job.getResult(); // Execute the selected job.
			
			//保存服务接口监控信息表
			Dto dto = new BaseDto();
			dto.put("serviceId",service.getServiceId());
			dto.put("startTime",format.format(new Date()));
			dto.put("status", "running");
			dto.put("userName",userName);
			dto.put("systemName",systemName);
			dto.put("MONITOR_ID",null);
			this.interfaceService.saveServiceMonitor(dto);
			logger.info("MONITOR_ID  : " + dto.get("MONITOR_ID"));
			
			//服务接口监控信息ID，传递到线程中
			//开启后台线程收集JOB运行的状态信息
			//Thread thread = new Thread(new JobMonitorThread(job, jobTaskName, config, jobConfigId, jobService, carteObjectId,service.getServiceIdentify(),this.userName));
			//thread.start();
			//收集JOB运行的状态信息,并保存T_JOB_LOG表中
			saveJobLog(job, jobTaskName,  jobConfigId, jobService, null,this.userName,service,dto);
			
		} catch(Exception e){
			String key = RestServiceController.generateIdentifier(userName ,service.getServiceIdentify(),parameterValueIdentify);
			TaskStatus status = RestTaskStatusManager.getInstance().getTaskStatus(key);
			status.setStatus(Status.Error);
			status.setStatusCode(500);
			status.setMessage("Data generation failure");
			RestTaskStatusManager.getInstance().setTaskStatus(key, status);
			e.printStackTrace();
		} 
	}
	
	/**
	 * 
	 * @param job  
	 * @param carteObjectId
	 * @param indetify  服务实例
	 */
	public void saveJobLog(Job job,String jobTaskName,
			String jobConfigId,JobService jobService,String carteObjectId,String userName,Service service,Dto dto){
		
		try {
			Map<String,String> jobObject = new HashMap<String,String>();
			jobObject.put("STARTDATE",format.format(new Date()));
			jobObject.put("LOGDATE", format.format(new Date()));
			Result res = job.getResult();
			
			this.generalJobLogObject(jobObject,jobConfigId,carteObjectId,job,res,jobTaskName);
			this.jobService.saveJobLog(jobObject);
			//设置状态是完成
			String indetify = service.getServiceIdentify();
			String key = RestServiceController.generateIdentifier(userName,indetify, parameterValueIdentify);
			TaskStatus status = RestTaskStatusManager.getInstance().getTaskStatus(key);
			status.setStatus(Status.Completed);
			status.setStatusCode(200);
			status.setMessage("completed");
			//add md5 in result, jason
			String md5 = getMD5();
			logger.info("md5="+md5);
			status.setMD5(md5);			
			
			RestTaskStatusManager.getInstance().setTaskStatus(key, status);			
			//更新服务接口监控信息表的结束时间
			Dto dto2 = new BaseDto();
			dto2.put("endTime",format.format(new Date()));
			dto2.put("status", "completed");
			dto2.put("monitorId",dto.get("MONITOR_ID"));
			this.interfaceService.updateServiceMonitor(dto2);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * get file content, 
	 * return null if no file or file content is null
	 * @return
	 * @throws Exception 
	 */
	private String getMD5() throws Exception {
		File file = new File(".");
		String absoultDirectory;
		String fileName = null;
		try{
			absoultDirectory = file.getCanonicalPath();
			fileName = absoultDirectory+"../webapps/etl_platform/temp/send/"+this.md5FileName;
		}catch(Exception ex)
		{
			throw new Exception("Error when get MD5 file name, file name="+fileName);
		}
		File md5File = new File(fileName);
		logger.info("md5 file name="+fileName);
		StringBuffer buffer = new StringBuffer();
		String result="";
		if(md5File.exists())
		{
			logger.info("md5 file name exist");
			char c;
			try {
				BufferedReader dr=new BufferedReader(new InputStreamReader(new FileInputStream(md5File)));
				result = dr.readLine();
				logger.info("result = "+result);
			}catch(Exception ex)
			{
				ex.printStackTrace();
			}
			md5File.delete();
		}
		else
			return null;
		
		if(result.length()==0)
			return  null;
		else 
			return result;
	}

	/**
	 * 生成转换运行对象信息
	 * @param transLog 运行对象
	 * @param jobConfigId 任务配置ID
	 * @param carteObjectId
	 * @param job 任务对象
	 * @param res 任务返回的结果信息
	 * @param config 配置信息
	 * @param jobTaskName 任务名称
	 * @throws Exception
	 */
	private void generalJobLogObject(Map<String,String> transLog,String jobConfigId,String carteObjectId,Job job,Result res,String jobTaskName) throws Exception{
		transLog.put("JOB_CONFIG_ID",jobConfigId);
		transLog.put("CHANNEL_ID",carteObjectId);
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
		transLog.put("EXECUTING_SERVER", "localhost");
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
		transLog.put("JOB_CN_NAME",jobTaskName);
	}
	

	@Override
	public void run() {
		try{
			this.exectorTask();
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		//如果是FTP返回数据，启动定时任务，清除数据
		if(service.getReturnType().equals("1")){
			try{
				long delay = Integer.parseInt(service.getTimeout()) * 3600 * 1000;
				logger.info(delay +"毫秒后，执行删除任务");
				logger.info("... will delete after "+delay +" ms!");
				DeleteFileSchedule.getInstance().schedule(delay, service, userName, ftpPath,parameterValueIdentify);
			} catch(Exception e){
				e.printStackTrace();
			} 
		}
		
	}
}
