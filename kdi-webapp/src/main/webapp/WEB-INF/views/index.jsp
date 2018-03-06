<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="com.auphi.ktrl.i18n.Messages" %>
<%@ page import="com.auphi.ktrl.util.Constants" %>
<%@ page import="com.auphi.ktrl.system.user.bean.UserBean" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%
	if(session.getAttribute("user_id")==null || "".equals(session.getAttribute("user_id"))){
		java.io.PrintWriter print = response.getWriter();
		print.println("<html>");
		print.println("<script>");
		print.println("window.open ('/login.do','_top')");
		print.println("</script>");
		print.println("</html>");
	}
	String user_id = session.getAttribute("user_id")==null?"":session.getAttribute("user_id").toString();
	UserBean userBean = session.getAttribute("userBean")==null?new UserBean():(UserBean)session.getAttribute("userBean");
	boolean isAdmin = userBean.isAdmin();
	boolean isSuperAdmin = userBean.isSuperAdmin();
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<link rel="shortcut icon" href="images/platform.ico" />
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<%@ include file="/common/extjs.jsp" %>
	<link rel="stylesheet" type="text/css" href="common/css/mystyle.css" />
	<title><spring:message code="Default.Jsp.Title" /></title>

	<script type="text/javascript">



		function tree_itemclick(node)  {

		}


		var menu = new Ext.Panel({
			iconCls:'icon_System_Navigation',
			region: 'west',
			collapsible: true,
			split: true,
			id: 'MainMenu',
			title:'<spring:message code="Default.Jsp.Menu" />',
			width: 205,
			minSize: 200,
			maxSize: 400,
			layout: 'accordion',
			items:[
				<%
                if(isSuperAdmin){
                %>
				{
					title:"<span class='menu_text' >服务接口维护</span>",
					iconCls:'icon_fuwujiekou',
					items:[
						{
							xtype: 'treepanel',
							border: 0,
							rootVisible: false,
							listeners:{ itemclick : tree_itemclick},
							root: {
								expanded: true,
								children: [
									{ iconCls:'icon_jiekouguanli',  text: "<span class='menu_text' >服务接口管理</span>", leaf: true, href: 'javascript:toLoadurl(\'service/index.shtml\',\'service_main\',\'服务接口管理\')' }
								]
							}
						}
					]},
				{
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
					]},
				{
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
					]},
				<%
                }
                %>
				{
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
									//{ iconCls:'icon_diaodu',text: "调度", leaf: true, href: 'javascript:toLoadurl(\'schedule?action=index\',\'schedule_main\',\'<spring:message code="Default.Jsp.Menu.Schedule" />\');'},
									{ iconCls:'icon_diaodu',text: "<span class='menu_text' >周期调度</span>", leaf: true, href: 'javascript:toLoadurl(\'schedule?action=list\',\'schedule_main\',\'<spring:message code="Default.Jsp.Menu.Schedule" />\');'},
									{ iconCls:'icon_diaodu',text: "<span class='menu_text' >事件调度</span>", leaf: true, href: 'javascript:toLoadurl(\'dschedule?action=list\',\'dschedule_main\',\'<spring:message code="Default.Jsp.Menu.DSchedule" />\');'},
									{ iconCls:'icon_jiankong',text: "<span class='menu_text' ><spring:message code="Default.Jsp.Menu.Monitor" />", leaf: true, href: 'javascript:toLoadurl(\'monitor?action=list\',\'monitor_main\',\'<spring:message code="Default.Jsp.Menu.Monitor" />\');'}
								]
							}
						}
					]},
				{
					title:"<span class='menu_text' ><spring:message code="Default.Jsp.Menu.Datasource" /></span>",
					iconCls:'icon_shujuyuanguanli',
					items:[
						{
							xtype: 'treepanel',
							border: 0,
							rootVisible: false,
							root: {
								expanded: true,
								children: [
									{ iconCls:'icon_database',text: "<span class='menu_text' ><spring:message code="Default.Jsp.Menu.Datasource.DB" /></span>", leaf: true, href: 'javascript:toLoadurl(\'datasource/index.shtml\',\'database_main\',\'<spring:message code="Default.Jsp.Menu.Datasource.DB" />\');'},
									{iconCls:'icon_ftp', text: "<span class='menu_text' ><spring:message code="Default.Jsp.Menu.Datasource.FTP" /></span>", leaf: true, href: 'javascript:toLoadurl(\'ftpMrg/index.shtml\',\'ftpMrg_main\',\'<spring:message code="Default.Jsp.Menu.Datasource.FTP" />\');'},
									{ iconCls:'icon_shujujishi',text: "<span class='menu_text' ><spring:message code="Default.Jsp.Menu.Datasource.DM" /></span>", leaf: true, href: 'javascript:toLoadurl(\'oracleDatasource/index.shtml\',\'oracleDatasource_main\',\'<spring:message code="Default.Jsp.Menu.Datasource.DM" />\');'},
									{ iconCls:'icon_hadoop',text: "<span class='menu_text' ><spring:message code="Default.Jsp.Menu.Datasource.Hadoop" /></span>", leaf: true, href: 'javascript:toLoadurl(\'hadoopMrg/index.shtml\',\'hadoopMrg_main\',\'<spring:message code="Default.Jsp.Menu.Datasource.Hadoop" />\');'}
								]
							}
						}
					]},
				{
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
					]}
				<%
                if(isSuperAdmin){
                %>
				,{
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
									{ iconCls:'icon_zhushujumoxing',text: "<span class='menu_text' >数据剖析</span>", leaf: true, href: 'javascript:toLoadurl(\'profileTableResult/index.shtml\',\'mdmModel_main\',\'数据剖析\');'},
									{ iconCls:'icon_shujubiao',text: "<span class='menu_text' >统计数据对比</span>", leaf: true, href: 'javascript:toLoadurl(\'compareSqlResult/index.shtml\',\'mdmTable_main\',\'原始数据对比\');'}
								]
							}
						}
					]
				}
				,{
					title:"<span class='menu_text' ><spring:message code="Default.Jsp.Menu.HA" /></span>",
					iconCls:'icon_HAjiqun',
					items:[
						{
							xtype: 'treepanel',
							border: 0,
							rootVisible: false,
							root: {
								expanded: true,
								children: [
									{iconCls:'icon_etlfuwuqiguanli', text: "<span class='menu_text' ><spring:message code="Default.Jsp.Menu.HA.ServerManage" /></span>", leaf: true, href: 'javascript:toLoadurl(\'servermanage?action=list\',\'serverManage_main\',\'<spring:message code="Default.Jsp.Menu.HA.ServerManage"/>\');'},
									{iconCls:'icon_hajiqunguanli', text: "<span class='menu_text' ><spring:message code="Default.Jsp.Menu.HA.HAManage" /></span>", leaf: true, href: 'javascript:toLoadurl(\'hamanage?action=list\',\'haManage_main\',\'<spring:message code="Default.Jsp.Menu.HA.HAManage"/>\');'}
								]
							}
						}
					]}
				<%
                }
               if(isAdmin || isSuperAdmin){
               %>
				,{
					title:"<span class='menu_text' ><spring:message code="Default.Jsp.Menu.System" /></span>",
					iconCls:'icon_xitongguanli',
					items:[
						{
							xtype: 'treepanel',
							border: 0,
							rootVisible: false,
							root: {
								expanded: true,
								children: [
									<%
                                    if(isSuperAdmin){
                                    %>
									{ iconCls:'icon_youxiangfuwuqi',text: "<span class='menu_text' ><spring:message code="Default.Jsp.Menu.System.Mail"/></span>", leaf: true, href: 'javascript:toLoadurl(\'mail?action=manage\',\'mail_main\',\'<spring:message code="Default.Jsp.Menu.System.Mail"/>\');'},
									{ iconCls:'icon_yonghuguanli',text: "<span class='menu_text' ><spring:message code="Default.Jsp.Menu.System.User"/></span>", leaf: true, href: 'javascript:toLoadurl(\'usermanager?action=list&issuperuser=1\',\'userManager_main\',\'<spring:message code="Default.Jsp.Menu.System.User"/>\');'},
									{ iconCls:'icon_jiaoseguanli',text: "<span class='menu_text' ><spring:message code="Default.Jsp.Menu.System.Role"/></span>", leaf: true, href: 'javascript:toLoadurl(\'rolemanager?action=list\',\'roleManager_main\',\'<spring:message code="Default.Jsp.Menu.System.Role"/>\');'},
									{ iconCls:'icon_xukeguanli',text: "<span class='menu_text' ><spring:message code="Default.Jsp.Menu.System.Permit"/></span>", leaf: true, href: 'javascript:toLoadurl(\'resourceauthmanager?action=list\',\'resourceAuth_main\',\'<spring:message code="Default.Jsp.Menu.System.Permit"/>\');'},
									{ iconCls:'icon_ziyuankuguanli',text: "<span class='menu_text' ><spring:message code="Default.Jsp.Menu.System.Repository"/></span>", leaf: true, href: 'javascript:toLoadurl(\'repositorymanager?action=list\',\'repositoryManager_main\',\'<spring:message code="Default.Jsp.Menu.System.Repository"/>\');'},
									{ iconCls:'icon_ziyuankuguanli',text: "<span class='menu_text' ><spring:message code="Default.Jsp.Menu.System.Organizer"/></span>", leaf: true, href: 'javascript:toLoadurl(\'organizerMrg/index.shtml\',\'organizerMrg_main\',\'<spring:message code="Default.Jsp.Menu.System.Organizer"/>\');'}
									<%
                                    }else {
                                    %>
									{ iconCls:'icon_yonghuguanli',text: "<span class='menu_text' ><spring:message code="Default.Jsp.Menu.System.User"/></span>", leaf: true, href: 'javascript:toLoadurl(\'usermanager?action=list\',\'userManager_main\',\'<spring:message code="Default.Jsp.Menu.System.User"/>\');'},
									{ iconCls:'icon_ziyuankuguanli',text: "<span class='menu_text' ><spring:message code="Default.Jsp.Menu.System.Repository"/></span>", leaf: true, href: 'javascript:toLoadurl(\'repositorymanager?action=list\',\'repositoryManager_main\',\'<spring:message code="Default.Jsp.Menu.System.Repository"/>\');'},
									{ iconCls:'icon_xukeguanli',text: "<span class='menu_text' ><spring:message code="Default.Jsp.Menu.System.Permit"/></span>", leaf: true, href: 'javascript:toLoadurl(\'resourceauthmanager?action=list\',\'resourceAuth_main\',\'<spring:message code="Default.Jsp.Menu.System.Permit"/>\');'}
									<%
                                    }
                                    %>
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
			Ext.create('Ext.container.Viewport', {
				layout:'border',
				items:[{
					region: 'north',
					border: false,
					contentEl: 'north',
					height: 75,
					margins: '0 0 0 0',
					bbar:[{
						iconCls:'icon_yonghu',
						text:'${sessionScope.userBean.nick_name}'+'<spring:message code="Default.Jsp.Welcome" />',
						margins: '0 0 0 0',
						handler:function(){ }
					},'-',{
						id:'system_time',
						iconCls:'icon_time',
						type:'textfield',
						text:'',
						listeners: {
							'render': function() {
								clockGo();
							}
						}

					},'->',{
						iconCls: 'icon_language',
						text:'<spring:message code="Default.Jsp.Welcome.Language"/> ',    //刷新
						menu:[{
							text:"中文",
							handler:function(){
								selectlang('zh')
							}
						},{
							text:"English",
							handler:function(){
								selectlang('en')
							}
						}]
					},'-',{
						iconCls:'icon_shuaxin',
						text:'<spring:message code="Default.Jsp.Toolbar.Refresh"/>',    //刷新
						handler:function(){
							window.location.reload();
						}
					},'-',{
						iconCls: 'icon_tuichu',
						text:'<spring:message code="Default.Jsp.Menu.LogOut"/> ',  //退出
						handler: function(){
							logout();
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


		function clockGo() {
			Ext.TaskManager.start({
				run: function() {

					var text = '<spring:message code="Default.Jsp.Top.Time.Title"/>'+' '+ Ext.Date.format(new Date(), 'Y年m月d日 A g:i:s');
					Ext.getCmp("system_time").setText(text);
				},
				interval: 1000
			});
		}

		function selectlang(lang){
			window.location.href="index.do?lang="+lang;
		}

		function logout(){
			Ext.Msg.confirm('<spring:message code="Default.Ext.PleaseConfirm"/>', '<spring:message code="Default.Jsp.Confirm.LogOut"/>', function(btn, text) {
				if (btn == 'yes') {
					exit();
				}
			});
		}

		function initWelcome(){
			var id = "mainTab";
			var h = tabs.getHeight() - tabs.tabBar.getHeight()-2;

			var html = '<iframe id="welcome" name="welcome" frameborder="0" width="100%" height="'+h+'" src="designer.jsp"></iframe>';

			var name = '<spring:message code="Default.Jsp.Tab.Welcome"/>';
			addTab(id, name, html,false);

			myMask = new Ext.LoadMask(Ext.getBody(), {
				msg: '<spring:message code="Default.Jsp.Openning"/>',
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
				addTab(id, name, html, true);
				document.getElementById(iframeid).src = url;
			}else {//if have ,make it active
				tabs.setActiveTab(id);
				document.getElementById(iframeid).src = url;
			}
			myMask = new Ext.LoadMask(Ext.getBody(), {
				msg: '<spring:message code="Default.Jsp.Openning"/>',
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

		function exit(){
			document.getElementById('logOutForm').attributes["action"].value = "login";
			//document.getElementById('logOutForm').action = "usermanager";
			document.getElementById('logOutForm').submit();
		}

		function setTimeTD(){
			var now = new Date();
			var now_show = '<spring:message code="Default.Jsp.Top.Time.Title"/>';
			now_show = now_show + now.getFullYear() + '<spring:message code="Default.Jsp.Top.Time.Year"/>';
			now_show = now_show + (now.getMonth()+1) + '<spring:message code="Default.Jsp.Top.Time.Month"/>';
			now_show = now_show + now.getDate() + '<spring:message code="Default.Jsp.Top.Time.Day"/>';
			now_show = now_show + ' ' + now.getHours() + '<spring:message code="Default.Jsp.Top.Time.Hour"/>';
			now_show = now_show + now.getMinutes() + '<spring:message code="Default.Jsp.Top.Time.Minute"/>';
			now_show = now_show + now.getSeconds() + '<spring:message code="Default.Jsp.Top.Time.Second"/>';
			document.getElementById('time_td').innerHTML = now_show;

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
<%
	}
%>

<div id="container">
	<div id="north">

		<table style="width:100%;" cellpadding="2" cellspacing="0" border="0" height="48px" background="images/index_north_bg.jpg">
			<tbody>
			<tr>
				<td rowspan="2" width="20px"></td>
				<td rowspan="2">
					<img src="images/logo.png" rowspan="2" height="32px"><span></span>
				</td>
				<td>
				</td>
				<td rowspan="2"  width="20px"></td>

				</td>
			</tr>

			</tbody>
		</table>


	</div>
	<div id="west"></div>
	<div id="center" class="x-hide-display">
		<iframe id="welcome" name="welcome" frameborder="0" width="100%" height="100%" src="designer.jsp"></iframe>
	</div>

	<div id="south">
		<div style="text-align: center; color: black; background-color: #E0EAFF; width: 100%; padding: 2px; font: 12px;">
			<%
				if("true".equals(Constants.get("USE_COPYRIGHT"))){
			%>
			<spring:message code="Default.Jsp.CopyRight" />
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
