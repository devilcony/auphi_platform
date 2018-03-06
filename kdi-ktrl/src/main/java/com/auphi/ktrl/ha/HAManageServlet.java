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
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.auphi.ktrl.ha.bean.HAClusterBean;
import com.auphi.ktrl.ha.bean.ServerStatusBean;
import com.auphi.ktrl.ha.util.HAClusterUtil;
import com.auphi.ktrl.ha.util.SlaveServerUtil;
import com.auphi.ktrl.util.PageList;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver;

/**
 * Servlet implementation class HAManageServlet
 */
public class HAManageServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	public static String param_action = "action";
	public static String parameter_page = "page";
	public static String parameter_pagelist = "pageList";
       
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request,response) ;
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String action = request.getParameter(param_action);
		
		int page = request.getParameter(parameter_page)==null?1:Integer.parseInt(request.getParameter("page"));
		
		if("list".equals(action)){
			PageList pageList = HAClusterUtil.findAllClusters(page);
            request.setAttribute(parameter_pagelist, pageList);
            
            RequestDispatcher dispatcher = request.getRequestDispatcher("modules/ha/haclusterlist.jsp"); 
            dispatcher.forward(request, response); 
		}else if("insert".equals(action)){
			HAClusterBean haClusterBean = getFromRequest(request);
			
			HAClusterUtil.addCluster(haClusterBean);
			
			response.sendRedirect("hamanage?action=list");
		}else if("beforeUpdate".equals(action)){
			int id_cluster = request.getParameter("id_cluster")==null?0:Integer.parseInt(request.getParameter("id_cluster"));
			
			HAClusterBean haClusterBean = HAClusterUtil.getCluster(id_cluster);
			
			XStream xstream = new XStream(new JettisonMappedXmlDriver());
			xstream.alias("item", HAClusterBean.class);
			
			response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0L);
            response.setContentType("text/html; charset=UTF-8");
            response.getWriter().write(xstream.toXML(haClusterBean));
            response.getWriter().close();
		}else if("update".equals(action)){
			HAClusterBean haClusterBean = getFromRequest(request);
			
			HAClusterUtil.updateCluster(haClusterBean);
			
			response.sendRedirect("hamanage?action=list");
		}else if("delete".equals(action)){
			String sel_ids = request.getParameter("sel_ids");
			
			HAClusterUtil.deleteCluster(sel_ids);
			
			response.sendRedirect("hamanage?action=list");
		}else if("checkExist".equals(action)){
			String cluster_id = request.getParameter("cluster_id");
			String cluster_name = request.getParameter("name_cluster");
			String slave_ids = request.getParameter("slave_ids");
			
			String response_text = "";
			
			if(HAClusterUtil.nameExists(cluster_name, cluster_id)){
				response_text = "nameExist";
			}else {
				String slaves_in_use = HAClusterUtil.getSlavesInUse(slave_ids, cluster_id);
				response_text = slaves_in_use;
			}
			
			response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0L);
            response.setContentType("text/html; charset=UTF-8");
            response.getWriter().write(response_text);
            response.getWriter().close();
		}else if("getSlavesNotUsed".equals(action)){
			String cluster_id = request.getParameter("cluster_id");
			String[] slavesNotUsed = HAClusterUtil.getSlavesNotUsed(cluster_id);
			
			XStream xstream = new XStream(new JettisonMappedXmlDriver());
			xstream.alias("item", String[].class);
			
			response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0L);
            response.setContentType("text/html; charset=UTF-8");
            response.getWriter().write(xstream.toXML(slavesNotUsed));
            response.getWriter().close();
		}else if("getSlaveStatus".equals(action)){
			String id_cluster = request.getParameter("id_cluster");
			
			List<ServerStatusBean> serverStatusList = SlaveServerUtil.getServerStatusFromCluster(id_cluster);
			StringBuffer json = new StringBuffer();
			json.append("{\"serverStatus\":[");
			for(int i=0;i<serverStatusList.size();i++){
				ServerStatusBean serverStatus = serverStatusList.get(i);
				String is_running_image = "<img src=\'images//icons//delete.gif\'></div>";
				if(serverStatus.getIs_running() == 1){
					is_running_image = "<img src=\'images//icons//accept.gif\'></div>";
				}
				if(i != 0){
					json.append(",");
				}
				json.append("{")
				.append("\"id_slave\":")
				.append(serverStatus.getId_slave())
				.append(",\"name_slave\":\"")
				.append(serverStatus.getName_slave())
				.append("\",\"is_running\":\"")
				.append(is_running_image)
				.append("\",\"cpu_usage\":\"")
				.append(serverStatus.getCpu_usage())
				.append("\",\"memory_usage\":\"")
				.append(serverStatus.getMemory_usage())
				.append("\",\"running_jobs\":")
				.append(serverStatus.getRunning_jobs_num())
				.append("}");
			}
			json.append("]}");
//			System.out.println(json.toString());
			
			response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0L);
            response.setContentType("text/html; charset=UTF-8");
            response.getWriter().write(json.toString());
            response.getWriter().close();
		}
	}

	private HAClusterBean getFromRequest(HttpServletRequest request){
		HAClusterBean haClusterBean = new HAClusterBean();
		String id_cluster = request.getParameter("id_cluster");
		int id_cluster_int = id_cluster==null || "".equals(id_cluster) ? 0 :Integer.parseInt(id_cluster);
		haClusterBean.setId_cluster(id_cluster_int);
		String[] slaves = new String[2];
		try{
			haClusterBean.setName(request.getParameter("cluster_name")==null?"":new String(request.getParameter("cluster_name").getBytes("ISO8859-1"), "UTF-8"));
			slaves[0] = request.getParameter("slave_ids");
			slaves[1] = request.getParameter("slave_names")==null?"":new String(request.getParameter("slave_names").getBytes("ISO8859-1"), "UTF-8");
		}catch(Exception e){
			e.printStackTrace();
		}
		haClusterBean.setBase_port(request.getParameter("base_port"));
		haClusterBean.setSockets_buffer_size(request.getParameter("sockets_buffer_size"));
		haClusterBean.setSockets_flush_interval(request.getParameter("sockets_flush_interval"));
		haClusterBean.setSockets_compressed(request.getParameter("sockets_compressed_radio"));
		haClusterBean.setDynamic_cluster(request.getParameter("dynamic_cluster_radio"));
		haClusterBean.setSlaves(slaves);
		
		return haClusterBean;
	}
}
