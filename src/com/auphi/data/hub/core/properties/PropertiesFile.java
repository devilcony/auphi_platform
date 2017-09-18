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
package com.auphi.data.hub.core.properties;

/**
 * Properties文件类型
 * @author zhangjiafeng
 * @since 2009-08-2
 */
public interface PropertiesFile {
	/**
	 * Properties文件类型<br>
	 * EREDBOS对应global.eredbos.properties属性文件
	 */
	public static final String CC2 = "cc2";
	
	/**
	 * Properties文件类型<br>
	 * EREDBOS对应global.myconfig.properties属性文件
	 */
	public static final String APP = "app";
	
	/**
	 * Properties文件类型<br>
	 * 对应mis.odb.properties属性文件
	 */
	public static final String ODB = "odb";
}
