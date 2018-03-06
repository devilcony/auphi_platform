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

public class MdmRelConsAttr {

	private Integer id_rel_cons_attr;

	private Integer id_constaint;
	
	private Integer id_attribute;

	public Integer getId_rel_cons_attr() {
		return id_rel_cons_attr;
	}

	public void setId_rel_cons_attr(Integer id_rel_cons_attr) {
		this.id_rel_cons_attr = id_rel_cons_attr;
	}

	public Integer getId_constaint() {
		return id_constaint;
	}

	public void setId_constaint(Integer id_constaint) {
		this.id_constaint = id_constaint;
	}

	public Integer getId_attribute() {
		return id_attribute;
	}

	public void setId_attribute(Integer id_attribute) {
		this.id_attribute = id_attribute;
	}
	

}
