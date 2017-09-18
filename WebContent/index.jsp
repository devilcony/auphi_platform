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
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="shortcut icon" href="images/platform.ico" />
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<%@ include file="common/extjs.jsp" %>
<link rel="stylesheet" type="text/css" href="common/css/mystyle.css" />
<title><%=Messages.getString("Default.Jsp.Title") %></title>
 
<script type="text/javascript">


var menu = new Ext.Panel({
    iconCls:'icon_caidan',
    region: 'west',
    collapsible: true,
    split: true,
    id: 'MainMenu',
    title:'<%=Messages.getString("Default.Jsp.Menu") %>',
    width: 205,
    minSize: 200,
    maxSize: 400,
    layout: 'accordion',
	items:[{
         title:"<span class='menu_text' >元数据管理</span>",
         iconCls:'ic_menu_root',
         items:[
             {
                 xtype: 'treepanel',
                 border: 0,
                 rootVisible: false,
                 root: {
                     expanded: true,
                     children: [
                         { iconCls:'icon_influence',  text: "<span class='menu_text' ><%=Messages.getString("Default.Jsp.Menu.Meta.Influence") %></span>", leaf: true, href: 'javascript:toLoadurl(\'metadata?action=influence\',\'metadataInfluence_main\',\'<%=Messages.getString("Default.Jsp.Menu.Meta.Influence") %>\')' },
                         { iconCls:'icon_descent',  text: "<span class='menu_text' ><%=Messages.getString("Default.Jsp.Menu.Meta.Descent") %></span>", leaf: true, href: 'javascript:toLoadurl(\'metadata?action=descent\',\'metadataDescent_main\',\'<%=Messages.getString("Default.Jsp.Menu.Meta.Descent") %>\')' }
                     ]
                 }
             }
         ]
	 	},{
     	title:"<span class='menu_text' >服务接口维护</span>",
     	iconCls:'icon_fuwujiekou',
     	items:[
     		{
              xtype: 'treepanel',
              border: 0,
              rootVisible: false,
              root: {
              	expanded: true,
                children: [
                   { iconCls:'icon_jiekouguanli',  text: "<span class='menu_text' >服务接口管理</span>", leaf: true, href: 'javascript:toLoadurl(\'service/index.shtml\',\'service_main\',\'服务接口管理\')' }
                ]
               }
           }
     	]},{
     	title:"<span class='menu_text' >服务权限管理</span>",
     	iconCls:'icon_fuwuquanxian',
     	items:[
     		{
              xtype: 'treepanel',
              border: 0,
              rootVisible: false,
              root: {
              	expanded: true,
                children: [
                   { id: "01",iconCls:'icon_fuwuyonghu', text: "<span class='menu_text' >服务用户管理</span>", leaf: true, href: 'javascript:toLoadurl(\'serviceUser/index.shtml\',\'serviceUser_main\',\'服务用户管理\')' },
                   { id: "02",iconCls:'icon_fuwushouquanguanli', text: "<span class='menu_text' >服务授权管理</span>", leaf: true, href: 'javascript:toLoadurl(\'serviceAuth/index.shtml\',\'serviceAuth_main\',\'服务授权管理\')' }
                ]
               }
           }
     	]},{
     	title:"<span class='menu_text' >服务接口监控</span>",
     	iconCls:'icon_jiekoujiankong',
     	items:[
     		{
              xtype: 'treepanel',
              border: 0,
              rootVisible: false,
              root: {
              	expanded: true,
                children: [
                   {iconCls:'icon_jiekoujiankong2',text: "<span class='menu_text' >服务接口调用监控</span>", leaf: true, href: 'javascript:toLoadurl(\'serviceMonitor/index.shtml\',\'serviceMonitor_main\',\'服务接口调用监控\');'}
                ]
               }
           }
     	]},{
     	title:"<span class='menu_text' >任务调度监控</span>",
     	iconCls:'icon_renwujiankong',
     	items:[
     		{
              xtype: 'treepanel',
              border: 0,
              rootVisible: false,
              root: {
              	expanded: true,
                children: [
                   //{ iconCls:'icon_diaodu',text: "调度", leaf: true, href: 'javascript:toLoadurl(\'schedule?action=index\',\'schedule_main\',\'<%=Messages.getString("Default.Jsp.Menu.Schedule") %>\');'},
                   { iconCls:'icon_diaodu',text: "<span class='menu_text' >周期调度</span>", leaf: true, href: 'javascript:toLoadurl(\'schedule?action=list\',\'schedule_main\',\'<%=Messages.getString("Default.Jsp.Menu.Schedule") %>\');'},
       		       { iconCls:'icon_diaodu',text: "<span class='menu_text' >事件调度</span>", leaf: true, href: 'javascript:toLoadurl(\'dschedule?action=list\',\'dschedule_main\',\'<%=Messages.getString("Default.Jsp.Menu.DSchedule") %>\');'},
       		       { iconCls:'icon_jiankong',text: "<span class='menu_text' ><%=Messages.getString("Default.Jsp.Menu.Monitor") %>", leaf: true, href: 'javascript:toLoadurl(\'monitor?action=list\',\'monitor_main\',\'<%=Messages.getString("Default.Jsp.Menu.Monitor") %>\');'}
                ]
               }
           }
     	]},{
     	title:"<span class='menu_text' >数据源管理</span>",
     	iconCls:'icon_shujuyuanguanli',
     	items:[
     		{
              xtype: 'treepanel',
              border: 0,
              rootVisible: false,
              root: {
              	expanded: true,
                children: [
                    { iconCls:'icon_database',text: "<span class='menu_text' >本地数据库管理</span>", leaf: true, href: 'javascript:toLoadurl(\'datasource/index.shtml\',\'datasource_main\',\'本地数据库管理\');'},
					{iconCls:'icon_ftp', text: "<span class='menu_text' >本地FTP管理</span>", leaf: true, href: 'javascript:toLoadurl(\'ftpMrg/index.shtml\',\'ftpMrg_main\',\'本地FTP管理\');'},
					{ iconCls:'icon_shujujishi',text: "<span class='menu_text' >远程数据库管理</span>", leaf: true, href: 'javascript:toLoadurl(\'oracleDatasource/index.shtml\',\'oracleDatasource_main\',\'远程数据库管理\');'},
       		    	{ iconCls:'icon_hadoop',text: "<span class='menu_text' >Hadoop集群管理</span>", leaf: true, href: 'javascript:toLoadurl(\'hadoopMrg/index.shtml\',\'hadoopMrg_main\',\'Hadoop集群管理\');'}
                ]
               }
           }
     	]},{
     	title:"<span class='menu_text' >主数据管理</span>",
     	iconCls:'icon_zhushujuguanli',
     	items:[
     		{
              xtype: 'treepanel',
              border: 0,
              rootVisible: false,
              root: {
              	expanded: true,
                children: [
                    { iconCls:'icon_zhushujumoxing',text: "<span class='menu_text' >主数据模型</span>", leaf: true, href: 'javascript:toLoadurl(\'mdmModel/index.shtml\',\'mdmModel_main\',\'主数据模型\');'},
					{ iconCls:'icon_shujubiao',text: "<span class='menu_text' >主数据表</span>", leaf: true, href: 'javascript:toLoadurl(\'mdmTable/index.shtml\',\'mdmTable_main\',\'主数据表\');'},
					{ iconCls:'icon_shujuqingxi',text: "<span class='menu_text' >数据映射</span>", leaf: true, href: 'javascript:toLoadurl(\'mdmDataClean/index.shtml\',\'dataClear_main\',\'数据清洗\');'}
                ]
               }
           }
     	]},{
			 title:"<span class='menu_text' >数据质量</span>",
			 iconCls:'icon_zhushujuguanli',
			 items:[
				 {
					 xtype: 'treepanel',
					 border: 0,
					 rootVisible: false,
					 root: {
						 expanded: true,
						 children: [
							 { iconCls:'icon_zhushujumoxing',text: "<span class='menu_text' >数据剖析</span>", leaf: true, href: 'javascript:toLoadurl(\'profileTableResult/index.shtml\',\'profileTableResult\',\'数据剖析\');'},
							 { iconCls:'icon_shujubiao',text: "<span class='menu_text' >数据稽核</span>", leaf: true, href: 'javascript:toLoadurl(\'compareSqlResult/index.shtml\',\'compareSqlResult\',\'数据稽核\');'}
						 ]
					 }
				 }
			 ]},
     	{
     	title:"<span class='menu_text' ><%=Messages.getString("Default.Jsp.Menu.HA") %></span>",
     	iconCls:'icon_HAjiqun',
     	items:[
     		{
              xtype: 'treepanel',
              border: 0,
              rootVisible: false,
              root: {
              	expanded: true,
                children: [
                    {iconCls:'icon_etlfuwuqiguanli', text: "<span class='menu_text' ><%=Messages.getString("Default.Jsp.Menu.HA.ServerManage") %></span>", leaf: true, href: 'javascript:toLoadurl(\'servermanage?action=list\',\'serverManage_main\',\'<%=Messages.getString("Default.Jsp.Menu.HA.ServerManage") %>\');'},
               		 {iconCls:'icon_hajiqunguanli', text: "<span class='menu_text' ><%=Messages.getString("Default.Jsp.Menu.HA.HAManage") %></span>", leaf: true, href: 'javascript:toLoadurl(\'hamanage?action=list\',\'haManage_main\',\'<%=Messages.getString("Default.Jsp.Menu.HA.HAManage") %>\');'}
                ]
               }
           }
     	]}
     	
     	<%
		boolean isAdmin = UserUtil.isAdmin(Integer.parseInt("".endsWith(user_id)?"0":user_id));
        if(isAdmin){
		%>
		,{
     	title:"<span class='menu_text' ><%=Messages.getString("Default.Jsp.Menu.System") %></span>",
     	iconCls:'icon_xitongguanli',
     	items:[
     		{
              xtype: 'treepanel',
              border: 0,
              rootVisible: false,
              root: {
              	expanded: true,
                children: [
                	
                	{ iconCls:'icon_youxiangfuwuqi',text: "<span class='menu_text' ><%=Messages.getString("Default.Jsp.Menu.System.Mail") %></span>", leaf: true, href: 'javascript:toLoadurl(\'mail?action=manage\',\'mail_main\',\'<%=Messages.getString("Default.Jsp.Menu.System.Mail") %>\');'},
                	{ iconCls:'icon_yonghuguanli',text: "<span class='menu_text' ><%=Messages.getString("Default.Jsp.Menu.System.User") %></span>", leaf: true, href: 'javascript:toLoadurl(\'usermanager?action=list\',\'userManager_main\',\'<%=Messages.getString("Default.Jsp.Menu.System.User") %>\');'},
                	{ iconCls:'icon_jiaoseguanli',text: "<span class='menu_text' ><%=Messages.getString("Default.Jsp.Menu.System.Role") %></span>", leaf: true, href: 'javascript:toLoadurl(\'rolemanager?action=list\',\'roleManager_main\',\'<%=Messages.getString("Default.Jsp.Menu.System.Role") %>\');'},
                	{ iconCls:'icon_xukeguanli',text: "<span class='menu_text' ><%=Messages.getString("Default.Jsp.Menu.System.Permit") %></span>", leaf: true, href: 'javascript:toLoadurl(\'resourceauthmanager?action=list\',\'resourceAuth_main\',\'<%=Messages.getString("Default.Jsp.Menu.System.Permit") %>\');'},
                	{ iconCls:'icon_ziyuankuguanli',text: "<span class='menu_text' ><%=Messages.getString("Default.Jsp.Menu.System.Repository") %></span>", leaf: true, href: 'javascript:toLoadurl(\'repositorymanager?action=list\',\'repositoryManager_main\',\'<%=Messages.getString("Default.Jsp.Menu.System.Repository") %>\');'}
                ]
               }
           }
     	]}
<%
        }
%>
    ]}); 

