<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="com.auphi.ktrl.util.*" %>
<%@ page import="com.auphi.ktrl.system.user.util.UMStatus" %>
<%@ page import="com.auphi.ktrl.system.role.bean.*" %>
<%@ page import="com.auphi.ktrl.i18n.Messages" %>

<%
	PageList pageList = (PageList)request.getAttribute("pageList");
	List<RoleBean> listRole = (List<RoleBean>)pageList.getList();
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<%@ include file="../../common/extjs.jsp" %>
<script type="text/javascript">
var win;
var priviledge_tree ;
var priviledge_tree_store ;
var winPriviledge;
var user_tree;
Ext.onReady(function(){
	var tb = Ext.create('Ext.toolbar.Toolbar');
	tb.render('toolbar');
	tb.add({text: '<%=Messages.getString("RoleManager.Toolbar.Create") %>',iconCls: 'add',handler: onCreateClick});
	tb.add({text: '<%=Messages.getString("RoleManager.Toolbar.Update") %>',iconCls: 'update',handler: onUpdateClick});
	tb.add({text: '<%=Messages.getString("RoleManager.Toolbar.Delete") %>',iconCls: 'delete',handler: onDeleteClick});
	tb.add({text: '<%=Messages.getString("RoleManager.Toolbar.Refresh") %>',iconCls: 'refresh',handler: onRefreshClick});
	tb.add({text: '<%=Messages.getString("RoleManager.Toolbar.UserSetting") %>',iconCls: 'refresh',handler: onPriviledgeClick});
	tb.doLayout();
	
	if(!win){
        win =  Ext.create('Ext.window.Window', {
        	contentEl: 'dlg',
        	title:'<%=Messages.getString("RoleManager.Dialog.Create.Title") %>',
        	width:320,
        	height:400,
        	autoHeight:true,
        	buttonAlign:'center',
        	closeAction:'hide',
	        buttons: [
	        	{text:'<%=Messages.getString("RoleManager.Dialog.Button.Submit") %>',handler: function(){
	        			valiDataForm();	        			
	    	    	}
	        	},
	        	{text:'<%=Messages.getString("RoleManager.Dialog.Button.Cancel") %>',handler: function(){win.hide();}}
	        ]
        });
        getPriviledgeTree() ;
    }
   
    if (!winPriviledge){
    	winPriviledge = Ext.create('Ext.window.Window',{
    		contentEl: 'dlgPriviledge',
    		title:'<%=Messages.getString("RoleManager.Dialog.UserSetting")%>',
    		autoHeight:true,
    		buttonAlign:'center',
    		closeAction:'hide',
    		buttons:[
	        	{text:'<%=Messages.getString("UserManager.Dialog.Button.Submit") %>',handler: function(){
	        			assginUsersToRole();	        			
	    	    	}
	        	},
	        	{text:'<%=Messages.getString("UserManager.Dialog.Button.Cancel") %>',handler: function(){winPriviledge.hide();}}    		
    		]
    	});

		var user_tree_store = Ext.create('Ext.data.TreeStore',{
		    proxy: {
		    	type: 'ajax',
		    	reader: {
		    		type: 'json'
		    	},
		    	url: 'usermanager?action=allusers'
		    }		
		});
		
		user_tree = Ext.create('Ext.tree.Panel',{
				renderTo:'user_tree',
				border:false,
				store: user_tree_store,
				expanded: true,
				rootVisible:true,
				autoScroll:true,
				height:240,
				width:150,
				containerScroll: true
		});

		user_tree_store.load();
		user_tree.expand() ;
    }
    
	
	function onCreateClick(){
       	document.getElementById('dataForm').reset();
       	document.getElementById('role_id').value= '';
       	document.getElementById('description').innerHTML = '';
       	document.getElementById('role_name').disabled=false;
		// Uncheck all
		var nodes = priviledge_tree.getChecked() ;
		if (nodes)
		{
			for (var i = 0 ; i < nodes.length ; i ++)
			{
				nodes[i].set('checked',false);
			} 
		}
       	document.getElementById('priviledge_tree').style.display='' ;
		closeMessageDiv() ;
       	win.setTitle('<%=Messages.getString("RoleManager.Dialog.Create.Title") %>');
        win.show();
    }
	function onUpdateClick(){
		document.getElementById('dataForm').reset();
		document.getElementById('priviledge_tree').style.display='' ;
		var checks = document.getElementsByName('check');
		var role_id = '';
		var check_count = 0 ;
		if(checks.length)
		{
			for(var i=0;i<checks.length;i++){
				if(checks[i].checked){
					check_count++;
					if(role_id == ''){
						role_id = checks[i].value;
					}else {
						Ext.MessageBox.alert('<%=Messages.getString("RoleManager.Warnning.Warn") %>','<%=Messages.getString("RoleManager.Warnning.Update.ChooseOne") %>');
						return false;
					}
				}
			}
			if(check_count == 0){
				Ext.MessageBox.alert('<%=Messages.getString("RoleManager.Warnning.Warn") %>','<%=Messages.getString("RoleManager.Warnning.Update.Choose") %>');
				return false;
			}
		}else {
			Ext.MessageBox.alert('<%=Messages.getString("RoleManager.Warnning.Warn") %>','<%=Messages.getString("RoleManager.Warnning.Update.Choose") %>');
			return false;
		}
		document.getElementById('role_id').value = role_id;
		
		Ext.Ajax.request(
		{
			url: 'rolemanager',
			method: 'POST',
			params: {
		        'action': 'beforeUpdate',
		        'role_id': role_id
		    },
			success: function(transport) 
			{
				var data = eval('('+transport.responseText+')');
				document.getElementById('role_id').value = data.item.role__id;
		       	document.getElementById('role_name').value = data.item.role__name;
				document.getElementById('description').value = data.item.description;
				document.getElementById('priviledges').value = data.item.priviledges ;
				document.getElementById('role_name').disabled = true;
				
				// Uncheck all
				var nodes = priviledge_tree.getChecked() ;
				if (nodes)
				{
					for (var i = 0 ; i < nodes.length ; i ++)
					{
						nodes[i].set('checked',false);
					} 
				}
				
				// Check specified priviledges
				var priviledges = eval(data.item.priviledges);
				var n = temp = priviledges ;
				while(n!=0)
				{
					temp = n ;
					if (((n-1)&n) != 0)
						n = n - ((n-1)&n) ;

					var node = priviledge_tree.getStore().getNodeById(n);
					if (node)
					{
						// How to check the node ????
						node.set('checked',true);
					}
					n = (temp-1)&temp ;
				}
			}
		}
		);
		
		closeMessageDiv() ;
		win.setTitle('<%=Messages.getString("UserManager.Dialog.Update.Title") %>');
        win.show();
    }
	
	function onDeleteClick(){
		var checks = document.getElementsByName('check');
		var role_id = '';
		var check_count = 0;
		if(checks.length){
			for(var i=0;i<checks.length;i++){
				if(checks[i].checked){
					check_count = check_count + 1;
					if(role_id == ''){
						role_id = checks[i].value;
					}else {
						role_id = role_id + ',' + checks[i].value;
					}
				}
			}
			if(check_count == 0){
				Ext.MessageBox.alert('<%=Messages.getString("RoleManager.Warnning.Warn") %>','<%=Messages.getString("RoleManager.Warnning.Delete.Choose") %>');
				return false;
			}
		}else {
			Ext.MessageBox.alert('<%=Messages.getString("RoleManager.Warnning.Warn") %>','<%=Messages.getString("RoleManager.Warnning.Delete.Choose") %>');
			return false;
		}
		
		Ext.MessageBox.confirm('<%=Messages.getString("RoleManager.Confirm.Delete.Title") %>', '<%=Messages.getString("RoleManager.Confirm.Delete.Message") %>', function (btn){
			if(btn=="yes"){
				document.getElementById('role_id').value = role_id;
				document.getElementById('listForm').action = 'rolemanager?action=delete';
				document.getElementById('listForm').submit();
			}
		});
	}
	
	function onRefreshClick(){
		window.location.href = 'rolemanager?action=list';
    }
});

