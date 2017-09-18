<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.auphi.ktrl.i18n.Messages" %>
<%@ page import="com.auphi.ktrl.system.user.util.*" %>
<%
	String errMsg = request.getAttribute("errMsg")==null?"":request.getAttribute("errMsg").toString();
	String email = request.getAttribute("email")==null?"":request.getAttribute("email").toString();
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link rel="shortcut icon" href="images/platform.ico" />
<title>注册成功页面</title>
<link href="common/css/home.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="common/jquery-1.7.1.js"></script>
</head>
<body>
<div id="headPasswd">
</div>
<div id="wrapSuc">
	<div class="wsTop"></div>
	<div class="wsCen" style="padding:10px 0;">
		<div class="regSuc">
			<img src="images/duigou.jpg" height="60" width="60" />
			<h4>恭喜!账户注册成功!</h4>
		</div>
        <div class="reg_email01" style="font-size:14px;">您注册的邮箱为：<b><%=email %></b>。</div>
        <div class="reg_email02">我们已经发送了一封激活邮件到您的注册邮箱，现在登陆您的邮箱，并点击激活链接，即可验证邮箱。</div>
        
        <div class="reg_email01">
           	如果您没有收到激活邮件:<br />
           	1、请您检查邮箱的垃圾桶，是否被邮箱误判为垃圾邮件。<br />
           	2、请仔细确认您所填写的邮箱地址是否正确，并稍等几分钟。<br />
           	若还没有收到激活邮件<span id="rsmParent"><a href="#" id="reSendMail" class="rsm btn">重发一份</a></span>。
           	若已经完成激活           <span id="rsmParent"><a href="#" id="backToLogin" class="rsm btn">返回登录</a></span>。
        </div>
	</div>
	<div class="wsBot"></div>
</div>
<script>
$(function(){
	$("#reSendMail").click(function(e){
		e.preventDefault();
		$.ajax({
			url: 'register',
			data: {
				action:'sendActivitionEmail',
				email:'<%=email %>'
			},
			type: 'post',
			dataType: 'text',
			success: function(data,status){
				if(data == 'true'){
					alert('重发邮件成功，请登录您的邮箱查收！');
				}
			}
		});
	});	
});

$(function(){
	$("#backToLogin").click(function(e){
		e.preventDefault();
		window.location.href="<%=request.getContextPath()%>";
	});	
});
</script>
</body>
</html>