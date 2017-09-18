<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="com.auphi.ktrl.util.*" %>
<%@ page import="com.auphi.ktrl.system.user.util.UMStatus" %>
<%@ page import="com.auphi.ktrl.system.user.bean.*" %>
<%@ page import="com.auphi.ktrl.system.user.util.*" %>
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
var win = null;
var resource_tree = null;
Ext.onReady(function(){
	var tb = Ext.create('Ext.toolbar.Toolbar');
	tb.render('toolbar');
	tb.add({text: '<%=Messages.getString("ResourceAuthManager.Toolbar.Auth") %>',iconCls: 'refresh',handler: onAuthClick});
	tb.doLayout();
	
	if(!win){
        win =  Ext.create('Ext.window.Window', {
        	contentEl: 'dlg',
        	title:'<%=Messages.getString("ResourceAuthManager.Dialog.Repository.Title") %>',
        	width:320,
        	height:450,
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
   
});	

function onAuthClick()
{
	var checks = document.getElementsByName('check');
	var user_id = '';
	var check_count = 0 ;
	if (resource_tree)
		resource_tree.destroy();
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
 	document.getElementById('dataForm').reset();
 	
	// get repository list
	Ext.Ajax.request({
		url:'resourceauthmanager',
		method:'POST',
		params:{
			'action':'getRepList'
		},
		success: function(transport)
		{
			var data = transport.responseText ;
			var reps = data.split(',');
			var rep_list_node = document.getElementById('rep_list') ;
			//var innerContent  = '' ;
			// Remove old options
			rep_list_node.options.length = 0 ;
			
			// Add new options
			for (var i = 0 ; i < reps.length ; i++)
			{
				var oOption = document.createElement("OPTION");
				oOption.text=reps[i];
				oOption.value=reps[i];
				rep_list_node.add(oOption)
			}
			// Construct the first resource tree.
			getResourceTree() ;
		}
	});
	closeMessageDiv() ;
	win.show();

}

function getResourceTree()
{
	var user_id = document.getElementById('user_id').value;
	var rep_name = document.getElementById('rep_list').value ;
	
	if (resource_tree)
	{
		resource_tree.destroy();
		resource_tree = null ;
	}
	
	// get resource tree
	var resource_tree_store = Ext.create('Ext.data.TreeStore',{
	    proxy: {
	    	type: 'ajax',
	    	reader: {
	    		type: 'json'
	    	},
	    	url: 'resourceauthmanager?action=getResourceTree&user_id='+user_id+'&rep_name='+encodeURI(rep_name)
	    }		
	});	
	resource_tree = Ext.create('Ext.tree.Panel',{
				renderTo:'resource_tree',
				border:true,
				store: resource_tree_store,
				expanded: false,
				rootVisible:true,
				autoScroll:true,
				height:320,
				width:240,
				containerScroll: true
		});
	/*
		checkchange : function(node, checked) {
	    node.parentNode.cascadeBy(function(n){n.set('checked', checked);});
	*/
	/*
		oncheckchange = function(node, checked, options){
		    node.cascadeBy(function(n){n.set('checked', checked);} );
		};
		tree.on('checkchange', oncheckchange, null);
		*/
	oncheckchange = function(node,checked,options){
		if (!checked)
			return ;
			/*
		node.parentNode.cascadeBy(
			function(n){
				alert(n.get('text'));
				if (n.get('checked') != null)
					n.set('checked',checked);
				});*/
		while (node.parentNode != null && node.parentNode.get('checked') != null)
		{
			node.parentNode.set('checked',checked);
			node = node.parentNode ;
		}
		
	};
	resource_tree.on('checkchange',oncheckchange,null);

	resource_tree_store.load();
	resource_tree.expand();
}

function getResources()
{
	var resources = '' ;
	var nodes = resource_tree.getChecked() ;
	if ( !nodes || nodes.length == 0)
		return resources ;
	
	for (var i = 1 ; i < nodes.length ; i ++)
	{
		resources += ',' + nodes[i].get('id');
	}
	resources = nodes[0].get('id') + resources ;
	return resources ;
}

function valiDataForm()
{
	var user_id = document.getElementById('user_id').value ;
	var rep_name = document.getElementById('rep_list').value;
	
	// get resource list
	var resources = getResources() ;
	
	Ext.Ajax.request({
		url: 'resourceauthmanager',
		method: 'POST',
		params: {
	        'action' : 'assignResourcesToUser',
	        'user_id': user_id,
	        'rep_name' : rep_name,
	        'resource_ids': resources
	    },
		success: function(transport) 
		{
			resource_tree.destroy();
			closeMessageDiv();
		 	win.close() ;
		}
	});

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

function checkAll(){
	var checkall = document.getElementById('checkall');
	var checks = document.getElementsByName('check');
	if(checkall.checked == 'true'){
		for(var i=0;i<checks.length;i++){
			if(checks[i].disabled){
				checks[i].checked = 'false';
			}else {
				checks[i].checked = 'true';
			}
		}
	}else {
		for(var i=0;i<checks.length;i++){
			if(!checks[i].disabled){
				checks[i].checked = !checks[i].checked;
			}
		}
	}
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
%>	
		<tr>
<%
		if(userBean.isAdmin()){
%>
			<td align="center" nowrap="nowrap"><input type="checkbox" name="check" disabled="disabled" value="<%=userBean.getUser_id() %>" class="ainput"></td>
<%
		}else {
%>		
			<td align="center" nowrap="nowrap"><input type="checkbox" name="check" value="<%=userBean.getUser_id() %>" class="ainput"></td>
<%
		}
%>
			<td nowrap="nowrap"><%=userBean.getNick_name() %></td>
			<td nowrap="nowrap"><%=description_b %></td>
			<td nowrap="nowrap"><%=userBean.getStatus() %></td>
			<td nowrap="nowrap"><%=lastLogin %></td>
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
			<input type="hidden" name="user_id">
			<input type="hidden" name="rep_name">
			<table border="0" align="center" height="30" width="98%">
				<tr height="30">
					<td width="100"><%=Messages.getString("ResourceAuthManager.Dialog.Title.Repository") %></td>
					<td>
						<select id='rep_list' name='rep_list' onchange='getResourceTree();'></select>
					</td>
				</tr>
				<tr height="30">
					<td width="100"><%=Messages.getString("ResourceAuthManager.Dialog.Title.Resources") %></td>
					<td>
						<div id='resource_tree'></div>
					</td>
				</tr>

			</table>
		</form>
	</div>
</body>
</html>
