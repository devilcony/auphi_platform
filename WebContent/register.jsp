<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.auphi.ktrl.i18n.Messages" %>
<%@ page import="com.auphi.ktrl.util.*" %>
<%@ page import="com.auphi.ktrl.system.organizer.bean.OrganizerBean"%>
<%
	String errMsg = request.getAttribute("errMsg")==null?"":request.getAttribute("errMsg").toString();
	OrganizerBean orgBean = request.getAttribute("organizerBean")==null?new OrganizerBean():(OrganizerBean)request.getAttribute("organizerBean");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<title>注册新账户</title>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<link href="common/css/register.css" rel='stylesheet' type='text/css' />
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<link rel="stylesheet" type="text/css" href="common/ext4/resources/css/ext-all-gray.css">
	<script type="text/javascript" src="common/ext4/bootstrap.js"></script>
	<script type="text/javascript" src="common/ext4/locale/ext-lang-zh_CN.js"></script>
	<link rel="shortcut icon" href="images/platform.ico" />
	<link rel="stylesheet" type="text/css" href="common/css/mystyle.css">
	<link href="common/css/style.css" rel="stylesheet" type="text/css" />
	<script type="text/javascript" src="common/jquery-1.7.1.js"></script>
	<script src="common/cloud.js" type="text/javascript"></script>
	<script language="javascript"> 
	Ext.onReady(function(){
		var errMsg = '<%=errMsg %>';
		if(errMsg != ''){
			Ext.MessageBox.alert('<%=Messages.getString("Metadata.Message.Title.Error") %>',errMsg);
		}
	});
	
	function checkVerifCode() {
		var verifyCode = document.getElementById("verifyCode");
		var submitButton = document.getElementById("submitButton")
		if(verifyCode.value==""){
			verifyCode.setCustomValidity("请输入验证码！");
			document.getElementById("submitButton").click();
			return false;
		}
		Ext.Ajax.request({
			url: 'register',
			method: 'POST',
			params: {
		        action: 'checkVerifyCode',
		        verifyCode: verifyCode.value
		    },
			success: function(transport) {
				var res = transport.responseText;
				if(res == 'true'){
					verifyCode.setCustomValidity("");
					checkName();
				}else {
					verifyCode.setCustomValidity("验证码错误，请重新输入！");
					submitButton.click();
					return false;
				}
			}
		});
	}
	
	function checkName() {
		var orgName = document.getElementById("<%=DBColumns.COLUMN_ORG_NAME %>");
		Ext.Ajax.request({
			url: 'register',
			method: 'POST',
			params: {
		        action: 'checkName',
		        orgName: orgName.value
		    },
			success: function(transport) {
				var res = transport.responseText;
				if(res == 'true'){
					orgName.setCustomValidity("");
					checkEmail();
				}else {
					orgName.setCustomValidity("组织机构已注册，请重新填写！");
					submitButton.click();
					return false;
				}
			}
		});
	}
	
	function checkEmail() {
		var email = document.getElementById("<%=DBColumns.COLUMN_ORG_EMAIL %>");
		Ext.Ajax.request({
			url: 'register',
			method: 'POST',
			params: {
		        action: 'checkEmail',
		        email: email.value
		    },
			success: function(transport) {
				var res = transport.responseText;
				if(res == 'true'){
					email.setCustomValidity("");
					submitButton.click();
				}else {
					email.setCustomValidity("此邮箱已注册，请重新填写！");
					submitButton.click();
					return false;
				}
			}
		});
	}
	
	function register(){
		return checkVerifCode();
	}
	
	function changeVerifyCode(){
		var imgSrc = $("#imgObj");     
		var src = imgSrc.attr("src");    
		var timestamp = (new Date()).valueOf();   
		var url = "register?action=createVerifyCode&timestamp=" + timestamp;       
		imgSrc.attr("src",url); 
	}
	
	</script>
</head>
<body>
	<div class="main">
		<div class="header" >
			<h1>注册新账户</h1>
		</div>
		<p></p>
			<form method="POST" id="registerForm" name="registerForm" action="register">
				<input type="hidden" id="action" name="action" value="register">
				<ul class="left-form">
					<h2>账户信息:</h2>
					<li>
						<input type="text" placeholder="组织机构名称" required id="<%=DBColumns.COLUMN_ORG_NAME %>" name="<%=DBColumns.COLUMN_ORG_NAME %>" value="<%=orgBean.getOrganizer_name() %>"/>
						<div class="clear"> </div>
					</li> 
					<li>
						<input type="text" placeholder="联系人" required id="<%=DBColumns.COLUMN_ORG_CONTACT %>" name="<%=DBColumns.COLUMN_ORG_CONTACT %>" value="<%=orgBean.getOrganizer_contact() %>""/>
						<div class="clear"> </div>
					</li> 
					<li>
						<input type="email" placeholder="邮箱" required id="<%=DBColumns.COLUMN_ORG_EMAIL %>" name="<%=DBColumns.COLUMN_ORG_EMAIL %>" value="<%=orgBean.getOrganizer_email() %>"/>
						<div class="clear"> </div>
					</li> 
					<li>
						<input type="password" placeholder="密码" required pattern="^[0-9a-zA-Z_]{5,16}$" id="<%=DBColumns.COLUMN_ORG_PASSWD %>" name="<%=DBColumns.COLUMN_ORG_PASSWD %>" value="<%=orgBean.getOrganizer_passwd() %>" title="请输入5-16位包含数字、字母或下划线的密码"/>
						<div class="clear"> </div>
					</li> 
					<li>
						<input type="text" placeholder="固定电话" id="<%=DBColumns.COLUMN_ORG_TELPHONE %>" name="<%=DBColumns.COLUMN_ORG_TELPHONE %>" value="<%=orgBean.getOrganizer_telphone() %>"/>
						<div class="clear"> </div>
					</li> 
					<li>
						<input type="text" placeholder="手机" required pattern="^1[345678][0-9]{9}$" id="<%=DBColumns.COLUMN_ORG_MOBILE %>" name="<%=DBColumns.COLUMN_ORG_MOBILE %>" value="<%=orgBean.getOrganizer_mobile() %>" title="请填写正确的手机号码"/>
						<div class="clear"> </div>
					</li> 
					<li>
						<input type="text" placeholder="地址" id="<%=DBColumns.COLUMN_ORG_ADDRESS %>" name="<%=DBColumns.COLUMN_ORG_ADDRESS %>" value="<%=orgBean.getOrganizer_address() %>"/>
						<div class="clear"> </div>
					</li> 
					<li>
						<input id="verifyCode" name="verifyCode" type="text" placeholder="验证码" required/>       
    				    <img id="imgObj"  alt="" src="register?action=createVerifyCode" onclick="changeVerifyCode()"/>       
						<div class="clear"> </div>
					</li>
					<div class="clear"> </div>
					<input type="button" onclick="register();" class="cs_btn gray" value="创建账户">
					<input type="submit" name="submitButton" id="submitButton" style="display:none">
				</ul>
			</form>
			
		</div>
		
		<div class="loginbm">版权所有  2016  <a href="http://www.doetl.com">北京傲飞商智软件有限公司</a> </div></body>
</html>