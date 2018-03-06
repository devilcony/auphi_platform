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

import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import com.auphi.data.hub.core.struct.BaseDto;
import com.auphi.data.hub.core.struct.Dto;
import com.auphi.data.hub.domain.UserInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.auphi.data.hub.core.util.CloudUtils;
import com.auphi.data.hub.core.util.SessionContainer;
import com.auphi.data.hub.dao.SystemDao;
import com.auphi.data.hub.dao.impl.SystemDaoImpl;


/**
 * Session监听器 完成对Seesion会话资源的实时监控
 * @author zhangjiafeng
 *
 */
public class SessionListener implements HttpSessionListener{

	private static Log log = LogFactory.getLog(SessionListener.class);
	
	// 集合对象，保存session对象的引用
	private static Map<String,Object> ht = new java.util.concurrent.ConcurrentHashMap<String, Object>();

	/**
	 * 实现HttpSessionListener接口，完成session创建事件控制
	 * 说明：此时的Session状态为无效会话，只有用户成功登录系统后才将此Session写入EAHTTPSESSION表作为有效SESSION来管理
	 */
	public void sessionCreated(HttpSessionEvent event) {
		SessionContainer sessionContainer =  (SessionContainer)event.getSession().getAttribute("SessionContainer");
		if(sessionContainer != null){
			UserInfo userInfo = sessionContainer.getUserInfo();
			if(userInfo != null){
				addSession(event.getSession(),userInfo);
			}
		}
		
	}

	/**
	 * 实现HttpSessionListener接口，完成session销毁事件控制
	 */
	public void sessionDestroyed(HttpSessionEvent event) {
		HttpSession session = event.getSession();
		SessionContainer sessionContainer =  (SessionContainer)session.getAttribute("SessionContainer");
		if(sessionContainer != null){
			sessionContainer.setUserInfo(null); //配合RequestFilter进行拦截
			sessionContainer.cleanUp();
		}
		WebApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext(event.getSession().getServletContext());
		SystemDao systemDao = wac.getAutowireCapableBeanFactory().getBean(SystemDaoImpl.class);
		Dto dto = new BaseDto();
		dto.put("sessionid", session.getId());
//		systemDao.delete("Monitor.deleteHttpSession", dto);
		ht.remove(session.getId());
		log.info("销毁了一个Session连接:" + session.getId() + " " + CloudUtils.getCurrentTime());
	}
	
	/**
	 * 增加一个有效Session
	 * @param session
	 */
	public static void addSession(HttpSession session, UserInfo userInfo) {
		ht.put(session.getId(), session);
		WebApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext(session.getServletContext());
		SystemDao systemDao = wac.getAutowireCapableBeanFactory().getBean(SystemDaoImpl.class);
//		UserInfo usInfo = (UserInfo)systemDao.queryForObject("Monitor.queryHttpSessionsByID", session.getId());
//		if(CloudUtils.isEmpty(usInfo)){
//			systemDao.save("Monitor.saveHttpSession", userInfo);
//		}
	}

	/**
	 * 返回全部session对象集合
	 * @return
	 */
	public static Iterator getSessions() {
		return ht.values().iterator();
	}

	/**
	 * 依据session id返回指定的session对象
	 * @param sessionId
	 * @return
	 */
	public static HttpSession getSessionByID(String sessionId) {
		return (HttpSession) ht.get(sessionId);
	}

}
