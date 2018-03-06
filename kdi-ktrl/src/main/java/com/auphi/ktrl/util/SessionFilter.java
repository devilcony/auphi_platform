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
package com.auphi.ktrl.util;

import org.apache.log4j.Logger;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;

/**
 * Servlet Filter implementation class SessionFilter
 */
public class SessionFilter implements Filter {

	private static Logger logger = Logger.getLogger(SessionFilter.class);

	private static final String[] IGNORE_URI = { "/login.jsp","/login","/login.do","/register.do"};


    /**
     * Default constructor. 
     */
    public SessionFilter() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		try{
			HttpServletRequest httpRequest = (HttpServletRequest)request;
			String servletPath = httpRequest.getServletPath();
			String user_id = httpRequest.getSession().getAttribute("user_id")==null?"":httpRequest.getSession().getAttribute("user_id").toString();
			int a = Arrays.binarySearch(IGNORE_URI, servletPath);

			if(!"".equals(user_id) || a > 0){
				// pass the request along the filter chain
				chain.doFilter(request, response);
			}else {
				//String errMsg = Messages.getString("Login.Jsp.Warn.SessionTimeout");
				//request.setAttribute("errMsg", errMsg);
				RequestDispatcher dispatcher = request.getRequestDispatcher("login.do");

				dispatcher.forward(request, response);
			}
		}catch(Exception e){
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
	}

}
