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

import java.io.IOException;
import java.math.BigDecimal;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.auphi.data.hub.core.properties.PropertiesFile;
import com.auphi.data.hub.core.struct.Dto;
import com.auphi.data.hub.domain.UserInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.auphi.data.hub.core.idgenerator.IDHelper;
import com.auphi.data.hub.core.properties.PropertiesFactory;
import com.auphi.data.hub.core.properties.PropertiesHelper;
import com.auphi.data.hub.core.struct.BaseDto;
import com.auphi.data.hub.core.util.CloudConstants;
import com.auphi.data.hub.core.util.CloudUtils;
import com.auphi.data.hub.core.util.WebUtils;
import com.auphi.data.hub.dao.SystemDao;
import com.auphi.data.hub.dao.impl.SystemDaoImpl;


/**
 * 请求拦截器,对所有的请求操作进行拦截
 * 
 * @author zhangjiafeng
 *
 */
public class RequestInterceptor implements Filter {
	private Log log = LogFactory.getLog(RequestInterceptor.class);
	protected FilterConfig filterConfig;
	protected boolean enabled;
	
	private SystemDao systemDao;

	/**
	 * 构造
	 */
	public RequestInterceptor() {
		filterConfig = null;
		enabled = true;
	}

	/**
	 * 初始化
	 */
	public void init(FilterConfig pFilterConfig) throws ServletException {
		this.filterConfig = pFilterConfig;
		String value = filterConfig.getInitParameter("enabled");
		WebApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext(pFilterConfig.getServletContext());
		systemDao = wac.getAutowireCapableBeanFactory().getBean(SystemDaoImpl.class);
		if (CloudUtils.isEmpty(value)) {
			this.enabled = true;
		} else if (value.equalsIgnoreCase("true")) {
			this.enabled = true;
		} else {
			this.enabled = false;
		}
	}

	/**
	 * 过滤处理
	 */
	public void doFilter(ServletRequest pRequest, ServletResponse pResponse, FilterChain fc) throws IOException,
			ServletException {
		HttpServletRequest request = (HttpServletRequest) pRequest;
		HttpServletResponse response = (HttpServletResponse) pResponse;
		String ctxPath = request.getContextPath();
		String requestUri = request.getRequestURI();
		String uri = requestUri.substring(ctxPath.length());
		UserInfo userInfo = WebUtils.getSessionContainer(request).getUserInfo();
		BigDecimal costTime = null;
		PropertiesHelper pHelper = PropertiesFactory.getPropertiesHelper(PropertiesFile.CC2);
		String eventMonitorEnabel = pHelper.getValue("requestMonitor", "1");
		String postType = request.getParameter("postType");
		postType = CloudUtils.isEmpty(postType) ? CloudConstants.PostType_Normal : postType;
		if (postType.equals(CloudConstants.PostType_Nude)) {
			long start = System.currentTimeMillis();
			fc.doFilter(request, response);
			if (eventMonitorEnabel.equalsIgnoreCase(CloudConstants.EVENTMONITOR_ENABLE_Y)) {
				costTime = new BigDecimal(System.currentTimeMillis() - start);
				saveEvent(request, costTime);
			}
		} else {
			String isAjax = request.getHeader("x-requested-with");
			String requestUrl = request.getRequestURI();
			if(requestUrl.endsWith("/rest") ){
				log.info("外部系统调用对外数据服务接口,调用者的IP地址 :" + request.getRemoteAddr() + " 试图访问的URL:"+ request.getRequestURL().toString() );
			} else if (CloudUtils.isEmpty(userInfo) && !uri.contains("login") && enabled) {
//				if (CloudUtils.isEmpty(isAjax)) {
//					response.getWriter().write("<script type=\"text/javascript\">parent.location.href='" + ctxPath + "/login/init.shtml'</script>");
//					response.getWriter().flush();
//					response.getWriter().close();
//				} else {
//					response.sendError(CloudConstants.Ajax_Timeout);
//				}
//				log.warn("警告:非法的URL请求已被成功拦截,请求已被强制重定向到了登录页面.访问来源IP锁定:" + request.getRemoteAddr() + " 试图访问的URL:"
//						+ request.getRequestURL().toString() );
//				return;
			}
//			if (CloudUtils.isNotEmpty(isAjax) && !uri.contains("/login")) {
//				if (request.getParameter("loginuserid") != null && !request.getParameter("loginuserid").equals(userInfo.getUserid())) {
//					response.sendError(CloudConstants.Ajax_Session_Unavaliable);
//					log.error("当前会话和登录用户会话不一致,请求被重定向到了登录页面");
//					return;
//				}
//			}
			// if(){.... return;}
			long start = System.currentTimeMillis();
			fc.doFilter(request, response);
			if (eventMonitorEnabel.equalsIgnoreCase(CloudConstants.EVENTMONITOR_ENABLE_Y)) {
				costTime = new BigDecimal(System.currentTimeMillis() - start);
				saveEvent(request, costTime);
			}
		}
	}

	/**
	 * 写操作员事件表
	 * 
	 * @param request
	 */
	private void saveEvent(HttpServletRequest request, BigDecimal costTime) {
		UserInfo userInfo = WebUtils.getSessionContainer(request).getUserInfo();
		if (CloudUtils.isEmpty(userInfo)) {
			return;
		}
		String menuid = request.getParameter("menuid4Log");
		Dto dto = new BaseDto();
		dto.put("account", userInfo.getAccount());
		dto.put("activetime", CloudUtils.getCurrentTime());
		dto.put("userid", userInfo.getUserid());
		dto.put("username", userInfo.getUsername());
		dto.put("requestpath", request.getRequestURI());
		dto.put("methodname", request.getParameter("reqCode"));
		dto.put("eventid", IDHelper.getEventID(systemDao));
		dto.put("costtime", costTime);
		if (CloudUtils.isNotEmpty(menuid)) {
			String menuname = ((BaseDto) systemDao.queryForObject("Resource.queryEamenuByMenuID", menuid)).getAsString("menuname");
			String msg = userInfo.getUsername() + "[" + userInfo.getAccount() + "]打开了菜单[" + menuname + "]";
			dto.put("description", msg);
			log.info(msg);
		} else {
			String msg = userInfo.getUsername() + "[" + userInfo.getAccount() + "]访问了URI["
					+ request.getRequestURI() + "]";
			dto.put("description", msg);
			log.info(msg + ";请求路径[" + request.getRequestURI() + "]");
		}
		systemDao.save("Monitor.saveEvent", dto);


	}

	/**
	 * 销毁
	 */
	public void destroy() {
		filterConfig = null;
	}

}
