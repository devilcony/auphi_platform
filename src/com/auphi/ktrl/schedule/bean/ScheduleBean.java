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
package com.auphi.ktrl.schedule.bean;

import java.io.Serializable;


public class ScheduleBean implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String triggerName;
	private String triggerGroup;
	private int triggerState;
	private String nextFireTime;
	private String prevFireTime;
	private String jobName;
	private String jobGroup;
	private String description;
	private String cronString;
	private int repeatCount;
	private long repeatInterval;
	private String startTime;
	private String startDate;
	private String haveEndDate;
	private String endDate;
	private String actionRef;
	private String middlePath;
	private String version;
	private String fileType;
	private String repName;
	private String actionPath;
	private int cycle;
	private String cycleNum;
	private String dayType;
	private String monthType;
	private String yearType;
	private String dayNum;
	private String weekNum;
	private String monthNum;
	private String userId;
	private boolean edit;
	private String execType;
	private String remoteServer;
	private String errorNoticeUserName;
	private String errorNoticeUserId;
	private String ha;
	
	
	public String getMiddlePath() {
		return middlePath;
	}

	public void setMiddlePath(String middlePath) {
		this.middlePath = middlePath;
	}
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public boolean getEdit() {
		return edit;
	}

	public void setEdit(boolean edit) {
		this.edit = edit;
	}

	public String getTriggerName() {
		return triggerName;
	}
	
	public void setTriggerName(String triggerName) {
		this.triggerName = triggerName;
	}
	
	public String getTriggerGroup() {
		return triggerGroup;
	}
	
	public void setTriggerGroup(String triggerGroup) {
		this.triggerGroup = triggerGroup;
	}
	
	public int getTriggerState() {
		return triggerState;
	}
	
	public void setTriggerState(int triggerState) {
		this.triggerState = triggerState;
	}
	
	public String getNextFireTime() {
		return nextFireTime;
	}
	
	public void setNextFireTime(String nextFireTime) {
		this.nextFireTime = nextFireTime;
	}
	
	public String getPrevFireTime() {
		return prevFireTime;
	}
	
	public void setPrevFireTime(String prevFireTime) {
		this.prevFireTime = prevFireTime;
	}
	public String getFullname(){
		return jobGroup+"."+jobName ;
	}
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
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getCronString() {
		return cronString;
	}
	
	public void setCronString(String cronString) {
		this.cronString = cronString;
	}
	
	public int getRepeatCount() {
		return repeatCount;
	}
	
	public void setRepeatCount(int repeatCount) {
		this.repeatCount = repeatCount;
	}
	
	public long getRepeatInterval() {
		return repeatInterval;
	}
	
	public void setRepeatInterval(long repeatInterval) {
		this.repeatInterval = repeatInterval;
	}
	
	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getStartDate() {
		return startDate;
	}
	
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	
	public String getHaveEndDate() {
		return haveEndDate;
	}

	public void setHaveEndDate(String haveEndDate) {
		this.haveEndDate = haveEndDate;
	}

	public String getEndDate() {
		return endDate;
	}
	
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getActionRef() {
		return actionRef;
	}

	public void setActionRef(String actionRef) {
		this.actionRef = actionRef;
	}
	
	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public String getRepName() {
		return repName;
	}

	public void setRepName(String repName) {
		this.repName = repName;
	}

	public String getActionPath() {
		return actionPath;
	}

	public void setActionPath(String actionPath) {
		this.actionPath = actionPath;
	}

	public int getCycle() {
		return cycle;
	}

	public void setCycle(int cycle) {
		this.cycle = cycle;
	}

	public String getCycleNum() {
		return cycleNum;
	}

	public void setCycleNum(String cycleNum) {
		this.cycleNum = cycleNum;
	}

	public String getDayType() {
		return dayType;
	}

	public void setDayType(String dayType) {
		this.dayType = dayType;
	}

	public String getMonthType() {
		return monthType;
	}

	public void setMonthType(String monthType) {
		this.monthType = monthType;
	}

	public String getYearType() {
		return yearType;
	}

	public void setYearType(String yearType) {
		this.yearType = yearType;
	}

	public String getDayNum() {
		return dayNum;
	}

	public void setDayNum(String dayNum) {
		this.dayNum = dayNum;
	}

	public String getWeekNum() {
		return weekNum;
	}

	public void setWeekNum(String weekNum) {
		this.weekNum = weekNum;
	}

	public String getMonthNum() {
		return monthNum;
	}

	public void setMonthNum(String monthNum) {
		this.monthNum = monthNum;
	}
	
	public String getExecType() {
		return execType;
	}

	public void setExecType(String execType) {
		this.execType = execType;
	}

	public String getRemoteServer() {
		return remoteServer;
	}

	public void setRemoteServer(String remoteServer) {
		this.remoteServer = remoteServer;
	}
	
	public String getErrorNoticeUserName() {
		return errorNoticeUserName;
	}

	public void setErrorNoticeUserName(String errorNoticeUserName) {
		this.errorNoticeUserName = errorNoticeUserName;
	}

	public String getErrorNoticeUserId() {
		return errorNoticeUserId;
	}

	public void setErrorNoticeUserId(String errorNoticeUserId) {
		this.errorNoticeUserId = errorNoticeUserId;
	}

	public String getHa() {
		return ha;
	}

	public void setHa(String ha) {
		this.ha = ha;
	}
}
