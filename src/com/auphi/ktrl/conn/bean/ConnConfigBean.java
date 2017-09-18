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
package com.auphi.ktrl.conn.bean;

public class ConnConfigBean {
	private String id;
	private String name;
	private String dbms;
	private String ip;
	private String port;
	private String username;
	private String password;
	private String database;
	private String driverclass;
	private String maxconn;
	private String maxidle;
	private String maxwait;
	private String url;
	
	public String getMaxidle() {
		return maxidle;
	}

	public void setMaxidle(String maxidle) {
		this.maxidle = maxidle;
	}

	public String getMaxwait() {
		return maxwait;
	}

	public void setMaxwait(String maxwait) {
		this.maxwait = maxwait;
	}
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getMaxconn() {
		return maxconn;
	}

	public void setMaxconn(String maxconn) {
		this.maxconn = maxconn;
	}

	public String getDriverclass() {
		return driverclass;
	}

	public void setDriverclass(String driverclass) {
		this.driverclass = driverclass;
	}

	private String validateQuery;
	
	public String getValidateQuery() {
		return validateQuery;
	}

	public void setValidateQuery(String validateQuery) {
		this.validateQuery = validateQuery;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDbms() {
		return dbms;
	}

	public void setDbms(String dbms) {
		this.dbms = dbms;
	}

	public String getIp() {
		return ip;
	}
	
	public void setIp(String ip) {
		this.ip = ip;
	}
	
	public String getPort() {
		return port;
	}
	
	public void setPort(String port) {
		this.port = port;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getDatabase() {
		return database;
	}
	
	public void setDatabase(String database) {
		this.database = database;
	}
}
