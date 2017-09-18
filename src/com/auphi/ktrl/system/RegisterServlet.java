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
package com.auphi.ktrl.system;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.auphi.ktrl.system.organizer.bean.OrganizerBean;
import com.auphi.ktrl.system.organizer.util.OrganizerUtil;
import com.auphi.ktrl.system.repository.util.RepositoryUtil;
import com.auphi.ktrl.util.Constants;
import com.auphi.ktrl.util.CreateVerifyCode;

/**
 * Servlet implementation class LoginServlet
 */
public class RegisterServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RegisterServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		String action = request.getParameter(Constants.PARAM_ACTION) ;
		if ("register".equals(action)) {
			OrganizerBean orgBean = OrganizerUtil.getOrganizerBean(request);
			boolean success = false;
			
			boolean checkName = OrganizerUtil.checkName(orgBean.getOrganizer_name());
			boolean checkEmail = OrganizerUtil.checkEmail(orgBean.getOrganizer_email());
			if(!checkName || !checkEmail){
				request.setAttribute("errMsg", "已注册，请勿多次提交！");
				request.setAttribute("email", orgBean.getOrganizer_email());
				RequestDispatcher dispatcher = request.getRequestDispatcher("success.jsp"); 
	            dispatcher.forward(request, response); 
			}else {
				try{
					success = OrganizerUtil.register(orgBean, request);
				}catch(Exception e){
					e.printStackTrace();
				}
	            
				if(success){
					request.setAttribute("email", orgBean.getOrganizer_email());
					RequestDispatcher dispatcher = request.getRequestDispatcher("success.jsp"); 
		            dispatcher.forward(request, response); 
				}else {
					request.setAttribute("errMsg", "注册失败，请检查输入！");
					request.setAttribute("orgBean", orgBean);
					RequestDispatcher dispatcher = request.getRequestDispatcher("register.jsp"); 
		            dispatcher.forward(request, response); 
				}
			}
        } else if("activation".equals(action)) {
        	String email = request.getParameter("email");
        	String verifyCode = request.getParameter("verifyCode");
        	OrganizerBean orgBean = OrganizerUtil.getOrgByEmail(email);
        	boolean success = false;
        	if(orgBean.getOrganizer_status() == OrganizerUtil.STATUS_ACTIVE){
        		success = true;
        	}else {
        		success = OrganizerUtil.activation(email, verifyCode);
            	if(success){
                	RepositoryUtil.createRepDB(orgBean);
                	RepositoryUtil.createRepository(orgBean);
            	}
        	}
        	
           	response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0L);
            response.setContentType("text/html; charset=UTF-8");
            if(success){
            	response.getWriter().write("激活成功！<a href=\"" + request.getContextPath() + "\">返回登录</a>");
            }else {
            	response.getWriter().write("激活失败！");
            }
            response.getWriter().close();
        } else if("checkName".equals(action)) {
			String orgName = request.getParameter("orgName")==null?"":new String(request.getParameter("orgName").getBytes("ISO-8859-1"), "UTF-8");
			boolean result = OrganizerUtil.checkName(orgName);
           	response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0L);
            response.setContentType("text/html; charset=UTF-8");
            response.getWriter().write(String.valueOf(result));
            response.getWriter().close();
        } else if("checkEmail".equals(action)) {
        	String email = request.getParameter("email");
			boolean result = OrganizerUtil.checkEmail(email);
           	response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0L);
            response.setContentType("text/html; charset=UTF-8");
            response.getWriter().write(String.valueOf(result));
            response.getWriter().close();
        } else if("createVerifyCode".equals(action)){
        	response.setContentType("image/jpeg");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);
            HttpSession session = request.getSession();
            
            CreateVerifyCode vCode = new CreateVerifyCode(100,30,5,10);
            session.setAttribute("srcVerifyCode", vCode.getCode());
            vCode.write(response.getOutputStream());
        } else if("checkVerifyCode".equals(action)){
        	String verifyCode = request.getParameter("verifyCode");
        	String srcVerifyCode = request.getSession().getAttribute("srcVerifyCode")==null?"":request.getSession().getAttribute("srcVerifyCode").toString();
        	String result = "false";
        	if(srcVerifyCode.equals(verifyCode)){
        		result = "true";
        	}
        	response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0L);
            response.setContentType("text/html; charset=UTF-8");
            response.getWriter().write(result);
            response.getWriter().close();
        } else if("sendActivitionEmail".equals(action)){
        	String email = request.getParameter("email");
        	OrganizerBean orgBean = OrganizerUtil.getOrgByEmail(email);
			String result = OrganizerUtil.sendActivationMail(orgBean, request);
           	response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0L);
            response.setContentType("text/html; charset=UTF-8");
            response.getWriter().write(result);
            response.getWriter().close();
        }
	}
}
