<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="com.auphi.ktrl.system.mail.util.*" %>
<%@ page import="com.auphi.ktrl.system.mail.bean.*" %>
<%@ page import="com.auphi.ktrl.i18n.Messages" %>

<%
	MailBean mailBean = request.getAttribute("mailBean")==null?new MailBean():(MailBean)request.getAttribute("mailBean");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<%@ include file="../../common/extjs.jsp" %>
<script type="text/javascript">
Ext.onReady(function(){
	var tb = Ext.create('Ext.toolbar.Toolbar');
	tb.render('toolbar');
	tb.add({text: '<%=Messages.getString("MailManager.Toolbar.Save") %>',iconCls: 'desc',handler: onSaveClick});
	tb.doLayout();
});	
function onSaveClick(){
	if(valiDataForm()){
		var smtp_server = document.getElementById('smtp_server').value;
		var smtp_port = document.getElementById('smtp_port').value;
		var user_name = document.getElementById('user_name').value;
		var passwd = document.getElementById('passwd').value;		
		Ext.Ajax.request({
			url: 'mail',
			method: 'POST',
			params: {
		        action: 'validate',
		        smtp_server: smtp_server,
		        smtp_port: smtp_port,
		        user_name: user_name,
		        passwd: passwd
		    },
			success: function(transport) {
			    var res = transport.responseText;
			   	if(res == 'false'){
			   		document.getElementById('validateFalse').style.display = '';
			   	}else if(res == 'true'){
			   		document.getElementById('validateFalse').style.display = 'none';
			   		document.getElementById('dataForm').action = 'mail?action=save';
					document.getElementById('dataForm').submit();
			   	}
		  	}
		});
	}
}

function valiDataForm()
{
	var smtp_server = document.getElementById('smtp_server').value;
	var smtp_port = document.getElementById('smtp_port').value;
	var user_name = document.getElementById('user_name').value;
	var passwd = document.getElementById('passwd').value; 
	
	var success = true;
	
	if(smtp_server == ''){
		document.getElementById('smtp_server_empty').style.display = '';
		success = false;
	}else{
		document.getElementById('smtp_server_empty').style.display = 'none';
		if(!valiServerURL(smtp_server)){
			document.getElementById('smtp_server_invalid').style.display = '';
			success = false;
		}else {
			document.getElementById('smtp_server_invalid').style.display = 'none';
		}
	}
	
	if(smtp_port == ''){
		document.getElementById('smtp_port_empty').style.display = '';
		success = false;
	}else{
		document.getElementById('smtp_port_empty').style.display = 'none';
		if(!valiInteger(smtp_port)){
			document.getElementById('smtp_port_invalid').style.display = '';
			success = false;
		}else {
			document.getElementById('smtp_port_invalid').style.display = 'none';
		}
	}
	
	if(user_name == ''){
		document.getElementById('user_name_empty').style.display = '';
		success = false;
	}else{
		document.getElementById('user_name_empty').style.display = 'none';
		if(!valiEmail(user_name)){
			document.getElementById('user_name_invalid').style.display = '';
			success = false;
		}else {
			document.getElementById('user_name_invalid').style.display = 'none';
		}
	}
	
	if (passwd == ''){
		document.getElementById('passwd_empty').style.display='';
		success = false ;
	}else{
		document.getElementById('passwd_empty').style.display='none';
	}
			
	return success;
}

function valiServerURL(serverURL){
	var re = /^[a-zA-Z0-9\.\_]*$/;
	if (re.test(serverURL))
		return true ;
	else
		return false;
}

function valiInteger(cyclenum){
	var re = /^[1-9]\d*$/;
    if (!re.test(cyclenum)){
        return false;
    }else {
    	return true;
    }
}

function valiEmail(email){
	if(email.length > 32)
		return false;
	var filter = /^[a-zA-Z_0-9\.\-]+@([a-zA-Z_0-9]+\.)+[a-zA-Z]{2,3}$/;
	if (filter.test(email))
		return true ;
	else
		return false ;
}
</script>
</head>
<body onload="parent.iframeResize(this.frames.name);" onresize="parent.iframeResize(this.frames.name);">
	<div id="toolbar"></div>
	<form id="dataForm" name="dataForm" action="" method="post">
	<br />
	<center><div id="validateFalse" style="display:none;" divType="message"><font color="red"><%=Messages.getString("MailManager.Dialog.ValidateFail") %></font></div></center>
	<table width="50%" align="center" id="the-table">
		<tr align="center">
			<td bgcolor="#ADD8E6" class="b_tr"><%=Messages.getString("MailManager.Table.Column.SmtpServer") %></td>
			<td>
				<input type="text" id="smtp_server" name="smtp_server" style="width: 195px" value="<%=mailBean.getSmtp_server()==null?"":mailBean.getSmtp_server() %>" maxlength="50">
				<div id="smtp_server_empty" style="display:none;" divType="message"><font color="red"><%=Messages.getString("MailManager.Dialog.Title.SmtpServer.Warn.Empty") %></font></div>
				<div id="smtp_server_invalid" style="display:none;" divType="message"><font color="red"><%=Messages.getString("MailManager.Dialog.Title.SmtpServer.Warn.Invalid") %></font></div>
			</td>
		</tr>
		<tr align="center">
			<td bgcolor="#ADD8E6" class="b_tr"><%=Messages.getString("MailManager.Table.Column.SmtpPort") %></td>
			<td>
				<input type="text" id="smtp_port" name="smtp_port" style="width: 195px" value="<%=mailBean.getSmtp_port()==0?25:mailBean.getSmtp_port() %>" maxlength="50">
				<div id="smtp_port_empty" style="display:none;" divType="message"><font color="red"><%=Messages.getString("MailManager.Dialog.Title.SmtpPort.Warn.Empty") %></font></div>
				<div id="smtp_port_invalid" style="display:none;" divType="message"><font color="red"><%=Messages.getString("MailManager.Dialog.Title.SmtpPort.Warn.Invalid") %></font></div>
			</td>
		</tr>
		<tr align="center">
			<td bgcolor="#ADD8E6" class="b_tr"><%=Messages.getString("MailManager.Table.Column.UserName") %></td>
			<td>
				<input type="text" id="user_name" name="user_name" style="width: 195px" value="<%=mailBean.getUser_name()==null?"":mailBean.getUser_name() %>" maxlength="50">
				<div id="user_name_empty" style="display:none;" divType="message"><font color="red"><%=Messages.getString("MailManager.Dialog.Title.UserName.Warn.Empty") %></font></div>
				<div id="user_name_invalid" style="display:none;" divType="message"><font color="red"><%=Messages.getString("MailManager.Dialog.Title.UserName.Warn.Invalid") %></font></div>
			</td>
		</tr>
		<tr align="center">
			<td bgcolor="#ADD8E6" class="b_tr"><%=Messages.getString("MailManager.Table.Column.Passwd") %></td>
			<td>
				<input type="password" id="passwd" name="passwd" style="width: 195px" value="<%=mailBean.getPasswd()==null?"":mailBean.getPasswd() %>" maxlength="50">
				<div id="passwd_empty" style="display:none;" divType="message"><font color="red"><%=Messages.getString("MailManager.Dialog.Title.Passwd.Warn.Empty") %></font></div>
			</td>
		</tr>
	</table>
	</form>
</body>
</html>
