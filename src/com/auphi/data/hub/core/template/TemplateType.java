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
package com.auphi.data.hub.core.template;

public class TemplateType {
	/**
	 * 引擎类型
	 */
	private String type;
	
	/**
	 * 引擎描述
	 */
	private String description;
	
	/**
	 * Velocity引擎定义
	 */
	public static final TemplateType VELOCITY = new TemplateType("Velocity", "Velocity engine");

	/**
	 * 构造函数
	 * @param pType 引擎类型
	 * @param pDescription 引擎描述
	 */
	public TemplateType(String pType, String pDescription) {
		this.type = pType;
		this.description = pDescription;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