function getPriviledgeTree()
{
	priviledge_tree_store = Ext.create('Ext.data.TreeStore',{
	    proxy: {
	    	type: 'ajax',
	    	reader: {
	    		type: 'json'
	    	},
	    	url: 'rolemanager?action=allpriviledges'
	    }		
	});
	
	priviledge_tree = Ext.create('Ext.tree.Panel',{
			renderTo:'priviledge_tree',
			border:false,
			store: priviledge_tree_store,
			expanded: true,
			rootVisible:true,
			autoScroll:true,
			height:240,
			containerScroll: true
	});

	priviledge_tree_store.load();
	priviledge_tree.expand() ;
	return 0 ;
}

function getPriviledges()
{
	var checked = priviledge_tree.getChecked() ;
	
	if ( !checked || checked.length == 0)
		return 0 ;
		
	var priviledges = '' ;
	for (var i = 1 ; i < checked.length ; i ++)
	{
		if (checked[i].get('checked'))
			priviledges = priviledges + ',' + checked[i].get('id');
	}
	priviledges = checked[0].get('id') + priviledges ;

	return priviledges ;
}

function onPriviledgeClick(){
  	var success = true ;
  	var checks = document.getElementsByName('check');
  	var role_id = '';
	var check_count = 0;
	if(checks.length)
	{
		for(var i=0;i<checks.length;i++)
		{
			if(checks[i].checked)
			{
				check_count = check_count + 1;
				if(role_id == '')
				{
					role_id = checks[i].value;
				}
				else 
				{
					Ext.MessageBox.alert('<%=Messages.getString("RoleManager.Warnning.Warn") %>','<%=Messages.getString("RoleManager.Warnning.Update.ChooseOne") %>');
					return false;
				}
			}
		}
		if(check_count == 0)
		{
			Ext.MessageBox.alert('<%=Messages.getString("RoleManager.Warnning.Warn") %>','<%=Messages.getString("RoleManager.Warnning.Update.Choose") %>');
			return false;
		}
	}
	else
	{
		Ext.MessageBox.alert('<%=Messages.getString("RoleManager.Warnning.Warn") %>','<%=Messages.getString("RoleManager.Warnning.Update.Choose") %>');
		return false;
	}
	document.getElementById('role_id').value = role_id ;
	Ext.Ajax.request(
	{
		url: 'rolemanager',
		method: 'POST',
		params: {
	        'action': 'getUsersOfRole',
	        'role_id': role_id
	    },
		success: function(transport) 
		{
			var user_ids = transport.responseText.split(',');
			
			//unchecked all
			var nodes = user_tree.getChecked();
			for (var i = 0 ; i < nodes.length ; i ++)
				nodes[i].set('checked',false);
			
			// get specific roles checked
			for (var i =0 ; i < user_ids.length; i++)
			{
				var node = user_tree.getStore().getNodeById(user_ids[i]);
				node.set('checked',true);
			}
		}
	});
  
  	closeMessageDiv() ;
  	winPriviledge.show() ;
  }

