<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="com.auphi.ktrl.util.*" %>
<%@ page import="com.auphi.ktrl.system.user.util.UMStatus" %>
<%@ page import="com.auphi.ktrl.system.user.bean.*" %>
<%@ page import="com.auphi.ktrl.i18n.Messages" %>

<%
	PageList pageList = (PageList)request.getAttribute("pageList");
	List<UserBean> listUser = (List<UserBean>)pageList.getList();
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<%@ include file="../../common/extjs.jsp" %>
<script type="text/javascript">
var win;
var winPriviledge ;
var role_tree;
Ext.onReady(function(){
	var tb = Ext.create('Ext.toolbar.Toolbar');
	tb.render('toolbar');
	tb.add({text: '<%=Messages.getString("UserManager.Toolbar.Create") %>',iconCls: 'add',handler: onCreateClick});
	tb.add({text: '<%=Messages.getString("UserManager.Toolbar.Update") %>',iconCls: 'update',handler: onUpdateClick});
	tb.add({text: '<%=Messages.getString("UserManager.Toolbar.Delete") %>',iconCls: 'delete',handler: onDeleteClick});
	tb.add({text: '<%=Messages.getString("UserManager.Toolbar.Refresh") %>',iconCls: 'refresh',handler: onRefreshClick});
	tb.add({text: '<%=Messages.getString("UserManager.Toolbar.RoleSetting") %>',iconCls: 'refresh',handler: onPriviledgeClick});
	tb.doLayout();
	
	if(!win){
        win =  Ext.create('Ext.window.Window', {
        	contentEl: 'dlg',
        	title:'<%=Messages.getString("UserManager.Dialog.Create.Title") %>',
        	width:320,
        	height:400,
        	autoHeight:true,
        	buttonAlign:'center',
        	closeAction:'hide',
	        buttons: [
	        	{text:'<%=Messages.getString("UserManager.Dialog.Button.Submit") %>',handler: function(){
	        			valiDataForm();	        			
	    	    	}
	        	},
	        	{text:'<%=Messages.getString("UserManager.Dialog.Button.Cancel") %>',handler: function(){win.hide();}}
	        ]
        });
    }
    
    if (!winPriviledge){
    	winPriviledge = Ext.create('Ext.window.Window',{
    		contentEl: 'dlgPriviledge',
    		title:'<%=Messages.getString("UserManager.Dialog.RoleSetting")%>',
    		autoHeight:true,
    		buttonAlign:'center',
    		closeAction:'hide',
    		buttons:[
	        	{text:'<%=Messages.getString("UserManager.Dialog.Button.Submit") %>',handler: function(){
	        			assginRolesToUser();	        			
	    	    	}
	        	},
	        	{text:'<%=Messages.getString("UserManager.Dialog.Button.Cancel") %>',handler: function(){winPriviledge.hide();}}    		
    		]
    	});

		var role_tree_store = Ext.create('Ext.data.TreeStore',{
		    proxy: {
		    	type: 'ajax',
		    	reader: {
		    		type: 'json'
		    	},
		    	url: 'rolemanager?action=allroles'
		    }		
		});
		
		role_tree = Ext.create('Ext.tree.Panel',{
				renderTo:'role_tree',
				border:false,
				store: role_tree_store,
				expanded: true,
				rootVisible:true,
				autoScroll:true,
				height:240,
				width:150,
				containerScroll: true
		});

		role_tree_store.load();
		role_tree.expand() ;
    }
});	
function onCreateClick(){
    document.getElementById('dataForm').reset();
    document.getElementById('description').innerHTML = '';

    document.getElementById('user_id').value= '';
	document.getElementById('user_name').disabled=false;
    document.getElementById('user_name_empty').style.display = 'none';
    document.getElementById('user_name_exist').style.display = 'none';
   	document.getElementById('password_empty').style.display = 'none';
	document.getElementById('password_confirm_empty').style.display = 'none';
	document.getElementById('password_confirm_mismatch').style.display = 'none';
	document.getElementById('description_empty').style.display = 'none';

	closeMessageDiv() ;
    win.setTitle('<%=Messages.getString("UserManager.Dialog.Create.Title") %>');
    win.show();
}

