<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.net.*" %>
<%@ page import="java.util.*" %>
<%
	String detailXML = request.getAttribute("detailXML")==null?"":request.getAttribute("detailXML").toString();
	String jobName = request.getAttribute("jobName")==null?"":request.getAttribute("jobName").toString();
	String actionRef = request.getAttribute("actionRef")==null?"":request.getAttribute("actionRef").toString();
	//int id_batch = request.getAttribute("id_batch")==null?-1:Integer.parseInt(request.getAttribute("id_batch").toString());
	String id_logchannel = request.getAttribute("id_logchannel")==null?"":request.getAttribute("id_logchannel").toString();
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

var swfETL = new SWF("ETL");
var url = '';
var old_objs = '';
function init(){
	var chartdiv = document.getElementById("chartdivETL");
	chartdiv.style.height = (parent.document.body.offsetHeight - 135) + 'px';
	swfETL.init(chartdiv,"/ChartBase/bin/ChartBase.swf","100%","100%","/ChartBase/bin/ChartStudio.xml","callbackETL");
}
			
function callbackETL(){
	//alert("ok");
	function l(){
		swfETL.swfCtrl.setConfigXml(xml);
		setTimeout(setState,2000);
	}
	setTimeout(l,50);
}
			
function setState(){
	var xmlHttpReq;
	if(window.XMLHttpRequest){ //Mozilla 
		xmlHttpReq=new XMLHttpRequest();
	}else if(window.ActiveXObject){
		xmlHttpReq=new ActiveXObject("MSXML2.XMLHTTP.3.0");
	}
	xmlHttpReq.open("GET", "monitor?action=getActiveDetails&jobName=<%=URLEncoder.encode(jobName, "UTF-8") %>&id_logchannel=<%=id_logchannel %>", false);
	xmlHttpReq.send();
	if(xmlHttpReq.responseText!=''){
					
		var as3 = 
			"import mx.core.FlexGlobals;\n"+
			"import mx.controls.Alert; \n"+
			"import flash.utils.describeType;\n"+
			"import bi.util.Util;\n"+
			"import bi.layout.ContainerBase;\n"+
			"import spark.components.Label;\n"+
			"import bi.layout.ContainerFactory;\n"+
			"var STOP:int=0;\n"+
			"var START:int=1;\n"+
			"var END:int=2;\n"+
			"var ERROR:int=3;\n"+
			"var state:Array = new Array();\n"+
			"var o:Object;\n";
					
		var resJSON = eval('(' + xmlHttpReq.responseText + ')') ;
		var objs = resJSON.objs;
		if(objs != ''){
			if(objs.length){
				old_objs = resJSON.objs;
				for(var i=0;i<objs.length;i++){
					as3 = as3 + 
					"o = new Object();\n"+
					"o.name = \""+escape(objs[i].name)+"\";\n"+
					"o.state = "+objs[i].state+";\n"+
					"state.push(o);\n";
				}
			}
		} else if(old_objs != ''){
			if(old_objs.length){
				for(var i=0;i<old_objs.length;i++){
					as3 = as3 + 
						"o = new Object();\n"+
						"o.name = \""+escape(old_objs[i].name)+"\";\n"+
						"o.state = 2;\n"+
						"state.push(o);\n";
				}
				clearInterval(tt);
			}
		}else {
			clearInterval(tt);
		}
		
		as3 = as3 + "var etl:ContainerBase = Util.document.getElementById(\"etl\");\n"+
			"etl.subClassInstance.setState(state);\n";
					
		swfETL.swfCtrl.evalScript(as3);
	}
}
			
var tt = window.setInterval(setState,3000);
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