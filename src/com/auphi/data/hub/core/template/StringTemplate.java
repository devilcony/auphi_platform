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

/**
 * 字符串模板
 * 
 * @author zhangjiafeng
 *
 */
public class StringTemplate implements DefaultTemplate {

	/**
	 * 字符串模板内容
	 */
	private String resource;
	
	/**
	 * 构造函数
	 * @param pResource 字符串模板内容
	 */
	public StringTemplate(String pResource){
		this.resource = pResource;
	}
	
	/**
	 * 缺省构造函数
	 */
	public StringTemplate() {
	}

	/**
	 * 获取字符串模板内容
	 */
	public String getTemplateResource() {
		return getResource();
	}

	/**
	 * 设置字符串模板内容
	 */
	public void setTemplateResource(String pResource) {
		this.resource = pResource;
	}

	public String getResource() {
		return resource;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}

}