function onUpdateClick(){
	document.getElementById('dataForm').reset();
	var checks = document.getElementsByName('check');
	var user_id = '';
	var check_count = 0 ;
	if(checks.length)
	{
		for(var i=0;i<checks.length;i++){
			if(checks[i].checked){
				check_count++;
				if(user_id == ''){
					user_id = checks[i].value;
				}else {
					Ext.MessageBox.alert('<%=Messages.getString("UserManager.Warnning.Warn") %>','<%=Messages.getString("UserManager.Warnning.Update.ChooseOne") %>');
					return false;
				}
			}
		}
		if(check_count == 0){
			Ext.MessageBox.alert('<%=Messages.getString("UserManager.Warnning.Warn") %>','<%=Messages.getString("UserManager.Warnning.Update.Choose") %>');
			return false;
		}
	}else {
		Ext.MessageBox.alert('<%=Messages.getString("UserManager.Warnning.Warn") %>','<%=Messages.getString("UserManager.Warnning.Update.Choose") %>');
		return false;
	}
	document.getElementById('user_id').value = user_id;

	
	Ext.Ajax.request(
	{
		url: 'usermanager',
		method: 'POST',
		params: {
	        'action': 'beforeUpdate',
	        'user_id': user_id
	    },
		success: function(transport) 
		{
			var data = eval('('+transport.responseText+')');
			document.getElementById('user_id').value = data.item.user__id;
	       	document.getElementById('user_name').value = data.item.user__name;
	       	document.getElementById('password').value = data.item.password;
	       	document.getElementById('password_confirm').value=data.item.password;
	       	document.getElementById('nick_name').value=data.item.nick__name;
	       	document.getElementById('email').value = data.item.email;
	       	document.getElementById('mobilephone').value = data.item.mobilephone;
			document.getElementById('description').value = data.item.description;
			document.getElementById('user_name').disabled=true;
		}
	}
	);
	
	closeMessageDiv() ;
	win.setTitle('<%=Messages.getString("UserManager.Dialog.Update.Title") %>');
    win.show();
   }

function onDeleteClick(){
	var checks = document.getElementsByName('check');
	var user_id = '';
	var check_count = 0;
	if(checks.length){
		for(var i=0;i<checks.length;i++){
			if(checks[i].checked){
				check_count = check_count + 1;
				if(user_id == ''){
					user_id = checks[i].value;
				}else {
					user_id = user_id + ',' + checks[i].value;
				}
			}
		}
		if(check_count == 0){
			Ext.MessageBox.alert('<%=Messages.getString("UserManager.Warnning.Warn") %>','<%=Messages.getString("UserManager.Warnning.Delete.Choose") %>');
			return false;
		}
	}else {
		Ext.MessageBox.alert('<%=Messages.getString("UserManager.Warnning.Warn") %>','<%=Messages.getString("UserManager.Warnning.Delete.Choose") %>');
		return false;
	}
	
	Ext.MessageBox.confirm('<%=Messages.getString("UserManager.Confirm.Delete.Title") %>', '<%=Messages.getString("UserManager.Confirm.Delete.Message") %>', function (btn){
		if(btn=="yes"){
			document.getElementById('user_id').value = user_id;
			document.getElementById('listForm').action = 'usermanager?action=delete';
			document.getElementById('listForm').submit();
		}
	});
}

