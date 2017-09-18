<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.net.*" %>
<%@ page import="java.util.*" %>
<%@ page import="com.auphi.ktrl.util.*" %>
<%@ page import="com.auphi.ktrl.monitor.bean.*" %>
<%@ page import="com.auphi.ktrl.monitor.util.MonitorUtil" %>
<%@ page import="com.auphi.ktrl.i18n.Messages" %>

<%
	PageList pageList = (PageList)request.getAttribute("pageList");
	List<MonitorScheduleBean> listMonitorSchedule = (List<MonitorScheduleBean>)pageList.getList();
	int pagenum = pageList.getPageInfo().getCurPage();
	String orderby = request.getAttribute("orderby")==null?"":request.getAttribute("orderby").toString();
	String order = request.getAttribute("order")==null?"DESC":request.getAttribute("order").toString();
	String search_start_date = request.getAttribute("search_start_date")==null?"":request.getAttribute("search_start_date").toString();
	String search_end_date = request.getAttribute("search_end_date")==null?"":request.getAttribute("search_end_date").toString();
	String search_text = request.getAttribute("search_text")==null?"":request.getAttribute("search_text").toString();
	String jobName = request.getAttribute("jobName")==null?"":request.getAttribute("jobName").toString();
	String jobStatus= request.getAttribute("jobStatus")==null?"":request.getAttribute("jobStatus").toString();
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<%@ include file="../../common/extjs.jsp" %>
<script type="text/javascript" src="My97DatePicker/WdatePicker.js"></script>
<title>Insert title here</title>
<script type="text/javascript">
var winErrorMsg;
var winLogMsg;
var winReload;
var winClear;
Ext.onReady(function(){
	if(!winErrorMsg){
		winErrorMsg =  Ext.create('Ext.window.Window', {
        	contentEl: 'dlg_errorMsg',
        	title:'<%=Messages.getString("Monitor.List.ErrorMsg.Title") %>',
        	width:700,
        	height:330,
        	autoHeight:true,
        	buttonAlign:'center',
        	closeAction:'hide',
        	buttons: [
      	        	{text:'<%=Messages.getString("Monitor.List.ErrorMsg.Button.Close") %>',handler: function(){winErrorMsg.hide();}}
      	        ]
        });
    }
	
	if(!winLogMsg){
		winLogMsg =  Ext.create('Ext.window.Window', {
        	contentEl: 'dlg_logMsg',
        	title:'<%=Messages.getString("Monitor.List.LogMsg.Title") %>',
        	width:700,
        	height:330,
        	autoHeight:true,
        	buttonAlign:'center',
        	closeAction:'hide',
        	buttons: [
      	        	{text:'<%=Messages.getString("Monitor.List.ErrorMsg.Button.Close") %>',handler: function(){winLogMsg.hide();}}
      	        ]
        });
    }
	
	if(!winReload){
		winReload =  Ext.create('Ext.window.Window', {
        	contentEl: 'dlg_reload',
        	title:'<%=Messages.getString("Monitor.List.Reload.Title") %>',
        	width:150,
        	height:95,
        	autoHeight:true,
        	buttonAlign:'center',
        	closeAction:'hide',
        	buttons: [
      	        	{text:'<%=Messages.getString("Monitor.List.Reload.Button.Reload") %>',handler: 
      	        		function(){
      	        			var checked_id = document.getElementById('checked_id').value;
      	        			var reload_date = document.getElementById('reload_date').value;
      	        			if(reload_date == ''){
      	        				Ext.MessageBox.alert('<%=Messages.getString("Monitor.Warnning.Warn") %>','<%=Messages.getString("Monitor.Warnning.Reload.DateChoose") %>');
      	        				return false;
      	        			}else {
      	        				window.location.href = "monitor?action=reload&page=<%=pagenum %>&orderby=<%=orderby %>&order=<%=order %>&search_start_date=<%=search_start_date %>&search_end_date=<%=search_end_date %>&search_text=<%=URLEncoder.encode(search_text, "UTF-8") %>&jobName=<%=URLEncoder.encode(jobName, "UTF-8") %>&checked_id=" + checked_id + "&reload_date=" + reload_date;
          	        			winReload.hide();
          	        			return true;
      	        			}
      	        		}
      	        	}
      	        ]
        });
    }
	
	if(!winClear){
		winClear =  Ext.create('Ext.window.Window', {
        	contentEl: 'dlg_clear',
        	title:'<%=Messages.getString("Monitor.List.Reload.Title") %>',
        	width:190,
        	height:150,
        	autoHeight:true,
        	buttonAlign:'center',
        	closeAction:'hide',
        	buttons: [
      	        	{text:'<%=Messages.getString("Monitor.List.Clear.Button.Clear") %>',handler: 
      	        		function(){
      	        			var clear_date = document.getElementById('clear_date').value;
      	        			if(clear_date == ''){
      	        				Ext.MessageBox.alert('<%=Messages.getString("Monitor.Warnning.Warn") %>','<%=Messages.getString("Monitor.Warnning.Clear.DateChoose") %>');
      	        				return false;
      	        			}else {
      	        				window.location.href = "monitor?action=clear&page=<%=pagenum %>&orderby=<%=orderby %>&order=<%=order %>&search_start_date=<%=search_start_date %>&search_end_date=<%=search_end_date %>&search_text=<%=URLEncoder.encode(search_text, "UTF-8") %>&jobName=<%=URLEncoder.encode(jobName, "UTF-8") %>&clear_date=" + clear_date;
              	        		winClear.hide();
              	        		return true;
      	        			}
      	        		}
      	        	}
      	        ]
        });
    }
	
var select_data =new Ext.data.Store({
		
		fields:['select_name','select_value'],
		data:[
		     {select_name:'- - -请选择- - -',select_value:''}, 
		     {select_name:'<%=Messages.getString("Monitor.Status.Running")%>',select_value:'<%=Messages.getString("Monitor.Status.Running")%>'},
		     {select_name:'<%=Messages.getString("Monitor.Status.Finished")%>',select_value:'<%=Messages.getString("Monitor.Status.Finished")%>'},
		     {select_name:'<%=Messages.getString("Monitor.Status.Error")%>',select_value:'<%=Messages.getString("Monitor.Status.Error")%>'},
		     {select_name:'<%=Messages.getString("Monitor.Status.Stopped")%>',select_value:'<%=Messages.getString("Monitor.Status.Stopped")%>'}
		      ]
	});
	//select_data.setDefaultSort('select_value'); 
	var select_Combo = Ext.create("Ext.form.ComboBox",{
		name:'select_seq',
        id : 'select_sel',
        fieldLabel:'<%=Messages.getString("Scheduler.Table.Column.Status")%>',
        labelPad:5,
        labelWidth:35,
        width:140,
        displayField:'select_name',
        valueField:'select_value',
        hiddenname:'select_seq',
        store: select_data,
        triggerAction: "all",
        queryMode: 'local',
        forceSelection: false,
        allowBlank: true,
        editable: false
	});
	//var hiddle=ext.get('hiddle_value').dom.value;
	
	//select_data.load();
	select_Combo.setValue('<%=jobStatus%>');
	
	
	
	
	var storeSort = new Ext.data.Store({
		fields: ['sortname', 'sortvalue'],
	    data : [
	        {sortname:'<%=Messages.getString("Monitor.List.Table.Column.StartTime") %>', sortvalue:'START_TIME'},
	        {sortname: '<%=Messages.getString("Monitor.List.Table.Column.EndTime") %>', sortvalue: 'END_TIME'},
	        {sortname: '<%=Messages.getString("Monitor.List.Table.Column.ContinuedTime") %>', sortvalue: 'CONTINUED_TIME'},
	        {sortname: '<%=Messages.getString("Monitor.List.Table.Column.Name") %>', sortvalue: 'JOBNAME'}
	    ]
	});
	
	var sortCombo = Ext.create("Ext.form.ComboBox",{
		name:'sort_sel',
        id : 'sort_sel',
        fieldLabel:'<%=Messages.getString("Monitor.List.Label.Sort") %>',
        labelPad:5,
        labelWidth:35,
        width:140,
        displayField:'sortname',
        valueField:'sortvalue',
        store: storeSort,
        triggerAction: "all",
        queryMode: 'local',
        forceSelection: false,
        allowBlank: true,
        editable: false,
        //emptyText:'<%=Messages.getString("Monitor.List.Table.Column.StartTime") %>',
        //value: 'START_TIME',
        listeners:{
            select:function(combo, record, index){
            	if(combo.value){
            		window.location.href = 'monitor?action=list&page=<%=pagenum %>&orderby=' + combo.value + '&order=<%=order %>&search_start_date=<%=search_start_date %>&search_end_date=<%=search_end_date %>&search_text=<%=URLEncoder.encode(search_text, "UTF-8") %>&jobName=<%=URLEncoder.encode(jobName, "UTF-8") %>';
            	}else if(record[0].data.sortvalue){
            		window.location.href = 'monitor?action=list&page=<%=pagenum %>&orderby=' + record[0].data.sortvalue + '&order=<%=order %>&search_start_date=<%=search_start_date %>&search_end_date=<%=search_end_date %>&search_text=<%=URLEncoder.encode(search_text, "UTF-8") %>&jobName=<%=URLEncoder.encode(jobName, "UTF-8") %>';
            	}
            }
        }
	});
	
	sortCombo.setValue('<%=orderby %>');
	
	var tb = Ext.create('Ext.toolbar.Toolbar');
	tb.render('toolbar');
	tb.add({text: '<%=Messages.getString("Monitor.List.Button.Stop") %>',iconCls: 'delete',handler: onStopClick});
	tb.add({text: '<%=Messages.getString("Monitor.List.Button.Reload") %>',iconCls: 'reload',handler: onReloadClick});
	tb.add({text: '<%=Messages.getString("Monitor.List.Button.Refresh") %>',iconCls: 'refresh',handler: onRefreshClick});
	tb.add({text: '<%=Messages.getString("Monitor.List.Button.Delete") %>',iconCls: 'completedelete',handler: onDeleteClick});
	tb.add({text: '<%=Messages.getString("Monitor.List.Button.Clear") %>',iconCls: 'completedelete',handler: onClearClick});
	tb.add({xtype: 'tbseparator'});
	tb.add(sortCombo);
	if('<%=order %>' == 'ASC'){
		tb.add({xtype: 'button',text: '<%=Messages.getString("Monitor.List.Button.ASC") %>',iconCls: 'asc',handler: onOrderClick,tooltip:'<%=Messages.getString("Monitor.List.Button.ASC.Tooltip") %>',tooltipType:'title'});
	}else {
		tb.add({text: '<%=Messages.getString("Monitor.List.Button.DESC") %>',iconCls: 'desc',handler: onOrderClick,tooltip:'<%=Messages.getString("Monitor.List.Button.DESC.Tooltip") %>',tooltipType:'title'});
	}
	tb.add({xtype: 'tbseparator'});
	tb.add(select_Combo);
	tb.add('<%=Messages.getString("Monitor.List.Label.StartDate") %>');
	tb.add('<input type=\"text\" id=\"search_start_date\" name=\"search_start_date\" value=\"<%=search_start_date %>\" style=\"width:70px;\" onclick=\"WdatePicker({dateFmt:\'yyyy-MM-dd\'});\">');
	tb.add('<%=Messages.getString("Monitor.List.Label.EndDate") %>');
	tb.add('<input type=\"text\" id=\"search_end_date\" name=\"search_end_date\" value=\"<%=search_end_date %>\" style=\"width:70px;\" onclick=\"WdatePicker({dateFmt:\'yyyy-MM-dd\'});\">');
	tb.add('<%=Messages.getString("Monitor.List.Label.KeyWords") %>');
	tb.add('<input type=\"text\" id=\"search_text\" name=\"search_text\" value=\"<%=search_text %>\" maxlength=\"200\" style=\"width:70px;\">');
	tb.add({text: '<%=Messages.getString("Monitor.List.Button.Search") %>',iconCls: 'search',handler: onSearchClick,tooltip:'<%=Messages.getString("Monitor.List.Button.Search.Tooltip") %>',tooltipType:'title'});
	
	tb.doLayout();
	
	function onReloadClick(){
		var checks = document.getElementsByName('check');
		var checked_id = '';
		var check_count = 0;
		if(checks.length){
			for(var i=0;i<checks.length;i++){
				if(checks[i].checked){
					check_count = check_count + 1;
					if(checked_id == ''){
						checked_id = checks[i].value;
					}else {
						checked_id = checked_id + ',' + checks[i].value;
					}
				}
			}
			if(check_count == 0){
				Ext.MessageBox.alert('<%=Messages.getString("Monitor.Warnning.Warn") %>','<%=Messages.getString("Monitor.Warnning.Reload.Choose") %>');
				return false;
			}
		}else {
			Ext.MessageBox.alert('<%=Messages.getString("Monitor.Warnning.Warn") %>','<%=Messages.getString("Monitor.Warnning.Reload.Choose") %>');
			return false;
		}
		document.getElementById('checked_id').value = checked_id;
       	winReload.show();
    }
	
	function onStopClick(){
		var checks = document.getElementsByName('check');
		var checked_id = '';
		var check_count = 0;
		if(checks.length){
			for(var i=0;i<checks.length;i++){
				if(checks[i].checked){
					check_count = check_count + 1;
					if(checked_id == ''){
						checked_id = checks[i].value;
					}else {
						checked_id = checked_id + ',' + checks[i].value;
					}
				}
			}
			if(check_count == 0){
				Ext.MessageBox.alert('<%=Messages.getString("Monitor.Warnning.Warn") %>','<%=Messages.getString("Monitor.Warnning.Stop.Choose") %>');
				return false;
			}
		}else {
			Ext.MessageBox.alert('<%=Messages.getString("Monitor.Warnning.Warn") %>','<%=Messages.getString("Monitor.Warnning.Stop.Choose") %>');
			return false;
		}
		
		
		Ext.MessageBox.confirm('<%=Messages.getString("Monitor.Confirm.Delete.Title") %>', '<%=Messages.getString("Monitor.Confirm.Stop.Message") %>', function (btn){
			if(btn=="yes"){
				document.getElementById('checked_id').value = checked_id;
				document.getElementById('listForm').action = 'monitor?action=stopRunning&orderby=<%=orderby %>&order=<%=order %>&search_start_date=<%=search_start_date %>&search_end_date=<%=search_end_date %>&search_text=<%=URLEncoder.encode(search_text, "UTF-8") %>&jobName=<%=URLEncoder.encode(jobName, "UTF-8") %>';
				document.getElementById('listForm').submit();
			}
		});
	}
	
	function onRefreshClick(){
       	window.location.href = "monitor?action=list&page=<%=pagenum %>&orderby=<%=orderby %>&order=<%=order %>&search_start_date=<%=search_start_date %>&search_end_date=<%=search_end_date %>&search_text=<%=URLEncoder.encode(search_text, "UTF-8") %>&jobName=<%=URLEncoder.encode(jobName, "UTF-8") %>";
    }
	
	function onOrderClick(){
		if('<%=order %>' == 'ASC'){
			window.location.href = "monitor?action=list&page=<%=pagenum %>&orderby=<%=orderby %>&order=DESC&search_start_date=<%=search_start_date %>&search_end_date=<%=search_end_date %>&search_text=<%=URLEncoder.encode(search_text, "UTF-8") %>&jobName=<%=URLEncoder.encode(jobName, "UTF-8") %>";
		}else {
			window.location.href = "monitor?action=list&page=<%=pagenum %>&orderby=<%=orderby %>&order=ASC&search_start_date=<%=search_start_date %>&search_end_date=<%=search_end_date %>&search_text=<%=URLEncoder.encode(search_text, "UTF-8") %>&jobName=<%=URLEncoder.encode(jobName, "UTF-8") %>";
		}
	}
	
	function onSearchClick(){
		var search_text = document.getElementById('search_text').value;
		var search_start_date = document.getElementById('search_start_date').value;
		var search_end_date = document.getElementById('search_end_date').value;
		var jobStatus=select_Combo.getValue();
		//Ext.MessageBox.alert("sds",jobStatus);
		window.location.href = "monitor?action=list&jobName=<%=URLEncoder.encode(jobName, "UTF-8") %>&orderby=<%=orderby %>&order=<%=order %>&search_start_date=" + search_start_date + "&search_end_date=" + search_end_date + "&search_text=" 
				+ encodeURI(search_text)+"&jobStatus="+encodeURI(jobStatus);
	}
	
	function onDeleteClick(){
		var checks = document.getElementsByName('check');
		var checked_id = '';
		var check_count = 0;
		if(checks.length){
			for(var i=0;i<checks.length;i++){
				if(checks[i].checked){
					check_count = check_count + 1;
					if(checked_id == ''){
						checked_id = checks[i].value;
					}else {
						checked_id = checked_id + ',' + checks[i].value;
					}
				}
			}
			if(check_count == 0){
				Ext.MessageBox.alert('<%=Messages.getString("Monitor.Warnning.Warn") %>','<%=Messages.getString("Monitor.Warnning.Delete.Choose") %>');
				return false;
			}
		}else {
			Ext.MessageBox.alert('<%=Messages.getString("Monitor.Warnning.Warn") %>','<%=Messages.getString("Monitor.Warnning.Delete.Choose") %>');
			return false;
		}
		
		
		Ext.MessageBox.confirm('<%=Messages.getString("Monitor.Confirm.Delete.Title") %>', '<%=Messages.getString("Monitor.Confirm.Delete.Message") %>', function (btn){
			if(btn=="yes"){
				document.getElementById('checked_id').value = checked_id;
				document.getElementById('listForm').action = 'monitor?action=delete&orderby=<%=orderby %>&order=<%=order %>&search_start_date=<%=search_start_date %>&search_end_date=<%=search_end_date %>&search_text=<%=URLEncoder.encode(search_text, "UTF-8") %>&jobName=<%=URLEncoder.encode(jobName, "UTF-8") %>';
				document.getElementById('listForm').submit();
			}
		});
	}
	
	function onClearClick(){
       	winClear.show();
	}
});

