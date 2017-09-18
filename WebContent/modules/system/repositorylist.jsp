<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="com.auphi.ktrl.util.*" %>
<%@ page import="com.auphi.ktrl.system.repository.bean.*" %>
<%@ page import="com.auphi.ktrl.i18n.Messages" %>

<%
	PageList pageList = (PageList)request.getAttribute("pageList");
	String[] databaseTypes = (String[]) request.getAttribute("databaseTypes");
	List<RepositoryBean> listRep = (List<RepositoryBean>)pageList.getList();
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<%@ include file="../../common/extjs.jsp" %>
<script type="text/javascript">
var win;
var oldName = '';
Ext.onReady(function(){
	var tb = Ext.create('Ext.toolbar.Toolbar');
	tb.render('toolbar');
	tb.add({text: '<%=Messages.getString("RepositoryManager.Toolbar.Create") %>',iconCls: 'add',handler: onCreateClick});
	tb.add({text: '<%=Messages.getString("RepositoryManager.Toolbar.Update") %>',iconCls: 'update',handler: onUpdateClick});
	tb.add({text: '<%=Messages.getString("RepositoryManager.Toolbar.Delete") %>',iconCls: 'delete',handler: onDeleteClick});
	tb.add({text: '<%=Messages.getString("RepositoryManager.Toolbar.Refresh") %>',iconCls: 'refresh',handler: onRefreshClick});
	tb.doLayout();
	
	if(!win){
        win =  Ext.create('Ext.window.Window', {
        	contentEl: 'dlg',
        	title:'<%=Messages.getString("RepositoryManager.Dialog.Create.Title") %>',
        	width:320,
        	height:400,
        	autoHeight:true,
        	buttonAlign:'center',
        	closeAction:'hide',
	        buttons: [
	        	{text:'<%=Messages.getString("RepositoryManager.Dialog.Button.Submit") %>',handler: function(){       			
	        		valiDataForm();	        			
	    	    	}
	        	},
	        	{text:'<%=Messages.getString("RepositoryManager.Dialog.Button.Cancel") %>',handler: function(){win.hide();}}
	        ]
        });
    }
	
	function onCreateClick(){
		oldName = '';
       	document.getElementById('dataForm').reset();
       	document.getElementById('repository_id').value= '';
       	document.getElementById('repository_name').disabled=false;
       	document.getElementById('db_port').value="1521";
		closeMessageDiv() ;
       	win.setTitle('<%=Messages.getString("RepositoryManager.Dialog.Create.Title") %>');
        win.show();
    }
	
	function onUpdateClick(){
		document.getElementById('dataForm').reset();
		var checks = document.getElementsByName('check');
		var repository_id = '';
		var check_count = 0 ;
		if(checks.length)
		{
			for(var i=0;i<checks.length;i++){
				if(checks[i].checked){
					check_count++;
					if(repository_id == ''){
						repository_id = checks[i].value;
					}else {
						Ext.MessageBox.alert('<%=Messages.getString("RepositoryManager.Warnning.Warn") %>','<%=Messages.getString("RepositoryManager.Warnning.Update.ChooseOne") %>');
						return false;
					}
				}
			}
			if(check_count == 0){
				Ext.MessageBox.alert('<%=Messages.getString("RepositoryManager.Warnning.Warn") %>','<%=Messages.getString("RepositoryManager.Warnning.Update.Choose") %>');
				return false;
			}
		}else {
			Ext.MessageBox.alert('<%=Messages.getString("RepositoryManager.Warnning.Warn") %>','<%=Messages.getString("RepositoryManager.Warnning.Update.Choose") %>');
			return false;
		}
		document.getElementById('repository_id').value = repository_id;

		
		Ext.Ajax.request(
		{
			url: 'repositorymanager',
			method: 'POST',
			params: {
		        'action': 'beforeUpdate',
		        'repository_id': repository_id
		    },
			success: function(transport) 
			{
				var data = eval('('+transport.responseText+')');
				document.getElementById('repository_id').value = data.item.repositoryID;
		       	document.getElementById('repository_name').value = data.item.repositoryName;
		       	document.getElementById('user_name').value = data.item.userName;
		       	document.getElementById('password').value = data.item.password;
//		       	document.getElementById('repository_version').value = data.item.version;
		       	document.getElementById('repository_name').disabled=true;
			    document.getElementById('db_type').value = data.item.dbType;
			    document.getElementById('db_host').value = data.item.dbHost;
			    document.getElementById('db_port').value = data.item.dbPort;
			    document.getElementById('db_name').value = data.item.dbName;	       	
			}
		}
		);
		
		closeMessageDiv() ;
		win.setTitle('<%=Messages.getString("RepositoryManger.Dialog.Update.Title") %>');
        win.show();
    }
	
	function onDeleteClick(){
		var checks = document.getElementsByName('check');
		var repository_id = '';
		var check_count = 0;
		if(checks.length){
			for(var i=0;i<checks.length;i++){
				if(checks[i].checked){
					check_count = check_count + 1;
					if(repository_id == ''){
						repository_id = checks[i].value;
					}else {
						repository_id = repository_id + ',' + checks[i].value;
					}
				}
			}
			if(check_count == 0){
				Ext.MessageBox.alert('<%=Messages.getString("RepositoryManager.Warnning.Warn") %>','<%=Messages.getString("RepositoryManager.Warnning.Delete.Choose") %>');
				return false;
			}
		}else {
			Ext.MessageBox.alert('<%=Messages.getString("RepositoryManager.Warnning.Warn") %>','<%=Messages.getString("RepositoryManager.Warnning.Delete.Choose") %>');
			return false;
		}
		
		Ext.MessageBox.confirm('<%=Messages.getString("RepositoryManager.Confirm.Delete.Title") %>', '<%=Messages.getString("RepositoryManager.Confirm.Delete.Message") %>', function (btn){
			if(btn=="yes"){
				document.getElementById('repository_id').value = repository_id;
				document.getElementById('listForm').action = 'repositorymanager?action=delete';
				document.getElementById('listForm').submit();
			}
		});
	}
	
	function onRefreshClick(){
		window.location.href = 'repositorymanager?action=list';
    }
});

