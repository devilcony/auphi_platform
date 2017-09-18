<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="com.auphi.ktrl.i18n.Messages" %>
<%@ page import="com.auphi.ktrl.util.Constants" %>
<%@ page import="com.auphi.ktrl.system.user.bean.UserBean" %>
<%@ page import="com.auphi.ktrl.system.user.util.UserUtil" %>
<%@ page import="com.auphi.ktrl.system.priviledge.bean.PriviledgeType" %>
<%
	String user_id = session.getAttribute("user_id")==null?"":session.getAttribute("user_id").toString();
	UserBean userBean = session.getAttribute("userBean")==null?null:(UserBean)session.getAttribute("userBean");
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>无标题文档</title>
<link rel="stylesheet" type="text/css" href="common/css/style.css" />

</head>


<body>

	
    
    <div class="mainindex">
    
    
    <div class="welinfo">
    <span><img src="images/sun.png" alt="天气" /></span>
    <b><%=userBean==null?"":userBean.getNick_name() %> <span ></span>  欢迎使用傲飞数据整合平台！</b>
    
    </div>
    
    <div class="xline"></div>
    
    <ul class="iconlist">


        <li><a href="javascript:window.parent.toLoadurl('schedule?action=list','schedule_main','<%=Messages.getString("Default.Jsp.Menu.Schedule") %>');"><img height="40px"src="images/icons/icon_diaodu.png" /><p>周期调度</p></a></li>
    <li><a href="javascript:window.parent.toLoadurl('datasource/index.shtml','datasource_main','本地数据库管理');"><img height="40px"src="images/icons/icon_bendishujvku.png" /><p>本地数据库</p></a></li>
    <li><a href="javascript:window.parent.toLoadurl('usermanager?action=list','userManager_main','<%=Messages.getString("Default.Jsp.Menu.System.User") %>');"><img height="40px"src="images/icons/icon_yonghuguanli.png" /><p>用户管理</p></a></li>
    </ul>
    
    
    
    <div class="xline"></div>
    
    </div>
    
</body>

</html>
