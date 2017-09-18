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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;
import com.auphi.ktrl.engine.KettleEngine;
import com.auphi.ktrl.engine.impl.KettleEngineImpl2_3;
import com.auphi.ktrl.engine.impl.KettleEngineImpl4_3;
import com.auphi.ktrl.ha.bean.HAClusterBean;
import com.auphi.ktrl.ha.bean.SlaveServerBean;
import com.auphi.ktrl.ha.util.HAClusterUtil;
import com.auphi.ktrl.ha.util.SlaveServerUtil;
import com.auphi.ktrl.i18n.Messages;
import com.auphi.ktrl.schedule.bean.ScheduleBean;
import com.auphi.ktrl.schedule.dependency.DSchedule;
import com.auphi.ktrl.schedule.dependency.DScheduleUtil;
import com.auphi.ktrl.schedule.util.QuartzUtil;
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
public class DScheduleServlet extends HttpServlet {

       
    /**
	 * 
	 */
	private static final long serialVersionUID = -1364710589355468848L;
	private static UserBean userBean;

	/**
     * @see HttpServlet#HttpServlet()
     */
    public DScheduleServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String action = request.getParameter("action");
		userBean = request.getSession().getAttribute("userBean")==null?null:(UserBean)request.getSession().getAttribute("userBean");
		if("list".equals(action))
			list(request,response) ;
		else if("add".equals(action))
			add(request,response) ;
		else if ("addDependencies".equals(action))
			addDependencies(request,response) ;
		else if ("getDependencies".equals(action))
			getDependencies(request,response) ;
		else if("deleteDependency".equals(action))
			deleteDependency(request,response) ;
		else if("getSchedule".equals(action))
			getSchedule(request,response) ;
		else if("beforeUpdate".equals(action))
			beforeUpdate(request,response) ;
		else if("update".equals(action))
			update(request,response) ;
		else if("delete".equals(action))
			delete(request,response) ;
		else if("pause".equals(action))
			pause(request,response) ;
		else if("resume".equals(action))
			resume(request,response) ;
		else if("run".equals(action))
			run(request,response) ;
		else if("getReps".equals(action))
			getReps(request,response) ;
		else if("getRepTree".equals(action))
			getRepTree(request,response) ;
		else if("checkRepLogin".equals(action))
			checkRepLogin(request,response) ;
		else if("checkJobExist".equals(action))
			checkJobExist(request,response) ;
		else if("completedelete".equals(action))
			completedelete(request,response) ;
		else if("getRemoteServers".equals(action))
			getRemoteServers(request,response) ;
	}
	
	private void getRemoteServers(HttpServletRequest request, HttpServletResponse response) throws IOException{
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
//		String[] slaveNames = kettleEngine.getSlaveNames(repository);
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
	
	private void completedelete(HttpServletRequest request, HttpServletResponse response) throws IOException{
		String checked_job = request.getParameter("checked_job")==null?"":new String(request.getParameter("checked_job").getBytes("ISO8859-1"), "UTF-8");
		
		QuartzUtil.completedelete(checked_job.split(","), userBean);
		for(String jobName : checked_job.split(",")){
			DScheduleUtil.deleteDSchedule(jobName, String.valueOf(userBean.getOrgId()));
		}
		
		response.sendRedirect("dschedule?action=list");		
	}
	
	private void run(HttpServletRequest request, HttpServletResponse response) throws IOException{
		int page = request.getParameter("page")==null?1:Integer.parseInt(request.getParameter("page"));
		String checked_job = request.getParameter("checked_job")==null?"":new String(request.getParameter("checked_job").getBytes("ISO8859-1"), "UTF-8");
		QuartzUtil.execute(checked_job.split(","), userBean);
		response.sendRedirect("dschedule?action=list&page=" + page);
	}

	private void checkRepLogin(HttpServletRequest request, HttpServletResponse response) throws IOException{
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
	}
	
	private void checkJobExist(HttpServletRequest request, HttpServletResponse response) throws IOException{
		String jobname = request.getParameter("jobname")==null?"":new String(request.getParameter("jobname").getBytes("ISO8859-1"), "UTF-8");
		
		boolean jobname_exist = QuartzUtil.checkJobExist(jobname, userBean);
		
		response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0L);
        response.setContentType("text/html; charset=UTF-8");
        response.getWriter().write(String.valueOf(jobname_exist));
        response.getWriter().close();
	}
	
	private void getReps(HttpServletRequest request, HttpServletResponse response) throws IOException{
		String version = request.getParameter("version")==null?"":request.getParameter("version");
		UserBean userBean = request.getSession().getAttribute("userBean")==null?null:(UserBean)request.getSession().getAttribute("userBean");
		
		List<RepositoryBean> listReps = RepositoryUtil.getRepByVersionAndOrg(version, userBean.getOrgId());
		
		XStream xstream = new XStream(new JettisonMappedXmlDriver());
		xstream.alias("item", RepositoryBean.class);
		
		response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0L);
        response.setContentType("text/html; charset=UTF-8");
        response.getWriter().write(xstream.toXML(listReps));
        response.getWriter().close();
	}
	
	private void getRepTree(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		
		String user_id = request.getSession().getAttribute("user_id")==null?"":request.getSession().getAttribute("user_id").toString();
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
	}
	
	private void beforeUpdate(HttpServletRequest request, HttpServletResponse response) throws IOException{
		String jobName = request.getParameter("jobname")==null?"":request.getParameter("jobname");
		String jobgroup = request.getParameter("jobgroup") ;
		ScheduleBean scheduleBean = DScheduleUtil.getScheduleBeanByJobName(jobName,jobgroup);
		
		XStream xstream = new XStream(new JettisonMappedXmlDriver());
		xstream.alias("item", ScheduleBean.class);
		
		response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0L);
        response.setContentType("text/html; charset=UTF-8");
        response.getWriter().write(xstream.toXML(scheduleBean));
        response.getWriter().close();
	}
	
	private void update(HttpServletRequest request, HttpServletResponse response) throws IOException{
		int page = request.getParameter("page")==null?1:Integer.parseInt(request.getParameter("page"));
		ScheduleBean scheduleBean = DScheduleUtil.createScheduleBeanFromRequest(request, userBean);
		String checked_job = request.getParameter("checked_job")==null?"":request.getParameter("checked_job");
		QuartzUtil.update(scheduleBean, checked_job, userBean);
		
		response.sendRedirect("dschedule?action=list&page=" + page);
	}
	
	private void delete(HttpServletRequest request, HttpServletResponse response) throws IOException{
		String checked_job = request.getParameter("checked_job")==null?"":new String(request.getParameter("checked_job").getBytes("ISO8859-1"), "UTF-8");
		
		QuartzUtil.delete(checked_job.split(","), userBean);
		
		response.sendRedirect("dschedule?action=list");		
	}
	
	private void resume(HttpServletRequest request, HttpServletResponse response) throws IOException{
		int page = request.getParameter("page")==null?1:Integer.parseInt(request.getParameter("page"));
		String checked_job = request.getParameter("checked_job")==null?"":new String(request.getParameter("checked_job").getBytes("ISO8859-1"), "UTF-8");
		QuartzUtil.resume(checked_job.split(","), userBean);
		response.sendRedirect("dschedule?action=list&page=" + page);
	} 
	
	private void pause(HttpServletRequest request, HttpServletResponse response) throws IOException{
		int page = request.getParameter("page")==null?1:Integer.parseInt(request.getParameter("page"));
		String checked_job = request.getParameter("checked_job")==null?"":new String(request.getParameter("checked_job").getBytes("ISO8859-1"), "UTF-8");
		QuartzUtil.pause(checked_job.split(","), userBean);
		response.sendRedirect("dschedule?action=list&page=" + page);
	}
	
	private void list(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		int page = request.getParameter("page")==null?1:Integer.parseInt(request.getParameter("page"));
		String user_id = request.getSession().getAttribute("user_id")==null?"":request.getSession().getAttribute("user_id").toString();
		String order= request.getParameter("order")==null?"DESC":request.getParameter("order");
		String orderby= request.getParameter("orderby")==null?"":request.getParameter("orderby");
		String search_text = request.getParameter("search_text")==null?"":new String(request.getParameter("search_text").getBytes("ISO8859-1"), "UTF-8");
		String jobName = request.getParameter("jobName")==null?"":new String(request.getParameter("jobName").getBytes("ISO8859-1"), "UTF-8");
		String trigger_state=request.getParameter("trigger_state")==null?"":request.getParameter("trigger_state");
		UserBean userBean = request.getSession().getAttribute("userBean")==null?null:(UserBean)request.getSession().getAttribute("userBean");
		PageList pageList = DScheduleUtil.findAll(page, userBean,order,orderby,search_text,trigger_state);
		List<UserBean> listUsers = UserUtil.getUsersByOrg(userBean.getOrgId());
		List<HAClusterBean> listHACluster = HAClusterUtil.findAll();
		
		request.setAttribute("pageList", pageList);
		request.setAttribute("listUsers", listUsers);
		request.setAttribute("listHACluster", listHACluster);
		
		RequestDispatcher dispatcher = request.getRequestDispatcher("modules/dschedule/list.jsp"); 
		dispatcher.forward(request, response); 
	}

	private void add(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		int page = request.getParameter("page")==null?1:Integer.parseInt(request.getParameter("page"));
		String user_id = request.getSession().getAttribute("user_id")==null?"":request.getSession().getAttribute("user_id").toString();
		ScheduleBean scheduleBean = DScheduleUtil.createScheduleBeanFromRequest(request, userBean);
		scheduleBean.setUserId(user_id);
		QuartzUtil.create(scheduleBean,DSchedule.class);
		response.sendRedirect("dschedule?action=list&page=" + page);
	}
	
	private void addDependencies(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		String jobname = request.getParameter("jobname") ;
		String jobgroup = String.valueOf(userBean.getOrgId());
		String jobfullname = request.getParameter("jobfullname") ;
		String[] djobfullnames = request.getParameterValues("djobfullnames");
		
		if(jobname == null || jobgroup == null || jobfullname == null || djobfullnames == null)
			response.getWriter().write("Invalid argument!");
		
		
		// precheck for cylicle dependencies
		for(int i = 0 ; i < djobfullnames.length ; i++){
			StringBuffer result = new StringBuffer() ;
			if(DScheduleUtil.cylicleDependency(jobname,jobgroup,jobfullname,djobfullnames[i],result))
			{
				response.getWriter().write("Cylicle dependent:"+result.toString());	
				return  ;
			}
		}
		response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0L);
        response.setContentType("text/html; charset=UTF-8");
		
		if(DScheduleUtil.addDependency(jobname, jobgroup, jobfullname, djobfullnames))
			response.getWriter().write("OK");
		else
			response.getWriter().write("Failed");
		response.getWriter().close();
	}
	
	
	private void getDependencies(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		String jobname = request.getParameter("jobname") ;
		String jobgroup = String.valueOf(userBean.getOrgId()) ;
		Map<String,ArrayList<String>> dtree = DScheduleUtil.getDependencies(jobname, jobgroup) ;
		response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0L);
        response.setContentType("text/html; charset=UTF-8");
		response.getWriter().write(JSON.toJSONString(dtree));
		response.getWriter().close();
	}
	
	private void deleteDependency(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		String jobname = request.getParameter("jobname") ;
		String jobgroup = request.getParameter("jobgroup") ;
		String jobfullname = request.getParameter("jobfullname") ;
		String djobfullnames = request.getParameter("djobfullname");
		
		response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0L);
        response.setContentType("text/html; charset=UTF-8");
		if(DScheduleUtil.deleteDependency(jobname, jobgroup, jobfullname, djobfullnames)) 
			response.getWriter().write("OK");
		else
			response.getWriter().write("Failed");
		response.getWriter().close();
	}
	
	private void getSchedule(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		Map<String,ArrayList<String>> maps = DScheduleUtil.getSchedules(userBean) ;
		response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0L);
        response.setContentType("text/html; charset=UTF-8");
		response.getWriter().write(JSON.toJSONString(maps)) ;
		response.getWriter().close();
	}
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
