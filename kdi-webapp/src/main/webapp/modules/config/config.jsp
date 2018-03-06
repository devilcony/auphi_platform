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

<title>配置</title>


</head>

<body>

<table class="_contentTable" width="100%" cellspacing="0" cellpadding="0" style="table-layout: fixed;">
	<tbody class="_tbody">
			<tr height="20">
				<td style="padding: 0px" colspan="4">
					<div class="hd" align="left">管理员配置</div>
				</td>
			</tr>
			<tr>
				<td width="200px" align="left">用户名：</td>
				<td>
					<input type="text" style="width:100%;" readonly="true" value="admin" >
				</td>			
			</tr>
			<tr>
				<td width="200px" align="left">密码：</td>
				<td>
					<input type="text" style="width:100%;" type="password" >
				</td>			
			</tr>			
	</tbody>
</table>
<table class="_contentTable" width="100%" cellspacing="0" cellpadding="0" style="table-layout: fixed;">
	<tbody class="_tbody">
		<tr height="20">
			<td style="padding: 0px" colspan="4">
				<div class="hd" align="left">系统库</div>
			</td>
		</tr>
		<tr>
			<td width="200px" align="left">数据库类型：</td>
			<td>
				<select style="width:100%;">
					<option value="KingbaseES">KingbaseES</option>
					<option value="Oracle">Oracle</option>
					<option value="MySQL">MySQL</option>
					<option value="SqlServer">MS SQL Server</option>
					<option value="DB2">IBM DB2</option>
				</select>
			</td>
		</tr>
		<tr>
			<td width="200px" align="left">服务器地址：</td>
			<td>
				<input class="_dbAddr" type="text" style="width:100%;">
			</td>
			<td>&nbsp;</td>
			<td>&nbsp;</td>
		</tr>

		<tr>
			<td width="200px" align="left">数据库名：</td>
			<td>
				<input class="_dbName" type="text" style="width:100%;" maxlength="122">
			</td>
			<td>&nbsp;</td>
			<td>&nbsp;</td>
		</tr>

		<tr>
			<td width="200px" align="left">用户名：</td>
			<td>
				<input class="_dbUser" type="text" style="width:100%;" maxlength="122">
			</td>
			<td>&nbsp;</td>
			<td>&nbsp;</td>
		</tr>

		<tr>
			<td width="200px" align="left">密码：</td>
			<td>
				<input class="_dbPass" type="password" style="width:100%;" value="Abcd1234" maxlength="122">
			</td>
		</tr>
	</tbody>
</table>

<table class="_contentTable" width="100%" cellspacing="0" cellpadding="0" style="table-layout: fixed;">
	<tbody class="_tbody">
		<tr height="20">
			<td style="padding: 0px" colspan="4">
				<div class="hd" align="left">授权文件 kdiPlatform.lic</div>
			</td>
		</tr>
		<tr>
			<td width="200px" align="left">上传License文件：</td>
			<td>
				<input class="_showUploadFile" type="text" readonly="" style="width:100%;" maxlength="122" name="uploadFile">
			</td>
			<td>
				<form class="_formUpload" target="configIFrame" action="config" method="post" enctype="multipart/form-data" name="formUpload">
					<input id="licenseFile" class="_licenseFile" type="file" title="从本地选择授权文件上传到服务器" hidefocus="" style="position:absolute;filter:alpha(opacity=0);width:10px;cursor:pointer;" name="licenseFile">
					<input class="_inputText" type="hidden" name="licenseFileDir">
					<input class="_uploadSubmit" type="submit" style="display:none" name="uploadSubmit">
					<input class="button-buttonbar-noimage _browseBtn " type="button" style="width:100px;" onmousemove="updateFileBtnPos(licenseFile)" value="选择...">
					<input class="button-buttonbar-noimage _uploadBtn " type="button" style="width:100px;" value="上传">
				</form>
				<span style="display:none">
					<iframe name="configIFrame">
						<html>
							<head></head>
							<body></body>
						</html>
					</iframe>
				</span>
			</td>
			<td>&nbsp;</td>
		</tr>
		
	</tbody>
</table>

<table width="100%" cellspacing="0" cellpadding="0" style="table-layout: fixed;">
	<tbody >
		<tr height="20">
			<td style="padding: 0px" colspan="4">
				<div class="hd" align="left">新建资源库</div>
			</td>
		</tr>
		<tr>
			<td width="200px" align="left">资源库名称：</td>
			<td>
				<input type="text" style="width:100%;">
			</td>
		</tr>		
		<tr>
			<td width="200px" align="left">数据库类型：</td>
			<td>
				<select style="width:100%;">
					<option value="KingbaseES">KingbaseES</option>
					<option value="Oracle">Oracle</option>
					<option value="MySQL">MySQL</option>
					<option value="SqlServer">MS SQL Server</option>
					<option value="DB2">IBM DB2</option>
				</select>
			</td>			
		</tr>
		<tr>
			<td width="200px" align="left">服务器地址：</td>
			<td>
				<input type="text" style="width:100%;">
			</td>
			<td>&nbsp;</td>
			<td>&nbsp;</td>
		</tr>

		<tr>
			<td width="200px" align="left">数据库名：</td>
			<td>
				<input type="text" style="width:100%;" maxlength="122">
			</td>
			<td>&nbsp;</td>
			<td>&nbsp;</td>
		</tr>


		<tr>
			<td width="200px" align="left">用户名：</td>
			<td>
				<input type="text" style="width:100%;" maxlength="122">
			</td>
			<td>&nbsp;</td>
			<td>&nbsp;</td>
		</tr>

		<tr>
			<td width="200px" align="left">密码：</td>
			<td>
				<input type="password" style="width:100%;" value="Abcd1234" maxlength="122">
			</td>
		</tr>
	</tbody>
</table>


</body>
</html>