var tabs;
var ie = /*@cc_on!@*/!1;
var tabsHeight;
if(ie){
	tabsHeight = 456;
}else {
	tabsHeight = 500;
}
Ext.onReady(function(){
	Ext.create('Ext.container.Viewport',{
        layout:'border',
        items:[
            {
                region: 'north',
                border: false,
                contentEl: 'north',
                height: 75,
                margins: '0 0 0 0',
                bbar:[{
                    iconCls:'icon_yonghu',
                    text:'<%=userBean==null?"":userBean.getNick_name() + Messages.getString("Default.Jsp.Welcome") %>',
                    handler:function(){ }
                },'-',{
                    id:'system_time',
                    iconCls:'icon_time',
                    type:'textfield',
                    text:''
                },'->',{
                    iconCls:'icon_shuaxin',
                    text:'<%=Messages.getString("Default.Jsp.Top.Button.Refresh") %>',    //刷新
                    handler:function(){
                        window.location.reload();
                    }
                },'-',{
                    iconCls: 'icon_tuichu',
                    text:'<%=Messages.getString("Default.Jsp.Top.Button.LogOut") %>',  //退出
                    handler: function(){
                        logOut();
                    }
                }]
            }, {
                region:'south',
                contentEl: 'south',
                height: 20
            }, menu, 
			tabs = Ext.createWidget('tabpanel', {
               	region: 'center', // a center region is ALWAYS required for border layout
               	deferredRender: false,
               	monitorResize:true,
        		minTabWidth: 115, 
        		tabWidth:135, 
        		tabMargin:0,
        		enableTabScroll:true, 
               	activeTab: 0, 
               	height:document.body.clientHeight,
               	defaults: {autoScroll:true},
               	items: []
            })
		]
	});
	
	initWelcome();
	
	
});

