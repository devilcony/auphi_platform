<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.auphi.ktrl.i18n.Messages" %>
<%@ page import="java.util.*" %>
<%@ page import="java.io.*" %>
<%@ page import="com.auphi.ktrl.metadata.util.*" %>
<%@ page import="com.auphi.ktrl.metadata.bean.*" %>


<%
List<MetaDataSourceBean> resourceList = (List<MetaDataSourceBean>)request.getAttribute("resourceList");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<%@ include file="../../common/extjs.jsp" %>
<link rel="stylesheet" type="text/css" href="common/js-graph-it/js-graph-it.css">
<link rel="stylesheet" type="text/css" href="common/wizard/wizard-style.css" />
<script type="text/javascript" src="common/wizard/wizard.js"></script>

<title><%=Messages.getString("Metadata.Report.Title") %></title>
<script type="text/javascript">

var treeSelect="-1";
var msg; 
var myMask;
Ext.onReady(function(){
	myMask = new Ext.LoadMask(Ext.getBody(), {msg:"<%=Messages.getString("Metadata.Report.creating")%>"});
	function onRefreshClick(){
       	window.location.href = "metadata?action=resources";
    }
});

function getReportTree(optValue)
{
	//alert(optValue);
	$("#reportTree-div").html("");
	$("#reportTree-div").empty();
	var treestore = Ext.create('Ext.data.TreeStore', {
		autoLoad: true,
	    proxy: {
	    	type: 'ajax',
	    	reader: {
	    		type: 'json'
	    	},
	    	url: 'metadata?action=reportResource&resource=' + encodeURI(optValue)
	    }
	});     

	Ext.create('Ext.tree.Panel', {
	    width: '100%',
	    height: 400,
	    frame : true,
		animate : true, // 开启动画效果
		enableDD : false, // 不允许子节点拖动
		border : false, // 没有边框
		singleClickExpand : true,
	    autoScroll: true,
	    store: treestore,
	    rootVisible: false, 
	    renderTo: 'reportTree-div',
	    listeners:{
	    	itemclick:function(view,record,item,index,e){                   
	    		//Ext.MessageBox.show({  
                // 	title: '节点操作',  
                // 	msg: 'itemclick：index=' + index + ",text=" + record.data.text+ ",leaf="+record.data.leaf +
                // 		 'id=' + record.data.id,  
                //	icon: Ext.MessageBox.INFO  
             	//	}); 
	    		if (record.data.leaf){
	    			treeSelect = record.data.id;
	    		}else{
	    			treeSelect = "-1";
	    		}
            }}
	});	
}
function createHtmlReport(){
    $("#report-div").html("");
    
	if($("#resource").val()==null){
		Ext.MessageBox.show({  
             	title: '<%=Messages.getString("Metadata.Message.Title") %>',  
             	msg: '<%=Messages.getString("Metadata.Message.SelectResource") %>',  
            	icon: Ext.MessageBox.INFO  
         		});
		return;
	}
	if(treeSelect == "-1"){
		Ext.MessageBox.show({  
             	title: '<%=Messages.getString("Metadata.Message.Title") %>',  
             	msg: '<%=Messages.getString("Metadata.Report.Message.SelectJobORTrans") %>',  
            	icon: Ext.MessageBox.INFO  
         		});
		return;
	}
	myMask.show();
	Ext.Ajax.request({
		url: 'metadata',
		method: 'POST',
		timeout: 100000,
		params: {
	        action: 'createReport',
	        resource: $("#resource").val(),
	        treeSelect:treeSelect,
	        type:'create',
	        fileType:'HTML'
	    },
		success: function(transport) {
		    var res = transport.responseText;
			//alert("createReport return:"+res);
			var ress = res.split(";");
		    if(ress[0] == "success"){
		    	showHtmlReport(ress[1]);
		    	
		    }else{
		    	Ext.MessageBox.show({  
	             	title: '<%=Messages.getString("Metadata.Message.Title") %>',  
	             	msg: '<%=Messages.getString("Metadata.Report.Message.CreateReportFail") %>',  
	            	icon: Ext.MessageBox.INFO  
	         		});
		    }
		    myMask.hide();
	  	}
	});
}
function showHtmlReport(time)
{
	Ext.Ajax.request({
		url: 'metadata',
		method: 'POST',
		timeout: 100000,
		params: {
	        action: 'createReport',
	        resource: $("#resource").val(),
	        time:time,
	        treeSelect:treeSelect,
	        type:'get',
	        fileType:'HTML'
	    },
		success: function(transport) {
		    var res = transport.responseText;
		    //alert(res);
		    $("#report-div").html(res);
	  	}
	});
	
}
function exportHtml() { 
	if($("#resource").val()==null){
		Ext.MessageBox.show({  
             	title: '<%=Messages.getString("Metadata.Message.Title") %>',  
             	msg: '<%=Messages.getString("Metadata.Message.SelectResource") %>',  
            	icon: Ext.MessageBox.INFO  
         		});
		return;
	}
	var  report = $("#report-div").html();
	//alert(report);
	if(report == '')
	{
		Ext.Ajax.request({
			url: 'metadata',
			method: 'POST',
			params: {
		        action: 'createReport',
		        resource: $("#resource").val(),
		        treeSelect:treeSelect,
		        type:'create',
		        fileType:'HTML'
		        
		    },
			success: function(transport) {
			    var res = transport.responseText;    
			    report = res;
		  	}
		});
	}
	
	Ext.Ajax.request({
		url: 'metadata',
		method: 'POST',
		params: {
	        action: 'exportReport',
	        report: report
	    },
		success: function(transport) {
		    var res = transport.responseText;
		    var msg = res.split(":::");
			//alert(msg[0]);
			//alert(msg[1]);
		    if(msg[0] == "true")
	    	{
		    	Ext.Msg.alert("<%=Messages.getString("Metadata.Message.Title") %>",
		    			      "<%=Messages.getString("Metadata.Report.Message.ExportSuccess") %>"+msg[1]);
	    	}
		    else
	    	{
		    	Ext.Msg.alert("<%=Messages.getString("Metadata.Message.Title") %>",
		    			      "<%=Messages.getString("Metadata.Report.Message.ExportFail") %>");
	    	}
	  	}
	});	
}
function exportPdf() { 
	//alert("exportPdf");
	
    var formPanel = Ext.create('Ext.form.Panel', {
        width: 500,
        frame: true,
        bodyPadding: '10 10 0',
        defaults: {
            anchor: '100%',
            allowBlank: false,
            msgTarget: 'side',
            labelWidth: 50
        },
        items: [{
        	id: 'filename',
            name: 'filename',
            xtype: 'textfield',
            fieldLabel: '<%=Messages.getString("Metadata.Report.filename") %>'
        },{
            xtype: 'filefield',
            id: 'folder-path',
            name: 'folder-path',
            emptyText: '<%=Messages.getString("Metadata.Report.SelectFolder") %>',
            fieldLabel: '<%=Messages.getString("Metadata.Report.Folder") %>',
            buttonText: '<%=Messages.getString("Metadata.Report.Browse") %>',
            buttonConfig: {
                iconCls: 'upload-icon'
            },
            inputType : 'folder'
        }],

        buttons: [{
            text: '<%=Messages.getString("Metadata.Report.button.ok") %>',
            handler: function(){
                var form = this.up('form').getForm();
                win.hide();
                if(form.isValid()){
                	
                    Ext.Ajax.request({
            			url: 'metadata',
            			method: 'POST',
            			timeout: 100000,
            			params: {
            		        action: 'createPDFReport',
            		        resource: $("#resource").val(),
            		        treeSelect:treeSelect,
            		        fileType:'PDF',
            		        filePath:Ext.getCmp('folder-path').getRawValue(),
            		        fileName:Ext.getCmp('filename').getRawValue()
            		    },
            			success: function(transport) {
            			    var res = transport.responseText;
            			    //alert("output pdf:"+res);
            			    var msg = res.split(":::");
            			    if(msg[0] == "success"){
            			    	Ext.Msg.alert("<%=Messages.getString("Metadata.Message.Title") %>",
            			    			      "<%=Messages.getString("Metadata.Report.Message.ExportSuccess") %>"+msg[1]);
            			    }else{
            			    	Ext.Msg.alert("<%=Messages.getString("Metadata.Message.Title") %>",
            			    			      "<%=Messages.getString("Metadata.Report.Message.ExportFail") %>");
            			    }
            		  	}
            		});
                }
            }
        },{
            text: '<%=Messages.getString("Metadata.Report.button.cancel") %>',
            handler: function() {
            	//Ext.Msg.alert("提示",Ext.getCmp('folder-path').getValue());
                this.up('form').getForm().reset();
            }
        }]
    });

	
	win = Ext.create('Ext.Window', {
           title: '<%=Messages.getString("Metadata.Report.window.exportPDF") %>',
           plain: true,  
           modal:true,
           width: 500,
           items:[formPanel]
	});
	win.show();
}

