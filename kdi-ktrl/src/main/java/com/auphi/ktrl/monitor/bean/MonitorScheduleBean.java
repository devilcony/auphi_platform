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
package com.auphi.ktrl.monitor.bean;

public class MonitorScheduleBean {
	private int id;
	private String jobName;
	private String jobGroup;
	private String jobFile;
	private String jobStatus;
	private String startTime;
	private String endTime;
	private float continuedTime;
	private String logMsg;
	private String errMsg;
	private String haName;
	private String serverName;
	private int id_batch;
	private String id_logchannel;
	private int lines_error;
	private int lines_input;
	private int lines_output;
	private int lines_updated;
	private int lines_read;
	private int lines_written;
	private int lines_deleted;
	
	public String getJobName() {
		return jobName;
	}
	
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
	
	public String getJobGroup() {
		return jobGroup;
	}
	
	public void setJobGroup(String jobGroup) {
		this.jobGroup = jobGroup;
	}
	
	public String getJobFile() {
		return jobFile;
	}
	
	public void setJobFile(String jobFile) {
		this.jobFile = jobFile;
	}
	
	public String getJobStatus() {
		return jobStatus;
	}
	
	public void setJobStatus(String jobStatus) {
		this.jobStatus = jobStatus;
	}

	public float getContinuedTime() {
		return continuedTime;
	}

	public void setContinuedTime(float continuedTime) {
		this.continuedTime = continuedTime;
	}

	public String getErrMsg() {
		return errMsg;
	}

	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public String getLogMsg() {
		return logMsg;
	}

	public void setLogMsg(String logMsg) {
		this.logMsg = logMsg;
	}

	public String getHaName() {
		return haName;
	}

	public void setHaName(String haName) {
		this.haName = haName;
	}

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
	
	public int getId_batch() {
		return id_batch;
	}
	
	public void setId_batch(int id_batch) {
		this.id_batch = id_batch;
	}

	public String getId_logchannel() {
		return id_logchannel;
	}

	public void setId_logchannel(String id_logchannel) {
		this.id_logchannel = id_logchannel;
	}

	public int getLines_error() {
		return lines_error;
	}

	public void setLines_error(int lines_error) {
		this.lines_error = lines_error;
	}
	
	public int getLines_read() {
		return lines_read;
	}
	
	public void setLines_read(int lines_read) {
		this.lines_read = lines_read;
	}
	
	public int getLines_written() {
		return lines_written;
	}
	
	public void setLines_written(int lines_written) {
		this.lines_written = lines_written;
	}
	
	public int getLines_updated() {
		return lines_updated;
	}
	
	public void setLines_updated(int lines_updated) {
		this.lines_updated = lines_updated;
	}
	
	public int getLines_input() {
		return lines_input;
	}
	
	public void setLines_input(int lines_input) {
		this.lines_input = lines_input;
	}
	
	public int getLines_output() {
		return lines_output;
	}
	
	public void setLines_output(int lines_output) {
		this.lines_output = lines_output;
	}
	
	public int getLines_deleted() {
		return lines_deleted;
	}

	public void setLines_deleted(int lines_deleted) {
		this.lines_deleted = lines_deleted;
	}
}
