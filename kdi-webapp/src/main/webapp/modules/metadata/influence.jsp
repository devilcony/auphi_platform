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
<script type="text/javascript" src="common/js-graph-it/js-graph-it.js"></script>
<script type="text/javascript" src="common/js-graph-it/graph.js"></script>

<link rel="stylesheet" type="text/css" href="common/wizard/wizard-style.css" />
<script type="text/javascript" src="common/wizard/wizard.js"></script>
<title><%=Messages.getString("Metadata.Influence.Title") %></title>
<script type="text/javascript">

Ext.onReady(function(){
	
	Ext.create("Ext.form.RadioGroup",{
	    id: 'radioFields',
	    name: 'radioFields',
	    xtype: 'radiogroup',
	    //fieldLabel: '选择数据库字段',
	    labelPad:5,
        labelWidth:90,
	    columns: 2,
	    vertical :true, 
	    width: 230,
	    //height: 25,
	    //bodyPadding: 10,
	    items: [
	        {boxLabel: '<%=Messages.getString("Metadata.SelectConnDatabase") %>', name: 'rbfieldtype', inputValue: '1', checked: true,id:'radioS',
	        	listeners: {
	        	    change : function(rb, newValue, oldValue) {
	        	    	//alert("1="+newValue);
	        	        if( newValue) {
	        	        	 Ext.getCmp('datasources').setVisible(true);
	                    	 Ext.getCmp('schemas').setVisible(true);
	                    	 Ext.getCmp('tables').setVisible(true);
	                    	 Ext.getCmp('fields').setVisible(true);
	                    	 
	                    	 $('[id$=datasources-div]:first').css('padding-bottom','5px');
	                    	 $('[id$=schemas-div]:first').css('padding-bottom','5px');
	                    	 $('[id$=tables-div]:first').css('padding-bottom','5px');
	                    	 $('[id$=fields-div]:first').css('padding-bottom','5px');	
	                    	 
	                    	 Ext.getCmp('datasources-txt').setVisible(false);
	                    	 Ext.getCmp('schemas-txt').setVisible(false);
	                    	 Ext.getCmp('tables-txt').setVisible(false);
	                    	 Ext.getCmp('fields-txt').setVisible(false);
	        	        } 
	        	    }
	        	}
	    	},
	        {boxLabel: '<%=Messages.getString("Metadata.ManualInput") %>', name: 'rbfieldtype', inputValue: '2',width:70,id:'radioM',
        	listeners: {
        	    change : function(rb, newValue, oldValue) {
        	    	//alert("2="+newValue);
        	    	//alert("2="+oldValue);
        	        if( newValue) {
        	            //alert('Yes2');
        	            Ext.getCmp('datasources').setVisible(false);
                   	 	Ext.getCmp('schemas').setVisible(false);
                   	 	Ext.getCmp('tables').setVisible(false);
                   	 	Ext.getCmp('fields').setVisible(false);
                   	 
                   	 	$('[id$=datasources-div]:first').css('padding-bottom','0px');
                	 	$('[id$=schemas-div]:first').css('padding-bottom','0px');
                	 	$('[id$=tables-div]:first').css('padding-bottom','0px');
                	 	$('[id$=fields-div]:first').css('padding-bottom','0px');	
                	 
	                   	 Ext.getCmp('datasources-txt').setVisible(true);
	                	 Ext.getCmp('schemas-txt').setVisible(true);
	                	 Ext.getCmp('tables-txt').setVisible(true);
	                	 Ext.getCmp('fields-txt').setVisible(true);
        	        } 
        	    }
        	}
	    	}
	    ],
	    renderTo:'fieldtype-div'
	});

	
/*=========连接数据库选择start============*/	
		//定义ComboBox模型
		Ext.define('State', {
		    extend: 'Ext.data.Model',
		    fields: [
		        {type: 'string', name: 'id'},
		        {type: 'string', name: 'cname'}
		    ]
		}); 
		
		//加载数据源                                                        
		var storeDatasources = Ext.create('Ext.data.Store', {                        
		    model: 'State',                                               
		    proxy: {                                                      
		        type: 'ajax',                                             
		        url: 'metadata?action=datasources' 
		    },                                                            
		    autoLoad: false,                                               
		    remoteSort:true                                               
		});                                                               
		//加载模式名                                                    
		var storeSchemas = Ext.create('Ext.data.Store', {                       
		    model: 'State',                                               
		    proxy: {                                                      
		        type: 'ajax',                                             
		        url: 'metadata?action=schemas'
		    },                                                            
		    autoLoad: false,
		    remoteSort:true         
		});                                                               
		//加载表名                                                    
		var storeTables = Ext.create('Ext.data.Store', {                       
		    model: 'State',                                               
		    proxy: {                                                      
		        type: 'ajax',                                             
		        url: 'metadata?action=tables'         
		    },                                                            
		    autoLoad: false,                                              
		    remoteSort:true                                               
		});                                                               
		
		//加载字段名                                                    
		var storeFields = Ext.create('Ext.data.Store', {                       
		    model: 'State',                                               
		    proxy: {                                                      
		        type: 'ajax',                                             
		        url: 'metadata?action=fields'         
		    },                                                            
		    autoLoad: false,                                              
		    remoteSort:true                                               
		}); 
	   
		Ext.create("Ext.form.ComboBox",{
	         name:'datasources',                                               
	         id : 'datasources',                                               
	         fieldLabel:'<%=Messages.getString("Metadata.DataSource") %>', 
	         labelPad:5,
	         labelWidth:50,
	         displayField:'cname',                                       
	         valueField:'id',   
	         store:storeDatasources,  
	         //triggerAction:'all', 
	         triggerAction: "all",
	         queryMode: 'local',                                         
	         selectOnFocus:true,                                         
	         forceSelection: true,                                       
	         allowBlank:false,                                           
	         editable: true,                                             
	         emptyText:'<%=Messages.getString("Metadata.SelectDataSource") %>',                                       
	         blankText : '<%=Messages.getString("Metadata.SelectDataSource") %>',                                     
	         listeners:{                                                 
	             select:function(combo, record,index){                   
	                  try{
	                		if($("#resource").val()==null){
	                			Ext.MessageBox.show({  
	                	             	title: '<%=Messages.getString("Metadata.Message.Title") %>',  
	                	             	msg: '<%=Messages.getString("Metadata.Message.SelectResource") %>',  
	                	            	icon: Ext.MessageBox.INFO  
	                	         		});
	                			return;
	                		}
	                      //userAdd = record.data.name;                  
	                      var parent=Ext.getCmp('schemas');                  
	                      var parent1 = Ext.getCmp("tables");
	                      var parent2 = Ext.getCmp("fields");
	                      parent.clearValue();                           
	                      parent1.clearValue();  
	                      parent2.clearValue();
	                      //alert(this.value);
	                      parent.store.load({params:{resource:this.getRawValue()}});
	                  }                                                  
	                  catch(ex){                                         
	                      Ext.MessageBox.alert("<%=Messages.getString("Metadata.Message.Title.Error") %>",
	                    		  			   "<%=Messages.getString("Metadata.Message.loadfail") %>"); 
	                  }                                                  
	             }} ,
			renderTo:'datasources-div'
		});                                                                   
	    
		Ext.create("Ext.form.ComboBox",{
			name:'schemas',                                                 
	         id : 'schemas',                                                 
	         fieldLabel:'<%=Messages.getString("Metadata.SchemaName") %>', 
	         labelPad:5,
	         labelWidth:50,
	         displayField:'cname',                                       
	         valueField:'id',                                            
	         store:storeSchemas,                                               
	         //triggerAction:'all', 
	         triggerAction: "all",
	         queryMode: 'local',                                         
	         selectOnFocus:true,                                         
	         forceSelection: true,                                       
	         allowBlank:false,                                           
	         editable: true,                                             
	         emptyText:'<%=Messages.getString("Metadata.SelectSchemaName") %>',                                       
	         blankText : '<%=Messages.getString("Metadata.SelectSchemaName") %>',                                     
	         listeners:{                                                 
	             select:function(combo, record,index){                   
	                  try{                                               
	                      //userAdd = record.data.name; 
	                      var datasources=Ext.getCmp('datasources');
	                       var parent = Ext.getCmp("tables");
	                      var parent1 = Ext.getCmp("fields");
	                      //alert(this.getRawValue());
	                      parent.clearValue();    
	                      parent1.clearValue();
	                      parent.store.load({params:{resource:datasources.getRawValue(),schemaName:this.getRawValue()}});
	                  }                                                  
	                  catch(ex){                                         
	                      Ext.MessageBox.alert("<%=Messages.getString("Metadata.Message.Title.Error") %>",
           		  			   				   "<%=Messages.getString("Metadata.Message.loadfail") %>"); 
	                  }                                                  
	             }},
			renderTo:'schemas-div'
		});  		
		
		Ext.create("Ext.form.ComboBox",{
			name:'tables',                                                  
	         id : 'tables',                                                  
	         fieldLabel:'<%=Messages.getString("Metadata.TableName") %>',  
	         labelPad:5,
	         labelWidth:50,
	         displayField:'cname',                                       
	         valueField:'id',                                            
	         store:storeTables,                                               
	         //triggerAction:'all',
	         triggerAction: "all",
	         queryMode: 'local',                                         
	         selectOnFocus:true,                                         
	         forceSelection: true,                                       
	         allowBlank:false,                                           
	         editable: true,                                             
	         emptyText:'<%=Messages.getString("Metadata.SelectTableName") %>',                                       
	         blankText : '<%=Messages.getString("Metadata.SelectTableName") %>',                                     
	         listeners:{                                                 
	             select:function(combo, record,index){                   
	                  try{                                               
	                      //userAdd = record.data.name;
	                      var datasources=Ext.getCmp('datasources');
	                      var schema=Ext.getCmp('schemas'); 
	                      var table = Ext.getCmp("tables");
	                      var parent = Ext.getCmp("fields");                 
	                      parent.clearValue();                           
	                      parent.store.load({params:{resource:datasources.getRawValue(),schemaName:schema.getRawValue(),tableName:table.getRawValue()}});
	                  }                                                  
	                  catch(ex){                                         
	                      Ext.MessageBox.alert("<%=Messages.getString("Metadata.Message.Title.Error") %>",
		  			   				   "<%=Messages.getString("Metadata.Message.loadfail") %>"); 
	                  }                                                  
	             }},
			renderTo:'tables-div'
		}); 
		
		Ext.create("Ext.form.ComboBox",{
			name:'fields',                                                  
	         id : 'fields',                                                  
	         fieldLabel:'<%=Messages.getString("Metadata.ColumnName") %>', 
	         labelPad:5,
	         labelWidth:50,
	         displayField:'cname',                                       
	         valueField:'id',                                            
	         store:storeFields,                                               
	         //triggerAction:'all', 
	         triggerAction: "all",
	         queryMode: 'local',                                         
	         selectOnFocus:true,                                         
	         forceSelection: true,                                       
	         allowBlank:false,                                           
	         editable: true,                                             
	         emptyText:'<%=Messages.getString("Metadata.SelectColumnName") %>',                                       
	         blankText : '<%=Messages.getString("Metadata.SelectColumnName") %>',
			 renderTo:'fields-div'
		}); 
/*=========连接数据库选择 end============*/	

	Ext.create("Ext.form.TextField",{
	    name: 'datasources-txt',
	    id:'datasources-txt',
	    fieldLabel: '<%=Messages.getString("Metadata.DataSource") %>',
	    labelPad:5,
        labelWidth:50,
	    allowBlank: false ,
	    hidden:true,
	    emptyText:'<%=Messages.getString("Metadata.InputDataSource") %>',                                       
        blankText : '<%=Messages.getString("Metadata.InputDataSource") %>', 
	    renderTo:'datasources-txt-div',
	  	//invalidText : '数据源不存在,请注意大小写',
	    //validationEvent : 'blur',
	    listeners : {
        	"blur" : function() {
        		if(Ext.getCmp("datasources-txt").getValue() != ''){
			    	Ext.Ajax.request({
			    		url: 'metadata',
			    		method: 'POST',
			    		params: {
			    	        action: 'checkDatasources',
			    	        connectionname:$("#resource").val(),
			    	        textValue: Ext.getCmp("datasources-txt").getValue()
			    	    },
			    		success: function(transport) {
			    		    var res = transport.responseText;
			    	        if (res == "fail"){
			    	        	Ext.Msg.alert('<%=Messages.getString("Metadata.Message.Title") %>', 
			    	        			'<%=Messages.getString("Metadata.Message.datasourceNoExist") %>');
			    	        }
			    	  	}
			    	});	
        		}
			}
		}
		});
	Ext.create("Ext.form.TextField",{
	    name: 'schemas-txt',
	    id:'schemas-txt',
	    fieldLabel: '<%=Messages.getString("Metadata.SchemaName") %>',
	    labelPad:5,
        labelWidth:50,
	    allowBlank: false ,
	    hidden:true,
	    emptyText:'<%=Messages.getString("Metadata.InputSchemaName") %>',                                       
        blankText : '<%=Messages.getString("Metadata.InputSchemaName") %>',
	    renderTo:'schemas-txt-div',
	  //invalidText : '模式名不存在,请注意大小写,没有模式请输入”无“',
	    listeners : {
        	"blur" : function() {
        		if(Ext.getCmp("schemas-txt").getValue() != ''){
			    	Ext.Ajax.request({
			    		url: 'metadata',
			    		method: 'POST',
			    		params: {
			    	        action: 'checkSchemas',
			    	        connectionname:Ext.getCmp("datasources-txt").getValue(),
			    	        textValue: Ext.getCmp("schemas-txt").getValue()
			    	    },
			    		success: function(transport) {
			    		    var res = transport.responseText;
			    		    //alert(res);
			    		    if (res == "fail"){
			    	        	Ext.Msg.alert('<%=Messages.getString("Metadata.Message.Title") %>', 
			    	        			'<%=Messages.getString("Metadata.Message.SchemaNoExist") %>');
			    	        }else if(res.indexOf("error:") != -1){
			    	        	Ext.Msg.alert('<%=Messages.getString("Metadata.Message.Title.Error") %>', res);
			    	        }
			    	  	}
			    	});	
	      	}
        }
	   }
	});
	Ext.create("Ext.form.TextField",{
	    name: 'tables-txt',
	    id:'tables-txt',
	    fieldLabel: '<%=Messages.getString("Metadata.TableName") %>',
	    labelPad:5,
        labelWidth:50,
	    allowBlank: false ,
	    hidden:true,
	    emptyText:'<%=Messages.getString("Metadata.InputTableName") %>',  
	    blankText : '<%=Messages.getString("Metadata.InputTableName") %>',
	    renderTo:'tables-txt-div',
	    //invalidText : '表名不存在,请注意大小写',
	    listeners : {
        	"blur" : function() {
        		if(Ext.getCmp("tables-txt").getValue() != ''){
			    	Ext.Ajax.request({
			    		url: 'metadata',
			    		method: 'POST',
			    		params: {
			    	        action: 'checkTables',
			    	        connectionname:Ext.getCmp("datasources-txt").getValue(),
			    	        schemaName:Ext.getCmp("schemas-txt").getValue(),
			    	        textValue: Ext.getCmp("tables-txt").getValue()
			    	    },
			    		success: function(transport) {
			    		    var res = transport.responseText;
			    		    if (res == "fail"){
			    	        	Ext.Msg.alert('<%=Messages.getString("Metadata.Message.Title") %>', 
			    	        			'<%=Messages.getString("Metadata.Message.tablenameNoExist") %>');
			    	        }
			    	  	}
			    	});	
        		}
	      	}	
	    }
	});
	Ext.create("Ext.form.TextField",{
	    name: 'fields-txt',
	    id:'fields-txt',
	    fieldLabel: '<%=Messages.getString("Metadata.ColumnName") %>',
	    labelPad:5,
        labelWidth:50,
	    allowBlank: false ,
	    hidden:true,
	    emptyText:'<%=Messages.getString("Metadata.InputColumnName") %>',
	    blankText : '<%=Messages.getString("Metadata.InputColumnName") %>',
	    renderTo:'fields-txt-div',
	    //invalidText : '字段名不存在,请注意大小写',
	    listeners : {
        	"blur" : function() {
        		if(Ext.getCmp("fields-txt").getValue() != ''){
			    	Ext.Ajax.request({
			    		url: 'metadata',
			    		method: 'POST',
			    		params: {
			    	        action: 'checkFields',
			    	        connectionname:Ext.getCmp("datasources-txt").getValue(),
			    	        schemaName:Ext.getCmp("schemas-txt").getValue(),
			    	        tableName:Ext.getCmp("tables-txt").getValue(),
			    	        textValue: Ext.getCmp("fields-txt").getValue()
			    	    },
			    		success: function(transport) {
			    		    var res = transport.responseText;
			    		    if (res == "fail"){
			    	        	Ext.Msg.alert('<%=Messages.getString("Metadata.Message.Title") %>', 
			    	        			'<%=Messages.getString("Metadata.Message.columnNoExist") %>');
			    	        }
			    	  	}
			    	});	
	      		}	 
        	}
	    }
	});
	/*=========手工输入 end============*/	
	Ext.create("Ext.form.field.Checkbox",{
	    name: 'syncheckbox',
	    id:'syncheckbox',
	    fieldLabel: '<%=Messages.getString("Metadata.descent.synCheckbox") %>',
	    labelAlign:'left',
	    labelPad:5,
        labelWidth:75,
        checked :false,
	    renderTo:'syncheckbox-div'
	});	
	

	function onRefreshClick(){
       	window.location.href = "metadata?action=influence";
    }
});
//点击资源库获取数据源
function getDatasource(optValue)
{
	if($("#resource").val()==null){
		Ext.MessageBox.show({  
             	title: '<%=Messages.getString("Metadata.Message.Title") %>',  
             	msg: '<%=Messages.getString("Metadata.Message.SelectResource") %>',  
            	icon: Ext.MessageBox.INFO  
         		});
		return;
	}
	//alert(optValue);
	var parent = Ext.getCmp("datasources");      
	//alert(parent.store.getCount());
    parent.clearValue();
    
    parent.store.load({params:{connectionname:optValue}});
    
	 var schema=Ext.getCmp('schemas'); 
	 var table = Ext.getCmp("tables");
	 var field = Ext.getCmp("fields");
	 schema.clearValue();
	 table.clearValue();
	 field.clearValue();
	//alert(parent.store.getCount());
}


