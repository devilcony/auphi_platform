<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.net.*" %>
<%@ page import="java.util.*" %>
<%
	String detailXML = request.getAttribute("detailXML")==null?"":request.getAttribute("detailXML").toString();
	String jobName = request.getAttribute("jobName")==null?"":request.getAttribute("jobName").toString();
	String actionRef = request.getAttribute("actionRef")==null?"":request.getAttribute("actionRef").toString();
	String fileType = request.getAttribute("fileType")==null?"":request.getAttribute("fileType").toString();
	String transStepsLog = request.getAttribute("transStepsLog")==null?"":request.getAttribute("transStepsLog").toString();
	String jobEntriesLog = request.getAttribute("jobEntriesLog")==null?"":request.getAttribute("jobEntriesLog").toString();
	System.out.println(transStepsLog);
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<%@ include file="../../common/extjs.jsp" %>
<script type="text/javascript" src="/ChartBase/bin/swf.js"></script>
<script type="text/javascript" src="/ChartBase/bin/json2.js"></script>
<script type="text/javascript">
var xml = '<%=detailXML %>';
var fileType = "<%=fileType %>";

var swfETL = new SWF("ETL");
var url = '';
var old_objs = '';
function init(){
	var chartdiv = document.getElementById("chartdivETL");
	chartdiv.style.height = (parent.document.body.offsetHeight - 135) + 'px';
	swfETL.init(chartdiv,"/ChartBase/bin/ChartBase.swf","100%","100%","/ChartBase/bin/ChartStudio.xml","callbackETL");
}
			
function callbackETL(){
	function l(){
		swfETL.swfCtrl.setConfigXml(xml);
		if(fileType == "ktr"){
			var l = JSON.stringify(<%=transStepsLog %>);
			l = addfix(l);
			swfETL.swfCtrl.addProperty("log",l);
			setTimeout(setLog,2000);
		}else if(fileType == "kjb"){
			var l = JSON.stringify(<%=jobEntriesLog %>);
			l = addfix(l);
			swfETL.swfCtrl.addProperty("log",l);
			setTimeout(setLog,2000);
		}
	}
	setTimeout(l,50);
}

//对json字符串中数值类型的值加引号
var c=0;
function addfix(s){
	var r = s.indexOf(",\"",2);
	if(r == -1) return s;
	var t = s.substring(r-1,r);
	var sl,sc,sr;
	
	if(t!="\""){
		var l = s.substring(0,r).lastIndexOf("\":")+2;
		
		sl = s.substring(0,l);
		sc = s.substring(l,r);
		sr = s.substring(r);
		
		//s = sl+"\""+sc+"\""+sr;
		//console.log(sl);
		//console.log(sc);
		//console.log(sr);
		
		return sl+"\""+sc+"\"" + addfix(sr);
	}else{
		sl = s.substring(0,r);
		sr = s.substring(r);
		//console.log(sl);
		//console.log(sr);
		return sl + addfix(sr);
	}
}

function setLog(){
	
	var as3 = 
		"import mx.core.FlexGlobals;\n"+
		"import mx.controls.Alert; \n"+
		"import flash.utils.describeType;\n"+
		"import bi.util.Util;\n"+
		"import bi.layout.ContainerBase;\n"+
		"import spark.components.Label;\n"+
		"import bi.layout.ContainerFactory;\n"+
		
		"var etl:ContainerBase = Util.document.getElementById(\"etl\");\n"+
		"etl.subClassInstance.setLog(Util.properties[\"log\"]);\n";
		
	swfETL.swfCtrl.evalScript(as3);
}
</script>
</head>
<title>Show running details</title>
</head>
<body onload="init();parent.iframeResize(this.frames.name);" onresize="parent.iframeResize(this.frames.name);">
	<br />
	<center><font size="18" style="font-weight: bold;"><%=actionRef %></font></center>
	<br />
	<div id="chartdivETL" style="width:100%;border:1px solid #336699;"></div>
</body>
</html>