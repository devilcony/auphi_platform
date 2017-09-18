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
package com.auphi.ktrl.system.user;

import java.io.IOException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.auphi.ktrl.system.user.bean.LoginResponse;
import com.auphi.ktrl.system.user.bean.UserBean;
import com.auphi.ktrl.system.user.util.UMStatus;
import com.auphi.ktrl.system.user.util.UserUtil;
import com.auphi.ktrl.util.Constants;
import com.auphi.ktrl.util.PageInfo;
import com.auphi.ktrl.util.PageList;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver;

public class UserManagerServlet extends HttpServlet
{

    private static final long serialVersionUID = 6858380063564549328L;
    
    public static String parameter_action = "action" ;
    public static String parameter_user_id = "user_id" ;
    public static String parameter_user_name = "user_name" ;
    public static String parameter_password = "password" ;
    public static String parameter_nick_name = "nick_name" ;
    public static String parameter_email = "email" ;
    public static String parameter_mobilephone = "mobilephone" ;
    public static String parameter_description = "description" ;
    public static String parameter_page = "page" ;
    public static String parameter_role_id = "role_id" ;
    public static String parameter_org_id = "org_id" ;
    
    public static String attribute_page_list = "pageList" ;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
    {
        doPost(request,response) ;
    }
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
    {
        String action = request.getParameter(parameter_action) ;
        UserBean loginUserBean = request.getSession().getAttribute("userBean")==null?null:(UserBean)request.getSession().getAttribute("userBean");

        int page = request.getParameter(parameter_page)==null?1:Integer.parseInt(request.getParameter("page"));
        
        if("list".equals(action))
        {//list
            PageList pageList = getUserPageList(page, loginUserBean) ;
            request.setAttribute(attribute_page_list, pageList);
            RequestDispatcher dispatcher = request.getRequestDispatcher("modules/system/userlist.jsp"); 
            dispatcher.forward(request, response); 
        }
        else if("create".equals(action))
        {//create user
            UserBean userBean = getUserBean(request, loginUserBean);
            UMStatus status = UserUtil.createUser(userBean);
            
            XStream xstream = new XStream(new JettisonMappedXmlDriver());
            xstream.alias("item", UMStatus.class) ;
            response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0L);
            response.setContentType("text/html; charset=UTF-8");
            response.getWriter().write(status.toJsonString());
//            System.out.println(status.toJsonString());
//            System.out.println(xstream.toXML(userBean));
            response.getWriter().close();        
            //response.sendRedirect("usermanager?action=list");
        }else if("beforeUpdate".equals(action)){//get data before update
            String user_id = request.getParameter("user_id");
            UserBean userBean = UserUtil.getUserById(user_id) ;
            
            XStream xstream = new XStream(new JettisonMappedXmlDriver());
            xstream.alias("item", UserBean.class);
            
            response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0L);
            response.setContentType("text/html; charset=UTF-8");
            response.getWriter().write(xstream.toXML(userBean));
//            System.out.println(xstream.toXML(userBean)) ;
            response.getWriter().close();
        }else if("update".equals(action)){//update
            UserBean userBean = getUserBean(request, loginUserBean) ;
            UMStatus ums = UserUtil.updateUser(userBean) ;
            
            response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0L);
            response.setContentType("text/html; charset=UTF-8");
            response.getWriter().write(ums.toJsonString());
            response.getWriter().close();
            //response.sendRedirect("usermanager?action=list");
        }else if("delete".equals(action)){//delete
            String user_ids = request.getParameter(parameter_user_id) ;
            UserUtil.deleteUsers(user_ids) ;
            
            response.sendRedirect("usermanager?action=list");
        }else if ("getRolesOfUser".equals(action)){
            String user_id = request.getParameter(parameter_user_id) ;
            String role_ids = UserUtil.getRolesOfUser(user_id) ;
            response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0L);
            response.setContentType("text/html; charset=UTF-8");
            response.getWriter().write(role_ids);
            response.getWriter().close();
        }
        else if ("assignRolesToUser".equals(action))
        {
            String user_id = request.getParameter(parameter_user_id) ;
            String role_ids = request.getParameter(parameter_role_id) ;
            UserUtil.assignRolesToUser(role_ids,user_id) ;
        }
        else if ("allusers".equals(action))
        {
            String users = UserUtil.getNonSystemUsers() ;
            response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0L);
            response.setContentType("text/html; charset=UTF-8");
            response.getWriter().write(users);
            response.getWriter().close();
        }
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
    private UserBean getUserBean(HttpServletRequest request, UserBean loginUserBean)
    {
        UserBean userBean = new UserBean() ;
        userBean.setUser_id(request.getParameter(parameter_user_id)) ;
        userBean.setUser_name(request.getParameter(parameter_user_name)) ;
        userBean.setPassword(request.getParameter(parameter_password)) ;
        userBean.setNick_name(request.getParameter(parameter_nick_name)) ;
        userBean.setEmail(request.getParameter(parameter_email)) ;
        userBean.setMobilephone(request.getParameter(parameter_mobilephone)) ;
        userBean.setDescription(request.getParameter(parameter_description)) ;
        if(!loginUserBean.isSuperAdmin()){
        	userBean.setOrgId(loginUserBean.getOrgId());
        }else {
        	userBean.setOrgId(request.getParameter(parameter_org_id)==null?loginUserBean.getOrgId():Integer.parseInt(request.getParameter(parameter_org_id)));
        }
        userBean.setStatus(UserUtil.STATUS_ACTIVE);
        return userBean ;
    }
}
