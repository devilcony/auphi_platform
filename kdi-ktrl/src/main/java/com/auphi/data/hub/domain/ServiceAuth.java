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
package com.auphi.data.hub.domain;

import java.io.Serializable;

public class ServiceAuth implements Serializable{
	
	private String authId;            //权限ID
	
	private String serviceId;         //接口服务ID
	
	private String userId;			  //接口服务用户ID
	
	private String service_name;      //接口服务名称
	
	private String service_url;       //接口服务地址
	
	private String authIP;            //授权IP
	
	private String userName;          //授权用户
	
	private String use_dept;          //使用部门
	
	private String user_name;         //使用人员
	
	private String use_desc;          //业务用途
	

	public String getAuthId() {
		return authId;
	}

	public void setAuthId(String authId) {
		this.authId = authId;
	}

	public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getService_name() {
		return service_name;
	}

	public void setService_name(String serviceName) {
		service_name = serviceName;
	}

	public String getService_url() {
		return service_url;
	}

	public void setService_url(String serviceUrl) {
		service_url = serviceUrl;
	}

	public String getAuthIP() {
		return authIP;
	}

	public void setAuthIP(String authIP) {
		this.authIP = authIP;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUse_dept() {
		return use_dept;
	}

	public void setUse_dept(String useDept) {
		use_dept = useDept;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String userName) {
		user_name = userName;
	}

	public String getUse_desc() {
		return use_desc;
	}

	public void setUse_desc(String useDesc) {
		use_desc = useDesc;
	}
	
	
	
}