function showErrorMsg(id){
	document.getElementById('errorMsgIframe').src = 'monitor?action=showErrorLog&id=' + id;
	winErrorMsg.show();
}

function showLogMsg(id){
	document.getElementById('logMsgIframe').src = 'monitor?action=showLog&id=' + id;
	winLogMsg.show();
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

function showDetails(jobName, jobStatus, idBatch, jobFile, id_logchannel){
	var vNum = Math.random();
	vNum = Math.round(vNum*10000000);
	parent.toLoadurl('monitor?action=showDetail&id_logchannel=' + id_logchannel + '&id_batch=' + idBatch + '&jobStatus=' + encodeURI(jobStatus) + '&jobName=' + encodeURI(jobName) + '&jobFile=' + encodeURI(jobFile),'monitor_detail_' + vNum,'<%=Messages.getString("Default.Jsp.Menu.Monitor") + "_" %>' + jobName);
}
</script>
</head>
<body onload="parent.iframeResize(this.frames.name);" onresize="parent.iframeResize(this.frames.name);">
	<div id="toolbar"></div>
	<form id="listForm" name="listForm" action="" method="post">
	<%=pageList.getPageInfo().getHtml("monitor?action=list&orderby=" + orderby + "&order=" + order + "&search_start_date=" + search_start_date + "&search_end_date=" + search_end_date + "&search_text=" + search_text + "&jobName=" + jobName) %>
	<br />
	<input type="hidden" name="checked_id" id="checked_id">
	<table width="90%" align="center" id="the-table">
		<tr align="center" bgcolor="#ADD8E6" class="b_tr">
			<td><input type="checkbox" name="checkall" id="checkall" onclick="checkAll();"><%=Messages.getString("Monitor.Table.Column.Choose") %></td>
			<td><%=Messages.getString("Monitor.List.Table.Column.Name") %></td>
			<td><%=Messages.getString("Monitor.List.Table.Column.File") %></td>
			<td><%=Messages.getString("Monitor.List.Table.Column.ClusterName") %></td>
			<td><%=Messages.getString("Monitor.List.Table.Column.ServerName") %></td>
			<td><%=Messages.getString("Monitor.List.Table.Column.Status") %></td>
			<td><%=Messages.getString("Monitor.List.Table.Column.ContinuedTime") %></td>
			<td><%=Messages.getString("Monitor.List.Table.Column.LinesErrors") %></td>
			<td><%=Messages.getString("Monitor.List.Table.Column.StartTime") %></td>
			<td><%=Messages.getString("Monitor.List.Table.Column.EndTime") %></td>
			<td><%=Messages.getString("Monitor.List.Table.Column.LogMsg") %></td>
			<td><%=Messages.getString("Monitor.List.Table.Column.ErrorMsg") %></td>
		</tr>
<%
	for(MonitorScheduleBean monitorScheduleBean:listMonitorSchedule){
%>	
		<tr>
			<td align="center" nowrap="nowrap"><input type="checkbox" name="check" value="<%=monitorScheduleBean.getId() %>" class="ainput"></td>
			<!-- <td><a href="#" onclick="parent.toLoadurl('monitor?action=show&jobName=<%=URLEncoder.encode(monitorScheduleBean.getJobName(), "UTF-8") %>','<%=monitorScheduleBean.getJobName() %>','<%=monitorScheduleBean.getJobName() %>');"><%=monitorScheduleBean.getJobName() %></a></td> -->
			<td><a href="#" onclick="showDetails('<%=monitorScheduleBean.getJobName() %>', '<%=monitorScheduleBean.getJobStatus() %>', '<%=monitorScheduleBean.getId_batch() %>', '<%=monitorScheduleBean.getJobFile() %>', '<%=monitorScheduleBean.getId_logchannel() %>');"><%=monitorScheduleBean.getJobName() %></a></td>
			<td><%=monitorScheduleBean.getJobFile() %></td>
			<td><%=monitorScheduleBean.getHaName() %></td>
			<td><%=monitorScheduleBean.getServerName() %></td>
			<td><%=monitorScheduleBean.getJobStatus() %></td>
<%
		float continuedTime = monitorScheduleBean.getContinuedTime();
		String continued_time = String.valueOf(continuedTime); 
		if("".equals(monitorScheduleBean.getEndTime()) && !MonitorUtil.STATUS_ERROR.equals(monitorScheduleBean.getJobStatus())){//now running,time is now-start
			Date start = StringUtil.StringToDate(monitorScheduleBean.getStartTime(), "yyyy-MM-dd HH:mm:ss");
			Date now = new Date();
			continuedTime = ((float)(now.getTime() - start.getTime()))/1000;
			continued_time = String.valueOf(continuedTime);
			continued_time = continued_time.substring(0, continued_time.indexOf(".") + 2);
		}
%>				
			<td><%=continued_time %></td>
			<td><%=monitorScheduleBean.getLines_error() %></td>
			<td><%=monitorScheduleBean.getStartTime() %></td>
			<td><%=monitorScheduleBean.getEndTime() %></td>
			<td><a href="#" onclick="showLogMsg('<%=monitorScheduleBean.getId() %>');"><%=Messages.getString("Monitor.List.Table.ErrorMsg.View") %></a></td>
			<td><a href="#" onclick="showErrorMsg('<%=monitorScheduleBean.getId() %>');"><%=Messages.getString("Monitor.List.Table.ErrorMsg.View") %></a></td>
		</tr>
<%
	}
%>		
	</table>
	</form>
	<div id="dlg_logMsg" class="x-hidden">
		<table border="0" align="center" height="30" width="98%">
			<tr>
				<td colspan="2" width="100%"><iframe id="logMsgIframe" name="logMsgIframe" frameborder="0" width="100%" height="260px"></iframe></td>
			</tr>
		</table>
	</div>
	<div id="dlg_errorMsg" class="x-hidden">
		<table border="0" align="center" height="30" width="98%">
			<tr>
				<td colspan="2" width="100%"><iframe id="errorMsgIframe" name="errorMsgIframe" frameborder="0" width="100%" height="260px"></iframe></td>
			</tr>
		</table>
	</div>
	<div id="dlg_reload" class="x-hidden">
		<table border="0" align="center" height="30" width="98%">
			<tr>
				<td width="100%">
					<%=Messages.getString("Monitor.Reload.ChooseDate") %><input type="text" id="reload_date" name="reload_date" style="width:70px;" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm'});">
				</td>
			</tr>
		</table>
	</div>
	<div id="dlg_clear" class="x-hidden">
		<table border="0" align="center" height="30" width="98%">
			<tr>
				<td width="100%" style="color: red">
				*清除日志将清除启动时间在所选日期之前的所有监控记录，请谨慎使用！
				</td>
			</tr>
			<tr>
				<td width="100%">
					<%=Messages.getString("Monitor.Reload.ChooseDate") %><input type="text" id="clear_date" name="clear_date" style="width:70px;" onclick="WdatePicker({dateFmt:'yyyy-MM-dd'});">
				</td>
			</tr>
		</table>
	</div>
</body>
</html>
