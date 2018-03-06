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

import java.util.HashMap;
import java.util.Map;

import com.auphi.data.hub.core.util.CloudConstants;

/**
 * 模板工厂
 * @author zhangjiafeng
 *
 */
public class TemplateEngineFactory {
	/**
	 * 引擎容器
	 */
	private static Map ENGINES = new HashMap();
	
	/**
	 * 实例化模板引擎并压入引擎容器
	 */
	static{
		if (isExistClass("org.apache.velocity.app.VelocityEngine")){
			VelocityTemplateEngine ve = new VelocityTemplateEngine();
			ENGINES.put(TemplateType.VELOCITY, ve);
		}else{
			//todo 支持其他模板引擎扩展
		}
	}
	
	/**
	 * 检查当前ClassLoader种,是否存在指定class
	 * @param pClass
	 * @return
	 */
	private static boolean isExistClass(String pClass) {
		try {
			Class.forName(pClass);
		} catch (ClassNotFoundException e) {
			return false;
		}
		return true;
	}
	
	/**
	 * 获取模板引擎实例
	 * @param pTemplateType 引擎类型
	 * @return 返回模板引擎实例
	 */
	public static TemplateEngine getTemplateEngine(TemplateType pType) {
		if (pType == null) {
			return null;
		}
		if (ENGINES.containsKey(pType) == false) {
			throw new IllegalArgumentException(CloudConstants.Exception_Head + "不支持的模板类别:" + pType.getType());
		}
		return (TemplateEngine) ENGINES.get(pType);
	}
}
