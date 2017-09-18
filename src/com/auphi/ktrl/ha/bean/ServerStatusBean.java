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

public class ServerStatusBean {
	private int id_status;
	private int id_slave;
	private String name_slave;
	private int is_running;
	private float cpu_usage;
	private float memory_usage;
	private int running_jobs_num;
	
	public int getId_status() {
		return id_status;
	}
	
	public void setId_status(int id_status) {
		this.id_status = id_status;
	}
	
	public int getId_slave() {
		return id_slave;
	}
	
	public void setId_slave(int id_slave) {
		this.id_slave = id_slave;
	}
	
	public int getIs_running() {
		return is_running;
	}
	
	public void setIs_running(int is_running) {
		this.is_running = is_running;
	}
	
	public float getCpu_usage() {
		return cpu_usage;
	}
	
	public void setCpu_usage(float cpu_usage) {
		this.cpu_usage = cpu_usage;
	}
	
	public float getMemory_usage() {
		return memory_usage;
	}
	
	public void setMemory_usage(float memory_usage) {
		this.memory_usage = memory_usage;
	}
	
	public int getRunning_jobs_num() {
		return running_jobs_num;
	}
	
	public void setRunning_jobs_num(int running_jobs_num) {
		this.running_jobs_num = running_jobs_num;
	}

	public String getName_slave() {
		return name_slave;
	}

	public void setName_slave(String name_slave) {
		this.name_slave = name_slave;
	}
}
