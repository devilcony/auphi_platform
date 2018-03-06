<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="com.auphi.ktrl.util.*" %>
<%@ page import="com.auphi.ktrl.ha.bean.*" %>
<%@ page import="com.auphi.ktrl.i18n.Messages" %>

<%
	PageList pageList = (PageList)request.getAttribute("pageList");
	List<SlaveServerBean> listSlaveServer = (List<SlaveServerBean>)pageList.getList();
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<%@ include file="../../common/extjs.jsp" %>
<script type="text/javascript">
var win;
var old_slave_id = '';
Ext.onReady(function(){
	var tb = Ext.create('Ext.toolbar.Toolbar');
	tb.render('toolbar');
	tb.add({text: '<%=Messages.getString("Default.Jsp.Toolbar.Create") %>',iconCls: 'add',handler: onCreateClick});
	tb.add({text: '<%=Messages.getString("Default.Jsp.Toolbar.Update") %>',iconCls: 'update',handler: onUpdateClick});
	tb.add({text: '<%=Messages.getString("Default.Jsp.Toolbar.Delete") %>',iconCls: 'delete',handler: onDeleteClick});
	tb.add({text: '<%=Messages.getString("Default.Jsp.Toolbar.Refresh") %>',iconCls: 'refresh',handler: onRefreshClick});
	tb.doLayout();
	
	if(!win){
        win =  Ext.create('Ext.window.Window', {
        	contentEl: 'dlg',
        	title:'<%=Messages.getString("Default.Jsp.Toolbar.Create") %>',
        	width:320,
        	height:200,
        	autoHeight:true,
        	buttonAlign:'center',
        	closeAction:'hide',
	        buttons: [
	        	{text:'<%=Messages.getString("Default.Jsp.Dialog.Submit") %>',handler: function(){       			
	        		valiDataForm();	        			
	    	    	}
	        	},
	        	{text:'<%=Messages.getString("Default.Jsp.Dialog.Cancel") %>',handler: function(){win.hide();}}
	        ]
        });
    }
	
	function onCreateClick(){
		old_slave_id = '';
		document.getElementById('dataForm').reset();
		document.getElementById('user_name').value = '<%=Constants.get("HALoginUser")==null?"cluster":Constants.get("HALoginUser") %>';
		document.getElementById('password').value = '<%=Constants.get("HALoginPassword")==null?"cluster":Constants.get("HALoginPassword") %>';
       	win.setTitle('<%=Messages.getString("Default.Jsp.Toolbar.Create") %>');
        win.show();
    }
	
	function onUpdateClick(){
		document.getElementById('dataForm').reset();
		var checks = document.getElementsByName('check');
		var id_slave = '';
		var check_count = 0 ;
		if(checks.length)
		{
			for(var i=0;i<checks.length;i++){
				if(checks[i].checked){
					check_count++;
					if(id_slave == ''){
						id_slave = checks[i].value;
					}else {
						Ext.MessageBox.alert('<%=Messages.getString("Default.Jsp.Warn.Title") %>','<%=Messages.getString("Default.Jsp.Warn.ChooseOne") %>');
						return false;
					}
				}
			}
			if(check_count == 0){
				Ext.MessageBox.alert('<%=Messages.getString("Default.Jsp.Warn.Title") %>','<%=Messages.getString("Default.Jsp.Warn.Choose") %>');
				return false;
			}
		}else {
			Ext.MessageBox.alert('<%=Messages.getString("Default.Jsp.Warn.Title") %>','<%=Messages.getString("Default.Jsp.Warn.Choose") %>');
			return false;
		}
		
		old_slave_id = id_slave;
		
		Ext.Ajax.request(
		{
			url: 'servermanage',
			method: 'POST',
			params: {
		        'action': 'beforeUpdate',
		        'id_slave': id_slave
		    },
			success: function(transport) 
			{
				var data = eval('('+transport.responseText+')');
				document.getElementById('id_slave').value = data.item.id__slave;
		       	document.getElementById('slave_name').value = data.item.name;
		       	document.getElementById('web_app_name').value = data.item.web__app__name;
			    document.getElementById('user_name').value = data.item.username;
			    document.getElementById('password').value = data.item.password;
			    proxyCheckChange(data.item.non__proxy__hosts);
		    	document.getElementById('host_name').value = data.item.host__name;
		       	document.getElementById('port').value = data.item.port;
		    	document.getElementById('proxy_host_name').value = data.item.proxy__host__name;
			    document.getElementById('proxy_port').value = data.item.proxy__port;
			    var master_radio = document.getElementsByName('master_radio');
			    if('0' == data.item.master){
			    	master_radio[0].checked = 'true';
			    }else if('1' == data.item.master){
			    	master_radio[1].checked = 'true';
			    }
			}
		}
		);
		
		win.setTitle('<%=Messages.getString("Default.Jsp.Toolbar.Update") %>');
        win.show();
    }
	
	function onDeleteClick(){
		var checks = document.getElementsByName('check');
		var sel_ids = '';
		var check_count = 0;
		if(checks.length){
			for(var i=0;i<checks.length;i++){
				if(checks[i].checked){
					check_count = check_count + 1;
					if(sel_ids == ''){
						sel_ids = checks[i].value;
					}else {
						sel_ids = sel_ids + ',' + checks[i].value;
					}
				}
			}
			if(check_count == 0){
				Ext.MessageBox.alert('<%=Messages.getString("Default.Jsp.Warn.Title") %>','<%=Messages.getString("Default.Jsp.Warn.Choose") %>');
				return false;
			}
		}else {
			Ext.MessageBox.alert('<%=Messages.getString("Default.Jsp.Warn.Title") %>','<%=Messages.getString("Default.Jsp.Warn.Choose") %>');
			return false;
		}
		
		Ext.MessageBox.confirm('<%=Messages.getString("Default.Jsp.Confirm.Title") %>', '<%=Messages.getString("Default.Jsp.Confirm.Delete") %>', function (btn){
			if(btn=="yes"){
				document.getElementById('sel_ids').value = sel_ids;
				document.getElementById('listForm').action = 'servermanage?action=delete';
				document.getElementById('listForm').submit();
			}
		});
	}
	
	function onRefreshClick(){
		window.location.href = 'servermanage?action=list';
    }
});

