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
package com.auphi.ktrl.monitor;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Date;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.auphi.ktrl.engine.KettleEngine;
import com.auphi.ktrl.engine.impl.KettleEngineImpl2_3;
import com.auphi.ktrl.engine.impl.KettleEngineImpl4_3;
import com.auphi.ktrl.i18n.Messages;
import com.auphi.ktrl.monitor.bean.MonitorScheduleBean;
import com.auphi.ktrl.monitor.util.MonitorUtil;
import com.auphi.ktrl.schedule.bean.ScheduleBean;
import com.auphi.ktrl.schedule.template.Template;
import com.auphi.ktrl.schedule.util.ScheduleUtil;
import com.auphi.ktrl.system.user.bean.UserBean;
import com.auphi.ktrl.util.PageList;
import com.auphi.ktrl.util.StringUtil;

/**
 * Servlet implementation class MonitorServlet
 */
public class MonitorServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	private static Logger logger = Logger.getLogger(MonitorServlet.class);
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MonitorServlet() {
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
		String action = request.getParameter("action");
		int page = request.getParameter("page")==null?1:Integer.parseInt(request.getParameter("page"));
		String orderby = request.getParameter("orderby")==null?"START_TIME":request.getParameter("orderby");
		String order = request.getParameter("order")==null?"DESC":request.getParameter("order");
		String search_start_date = request.getParameter("search_start_date")==null?"":request.getParameter("search_start_date");
		String search_end_date = request.getParameter("search_end_date")==null?"":request.getParameter("search_end_date");
		String search_text = request.getParameter("search_text")==null?"":request.getParameter("search_text");
		int user_id = request.getSession().getAttribute("user_id")==null?0:Integer.parseInt(request.getSession().getAttribute("user_id").toString());
		UserBean userBean = request.getSession().getAttribute("userBean")==null?null:(UserBean)request.getSession().getAttribute("userBean");
		String jobName = request.getParameter("jobName")==null?"":request.getParameter("jobName");
		String jobStatus = request.getParameter("jobStatus")==null?"":new String(request.getParameter("jobStatus").toString());
		//System.out.println(jobStatus);
		int id_batch= request.getParameter("id_batch")==null?-1:Integer.parseInt(request.getParameter("id_batch"));
		String jobFile = request.getParameter("jobFile")==null?"":new String(request.getParameter("jobFile").getBytes("ISO8859-1"), "UTF-8");
		String id_logchannel = request.getParameter("id_logchannel")==null?"":request.getParameter("id_logchannel");
		
		if("list".equals(action)){//monitor list
			try{
				PageList pageList = MonitorUtil.findAll(page, orderby, order, search_start_date, search_end_date, search_text,jobStatus, user_id, jobName, userBean);

				request.setAttribute("pageList", pageList);
				request.setAttribute("orderby", orderby);
				request.setAttribute("order", order);
				request.setAttribute("search_start_date", search_start_date);
				request.setAttribute("search_end_date", search_end_date);
				request.setAttribute("search_text", search_text);
				request.setAttribute("jobStatus", jobStatus);
				request.setAttribute("jobName", jobName);
			}catch(Exception e){
				logger.error(e.getMessage(),e);
			}
			
			RequestDispatcher dispatcher = request.getRequestDispatcher("modules/monitor/list.jsp"); 
			dispatcher.forward(request, response); 
		}else if("showErrorLog".equals(action)){//show error message
			String id = request.getParameter("id")==null?"":request.getParameter("id");
			
			String errorMessage = "<font size='2'>" + MonitorUtil.getErrorMessage(id) + "</font>";
			
			response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0L);
            response.setContentType("text/html; charset=UTF-8");
            response.getWriter().write(errorMessage);
            response.getWriter().close();
		}else if("showLog".equals(action)){//show error message
			String id = request.getParameter("id")==null?"":request.getParameter("id");
			
			MonitorScheduleBean monitorScheduleBean = MonitorUtil.getMonitorData(id);
			if(monitorScheduleBean.getLogMsg() == null || "".equals(monitorScheduleBean.getLogMsg())){
				KettleEngine kettleEngine = new KettleEngineImpl4_3();
				monitorScheduleBean = kettleEngine.getMonitorDataFromJobLogTable(monitorScheduleBean, userBean);
			}
			
			StringBuffer resultSB = new StringBuffer();
			
			resultSB.append("<table border='1' style='border:1px solid #bbb;border-collapse:collapse;margin: 15px auto 15px auto; font-size: 15px;' width='95%' align='center'>")
					.append("<tr style='font-weight:bold;' align='center'>")
					.append("<td>").append(Messages.getString("Monitor.Data.Table.Column.Errors")).append("</td>")
					.append("<td>").append(Messages.getString("Monitor.Data.Table.Column.Input")).append("</td>")
					.append("<td>").append(Messages.getString("Monitor.Data.Table.Column.Output")).append("</td>")
					.append("<td>").append(Messages.getString("Monitor.Data.Table.Column.Updated")).append("</td>")
					.append("<td>").append(Messages.getString("Monitor.Data.Table.Column.Read")).append("</td>")
					.append("<td>").append(Messages.getString("Monitor.Data.Table.Column.Written")).append("</td>")
					.append("<td>").append(Messages.getString("Monitor.Data.Table.Column.Deleted")).append("</td>")
					.append("</tr>")
					.append("<tr>")
					.append("<td>").append(monitorScheduleBean.getLines_error()).append("</td>")
					.append("<td>").append(monitorScheduleBean.getLines_input()).append("</td>")
					.append("<td>").append(monitorScheduleBean.getLines_output()).append("</td>")
					.append("<td>").append(monitorScheduleBean.getLines_updated()).append("</td>")
					.append("<td>").append(monitorScheduleBean.getLines_read()).append("</td>")
					.append("<td>").append(monitorScheduleBean.getLines_output()).append("</td>")
					.append("<td>").append(monitorScheduleBean.getLines_deleted()).append("</td>")
					.append("</tr>")
					.append("<tr>")
					.append("<td colspan='7'><font size='2'>").append(monitorScheduleBean.getLogMsg()).append("</font></td>")
					.append("</tr>")
					.append("</table>");
			
			response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0L);
            response.setContentType("text/html; charset=UTF-8");
            response.getWriter().write(resultSB.toString());
            response.getWriter().close();
		}else if("delete".equals(action)){//delete monitor
			String checked_id = request.getParameter("checked_id")==null?"":request.getParameter("checked_id");
			
			MonitorUtil.deleteMonitor(checked_id);
			
			response.sendRedirect("monitor?action=list&orderby=" + orderby + "&order=" + order + "&search_start_date=" + search_start_date + "&search_end_date=" + search_end_date + "&search_text=" + URLEncoder.encode(search_text, "UTF-8") + "&jobName=" + URLEncoder.encode(jobName, "UTF-8"));
		}else if("showDetail".equals(action)){
			ScheduleBean scheduleBean = ScheduleUtil.getScheduleBeanByJobName(jobName, String.valueOf(userBean.getOrgId()));
			String version = scheduleBean.getVersion()==null?KettleEngine.VERSION_4_3:scheduleBean.getVersion();
			String actionPath = scheduleBean.getActionPath()==null?jobFile.replace("/" + Template.JOB_NAME, ""):scheduleBean.getActionPath();
			String actionRef = scheduleBean.getActionRef()==null?Template.JOB_NAME:scheduleBean.getActionRef();
			String repName = scheduleBean.getRepName()==null?"Default":scheduleBean.getRepName();
			String fileType = scheduleBean.getFileType()==null?KettleEngine.TYPE_JOB:scheduleBean.getFileType();
			String transStepsLog = "";
			String jobEntriesLog = "";
			
			KettleEngine kettleEngine = null;
			
			if(KettleEngine.VERSION_2_3.equals(version)){
				kettleEngine = new KettleEngineImpl2_3();
			}else if(KettleEngine.VERSION_4_3.equals(version)){
				kettleEngine = new KettleEngineImpl4_3();
			}
			
			StringBuffer sb = new StringBuffer();
			
			
			sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
			  .append("<config>")
			  .append("<container height=\"100%\" id=\"Scroller0\" type=\"spark.components.Scroller\" width=\"100%\">")
			  .append("<container height=\"100%\" id=\"Vgroup0\" paddingBottom=\"5\" paddingLeft=\"5\" paddingRight=\"5\" paddingTop=\"5\" type=\"spark.components.VGroup\" width=\"100%\">")
			  .append("<container height=\"100%\" id=\"Group0\" type=\"spark.components.Group\" width=\"100%\">")
			  .append("<container id=\"etl\" height=\"100%\" type=\"bi.etl.ETL\" width=\"100%\" x=\"0\" y=\"0\">")
			  .append("<etl type=\"kettle\">")
			  .append("<dataSource type=\"data\">")
			  .append("<data>");
			
			sb.append(kettleEngine.getXML(repName, fileType, actionPath, actionRef));
			
			sb.append("</data>")
			  .append("<url/>")
			  .append("</dataSource>")
			  .append("<monitorServer><![CDATA[/etl_platform/monitor?action=getJobEntryErrorLog&jobName=")
			  .append(jobName)
			  .append("]]></monitorServer>")
			  .append("</etl>")
			  .append("</container>")
			  .append("</container>")
			  .append("</container>")
			  .append("</container>")
			  .append("</config>");
			
			request.setAttribute("detailXML", sb.toString());
			request.setAttribute("jobName", jobName);
			request.setAttribute("actionRef", actionRef);
			request.setAttribute("fileType", fileType);
			request.setAttribute("id_batch", id_batch);
			request.setAttribute("id_logchannel", id_logchannel);
			
			RequestDispatcher dispatcher = request.getRequestDispatcher("modules/monitor/show.jsp");
			
			if(!MonitorUtil.STATUS_RUNNING.equals(jobStatus)){
				if(KettleEngine.TYPE_TRANS.equals(fileType)){
					transStepsLog = kettleEngine.getTransStepsLog(repName, actionPath, actionRef, id_batch);
					transStepsLog = transStepsLog.replaceAll("\r", "");
					transStepsLog = transStepsLog.replaceAll("\n", "");
				}else if(KettleEngine.TYPE_JOB.equals(fileType)){
					jobEntriesLog = kettleEngine.getJobEntriesLog(repName, actionPath, actionRef, id_batch);
					jobEntriesLog = jobEntriesLog.replaceAll("\r", "");
					jobEntriesLog = jobEntriesLog.replaceAll("\n", "");
				}
				
				request.setAttribute("transStepsLog", transStepsLog.replaceAll("\\r", ""));
				request.setAttribute("jobEntriesLog", jobEntriesLog);
				System.out.println("====job xml===:"+sb.toString());
				System.out.println("====jobEntriesLog===:"+jobEntriesLog);
				dispatcher = request.getRequestDispatcher("modules/monitor/show_result.jsp");
			}
			dispatcher.forward(request, response); 
		}else if("getActiveDetails".equals(action)){
			ScheduleBean scheduleBean = ScheduleUtil.getScheduleBeanByJobName(jobName, String.valueOf(userBean.getOrgId()));
			String fileType = scheduleBean.getFileType();
			String version = scheduleBean.getVersion();
			String actionPath = scheduleBean.getActionPath();
			String actionRef = scheduleBean.getActionRef();
			String repName = scheduleBean.getRepName();
			
			KettleEngine kettleEngine = null;
			
			if(KettleEngine.VERSION_2_3.equals(version)){
				kettleEngine = new KettleEngineImpl2_3();
			}else if(KettleEngine.VERSION_4_3.equals(version)){
				kettleEngine = new KettleEngineImpl4_3();
			}
			
			String status = kettleEngine.getActiveDetails(repName, fileType, actionPath, actionRef, id_logchannel);
			
			response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0L);
            response.setContentType("text/html; charset=UTF-8");
            response.getWriter().write(status);
            response.getWriter().close();
		}else if("reload".equals(action)){//monitor list
			try{
				String checked_id = request.getParameter("checked_id")==null?"":request.getParameter("checked_id");
				String reload_date = request.getParameter("reload_date")==null?"":request.getParameter("reload_date");
				Date date = StringUtil.StringToDate(reload_date, "yyyy-MM-dd hh:mm");
				String[] ids = checked_id.split(",");
				
				for(String id : ids){
					System.out.println(id);
					MonitorReloadThread monitorReloadThread = new MonitorReloadThread(id, date, userBean);
					monitorReloadThread.start();
				}
				
				PageList pageList = MonitorUtil.findAll(page, orderby, order, search_start_date, search_end_date, search_text,jobStatus, user_id, jobName, userBean);
				
				request.setAttribute("pageList", pageList);
				request.setAttribute("orderby", orderby);
				request.setAttribute("order", order);
				request.setAttribute("search_start_date", search_start_date);
				request.setAttribute("search_end_date", search_end_date);
				request.setAttribute("search_text", search_text);
				request.setAttribute("jobStatus", jobStatus);
				request.setAttribute("jobName", jobName);
			}catch(Exception e){
				logger.error(e.getMessage(),e);
			}
			
			RequestDispatcher dispatcher = request.getRequestDispatcher("modules/monitor/list.jsp"); 
			dispatcher.forward(request, response); 
		}else if("getJobEntryErrorLog".equals(action)){
			System.out.println("========jobName="+jobName);
			ScheduleBean scheduleBean = ScheduleUtil.getScheduleBeanByJobName(jobName, String.valueOf(userBean.getOrgId()));
			String actionPath = scheduleBean.getActionPath();
			String actionRef = scheduleBean.getActionRef();
			String repName = scheduleBean.getRepName();
			String jobEntryName = request.getParameter("jobEntryName")==null?"":new String(request.getParameter("jobEntryName").getBytes("ISO8859-1"), "UTF-8");;
			
			KettleEngine kettleEngine = new KettleEngineImpl4_3();
			String jobEntryErrorLog = kettleEngine.getJobEntryErrorLog(jobEntryName, repName, actionPath, actionRef, id_batch);
			
			response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0L);
            response.setContentType("text/html; charset=UTF-8");
            response.getWriter().write(jobEntryErrorLog);
            response.getWriter().close();
		}else if("stopRunning".equals(action)){
			String checked_id = request.getParameter("checked_id")==null?"":request.getParameter("checked_id");
			for(String monitorId : checked_id.split(",")){
				MonitorScheduleBean monitorBean = MonitorUtil.getMonitorData(monitorId);
				jobName =  monitorBean.getJobName();
				
				System.out.println("========jobName="+jobName);
				ScheduleBean scheduleBean = ScheduleUtil.getScheduleBeanByJobName(jobName, String.valueOf(userBean.getOrgId()));
				String actionPath = scheduleBean.getActionPath();
				String actionRef = scheduleBean.getActionRef();
				String repName = scheduleBean.getRepName();
				String fileType = scheduleBean.getFileType();
				KettleEngine kettleEngine = new KettleEngineImpl4_3();
				kettleEngine.stopRunning(repName, fileType, actionPath, actionRef, monitorBean);
			}
			
			response.sendRedirect("monitor?action=list&orderby=" + orderby + "&order=" + order + "&search_start_date=" + search_start_date + "&search_end_date=" + search_end_date + "&search_text=" + URLEncoder.encode(search_text, "UTF-8") + "&jobName=" + URLEncoder.encode(jobName, "UTF-8"));
		}else if("clear".equals(action)){//monitor list
			try{
				String clear_date = request.getParameter("clear_date")==null?"":request.getParameter("clear_date");
				
				MonitorUtil.clearMonitor(clear_date);
				
				PageList pageList = MonitorUtil.findAll(page, orderby, order, search_start_date, search_end_date, search_text,jobStatus, user_id, jobName, userBean);
				
				request.setAttribute("pageList", pageList);
				request.setAttribute("orderby", orderby);
				request.setAttribute("order", order);
				request.setAttribute("search_start_date", search_start_date);
				request.setAttribute("search_end_date", search_end_date);
				request.setAttribute("search_text", search_text);
				request.setAttribute("jobStatus", jobStatus);
				request.setAttribute("jobName", jobName);
			}catch(Exception e){
				logger.error(e.getMessage(),e);
			}
			
			RequestDispatcher dispatcher = request.getRequestDispatcher("modules/monitor/list.jsp"); 
			dispatcher.forward(request, response); 
		}
	}
}
