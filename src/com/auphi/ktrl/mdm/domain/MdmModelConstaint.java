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

public class MdmModelConstaint {

	private Integer id_constaint;

	private Integer constaint_order;
	
	private Integer constaint_type;
	
	private String constaint_name;
	
	private Integer id_attribute;
	
	private Integer reference_id_model;
	
	private String reference_id_model_show;
	
	private Integer reference_id_attribute;
	
	private String reference_id_attribute_show;
	
	private Integer alias_table_flag;
	
	private String alias_table_flag_show;
	
	private String id_attributes;
	
	private String id_attributes_show;

	public Integer getId_constaint() {
		return id_constaint;
	}

	public void setId_constaint(Integer id_constaint) {
		this.id_constaint = id_constaint;
	}

	public Integer getConstaint_order() {
		return constaint_order;
	}

	public void setConstaint_order(Integer constaint_order) {
		this.constaint_order = constaint_order;
	}

	public Integer getConstaint_type() {
		return constaint_type;
	}

	public void setConstaint_type(Integer constaint_type) {
		this.constaint_type = constaint_type;
	}

	public String getConstaint_name() {
		return constaint_name;
	}

	public void setConstaint_name(String constaint_name) {
		this.constaint_name = constaint_name;
	}

	public Integer getId_attribute() {
		return id_attribute;
	}

	public void setId_attribute(Integer id_attribute) {
		this.id_attribute = id_attribute;
	}

	public Integer getReference_id_model() {
		return reference_id_model;
	}

	public void setReference_id_model(Integer reference_id_model) {
		this.reference_id_model = reference_id_model;
	}

	public Integer getReference_id_attribute() {
		return reference_id_attribute;
	}

	public void setReference_id_attribute(Integer reference_id_attribute) {
		this.reference_id_attribute = reference_id_attribute;
	}

	public Integer getAlias_table_flag() {
		return alias_table_flag;
	}

	public void setAlias_table_flag(Integer alias_table_flag) {
		this.alias_table_flag = alias_table_flag;
	}

	public String getReference_id_model_show() {
		return reference_id_model_show;
	}

	public void setReference_id_model_show(String reference_id_model_show) {
		this.reference_id_model_show = reference_id_model_show;
	}

	public String getReference_id_attribute_show() {
		return reference_id_attribute_show;
	}

	public void setReference_id_attribute_show(String reference_id_attribute_show) {
		this.reference_id_attribute_show = reference_id_attribute_show;
	}

	public String getId_attributes() {
		return id_attributes;
	}

	public void setId_attributes(String id_attributes) {
		this.id_attributes = id_attributes;
	}

	public String getId_attributes_show() {
		return id_attributes_show;
	}

	public void setId_attributes_show(String id_attributes_show) {
		this.id_attributes_show = id_attributes_show;
	}

	public String getAlias_table_flag_show() {
		return alias_table_flag_show;
	}

	public void setAlias_table_flag_show(String alias_table_flag_show) {
		this.alias_table_flag_show = alias_table_flag_show;
	}


}