function valiDataForm()
{
	var slave_name = document.getElementById('slave_name').value;
	var proxy_hosts_radio = document.getElementById('proxy_hosts_radio');
	var proxy_hosts = '0';
	for(var i=0;i<proxy_hosts_radio.length;i++){
		if(proxy_hosts_radio[i].checked){
			proxy_hosts = proxy_hosts_radio[i].value;
		}
	}
	var host_name = document.getElementById('host_name').value;
	var port = document.getElementById('port').value;
	var proxy_host_name = document.getElementById('proxy_host_name').value;
	var proxy_port = document.getElementById('proxy_port').value;
	var user_name = document.getElementById('user_name').value.replace(/(^\s*)|(\s*$)/g, "");
	var password = document.getElementById('password').value;
	var success = true;
	
	if(slave_name == ''){
		document.getElementById('slave_name_empty').style.display = '';
		success = false;
	}else {
		document.getElementById('slave_name_empty').style.display = 'none';
	}
	
	if(proxy_hosts == '0'){
		if(host_name == ''){
			document.getElementById('host_name_empty').style.display = '';
			document.getElementById('host_name_invalid').style.display = 'none';
			success = false;
		}else if(!valiHost(host_name)){
			document.getElementById('host_name_empty').style.display = 'none';
			document.getElementById('host_name_invalid').style.display = '';
			success = false;
		}else {
			document.getElementById('host_name_empty').style.display = 'none';
			document.getElementById('host_name_invalid').style.display = 'none';
		}
		
		if(port == ''){
			document.getElementById('port_empty').style.display = '';
			document.getElementById('port_invalid').style.display = 'none';
			success = false;
		}else if(!valiInteger(port)){
			document.getElementById('port_empty').style.display = 'none';
			document.getElementById('port_invalid').style.display = '';
			success = false;
		}else {
			document.getElementById('port_empty').style.display = 'none';
			document.getElementById('port_invalid').style.display = 'none';
		}
	}else if(proxy_hosts == '1'){
		if(proxy_host_name == ''){
			document.getElementById('proxy_host_name_empty').style.display = '';
			document.getElementById('proxy_host_name_invalid').style.display = 'none';
			success = false;
		}else if(!valiHost(proxy_host_name)){
			document.getElementById('proxy_host_name_empty').style.display = 'none';
			document.getElementById('proxy_host_name_invalid').style.display = '';
			success = false;
		}else {
			document.getElementById('proxy_host_name_empty').style.display = 'none';
			document.getElementById('proxy_host_name_invalid').style.display = 'none';
		}
		if(proxy_port == ''){
			document.getElementById('proxy_port_empty').style.display = '';
			success = false;
		}else if(!valiInteger(proxy_port)){
			document.getElementById('proxy_port_empty').style.display = 'none';
			document.getElementById('proxy_port_invalid').style.display = '';
			success = false;
		}else {
			document.getElementById('proxy_port_empty').style.display = 'none';
			document.getElementById('proxy_port_invalid').style.display = 'none';
		}
	}
	
	//if(web_app_name == ''){
	//	document.getElementById('web_app_name_empty').style.display = '';
	//	success = false;
	//}else {
	//	document.getElementById('web_app_name_empty').style.display = 'none';
	//}
	
	if(user_name == ''){
		document.getElementById('user_name_empty').style.display = '';
		success = false;
	}else {
		document.getElementById('user_name_empty').style.display = 'none';
	}
		
	if (password == ''){
		document.getElementById('password_empty').style.display='';
		successvar = false ;
	}else {
		document.getElementById('password_empty').style.display='none';
	}

	if(success)
	{
		Ext.Ajax.request({
			url:'servermanage',
			method:'POST',
			params: {
		        'action' : 'checkServerNameExist',
		        'name_slave' : slave_name,
		        'host_name': host_name,
		        'port': port,
		        'old_slave_id': old_slave_id
		    },
		    success: function(transport){
				if('nameExist' == transport.responseText){
					document.getElementById('slave_name_exist').style.display = '';
					document.getElementById('host_name_exist').style.display = 'none';
				}else if('hostAndPortExist' == transport.responseText){
					document.getElementById('slave_name_exist').style.display = 'none';
					document.getElementById('host_name_exist').style.display = '';
					
				}else {
					if(old_slave_id == ''){
						Ext.getDom('dataForm').action = 'servermanage?action=insert';
						Ext.getDom('dataForm').submit();
					}else {
						Ext.getDom('dataForm').action = 'servermanage?action=update';
						Ext.getDom('dataForm').submit();
					}			
				}
			}
		});			
	}	
}