function getUserIds()
{
	var	user_ids = '' ;
	var checked = user_tree.getChecked() ;
	if (!checked || checked.length == 0)
		return '' ;

	for (var i = 1 ; i < checked.length ; i ++)
	{
		user_ids = user_ids + ',' + checked[i].get('id');
	}
	user_ids = checked[0].get('id')+user_ids; 

	return user_ids;
}
function assginUsersToRole()
{
	var user_ids = getUserIds() ;
	var role_id = document.getElementById('role_id').value;
	
	if (user_ids == '')
	{
		document.getElementById('user_empty').style.display='' ;
		return false ;
	}
	
	Ext.Ajax.request({
		url: 'rolemanager',
		method: 'POST',
		params: {
	        'action': 'assignUsersToRole',
	        'user_id': user_ids,
	        'role_id': role_id
	    },
		success: function(transport) 
		{
			winPriviledge.close() ;
		}
	});
}


function valiDataForm()
{
	var role_id = document.getElementById('role_id').value ;
	var role_name = document.getElementById('role_name').value.replace(/(^\s*)|(\s*$)/g, "") ;
	var description = document.getElementById('description').value.replace(/(^\s*)|(\s*$)/g, ""); 
	var priviledges = getPriviledges() ;
	var success = true;
	

	if (priviledges == '')
	{
		document.getElementById('priviledges_empty').style.display='' ;
		success = false ;
	}
	
	if(role_name == '')
	{
		document.getElementById('role_name_empty').style.display = '';
		success = false;
	}
	else
		document.getElementById('role_name_empty').style.display = 'none';
	
	if(!valiRoleName(role_name))
	{
		document.getElementById('role_name_invalid').style.display = '';
		success = false;
	}
	else 
		document.getElementById('role_name_invalid').style.display = 'none';
	
	if(description == ''){
		document.getElementById('description_empty').style.display = '';
		success = false;
	}else {
		document.getElementById('description_empty').style.display = 'none';
	}
	
	if(success)
	{
		// If role_id is not set, send create request
		if (role_id == '')
		{
			Ext.Ajax.request({
				url: 'rolemanager',
				method: 'POST',
				params: {
			        'action' : 'create',
			        'role_name' : role_name,
			        'priviledges' : priviledges,
			        'description': description
			    },
				success: function(transport) {
				    var data = eval('('+transport.responseText+')');
				    if(data.statusCode == '<%=UMStatus.ROLE_NAME_EXIST.getStatusCode()%>'){//user name exists already
				    	document.getElementById('role_name_exist').style.display = '';
				    	document.getElementById('unknown_error').style.display = 'none';
				    	return false;
				    }else if(data.statusCode == '<%=UMStatus.UNKNOWN_ERROR.getStatusCode()%>'){//unknown error
				    	document.getElementById('role_name_exist').style.display = 'none';
				    	document.getElementById('unknown_error').style.display = '';
				    	return false;
				    }else if(data.statusCode == '<%=UMStatus.SUCCESS.getStatusCode()%>'){//create successful
				    	document.getElementById('role_name_exist').style.display = 'none';
				    	document.getElementById('unknown_error').style.display = 'none';
				 		win.close() ;
				 		window.location.href = 'rolemanager?action=list';
				    }
		  		}
			});
		}
		else // If role_id is  set, send upate request
			Ext.Ajax.request({
				url: 'rolemanager',
				method: 'POST',
				params: {
			        'action' : 'update',
			        'role_id': role_id,
			        'role_name' : role_name,
			        'priviledges' : priviledges,
			        'description': description
			    },
				success: function(transport) 
				{
				    var data = eval('('+transport.responseText+')');
				    if(data.statusCode == '<%=UMStatus.SUCCESS.getStatusCode()%>')
				    {//update successful
						document.getElementById('unknown_error').style.display = 'none';
				 		win.close() ;
				 		window.location.href = 'rolemanager?action=list';
				    }
				    else
				    	document.getElementById('unknown_error').style.display = '';
				}
			});		
	}
	else
	{
		document.getElementById('role_name_exist').style.display = 'none';
		document.getElementById('unknown_error').style.display = 'none';	
	}
}

