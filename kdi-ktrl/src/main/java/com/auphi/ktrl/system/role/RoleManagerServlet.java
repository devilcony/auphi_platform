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
package com.auphi.ktrl.system.role;

import java.io.IOException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.auphi.ktrl.system.priviledge.bean.PriviledgeType;
import com.auphi.ktrl.system.role.bean.RoleBean;
import com.auphi.ktrl.system.role.util.RoleUtil;
import com.auphi.ktrl.system.user.bean.UserBean;
import com.auphi.ktrl.system.user.util.UMStatus;
import com.auphi.ktrl.util.PageInfo;
import com.auphi.ktrl.util.PageList;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver;

public class RoleManagerServlet extends HttpServlet
{    
    private static final long serialVersionUID = 7676370875286092563L;
    
    public static String parameter_action = "action" ;
    public static String parameter_role_id = "role_id" ;
    public static String parameter_user_id = "user_id" ;
    public static String parameter_role_name= "role_name" ;
    public static String parameter_description = "description" ;
    public static String parameter_priviledges = "priviledges" ;
    public static String parameter_page = "page" ;
    public static String parameter_pagelist = "pageList" ;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
    {
        doPost(request,response) ;
    }
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
    {
        String action = request.getParameter(parameter_action) ;
        UserBean userBean = request.getSession().getAttribute("userBean")==null?null:(UserBean)request.getSession().getAttribute("userBean");
        int page = request.getParameter(parameter_page)==null?1:Integer.parseInt(request.getParameter(parameter_page));
        
        if("list".equals(action))
        {//list
            PageList pageList = getRolePageList(page) ;
            request.setAttribute(parameter_pagelist, pageList);
            RequestDispatcher dispatcher = request.getRequestDispatcher("modules/system/rolelist.jsp"); 
            dispatcher.forward(request, response); 
        }
        else if("create".equals(action))
        {//create user
            RoleBean roleBean = getRoleBean(request);
            UMStatus status = RoleUtil.createRole(roleBean);
            XStream xstream = new XStream(new JettisonMappedXmlDriver());
            xstream.alias("item", UMStatus.class) ;
            response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0L);
            response.setContentType("text/html; charset=UTF-8");
            response.getWriter().write(status.toJsonString());
//            System.out.println(status.toJsonString());
//            System.out.println(xstream.toXML(roleBean));
            response.getWriter().close();        
            //response.sendRedirect("rolemanager?action=list");
        }else if("beforeUpdate".equals(action)){//get data before update
            String role_id = request.getParameter(parameter_role_id);
            RoleBean roleBean = RoleUtil.getRoleById(role_id) ;
            XStream xstream = new XStream(new JettisonMappedXmlDriver());
            xstream.alias("item", RoleBean.class);
            response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0L);
            response.setContentType("text/html; charset=UTF-8");
            response.getWriter().write(xstream.toXML(roleBean));
            response.getWriter().close();
        }else if("update".equals(action)){//update
            RoleBean roleBean = getRoleBean(request) ;
            UMStatus ums = RoleUtil.updateRole(roleBean) ;
            response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0L);
            response.setContentType("text/html; charset=UTF-8");
            response.getWriter().write(ums.toJsonString());
            response.getWriter().close();
        }else if("delete".equals(action)){//delete
            String role_ids = request.getParameter(parameter_role_id) ;
            RoleUtil.deleteRoles(role_ids) ;
            response.sendRedirect("rolemanager?action=list");
        }else if ("allpriviledges".equals(action)){
            String jsonTree = RoleUtil.getAllPrivileges() ;
            response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0L);
            response.setContentType("text/html; charset=UTF-8");
            response.getWriter().write(jsonTree);
            response.getWriter().close();
        }else if ("allroles".equals(action)){
            String jsonTree = RoleUtil.getAllRoles(userBean) ;
            response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0L);
            response.setContentType("text/html; charset=UTF-8");
            response.getWriter().write(jsonTree);
            response.getWriter().close();
        }else if ("getUsersOfRole".equals(action)){
            String role_id = request.getParameter(parameter_role_id);
            String user_ids = RoleUtil.getUsersOfRole(role_id) ;
            response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0L);
            response.setContentType("text/html; charset=UTF-8");
            response.getWriter().write(user_ids);
            response.getWriter().close();
        }else if ("assignUsersToRole".equals(action)){
            String role_id = request.getParameter(parameter_role_id);
            String user_ids = request.getParameter(parameter_user_id);
            UMStatus ums = RoleUtil.assignUsersToRole(role_id, user_ids) ;
        }
    }
    public static PageList getRolePageList(int pageNo)
    {
        PageList pageList = new PageList() ;
        int start = (pageNo-1)*PageInfo.PAGESIZE ;
        int end = pageNo*PageInfo.PAGESIZE ;
        List<RoleBean> roleList = RoleUtil.getRoles(start, end) ;
        int count = RoleUtil.getRoleCount() ;
        PageInfo pageInfo = new PageInfo(pageNo,count) ;
        pageList.setList(roleList) ;
        pageList.setPageInfo(pageInfo) ;
        
        return pageList ;
    }
    private RoleBean getRoleBean(HttpServletRequest request)
    {
        RoleBean roleBean = new RoleBean() ;
        
        roleBean.setRole_id(request.getParameter(parameter_role_id)) ;
        roleBean.setRole_name(request.getParameter(parameter_role_name)) ;
        roleBean.setDescription(request.getParameter(parameter_description)) ;
        String priviledges = request.getParameter(parameter_priviledges) ;
        long pri = PriviledgeType.getPriviledges(priviledges) ;
        roleBean.setPriviledges(pri) ;
        
        return roleBean ;
    }
}
