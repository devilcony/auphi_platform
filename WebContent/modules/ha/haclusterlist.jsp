<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="com.auphi.ktrl.util.*" %>
<%@ page import="com.auphi.ktrl.ha.bean.*" %>
<%@ page import="com.auphi.ktrl.i18n.Messages" %>

<%
	PageList pageList = (PageList)request.getAttribute("pageList");
	List<HAClusterBean> listHACluster = (List<HAClusterBean>)pageList.getList();
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<%@ include file="../../common/extjs.jsp" %>
<script type="text/javascript">
var win;
var winSlaves;
var winSlaveStatus;
var cluster_id = '';
var myStore;
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
	
	if(!winSlaves){
        winSlaves =  Ext.create('Ext.window.Window', {
        	contentEl: 'dlg_slaves',
        	title:'<%=Messages.getString("HACluster.Dialog.SelSlaves.Title") %>',
        	width:207,
        	height:290,
        	autoHeight:true,
        	buttonAlign:'center',
        	closeAction:'hide',
	        buttons: [
	        	{text:'<%=Messages.getString("Default.Jsp.Dialog.Submit") %>',handler: function(){
	        			var selSlaves = document.getElementById('selSlaves');
	        			var slave_names = document.getElementById('slave_names');
	        			var slave_ids = document.getElementById('slave_ids');
	        			
	        			var sel_text = "";
	        			var sel_value = "";
	        			for(var i=0;i<selSlaves.length;i++){     
	        		        if(selSlaves.options[i].selected){
	        		        	if(sel_value.length == 0){
	        		        		sel_value = selSlaves.options[i].value;
	        		        	}else {
	        		        		sel_value = sel_value + ',' + selSlaves.options[i].value;
	        		        	}
	        		        	if(sel_text.length == 0){
	        		        		sel_text = selSlaves.options[i].text;
	        		        	}else {
	        		        		sel_text = sel_text + ',' + selSlaves.options[i].text;
	        		        	}
	        		        }  
	        		    }
	        			
	        			slave_names.value = sel_text;
	        			slave_ids.value = sel_value;
	        			winSlaves.hide();
	    	    	}
	        	},
	        	{text:'<%=Messages.getString("Default.Jsp.Dialog.Cancel") %>',handler: function(){winSlaves.hide();}}
	        ]
        });
    }
	
	if(!winSlaveStatus){
		winSlaveStatus =  Ext.create('Ext.window.Window', {
        	contentEl: 'dlg_slave_status',
        	title:'',
        	width:450,
        	height:200,
        	autoHeight:true,
        	buttonAlign:'center',
        	closeAction:'hide',
	        buttons: [
	        	{text:'<%=Messages.getString("Default.Jsp.Dialog.Close") %>',handler: function(){winSlaveStatus.hide();}}
	        ]
        });
    }
	
	function onCreateClick(){
		cluster_id = '';
		document.getElementById('dataForm').reset();
       	win.setTitle('<%=Messages.getString("Default.Jsp.Toolbar.Create") %>');
        win.show();
    }
	
	function onUpdateClick(){
		document.getElementById('dataForm').reset();
		var checks = document.getElementsByName('check');
		var id_cluster = '';
		var check_count = 0 ;
		if(checks.length)
		{
			for(var i=0;i<checks.length;i++){
				if(checks[i].checked){
					check_count++;
					if(id_cluster == ''){
						id_cluster = checks[i].value;
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
		
		cluster_id = id_cluster;
		
		Ext.Ajax.request(
		{
			url: 'hamanage',
			method: 'POST',
			params: {
		        'action': 'beforeUpdate',
		        'id_cluster': id_cluster
		    },
			success: function(transport) 
			{
				var data = eval('('+transport.responseText+')');
				document.getElementById('id_cluster').value = data.item.id__cluster;
		       	document.getElementById('cluster_name').value = data.item.name;
		       	document.getElementById('base_port').value = data.item.base__port;
			    document.getElementById('sockets_buffer_size').value = data.item.sockets__buffer__size;
			    document.getElementById('sockets_flush_interval').value = data.item.sockets__flush__interval;
			    document.getElementById('slave_ids').value = data.item.slaves.string[0];
			    document.getElementById('slave_names').value = data.item.slaves.string[1];
			    var socket_compressed_radio = document.getElementById('socket_compressed_radio');
			    if('0' == data.item.socket__compressed){
			    	socket_compressed_radio[0].checked = 'true';
			    }else if('1' == data.item.socket__compressed){
			    	socket_compressed_radio[1].checked = 'true';
			    }
			    var dynamic_cluster_radio = document.getElementById('dynamic_cluster_radio');
			    if('0' == data.item.dynamic_cluster){
			    	dynamic_cluster_radio[0].checked = 'true';
			    }else if('1' == data.item.dynamic_cluster){
			    	dynamic_cluster_radio[1].checked = 'true';
			    }
			}
		});
		
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
				document.getElementById('listForm').action = 'hamanage?action=delete';
				document.getElementById('listForm').submit();
			}
		});
	}
	
	function onRefreshClick(){
		window.location.href = 'hamanage?action=list';
    }
	
	Ext.define('ServerStatus', {
	    extend: 'Ext.data.Model',
	    fields: [
			{name: 'id_slave', type: 'int'},
	        {name: 'name_slave', type: 'string'},
	        {name: 'is_running', type: 'string'},
	        {name: 'cpu_usage', type: 'string'},
	        {name: 'memory_usage', type: 'string'},
	        {name: 'running_jobs', type: 'int'}
	    ]
	});
	
	myStore = new Ext.data.Store({
	    model: 'ServerStatus',
	    proxy: {
	        type: 'ajax',
	        url : 'hamanage?action=getSlaveStatus',
	        reader: {
	            type: 'json',
	            root: 'serverStatus'
	        }
	    },
	    autoLoad: false
	});
	
	Ext.create('Ext.grid.Panel', {
        store: myStore,
        stateful: true,
        stateId: 'stateGrid',
        width: 500,
        height: 195,
        renderTo: 'grid_slave_status',
        columns: [
        	{
                text     : '<%=Messages.getString("SlaveServer.Status.Dialog.Name") %>',
                sortable : true,
                width    : 75,
                dataIndex: 'name_slave'
            },{
                text     : '<%=Messages.getString("SlaveServer.Status.Dialog.Running") %>',
                sortable : true,
                width    : 70,
                dataIndex: 'is_running'
            },{
                text     : '<%=Messages.getString("SlaveServer.Status.Dialog.CPU_Usage") %>',
                sortable : true,
                width    : 90,
                dataIndex: 'cpu_usage'
            },{
                text     : '<%=Messages.getString("SlaveServer.Status.Dialog.Memory_Usage") %>',
                sortable : true,
                width    : 105,
                dataIndex: 'memory_usage'
            },{
                text     : '<%=Messages.getString("SlaveServer.Status.Dialog.Running_Jobs") %>',
                sortable : true,
                width    : 100,
                dataIndex: 'running_jobs'
            }
        ],
        viewConfig: {
            stripeRows: true
        }
    });
});