function valiInteger(cyclenum){
	var re = /^[1-9]\d*$/;
    if (!re.test(cyclenum)){
        return false;
    }else {
    	return true;
    }
}

function valiHost(host)
{
	var validHostnameRegex = /^(([a-zA-Z]|[a-zA-Z][a-zA-Z0-9\-]*[a-zA-Z0-9])\.)*([A-Za-z]|[A-Za-z][A-Za-z0-9\-]*[A-Za-z0-9])$/;
	var validIPRegex = /^(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9])\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[0-9])$/;
	if (validHostnameRegex.test(host) || validIPRegex.test(host))
		return true ;
	else
		return false ;
}

function checkAll(){
	var checkall = document.getElementById('checkall');
	var checks = document.getElementsByName('check');
	if(checkall.checked == 'true'){
		for(var i=0;i<checks.length;i++){
			checks[i].checked = 'true';
		}
	}else {
		for(var i=0;i<checks.length;i++){
			checks[i].checked = !checks[i].checked;
		}
	}
}

function proxyCheckChange(sel_value){
	var proxy_hosts_radio = document.getElementsByName('proxy_hosts_radio');
	if('0' == sel_value){
		proxy_hosts_radio[1].checked = true;
		document.getElementById('tr_host').style.display = '';
		document.getElementById('tr_port').style.display = '';
		document.getElementById('tr_proxy_host').style.display = 'none';
		document.getElementById('tr_proxy_port').style.display = 'none';
	}else if('1' == sel_value){
		proxy_hosts_radio[0].checked = true;
		document.getElementById('tr_host').style.display = 'none';
		document.getElementById('tr_port').style.display = 'none';
		document.getElementById('tr_proxy_host').style.display = '';
		document.getElementById('tr_proxy_port').style.display = '';
	}
}

