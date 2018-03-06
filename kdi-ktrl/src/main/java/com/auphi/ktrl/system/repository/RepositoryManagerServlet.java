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
package com.auphi.ktrl.system.repository;

import java.io.IOException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.auphi.ktrl.engine.KettleEngine;
import com.auphi.ktrl.engine.impl.KettleEngineImpl4_3;
import com.auphi.ktrl.system.repository.bean.RepositoryBean;
import com.auphi.ktrl.system.repository.util.RepositoryUtil;
import com.auphi.ktrl.system.user.bean.UserBean;
import com.auphi.ktrl.util.Constants;
import com.auphi.ktrl.util.PageInfo;
import com.auphi.ktrl.util.PageList;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver;

public class RepositoryManagerServlet extends HttpServlet
{

    private static Logger logger = Logger.getLogger(RepositoryManagerServlet.class);
    private static final long serialVersionUID = 6858380063564549328L;
    
    public static String parameter_action = "action" ;
    public static String parameter_repository_id = "repository_id" ;
    public static String parameter_user_name = "user_name" ;
    public static String parameter_password = "password" ;
    public static String parameter_jdbc_driver = "jdbc_driver" ;
    public static String parameter_jdbc_url = "jdbc_url" ;
    public static String parameter_repository_name = "repository_name" ;
    public static String parameter_version= "version" ;
    public static String parameter_page = "page" ;
    public static String parameter_db_host = "db_host";
    public static String parameter_db_port = "db_port" ;
    public static String parameter_db_name = "db_name" ;
    public static String parameter_db_type = "db_type" ;
    public static String parameter_db_access="db_access" ;
    public static String check_table_name="R_USER" ;
    
    public static String parameter_pagelist = "pageList" ;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
    {
        doPost(request,response) ;
    }
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
    {
        String action = request.getParameter(parameter_action) ;

        int page = request.getParameter(parameter_page)==null?1:Integer.parseInt(request.getParameter("page"));
        UserBean userBean = request.getSession().getAttribute("userBean")==null?null:(UserBean)request.getSession().getAttribute("userBean");
        
        if("list".equals(action))
        {//list
            PageList pageList = getRepositoryPageList(page, userBean) ;
            request.setAttribute(parameter_pagelist, pageList);
            String databaseType = Constants.get("SupportDatabaseTypes");
            String[] databaseTypes = databaseType.split(",");
            request.setAttribute("databaseTypes", databaseTypes);            
            RequestDispatcher dispatcher = request.getRequestDispatcher("modules/system/repositorylist.jsp"); 
            dispatcher.forward(request, response); 
        }
        else if("beforeUpdate".equals(action)){//get data before update
            String repository_id  = request.getParameter("repository_id");
            RepositoryBean repositoryBean = RepositoryUtil.getRepositoryByID(Integer.parseInt(repository_id));
            
            XStream xstream = new XStream(new JettisonMappedXmlDriver());
            xstream.alias("item", RepositoryBean.class);
            
            response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0L);
            response.setContentType("text/html; charset=UTF-8");
            response.getWriter().write(xstream.toXML(repositoryBean));
//            System.out.println(xstream.toXML(repositoryBean)) ;
            response.getWriter().close();
        }else if("delete".equals(action)){//delete
            String repositoryIDs = request.getParameter("repository_id");
            RepositoryUtil.deleteRepository(repositoryIDs);           
            response.sendRedirect("repositorymanager?action=list");
        }else if("createRepository".equals(action)){
        	RepositoryBean repBean = getRepositoryBean(request);        	
        	KettleEngine kettleEngine = new KettleEngineImpl4_3(); 
        	kettleEngine.addNewTables(repBean);
        	kettleEngine.createRepository(repBean, Boolean.parseBoolean(request.getParameter("update").toString()));
            if(repBean.getRepositoryID()==0){//创建新的资源库管理信息
            	RepositoryUtil.createRepository(repBean);
            }else{//更新资源库管理信息
            	RepositoryUtil.updateRepository(repBean);
            }        		
            response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0L);
            response.setContentType("text/html; charset=UTF-8");
            response.getWriter().write("OK");
            response.getWriter().close(); 
        }else if("addRepository".equals(action)){//只添加资源库，不更新
        	RepositoryBean repBean = getRepositoryBean(request);
        	KettleEngine kettleEngine = new KettleEngineImpl4_3(); 
        	kettleEngine.addNewTables(repBean);
        	if(repBean.getRepositoryID()!=0){//在修改资源库时，选择不更新资源库
        		RepositoryUtil.updateRepository(repBean);
        	}else{
            	RepositoryUtil.createRepository(repBean);
        	}
            response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0L);
            response.setContentType("text/html; charset=UTF-8");
            response.getWriter().write("OK");
            response.getWriter().close();
        }else if("checkTableExist".equals(action)){
        	RepositoryBean repBean = getRepositoryBean(request);
        	KettleEngine kettleEngine = new KettleEngineImpl4_3();
        	String isExist = kettleEngine.checkTableExist(repBean, check_table_name);
            response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0L);
            response.setContentType("text/html; charset=UTF-8");
            response.getWriter().write(isExist);
            response.getWriter().close();        	
        }else if("checkRepositoryNameExist".equals(action)){
        	RepositoryBean repBean = getRepositoryBean(request);
        	boolean isExist = RepositoryUtil.checkRepositoryNameExist(repBean.getRepositoryName());
            response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0L);
            response.setContentType("text/html; charset=UTF-8");
            response.getWriter().write(Boolean.toString(isExist));
            response.getWriter().close();  
        }
    }
    private PageList getRepositoryPageList(int pageNo, UserBean userBean)
    {
        PageList pageList = new PageList() ;
        int start = (pageNo-1)*PageInfo.PAGESIZE ;
        int end = pageNo*PageInfo.PAGESIZE ;
        List<RepositoryBean> repList = RepositoryUtil.getRepository(start, end, userBean);
        int count = RepositoryUtil.getRepositoryCount() ;
        PageInfo pageInfo = new PageInfo(pageNo,count) ;
        pageList.setList(repList) ;
        pageList.setPageInfo(pageInfo) ;        
        return pageList ;
    }
    private RepositoryBean getRepositoryBean(HttpServletRequest request)
    {
        RepositoryBean repositoryBean = new RepositoryBean() ;
        String repository_id =request.getParameter(parameter_repository_id); 
        if("".equals(repository_id)||repository_id==null)
        	repository_id="0";
        repositoryBean.setRepositoryID(Integer.parseInt(repository_id)) ;
        repositoryBean.setUserName(request.getParameter(parameter_user_name)) ;
        repositoryBean.setPassword(request.getParameter(parameter_password)) ;
        repositoryBean.setDbAccess("Native") ;
        repositoryBean.setDbHost(request.getParameter(parameter_db_host)) ;
        repositoryBean.setDbName(request.getParameter(parameter_db_name)) ;
        repositoryBean.setDbPort(request.getParameter(parameter_db_port)) ;
        repositoryBean.setDbType(request.getParameter(parameter_db_type)) ;
        repositoryBean.setRepositoryName(request.getParameter(parameter_repository_name)) ;
        repositoryBean.setVersion(KettleEngine.VERSION_4_3) ;
        UserBean userBean = request.getSession().getAttribute("userBean")==null?null:(UserBean)request.getSession().getAttribute("userBean");
        repositoryBean.setOrgId(userBean.getOrgId());
        
        return repositoryBean ;
    }
}
