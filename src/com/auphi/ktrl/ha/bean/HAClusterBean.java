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

public class HAClusterBean {

	private int id_cluster;
	private String name;
	private String base_port;
	private String sockets_buffer_size;
	private String sockets_flush_interval;
	private String sockets_compressed;
	private String dynamic_cluster;
	private String[] slaves;
	
	public int getId_cluster() {
		return id_cluster;
	}
	
	public void setId_cluster(int id_cluster) {
		this.id_cluster = id_cluster;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getBase_port() {
		return base_port;
	}
	
	public void setBase_port(String base_port) {
		this.base_port = base_port;
	}
	
	public String getSockets_buffer_size() {
		return sockets_buffer_size;
	}
	
	public void setSockets_buffer_size(String sockets_buffer_size) {
		this.sockets_buffer_size = sockets_buffer_size;
	}
	
	public String getSockets_flush_interval() {
		return sockets_flush_interval;
	}
	
	public void setSockets_flush_interval(String sockets_flush_interval) {
		this.sockets_flush_interval = sockets_flush_interval;
	}
	
	public String getSockets_compressed() {
		return sockets_compressed;
	}
	
	public void setSockets_compressed(String sockets_compressed) {
		this.sockets_compressed = sockets_compressed;
	}
	
	public String getDynamic_cluster() {
		return dynamic_cluster;
	}
	
	public void setDynamic_cluster(String dynamic_cluster) {
		this.dynamic_cluster = dynamic_cluster;
	}

	public String[] getSlaves() {
		return slaves;
	}

	public void setSlaves(String[] slaves) {
		this.slaves = slaves;
	}
}