function getReportFlow()
{
	//alert("getReportFlow");
	//$("#reportFlow-div").empty();
	//alert($("#resource").val());
	if($("#resource").val()==null){
		Ext.MessageBox.show({  
             	title: '<%=Messages.getString("Metadata.Message.Title") %>',  
             	msg: '<%=Messages.getString("Metadata.Message.SelectResource") %>',  
            	icon: Ext.MessageBox.INFO  
         		});
		return;
	}
	//alert($("#resource").val());
	var paramsStr = "";
	var radiochoose = Ext.getCmp('radioS');
	//alert(radiochoose.getValue());
	var datasources;
	var schemas;
	var tables;
	var fields;
	if(radiochoose.getValue())
	{
		//alert("aaaaa:"+Ext.getCmp('datasources').getValue());
		if(Ext.getCmp('datasources').getValue()==null){
			Ext.MessageBox.show({  
                 	title: '<%=Messages.getString("Metadata.Message.Title") %>',  
                 	msg: '<%=Messages.getString("Metadata.SelectDataSource") %>',  
                	icon: Ext.MessageBox.INFO  
             		});
			return;
		}
		if(Ext.getCmp('schemas').getValue()==null){
			Ext.MessageBox.show({  
                 	title: '<%=Messages.getString("Metadata.Message.Title") %>',  
                 	msg: '<%=Messages.getString("Metadata.SelectSchemaName") %>',  
                	icon: Ext.MessageBox.INFO  
             		});
			return;
		}	
		if(Ext.getCmp('tables').getValue()==null){
			Ext.MessageBox.show({  
                 	title: '<%=Messages.getString("Metadata.Message.Title") %>',  
                 	msg: '<%=Messages.getString("Metadata.SelectTableName") %>',  
                	icon: Ext.MessageBox.INFO  
             		});
			return;
		}
		if(Ext.getCmp('fields').getValue()==null){
			Ext.MessageBox.show({  
                 	title: '<%=Messages.getString("Metadata.Message.Title") %>',  
                 	msg: '<%=Messages.getString("Metadata.SelectColumnName") %>',  
                	icon: Ext.MessageBox.INFO  
             		});
			return;
		}
		datasources=Ext.getCmp('datasources').getValue();
		schemas=Ext.getCmp('schemas').getValue();
		tables=Ext.getCmp('tables').getValue();
		fields=Ext.getCmp('fields').getValue();
        //alert(Ext.getCmp('datasources').getValue());
        //alert(Ext.getCmp('schemas').getValue());
        //alert(Ext.getCmp('tables').getValue());
        //alert(Ext.getCmp('fields').getValue());
	}
	else
	{
		if(Ext.getCmp('datasources-txt').getValue()==""){
			Ext.MessageBox.show({  
                 	title: '<%=Messages.getString("Metadata.Message.Title") %>',  
                 	msg: '<%=Messages.getString("Metadata.InputDataSource") %>',  
                	icon: Ext.MessageBox.INFO  
             		});
			return;
		}
		if(Ext.getCmp('schemas-txt').getValue()==""){
			Ext.MessageBox.show({  
                 	title: '<%=Messages.getString("Metadata.Message.Title") %>',  
                 	msg: '<%=Messages.getString("Metadata.InputSchemaName") %>',  
                	icon: Ext.MessageBox.INFO  
             		});
			return;
		}	
		if(Ext.getCmp('tables-txt').getValue()==""){
			Ext.MessageBox.show({  
                 	title: '<%=Messages.getString("Metadata.Message.Title") %>',  
                 	msg: '<%=Messages.getString("Metadata.InputTableName") %>',  
                	icon: Ext.MessageBox.INFO  
             		});
			return;
		}
		if(Ext.getCmp('fields-txt').getValue()==""){
			Ext.MessageBox.show({  
                 	title: '<%=Messages.getString("Metadata.Message.Title") %>',  
                 	msg: '<%=Messages.getString("Metadata.InputColumnName") %>',  
                	icon: Ext.MessageBox.INFO  
             		});
			return;
		}
		datasources=Ext.getCmp('datasources-txt').getValue();
		schemas=Ext.getCmp('schemas-txt').getValue();
		tables=Ext.getCmp('tables-txt').getValue();
		fields=Ext.getCmp('fields-txt').getValue();
	}

	Ext.Ajax.request({
		url: 'metadata',
		method: 'POST',
		params: {
	        action: 'influenceGraphFlow',
	        syn:Ext.getCmp('syncheckbox').getValue(),
	        resource:$("#resource").val(),
	        datasources: datasources,
	        schemas:schemas,
	        tables:tables,
	        fields:fields
	    },
		success: function(transport) {
		    var res = transport.responseText;
		    //alert(res);

		    $("#graphFlow-div").html("");
	    	$("#graphFlow-div").empty();
		    $("#graphFlow-div").html(res);
		    initPageObjects();
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
	}
}
function checkStep2(){
	var radiochoose = Ext.getCmp('radioS');
	if(radiochoose.getValue())
	{
		if(Ext.getCmp('datasources').getValue()==null){
			Ext.MessageBox.show({  
                 	title: '<%=Messages.getString("Metadata.Message.Title") %>',  
                 	msg: '<%=Messages.getString("Metadata.SelectDataSource") %>',  
                	icon: Ext.MessageBox.INFO  
             		});
			return;
		}
		if(Ext.getCmp('schemas').getValue()==null){
			Ext.MessageBox.show({  
                 	title: '<%=Messages.getString("Metadata.Message.Title") %>',  
                 	msg: '<%=Messages.getString("Metadata.SelectSchemaName") %>',  
                	icon: Ext.MessageBox.INFO  
             		});
			return;
		}	
		if(Ext.getCmp('tables').getValue()==null){
			Ext.MessageBox.show({  
                 	title: '<%=Messages.getString("Metadata.Message.Title") %>',  
                 	msg: '<%=Messages.getString("Metadata.SelectTableName") %>',  
                	icon: Ext.MessageBox.INFO  
             		});
			return;
		}
		if(Ext.getCmp('fields').getValue()==null){
			Ext.MessageBox.show({  
                 	title: '<%=Messages.getString("Metadata.Message.Title") %>',  
                 	msg: '<%=Messages.getString("Metadata.SelectColumnName") %>',  
                	icon: Ext.MessageBox.INFO  
             		});
			return;
		}
	}
	else
	{
		if(Ext.getCmp('datasources-txt').getValue()==""){
			Ext.MessageBox.show({  
                 	title: '<%=Messages.getString("Metadata.Message.Title") %>',  
                 	msg: '<%=Messages.getString("Metadata.InputDataSource") %>',  
                	icon: Ext.MessageBox.INFO  
             		});
			return;
		}
		if(Ext.getCmp('schemas-txt').getValue()==""){
			Ext.MessageBox.show({  
                 	title: '<%=Messages.getString("Metadata.Message.Title") %>',  
                 	msg: '<%=Messages.getString("Metadata.InputSchemaName") %>',  
                	icon: Ext.MessageBox.INFO  
             		});
			return;
		}	
		if(Ext.getCmp('tables-txt').getValue()==""){
			Ext.MessageBox.show({  
                 	title: '<%=Messages.getString("Metadata.Message.Title") %>',  
                 	msg: '<%=Messages.getString("Metadata.InputTableName") %>',  
                	icon: Ext.MessageBox.INFO  
             		});
			return;
		}
		if(Ext.getCmp('fields-txt').getValue()==""){
			Ext.MessageBox.show({  
                 	title: '<%=Messages.getString("Metadata.Message.Title") %>',  
                 	msg: '<%=Messages.getString("Metadata.InputColumnName") %>',  
                	icon: Ext.MessageBox.INFO  
             		});
			return;
		}
	}
	callCreateGraph();
}
function callCreateGraph(){
	loadnext(2,3);
	getReportFlow();
}
function clearGraph(){
	$("#graphFlow-div").html("");
	$("#graphFlow-div").empty();	
}
</script>
</head>
<body onload="parent.iframeResize(this.frames.name);" onresize="parent.iframeResize(this.frames.name);" style="margin: 5px;">
	<div id="wizardwrapper">
	  <!-- first step start -->
	  <div class="1">
	    <ul id="mainNav" class="threeStep">
	      <li class="current"><a title=""><em><%=Messages.getString("Metadata.navigation.influence.step1") %></em></a></li>
	      <li><a title="" onclick="checkStep1()"><em><%=Messages.getString("Metadata.navigation.influence.step2") %></em></a></li>
	      <li><a title="" onclick="checkStep1();checkStep2();"><em><%=Messages.getString("Metadata.navigation.influence.step3") %></em></a></li>
	    </ul>    
	    <div id="wizardcontent"> 
				<SELECT size=2 class="selectType" id="resource" onchange="getDatasource(this.value)">
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
	      <li class="lastDone"><a onclick="loadnext(2,1)" title=""><em><%=Messages.getString("Metadata.navigation.influence.step1") %></em></a></li>
	      <li class="current"><a title=""><em><%=Messages.getString("Metadata.navigation.influence.step2") %></em></a></li>
	      <li><a title="" onclick="checkStep2()"><em><%=Messages.getString("Metadata.navigation.influence.step3") %></em></a></li>
	    </ul>    
	    <div id="wizardcontent">
       		 <div style="float:left;">
	       		<div id="fieldtype-div"></div>
	       		 
	       		<div id="datasources-div" style="padding-bottom: 5px"></div>
	       		<div id="schemas-div" style="padding-bottom: 5px"></div>
	       		<div id="tables-div" style="padding-bottom: 5px"></div>
	       		<div id="fields-div" style="padding-bottom: 5px"></div>
	       		
	       		<div id="datasources-txt-div" style="padding-bottom: 5px"></div>
	       		<div id="schemas-txt-div" style="padding-bottom: 5px"></div>
	       		<div id="tables-txt-div" style="padding-bottom: 5px"></div>
	       		<div id="fields-txt-div" style="padding-bottom: 5px"></div>
	       		
	       		<div id="syncheckbox-div" style="padding-bottom: 5px"></div>
       		</div>	    
	    </div>
	    <div class="buttons">
	      <button type="submit" class="previous" onclick="loadnext(2,1);"> <img src="common/wizard/images/arrow_left.png" alt="" /> <%=Messages.getString("Metadata.navigation.back") %> </button>
	      <button type="submit" class="next" onclick="checkStep2()"> <%=Messages.getString("Metadata.navigation.next") %> <img src="common/wizard/images/arrow_right.png" alt="" /> </button>
	    </div>
	  </div>
	  <!-- second step end -->	  

	  <!-- third step start -->	
	  <div id="wizardpanel" class="3">
	    <ul id="mainNav" class="threeStep">
	      <li class="done"><a onclick="loadnext(3,1)" title=""><em><%=Messages.getString("Metadata.navigation.influence.step1") %></em></a></li>
	      <li class="lastDone"><a onclick="loadnext(3,2)" title=""><em><%=Messages.getString("Metadata.navigation.influence.step2") %></em></a></li>
	      <li class="current"><a title=""><em><%=Messages.getString("Metadata.navigation.influence.step3") %></em></a></li>
	    </ul>

	    <div id="wizardcontent"><div id="graphFlow-div"></div></div>
	    <div class="buttons">
	      <button type="submit" class="previous" onclick="loadnext(3,2);"> <img src="common/wizard/images/arrow_left.png" alt="" /> <%=Messages.getString("Metadata.navigation.back") %> </button>
	      <button type="submit" class="next" onclick="clearGraph();" > <%=Messages.getString("Metadata.button.clear") %>  <img src="common/wizard/images/arrow_right.png" alt=""/> </button>
	    </div>
	  </div>	  
	  <!-- third step end -->	
	 </div>

		

</body>
</html>
