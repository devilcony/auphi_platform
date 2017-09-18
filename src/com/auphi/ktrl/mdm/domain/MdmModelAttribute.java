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

public class MdmModelAttribute {

	private Integer id_attribute;

	private Integer id_model;
	
	private Integer attribute_order;
	
	private String attribute_name;
	
	private Integer statistic_type;
	
	private String field_name;
	
	private Integer field_type;
	
	private String field_type_show;

	private Integer field_length;
	
	private Integer field_precision;
	
	private String is_primary;
	
	private String is_primary_show;

	public Integer getId_attribute() {
		return id_attribute;
	}

	public void setId_attribute(Integer id_attribute) {
		this.id_attribute = id_attribute;
	}

	public Integer getId_model() {
		return id_model;
	}

	public void setId_model(Integer id_model) {
		this.id_model = id_model;
	}

	public Integer getAttribute_order() {
		return attribute_order;
	}

	public void setAttribute_order(Integer attribute_order) {
		this.attribute_order = attribute_order;
	}

	public String getAttribute_name() {
		return attribute_name;
	}

	public void setAttribute_name(String attribute_name) {
		this.attribute_name = attribute_name;
	}

	public Integer getStatistic_type() {
		return statistic_type;
	}

	public void setStatistic_type(Integer statistic_type) {
		this.statistic_type = statistic_type;
	}

	public String getField_name() {
		return field_name;
	}

	public void setField_name(String field_name) {
		this.field_name = field_name;
	}

	public Integer getField_type() {
		return field_type;
	}

	public void setField_type(Integer field_type) {
		this.field_type = field_type;
	}

	public Integer getField_length() {
		return field_length;
	}

	public void setField_length(Integer field_length) {
		this.field_length = field_length;
	}

	public String getIs_primary() {
		return is_primary;
	}

	public void setIs_primary(String is_primary) {
		this.is_primary = is_primary;
	}

	public String getField_type_show() {
		return field_type_show;
	}

	public void setField_type_show(String field_type_show) {
		this.field_type_show = field_type_show;
	}

	public String getIs_primary_show() {
		return is_primary_show;
	}

	public void setIs_primary_show(String is_primary_show) {
		this.is_primary_show = is_primary_show;
	}

	public Integer getField_precision() {
		return field_precision;
	}

	public void setField_precision(Integer field_precision) {
		this.field_precision = field_precision;
	}

}
