<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%
	String user_id = session.getAttribute("user_id")==null?"":session.getAttribute("user_id").toString();
	String contextPath = request.getContextPath();
	String redirectPath = contextPath + "/login.do";
%>
<form action="" method="POST" id="sessionForm" name="sessionForm" target="_top">
	<input type="hidden" name="action" id="action" value="logOut">
	<input type="hidden" name="errMsg" id="errMsg" value='<spring:message code="Login.Jsp.Warn.SessionTimeout"/>'/>
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

