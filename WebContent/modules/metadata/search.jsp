<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.auphi.ktrl.i18n.Messages" %>
<%
request.setCharacterEncoding("UTF-8");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<%@ include file="../../common/extjs.jsp" %>
<script type="text/javascript" src="common/jquery-1.7.1.min.js"></script>
<title><%=Messages.getString("Metadata.search.Title") %></title>
<script type="text/javascript">
Ext.Loader.setConfig({enabled: true});
Ext.Loader.setPath('Ext.ux', 'common/ux');
Ext.require([
    'Ext.data.*',
    'Ext.panel.Panel',
    'Ext.view.View',
    'Ext.layout.container.Fit',
    'Ext.toolbar.Paging',
    'Ext.ux.form.SearchField'
]);
var resourcesComboBox;
var storeSearchType;
Ext.onReady(function(){

	//定义ComboBox模型
	Ext.define('State', {
	    extend: 'Ext.data.Model',
	    fields: [
	        {type: 'string', name: 'id'},
	        {type: 'string', name: 'cname'}
	    ]
	}); 
	
	//加载资源库                                                        
	var storeResources = Ext.create('Ext.data.Store', {                        
	    model: 'State',                                               
	    proxy: {                                                      
	        type: 'ajax',                                             
	        url: 'metadata?action=resources4Search' 
	    },                                                            
	    autoLoad: true,                                               
	    remoteSort:true                                               
	});                                                               
	
    //填充资源库列表
	resourcesComboBox = Ext.create("Ext.form.ComboBox",{
         name:'resourcescmb',                                               
         id : 'resourcescmb',                                               
         fieldLabel:'<%=Messages.getString("Metadata.resource") %>', 
         labelPad:5,
         labelWidth:50,
         displayField:'cname',                                       
         valueField:'id',   
         store:storeResources,  
         //triggerAction:'all', 
         triggerAction: "all",
         queryMode: 'local',                                         
         selectOnFocus:true,                                         
         forceSelection: true,                                       
         allowBlank:false,                                           
         editable: true,                                             
         emptyText:'<%=Messages.getString("Metadata.Search.SelectResource") %>',                                       
         blankText : '<%=Messages.getString("Metadata.Search.SelectResource") %>'//,                                     
		 //renderTo:'resources-div'
	});                                                                   
    
	//加载搜索类型                                                        
	storeSearchType = Ext.create('Ext.data.Store', {                        
	    model: 'State',                                               
	    proxy: {                                                      
	        type: 'ajax',                                             
	        url: 'metadata?action=searchType' 
	    },                                                            
	    autoLoad: true,                                               
	    remoteSort:true                                               
	});                                                               
	
    //填充搜索类型列表
	var searchTypeComboBox = Ext.create("Ext.form.ComboBox",{
         name:'searchTypecmb',                                               
         id : 'searchTypecmb',                                               
         fieldLabel:'<%=Messages.getString("Metadata.Search.searchType") %>', 
         labelPad:5,
         labelWidth:60,
         displayField:'cname',                                       
         valueField:'id',   
         store:storeSearchType,  
         //triggerAction:'all', 
         triggerAction: "all",
         queryMode: 'local',                                         
         selectOnFocus:true,                                         
         forceSelection: true,                                       
         allowBlank:false,                                           
         editable: true,                                             
         emptyText:'<%=Messages.getString("Metadata.Search.SelectSearchType") %>',                                       
         blankText : '<%=Messages.getString("Metadata.Search.SelectSearchType") %>'//,                                     
		 //renderTo:'searchType-div'
	}); 
	
    //定义搜索结果列表
	Ext.define('Post', {
	    extend: 'Ext.data.Model',
	    idProperty: 'main_id',
	    fields: [
			{name: 'mainid', mapping: 'main_id'},
	        {name: 'postId', mapping: 'post_id'},
	        {name: 'title', mapping: 'topic_title'},
	        {name: 'type', mapping: 'topic_type'},
	        {name: 'resources', mapping: 'topic_resources'}
	    ]
	});
    //搜索store对象
    var store = Ext.create('Ext.data.Store', {
        model: 'Post',
        proxy: {
            type: 'ajax',
            url: 'metadata?action=searchAction',
            reader: {
                type: 'json',
                root: 'topics',
                totalProperty: 'totalCount'
            }
        },
        autoLoad: false,                                               
	    remoteSort:true 
    });
    //store.loadPage(1);
	store.on('beforeload',function(){
       store.proxy.extraParams = {
       							resources:Ext.getCmp('resourcescmb').getValue(),
       							searchType:Ext.getCmp('searchTypecmb').getValue(),
       							searchKey:store.getProxy().extraParams.query
       						  };
    });
	//搜索结果
    var resultTpl = Ext.create('Ext.XTemplate',
        '<tpl for=".">',
        '<div class="search-item"><h3><span>{postId}</span>',
            '<a href="metadata?action=searchShowItem&mainid={mainid}&postId={postId}&title={title}&resources={resources}&type={type}" target="_blank">{title}</a></h3>',
        '</div></tpl>'
    );
	//搜索面板
    Ext.create('Ext.panel.Panel', {
        //title: 'Forum Search',
        height: 400,
        width: '100%',
        renderTo: 'searchResult-div',
        id: 'search-results',
        layout: 'fit',
        items: {
        	autoHeight: true,
            autoScroll: true,
            xtype: 'dataview',
            tpl: resultTpl,
            store: store,
            itemSelector: 'div.search-item'
        },
        dockedItems: [{
            dock: 'top',
            xtype: 'toolbar',
            items: [resourcesComboBox,
                    searchTypeComboBox,
                    {
	                width: 200,
	                fieldLabel: '<%=Messages.getString("Metadata.Search") %>',
	                labelWidth: 50,
	                xtype: 'searchfield',
	                store: store,
	                id:'searchKey-txt',
	                name:'searchKey-txt',
	                allowBlank:true,
	                handler : function() {  
                        //store.load();
                        //store.loadPage(1);
                        
                        store.load({ 
                            params: { start: 0, limit: 25 }
                        });


                    	}  
	                }
            ]
        }, {
            dock: 'bottom',
            xtype: 'pagingtoolbar',
            store: store,
            pageSize: 25,
            displayInfo: true,
            displayMsg: '<%=Messages.getString("Metadata.Search.page.total") %>',
            emptyMsg: '<%=Messages.getString("Metadata.Search.page.nodata") %>' 
        }]
    });
	function onRefreshClick(){
       	window.location.href = "metadata?action=search";
    }
});
</script>
</head>
	<body onload="parent.iframeResize(this.frames.name);" onresize="parent.iframeResize(this.frames.name);">
		<div id="searchResult-div"></div>
	</body>
</html>
