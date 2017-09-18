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
package com.auphi.data.hub.listener;

import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.auphi.data.hub.core.struct.BaseDto;
import com.auphi.data.hub.core.util.CloudUtils;
import com.auphi.data.hub.core.util.SpringBeanLoader;
import com.auphi.data.hub.dao.SystemDao;
import com.auphi.data.hub.dao.impl.SystemDaoImpl;

/**
 * 系统启动初始化监听器
 * @author mac
 *
 */
public class SystemInitListener implements ServletContextListener {
	
	private static Log log = LogFactory.getLog(SystemInitListener.class);
	private boolean success = true;
	private ApplicationContext wac = null;

	public void contextDestroyed(ServletContextEvent sce) {

	}

	public void contextInitialized(ServletContextEvent sce) {
		systemStartup(sce.getServletContext());
	}

	/**
	 * 应用平台启动
	 */
	private void systemStartup(ServletContext servletContext) {
		long start = System.currentTimeMillis();
		log.info("********************************************");
		log.info("北京傲飞商智软件有限公司...");
		log.info("********************************************");
		log.info("傲飞数据整合平台...");
		log.info("系统开始启动字典装载程序...");
		log.info("开始加载字典...");
		WebApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
		SpringBeanLoader.getInstance().setWac(wac);
		SystemDao systemDao = wac.getAutowireCapableBeanFactory().getBean(SystemDaoImpl.class);
//		List codeList = null;
//		try {
//			codeList = systemDao.queryForList("Resource.getCodeViewList");
//			log.info("字典加载成功!");
//		} catch (Exception e) {
//			success = false;
//			log.error("字典加载失败!");
//			e.printStackTrace();
//		}
//		if(success){
//			systemDao.delete("Monitor.deleteHttpSession", new BaseDto());
//		}
//		servletContext.setAttribute("EACODELIST", codeList);
//		log.info("系统开始启动全局参数表装载程序...");
//		log.info("开始加载全局参数表...");
//		List paramList = null;
//		try {
//			paramList = systemDao.queryForList("Resource.getParamList");
//			log.info("全局参数表加载成功!");
//		} catch (Exception e) {
//			success = false;
//			log.error("全局参数表加载失败!");
//			e.printStackTrace();
//		}
//		servletContext.setAttribute("EAPARAMLIST", paramList);


		long timeSec = (System.currentTimeMillis() - start) / 1000;
		log.info("********************************************");
		if (success) {
			log.info("平台启动成功[" + CloudUtils.getCurrentTime() + "]");
			log.info("启动总耗时: " + timeSec / 60 + "分 " + timeSec % 60 + "秒 ");
		} else {
			log.error("平台启动失败[" + CloudUtils.getCurrentTime() + "]");
			log.error("启动总耗时: " + timeSec / 60 + "分" + timeSec % 60 + "秒");
		}
		log.info("********************************************");
	}

}