function downloadPDF(){
	if($("#resource").val()==null){
		Ext.MessageBox.show({  
             	title: '<%=Messages.getString("Metadata.Message.Title") %>',  
             	msg: '<%=Messages.getString("Metadata.Message.SelectResource") %>',  
            	icon: Ext.MessageBox.INFO  
         		});
		return;
	}
	if(treeSelect == "-1"){
		Ext.MessageBox.show({  
             	title: '<%=Messages.getString("Metadata.Message.Title") %>',  
             	msg: '<%=Messages.getString("Metadata.Report.Message.SelectJobORTrans") %>',  
            	icon: Ext.MessageBox.INFO  
         		});
		return;
	}
	myMask.show();
    Ext.Ajax.request({
		url: 'metadata',
		method: 'POST',
		timeout: 100000,
		params: {
	        action: 'downloadPDFReport',
	        resource: $("#resource").val(),
	        treeSelect:treeSelect,
	        fileType:'PDF'
	    },
		success: function(transport) {
			myMask.hide();
			var obj = Ext.decode(transport.responseText);
	        //加入getPath返回的json为{'path':'upload/abc.jpg'}
			if(obj == "fail"){
				
				Ext.Msg.alert("<%=Messages.getString("Metadata.Message.Title") %>",
						      "<%=Messages.getString("Metadata.Report.Message.ExportFail") %>");
		    }else{
		    	window.location.href = obj.path;//这样就可以弹出下载对话框了
		    }
	        
	  	}
	});
}

