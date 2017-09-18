<%@ page contentType="text/html; charset=utf-8"%>
<%@ include file="/common/include/taglib.jsp"%>
<skyform:html title="汕头移动数据枢纽系统" showLoading="false" exportParams="true"
	isSubPage="false">
<skyform:import src="/admin/js/login.js" />
<skyform:body>
	<div id="hello-win" class="x-hidden">
	<div id="hello-tabs"><img border="0" width="450" height="70"
		src="<%=request.getAttribute("bannerPath") == null ? request.getContextPath()
							+ "/resource/image/login_banner.png" : request.getAttribute("bannerPath")%>" />
	</div>
	</div>
	<div id="aboutDiv" class="x-hidden"
		style='color: black; padding-left: 10px; padding-top: 10px; font-size: 12px'>
	${SYS_TITLE}<br>
	<br>
	<br>
	</div>
	<div id="infoDiv" class="x-hidden"
		style='color: black; padding-left: 10px; padding-top: 10px; font-size: 12px'>
	登录帐户[用户名/密码]...<br>
	[developer/111111][super/111111]<br>
	[admin/111111][test/111111]
	
	
	</div>
</skyform:body>
</skyform:html>