</script>
</head>
<body onload="parent.iframeResize(this.frames.name);" onresize="parent.iframeResize(this.frames.name);">
	<div id="toolbar"></div>
	<form id="listForm" name="listForm" action="" method="post">
	<input type="hidden" id="sel_ids" name="sel_ids" value="">
	<%=pageList.getPageInfo().getHtml("servermanage?action=list") %>
	<br />
	<table width="90%" align="center" id="the-table">
		<tr align="center" bgcolor="#ADD8E6" class="b_tr">
			<td><input type="checkbox" name="checkall" id="checkall" onclick="checkAll();"><%=Messages.getString("Default.Jsp.ChooseAll") %></td>
			<td><%=Messages.getString("SlaveServer.Table.Title.Name") %></td>
			<td><%=Messages.getString("SlaveServer.Table.Title.HostName") %></td>
			<td><%=Messages.getString("SlaveServer.Table.Title.Port") %></td>
			<!-- <td><%=Messages.getString("SlaveServer.Table.Title.WebAppName") %></td>
			<td><%=Messages.getString("SlaveServer.Table.Title.UserName") %></td>
			<td><%=Messages.getString("SlaveServer.Table.Title.NoneProxyHosts") %></td>
			<td><%=Messages.getString("SlaveServer.Table.Title.ProxyHostName") %></td>
			<td><%=Messages.getString("SlaveServer.Table.Title.ProxyPort") %></td>
			<td><%=Messages.getString("SlaveServer.Table.Title.Master") %></td> -->
		</tr>
<%
	for(SlaveServerBean slaveServerBean : listSlaveServer)
	{
%>	
		<tr>
			<td align="center" nowrap="nowrap"><input type="checkbox" name="check" value="<%=slaveServerBean.getId_slave() %>" class="ainput"></td>
			<td nowrap="nowrap"><%=slaveServerBean.getName()==null?"":slaveServerBean.getName()%></td>
			<td nowrap="nowrap"><%=slaveServerBean.getHost_name()==null?"":slaveServerBean.getHost_name() %></td>
			<td nowrap="nowrap"><%=slaveServerBean.getPort()==null?"":slaveServerBean.getPort() %></td>
			<!-- <td nowrap="nowrap"><%=slaveServerBean.getWeb_app_name()==null?"":slaveServerBean.getWeb_app_name() %></td>
			<td nowrap="nowrap"><%=slaveServerBean.getUsername()==null?"":slaveServerBean.getUsername() %></td>
			<td nowrap="nowrap"><%=slaveServerBean.getNon_proxy_hosts()==null?"":slaveServerBean.getNon_proxy_hosts() %></td>
			<td nowrap="nowrap"><%=slaveServerBean.getProxy_host_name()==null?"":slaveServerBean.getProxy_host_name() %></td>
			<td nowrap="nowrap"><%=slaveServerBean.getProxy_port()==null?"":slaveServerBean.getProxy_port() %></td>
			<td nowrap="nowrap"><%=slaveServerBean.getMaster()==null?"":slaveServerBean.getMaster() %></td> -->
		</tr>
<%
	}