function valiRoleName(role_name){
	var re = /^[0-9a-zA-Z_]{5,}$/;
	if (re.test(role_name))
		return true ;
	else
		return false;
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
    	Ext.MessageBox.alert('<%=Messages.getString("RoleManager.Warnning.Warn") %>','<%=Messages.getString("RoleManager.Dialog.Title.Description.Warn.Long") %>');
    } 
    return true; 
}
</script>
</head>
<body onload="parent.iframeResize(this.frames.name);" onresize="parent.iframeResize(this.frames.name);">
	<div id="toolbar"></div>
	<form id="listForm" name="listForm" action="" method="post">
	<%=pageList.getPageInfo().getHtml("rolemanager?action=list") %>
	<br />
	<input type="hidden" name="role_id" id="role_id">
	<table width="90%" align="center" id="the-table">
		<tr align="center" bgcolor="#ADD8E6" class="b_tr">
			<td><input type="checkbox" name="checkall" id="checkall" onclick="checkAll();"><%=Messages.getString("RoleManager.Table.Column.Choose") %></td>
			<td><%=Messages.getString("RoleManager.Table.Column.ID") %></td>
			<td><%=Messages.getString("RoleManager.Table.Column.Name") %></td>
			<td><%=Messages.getString("RoleManager.Table.Column.Description") %></td>
		</tr>
