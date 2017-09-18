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

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.web.context.WebApplicationContext;

/**
 * SpringBwan加载器<br>
 * (1)、使用此加载器可以获得一个Spring容器的ApplicationContext实例,通过此实例你就可以方便的使用getBean()
 * 方法获取SpringBean.<br>
 * (2)、您也可以直接通过我们提供的getSpringBean()方法活得SpringBean。
 * 
 * @author XiongChun
 * @since 2009-07-22
 */
public class SpringBeanLoader{

	private static SpringBeanLoader loader;
	
	private SpringBeanLoader(){
		
	}
	
	private WebApplicationContext wac;
	
	public static SpringBeanLoader getInstance(){
		if(loader == null){
			loader = new SpringBeanLoader();
		}
		return loader;
	}

	
	public void setWac(WebApplicationContext wac){
		this.wac = wac;
	}

	/**
	 * 获取一个SpringBean服务
	 * 
	 * @param pBeanId
	 *            Spring配置文件名中配置的SpringID号
	 * @return Object 返回的SpringBean实例
	 */
	public Object getSpringBean(Class t) {
		Object springBean = null;
		try {
			springBean = wac.getBean(t);
		} catch (NoSuchBeanDefinitionException e) {
			e.printStackTrace();
		}
		return springBean;
	}
	
	public Object getBean(String beanName) {
		Object springBean = null;
		try {
			springBean = wac.getBean(beanName);
		} catch (NoSuchBeanDefinitionException e) {
			e.printStackTrace();
		}
		return springBean;
	}
	

}
