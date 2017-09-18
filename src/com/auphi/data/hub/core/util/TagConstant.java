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
package com.auphi.data.hub.core.util;

public class TagConstant {
	/**
	 * Ext运行模式<br>
	 * run:生产模式
	 */
	public static final String Ext_Mode_Run = "run";
	
	/**
	 * Ext运行模式<br>
	 * run:调试模式
	 */
	public static final String Ext_Mode_debug = "debug";
	
	/**
	 * 系统运行模式
	 * 0:演示模式
	 */
	public static final String RUN_MODE_DEMO = "0";
	
	/**
	 * 系统运行模式
	 * 0:正常模式
	 */
	public static final String RUN_MODE_NORMAL = "1";
	
	/**
	 * 模板变量输出模式<br>
	 * on:打开
	 */
	public static final String Tpl_Out_On = "on";
	
	/**
	 * 模板变量输出模式<br>
	 * off:关闭
	 */
	public static final String Tpl_Out_Off = "off";
	
	/**
	 * JS头<br>
	 */
	public static final String SCRIPT_START = "<script type=\"text/javascript\">\n";
	
	/**
	 * JS尾<br>
	 */
	public static final String SCRIPT_END = "\n</script>";
}