function onRefreshClick(){
	window.location.href = 'usermanager?action=list';
}
   
   function onPriviledgeClick(){
   	var success = true ;
   	var checks = document.getElementsByName('check');
   	var user_id = '';
	var check_count = 0;
	if(checks.length){
		for(var i=0;i<checks.length;i++){
			if(checks[i].checked){
				check_count = check_count + 1;
				if(user_id == ''){
					user_id = checks[i].value;
				}else {
					Ext.MessageBox.alert('<%=Messages.getString("UserManager.Warnning.Warn") %>','<%=Messages.getString("UserManager.Warnning.Update.ChooseOne") %>');
					return false;
				}
			}
		}
		if(check_count == 0){
			Ext.MessageBox.alert('<%=Messages.getString("UserManager.Warnning.Warn") %>','<%=Messages.getString("UserManager.Warnning.Update.Choose") %>');
			return false;
		}
	}else {
		Ext.MessageBox.alert('<%=Messages.getString("UserManager.Warnning.Warn") %>','<%=Messages.getString("UserManager.Warnning.Update.Choose") %>');
		return false;
	}
	document.getElementById('user_id').value = user_id ;
	Ext.Ajax.request(
	{
		url: 'usermanager',
		method: 'POST',
		params: {
	        'action': 'getRolesOfUser',
	        'user_id': user_id
	    },
		success: function(transport) 
		{
		    var data = eval('('+transport.responseText+')');
			var role_ids = transport.responseText.split(',');
			
			//unchecked all
			var nodes = role_tree.getChecked();
			for (var i = 0 ; i < nodes.length ; i ++)
				nodes[i].set('checked',false);
			
			// get specific roles checked
			for (var i =0 ; i < role_ids.length; i++)
			{
				var node = role_tree.getStore().getNodeById(role_ids[i]);
				node.set('checked',true);
			}
		}
	}
	);		
   
   	closeMessageDiv() ;
   	winPriviledge.show() ;
   }

function getRoleIds()
{
	var	role_ids = '' ;
	var checked = role_tree.getChecked() ;
	if (!checked || checked.length == 0)
		return '' ;

	for (var i = 1 ; i < checked.length ; i ++)
	{
		role_ids = role_ids + ',' + checked[i].get('id');
	}
	role_ids = checked[0].get('id')+role_ids; 

	return role_ids;
}
function assginRolesToUser()
{
	var role_ids = getRoleIds() ;
	var user_id = document.getElementById('user_id').value;
	
	if (role_ids == '')
	{
		document.getElementById('role_empty').style.display='' ;
		return false ;
	}
	
	Ext.Ajax.request({
		url: 'usermanager',
		method: 'POST',
		params: {
	        'action': 'assignRolesToUser',
	        'user_id': user_id,
	        'role_id': role_ids
	    },
		success: function(transport) 
		{
			winPriviledge.close() ;
		}
	});
}

