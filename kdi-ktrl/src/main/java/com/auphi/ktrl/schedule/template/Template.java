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
package com.auphi.ktrl.schedule.template;


public interface Template {
	public static final String TEMPLATE_PATH = "/Template";
	public static final String JOB_NAME = "job";
	
	public static final String TEMPLATE_FIELDS = "fields";
	public static final String TEMPLATE_TABLE_NAME = "table_name";
	public static final String TEMPLATE_CONDITIONS = "conditions";
	
	/**
	 * get templateClassName(same to middlePath)
	 * @return
	 */
	public String getTemplateClassName();
	
	/**
	 * bind params to template
	 * @param params
	 * @throws Exception
	 */
	public void bind(String fastConfigJson, String fieldMappingJson) throws Exception;
	
	/**
	 * run template job
	 * @param monitorId
	 * @param execType
	 * @param remoteServer
	 * @param ha
	 * @return
	 * @throws Exception
	 */
	public boolean execute(int monitorId, int execType, String remoteServer, String ha) throws Exception;
}