%>		
	</table>
	</form>
	<div id="dlg" class="x-hidden">
		<form id="dataForm" name="dataForm" action="" method="post">
			<input type="reset" style="display: none;"><input type="submit" style="display: none;">
			<input type="hidden" name="id_slave" id="id_slave">
			<table border="0" align="center" height="30" width="98%">
				<tr height="30">
					<td width="100"><%=Messages.getString("SlaveServer.Table.Title.Name") %></td>
					<td>
						<input type="text" id="slave_name" name="slave_name" style="width: 195px" value="" maxlength="50">
						<div id="slave_name_empty" style="display:none;" divType="message"><font color="red"><%=Messages.getString("SlaveServer.Table.Warn.SlaveNameEmpty") %></font></div>
						<div id="slave_name_exist" style="display:none;" divType="message"><font color="red"><%=Messages.getString("SlaveServer.Table.Warn.SlaveNameExist") %></font></div>
					</td>
				</tr>
				<tr height="30"  style="display: none;">
					<td width="100"><%=Messages.getString("SlaveServer.Table.Title.NoneProxyHosts") %></td>
					<td >
						<input type="radio" id="proxy_hosts_radio" name="proxy_hosts_radio" value="1" checked="false" onchange="proxyCheckChange(1);"><%=Messages.getString("Default.Jsp.Yes") %>
						<input type="radio" id="proxy_hosts_radio" name="proxy_hosts_radio" value="0" checked="true" onchange="proxyCheckChange(0);"><%=Messages.getString("Default.Jsp.No") %>
					</td>
				</tr>		 
				<tr height="30" id="tr_host">
					<td width="100"><%=Messages.getString("SlaveServer.Table.Title.HostName") %></td>
					<td>
						<input type="text" id="host_name" name="host_name" style="width: 195px" maxlength="50">
						<div id="host_name_empty" style="display:none;" divType="message"><font color="red"><%=Messages.getString("SlaveServer.Table.Warn.HostNameEmpty") %></font></div>
						<div id="host_name_invalid" style="display:none;" divType="message"><font color="red"><%=Messages.getString("SlaveServer.Table.Warn.HostNameInvalid") %></font></div>
						<div id="host_name_exist" style="display:none;" divType="message"><font color="red"><%=Messages.getString("SlaveServer.Table.Warn.HostNameAndPortExist") %></font></div>
					</td>
				</tr>	
				<tr height="30" id="tr_port">
					<td width="100"><%=Messages.getString("SlaveServer.Table.Title.Port") %></td>
					<td>
						<input type="text" id="port" name="port" style="width: 195px" maxlength="50">
						<div id="port_empty" style="display:none;" divType="message"><font color="red"><%=Messages.getString("SlaveServer.Table.Warn.PortEmpty") %></font></div>
						<div id="port_invalid" style="display:none;" divType="message"><font color="red"><%=Messages.getString("SlaveServer.Table.Warn.PortInvalid") %></font></div>
					</td>
				</tr>
				<tr height="30" id="tr_proxy_host" style="display: none;">
					<td width="100"><%=Messages.getString("SlaveServer.Table.Title.ProxyHostName") %></td>
					<td>
						<input type="text" id="proxy_host_name" name="proxy_host_name" style="width: 195px" maxlength="50">
						<div id="proxy_host_name_empty" style="display:none;" divType="message"><font color="red"><%=Messages.getString("SlaveServer.Table.Warn.ProxyHostNameEmpty") %></font></div>
						<div id="proxy_host_name_invalid" style="display:none;" divType="message"><font color="red"><%=Messages.getString("SlaveServer.Table.Warn.ProxyHostNameInvalid") %></font></div>
						<div id="proxy_host_name_exist" style="display:none;" divType="message"><font color="red"><%=Messages.getString("SlaveServer.Table.Warn.ProxyHostNameAndPortExist") %></font></div>
					</td>
				</tr>
				<tr height="30" id="tr_proxy_port" style="display: none;">
					<td width="100"><%=Messages.getString("SlaveServer.Table.Title.ProxyPort") %></td>
					<td>
						<input type="text" id="proxy_port" name="proxy_port" style="width: 195px" maxlength="50">
						<div id="proxy_port_empty" style="display:none;" divType="message"><font color="red"><%=Messages.getString("SlaveServer.Table.Warn.ProxyPortEmpty") %></font></div>
						<div id="proxy_port_invalid" style="display:none;" divType="message"><font color="red"><%=Messages.getString("SlaveServer.Table.Warn.ProxyPortInvalid") %></font></div>
					</td>
				</tr>
				<tr height="30" style="display: none;">
					<td width="100"><%=Messages.getString("SlaveServer.Table.Title.WebAppName") %></td>
					<td>
						<input type="text" id="web_app_name" name="web_app_name" style="width: 195px" maxlength="50">
						<div id="web_app_name_empty" style="display:none;" divType="message"><font color="red"><%=Messages.getString("SlaveServer.Table.Warn.WebAppNameEmpty") %></font></div>
					</td>
				</tr>
				<tr height="30" style="display: none;">
					<td width="100"><%=Messages.getString("SlaveServer.Table.Title.UserName") %></td>
					<td>
						<input type="text" id="user_name" name="user_name" style="width: 195px" maxlength="50">
						<div id="user_name_empty" style="display:none;" divType="message"><font color="red"><%=Messages.getString("SlaveServer.Table.Warn.UserNameEmpty") %></font></div>
					</td>
				</tr>
				<tr height="30" style="display: none;">
					<td width="100"><%=Messages.getString("SlaveServer.Table.Title.Password") %></td>
					<td>
						<input type="password" id="password" name="password" style="width: 195px" maxlength="50">
						<div id="password_empty" style="display:none;" divType="message"><font color="red"><%=Messages.getString("SlaveServer.Table.Warn.PasswordEmpty") %></font></div>
					</td>
				</tr>
				<tr height="30" style="display: none;">
					<td width="100"><%=Messages.getString("SlaveServer.Table.Title.Master") %></td>
					<td>
						<input type="radio" id="master_radio" name="master_radio" value="1" checked="true"><%=Messages.getString("Default.Jsp.Yes") %>
						<input type="radio" id="master_radio" name="master_radio" value="0" checked="false"><%=Messages.getString("Default.Jsp.No") %>
					</td>
				</tr>
			</table>
		</form>
	</div>
	<div id="dlg_file" class="x-hidden">
	</div>
</body>
</html>
