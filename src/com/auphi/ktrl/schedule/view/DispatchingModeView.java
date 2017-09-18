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
package com.auphi.ktrl.schedule.view;

/**
 * 
 * 调度方式
 * **/
public class DispatchingModeView {
	private String runMode;//运行方式
	private String runCluster;//集群clusterid
	private String beginTime;//开始时间
	private Integer scheduletype;// 1为小时，2为天，3为月
	private String cycleMode;//周期模式  小时/ 天
	private String cycleModeMonth;//周期模式 月
	private String runDate; //开始日期
	private Integer endTimeType;// 1为永久，2为自定义
	private String endTime;//自定义时间
	

	public String getRunMode() {
		return runMode;
	}

	public void setRunMode(String runMode) {
		this.runMode = runMode;
	}

	public String getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(String beginTime) {
		this.beginTime = beginTime;
	}

	public Integer getScheduletype() {
		return scheduletype;
	}

	public void setScheduletype(Integer scheduletype) {
		this.scheduletype = scheduletype;
	}

	public String getCycleMode() {
		return cycleMode;
	}

	public void setCycleMode(String cycleMode) {
		this.cycleMode = cycleMode;
	}

	public String getRunDate() {
		return runDate;
	}

	public void setRunDate(String runDate) {
		this.runDate = runDate;
	}

	public Integer getEndTimeType() {
		return endTimeType;
	}

	public void setEndTimeType(Integer endTimeType) {
		this.endTimeType = endTimeType;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getCycleModeMonth() {
		return cycleModeMonth;
	}

	public void setCycleModeMonth(String cycleModeMonth) {
		this.cycleModeMonth = cycleModeMonth;
	}

	public String getRunCluster() {
		return runCluster;
	}

	public void setRunCluster(String runCluster) {
		this.runCluster = runCluster;
	}
}
