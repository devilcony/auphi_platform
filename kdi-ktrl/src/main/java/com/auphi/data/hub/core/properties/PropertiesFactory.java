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

import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.auphi.data.hub.core.struct.BaseDto;
import com.auphi.data.hub.core.struct.Dto;
import com.auphi.data.hub.core.util.CloudConstants;


/**
 * Properties文件静态工厂
 * 
 * @author zhangjiafeng
 * 
 */
public class PropertiesFactory {
	private static Log log = LogFactory.getLog(PropertiesFactory.class);
	/**
	 * 属性文件实例容器
	 */
	private static Dto container = new BaseDto();
	
	static{
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			if (classLoader == null) {
				classLoader = PropertiesFactory.class.getClassLoader();
				}
			//加载属性文件global.eredbos.properties
			try {
			  InputStream is = classLoader.getResourceAsStream("mis.cc2.properties");
			  PropertiesHelper ph = new PropertiesHelper(is);
			  container.put(PropertiesFile.CC2, ph);
			  } catch (Exception e1) {
			  log.error(CloudConstants.Exception_Head + "加载属性文件mis.cc2.properties出错!");
			  e1.printStackTrace();
			  }
		     //加载属性文件global.myconfig.properties
			 try {
				InputStream is = classLoader.getResourceAsStream("mis.app.properties");
				PropertiesHelper ph = new PropertiesHelper(is);
				container.put(PropertiesFile.APP, ph);
			 } catch (Exception e1) {
				log.error(CloudConstants.Exception_Head + "加载属性文件mis.app.properties出错!");
				e1.printStackTrace();
			 }
			 
			 //加载属性文件mis.odb.properties
			 try {
				InputStream is = classLoader.getResourceAsStream("mis.odb.properties");
				PropertiesHelper ph = new PropertiesHelper(is);
				container.put(PropertiesFile.ODB, ph);
			 } catch (Exception e1) {
				log.error(CloudConstants.Exception_Head + "加载属性文件mis.odb.properties出错!");
				e1.printStackTrace();
			}
	}
	
    /**
     * 获取属性文件实例
     * @param pFile 文件类型
     * @return 返回属性文件实例
     */
	public static PropertiesHelper getPropertiesHelper(String pFile){
		PropertiesHelper ph = (PropertiesHelper)container.get(pFile);
		return ph;
	}
}