function initWelcome(){
	var id = "mainTab";
	var h = tabs.getHeight() - tabs.tabBar.getHeight()-2;
	
	var html = '<iframe id="welcome" name="welcome" frameborder="0" width="100%" height="'+h+'" src="welcome.jsp"></iframe>';
	
	var name = '<%=Messages.getString("Default.Jsp.Tab.Welcome") %>';
	addTab(id, name, html,false); 
    
	myMask = new Ext.LoadMask(Ext.getBody(), {
        msg: '<%=Messages.getString("Default.Jsp.Openning") %>',
        removeMask: true
    });
	myMask.show();
	setTimeout("myMask.hide()", 1000);
}


function toLoadurl(url,id,name){
	var iframeid = "frame" + id;
	var h = tabs.getHeight() - tabs.tabBar.getHeight()-2;
	
	var html = "<iframe frameborder='0'  id='" + iframeid + "' name='" + iframeid + "' width='100%' src='' height="+h+"px></iframe>";
	var iframe_old = document.getElementById(iframeid);
	if(iframe_old==null){//if do not have ,create a new iframe
		addTab(id, name, html,true); 
	    document.getElementById(iframeid).src = url;
	}else {//if have ,make it active
		tabs.setActiveTab(id);
	    document.getElementById(iframeid).src = url;
	}
	myMask = new Ext.LoadMask(Ext.getBody(), {
        msg: '<%=Messages.getString("Default.Jsp.Openning") %>',
        removeMask: true
    });
	myMask.show();
	setTimeout("myMask.hide()", 1000);
	//IFrameResize(iframeid);
}

