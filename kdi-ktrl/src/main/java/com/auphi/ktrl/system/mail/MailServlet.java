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
package com.auphi.ktrl.system.mail;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.auphi.ktrl.system.mail.bean.MailBean;
import com.auphi.ktrl.system.mail.util.MailUtil;

/**
 * Servlet implementation class MailServlet
 */
public class MailServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	public static String param_action = "action";
	public static String param_smtp_server = "smtp_server";
	public static String param_smtp_port = "smtp_port";
	public static String param_user_name = "user_name";
	public static String param_passwd = "passwd";
       
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request,response) ;
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String action = request.getParameter(param_action);
		
		if("manage".equals(action)){
			MailBean mailBean = MailUtil.getMailConfig();
			
			request.setAttribute("mailBean", mailBean);
			
			RequestDispatcher dispatcher = request.getRequestDispatcher("modules/system/mailmanage.jsp"); 
            dispatcher.forward(request, response); 
		}else if("save".equals(action)){
			MailBean mailBean = getMailBean(request);
			MailUtil.saveMailConfig(mailBean);
			
			response.sendRedirect("mail?action=manage");
		}else if("validate".equals(action)){
			MailBean mailBean = getMailBean(request);
			boolean validate = MailUtil.valiSMTP(mailBean);
			
			response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0L);
            response.setContentType("text/html; charset=UTF-8");
            response.getWriter().write(String.valueOf(validate));
            response.getWriter().close();
		}
	}

	private MailBean getMailBean(HttpServletRequest request)
    {
        MailBean mailBean = new MailBean() ;
        
        mailBean.setSmtp_server(request.getParameter(param_smtp_server));
        mailBean.setSmtp_port(request.getParameter(param_smtp_port)==null?25:Integer.parseInt(request.getParameter(param_smtp_port)));
        mailBean.setUser_name(request.getParameter(param_user_name));
        mailBean.setPasswd(request.getParameter(param_passwd));
        
        return mailBean ;
    }
}

