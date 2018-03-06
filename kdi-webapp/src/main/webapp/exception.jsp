<%@page import="com.auphi.designer.utils.StringEscapeHelper"%>
<%@page import="com.auphi.webapp.utils.ExceptionUtils"%>
<%@page pageEncoding="utf-8" %>
<%
	Exception e = (Exception) request.getAttribute("exception");
	String str = ExceptionUtils.toString(e);
	str = StringEscapeHelper.encode(str);
	response.getWriter().write(str);
	
%>