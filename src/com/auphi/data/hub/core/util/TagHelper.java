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

import javax.servlet.jsp.tagext.BodyContent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * JSP自定义标签内部使用的辅助工具类
 * 
 * @author zhangjiafeng
 *
 */
public class TagHelper {
	private static Log log = LogFactory.getLog(TagHelper.class);
	
	/**
	 * 获取模板路径
	 * @param pPath 标签实现类的Java包路径
	 * @return 返回模板路径
	 */
	public static String getTemplatePath(String pPath){
		if(CloudUtils.isEmpty(pPath))
			return "";
		String templatePath = "";
		String path = pPath.replace('.', '/');
		String packageUnits[] = path.split("/");
		String className = packageUnits[packageUnits.length - 1];
		templatePath = path.substring(0, path.length() - className.length());
		templatePath += "template/" + className + ".tpl";
		log.debug("模板文件路径:" + templatePath);
		return templatePath;
	}
	
	/**
	 * 获取模板路径
	 * @param pPath 标签实现类的Java包路径
	 * @return 返回模板路径
	 */
	public static String getTemplatePath(String pPath,String pFileName){
		if(CloudUtils.isEmpty(pPath))
			return "";
		String templatePath = "";
		String path = pPath.replace('.', '/');
		String packageUnits[] = path.split("/");
		String className = packageUnits[packageUnits.length - 1];
		templatePath = path.substring(0, path.length() - className.length());
		templatePath += "template/" + pFileName;
		log.debug("模板文件路径:" + templatePath);
		return templatePath;
	}
	
	/**
	 * 对BodyContent进行格式处理
	 * @param pBodyContent 传入的BodyContent对象
	 * @return 返回处理后的BodyContent字符串对象
	 */
	public static String formatBodyContent(BodyContent pBodyContent){
		if(CloudUtils.isEmpty(pBodyContent))
			return "";
		return pBodyContent.getString().trim();
	}
	
	/**
	 * 对字符串模板中的特殊字符进行处理
	 * @param pStr 传入的字符串模板
	 * @return 返回处理后的字符串
	 */
	public static String replaceStringTemplate(String pStr){
		if(CloudUtils.isEmpty(pStr))
			return "";
		pStr = pStr.replace('*','\"');

		return pStr;
	}
	
	/**
	 * 对模板字符型变量进行空校验
	 * @param pString
	 * @return
	 */
	public static String checkEmpty(String pString){
		return CloudUtils.isEmpty(pString) ? TagConstant.Tpl_Out_Off : pString;
	}

}