function valiDataForm()
{
	var repository_id = document.getElementById('repository_id').value ;
	var user_name = document.getElementById('user_name').value.replace(/(^\s*)|(\s*$)/g, "") ;
	var password = document.getElementById('password').value ;
	var repository_name = document.getElementById('repository_name').value ;
	var db_type = document.getElementById('db_type').value ;
	var db_host = document.getElementById('db_host').value ;
	var db_port = document.getElementById('db_port').value ;
	var db_name = document.getElementById('db_name').value ;
	var successvar = true;		
	closeMessageDiv();
	
	if(repository_name == '')
	{
		document.getElementById('repository_name_empty').style.display = '';
		successvar = false;
	}
	if(document.getElementById('repository_name_exist').style.display != 'none')
		successvar=false;
	if(user_name == '')
	{
		document.getElementById('user_name_empty').style.display = '';
		successvar = false;
	}
		
	if (db_type == '')
	{
		document.getElementById('db_type_empty').style.display='';
		successvar = false ;
	}

	if (db_host == '')
	{
		document.getElementById('db_host_empty').style.display='';
		successvar = false ;
	}
	if (!valiIP(db_host) && !valiHost(db_host))
	{
		document.getElementById('db_host_invalid').style.display='';
		successvar = false ;
	}

	if (db_port == '')
	{
		document.getElementById('db_port_empty').style.display='';
		successvar = false ;
	}
	
	if (db_name == '')
	{
		document.getElementById('db_name_empty').style.display='';
		successvar = false ;
	}	
	if(successvar)
	{
		if(repository_id!=''){
			afterCheckDataForm();
		}
		else{
			Ext.Ajax.request({
				url:'repositorymanager',
				method:'POST',
				params: {
			        'action' : 'checkRepositoryNameExist',
			        'repository_name' : repository_name,
			        'password': password,
			        'user_name': user_name,
			        'db_type':db_type,
			        'db_host':db_host,
			        'db_port':db_port,
			        'db_name':db_name			        
			    },
			    success: function(transport){
					if('false'==transport.responseText){						
						afterCheckDataForm();
					}else{
						document.getElementById('repository_name_exist').style.display = '';
					}
			    }
			});			
		}
	}	
}
function afterCheckDataForm(){
	win.disable(true);
	var repository_id = document.getElementById('repository_id').value ;
	var user_name = document.getElementById('user_name').value.replace(/(^\s*)|(\s*$)/g, "") ;
	var password = document.getElementById('password').value ;
	var repository_name = document.getElementById('repository_name').value ;
	var db_type = document.getElementById('db_type').value ;
	var db_host = document.getElementById('db_host').value ;
	var db_port = document.getElementById('db_port').value ;
	var db_name = document.getElementById('db_name').value ;	
	Ext.Ajax.request({
		url:'repositorymanager',
		method:'POST',
		params: {
	        'action' : 'checkTableExist',
	        'repository_name' : repository_name,
	        'password': password,
	        'user_name': user_name,
	        'db_type':db_type,
	        'db_host':db_host,
	        'db_port':db_port,
	        'db_name':db_name			        
	    },
	    success: function(transport){
	    	var message;
			if('true'==transport.responseText){
				if(repository_id == ''){
					message ='<%=Messages.getString("RepositoryManager.CreateRepository.newButExist") %>';
					createRepository('true',message);
				}else{
					message = '<%=Messages.getString("RepositoryManager.CreateRepository.oldButExist") %>';
					createRepository('true',message);							
				}
					
			}else if('false'==transport.responseText){
				if(repository_id == ''){
					message ='<%=Messages.getString("RepositoryManager.CreateRepository.newButNotExist") %>';
					createRepository('false',message);
				}else{
					message ='<%=Messages.getString("RepositoryManager.CreateRepository.oldButNotExist") %>';
					createRepository('false',message);							
				}						
			}else {
				win.enable(true);
				Ext.MessageBox.alert('<%=Messages.getString("Default.Jsp.Error.Title") %>','<%=Messages.getString("RepositoryManager.Error.Config") %>');
			}					
	    }			    
	});		
}
function createRepository(exist,message){
	win.enable(true);
	var repository_id = document.getElementById('repository_id').value ;
	var user_name = document.getElementById('user_name').value.replace(/(^\s*)|(\s*$)/g, "") ;
	var password = document.getElementById('password').value ;
	var repository_name = document.getElementById('repository_name').value ; 
	var db_type = document.getElementById('db_type').value ;
	var db_host = document.getElementById('db_host').value ;
	var db_port = document.getElementById('db_port').value ;
	var db_name = document.getElementById('db_name').value ;
	if("true"== exist){
		Ext.MessageBox.confirm({
			     title:'<%=Messages.getString("RepositoryManager.Confirm.Create.Title") %>',
			     msg:message,
				 buttons: Ext.MessageBox.YESNOCANCEL,
				fn:function (btn){
			if(btn=="yes"){
	            Ext.MessageBox.show({
	                msg: '<%=Messages.getString("RepositoryManager.progress.Title") %>',
	                progressText: '<%=Messages.getString("RepositoryManager.progress.Text") %>',
	                width:300,
	                wait:true,
	                waitConfig: {interval:200},
	                icon:'ext-mb-download' //custom class in msg-box.html
	            });	 		
	            setTimeout(function(){Ext.MessageBox.hide();alert('<%=Messages.getString("RepositoryManager.timeout") %>')},30000);
				Ext.Ajax.request({
					url:'repositorymanager',
					method:'POST',
					params: {
				        'action' : 'createRepository',
				        'repository_id':repository_id,
				        'repository_name' : repository_name,
				        'password': password,
				        'user_name': user_name,
				        'db_type':db_type,
				        'db_host':db_host,
				        'db_port':db_port,
				        'db_name':db_name,
				        'update':'true'
				    },	
				    success: function(transport){
				    		window.location.href ="repositorymanager?action=list";				    		
				    }
				});							
			}else if(btn=="no"){
	            Ext.MessageBox.show({
	                msg: '<%=Messages.getString("RepositoryManager.progress.Title") %>',
	                progressText: '<%=Messages.getString("RepositoryManager.progress.Text") %>',
	                width:300,
	                wait:true,
	                waitConfig: {interval:200},
	                icon:'ext-mb-download' //custom class in msg-box.html
	            });	 		
	            setTimeout(function(){Ext.MessageBox.hide();alert('<%=Messages.getString("RepositoryManager.timeout") %>')},30000);				
				Ext.Ajax.request({
					url:'repositorymanager',
					method:'POST',
					params: {
				        'action' : 'addRepository',
				        'repository_id':repository_id,
				        'repository_name' : repository_name,
				        'password': password,
				        'user_name': user_name,
				        'db_type':db_type,
				        'db_host':db_host,
				        'db_port':db_port,
				        'db_name':db_name
				    },	
				    success: function(transport){
				    		window.location.href ="repositorymanager?action=list";				    				
				    }
				})				
			}
		}});			    		
	}else{
		Ext.MessageBox.confirm('<%=Messages.getString("RepositoryManager.Confirm.Create.Title") %>', message,
				function (btn){
			if(btn=="yes"){
	            Ext.MessageBox.show({
	                msg: '<%=Messages.getString("RepositoryManager.progress.Title") %>',
	                progressText: '<%=Messages.getString("RepositoryManager.progress.Text") %>',
	                width:300,
	                wait:true,
	                waitConfig: {interval:200},
	                icon:'ext-mb-download' //custom class in msg-box.html
	            });	 		
	            setTimeout(function(){Ext.MessageBox.hide();alert('<%=Messages.getString("RepositoryManager.timeout") %>')},30000);				
				Ext.Ajax.request({
					url:'repositorymanager',
					method:'POST',
					params: {
				        'action' : 'createRepository',
				        'repository_id':repository_id,
				        'repository_name' : repository_name,
				        'password': password,
				        'user_name': user_name,
				        'db_type':db_type,
				        'db_host':db_host,
				        'db_port':db_port,
				        'db_name':db_name,
				        'update':'false'
				    },	
				    success: function(transport){
				    		window.location.href ="repositorymanager?action=list";				    		
				    }
				});							
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
	var re = /^[0-9a-zA-Z_]{5,}$/;
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

function valiIP(ip)
{
	var validIpAddressRegex = /^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$/;
	
	if (validIpAddressRegex.test(ip))
		return true;
	else
		return false ;
}

function valiHost(host)
{
	var validHostnameRegex = /^(([a-zA-Z]|[a-zA-Z][a-zA-Z0-9\-]*[a-zA-Z0-9])\.)*([A-Za-z]|[A-Za-z][A-Za-z0-9\-]*[A-Za-z0-9])$/;
	if (validHostnameRegex.test(host))
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
function selectChange(){
	var databaseType = document.getElementById('db_type');
	var selected = databaseType.options[databaseType.selectedIndex].value;
	if('Oracle'==selected){
		document.getElementById('db_port').value="1521";
	}else if('MSSQL'==selected){
		document.getElementById('db_port').value="1433";
	}else if('MySQL'==selected){
		document.getElementById('db_port').value="3306";
	}else if('DB2'==selected){
		document.getElementById('db_port').value="50000";
	}else if('KingbaseES'==selected){
		document.getElementById('db_port').value="54321";
	}else if('PostgreSQL'==selected){
		document.getElementById('db_port').value="5432";
	}
		
}

</script>
</head>
<body onload="parent.iframeResize(this.frames.name);" onresize="parent.iframeResize(this.frames.name);">
	<div id="toolbar"></div>
	<form id="listForm" name="listForm" action="" method="post">
	<%=pageList.getPageInfo().getHtml("repositorymanager?action=list") %>
	<br />
	<input type="hidden" name="repository_id" id="repository_id">
	<table width="90%" align="center" id="the-table">
		<tr align="center" bgcolor="#ADD8E6" class="b_tr">
			<td><input type="checkbox" name="checkall" id="checkall" onclick="checkAll();"><%=Messages.getString("RepositoryManager.Table.Column.Choose") %></td>
			<td><%=Messages.getString("RepositoryManager.Table.Column.RepositoryName") %></td>
			<td><%=Messages.getString("RepositoryManager.Table.Column.DBType") %></td>
			<td><%=Messages.getString("RepositoryManager.Table.Column.DBHost") %></td>
			<td><%=Messages.getString("RepositoryManager.Table.Column.DBPort") %></td>
			<td><%=Messages.getString("RepositoryManager.Table.Column.DBName") %></td>
		</tr>
<%
	for(RepositoryBean repBean:listRep)
	{
%>	
		<tr>
			<td align="center" nowrap="nowrap"><input type="checkbox" name="check" value="<%=repBean.getRepositoryID() %>" class="ainput"></td>
			<td nowrap="nowrap"><%=repBean.getRepositoryName()%></td>
			<td nowrap="nowrap"><%=repBean.getDbType() %></td>
			<td nowrap="nowrap"><%=repBean.getDbHost() %></td>
			<td nowrap="nowrap"><%=repBean.getDbPort()%></td>
			<td nowrap="nowrap"><%=repBean.getDbName()%></td>
		</tr>
<%
	}
%>		
	</table>
	</form>
	<div id="dlg" class="x-hidden">
		<form id="dataForm" name="dataForm" action="" method="post">
			<input type="reset" style="display: none;"><input type="submit" style="display: none;">
			<input type="hidden" name="repository_id">
			<table border="0" align="center" height="30" width="98%">
				<tr height="30">
					<td width="100"><%=Messages.getString("RepositoryManager.Dialog.Title.RepositoryName") %></td>
					<td>
						<input type="text" id="repository_name" name="repository_name" style="width: 195px" value="" maxlength="50">
						<div id="repository_name_empty" style="display:none;" divType="message"><font color="red"><%=Messages.getString("RepositoryManager.Dialog.Title.RepositoryName.Warn.Empty") %></font></div>
						<div id="repository_name_exist" style="display:none;" divType="message"><font color="red"><%=Messages.getString("RepositoryManager.Dialog.Title.RepositoryName.Warn.Exist") %></font></div>
						<div id="repository_name_invalid" style="display:none;" divType="message"><font color="red"><%=Messages.getString("RepositoryManager.Dialog.Title.RepositoryName.Warn.Invalid") %></font></div>
					</td>
				</tr>
				<tr height="30">
					<td width="100"><%=Messages.getString("RepositoryManager.Dialog.Title.DBType") %></td>
					<td >
						<select id="db_type" name="db_type" style="width: 195px;" onchange="selectChange();">
						<%
							for(String databaseType : databaseTypes){
						%>
							<option value='<%=databaseType %>'><%=databaseType %></option>
						<%} %>
						</select>
						<div id="db_type_empty" style="display:none;" divType="message"><font color="red"><%=Messages.getString("RepositoryManager.Dialog.Title.DBType.Warn.Empty") %></font></div>
					</td>
				</tr>		 
				<tr height="30">
					<td width="100"><%=Messages.getString("RepositoryManager.Dialog.Title.DBHost") %></td>
					<td>
						<input type="text" id="db_host" name="db_host" style="width: 195px" maxlength="50">
						<div id="db_host_empty" style="display:none;" divType="message"><font color="red"><%=Messages.getString("RepositoryManager.Dialog.Title.RepositoryDBHost.Warn.Empty") %></font></div>
						<div id="db_host_invalid" style="display:none;" divType="message"><font color="red"><%=Messages.getString("RepositoryManager.Dialog.Title.RepositoryDBHost.Warn.Invalid") %></font></div>
					</td>
				</tr>	
				<tr height="30">
					<td width="100"><%=Messages.getString("RepositoryManager.Dialog.Title.DBPort") %></td>
					<td>
						<input type="text" id="db_port" name="db_port" style="width: 195px" maxlength="50">
						<div id="db_port_empty" style="display:none;" divType="message"><font color="red"><%=Messages.getString("RepositoryManager.Dialog.Title.RepositoryDBPort.Warn.Empty") %></font></div>
						<div id="db_port_invalid" style="display:none;" divType="message"><font color="red"><%=Messages.getString("RepositoryManager.Dialog.Title.RepositoryDBPort.Warn.Invalid") %></font></div>
					</td>
				</tr>
				<tr height="30">
					<td width="100"><%=Messages.getString("RepositoryManager.Dialog.Title.DBName") %></td>
					<td>
						<input type="text" id="db_name" name="db_name" style="width: 195px" maxlength="50">
						<div id="db_name_empty" style="display:none;" divType="message"><font color="red"><%=Messages.getString("RepositoryManager.Dialog.Title.RepositoryDBName.Warn.Empty") %></font></div>
					</td>
				</tr>
				<tr height="30">
					<td width="100"><%=Messages.getString("RepositoryManager.Dialog.Title.UserName") %></td>
					<td>
						<input type="text" id="user_name" name="user_name" style="width: 195px" maxlength="50">
						<div id="user_name_empty" style="display:none;" divType="message"><font color="red"><%=Messages.getString("RepositoryManager.Dialog.Title.RepositoryUserName.Warn.Empty") %></font></div>
					</td>
				</tr>
				<tr height="30">
					<td width="100"><%=Messages.getString("RepositoryManager.Dialog.Title.Password") %></td>
					<td>
						<input type="password" id="password" name="password" style="width: 195px" maxlength="50">
						<div id="password_empty" style="display:none;" divType="message"><font color="red"><%=Messages.getString("RepositoryManager.Dialog.Title.RepositoryUserName.Warn.Empty") %></font></div>
					</td>
				</tr>		
			</table>
		</form>
	</div>
	<div id="dlg_file" class="x-hidden">
	</div>
</body>
</html>