function addTab(id,name,html,closable){
	var newtab = tabs.add({ 
		id: id,
		title: name, 
		html: html,
		closable:closable,  
       	autoScroll: true  
	});
	newtab.setHeight(tabs.getHeight() - tabs.tabBar.getHeight());
	newtab.show();
}

function confirm_logout(){
    Ext.Msg.confirm('<%=Messages.getString("Default.Jsp.Confirm.Title") %>', '<%=Messages.getString("Default.Jsp.Confirm.Title") %>', function(btn, text) {
        if (btn == 'yes') {
            logOut();
        }
    });
}

function logOut(){
	document.getElementById('logOutForm').attributes["action"].value = "login";
	//document.getElementById('logOutForm').action = "usermanager";
	document.getElementById('logOutForm').submit();
}

function setTimeTD(){
	var now = new Date();
	var now_show = '<%=Messages.getString("Default.Jsp.Top.Time.Title") %>';
	now_show = now_show + now.getFullYear() + '<%=Messages.getString("Default.Jsp.Top.Time.Year") %>';
	now_show = now_show + (now.getMonth()+1) + '<%=Messages.getString("Default.Jsp.Top.Time.Month") %>';
	now_show = now_show + now.getDate() + '<%=Messages.getString("Default.Jsp.Top.Time.Day") %>';
	now_show = now_show + ' ' + now.getHours() + '<%=Messages.getString("Default.Jsp.Top.Time.Hour") %>';
	now_show = now_show + now.getMinutes() + '<%=Messages.getString("Default.Jsp.Top.Time.Minute") %>';
	now_show = now_show + now.getSeconds() + '<%=Messages.getString("Default.Jsp.Top.Time.Second") %>';
	//document.getElementById('system_time').innerHTML = now_show;

	Ext.getCmp("system_time").setText(now_show);


	setTimeout('setTimeTD()', 1000);
}


</script>
</head>
<%
	if(userBean == null){
%>
<body onload="logOut();setTimeTD();">;
<%
	}else {
%>
<body onload="setTimeTD();">
<%
	}
%>

	<div id="container">
		<div id="north">
			<table style="width:100%;" cellpadding="2" cellspacing="0" border="0" height="48px" background="images/index_north_bg.jpg">
				<tbody>
					<tr>
						<td rowspan="2" width="20px"></td>
						<td rowspan="2"><img src="images/logo.png" rowspan="2" height="32px"></td>
					</tr>
				</tbody>
			</table>
		</div>
		<div id="west"></div>
		<div id="center" class="x-hide-display">
    		<iframe id="welcome" name="welcome" frameborder="0" width="100%" height="100%" src="welcome.jsp"></iframe>
    	</div>
		<div id="south">
			<div style="text-align: center; color: black; background-color: #E0EAFF; width: 100%; padding: 2px; font: 12px;">
<%
	if("true".equals(Constants.get("USE_COPYRIGHT"))){
%>
				<%=Messages.getString("Default.Jsp.CopyRight") %>
<%
	}else {
%>
				&nbsp;		
<%
	}
%>				
			</div>
		</div>
	</div>
	<form action="" method="post" id="logOutForm" name="logOutForm" target="_top">
		<input type="hidden" name="action" id="action" value="logOut">
		<input type="hidden" name="errMsg" id="errMsg" value="">
	</form>
	<div id="targetWins"></div>
</body>
</html>
