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

import java.io.IOException;
import java.net.SocketException;
import java.util.Timer;
import java.util.TimerTask;

import com.auphi.data.hub.core.properties.PropertiesFile;
import com.auphi.data.hub.core.properties.PropertiesHelper;
import com.auphi.data.hub.domain.Service;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.net.ftp.FTPClient;

import com.auphi.data.hub.core.properties.PropertiesFactory;


/**
 * 删除FTP上超时的文件
 * @author zhangfeng
 *
 */
public class DeleteFileSchedule {
	
	private static Log logger = LogFactory.getLog(DeleteFileSchedule.class);
	
	private static DeleteFileSchedule task = null;
	
	private DeleteFileSchedule(){
		
	}

	public static DeleteFileSchedule getInstance(){
		if(task == null){
			task = new DeleteFileSchedule();
		}
		return task;
	}
	
	public void schedule(long delay, Service service, String userName, String ftpPath, String parameterValueIdentify){
		logger.info("exector timer task............");
		logger.info("userName : " + userName);
		logger.info("ftpPath : " + ftpPath);
		logger.info("service indentify : " + service.getServiceIdentify());	
		Timer timer = new Timer();
		timer.schedule(new DeleteFileTask(service,userName,ftpPath,parameterValueIdentify),  delay);
	}
	
	static class DeleteFileTask extends TimerTask{
		
		private Service service ;
		
		private String userName;
		
		private String ftpPath;
		
		private String parameterValueIdentify;
		
		public DeleteFileTask(Service service,String userName,String ftpPath,String parameterValueIdentify){
			this.ftpPath = ftpPath;
			this.userName = userName;
			this.service = service;
			this.parameterValueIdentify = parameterValueIdentify;
		}

		@Override
		public void run() {
			String indentify = service.getServiceIdentify();
			logger.info("delete ftp dir : " + ftpPath);
			//删除文件并清除用户的状态信息
			this.deleteFTPFile(indentify,ftpPath,userName);
		}
		
		/**
		 * 连接ftp，获取ftpclient对象
		 * @param ip
		 * @param userName
		 * @param password
		 * @return
		 */
		private FTPClient getFtpClient(String ip,String userName,String password){
			FTPClient ftpClient = new FTPClient(); 
			try {
				ftpClient.connect(ip);
				ftpClient.login(userName, password); 
			} catch (SocketException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} 
			return ftpClient;
		}
		
		/**
		 * 根据服务标示删除FTP文件
		 * @param indentify
		 */
		public void deleteFTPFile(String indentify,String ftppath,String user){
			PropertiesHelper helper = PropertiesFactory.getPropertiesHelper(PropertiesFile.APP);
			//获取FTP用户名、密码、及IP地址
			String ip = helper.getValue("ftp.ip");// 获取属性值
			String userName = helper.getValue("ftp.username");
			String password = helper.getValue("ftp.password");
			String rootPath = helper.getValue("ftp.root.path");
			//获取对外数据服务对象
			String ftpPath = rootPath + "/" +ftppath;
			//获取FTPClient对象
			FTPClient ftpClient = getFtpClient(ip,userName,password);
			try {
				logger.info("delete ftp dir path : " + ftpPath);
				//删除文件路径
				ftpClient.removeDirectory(ftpPath);
				//移除定时任务
				this.cancel();
				String key = RestServiceController.generateIdentifier(user, indentify,parameterValueIdentify);
				logger.info("after  : " + RestTaskStatusManager.getInstance().getTaskStatus(key));
				//清除状态管理器中的状态
				RestTaskStatusManager.getInstance().removeStatus(key);
				logger.info("before  : " + RestTaskStatusManager.getInstance().getTaskStatus(key));
				logger.info("task completed");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
}
