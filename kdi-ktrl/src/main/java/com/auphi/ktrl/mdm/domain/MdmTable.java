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

public class MdmTable {
	
	private Integer id_table;
	
	private Integer id_model;
	
	private String id_model_show;
	
	private Integer id_database;
	
	private String id_database_show;
	
	private String schema_name;
	
	private String table_name;

	public Integer getId_table() {
		return id_table;
	}

	public void setId_table(Integer id_table) {
		this.id_table = id_table;
	}

	public Integer getId_model() {
		return id_model;
	}

	public void setId_model(Integer id_model) {
		this.id_model = id_model;
	}

	public Integer getId_database() {
		return id_database;
	}

	public void setId_database(Integer id_database) {
		this.id_database = id_database;
	}

	public String getSchema_name() {
		return schema_name;
	}

	public void setSchema_name(String schema_name) {
		this.schema_name = schema_name;
	}

	public String getTable_name() {
		return table_name;
	}

	public void setTable_name(String table_name) {
		this.table_name = table_name;
	}

	public String getId_model_show() {
		return id_model_show;
	}

	public void setId_model_show(String id_model_show) {
		this.id_model_show = id_model_show;
	}

	public String getId_database_show() {
		return id_database_show;
	}

	public void setId_database_show(String id_database_show) {
		this.id_database_show = id_database_show;
	}
	


}
