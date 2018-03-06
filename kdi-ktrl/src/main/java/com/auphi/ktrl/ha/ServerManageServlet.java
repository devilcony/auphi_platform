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
package com.auphi.ktrl.ha;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.auphi.ktrl.ha.bean.SlaveServerBean;
import com.auphi.ktrl.ha.util.SlaveServerUtil;
import com.auphi.ktrl.util.Constants;
import com.auphi.ktrl.util.PageList;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver;

/**
 * Servlet implementation class ServerManageServlet
 */
public class ServerManageServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	public static String param_action = "action";
	public static String parameter_page = "page";
	public static String parameter_pagelist = "pageList";

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String action = request.getParameter(param_action);
		
		int page = request.getParameter(parameter_page)==null?1:Integer.parseInt(request.getParameter("page"));
		
		if("list".equals(action)){
			PageList pageList = SlaveServerUtil.findAllSlaveServers(page);
            request.setAttribute(parameter_pagelist, pageList);
            
            RequestDispatcher dispatcher = request.getRequestDispatcher("modules/ha/slaveserverlist.jsp"); 
            dispatcher.forward(request, response); 
		}else if("insert".equals(action)){
			SlaveServerBean slaveServerBean = getFromRequest(request);
			
			SlaveServerUtil.addSlaveServer(slaveServerBean);
			
			response.sendRedirect("servermanage?action=list");
		}else if("beforeUpdate".equals(action)){
			String id_slave = request.getParameter("id_slave");
			
			SlaveServerBean slaveServerBean = SlaveServerUtil.getSlaveServer(id_slave);
			
			XStream xstream = new XStream(new JettisonMappedXmlDriver());
			xstream.alias("item", SlaveServerBean.class);
			
			response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0L);
            response.setContentType("text/html; charset=UTF-8");
            response.getWriter().write(xstream.toXML(slaveServerBean));
            response.getWriter().close();
		}else if("update".equals(action)){
			SlaveServerBean slaveServerBean = getFromRequest(request);
			
			SlaveServerUtil.updateSlaveServer(slaveServerBean);
			
			response.sendRedirect("servermanage?action=list");
		}else if("delete".equals(action)){
			String sel_ids = request.getParameter("sel_ids");
			
			SlaveServerUtil.deleteSlaveServer(sel_ids);
			
			response.sendRedirect("servermanage?action=list");
		}else if("checkServerNameExist".equals(action)){
			String slave_name = request.getParameter("name_slave");
			String host_name = request.getParameter("host_name");
			String port = request.getParameter("port");
			String old_slave_id = request.getParameter("old_slave_id");
			
			String response_text = "";
			
			if(SlaveServerUtil.nameExists(slave_name, old_slave_id)){
				response_text = "nameExist";
			}else if(SlaveServerUtil.hostAndPortExists(host_name, port, old_slave_id)){
				response_text = "hostAndPortExist";
			}
			
			response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0L);
            response.setContentType("text/html; charset=UTF-8");
            response.getWriter().write(response_text);
            response.getWriter().close();
		}
	}

	private SlaveServerBean getFromRequest(HttpServletRequest request) {
		SlaveServerBean slaveServerBean = new SlaveServerBean();
		String id_slave = request.getParameter("id_slave");
		int id_slave_int = id_slave==null || "".equals(id_slave) ? 0 :Integer.parseInt(id_slave);
		slaveServerBean.setId_slave(id_slave_int);
		try{
			slaveServerBean.setName(request.getParameter("slave_name")==null?"":new String(request.getParameter("slave_name").getBytes("ISO8859-1"), "UTF-8"));
		}catch(Exception e){
			e.printStackTrace();
		}
		slaveServerBean.setNon_proxy_hosts(request.getParameter("proxy_hosts_radio"));
		slaveServerBean.setHost_name(request.getParameter("host_name"));
		slaveServerBean.setPort(request.getParameter("port"));
		slaveServerBean.setProxy_host_name(request.getParameter("proxy_host_name"));
		slaveServerBean.setProxy_port(request.getParameter("proxy_port"));
		slaveServerBean.setWeb_app_name(request.getParameter("web_app_name"));
		slaveServerBean.setUsername(Constants.get("HALoginUser")==null?"cluster":Constants.get("HALoginUser"));
		slaveServerBean.setPassword(Constants.get("HALoginPassword")==null?"cluster":Constants.get("HALoginPassword"));
		slaveServerBean.setMaster(request.getParameter("master_radio"));
		
		return slaveServerBean;
	}
}
