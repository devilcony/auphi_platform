<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.auphi.ktrl.i18n.Messages" %>
<%@ page import="com.auphi.ktrl.system.user.util.*" %>
<%
	String errMsg = request.getAttribute("errMsg")==null?"":request.getAttribute("errMsg").toString();
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="common/ext4/resources/css/ext-all-gray.css">
<link rel="stylesheet" type="text/css" href="config.css">

<script type="text/javascript" src="common/ext4/bootstrap.js"></script>
<script type="text/javascript" src="common/ext4/locale/ext-lang-zh_CN.js"></script>
<script type="text/javascript">
function toLogin(){
	var username = document.getElementById("user_name").value;
	var password = document.getElementById("password").value;
	if(username == ''){
		Ext.MessageBox.alert('<%=Messages.getString("Metadata.Message.Title.Error") %>','<%=Messages.getString("Login.Jsp.Warn.Empty.Username") %>');
		return false;
	}else {
		if(password == ''){
			Ext.MessageBox.alert('<%=Messages.getString("Metadata.Message.Title.Error") %>','<%=Messages.getString("Login.Jsp.Warn.Empty.Password") %>');
			return false;
		}else {
			Ext.Ajax.request({
				url: 'login',
				method: 'POST',
				params: {
			        action: 'checkLogin',
			        user_name: username,
			        password: password
			    },
				success: function(transport) {
					var res = transport.responseText;
					if(res == '<%=UMStatus.SUCCESS.getStatusMessage() %>'){
						document.forms[0].submit();
					}else {
						Ext.MessageBox.alert('<%=Messages.getString("Metadata.Message.Title.Error") %>',res);
						return false;
					}
				}
			});
		}
	}
}
function toConfig(){
	window.location.href="config.jsp"; 
}
</script>
<title>管理员配置</title>


</head>

<body>
<div>
	<table align="center"  style="border: 0.1mm solid black; background-color: #f7f7f7;vertical-align: middle;margin-top: 100px;">
		<tbody>
			<tr height="26px" >
				<td style="background-color: #D6E7F7" colspan="4" >请输入首次配置的用户名和密码登录系统配置!</td>
			</tr>
			<tr>
				<td align="center" style="padding: 10px" colspan="4" ></td>
			</tr>
			<tr height="35px" align="right">
				<td width="100px">&nbsp;</td>
				<td>用户名：</td>
				<td>
					<input class="input-box" type="text" style="background-color: #EBEBEB;border: 1px solid #7F9DB9;" readonly="true" value="admin" >
				</td>
				<td width="100px">&nbsp;</td>
			</tr>
			<tr height="35px" align="right">
				<td>&nbsp;</td>
				<td>密码：</td>
				<td>
					<input class="input-box" type="password" >
				</td>
				<td>&nbsp;</td>
			</tr>
				<tr height="35px" align="right">
				<td>&nbsp;</td>
				<td>确认密码：</td>
				<td>
					<input class="input-box" type="password" >
				</td>
				<td>&nbsp;</td>
			</tr>
			<tr height="35px" align="right">
				<td>&nbsp;</td>
				<td>&nbsp;</td>
				<td>
					<input class="button-buttonbar-noimage" type="button" onclick="toConfig()" style="width: 80px;" value="确 定" >
				</td>
				<td>&nbsp;</td>
			</tr>
		</tbody>
	</table>
</div>
</body>
</html>