<%
	for(RoleBean roleBean:listRole)
	{
		String description = roleBean.getDescription()==null?"":roleBean.getDescription();
		String description_b = "";
		
		for(int i=0;i<description.length();i+=20){
			String temp = description.substring(i,i+20>description.length()?description.length():i+20)+"\n";
			description_b += temp;
		}
%>
		<tr>
			<td align="center" nowrap="nowrap"><input type="checkbox" name="check"  <%=roleBean.isSystemRole()?"disabled='disabled'":"" %> value="<%=roleBean.getRole_id() %>" class="ainput"></td>
			<td nowrap="nowrap"><%=roleBean.getRole_id() %></td>
			<td nowrap="nowrap"><%=roleBean.getRole_name() %></td>
			<td nowrap="nowrap"><%=description_b %></td>
		</tr>
<%
	}
%>		
	</table>
	</form>
	<div id="dlg" class="x-hidden">
		<form id="dataForm" name="dataForm" action="" method="post">
			<input type="reset" style="display: none;">
			<input type="submit" style="display: none;">
			<input type="hidden" name="role_id">
			<table border="0" align="center" height="30" width="98%">
				<tr height="30">
					<td width="100"><%=Messages.getString("RoleManager.Dialog.Title.RoleName") %></td>
					<td>
						<input type="text" id="role_name" name="role_name" style="width: 195px" value="" maxlength="50">
						<div id="role_name_empty" style="display:none;" divType="message"><font color="red"><%=Messages.getString("RoleManager.Dialog.Title.RoleName.Warn.Empty") %></font></div>
						<div id="role_name_exist" style="display:none;" divType="message"><font color="red"><%=Messages.getString("RoleManager.Dialog.Title.RoleName.Warn.Exist") %></font></div>
						<div id="role_name_invalid" style="display:none;" divType="message"><font color="red"><%=Messages.getString("RoleManager.Dialog.Title.RoleName.Warn.Invalid") %></font></div>
					</td>
				</tr>
				<tr height="30">
					<td><%=Messages.getString("RoleManager.Dialog.Title.Description") %></td>
					<td>
						<textarea name="description" id="description" style="width: 195px" cols="3" maxlength="120" onkeyup="countlen();"></textarea>
						<div id="description_empty" style="display:none;" divType="message"><font color="red"><%=Messages.getString("RoleManager.Dialog.Title.Description.Warn.Empty") %></font></div>
						<div id="unknown_error" style="display:none;" divType="message"><font color="red"><%=Messages.getString("RoleManager.Dialog.Title.Warn.UnknownError") %></font></div>
					</td>
				</tr>
				<tr>
					<td><%=Messages.getString("RoleManager.Dialog.Title.Priviledges") %></td>
					<td height=100>
						<input type="text" id="priviledges" name="priviledges" style="display:none" value="" maxlength="50">
						<div id="priviledge_tree"/>
						<div id="priviledges_empty" style="display:none;" divType="message"><font color="red"><%=Messages.getString("RoleManager.Dialog.Title.Priviledges.Warn.Empty") %></font></div>
					</td>
				</tr>
			</table>
		</form>
	</div>
	<div id="dlgPriviledge" class="x-hidden">
		<form id="userForm" name="userForm" action="" method="post">
		<input type="hidden" name="role_id">
		<table border="0" align="center" height="30" width="98%">
		<tr>
			<td>
				<div id="user_tree"/>
			</td>
		</tr>
		</table>
		</form>
	</div>
</body>
</html>