function valiDataForm()
{
	var cluster_name = document.getElementById('cluster_name').value;
	var slave_ids = document.getElementById('slave_ids').value;
	var slave_names = document.getElementById('slave_names').value;
	
	var success = true;
	
	if(cluster_name == ''){
		document.getElementById('cluster_name_empty').style.display = '';
		success = false;
	}else {
		document.getElementById('cluster_name_empty').style.display = 'none';
	}
	
	if(slave_ids == '' && slave_names == ''){
		document.getElementById('slaves_empty').style.display = '';
		success = false;
	}else {
		document.getElementById('slaves_empty').style.display = 'none';
	}

	if(success)
	{
		Ext.Ajax.request({
			url:'hamanage',
			method:'POST',
			params: {
		        'action' : 'checkExist',
		        'name_cluster' : cluster_name,
		        'cluster_id': cluster_id
		    },
		    success: function(transport){
				if('nameExist' == transport.responseText){
					document.getElementById('cluster_name_exist').style.display = '';
					document.getElementById('slave_in_use').style.display = 'none';
				}else if('' == transport.responseText) {
					if(cluster_id == ''){
						Ext.getDom('dataForm').action = 'hamanage?action=insert';
						Ext.getDom('dataForm').submit();
					}else {
						Ext.getDom('dataForm').action = 'hamanage?action=update';
						Ext.getDom('dataForm').submit();
					}			
				}else {
					document.getElementById('cluster_name_exist').style.display = 'none';
					document.getElementById('slave_in_use').style.display = '';
				}
			}
		});		
	}	
}

