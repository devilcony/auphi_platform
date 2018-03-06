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
package com.auphi.ktrl.metadata;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.auphi.ktrl.i18n.Messages;
import com.auphi.ktrl.metadata.bean.JsFlowBean;
import com.auphi.ktrl.metadata.bean.MetaDataConnBean;
import com.auphi.ktrl.metadata.bean.MetaDataSourceBean;
import com.auphi.ktrl.metadata.util.GraphUtil;
import com.auphi.ktrl.metadata.util.MetadataUtil;
import com.auphi.ktrl.util.FileUtil;
import com.auphi.ktrl.util.UtilDateTime;


/**
 * Servlet implementation class MetadataServlet
 */
public class MetadataServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static Logger logger = Logger.getLogger(MetadataServlet.class);
	
	public MetadataServlet() {
        super();
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
		//左边树点击作业报告获取资源库列表
		if("resources".equals(action)){//resource list
			try{
				List<MetaDataSourceBean> resourceList = MetadataUtil.getResourceList();
				
				request.setAttribute("resourceList", resourceList);
			}catch(Exception e){
				logger.error(e.getMessage(),e);
			}
			
			RequestDispatcher dispatcher = request.getRequestDispatcher("modules/metadata/jobReport.jsp"); 
			dispatcher.forward(request, response); 
		}
		//作业报告树
		else if("reportResource".equals(action))
		{
			String resource = request.getParameter("resource")==null?"":new String(request.getParameter("resource").getBytes("ISO8859-1"), "UTF-8");
			String reportTreeJSON = MetadataUtil.getResourceReportTree(resource);
			
			//System.out.println("reportTreeJSON="+reportTreeJSON);
			response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0L);
            response.setContentType("text/html; charset=UTF-8");
            response.getWriter().write(reportTreeJSON);
            response.getWriter().close();
		}
		//作业报告
		else if("createReport".equals(action))
		{
			String resource = request.getParameter("resource")==null?"":request.getParameter("resource");
			String treeSelect = request.getParameter("treeSelect");
			String type = request.getParameter("type")==null?"":request.getParameter("type");
			String fileType = request.getParameter("fileType")==null?"":request.getParameter("fileType");
			
			//System.out.println(resource);
			//System.out.println(treeSelect);
			//System.out.println(type);
			String time = UtilDateTime.toDateTimeString(new Date()).replaceAll(" ", "_").replaceAll(":", "_");
			String reportoutput = "reportoutput";
			String fileDir = request.getRealPath("/")+"modules"+File.separator+"metadata"+File.separator+reportoutput+File.separator;
			FileUtil.createFileDir(fileDir);
			String targetFilename = fileDir+"output_"+time+".html";
			
			//String contextPath = request.getContextPath();
			//String outFilename = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort() +"/output.html";
			//String outFilename = "/output.html";
			//System.out.println(outFilename);
			
			//String targetFilename = "d:\\test\\ETL Documenation.html";
			String reportJSON="";
			if("create".equals(type))
			{
				reportJSON = MetadataUtil.getResourceReport(resource,Integer.parseInt(treeSelect),fileType,targetFilename);
				reportJSON = reportJSON+";"+time;
			}
			//System.out.println(reportJSON);
			
			if("get".equals(type))
			{
				String createtime = request.getParameter("time");
				if(createtime != null){
					targetFilename = fileDir+"output_"+createtime+".html";
					File fileOutput = FileUtil.getFile(targetFilename, "output_"+createtime, ".html");
					boolean isExists = FileUtil.findfileExists(fileOutput);
					if(isExists){
						reportJSON = FileUtil.readFile(fileOutput.getAbsolutePath());
						int styleIdx = reportJSON.indexOf("rel=\"stylesheet\" href=\"");
						if(styleIdx > 0){
							String firstStyleStr = reportJSON.substring(0, styleIdx);
							String lastStyleStr = reportJSON.substring(styleIdx+23);
							styleIdx = lastStyleStr.indexOf("\"");
							if(styleIdx > 0){
								fileOutput = new File(fileDir + lastStyleStr.substring(0, styleIdx));
								isExists = FileUtil.findfileExists(fileOutput);
								if(isExists){
									reportJSON = firstStyleStr + "rel=\"stylesheet\" href=\"modules/metadata/"+reportoutput+"/" +lastStyleStr;
								}
							}
						}
						int imgIdx = reportJSON.indexOf("src=\"");
						if(imgIdx >0){
						String firstImgStr = reportJSON.substring(0, imgIdx);
						String lastImgStr = reportJSON.substring(imgIdx+5);
						imgIdx = lastImgStr.indexOf("\"");
						if(imgIdx>0){
							fileOutput = new File(fileDir + lastImgStr.substring(0, imgIdx));
							isExists = FileUtil.findfileExists(fileOutput);
							if(isExists){
								reportJSON = firstImgStr + "src=\"modules/metadata/"+reportoutput+"/" +lastImgStr;
							}
						}
						}
					}
				}
				//System.out.println(reportJSON);
			}
			response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0L);
            response.setContentType("text/html; charset=UTF-8");
            response.getWriter().write(reportJSON);
            response.getWriter().close();
		}
		//导出作业报告html
		else if("exportReport".equals(action))
		{
		        String report = request.getParameter("report")==null?"":new String(request.getParameter("report").getBytes("ISO8859-1"), "UTF-8");
		        String fileName = UtilDateTime.toDateTimeString(new Date()).replaceAll(" ", "_").replaceAll(":", "_");
		        //System.out.println(report);
		        String fileType = request.getParameter("fileType");
		        fileName = request.getSession().getServletContext().getRealPath("/")+ "report_" + fileName+"."+fileType;
		        //System.out.println(fileName);
		        boolean isSuccess = false;
		        try {
		        	FileUtil.deletefile(fileName, "report_", ".html");
		        	FileUtil.deletefile(fileName, "picture", ".png");
		        	FileUtil.deletefile(fileName, "style", ".css");
		        	
		        	
					int styleIdx = report.indexOf("href=\"modules/metadata/");
					String styleName = null;
					if(styleIdx != -1){
					String firstStyleStr = report.substring(0, styleIdx);
					String lastStyleStr = report.substring(styleIdx+24);
					report = firstStyleStr + "href=\"" +lastStyleStr;
						styleName = lastStyleStr.substring(0, lastStyleStr.indexOf("\""));
					}
					int imgIdx = report.indexOf("src=\"modules/metadata/");
					String firstImgStr = report.substring(0, imgIdx);
					String lastImgStr = report.substring(imgIdx+23);
					report = firstImgStr + "<img src=\"" +lastImgStr;
					String imgName = lastImgStr.substring(0, lastImgStr.indexOf("\""));
					
					FileUtil.writeFile(fileName, report,false);
					
					String targetFilename = request.getRealPath("/")+"modules"+File.separator+"metadata"+File.separator+"output.html";
					
					String fileOutdir = new File(fileName).getParentFile().getAbsolutePath();
					if(!fileOutdir.endsWith(File.separator)){
						fileOutdir = fileOutdir + File.separator;
					}
					File fileIn = null;
					File fileOut = null;
					if(styleName != null){
						fileIn = FileUtil.getFile(targetFilename, "style", ".css");
						fileOut = new File(fileOutdir+styleName);
						FileUtil.copyFile(fileIn, fileOut);
					}
					fileIn = FileUtil.getFile(targetFilename, "picture", ".png");
					fileOut = new File(fileOutdir+imgName);
					FileUtil.copyFile(fileIn, fileOut);
					isSuccess = true;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.error(e.getMessage(),e);
				}
		        
				response.setHeader("Pragma", "No-cache");
	            response.setHeader("Cache-Control", "no-cache");
	            response.setDateHeader("Expires", 0L);
	            response.setContentType("text/html; charset=UTF-8");
	            response.getWriter().write(isSuccess+":::"+fileName);
	            response.getWriter().close();	
		}
		//导出pdf作业报告
		else if("createPDFReport".equals(action))
		{
			String resource = request.getParameter("resource")==null?"":request.getParameter("resource");
			String treeSelect = request.getParameter("treeSelect")==null?"":request.getParameter("treeSelect");
			String fileType = request.getParameter("fileType")==null?"":request.getParameter("fileType");
			String filePath = request.getParameter("filePath")==null?"":request.getParameter("filePath");
			String fileName = request.getParameter("fileName")==null?"":request.getParameter("fileName");
			if(!fileName.toUpperCase().endsWith("."+fileType.toUpperCase())){
				fileName = fileName+"."+fileType;
			}
			//System.out.println(resource);
			//System.out.println(treeSelect);
			//System.out.println(fileType);
			//System.out.println(filePath);
			//System.out.println(fileName);
			filePath = filePath.substring(0, filePath.lastIndexOf(File.separator));
			String targetFilename = filePath + File.separator + fileName;

			String reportJSON = MetadataUtil.getResourceReport(resource,Integer.parseInt(treeSelect),fileType,targetFilename);
			//System.out.println("createPDFReport result="+reportJSON+":::"+targetFilename);
			response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0L);
            response.setContentType("text/html; charset=UTF-8");
            response.getWriter().write(reportJSON+":::"+targetFilename);
            response.getWriter().close();
		}	
		//以下载的方式来导出pdf作业报告
		else if("downloadPDFReport".equals(action))
		{
			String resource = request.getParameter("resource")==null?"":request.getParameter("resource");
			String treeSelect = request.getParameter("treeSelect")==null?"":request.getParameter("treeSelect");
			String fileType = request.getParameter("fileType")==null?"":request.getParameter("fileType");

			//System.out.println(resource);
			//System.out.println(treeSelect);
			//System.out.println(fileType);
			String fileDir = request.getRealPath("/")+"modules"+File.separator+"metadata"+File.separator + "reportpdf"+File.separator;
			FileUtil.createFileDir(fileDir);
			
			String targetFilename = fileDir+"report_"+System.currentTimeMillis()+"."+fileType;
			//FileUtil.deletefile(targetFilename, "report_", "."+fileType);
			
			String result = MetadataUtil.getResourceReport(resource,Integer.parseInt(treeSelect),fileType,targetFilename);
			//System.out.println("createPDFReport result="+result+":::"+targetFilename);
			if("success".equals(result)){
				result = "{'path':'"+targetFilename.replace(request.getRealPath("/"), "").replace(File.separator, "/")+"'}";
			}
			//System.out.println("createPDFReport result="+result);
			response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0L);
            response.setContentType("text/html; charset=UTF-8");
            response.getWriter().write(result);
            response.getWriter().close();
		}		
		//左边树点击影响分析获取资源库数据
		else if("influence".equals(action))
		{
			try{
				List<MetaDataSourceBean> resourceList = MetadataUtil.getResourceList();
				
				request.setAttribute("resourceList", resourceList);
			}catch(Exception e){
				logger.error(e.getMessage(),e);
			}
			
			RequestDispatcher dispatcher = request.getRequestDispatcher("modules/metadata/influence.jsp"); 
			dispatcher.forward(request, response); 
		}
		//影响分析-数据源数据
		else if("datasources".equals(action))
		{
			String connectionname = request.getParameter("connectionname")==null?"":new String(request.getParameter("connectionname").getBytes("ISO8859-1"), "UTF-8");
			
			String datasourcesJSON = MetadataUtil.getDatasources(connectionname);
			
			
			response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0L);
            response.setContentType("text/html; charset=UTF-8");
            response.getWriter().write(datasourcesJSON);
            response.getWriter().close();
		}
		//校验数据源输入
		else if("checkDatasources".equals(action)){
			String connectionname = request.getParameter("connectionname")==null?"":request.getParameter("connectionname");
			String textValue = request.getParameter("textValue")==null?"":request.getParameter("textValue");
			//System.out.println("checkDatasources connectionname:"+connectionname);
			//System.out.println("checkDatasources textvalue:"+textValue);
			
			String result = MetadataUtil.checkDatasources(connectionname, textValue);
			//System.out.println(result);
			response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0L);
            response.setContentType("text/html; charset=UTF-8");
            response.getWriter().write(result);
            response.getWriter().close();
		}		
		//影响分析-模式名数据
		else if("schemas".equals(action))
		{
			String resource = request.getParameter("resource")==null?"":request.getParameter("resource");
			String schemasJSON = null;
			//try{
				schemasJSON = MetadataUtil.getSchemas(resource);
			//}catch(Exception e){
			//	schemasJSON = "getSchemas("+resource +") error:"+e.getMessage();
			//}
			//System.out.println(schemasJSON);
			response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0L);
            response.setContentType("text/html; charset=UTF-8");
            response.getWriter().write(schemasJSON);
            response.getWriter().close();
		}
		//校验模式名输入
		else if("checkSchemas".equals(action)){
			String connectionname = request.getParameter("connectionname")==null?"":request.getParameter("connectionname");
			String textValue = request.getParameter("textValue")==null?"":request.getParameter("textValue");
			//System.out.println("checkSchemas connectionname:"+connectionname);
			//System.out.println("checkSchemas textvalue:"+textValue);
			String result = null;
			//try{
				result = MetadataUtil.checkSchemas(connectionname, textValue);
			//}catch(Exception e){
			//	result = "checkSchemas("+connectionname +","+textValue+") error:"+e.getMessage();
			//}
			//System.out.println(result);
			response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0L);
            response.setContentType("text/html; charset=UTF-8");
            response.getWriter().write(result);
            response.getWriter().close();
		}			
		//影响分析-表名
		else if("tables".equals(action))
		{
			String resource = request.getParameter("resource")==null?"":request.getParameter("resource");
			String schemaName = request.getParameter("schemaName")==null?"":request.getParameter("schemaName");
			
			String tablesJSON = MetadataUtil.getTables(resource,schemaName);
			
			
			response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0L);
            response.setContentType("text/html; charset=UTF-8");
            response.getWriter().write(tablesJSON);
            response.getWriter().close();
		}
		//校验表名输入
		else if("checkTables".equals(action))
		{
			String connectionname = request.getParameter("connectionname")==null?"":request.getParameter("connectionname");
			String schemaName = request.getParameter("schemaName")==null?"":request.getParameter("schemaName");
			String textValue = request.getParameter("textValue")==null?"":request.getParameter("textValue");
			//System.out.println("checkTables connctionname:"+connectionname);
			//System.out.println("checkTables schemaName:"+schemaName);
			//System.out.println("checkTables textvalue:"+textValue);
			
			String result = MetadataUtil.checkTables(connectionname,schemaName,textValue);
			//System.out.println(result);
			response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0L);
            response.setContentType("text/html; charset=UTF-8");
            response.getWriter().write(result);
            response.getWriter().close();
		}
		//影响分析-字段名
		else if("fields".equals(action))
		{
			String resource = request.getParameter("resource")==null?"":new String(request.getParameter("resource").getBytes("ISO8859-1"),"UTF-8");
			String schemaName = request.getParameter("schemaName")==null?"":new String(request.getParameter("schemaName").getBytes("ISO8859-1"),"UTF-8");
			String tableName = request.getParameter("tableName")==null?"":new String(request.getParameter("tableName").getBytes("ISO8859-1"),"UTF-8");
			String fieldsJSON = MetadataUtil.getFields(resource,schemaName,tableName);
			
			
			response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0L);
            response.setContentType("text/html; charset=UTF-8");
            response.getWriter().write(fieldsJSON);
            response.getWriter().close();
		}
		//校验字段名输入
		else if("checkFields".equals(action))
		{
			String connectionname = request.getParameter("connectionname")==null?"":request.getParameter("connectionname");
			String schemaName = request.getParameter("schemaName")==null?"":request.getParameter("schemaName");
			String tableName = request.getParameter("tableName")==null?"":request.getParameter("tableName");
			String textValue = request.getParameter("textValue")==null?"":request.getParameter("textValue");
			//System.out.println("checkFields connctionname:"+connectionname);
			//System.out.println("checkFields schemaName:"+schemaName);
			//System.out.println("checkFields tablename:"+tableName);
			//System.out.println("checkFields textvalue:"+textValue);
			String result = MetadataUtil.checkFields(connectionname,schemaName,tableName,textValue);
			//System.out.println(result);
			response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0L);
            response.setContentType("text/html; charset=UTF-8");
            response.getWriter().write(result);
            response.getWriter().close();
		}		
		//影响分析-影响分析按钮
		else if("reportInfluence".equals(action))
		{
			String resource = request.getParameter("resource")==null?"":request.getParameter("resource");
			String datasource = request.getParameter("datasource")==null?"":request.getParameter("datasource");
			String schemas = request.getParameter("schemas")==null?"":request.getParameter("schemas");
			String tables = request.getParameter("tables")==null?"":request.getParameter("tables");
			String fields = request.getParameter("fields")==null?"":request.getParameter("fields");

			//System.out.println("reportInfluence resource:"+resource);
			//System.out.println("reportInfluence datasource:"+datasource);
			//System.out.println("reportInfluence schemas:"+schemas);
			//System.out.println("reportInfluence tables:"+tables);
			//System.out.println("reportInfluence fields:"+fields);
			String reportTreeJSON = MetadataUtil.getResourceInfluenceReportTree(resource);
			
			
			response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0L);
            response.setContentType("text/html; charset=UTF-8");
            response.getWriter().write(reportTreeJSON);
            response.getWriter().close();
		}
		//影响分析-影响分析结构节点
		else if("nodeDetail".equals(action))
		{
			String params = request.getParameter("params")==null?"":request.getParameter("params");
			//System.out.println(params);
			
			String reportJSON = MetadataUtil.getNodeReport(params);
			//System.out.println(reportJSON);
			
			response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0L);
            response.setContentType("text/html; charset=UTF-8");
            response.getWriter().write(reportJSON);
            response.getWriter().close();
		}
		//左边树点击血统分析获取资源库数据
		else if("descent".equals(action))
		{
			try{
				List<MetaDataSourceBean> resourceList = MetadataUtil.getResourceList();
				
				request.setAttribute("resourceList", resourceList);
			}catch(Exception e){
				logger.error(e.getMessage(),e);
			}
			
			RequestDispatcher dispatcher = request.getRequestDispatcher("modules/metadata/descent.jsp"); 
			dispatcher.forward(request, response); 
		}	
		//影响分析-按钮-流程图
		else if("reportInfluenceFlow".equals(action))
		{			
			String datasource = request.getParameter("datasources")==null?"":request.getParameter("datasources");
			String schemas = request.getParameter("schemas")==null?"":request.getParameter("schemas");
			String tables = request.getParameter("tables")==null?"":request.getParameter("tables");
			String fields = request.getParameter("fields")==null?"":request.getParameter("fields");

			//System.out.println("reportFlow datasource:"+datasource);
			//System.out.println("reportFlow schemas:"+schemas);
			//System.out.println("reportFlow tables:"+tables);
			//System.out.println("reportFlow fields:"+fields);
			List<JsFlowBean> list = MetadataUtil.getInfluenceJsFlowBeanList(datasource,schemas,tables,fields);
			String reportFlow = MetadataUtil.getJsFlow(list);
			
			
			response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0L);
            response.setContentType("text/html; charset=UTF-8");
            response.getWriter().write(reportFlow);
            response.getWriter().close();
		}	
		//影响分析-按钮-graph流程图
		else if("influenceGraphFlow".equals(action))
		{			
			String syn = request.getParameter("syn")==null?"false":request.getParameter("syn");
			String resource = request.getParameter("resource")==null?"":request.getParameter("resource");
			String datasource = request.getParameter("datasources")==null?"":request.getParameter("datasources");
			String schemas = request.getParameter("schemas")==null?"":request.getParameter("schemas");
			String tables = request.getParameter("tables")==null?"":request.getParameter("tables");
			String fields = request.getParameter("fields")==null?"":request.getParameter("fields");

			//System.out.println("reportFlow datasource:"+datasource);
			//System.out.println("reportFlow schemas:"+schemas);
			//System.out.println("reportFlow tables:"+tables);
			//System.out.println("reportFlow fields:"+fields);
			MetaDataConnBean bean = MetadataUtil.getResourceBean(resource);
			GraphUtil graphUtil = new GraphUtil();
			graphUtil.setBean(graphUtil.getGraphBean4Influence(Boolean.parseBoolean(syn),bean,datasource,schemas,tables,fields));
			
			String reportFlow =  graphUtil.getGraph(true);
			
			//System.out.println(reportFlow);
			response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0L);
            response.setContentType("text/html; charset=UTF-8");
            response.getWriter().write(reportFlow);
            response.getWriter().close();
		}		
		//血统分析-血统分析按钮-树结构
		else if("reportDescent".equals(action))
		{
			String resource = request.getParameter("resource")==null?"":new String(request.getParameter("resource").getBytes("ISO8859-1"), "UTF-8");
			String datasource = request.getParameter("datasource")==null?"":new String(request.getParameter("datasource").getBytes("ISO8859-1"), "UTF-8");
			String schemas = request.getParameter("schemas")==null?"":new String(request.getParameter("schemas").getBytes("ISO8859-1"), "UTF-8");
			String tables = request.getParameter("tables")==null?"":new String(request.getParameter("tables").getBytes("ISO8859-1"), "UTF-8");
			String fields = request.getParameter("fields")==null?"":new String(request.getParameter("fields").getBytes("ISO8859-1"), "UTF-8");

			//System.out.println("reportDescent resource:"+resource);
			//System.out.println("reportDescent datasource:"+datasource);
			//System.out.println("reportDescent schemas:"+schemas);
			//System.out.println("reportDescent tables:"+tables);
			//System.out.println("reportDescent fields:"+fields);
			String reportTreeJSON = MetadataUtil.getResourceDescentReportTree(resource);
			
			
			response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0L);
            response.setContentType("text/html; charset=UTF-8");
            response.getWriter().write(reportTreeJSON);
            response.getWriter().close();
		}
		//血统分析-按钮-流程图
		else if("reportFlow".equals(action))
		{
			String datasource = request.getParameter("datasources")==null?"":request.getParameter("datasources");
			String schemas = request.getParameter("schemas")==null?"":request.getParameter("schemas");
			String tables = request.getParameter("tables")==null?"":request.getParameter("tables");
			String fields = request.getParameter("fields")==null?"":request.getParameter("fields");

			//System.out.println("reportFlow datasource:"+datasource);
			//System.out.println("reportFlow schemas:"+schemas);
			//System.out.println("reportFlow tables:"+tables);
			//System.out.println("reportFlow fields:"+fields);
			List<JsFlowBean> list = MetadataUtil.getJsFlowBeanList(datasource,schemas,tables,fields);
			String reportFlow = MetadataUtil.getJsFlow(list);
			
			
			response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0L);
            response.setContentType("text/html; charset=UTF-8");
            response.getWriter().write(reportFlow);
            response.getWriter().close();
		}
		//血统分析-按钮-graph流程图
		else if("descentGraphFlow".equals(action))
		{
			String syn = request.getParameter("syn")==null?"false":request.getParameter("syn");
			String resource = request.getParameter("resource")==null?"":request.getParameter("resource");
			String datasource = request.getParameter("datasources")==null?"":request.getParameter("datasources");
			String schemas = request.getParameter("schemas")==null?"":request.getParameter("schemas");
			String tables = request.getParameter("tables")==null?"":request.getParameter("tables");
			String fields = request.getParameter("fields")==null?"":request.getParameter("fields");

			//System.out.println("reportFlow datasource:"+datasource);
			//System.out.println("reportFlow schemas:"+schemas);
			//System.out.println("reportFlow tables:"+tables);
			//System.out.println("reportFlow fields:"+fields);
			MetaDataConnBean bean = MetadataUtil.getResourceBean(resource);
			GraphUtil graphUtil = new GraphUtil();
			graphUtil.setBean(graphUtil.getGraphBean4Descent(Boolean.parseBoolean(syn),bean,datasource,schemas,tables,fields));
			String reportFlow =  graphUtil.getGraph(false);
			
			
			response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0L);
            response.setContentType("text/html; charset=UTF-8");
            response.getWriter().write(reportFlow);
            response.getWriter().close();
		}		
		//元数据搜索-资源库列表
		else if("search".equals(action)){
            RequestDispatcher dispatcher = request.getRequestDispatcher("modules/metadata/search.jsp"); 
			dispatcher.forward(request, response); 
		}
		//元数据搜索-资源库列表
		else if("resources4Search".equals(action)){
			try{
				List<MetaDataSourceBean> resourceList = MetadataUtil.getResourceList();
				StringBuffer sb = new StringBuffer();
				for(MetaDataSourceBean bean:resourceList){
					sb.append("{'cname':'"+bean.getDescription()+"','id':'"+bean.getConnection()+"'},");
				}
				
				response.setHeader("Pragma", "No-cache");
	            response.setHeader("Cache-Control", "no-cache");
	            response.setDateHeader("Expires", 0L);
	            response.setContentType("text/html; charset=UTF-8");
	            response.getWriter().write(MetadataUtil.adjustString(sb));
	            response.getWriter().close();
	            
			}catch(Exception e){
				logger.error(e.getMessage(), e);
			}
		}
		//元数据搜索-搜索类型列表
		else if("searchType".equals(action)){
			StringBuffer sb = new StringBuffer();
			
			String searchTypePath = request.getRealPath("/").replace("\\", "/");
			if(searchTypePath.endsWith("\\") || searchTypePath.endsWith("/")){
				searchTypePath = searchTypePath + "modules/metadata/searchType.xml";
			}else{
				searchTypePath = searchTypePath + "/modules/metadata/searchType.xml";
			}
			String[] searchTypeXpaths = {"/searchTypes/searchType/type",
			  							"/searchTypes/searchType/sql"};
			
			List<List<String>> searchTypeValuesList = FileUtil.getElementValueXML(searchTypePath, searchTypeXpaths);
			for(List<String> list:searchTypeValuesList){
				sb.append("{'cname':'"+list.get(0)+"','id':'"+list.get(1)+"'},");
			}
			
			response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0L);
            response.setContentType("text/html; charset=UTF-8");
            response.getWriter().write(MetadataUtil.adjustString(sb));
            response.getWriter().close();
		}
		//搜索按钮
		else if("searchAction".equals(action)){
			String resources = request.getParameter("resources")==null?"":new String(request.getParameter("resources").getBytes("ISO8859-1"), "UTF-8");
			String searchType = request.getParameter("searchType")==null?"":new String(request.getParameter("searchType").getBytes("ISO8859-1"), "UTF-8");
			String searchKey = request.getParameter("searchKey")==null?"":new String(request.getParameter("searchKey").getBytes("ISO8859-1"), "UTF-8");
			String start = request.getParameter("start")==null?"0":request.getParameter("start");
			String limit = request.getParameter("limit")==null?"0":request.getParameter("limit");
			String query = request.getParameter("query")==null?"":request.getParameter("query");
			String page = request.getParameter("page")==null?"0":request.getParameter("page");
			String _dc = request.getParameter("_dc")==null?"":request.getParameter("_dc");
		
			
			//System.out.println(" searchAction resources:"+resources);
			//System.out.println(" searchAction searchType:"+searchType);
			//System.out.println(" searchAction searchKey:"+searchKey);
			//System.out.println(" searchAction start:"+start);
			//System.out.println(" searchAction limit:"+limit);
			//System.out.println(" searchAction query:"+query);
			//System.out.println(" searchAction page:"+page);
			//System.out.println(" searchAction _dc:"+_dc);
			
			StringBuffer sb = new StringBuffer();
			if(resources.equals("") || searchType.equals("")){
				sb.append("");
			}else{
				sb.append(MetadataUtil.queryMetadata(Integer.parseInt(start),Integer.parseInt(limit),resources, searchType, searchKey));
			}
			//System.out.println(sb.toString());
			/**
			StringBuffer sb = new StringBuffer();
			sb.append("{\"totalCount\":\"2\",\"topics\":[");
			sb.append("{\"post_id\":\"1\",\"topic_title\":\"转换1\"},");
			sb.append("{\"post_id\":\"2\",\"topic_title\":\"作业2\"}");
			sb.append("]}");
			System.out.println(sb.toString());
			**/
			response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0L);
            response.setContentType("text/html; charset=UTF-8");
            response.getWriter().write(sb.toString());
            response.getWriter().close();
		}
		//搜索结果项目链接页面
		else if("searchShowItem".equals(action)){
			String mainid = request.getParameter("mainid")==null?"0":request.getParameter("mainid");
			String fileDir = "";
			String fileName = "";
			
			String fileType = "";
			String resources = "";
			List<String> searchResourceStore = MetadataUtil.searchResourceStore;
			//count+";"+dir+";"+name+";"+fileType+";"+resource
			for(String strs:searchResourceStore){
				String[] strArray = strs.split(";");
				if(strArray.length == 5 && Integer.parseInt(mainid) == Integer.parseInt(strArray[0])){
					fileDir = strArray[1];
					fileName = strArray[2];
					fileType = strArray[3];
					resources = strArray[4];
					break;
				}
			}
//			String fileDir = request.getParameter("postId")==null?"":new String(request.getParameter("postId").getBytes("ISO8859-1"), "GBK");
//			String fileName = request.getParameter("title")==null?"":new String(request.getParameter("title").getBytes("ISO8859-1"), "GBK");
//			
//			String fileType = request.getParameter("type")==null?"":new String(request.getParameter("type").getBytes("ISO8859-1"), "UTF-8");
//			String resources = request.getParameter("resources")==null?"":new String(request.getParameter("resources").getBytes("ISO8859-1"), "UTF-8");
			
			String time = UtilDateTime.toDateTimeString(new Date()).replaceAll(" ", "_").replaceAll(":", "_");
			String searchoutput = "searchoutput";
			String readFileDir = request.getRealPath("/")+"modules"+File.separator+"metadata"+File.separator + searchoutput+File.separator;
			FileUtil.createFileDir(readFileDir);
			String targetFilename = readFileDir+"searchoutput_"+time+".html";
			
			String outPutTypeStr = "HTML";
			//System.out.println(targetFilename);
			
//			FileUtil.deletefile(targetFilename, "searchoutput_", ".html");
//        	FileUtil.deletefile(targetFilename, "picture", ".png");
//        	FileUtil.deletefile(targetFilename, "style", ".css");
			
        	//System.out.println(" searchShowItem fileDir:"+fileDir);
			//System.out.println(" searchShowItem fileName:"+fileName);
			//System.out.println(" searchShowItem fileType:"+fileType);
			//System.out.println(" searchShowItem outPutTypeStr:"+outPutTypeStr);
			//System.out.println(" searchShowItem targetFilename:"+targetFilename);
			//System.out.println(" searchShowItem resources:"+resources);
        	
			boolean isSuccess = MetadataUtil.createReport4Search(fileDir, fileName, fileType, 
					outPutTypeStr, targetFilename, 
					resources);
			
			String reportJSON = Messages.getString("Metadata.Report.Message.CreateReportFail");
			if(isSuccess){
				
	        
				File fileOutput = FileUtil.getFile(targetFilename, "searchoutput_"+time, ".html");
				boolean isExists = FileUtil.findfileExists(fileOutput);
				if(isExists){
					FileUtil.deletefileOther(targetFilename, "searchoutput_", ".html",fileOutput.getName(),10);
					reportJSON = FileUtil.readFile(fileOutput.getAbsolutePath());
					int styleIdx = reportJSON.indexOf("rel=\"stylesheet\" href=\"");
					if(styleIdx > 0){
					String firstStyleStr = reportJSON.substring(0, styleIdx);
					String lastStyleStr = reportJSON.substring(styleIdx+23);
					styleIdx = lastStyleStr.indexOf("\"");
					if(styleIdx > 0){
						fileOutput = new File(readFileDir + lastStyleStr.substring(0, styleIdx));
						isExists = FileUtil.findfileExists(fileOutput);
						if(isExists){
							FileUtil.deletefileOther(targetFilename, "style", ".css",fileOutput.getName(),10);
							reportJSON = firstStyleStr + "rel=\"stylesheet\" href=\"modules/metadata/"+searchoutput+"/" +lastStyleStr;
						}
					}
					int imgIdx = reportJSON.indexOf("src=\"");
					if(imgIdx >0){
						String firstImgStr = reportJSON.substring(0, imgIdx);
						String lastImgStr = reportJSON.substring(imgIdx+5);
						imgIdx = lastImgStr.indexOf("\"");
						if(imgIdx > 0){
							fileOutput = new File(readFileDir + lastImgStr.substring(0, imgIdx));
							isExists = FileUtil.findfileExists(fileOutput);
							if(isExists){
								FileUtil.deletefileOther(targetFilename, "picture", ".png",fileOutput.getName(),10);
								reportJSON = firstImgStr + "src=\"modules/metadata/"+searchoutput+"/" +lastImgStr;
							}
						}
					}
				}
			}
			}
			//System.out.println(reportJSON);
			response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0L);
            response.setContentType("text/html; charset=UTF-8");
            response.getWriter().write(reportJSON);
            response.getWriter().close();
		}
	}
}
