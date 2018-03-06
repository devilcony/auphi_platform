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
package com.auphi.ktrl.system.resourceAuth;

import java.io.IOException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.auphi.ktrl.system.repository.bean.RepositoryBean;
import com.auphi.ktrl.system.repository.util.RepositoryUtil;
import com.auphi.ktrl.system.resourceAuth.util.ResourceAuthUtil;
import com.auphi.ktrl.system.user.bean.UserBean;
import com.auphi.ktrl.system.user.util.UserUtil;
import com.auphi.ktrl.util.PageInfo;
import com.auphi.ktrl.util.PageList;

public class ResourceAuthServlet extends HttpServlet
{

    private static Logger logger = Logger.getLogger(ResourceAuthServlet.class);
    private static final long serialVersionUID = 6858380063564549328L;

    public static String parameter_page = "page" ;    
    public static String parameter_action = "action" ;
    public static String parameter_user_id = "user_id" ;
    public static String parameter_rep_name = "rep_name" ;
    public static String parameter_resource_ids = "resource_ids" ;
    
    public static String attribute_page_list = "pageList" ;
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
    {
        doPost(request,response) ;
    }
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
    {
        String actionAttr = "action" ;
        String parameterAttr = "parameter" ;
        int page = request.getParameter(parameter_page)==null?1:Integer.parseInt(request.getParameter("page"));
        UserBean userBean = request.getSession().getAttribute("userBean")==null?null:(UserBean)request.getSession().getAttribute("userBean");
        
        String action = request.getParameter(actionAttr) ;
        String parameter = request.getParameter(parameterAttr) ;
        
        if("list".equals(action))
        {//list
            PageList pageList = getUserPageList(page, userBean) ;
            request.setAttribute(attribute_page_list, pageList);
            RequestDispatcher dispatcher = request.getRequestDispatcher("modules/system/resourceauthlist.jsp"); 
            dispatcher.forward(request, response); 
        } 
        else if ("getRepList".equals(action))
        {
        	List<RepositoryBean> repList = RepositoryUtil.getAllRepositories(userBean);
        	String repListString = "";
        	for(RepositoryBean repBean : repList){
        		if("".equals(repListString)){
        			repListString = repListString + repBean.getRepositoryName();
        		}else {
        			repListString = repListString + "," + repBean.getRepositoryName();
        		}
        	}
            response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0L);
            response.setContentType("text/html; charset=UTF-8");
            response.getWriter().write(repListString);
//            System.out.println(repList) ;
            response.getWriter().close();            
        } 
        else if ("getResourceTree".equals(action))
        {
            String user_id = request.getParameter(parameter_user_id) ;
            String rep_name = request.getParameter(parameter_rep_name)==null?"":new String(request.getParameter(parameter_rep_name).getBytes("ISO8859-1"), "UTF-8") ;
            String jsonTree = ResourceAuthUtil.getResourceTreeJson(user_id,rep_name) ;
            response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0L);
            response.setContentType("text/html; charset=UTF-8");
            response.getWriter().write(jsonTree);
//            System.out.println(jsonTree) ;
            response.getWriter().close();  
        }
        else if ("assignResourcesToUser".equals(action))
        {
            String user_id = request.getParameter(parameter_user_id) ;
            String rep_name = request.getParameter(parameter_rep_name) ;
            String resource_ids = request.getParameter(parameter_resource_ids) ;
            ResourceAuthUtil.authResourcesToUser(user_id, rep_name, resource_ids) ;
        }
        
        logger.debug(action+":"+parameter) ;
    }
    public static PageList getUserPageList(int pageNo, UserBean userBean)
    {
        PageList pageList = new PageList() ;
        int start = (pageNo-1)*PageInfo.PAGESIZE ;
        int end = pageNo*PageInfo.PAGESIZE ;
        List<UserBean> userList = UserUtil.getUsers(start, end, userBean) ;
        int count = UserUtil.getUserCount() ;
        PageInfo pageInfo = new PageInfo(pageNo,count) ;
        pageList.setList(userList) ;
        pageList.setPageInfo(pageInfo) ;
        
        return pageList ;
    }    
}
