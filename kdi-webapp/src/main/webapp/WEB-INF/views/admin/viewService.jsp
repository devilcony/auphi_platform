<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="shortcut icon" href="images/platform.ico" />
<title>对外数据服务接口[${service.serviceName }]使用说明</title>
</head>
<body>

============== ${service.serviceName} 使用说明 ===================
<br>
<p>接口名称：${service.serviceName}</p>

<p>接口服务标识：${service.serviceIdentify}</p>

<p>接口服务URL：${service.serviceUrl}</p>

<p>接口数据返回方式：${service.returnType}</p>

<p>接口数据返回格式：${service.returnDataFormat}</p>

<p>接口参数：{
    "identify": "服务标识",
    "userName": "用户名",
    "password": "密码",
    "systemName": "调用者系统名称",
    "parameter":{"参数名1": "参数值1","参数名2": "参数值2","参数名3": "参数值3"}
}</p>
<p>接口提交方式：POST</p>
<p>接口参数：Content-Type=application/json</p>

</body>
</html>