function checkStep1(){
	if($("#resource").val()==null){
		Ext.MessageBox.show({  
         	title: '<%=Messages.getString("Metadata.Message.Title") %>',  
         	msg: '<%=Messages.getString("Metadata.Message.SelectResource") %>',  
        	icon: Ext.MessageBox.INFO  
     		});
		return;
	}else{
		loadnext(1,2);
		getReportTree($("#resource").val());
	}
}
function checkStep2(){
	if(treeSelect == "-1"){
		Ext.MessageBox.show({  
         	title: '<%=Messages.getString("Metadata.Message.Title") %>',  
         	msg: '<%=Messages.getString("Metadata.Report.Message.SelectJobORTrans") %>',  
        	icon: Ext.MessageBox.INFO  
     		});
		return;
	}else{
		loadnext(2,3);
		createHtmlReport();
	}
}
</script>
</head>
<body onload="parent.iframeResize(this.frames.name);" onresize="parent.iframeResize(this.frames.name);" style="margin: 5px;">
<!-- top to bottom -->
	<div id="wizardwrapper">
	  <!-- first step start -->
	  <div class="1">
	    <ul id="mainNav" class="threeStep">
	      <li class="current"><a title=""><em><%=Messages.getString("Metadata.navigation.jobreport.step1") %></em></a></li>
	      <li><a title="" onclick="checkStep1()"><em><%=Messages.getString("Metadata.navigation.jobreport.step2") %></em></a></li>
	      <li><a title="" onclick="checkStep1();checkStep2();"><em><%=Messages.getString("Metadata.navigation.jobreport.step3") %></em></a></li>
	    </ul>    
	    <div id="wizardcontent"> 
				<SELECT size=2 class="selectType" id="resource">
			<%
				for(MetaDataSourceBean resources:resourceList){
			%>		
					<OPTION  value="<%=resources.getConnection() %>" ><%=resources.getDescription() %></OPTION>
			<%
				}
			%>	
				</SELECT>	    
	    </div>
	    <div class="buttons">
	      <button type="submit" class="previous"  disabled="disabled"> <img src="common/wizard/images/arrow_left.png" alt=""/> <%=Messages.getString("Metadata.navigation.back") %> </button>
	      <button type="submit" class="next" onclick="checkStep1()"> <%=Messages.getString("Metadata.navigation.next") %> <img src="common/wizard/images/arrow_right.png" alt="" /> </button>
	    </div>
	  </div>
	  <!-- first step end -->
	  
	  
	  <!-- second step start -->
	  <div id="wizardpanel" class="2">
	    <ul id="mainNav" class="threeStep">
	      <li class="lastDone"><a onclick="loadnext(2,1)" title=""><em><%=Messages.getString("Metadata.navigation.jobreport.step1") %></em></a></li>
	      <li class="current"><a title=""><em><%=Messages.getString("Metadata.navigation.jobreport.step2") %></em></a></li>
	      <li><a title="" onclick="checkStep2()"><em><%=Messages.getString("Metadata.navigation.jobreport.step3") %></em></a></li>
	    </ul>    
	    <div id="wizardcontent"><div id="reportTree-div"></div></div>
	    <div class="buttons">
	      <button type="submit" class="previous" onclick="loadnext(2,1);"> <img src="common/wizard/images/arrow_left.png" alt="" /> <%=Messages.getString("Metadata.navigation.back") %> </button>
	      <button type="submit" class="next" onclick="checkStep2()"> <%=Messages.getString("Metadata.navigation.next") %> <img src="common/wizard/images/arrow_right.png" alt="" /> </button>
	    </div>
	  </div>
	  <!-- second step end -->
	  
	  <!-- third step start -->	
	  <div id="wizardpanel" class="3">
	    <ul id="mainNav" class="threeStep">
	      <li class="done"><a onclick="loadnext(3,1)" title=""><em><%=Messages.getString("Metadata.navigation.jobreport.step1") %></em></a></li>
	      <li class="lastDone"><a onclick="loadnext(3,2)" title=""><em><%=Messages.getString("Metadata.navigation.jobreport.step2") %></em></a></li>
	      <li class="current"><a title=""><em><%=Messages.getString("Metadata.navigation.jobreport.step3") %></em></a></li>
	    </ul>

	    <div id="wizardcontent"><div id="report-div" style="text-align:center;"></div></div>
	    <div class="buttons">
	      <button type="submit" class="previous" onclick="loadnext(3,2);"> <img src="common/wizard/images/arrow_left.png" alt="" /> <%=Messages.getString("Metadata.navigation.back") %> </button>
	      <button type="submit" class="next" onclick="downloadPDF();" > <%=Messages.getString("Metadata.Report.ExportPdfReport") %>  <img src="common/wizard/images/arrow_right.png" alt=""/> </button>
	    </div>
	  </div>	  
	  <!-- third step end -->	
	 </div>
			

</body>
</html>