function valiDataForm()
{
	var user_id = document.getElementById('user_id').value ;
	var user_name = document.getElementById('user_name').value.replace(/(^\s*)|(\s*$)/g, "") ;
	var password = document.getElementById('password').value ;
	var password_confirm = document.getElementById('password_confirm').value ;
	var nick_name = document.getElementById('nick_name').value.replace(/(^\s*)|(\s*$)/g, "") ;
	var email = document.getElementById('email').value.replace(/(^\s*)|(\s*$)/g, "") ;
	var mobilephone = document.getElementById('mobilephone').value ;
	var description = document.getElementById('description').value.replace(/(^\s*)|(\s*$)/g, ""); 
	
	var success = true;
	
	if(user_name == '')
	{
		document.getElementById('user_name_empty').style.display = '';
		success = false;
	}
	else
		document.getElementById('user_name_empty').style.display = 'none';
	
	if(!valiUserName(user_name))
	{
		document.getElementById('user_name_invalid').style.display = '';
		success = false;
	}
	else 
		document.getElementById('user_name_invalid').style.display = 'none';
	
	if (!valiPassword(password))
	{
		document.getElementById('password_invalid').style.display='';
		success = false ;
	}
	else
		document.getElementById('password_invalid').style.display='none';
			
	if (password != password_confirm)
	{	document.getElementById('password_confirm_mismatch').style.display='';
		success = false ;
	}
	else
		document.getElementById('password_confirm_mismatch').style.display='none';
	
	
	if (email == ''){
		document.getElementById('email_empty').style.display = '';
		success = false;
	} else {
		document.getElementById('email_empty').style.display = 'none';
	}
	
	if (!valiEmail(email)){
		document.getElementById('email_invalid').style.display = '' ;
		success = false ;
	} else {
		document.getElementById('email_invalid').style.display = 'none' ;
	}
	
	if(description == ''){
		document.getElementById('description_empty').style.display = '';
		success = false;
	}else {
		document.getElementById('description_empty').style.display = 'none';
	}
	
	if(success)
	{
		// If user_id is not set, send create request
		if (user_id == '')
		{
			Ext.Ajax.request({
				url: 'usermanager',
				method: 'POST',
				params: {
			        'action' : 'create',
			        'user_name' : user_name,
			        'password': password,
			        'nick_name': nick_name,
			        'email': email,
			        'mobilephone': mobilephone,
			        'description': description
			    },
				success: function(transport) {
				    var data = eval('('+transport.responseText+')');
				    if(data.statusCode == '<%=UMStatus.USER_NAME_EXIST.getStatusCode()%>'){//user name exists already
				    	document.getElementById('user_name_exist').style.display = '';
				    	document.getElementById('unknown_error').style.display = 'none';
				    	document.getElementById('nick_name_exist').style.display = 'none';
				    	return false;
				    }else if(data.statusCode == '<%=UMStatus.NICK_NAME_EXIST.getStatusCode()%>'){//user name exists already
				    	document.getElementById('nick_name_exist').style.display = '';
				    	document.getElementById('user_name_exist').style.display = 'none';
				    	document.getElementById('unknown_error').style.display = 'none';
				    	return false;
				    }else if(data.statusCode == '<%=UMStatus.UNKNOWN_ERROR.getStatusCode()%>'){//unknown error
				    	document.getElementById('user_name_exist').style.display = 'none';
				    	document.getElementById('nick_name_exist').style.display = 'none';
				    	document.getElementById('unknown_error').style.display = '';
				    	return false;
				    }else if(data.statusCode == '<%=UMStatus.SUCCESS.getStatusCode()%>'){//create successful
				    	document.getElementById('user_name_exist').style.display = 'none';
				    	document.getElementById('nick_name_exist').style.display = 'none';
				    	document.getElementById('unknown_error').style.display = 'none';
				 		win.close() ;
				 		window.location.href = 'usermanager?action=list';
				    }
		  		}
			});
		}
		else // If user_id is  set, send upate request
			Ext.Ajax.request({
				url: 'usermanager',
				method: 'POST',
				params: {
			        'action' : 'update',
			        'user_id': user_id,
			        'user_name' : user_name,
			        'password': password,
			        'nick_name': nick_name,
			        'email': email,
			        'mobilephone': mobilephone,
			        'description': description
			    },
				success: function(transport) 
				{
				    var data = eval('('+transport.responseText+')');
				    if(data.statusCode == '<%=UMStatus.SUCCESS.getStatusCode()%>')
				    {//update successful
						document.getElementById('unknown_error').style.display = 'none';
						document.getElementById('nick_name_exist').style.display = 'none';
				 		win.close() ;
				 		window.location.href = 'usermanager?action=list';
				    }else if(data.statusCode == '<%=UMStatus.NICK_NAME_EXIST.getStatusCode()%>'){
				    	document.getElementById('nick_name_exist').style.display = '';
				    	document.getElementById('unknown_error').style.display = 'none';
				    	return false;
				    }else{
				    	document.getElementById('unknown_error').style.display = '';
				    	document.getElementById('nick_name_exist').style.display = 'none';
				    	return false;
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

function valiUserName(user_name){
	var re = /^[0-9a-zA-Z_\.@]{5,}$/;
	if (re.test(user_name))
		return true ;
	else
		return false;
}

function valiPassword(password){
	var re = /^[0-9a-zA-Z_]{5,16}$/;
	if (re.test(password))
		return true ;
	else
		return false ;
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

function checkAll(){
	var checkall = document.getElementById('checkall');
	var checks = document.getElementsByName('check');
	if(checkall.checked == 'true'){
		for(var i=0;i<checks.length;i++){
			if (checks[i].disabled == true)
				continue ;
			checks[i].checked = 'true';
		}
	}else {
		for(var i=0;i<checks.length;i++){
			if (checks[i].disabled == true)
				continue ;
			checks[i].checked = !checks[i].checked;
		}
	}
}

function closeMessageDiv()
{
	var divs = document.getElementsByTagName('div') ;
	for (var i=0;i < divs.length ;i ++)
	{
		var divType = divs[i].getAttribute('divType') ;
		if ( divType && divType == 'message')
			divs[i].style.display = 'none'  ;
	}
}

function countlen(){ 
	var description = document.getElementById('description');
    if(description.value.length > 120){ 
    	description.value = description.value.substring(0,120);
    	Ext.MessageBox.alert('<%=Messages.getString("UserManager.Warnning.Warn") %>','<%=Messages.getString("UserManager.Dialog.Title.Description.Warn.Long") %>');
    } 
    return true; 
}
</script>
</head>
<body onload="parent.iframeResize(this.frames.name);" onresize="parent.iframeResize(this.frames.name);">
	<div id="toolbar"></div>
	<div id="dlgPriviledge" class="x-hidden">
		<form id="roleForm" name="roleForm" action="" method="post">
		<input type="hidden" name="user_id">
		<table border="0" align="center" height="30" width="98%">
		<tr>
			<td>
				<div id="role_tree"/>
				<div id="role_empty" style="display:none;" divType="message"><font color="red"><%=Messages.getString("UserManager.Dialog.Title.Role.Warn.Empty") %></font></div>
			</td>
		</tr>
		</table>
		</form>
	</div>	
	<form id="listForm" name="listForm" action="" method="post">
	<%=pageList.getPageInfo().getHtml("usermanager?action=list") %>
	<br />
	<input type="hidden" name="user_id" id="user_id">
	<table width="90%" align="center" id="the-table">
		<tr align="center" bgcolor="#ADD8E6" class="b_tr">
			<td><input type="checkbox" name="checkall" id="checkall" onclick="checkAll();"><%=Messages.getString("UserManager.Table.Column.Choose") %></td>
			<td><%=Messages.getString("UserManager.Table.Column.Name") %></td>
			<td><%=Messages.getString("UserManager.Table.Column.Description") %></td>
			<td><%=Messages.getString("UserManager.Table.Column.Status") %></td>
			<td><%=Messages.getString("UserManager.Table.Column.LastLogin") %></td>
		</tr>
<%
	for(UserBean userBean:listUser)
	{
		String description = userBean.getDescription()==null?"":userBean.getDescription();
		String description_b = "";
		String lastLogin = userBean.getLastLogin() == null?"":userBean.getLastLogin().toString() ;
		for(int i=0;i<description.length();i+=20){
			String temp = description.substring(i,i+20>description.length()?description.length():i+20)+"\n";
			description_b += temp;
		}
		String user_status = "";
		if(userBean.getStatus() == 0){
			user_status = "未激活";
		}else if(userBean.getStatus() == 1){
			user_status = "正常";
		}else if(userBean.getStatus() == 2){
			user_status = "已停用";
		}
		
%>	
		<tr>
			<td align="center" nowrap="nowrap"><input type="checkbox" name="check"   value="<%=userBean.getUser_id() %>" class="ainput"></td>
			<td nowrap="nowrap"><%=userBean.getNick_name() %></td>
			<td nowrap="nowrap"><%=description_b %></td>
			<td nowrap="nowrap"><%=user_status %></td>
			<td nowrap="nowrap"><%=lastLogin %></td>
		</tr>
<%
	}
%>		
	</table>
	</form>
	<div id="dlg" class="x-hidden">
		<form id="dataForm" name="dataForm" action="" method="post">
			<input type="reset" style="display: none;"><input type="submit" style="display: none;">
			<input type="hidden" name="user_id">
			<table border="0" align="center" height="30" width="98%">
				<tr height="30">
					<td width="100"><%=Messages.getString("UserManager.Dialog.Title.UserName") %></td>
					<td>
						<input type="text" id="user_name" name="user_name" style="width: 195px" value="" maxlength="50">
						<div id="user_name_empty" style="display:none;" divType="message"><font color="red"><%=Messages.getString("UserManager.Dialog.Title.UserName.Warn.Empty") %></font></div>
						<div id="user_name_exist" style="display:none;" divType="message"><font color="red"><%=Messages.getString("UserManager.Dialog.Title.UserName.Warn.Exist") %></font></div>
						<div id="user_name_invalid" style="display:none;" divType="message"><font color="red"><%=Messages.getString("UserManager.Dialog.Title.UserName.Warn.Invalid") %></font></div>
					</td>
				</tr>
				<tr height="30">
					<td width="100"><%=Messages.getString("UserManager.Dialog.Title.Password") %></td>
					<td>
						<input type="password" id="password" name="password" style="width: 195px" maxlength="50">
						<div id="password_empty" style="display:none;" divType="message"><font color="red"><%=Messages.getString("UserManager.Dialog.Title.Password.Warn.Empty") %></font></div>
						<div id="password_invalid" style="display:none;" divType="message"><font color="red"><%=Messages.getString("UserManager.Dialog.Title.Password.Warn.Invalid") %></font></div>
					</td>
				</tr>
				<tr height="30">
					<td width="100"><%=Messages.getString("UserManager.Dialog.Title.PasswordConfirm") %></td>
					<td>
						<input type="password" id="password_confirm" name="password_confirm" style="width: 195px" maxlength="50">
						<div id="password_confirm_empty" style="display:none;" divType="message"><font color="red"><%=Messages.getString("UserManager.Dialog.Title.Password.Warn.Empty") %></font></div>
						<div id="password_confirm_mismatch" style="display:none;" divType="message"><font color="red"><%=Messages.getString("UserManager.Dialog.Title.Password.Warn.Mismatch") %></font></div>
					</td>
				</tr>
				<tr height="30">
					<td width="100"><%=Messages.getString("UserManager.Dialog.Title.NickName") %></td>
					<td>
						<input type="text" id="nick_name" name="nick_name" style="width: 195px" maxlength="50">
						<div id="nick_name_exist" style="display:none;" divType="message"><font color="red"><%=Messages.getString("UserManager.Status.NickNameExist") %></font></div>
					</td>
				</tr>
				<tr height="30">
					<td width="100"><%=Messages.getString("UserManager.Dialog.Title.Mobilephone") %></td>
					<td>
						<input type="text" id="mobilephone" name="mobilephone" style="width: 195px" maxlength="50">
					</td>
				</tr>
				<tr height="30">
					<td width="100"><%=Messages.getString("UserManager.Dialog.Title.Email") %></td>
					<td>
						<input type="text" id="email" name="email" style="width: 195px" maxlength="50">
						<div id="email_empty" style="display:none;" divType="message"><font color="red"><%=Messages.getString("UserManager.Dialog.Title.Email.Warn.Empty") %></font></div>
						<div id="email_invalid" style="display:none;" divType="message"><font color="red"><%=Messages.getString("UserManager.Dialog.Title.Email.Warn.Invalid") %></font></div>
					</td>
				</tr>					
				<tr height="30">
					<td><%=Messages.getString("UserManager.Dialog.Title.Description") %></td>
					<td>
						<textarea name="description" id="description" style="width: 195px" cols="3" maxlength="120" onkeyup="countlen();"></textarea>
						<div id="description_empty" style="display:none;" divType="message"><font color="red"><%=Messages.getString("UserManager.Dialog.Title.Description.Warn.Empty") %></font></div>
						<div id="unknown_error" style="display:none;" divType="message"><font color="red"><%=Messages.getString("UserManager.Dialog.Title.Warn.UnknownError") %></font></div>
					</td>
				</tr>			
			</table>
		</form>
	</div>
</body>
</html>