function selSlaves(){
	Ext.Ajax.request({
		url:'hamanage',
		method:'POST',
		params: {
	        'action' : 'getSlavesNotUsed',
	        'cluster_id': cluster_id
	    },
	    success: function(transport){
	    	var data = eval('('+transport.responseText+')');
	    	var ids_not_used = data["string-array"].string[0].split(',');
	    	var names_not_used = data["string-array"].string[1].split(',');
	    	
	    	var selSlaves = document.getElementById('selSlaves');
	    	selSlaves.options.length = 0;    
	    	for(var i=0;i<ids_not_used.length;i++){
	    		var oOption = document.createElement("option");
		        oOption.text = names_not_used[i];
		        oOption.value = ids_not_used[i];
		        selSlaves.add(oOption);
	    	}
	    	
	    	var slave_ids = document.getElementById('slave_ids').value;
	    	var sel_slave_ids = slave_ids.split(',');
	    	
	    	for(var i=0;i<sel_slave_ids.length;i++){    
	    		for(var j=0;j<selSlaves.options.length;j++){
	    			if(selSlaves.options[j].value == sel_slave_ids[i]){
	    				selSlaves.options[j].selected = true;
	    	        	break;
	    	        }	
	    		}  
	        }
		}
	});
	
	winSlaves.show();
}

