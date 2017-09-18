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
package com.auphi.ktrl.schedule;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.auphi.ktrl.engine.KettleEngine;
import com.auphi.ktrl.engine.impl.KettleEngineImpl2_3;
import com.auphi.ktrl.engine.impl.KettleEngineImpl4_3;
import com.auphi.ktrl.ha.bean.HAClusterBean;
import com.auphi.ktrl.ha.bean.SlaveServerBean;
import com.auphi.ktrl.ha.util.HAClusterUtil;
import com.auphi.ktrl.ha.util.SlaveServerUtil;
import com.auphi.ktrl.i18n.Messages;
import com.auphi.ktrl.schedule.bean.ScheduleBean;
import com.auphi.ktrl.schedule.util.QuartzUtil;
import com.auphi.ktrl.schedule.util.ScheduleUtil;
import com.auphi.ktrl.system.repository.bean.RepositoryBean;
import com.auphi.ktrl.system.repository.util.RepositoryUtil;
import com.auphi.ktrl.system.user.bean.UserBean;
import com.auphi.ktrl.system.user.util.UserUtil;
import com.auphi.ktrl.util.PageList;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver;

/**
 * Servlet implementation class ScheduleMonitor
 */
public class ScheduleServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ScheduleServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String action = request.getParameter("action");
		int page = request.getParameter("page")==null?1:Integer.parseInt(request.getParameter("page"));
		String user_id = request.getSession().getAttribute("user_id")==null?"":request.getSession().getAttribute("user_id").toString();
		UserBean userBean = request.getSession().getAttribute("userBean")==null?null:(UserBean)request.getSession().getAttribute("userBean");
		String order= request.getParameter("order")==null?"DESC":request.getParameter("order");
		String orderby= request.getParameter("orderby")==null?"NEXT_FIRE_TIME":request.getParameter("orderby");
		String search_text = request.getParameter("search_text")==null?"":request.getParameter("search_text");
		String trigger_state=request.getParameter("trigger_state")==null?"":request.getParameter("trigger_state");

		if("index".equals(action)){
			//PageList pageList = QuartzUtil.findAll(page, user_id);
			PageList pageList = QuartzUtil.findAllSchedule(page, userBean, order, orderby, search_text, trigger_state);
			List<UserBean> listUsers = UserUtil.getUsers();
			List<HAClusterBean> listHACluster = HAClusterUtil.findAll();
			
			
			request.setAttribute("orderby", orderby);
			request.setAttribute("order", order);
			request.setAttribute("pageList", pageList);
			request.setAttribute("search_text", search_text);
			request.setAttribute("trigger_state", trigger_state);
			request.setAttribute("listUsers", listUsers);
			request.setAttribute("listHACluster", listHACluster);
			RequestDispatcher dispatcher = request.getRequestDispatcher("modules/schedule/list.jsp"); 
			dispatcher.forward(request, response); 
			
		}else if("list".equals(action)){//list
			//PageList pageList = QuartzUtil.findAll(page, user_id);
			PageList pageList = QuartzUtil.findAllSchedule(page, userBean, order, orderby, search_text, trigger_state);
			List<UserBean> listUsers = UserUtil.getUsersByOrg(userBean.getOrgId());
			List<HAClusterBean> listHACluster = HAClusterUtil.findAll();
			
			
			
			
			request.setAttribute("pageList", pageList);
			request.setAttribute("orderby", orderby);
			request.setAttribute("order", order);
			request.setAttribute("search_text", search_text);
			request.setAttribute("trigger_state", trigger_state);
			request.setAttribute("listUsers", listUsers);
			request.setAttribute("listHACluster", listHACluster);
			RequestDispatcher dispatcher = request.getRequestDispatcher("modules/schedule/list.jsp"); 
			dispatcher.forward(request, response); 
			
			
			//String jsonString = JsonHelper.encodeList2PageJson(pageList.getList(),totalCount,"yyyy-MM-dd H:m:s");	
//			response.setCharacterEncoding("UTF-8");
//			response.getWriter().write(jsonString);
//			response.getWriter().flush();
//			response.getWriter().close();
			
		}else if("add".equals(action)){//add schedule
			ScheduleBean scheduleBean = ScheduleUtil.createScheduleBeanFromRequest(request, userBean);
			scheduleBean.setUserId(user_id);
			QuartzUtil.create(scheduleBean);
			
			response.sendRedirect("schedule?action=list&page=" + page);
		}else if("beforeUpdate".equals(action)){//get data before update
			String jobName = request.getParameter("jobname")==null?"":request.getParameter("jobname");
			
			ScheduleBean scheduleBean = ScheduleUtil.getScheduleBeanByJobName(jobName, String.valueOf(userBean.getOrgId()));
			
			XStream xstream = new XStream(new JettisonMappedXmlDriver());
			xstream.alias("item", ScheduleBean.class);
			
			response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0L);
            response.setContentType("text/html; charset=UTF-8");
            response.getWriter().write(xstream.toXML(scheduleBean));
            response.getWriter().close();
		}else if("update".equals(action)){//update the schedule
			ScheduleBean scheduleBean = ScheduleUtil.createScheduleBeanFromRequest(request, userBean);
			String checked_job = request.getParameter("checked_job")==null?"":request.getParameter("checked_job");
			QuartzUtil.update(scheduleBean, checked_job, userBean);
			
			response.sendRedirect("schedule?action=list&page=" + page+ "&search_text=" + URLEncoder.encode(search_text, "UTF-8") );
		}else if("delete".equals(action)){//delete the schedule
			String checked_job = request.getParameter("checked_job")==null?"":new String(request.getParameter("checked_job").getBytes("ISO8859-1"), "UTF-8");
			
			QuartzUtil.delete(checked_job.split(","), userBean);
			
			response.sendRedirect("schedule?action=list");
		}else if("pause".equals(action)){
			String checked_job = request.getParameter("checked_job")==null?"":new String(request.getParameter("checked_job").getBytes("ISO8859-1"), "UTF-8");
			
			QuartzUtil.pause(checked_job.split(","), userBean);
			
			response.sendRedirect("schedule?action=list&page=" + page+"&orderby=" + orderby + "&order=" + order + "&search_text=" + URLEncoder.encode(search_text, "UTF-8") );
		}else if("resume".equals(action)){
			String checked_job = request.getParameter("checked_job")==null?"":new String(request.getParameter("checked_job").getBytes("ISO8859-1"), "UTF-8");
			
			QuartzUtil.resume(checked_job.split(","), userBean);
			
			response.sendRedirect("schedule?action=list&page=" + page+"&orderby=" + orderby + "&order=" + order + "&search_text=" + URLEncoder.encode(search_text, "UTF-8"));
		}else if("run".equals(action)){
			String checked_job = request.getParameter("checked_job")==null?"":new String(request.getParameter("checked_job").getBytes("ISO8859-1"), "UTF-8");
			
			QuartzUtil.execute(checked_job.split(","), userBean);
			
			response.sendRedirect("schedule?action=list&page=" + page+"&orderby=" + orderby + "&order=" + order + "&search_text=" + URLEncoder.encode(search_text, "UTF-8"));
		}else if("getReps".equals(action)){
			String version = request.getParameter("version")==null?"":request.getParameter("version");
			
			List<RepositoryBean> listReps = RepositoryUtil.getRepByVersionAndOrg(version, userBean.getOrgId());
			
			XStream xstream = new XStream(new JettisonMappedXmlDriver());
			xstream.alias("item", RepositoryBean.class);
			
			response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0L);
            response.setContentType("text/html; charset=UTF-8");
            response.getWriter().write(xstream.toXML(listReps));
            response.getWriter().close();
		}else if("getRepTree".equals(action)){
			String version = request.getParameter("version")==null?"":request.getParameter("version");
			String repository = request.getParameter("repository")==null?"":new String(request.getParameter("repository").getBytes("ISO8859-1"), "UTF-8");
			
			KettleEngine kettleEngine = null;
			
			if(KettleEngine.VERSION_2_3.equals(version)){
				kettleEngine = new KettleEngineImpl2_3();
			}else if(KettleEngine.VERSION_4_3.equals(version)){
				kettleEngine = new KettleEngineImpl4_3();
			}
			
			String repTreeJSON = kettleEngine.getRepTreeJSON(repository, user_id);
			
			kettleEngine = null;
			
			response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0L);
            response.setContentType("text/html; charset=UTF-8");
            response.getWriter().write(repTreeJSON);
            response.getWriter().close();
		}else if("checkRepLogin".equals(action)){
			int checkRepLogin = 0;
			
			String version = request.getParameter("version")==null?"":request.getParameter("version");
			String repository = request.getParameter("repository")==null?"":request.getParameter("repository");
			
			KettleEngine kettleEngine = null;
			
			if(KettleEngine.VERSION_2_3.equals(version)){
				kettleEngine = new KettleEngineImpl2_3();
			}else if(KettleEngine.VERSION_4_3.equals(version)){
				kettleEngine = new KettleEngineImpl4_3();
			}
			
			checkRepLogin = kettleEngine.checkRepLogin(repository);
			
			kettleEngine = null;
			
			response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0L);
            response.setContentType("text/html; charset=UTF-8");
            response.getWriter().write(String.valueOf(checkRepLogin));
            response.getWriter().close();
		}else if("checkJobExist".equals(action)){
			String jobname = request.getParameter("jobname")==null?"":request.getParameter("jobname");
			
			boolean jobname_exist = QuartzUtil.checkJobExist(jobname, userBean);
			
			response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0L);
            response.setContentType("text/html; charset=UTF-8");
            response.getWriter().write(String.valueOf(jobname_exist));
            response.getWriter().close();
		}else if("completedelete".equals(action)){
			String checked_job = request.getParameter("checked_job")==null?"":new String(request.getParameter("checked_job").getBytes("ISO8859-1"), "UTF-8");
			
			QuartzUtil.completedelete(checked_job.split(","), userBean);
			
			response.sendRedirect("schedule?action=list&page="+page+"&orderby=" + orderby + "&order=" + order + "&search_text=" + URLEncoder.encode(search_text, "UTF-8") );
		}else if("getRemoteServers".equals(action)){
			String version = request.getParameter("version")==null?"":request.getParameter("version");
			String repository = request.getParameter("repository")==null?"":request.getParameter("repository");
			
			KettleEngine kettleEngine = null;
			
			if(KettleEngine.VERSION_2_3.equals(version)){
				kettleEngine = new KettleEngineImpl2_3();
			}else if(KettleEngine.VERSION_4_3.equals(version)){
				kettleEngine = new KettleEngineImpl4_3();
			}
			
			StringBuffer serverSelect = new StringBuffer();
			serverSelect.append("<select name=\"remoteServer\" id=\"remoteServer\" style=\"width: 195px;\">");
			List<SlaveServerBean> listSlaveServer = SlaveServerUtil.findAll();
//			String[] slaveNames = kettleEngine.getSlaveNames(repository);
			for(SlaveServerBean slaveServer : listSlaveServer){
				serverSelect.append("<option value=\"")
							.append(slaveServer.getId_slave())
							.append("\">")
							.append(slaveServer.getName())
							.append("</option>");
			}
			serverSelect.append("</select>");
			serverSelect.append("<div id=\"remoteserver_error\" style=\"display:none;\"><font color=\"red\">")
						.append(Messages.getString("Scheduler.Dialog.Title.RemoteServer.Error"))
						.append("</font></div>");
			
			response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0L);
            response.setContentType("text/html; charset=UTF-8");
            response.getWriter().write(serverSelect.toString());
            response.getWriter().close();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
