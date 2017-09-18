<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.auphi.ktrl.i18n.Messages" %>
<%
	String user_id = session.getAttribute("user_id")==null?"":session.getAttribute("user_id").toString();
	String contextPath = request.getContextPath();
	String redirectPath = contextPath + "/login";
%>
<form action="" method="post" id="sessionForm" name="sessionForm" target="_top">
	<input type="hidden" name="action" id="action" value="logOut">
	<input type="hidden" name="errMsg" id="errMsg" value="<%=Messages.getString("Login.Jsp.Warn.SessionTimeout") %>">
</form>
<script type="text/javascript">
	function submitForm() {
		document.getElementById('sessionForm').attributes["action"].value = "<%=redirectPath %>";
		//document.getElementById('sessionForm').action = "usermanager";
		document.getElementById('sessionForm').submit();
	}
	var user_id = '<%=user_id %>';
	if(user_id == ''){
		submitForm();
	}
</script>