function showStatus(cluster_id, cluster_name){
	myStore.proxy.url = 'hamanage?action=getSlaveStatus&id_cluster=' + cluster_id;
	myStore.load();
	
	winSlaveStatus.setTitle('<%=Messages.getString("SlaveServer.Status.Dialog.Title1") %>[' + cluster_name + ']<%=Messages.getString("SlaveServer.Status.Dialog.Title2") %>');
	winSlaveStatus.show();
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

</script>
</head>
<body onload="parent.iframeResize(this.frames.name);" onresize="parent.iframeResize(this.frames.name);">
	<div id="toolbar"></div>
	<form id="listForm" name="listForm" action="" method="post">
	<input type="hidden" id="sel_ids" name="sel_ids" value="">
	<%=pageList.getPageInfo().getHtml("hamanage?action=list") %>
	<br />
	<table width="90%" align="center" id="the-table">
		<tr align="center" bgcolor="#ADD8E6" class="b_tr">
			<td><input type="checkbox" name="checkall" id="checkall" onclick="checkAll();"><%=Messages.getString("Default.Jsp.ChooseAll") %></td>
			<td><%=Messages.getString("HACluster.Table.Title.Name") %></td>
			<td><%=Messages.getString("HACluster.Table.Title.SlaveServers") %></td>
			<!-- <td><%=Messages.getString("HACluster.Table.Title.BasePort") %></td>
			<td><%=Messages.getString("HACluster.Table.Title.SocketsBufferSize") %></td>
			<td><%=Messages.getString("HACluster.Table.Title.SocketsFlushInterval") %></td>
			<td><%=Messages.getString("HACluster.Table.Title.SocketsCompressed") %></td>
			<td><%=Messages.getString("HACluster.Table.Title.DynamicCluster") %></td> -->
			<td><%=Messages.getString("HACluster.Table.Title.Status") %></td>
		</tr>
<%
	for(HAClusterBean haClusterBean : listHACluster)
	{
%>	
		<tr>
			<td align="center" nowrap="nowrap"><input type="checkbox" name="check" value="<%=haClusterBean.getId_cluster() %>" class="ainput"></td>
			<td nowrap="nowrap"><%=haClusterBean.getName()==null?"":haClusterBean.getName()%></td>
			<td nowrap="nowrap"><%=haClusterBean.getSlaves()==null?"":haClusterBean.getSlaves()[1]%></td>
			<!-- <td nowrap="nowrap"><%=haClusterBean.getBase_port()==null?"":haClusterBean.getBase_port() %></td>
			<td nowrap="nowrap"><%=haClusterBean.getSockets_buffer_size()==null?"":haClusterBean.getSockets_buffer_size() %></td>
			<td nowrap="nowrap"><%=haClusterBean.getSockets_flush_interval()==null?"":haClusterBean.getSockets_flush_interval() %></td>
			<td nowrap="nowrap"><%=haClusterBean.getSockets_compressed()==null?"":haClusterBean.getSockets_compressed() %></td>
			<td nowrap="nowrap"><%=haClusterBean.getDynamic_cluster()==null?"":haClusterBean.getDynamic_cluster() %></td> -->
			<td nowrap="nowrap"><a href="#" onclick="showStatus('<%=haClusterBean.getId_cluster() %>', '<%=haClusterBean.getName() %>');"><%=Messages.getString("Default.Jsp.View") %></a></td>
		</tr>
<%
	}
%>		
	</table>
	</form>
	<div id="dlg" class="x-hidden">
		<form id="dataForm" name="dataForm" action="" method="post">
			<input type="reset" style="display: none;"><input type="submit" style="display: none;">
			<input type="hidden" name="id_cluster" id="id_cluster">
			<table border="0" align="center" height="30" width="98%">
				<tr height="30">
					<td width="100"><%=Messages.getString("HACluster.Table.Title.Name") %></td>
					<td>
						<input type="text" id="cluster_name" name="cluster_name" style="width: 195px" value="" maxlength="50">
						<div id="cluster_name_empty" style="display:none;" divType="message"><font color="red"><%=Messages.getString("HACluster.Table.Warn.ClusterNameEmpty") %></font></div>
						<div id="cluster_name_exist" style="display:none;" divType="message"><font color="red"><%=Messages.getString("HACluster.Table.Warn.ClusterNameExist") %></font></div>
					</td>
				</tr>
				<tr height="30">
					<td width="100"><%=Messages.getString("HACluster.Table.Title.SlaveServers") %></td>
					<td>
						<input type="text" id="slave_names" name="slave_names" style="width: 195px" readonly="readonly" onclick="selSlaves();">
						<input type="hidden" id="slave_ids" name="slave_ids">
						<div id="slaves_empty" style="display:none;" divType="message"><font color="red"><%=Messages.getString("HACluster.Table.Warn.SlavesEmpty") %></font></div>
					</td>
				</tr>
				<tr height="30" style="display: none;">
					<td width="100"><%=Messages.getString("HACluster.Table.Title.BasePort") %></td>
					<td>
						<input type="text" id="base_port" name="base_port" style="width: 195px" maxlength="50">
					</td>
				</tr>
				<tr height="30" style="display: none;">
					<td width="100"><%=Messages.getString("HACluster.Table.Title.SocketsBufferSize") %></td>
					<td>
						<input type="text" id="sockets_buffer_size" name="sockets_buffer_size" style="width: 195px" maxlength="50">
					</td>
				</tr>
				<tr height="30" style="display: none;">
					<td width="100"><%=Messages.getString("HACluster.Table.Title.SocketsFlushInterval") %></td>
					<td>
						<input type="text" id="sockets_flush_interval" name="sockets_flush_interval" style="width: 195px" maxlength="50">
					</td>
				<tr height="30" style="display: none;">
					<td width="100"><%=Messages.getString("HACluster.Table.Title.SocketsCompressed") %></td>
					<td >
						<input type="radio" id="sockets_compressed_radio" name="sockets_compressed_radio" value="1" checked="false" onchange="proxyCheckChange(1);"><%=Messages.getString("Default.Jsp.Yes") %>
						<input type="radio" id="sockets_compressed_radio" name="sockets_compressed_radio" value="0" checked="true" onchange="proxyCheckChange(0);"><%=Messages.getString("Default.Jsp.No") %>
					</td>
				</tr>
				<tr height="30" style="display: none;">
					<td width="100"><%=Messages.getString("HACluster.Table.Title.DynamicCluster") %></td>
					<td>
						<input type="radio" id="dynamic_cluster_radio" name="dynamic_cluster_radio" value="1" checked="true"><%=Messages.getString("Default.Jsp.Yes") %>
						<input type="radio" id="dynamic_cluster_radio" name="dynamic_cluster_radio" value="0" checked="false"><%=Messages.getString("Default.Jsp.No") %>
					</td>
				</tr>
			</table>
		</form>
	</div>
	<div id="dlg_slaves" class="x-hidden">
		<select id="selSlaves" name="selSlaves" multiple="multiple" style="width: 195px" size="15"></select>
	</div>
	<div id="dlg_slave_status" class="x-hidden">
		<div id="grid_slave_status"></div>
	</div>
</body>
</html>
