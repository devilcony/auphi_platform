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
package com.auphi.ktrl.mdm.domain;

/**
 * 主数据模型
 */
public class MdmModel {

	private Integer id_model;

	private String model_code;//模型编码
	
	private String model_name;
	
	private String model_desc;
	
	private String model_status;
	
	private String model_status_show;
	
	private String model_author;
	
	private String model_note;

	public Integer getId_model() {
		return id_model;
	}

	public void setId_model(Integer id_model) {
		this.id_model = id_model;
	}

	public String getModel_name() {
		return model_name;
	}

	public void setModel_name(String model_name) {
		this.model_name = model_name;
	}

	public String getModel_desc() {
		return model_desc;
	}

	public void setModel_desc(String model_desc) {
		this.model_desc = model_desc;
	}

	public String getModel_status() {
		return model_status;
	}

	public void setModel_status(String model_status) {
		this.model_status = model_status;
	}

	public String getModel_author() {
		return model_author;
	}

	public void setModel_author(String model_author) {
		this.model_author = model_author;
	}

	public String getModel_status_show() {
		return model_status_show;
	}

	public void setModel_status_show(String model_status_show) {
		this.model_status_show = model_status_show;
	}

	public String getModel_note() {
		return model_note;
	}

	public void setModel_note(String model_note) {
		this.model_note = model_note;
	}

	public String getModel_code() {
		return model_code;
	}

	public void setModel_code(String model_code) {
		this.model_code = model_code;
	}
}
