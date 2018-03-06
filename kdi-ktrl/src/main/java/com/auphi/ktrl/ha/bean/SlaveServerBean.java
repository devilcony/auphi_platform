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
package com.auphi.ktrl.ha.bean;

public class SlaveServerBean {

	private int id_slave;
	private String name;
	private String host_name;
	private String port;
	private String web_app_name;
	private String username;
	private String password;
	private String proxy_host_name;
	private String proxy_port;
	private String non_proxy_hosts;
	private String master;
	public int getId_slave() {
		return id_slave;
	}
	
	public void setId_slave(int id_slave) {
		this.id_slave = id_slave;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getHost_name() {
		return host_name;
	}
	
	public void setHost_name(String host_name) {
		this.host_name = host_name;
	}
	
	public String getPort() {
		return port;
	}
	
	public void setPort(String port) {
		this.port = port;
	}
	
	public String getWeb_app_name() {
		return web_app_name;
	}
	
	public void setWeb_app_name(String web_app_name) {
		this.web_app_name = web_app_name;
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
	
	public String getProxy_host_name() {
		return proxy_host_name;
	}
	
	public void setProxy_host_name(String proxy_host_name) {
		this.proxy_host_name = proxy_host_name;
	}
	
	public String getProxy_port() {
		return proxy_port;
	}
	
	public void setProxy_port(String proxy_port) {
		this.proxy_port = proxy_port;
	}
	
	public String getNon_proxy_hosts() {
		return non_proxy_hosts;
	}
	
	public void setNon_proxy_hosts(String non_proxy_hosts) {
		this.non_proxy_hosts = non_proxy_hosts;
	}
	
	public String getMaster() {
		return master;
	}
	
	public void setMaster(String master) {
		this.master = master;
	}
}
