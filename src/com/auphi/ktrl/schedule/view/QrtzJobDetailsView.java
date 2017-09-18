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

import java.util.List;

public class QrtzJobDetailsView {
	private FastConfigView fastConfigView;
	private List<FieldMappingView> fieldMappingView;
	private DispatchingModeView dispatchingModeView;
	private String sourceDatabase;
	private String destDatabase;
	
	private String jobName;

	public FastConfigView getFastConfigView() {
		return fastConfigView;
	}

	public void setFastConfigView(FastConfigView fastConfigView) {
		this.fastConfigView = fastConfigView;
	}



	public List<FieldMappingView> getFieldMappingView() {
		return fieldMappingView;
	}

	public void setFieldMappingView(List<FieldMappingView> fieldMappingView) {
		this.fieldMappingView = fieldMappingView;
	}

	public DispatchingModeView getDispatchingModeView() {
		return dispatchingModeView;
	}

	public void setDispatchingModeView(DispatchingModeView dispatchingModeView) {
		this.dispatchingModeView = dispatchingModeView;
	}

	
	public String getSourceDatabase() {
		return sourceDatabase;
	}

	public void setSourceDatabase(String sourceDatabase) {
		this.sourceDatabase = sourceDatabase;
	}

	public String getDestDatabase() {
		return destDatabase;
	}

	public void setDestDatabase(String destDatabase) {
		this.destDatabase = destDatabase;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

}
