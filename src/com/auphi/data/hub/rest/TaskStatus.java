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

import java.io.Serializable;

/**
 * 任务状态信息
 * 
 * @author zhangfeng
 * 
 */
public class TaskStatus implements Serializable{

	private static final long serialVersionUID = 8268953765464038939L;

	enum Status {
		Running(201), 
		Error(500), 
		Forbidden(403), 
		Completed(200), 
		ParameterNotFound(407),
		NULL(406),
		MethodNotAllowed(405),
		Unauthorized(401),
		NotFound(404);		
		
		private int index;

		Status(int idx) {
			this.index = idx;
		}

		public int getIndex() {
			return index;
		}
	}

	private Status status;
	
	private String ftpUserName;
	
	private String ftpPassword;

	private int statusCode;
	
	private String message;

	private String ftpPath;
	
	private String zipPassword;
	
	private String MD5;
	
	public String getFtpUserName() {
		return ftpUserName;
	}

	public void setFtpUserName(String ftpUserName) {
		this.ftpUserName = ftpUserName;
	}

	public String getFtpPassword() {
		return ftpPassword;
	}

	public void setFtpPassword(String ftpPassword) {
		this.ftpPassword = ftpPassword;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getFtpPath() {
		return ftpPath;
	}

	public void setFtpPath(String ftpPath) {
		this.ftpPath = ftpPath;
	}

	public String getZipPassword() {
		return zipPassword;
	}

	public void setZipPassword(String zipPassword) {
		this.zipPassword = zipPassword;
	}

	public String getMD5() {
		return MD5;
	}

	public void setMD5(String md5) {
		MD5 = md5;
	}

	
}
