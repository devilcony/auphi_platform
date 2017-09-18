
Ext.define('destColumuType', {
			extend : 'Ext.data.Model',
			fields : [{
						name : 'name',
						type : 'string'
					}]
		});

Ext.define('schmaNameModel', {
			extend : 'Ext.data.Model',
			fields : [{
						name : 'name',
						type : 'string'
					}]
		});
Ext.define('targetSchmaNameModel', {
			extend : 'Ext.data.Model',
			fields : [{
						name : 'name',
						type : 'string'
					}]
		});

//定义模型
Ext.define('fastConfigData', {
	extend : 'Ext.data.Model',
	// requires: ["DestColumuType"],
	fields : [{
				name : 'idConfig',
				type : 'int'
			},// 1
			{
			    name :'isFirstLineFieldName',
			    type : 'int'				
			},
			{
			   name:'idSourceType',
			   type:'int'
			 },
			{
				name : 'idSourceDatabase',
				type : 'int'
			},// 2 COMMENT '1 databsae, 2 ftp, 3 hadoop',
			{
				name : 'idSourceFTP',
				type : 'int'
			},// 3
			{
				name : 'idSourceHadoop',
				type : 'int'
			},// 4
			{
			   name:'sourceSchenaName',
			   type:'string'
			},
			{
				name : 'sourceTableName',  
				type : 'string'
			},// 5
			{
				name : 'sourceCondition',
				type : 'string'
			},// 6
			{
				name : 'sourceFilePath',
				type : 'string'
			},// 7
			{
				name : 'sourceFileName',
				type : 'stirng'
			},// 8
			{
				name : 'sourceSeperator',
				type : 'stirng'
			},// 9
			{
				name : 'idDestType',
				type : 'int'
			},// 10
			{
				name : 'idDestDatabase',
				type : 'int'
			},// 11
			{
				name : 'idDestFTP',
				type : 'int'
			},// 12
			{
				name : 'idDestHadoop',
				type : 'int'
			},// 13
			{  
			    name:'destSchenaName',
			    type:'string'
			},
			{
				name : 'destTableName',
				type : 'string'
			},// 14
			{
				name : 'destFilePath',
				type : 'string'
			},// 15
			{
				name : 'destFileName',
				type : 'string'
			},// 16
			{
				name : 'loadType',
				type : 'int'
			}//  '1 全量  2 增量',
]
//reader : {
//						type : 'json'
//					    //root: 'fastConfigData',
//					    //idProperty : 'fastConfigData'
//				},
//	validations: [ 
//		{type: 'format', field: 'idSourceDatabase',matcher: /^([^\a-\z\A-\Z0-9\u4E00-\u9FA5]$/, message: '你还没选择!'},
//		{}
//	],
//	proxy: {
//		//model: 'fastConfigData',  
//        type: 'rest',
//        url : 'FastConfigScheduleServlet?action=findField'
//    }
		// hasMany: {model: 'destColumuType', name: 'destColumuTypes'}
	});

	//初始化模型
	var _fastConfigData= Ext.create('fastConfigData',
		{
		idConfig:0,
		isFirstLineFieldName:0,
		idSourceType:0,
		idSourceDatabase:0,
		idSourceFTP:0,
		idSourceHadoop:0,
		sourceSchenaName:'',
		sourceTableName:'',
		sourceCondition:'',
		sourceFilePath:'',
		sourceFileName:'',
		sourceSeperator:'',
		idDestType:0,
		idDestDatabase:0,
		idDestFTP:0,
		idDestHadoop:0,
		destSchenaName:'',
		destTableName:'',
		destFilePath:'',
		destFileName:'',
		loadType:0
		});
//_fastConfigData.save()
//定义字段映射模型
var fieldMappingData=Ext.define('fieldMappingData', {
	extend : 'Ext.data.Model',
	// requires: ["DestColumuType"],
	fields : [{
				name : 'sourceColumnName',
				type : 'stirng'
			},// 1
			
			{
				name : 'reference',
				type : 'string'
			},// 2
			{
				name : 'startIndex',
				type : 'int'
			},// 3
			{
				name : 'endIndex',
				type : 'int'
			},// 4
			{
				name : 'sourceColumnType',
				type : 'string'
			},// 5
			{
			
				name:'destColumuName',
				type:'string'
			},
			{
				name : 'destColumnType',
				type : 'string'
			},// 6
			{
				name : 'destLength',
				type : 'int'
			},// 7
			{
			    name:'destScale',
			    type:'int'
			},
			{
				name : 'isPrimary',
				type : 'bool'
			},// 8
			{
				name : 'isNullable',
				type : 'bool'
			}// 9
	],
	idProperty : 'fieldMappingDataID'// 极为重要的配置。关系到表格修改数据的获取 
		// hasMany: {model: 'destColumuType', name: 'destColumuTypeViews'}
	});

//初始化字段映射模型
/*var _fieldMappingData= Ext.create('fieldMappingData',
	{
	  sourceColumnName:'',
	  reference:'',
	  startIndex:0,
	  endIndex:0,
	  sourceColumnType:'',
	  destColumnType:'',
	  destLength:0,
	  isPrimary:'',
	  isNullable:''
	});*/
//
// { header: '源字段名', dataIndex: '1' },
// { header: '参考值', dataIndex: '2', flex: 1 },
// { header: '开始位', dataIndex: '3' },
// { header: '结束位', dataIndex: '4' },
// { header: '源数据类型', dataIndex: '5', flex: 1 },
// { header: '目标数据类型', dataIndex: '6' },
// { header: '长度', dataIndex: '7' },
// { header: '主键标识', dataIndex: '8', flex: 1 },
// { header: '是否为空', dataIndex: '9' }

Ext.regModel('dataBaseName', {
			fields : [{
						type : 'string',
						name : 'name'
					}]
		});
		
Ext.regModel('remoteMarketDataBaseName',{
      fields: [
      {
        type:'string',
        name:'name'
      }]
})
//定义全局变量
var myStore;
var check = false;
var targetStore;
var targetCheck = false;
var remoteMarketStore;
var remoteMarketCheck=false;
var jboName;
var schemaMamesORTables=[];
var schemaNames=[];
var checkIsUpatde=false;
   var fieldMappingDataStore = Ext.create('Ext.data.Store', {
    model: 'fieldMappingData',
    proxy: {
        type: 'ajax',
        url : 'FastConfigScheduleServlet?action=findField',
        method: "post",
        reader: {
            type: 'json'
            //root: 'users'
        }
    },
//    sorters: [{ 
//        //排序字段。 
//        property: 'hits',  
//        //排序类型，默认为 ASC  
//        direction: 'DESC'
//    }] ,
    autoLoad:false
});
//源数据库表名选择框
var nonTargetDatabaseName = new Ext.form.ComboBox({
			fieldLabel : '数据库表名', // UI标签名称
			name : 'nonTargetDatabaseName', // 作为form提交时传送的参数名
			allowBlank : false, // 是否允许为空
			// queryMode: 'local',
			mode : 'local', // 数据模式, local为本地模式,remote
			// 如果不设置,就显示不停的加载中...
			// readOnly : true, // 是否只读,如果设置了只读将不显示列表
			editable : true,
			triggerAction : 'all', // 显示所有下列数.必须指定为'all'
			anchor : '99%',
			id : 'nonTargetDatabaseName',
			emptyText : '请选择...', // 没有默认值时,显示的字符串
			// store : nonTargetDatabaseNameList,
			store : new Ext.data.SimpleStore({ // 填充的数据
				fields : ['text', 'value'],
				data : [['table1', 'table1'], ['table2', 'table2'],
						['table3', 'table3']]
			}),
			// value : 't2', // 设置当前选中的值, 也可用作初始化时的默认值, 默认为空
			valueField : 'name', // 传送的值
			displayField : 'name'// UI列表显示的文本
				,listeners:{
					beforequery : function(e){
						var combo = e.combo;
						if(!e.forceAll){
							var value = e.query;
						    combo.store.filterBy(function(record,id){
							    var text = record.get(combo.displayField);
						    	return (text.indexOf(value)!=-1);
							});
						    combo.expand();
						    return false;
						}
					}
				}
		});
//源数据库表名输入框
var nonTargetDatabaseNameInput = new Ext.form.TextField({
	fieldLabel : '数据库表名', // UI标签名称
	name : 'nonTargetDatabaseNameInput', // 作为form提交时传送的参数名
	allowBlank : false, // 是否允许为
	// 如果不设置,就显示不停的加载中...
	anchor : '99%',
	id : 'nonTargetDatabaseNameInput'
		// emptyText : '请选择输入...' // 没有默认值时,显示的字符串
	});
nonTargetDatabaseNameInput.hide();


var marketdatabaseTable =new Ext.data.Store({
    proxy: {
        type: 'ajax',
        url : 'FastConfigScheduleServlet?action=findSchemaTable',
        reader: {
            type: 'json'
            //root: 'schemaNames'
        }
    },
    fields : ['schmaName','tableName'],
    autoLoad: false,
    listeners: {
           beforeload: function (store, options) {          	  
           	   var combo=Ext.getCmp('nonTargetMarketSchema');
           	   if(combo.getValue()!=null)
           	   {
           	   	  var params = { schemaName: combo.getRawValue()};
                  Ext.apply(store.proxy.extraParams, params);
           	   }else
           	   {
           	   	 return;
           	   }

           }
       }
});

var targetMarketdatabaseTable =new Ext.data.Store({
    proxy: {
        type: 'ajax',
        url : 'FastConfigScheduleServlet?action=findSchemaTable',
        reader: {
            type: 'json'
            //root: 'schemaNames'
        }
    },
    fields : ['schmaName','tableName'],
    autoLoad: false,
    listeners: {
           beforeload: function (store, options) {          	  
           	   var combo=Ext.getCmp('targetMarketSchema');
           	   if(combo.getValue()!=null)
           	   {
           	   	  var params = { schemaName: combo.getRawValue()};
                  Ext.apply(store.proxy.extraParams, params);
           	   }else
           	   {
           	   	 return;
           	   }

           }
       }
});
//源数据集市表名选择框
var remoteMarketDatabaseName = new Ext.form.ComboBox({
			fieldLabel : '数据库表名', // UI标签名称
			name : 'remoteMarketDatabaseName', // 作为form提交时传送的参数名
			allowBlank : false, // 是否允许为空
			// queryMode: 'local',
			mode : 'local', // 数据模式, local为本地模式,remote
			// 如果不设置,就显示不停的加载中...
			// readOnly : true, // 是否只读,如果设置了只读将不显示列表
			//disabled : true,
			triggerAction : 'all', // 显示所有下列数.必须指定为'all'
			anchor : '99%',
			editable : false,
			autoLoad: false,
			id : 'remoteMarketDatabaseName',
			emptyText : '请选择...', // 没有默认值时,显示的字符串
			// store : nonTargetDatabaseNameList,
			store : marketdatabaseTable,
			valueField : 'schemaName', // 传送的值
			displayField : 'tableName'// UI列表显示的文本
		});

//源数据集市表名输入框
var remoteMarketDatabaseNameInput = new Ext.form.TextField({
	fieldLabel : '数据库表名', // UI标签名称
	name : 'remoteMarketDatabaseNameInput', // 作为form提交时传送的参数名
	//allowBlank : true, // 是否允许为
	hidden:true,
	// 如果不设置,就显示不停的加载中...
	anchor : '99%',
	id : 'remoteMarketDatabaseNameInput'
		// emptyText : '请选择输入...' // 没有默认值时,显示的字符串
	});
//remoteMarketDatabaseNameInput.hide();


//源数据集市表名选择框
var targetRemoteMarketDatabaseName = new Ext.form.ComboBox({
			fieldLabel : '数据库表名', // UI标签名称
			name : 'targetRemoteMarketDatabaseName', // 作为form提交时传送的参数名
			allowBlank : false, // 是否允许为空
			// queryMode: 'local',
			mode : 'local', // 数据模式, local为本地模式,remote
			// 如果不设置,就显示不停的加载中...
			// readOnly : true, // 是否只读,如果设置了只读将不显示列表
			//disabled : true,
			triggerAction : 'all', // 显示所有下列数.必须指定为'all'
			anchor : '99%',
			id : 'targetRemoteMarketDatabaseName',
			emptyText : '请选择...', // 没有默认值时,显示的字符串
			// store : nonTargetDatabaseNameList,
			store : targetMarketdatabaseTable,
			// value : 't2', // 设置当前选中的值, 也可用作初始化时的默认值, 默认为空
			valueField : 'schemaName', // 传送的值
			displayField : 'tableName'// UI列表显示的文本

		});
				
//源数据集市表名输入框
var targetRemoteMarketDatabaseNameInput = new Ext.form.TextField({
	fieldLabel : '数据库表名', // UI标签名称
	name : 'targetRemoteMarketDatabaseNameInput', // 作为form提交时传送的参数名
	allowBlank : false, // 是否允许为
	// 如果不设置,就显示不停的加载中...
	anchor : '99%',
	id : 'targetRemoteMarketDatabaseNameInput'
		// emptyText : '请选择输入...' // 没有默认值时,显示的字符串
	});
targetRemoteMarketDatabaseNameInput.hide();

//目标数据库表名选择框
var targetDatabaseName = new Ext.form.ComboBox({
			fieldLabel : '数据库表名', // UI标签名称
			name : 'targetDatabaseName', // 作为form提交时传送的参数名
			allowBlank : false, // 是否允许为空
			// queryMode: 'local',
			mode : 'local', // 数据模式, local为本地模式,remote
			// 如果不设置,就显示不停的加载中...
			// readOnly : true, // 是否只读,如果设置了只读将不显示列表
			editable : true,
			triggerAction : 'all', // 显示所有下列数.必须指定为'all'
			anchor : '99%',
			id : 'targetDatabaseName',
			emptyText : '请选择...', // 没有默认值时,显示的字符串
			// store : nonTargetDatabaseNameList,
			store : new Ext.data.SimpleStore({ // 填充的数据
				fields : ['text', 'value'],
				data : [['table1', 'table1'], ['table2', 'table2'],
						['table3', 'table3']]
			}),
			// value : 't2', // 设置当前选中的值, 也可用作初始化时的默认值, 默认为空
			valueField : 'name', // 传送的值
			displayField : 'name'// UI列表显示的文本
				,listeners:{
					beforequery : function(e){
						var combo = e.combo;
						if(!e.forceAll){
							var value = e.query;
						    combo.store.filterBy(function(record,id){
							    var text = record.get(combo.displayField);
						    	return (text.indexOf(value)!=-1);
							});
						    combo.expand();
						    return false;
						}
					}
				}
		});
//目标数据库表名输入框
var targetDatabaseNameInput = new Ext.form.TextField({
	fieldLabel : '数据库表名', // UI标签名称
	name : 'targetDatabaseNameInput', // 作为form提交时传送的参数名
	allowBlank : false, // 是否允许为
	hidden:true,
	// 如果不设置,就显示不停的加载中...
	anchor : '99%',
	id : 'targetDatabaseNameInput'
		// emptyText : '请选择输入...' // 没有默认值时,显示的字符串
	});
targetDatabaseNameInput.hide();


//源远程数据
var planNameSorte = Ext.create('Ext.data.Store', {
    model: 'schmaNameModel',
    proxy: {
        type: 'ajax',
        url : 'FastConfigScheduleServlet?action=findRemoteMarketTable',
        reader: {
            type: 'json'
            //root: 'schemaNames'
        }
    },
    fields : ['name'],
    autoLoad: false
});

//目标远程数据
var targetPlanNameSorte = Ext.create('Ext.data.Store', {
    model: 'targetSchmaNameModel',
    proxy: {
        type: 'ajax',
        url : 'FastConfigScheduleServlet?action=findRemoteMarketTable',
        reader: {
            type: 'json'
            //root: 'schemaNames'
        }
    },
    fields : ['name'],
    autoLoad: false
});
//远程数据集市面板
var remoteMarketPanel= new Ext.form.Panel({
  id:'remoteMarketPanel',
  name:'remoteMarketPanel',
  border: false,
  bodyStyle:'padding:15px;background-color: #CED9E7;',
  frame:false,
  items:[
   {
      xtype : 'combo',
		fieldLabel : '模式名称', // UI标签名称
		id : 'nonTargetMarketSchema',
		name : 'nonTargetMarketSchema', // 作为form提交时传送的参数名
		//blankText : '不能为空',//验证错误信息
		allowBlank : false, // 是否允许为空
		editable: false,
		mode : 'local', // 数据模式, local为本地模式,
		// 如果不设置,就显示不停的加载中...'remote远程
		// readOnly : true, // 是否只读,如果设置了只读将不显示列表
		triggerAction : 'all', // 显示所有下列数.必须指定为'all'
		anchor : '99%',
		emptyText : '请选择...', // 没有默认值时,显示的字符串
		loadingText:'加载中请稍后',
		store:planNameSorte,
			valueField : 'name', // 传送的值
			displayField : 'name',// UI列表显示的文本
			listeners : {
			select : function(combo, record, index) { // 
			//marketdatabaseTable.load({params:{schemaName: combo.getRawValue()}});
			marketdatabaseTable.load()
			 /* var arr =[];
			  //alert(combo.getRawValue());
			  for(var i=0;i<planNameSorte.getCount();i=i+1)
			  {
			  	 if(combo.getRawValue()==planNameSorte.getAt(i).get('schemaName'))
			  	 {
			  	   arr.push([planNameSorte.getAt(i).get('tableName'),planNameSorte.getAt(i).get('schemaName')]);
		 	
			  	 }
	           // console.log(TEST.getAt(i).get('schemaName'));
			   // console.log(TEST.getAt(i).get('tableName'));
			  }		  
			  marketdatabaseTable.loadData(arr);*/
			 //console.log(JSON.stringify(arr));
			}
		}
     },
     
  remoteMarketDatabaseName, remoteMarketDatabaseNameInput,{
		xtype : 'checkbox',
		style : 'margin-left:110px',
		boxLabel : '分月',
		name : 'remoteMarketQueryMonthly',
		id : 'remoteMarketQueryMonthly',
		listeners : {
			change : function(obj, ischecked) {
				if (ischecked) {
					remoteMarketDatabaseName.hide();
					remoteMarketDatabaseNameInput.show();
					//Ext.getCmp('remoteMarketQueryMonthly').setLoading('加载中');
				} else {
					remoteMarketDatabaseName.show();
					remoteMarketDatabaseNameInput.hide();
				}
			}
		},
		anchor : '50%'
	}, {
		xtype : 'textfield',
		fieldLabel : '查询条件',
		name : 'remoteMarketQueryCondition',
		id : 'remoteMarketQueryCondition',
		anchor : '99%'
	},{
		xtype : 'textfield',
		fieldLabel : '分隔符',
		name : 'remoteMarketSeparator',
		id : 'remoteMarketSeparator',
		//emptyText : '|', // 没有默认值时,显示的字符串
		value:'|',
		allowBlank : false,
		anchor : '99%'
					}]
});
remoteMarketPanel.hide();

//目标远程数据集市面板
var targetRemoteMarketPanel= new Ext.form.Panel({
  id:'targetRemoteMarketPanel',
  name:'targetRemoteMarketPanel',
  border: false,
  bodyStyle:'padding:15px;background-color: #CED9E7;',
  frame:false,
  items:[
       {
      xtype : 'combo',
		fieldLabel : '模式名称', // UI标签名称
		id : 'targetMarketSchema',
		name : 'targetMarketSchema', // 作为form提交时传送的参数名
		//blankText : '不能为空',//验证错误信息
		allowBlank : false, // 是否允许为空
		editable: false,
		mode : 'local', // 数据模式, local为本地模式,
		// 如果不设置,就显示不停的加载中...'remote远程
		// readOnly : true, // 是否只读,如果设置了只读将不显示列表
		triggerAction : 'all', // 显示所有下列数.必须指定为'all'
		anchor : '99%',
		emptyText : '请选择...', // 没有默认值时,显示的字符串
		loadingText:'加载中请稍后',
		store:targetPlanNameSorte,
		valueField : 'name', // 传送的值
		displayField : 'name',// UI列表显示的文本
		listeners : {
		select : function(combo, record, index) {
			     targetMarketdatabaseTable.load();
			     // targetMarketdatabaseTable.load({params:{schemaName: combo.getRawValue()}});
		      }
		      }
     },
    targetRemoteMarketDatabaseName, targetRemoteMarketDatabaseNameInput,{
		xtype : 'checkbox',
		style : 'margin-left:110px',
		boxLabel : '分月',
		name : 'targetRemoteMarketQueryMonthly',
		id : 'targetRemoteMarketQueryMonthly',
		listeners : {
			change : function(obj, ischecked) {
				if (ischecked) {
					targetRemoteMarketDatabaseName.hide();
					targetRemoteMarketDatabaseNameInput.show();
				} else {
					targetRemoteMarketDatabaseName.show();
					targetRemoteMarketDatabaseNameInput.hide();
				}
			}
		},
		anchor : '50%'
	},  {
		xtype : 'radiogroup',
		fieldLabel : '加载方式',
		columns : 2,
		items : [{
					boxLabel : "全量",
					name : "loadingMode",
					id:'remoteMarketFulldose',
					checked : true,
					inputValue : "全量"
				}, {
					boxLabel : "增量",
					name : "loadingMode",
					id:'remoteMarketIncrement',
					inputValue : "增量"
				}],
		anchor : '99%'
	}
		
//			{
//		xtype : 'textfield',
//		fieldLabel : '查询条件',
//		name : 'targetRemoteMarketQueryCondition',
//		id : 'targetRemoteMarketQueryCondition',
//		allowBlank : false,
//		anchor : '99%'
//	}
//	
	
	]
});
targetRemoteMarketPanel.hide();

//源数据库面板
var databasePanel = new Ext.form.Panel({
	id : 'databasePanel',
	name : 'databasePanel',
	border : false,
	bodyStyle : 'padding:15px;background-color: #CED9E7;',
	frame : false,
	items : [{
		xtype : 'combo',
		fieldLabel : '数据源名称', // UI标签名称
		id : 'nonTargetCombo',
		name : 'identity', // 作为form提交时传送的参数名
		allowBlank : false, // 是否允许为空
		mode : 'local', // 数据模式, local为本地模式,
		// 如果不设置,就显示不停的加载中...'remote远程
		// readOnly : true, // 是否只读,如果设置了只读将不显示列表
		triggerAction : 'all', // 显示所有下列数.必须指定为'all'
		anchor : '99%',
		emptyText : '请选择...', // 没有默认值时,显示的字符串
		store : new Ext.data.JsonStore({
					autoDestroy : true,
					id : 'nonTargetDataStore',
					// autoSync:false,
					// autoLoad:true,
					storeId : 'myStore',
					proxy : {
						type : 'ajax',
						url : 'FastConfigScheduleServlet?action=findDatabase',
						reader : {
							type : 'json'
							// root: 'images'
							// idProperty : 'name'
						}
					},
					fields : ['idDatase', 'name']
				}),
		// value : 't2', // 设置当前选中的值, 也可用作初始化时的默认值, 默认为空
		valueField : 'idDatase', // 传送的值
		displayField : 'name',// UI列表显示的文本
		listeners : {
			select : function(combo, record, index) { // 
				var id = Ext.getCmp('nonTargetDatabaseName');
				var count = 1;
				if (check == false) {
					myStore = Ext.create('Ext.data.Store', {
						model : 'dataBaseName',
						proxy : {
							type : 'ajax',
							url : 'FastConfigScheduleServlet?action=findTable&idDatabase='
									+ combo.getValue(),
							reader : {
								type : 'json'
								// root : 'users'
							}
						},
						autoLoad : true
					});
					check = true;
					count++;
				} else {
					myStore.proxy = new Ext.data.proxy.Ajax({
						url : 'FastConfigScheduleServlet?action=findTable&idDatabase='
								+ combo.getValue(),
						model : 'dataBaseName',
						reader : 'json'
					});
					count = count + 2;
				}
				id.clearValue();
				if (count == 3) {
					myStore.load();
				} else {
					nonTargetDatabaseName.disabled = false;
					id.store = myStore;

				}

			}
		}

	}, nonTargetDatabaseName, nonTargetDatabaseNameInput, {
		xtype : 'checkbox',
		style : 'margin-left:110px',
		boxLabel : '分月',
		name : 'queryMonthly',
		id : 'queryMonthly',
		listeners : {
			change : function(obj, ischecked) {
				if (ischecked) {
					nonTargetDatabaseName.hide();
					nonTargetDatabaseNameInput.show();
				} else {
					nonTargetDatabaseName.show();
					nonTargetDatabaseNameInput.hide();
				}
			}
		},
		anchor : '50%'
	}, {
		xtype : 'textfield',
		fieldLabel : '查询条件',
		name : 'queryCondition',
		id : 'queryCondition',
		anchor : '99%'
	}]

});
databasePanel.hide();
// Ext.getCmp('nonTargetDatabaseName').
// 数据源datasoure
// function getDataSoure()
// {
// var judge=false;
// Ext.Ajax.request({
// url: 'FastConfigScheduleServlet',
// method: 'post',
// async : false,//同步请求数据
// success: function(res){
// //Ext.Msg.alert("提示消息",res.responseText);
// //var jsonResult = Ext.JSON.decode(res.responseText);
// console.log(res.responseText);
// // alert(jsonResult.databaseName.name);//我返回的数据只有一个，这里也不能用manager[0].name
// //Ext.Msg.alert("获得数据源成功","成功");
// judge=true;
// return res.responseText;
// },
// failure: function(){
// Ext.Msg.alert("获得数据源失败","失败");
// judge=false;
// },
// headers: {
// 'my-header': 'foo'
// },
// params: { action: 'findDatabase',user_id:'1'}
// });
// return judge;
//
// }

// ftp面板
var ftpPanel = new Ext.form.Panel({
			id : 'ftpPanel',
			name : 'ftpPanel',
			border : false,
			bodyStyle : 'padding:15px;background-color: #CED9E7;',
			frame : false,
			items : [{
				xtype : 'combo',
				fieldLabel : 'FTP名称', // UI标签名称
				name : 'ftpName', // 作为form提交时传送的参数名
				id:'ftpName',
				allowBlank : false, // 是否允许为空
				mode : 'local', // 数据模式, local为本地模式,
				// 如果不设置,就显示不停的加载中...
				// readOnly : true, // 是否只读,如果设置了只读将不显示列表
				triggerAction : 'all', // 显示所有下列数.必须指定为'all'
				anchor : '99%',
				emptyText : '请选择...', // 没有默认值时,显示的字符串
				store : new Ext.data.JsonStore({
									autoDestroy : true,
									id : 'FTPDataStore',
									// autoSync:false,
									autoLoad:true,
									storeId : 'FTPStore',
									proxy : {
										type : 'ajax',
										url : 'FastConfigScheduleServlet?action=findFtpTable',
										reader : {
											type : 'json'
											// root: 'images'
											// idProperty : 'name'
										}
									},
									fields : ['ftpID', 'name']
								}),
						// value : 't2', // 设置当前选中的值, 也可用作初始化时的默认值, 默认为空
						valueField : 'ftpID', // 传送的值
						displayField : 'name'// UI列表显示的文本
			},{
				xtype : 'checkbox',
				style : 'margin-left:110px',
				boxLabel : '是否第一行为字段名',
			    id:'isFirstLineFieldName',
			    anchor : '99%'
			},
			{
				xtype : 'textfield',
				fieldLabel : 'FTP路径',
				name : 'fptPath',
				id : 'fptPath',
				allowBlank : false,
				anchor : '99%'
			}, {
				xtype : 'textfield',
				fieldLabel : '文件名称',
				name : 'fileName',
				id : 'fileName',
				allowBlank : false,
				anchor : '99%'
			}, {
				xtype : 'textfield',
				fieldLabel : '字段分隔符',
				name : 'ftpSeparator',
				id : 'ftpSeparator',
				allowBlank : false,
				anchor : '99%'
			}]
		});
ftpPanel.hide();

// 数据目标DatabasePanel,
var targetDatabasePanel = new Ext.form.Panel({
	id : 'targetDatabasePanel',
	name : 'targetDatabasePanel',
	border : false,
	bodyStyle : 'padding:15px;background-color: #CED9E7;',
	frame : false,
	// layout : 'column',//
	items : [{
		xtype : 'combo',
		fieldLabel : '数据源名称', // UI标签名称
		name : 'targetCombo', // 作为form提交时传送的参数名
		id:   'targetCombo',
		allowBlank : false, // 是否允许为空
		mode : 'local', // 数据模式, local为本地模式, 如果不设置,就显示不停的加载中...
		// readOnly : true, // 是否只读,如果设置了只读将不显示列表
		triggerAction : 'all', // 显示所有下列数.必须指定为'all'
		anchor : '99%',
		emptyText : '请选择...', // 没有默认值时,显示的字符串
		store : new Ext.data.JsonStore({
					autoDestroy : true,
					id : 'targetDataStore',
					// autoSync:false,
					// autoLoad:true,
					storeId : 'targetStore',
					proxy : {
						type : 'ajax',
						url : 'FastConfigScheduleServlet?action=findDatabase',
						reader : {
							type : 'json'
							// root: 'images'
							// idProperty : 'name'
						}
					},
					fields : ['idDatase', 'name']
				}),
		// value : 't2', // 设置当前选中的值, 也可用作初始化时的默认值, 默认为空
		valueField : 'idDatase', // 传送的值
		displayField : 'name',// UI列表显示的文本
		listeners : {
			select : function(combo, record, index) { // 
				var id = Ext.getCmp('targetDatabaseName');
				var count = 1;
				if (targetCheck == false) {
					targetStore = Ext.create('Ext.data.Store', {
						model : 'dataBaseName',
						proxy : {
							type : 'ajax',
							url : 'FastConfigScheduleServlet?action=findTable&idDatabase='
									+ combo.getValue(),
							reader : {
								type : 'json'
								// root : 'users'
							}
						},
						autoLoad : true
					});
					targetCheck = true;
					count++;
				} else {
					targetStore.proxy = new Ext.data.proxy.Ajax({
						url : 'FastConfigScheduleServlet?action=findTable&idDatabase='
								+ combo.getValue(),
						model : 'dataBaseName',
						reader : 'json'
					});
					count = count + 2;
				}
				id.clearValue();
				if (count == 3) {
					targetStore.load();
				} else {
					targetDatabaseName.disabled = false;
					id.store = targetStore;

				}

			}
		}

	}, targetDatabaseName, targetDatabaseNameInput, {
		xtype : 'checkbox',
		style : 'margin-left:110px',
		boxLabel : '分月',
		name : 'targetQueryMonthly',
		id : 'targetQueryMonthly',
		listeners : {
			change : function(obj, ischecked) {
				if (ischecked) {
					targetDatabaseName.hide();
					targetDatabaseNameInput.show();
				} else {
					targetDatabaseName.show();
					targetDatabaseNameInput.hide();
				}
			}
		},
		anchor : '50%'
	}, {
		xtype : 'radiogroup',
		fieldLabel : '加载方式',
		columns : 2,
		items : [{
					boxLabel : "全量",
					name : "loadingMode",
					id:'fulldose',
					checked : true,
					inputValue : "全量"
				}, {
					boxLabel : "增量",
					name : "loadingMode",
					id:'increment',
					inputValue : "增量"
				}],
		anchor : '99%'
	}]

});
targetDatabasePanel.hide();

// Hadoop面板
var hadoopPanel = new Ext.form.Panel({
			id : 'hadoopPanel',
			name : 'hadoopPanel',
			border : false,
			bodyStyle : 'padding:15px;background-color: #CED9E7;',
			frame : false,
			items : [{
						xtype : 'textfield',
						fieldLabel : '文件路径',
						name : 'hadoopPath',
						id : 'hadoopPath',
						allowBlank : false,
						anchor : '99%'
					}, {
						xtype : 'textfield',
						fieldLabel : '文件名称',
						name : 'hadoopFileName',
						id : 'hadoopFileName',
						allowBlank : false,
						anchor : '99%'
					}]
		});
hadoopPanel.hide();

// 数据库备注面板

var databaseRemarksPanel = new Ext.form.Panel({
	border : false,
	bodyStyle : 'padding:15px;background-color: #CED9E7;',
	html : '<ul>'
			+ '<li>（备注：）选择分月表名可以使用以下变量模式</li>'
			+ '<li>${YYYYMM}&nbsp;&nbsp;${YYYYMMDD}&nbsp;&nbsp;${YYYYMMDDHH}</li>'
			+ '<li>${YYYYMMDDHHmm}' + '</ul>'
});
databaseRemarksPanel.hide();






// 字段映射
var fieldMappingPanel = Ext.create('Ext.grid.Panel', {
	        id:'fieldMappingPanel',
			//title : '作业test',
	        store:fieldMappingDataStore,
			frame: true, 
			selType: 'cellmodel', 
            //loadMask: true, 
           plugins : [Ext.create('Ext.grid.plugin.CellEditing', {
									    clicksToEdit : 1
								})],
			columns : [{
						header : '源字段名',
						id:'sourceColumnName',
						sortable: true, 
						width:160,
						dataIndex : 'sourceColumnName',
				        field: { 
                        allowBlank: false
                        }
					}, {
						header : '参考值',
						id:'reference',
						sortable: true, 
						dataIndex : 'reference',
						field: { 
                        allowBlank: false
                        }
					}, {
						header : '开始位',
						id:'startIndex',
						sortable: false, 
						width:60,
						dataIndex : 'startIndex',
					    field: { 
                        allowBlank: false
                        }
					}, {
						header : '结束位',
						id:'endIndex',
						sortable: true, 
						width:60,
						dataIndex : 'endIndex',
						field: { 
                        allowBlank: false
                        }
					}, {
						header : '目标字段名',
						id:'destColumuName',
						sortable: true, 
						width:160,
						dataIndex : 'destColumuName',
						field: { 
                        allowBlank: false
                        }
					}, {
						header : '源数据类型',
						id:'sourceColumnType',
						sortable: true, 
						dataIndex : 'sourceColumnType',
						field: { 
                        allowBlank: false
                        }
					}, {
						header : '目标数据类型',
						id:'destColumnType',
						sortable: true, 
						dataIndex : 'destColumnType',
						field: { 
		                xtype: 'combobox', 
		                id:'destColumnTypeList', 
		                typeAhead: true, 
		                triggerAction: 'all', 
		                queryMode: 'local',  
		                selectOnTab: true, 
						anchor : '99%',
						emptyText : '请选择...', // 没有默认值时,显示的字符串
						store : Ext.data.JsonStore({
									autoDestroy : true,
									id : 'columnTypeJsonStore',
									// autoSync:false,
									autoLoad:true,
									storeId : 'columnTypeStore',
									proxy : {
										type : 'ajax',
										url : 'FastConfigScheduleServlet?action=findDatabaseType',
										reader : {
											type : 'json'
											// root: 'images'
											// idProperty : 'name'
										}
									},
									fields : ['name']
								}),
		                lazyRender: true, 
		                displayField:'name', 
		                valueField:'name', 
		                listClass: 'x-combo-list-small', 
		                listeners:{     
		                    select : function(combo, record,index){ 
		                        isEdit = true; 
		                   } 
		                } 
                  
                       }
                    // enderer:rendererData 

					}, {
						header : '长度',
						id:'destLength',
						sortable: true, 
						width:60,
						dataIndex : 'destLength',
						field: { 
		                xtype: 'numberfield', 
		                allowBlank: false, 
		                minValue: 0, 
		                maxValue: 100000 
                       } 

					}, {
					   header:'刻度',
					   id:'destScale',
					   sortable:true,
					   width:60,
					   dataIndex : 'destScale',
						field: { 
		                xtype: 'numberfield', 
		                allowBlank: false, 
		                minValue: 0, 
		                maxValue: 100000 
                       } 
					},{
						//xtype: 'checkcolumn', 
						header : '主键标识',
						id:'isPrimary',
						//sortable: true, 
						width:70,
						dataIndex : 'isPrimary',
						field: { 
		                xtype: 'checkbox', 
		                allowBlank: false
                       },
						renderer: function (v) {
						 	return '<input disabled="true" type="checkbox"'+(v==true?" checked":"")+'/>';
						 	}//根据值返回checkbox是否勾选
					}, {
						//xtype: 'checkcolumn', 
						header : '是否为空',
						id:'isNullable',
						width:70,
						//sortable: true, 
						dataIndex : 'isNullable',
					    field: { 
		                xtype: 'checkbox', 
		                allowBlank: false
                       },
						renderer: function (v) { 
						  // console.log("dd"+v);
						   return '<input disabled="true" type="checkbox"'+(v==true?" checked":"")+'/>';
						}//根据值返回checkbox是否勾选
					},{
						header:'操作',
						id:'delete',
						width:60,
                        //iconCls:"myDelete", 
						renderer: function (v) { 
						   return '<div  class="myDelete" onclick="deleteField()")></div>';
						}
						
					}],
			height : 500,
			width : '100%',
			renderTo : Ext.getBody()
		});
//调动方式
var typeComboData = [ [1, '小时' ],[2, '天' ],[ 3, '月' ]];
var runModeData=[[1,'本地运行'],[2,'集群运行']];

function monthComboData()
{
  var monthComboData=[];
  for(var i=1;i<=32;i=i+1)
  {
  	if(i<32)
  	{
  		var data=[i,i];
  	    monthComboData.push(data);
  	}else
  	{
  		var data=['L','L'];
  	    monthComboData.push(data);  	
  	}
  }
   // console.log(Ext.JSON.decode(monthComboData))
  return monthComboData;
}


var dispatchingModePanel= new Ext.form.Panel({
     id:'dispatchingModePanel',
     name : 'dispatchingModePanel',
	 border : false,
	 bodyStyle : 'padding:15px;background-color: #CED9E7;',
	 frame : false,
	 items:[{
						xtype : 'label',
						text : '作业(test1)数据目标',
						id:"dispatchingModeJboName",
						cls : 'x-form-check-group-label'
					}, {
						xtype : 'component',
						html : '<hr/>'

					}, {
						xtype : 'combo',
						fieldLabel : '运行方式',
						name : 'runMode',
						// checked : true,
						blankText : '运行方式不能为空',//验证错误信息
						id : 'runMode',
						anchor : '99%',
					    emptyText : '请选择...', // 没有默认值时,显示的字符串
						store : new Ext.data.SimpleStore({
						fields : [ "runModeValue", "runModeText" ],
						data : runModeData
					    }),
					    valueField : "runModeValue",
						displayField : "runModeText",
						mode : 'local',
						//forceSelection : true,
						value : "",
						hiddenName : 'runModeValue',
						allowBlank : false,
						editable : false,
						triggerAction : 'all',
						listeners : {
							select : function(combo, record, index) {							
	                              if(combo.getValue()==1)
	                              {
	                                Ext.getCmp('runCluster').hide();
	                                Ext.getCmp('runCluster').select('');
	                              }
	                              if(combo.getValue()==2)
	                              {
	                            	  Ext.getCmp('runCluster').show();
	                              }
								}
						}
					},{
						xtype : 'combo',
						fieldLabel : '选择集群',
						name : 'runCluster',
						// checked : true,
						blankText : '集群不能为空',//验证错误信息
						id : 'runCluster',
						anchor : '99%',
					    emptyText : '请选择...', // 没有默认值时,显示的字符串
					    store: Ext.data.JsonStore({
							autoDestroy : true,
							id : 'haJsonStore',
							autoLoad:true,
							storeId : 'haStore',
							proxy : {
								type : 'ajax',
								url : 'FastConfigScheduleServlet?action=findHA',
								reader : {
									type : 'json'
								}
							},
							fields : ['id_cluster','name']
						}),
					    valueField : "id_cluster",
						displayField : "name",
						mode : 'local',
						//forceSelection : true,
						value : "",
						hiddenName : 'runClusterValue',
						allowBlank : false,
						editable : false,
						triggerAction : 'all'
					},{
						xtype : 'timefield',
						fieldLabel : '开始时间',
						name : 'beginTime',
						id : 'beginTime',
						anchor : '99%',
						increment:15,
						blankText : '开始时间不能为空',//验证错误信息
						allowBlank:false,						
						editable : false,
						emptyText : '请选择...', // 没有默认值时,显示的字符串
			            format:'H:i:s'
						
					},  {
						xtype : 'combo',
						fieldLabel : '调度类型',
						name : 'scheduletype',
						// checked : true,
						id : 'scheduletype',
						anchor : '99%',
						blankText : '开始时间不能为空',//验证错误信息
						store : new Ext.data.SimpleStore({
						fields : [ "scheduletype", "scheduletypeName" ],
						data : typeComboData
					}),
					    emptyText : '请选择...', // 没有默认值时,显示的字符串
						valueField : "scheduletype",
						displayField : "scheduletypeName",
						mode : 'local',
						//forceSelection : true,
						value : "",
						hiddenName : 'scheduletype',
						allowBlank : false,
						editable : false,
						triggerAction : 'all',
					//	fieldLabel : '调度类型',
				//		autoWidth : true,
						listeners : {
							select : function(combo, record, index) {							
                              if(combo.getValue()==1)
                              {
                                Ext.getCmp('cycleStateHtml').setText("小时");
                                Ext.getCmp('cycleModeMonth').hide();
                                Ext.getCmp('cycleMode').show();
                                Ext.getCmp('cycleStateTextMonth').allowBlank=true;
                                Ext.getCmp('cycleStateText').allowBlank=false;
                              }
                              if(combo.getValue()==2)
                              {
                                Ext.getCmp('cycleStateHtml').setText("天");
                                Ext.getCmp('cycleModeMonth').hide();
                                Ext.getCmp('cycleMode').show();
                                Ext.getCmp('cycleStateTextMonth').allowBlank=true;
                                Ext.getCmp('cycleStateText').allowBlank=false;
                              }
                              if(combo.getValue()==3)
                              {
                                Ext.getCmp('cycleStateHtml').setText("月");
                                Ext.getCmp('cycleMode').hide();
                                Ext.getCmp('cycleModeMonth').show();
                                Ext.getCmp('cycleStateTextMonth').allowBlank=false;
                                Ext.getCmp('cycleStateText').allowBlank=true;
                              }
							}
						}
					},{
					   xtype:'radiogroup',
					   fieldLabel : '周期模式',
					   id:'cycleMode',
					   items: [
					    {xtype:'textfield', id:'cycleStateText',
					    allowBlank:false,
					    blankText : '周期模式不能为空',//验证错误信息
					    name:'cycleMode',anchor:'100%'},
		                {xtype:'label',id:'cycleStateHtml',text:'小时'}
		            ]
					}, {
					   xtype:'radiogroup',
					   fieldLabel : '周期模式',
					   id:'cycleModeMonth',
					   hidden:true,
					   items: [
					   {xtype:'label',text:'每月的第'},
		                {xtype:'combo', id:'cycleStateTextMonth',anchor:'100%',
					    emptyText : '请选择...', // 没有默认值时,显示的字符串
					    name:'cycleModeMonth',				    
						store : new Ext.data.SimpleStore({
						fields : [ "runDateValue", "runDateText" ],
		                data:monthComboData()
		                }),
						valueField : "runDateValue",
						displayField : "runDateText",
						mode : 'local',
						value : "",
						blankText : '周期模式月份不能为空',//验证错误信息
						//allowBlank : false,
						editable : false,
						triggerAction : 'all'
		                },
		                {xtype:'label',id:'cycleStateMonthHtml',text:'日，L为最后一天'}
		            ]
					},{
						xtype:'datefield',
						fieldLabel : '开始日期',
						name : 'runDate',
					    blankText : '开始日期不能为空',//验证错误信息
					    allowBlank : false,
						id : 'runDate',
			            format:'Y-m-d', 
			            value:Ext.util.Format.date(Ext.Date.add(new Date(),Ext.Date.MONTH,-1),"Y-m-d"),
						anchor : '99%'
					},{
					   xtype:'radiogroup',
					   fieldLabel : '截止日期',
					   id:'endTime',
					   items: [
		                {boxLabel: '永不', id:'timeDefinition', name: 'endTimeType', inputValue: 1,checked: true},
		                {boxLabel: '自定义',name: 'endTimeType',id:'theCustom', inputValue: 2},
		                {xtype:'datefield',format:'Y-m-d', 
			            value:Ext.util.Format.date(Ext.Date.add(new Date(),Ext.Date.MONTH,-1),"Y-m-d"), id:'inputEndTime', name:'endTime'
			            }
		            ]
					},{
					   	xtype:'label',
						id:'testStateText',
						hidden:true,
						text:''}
						
					]
     
	
});
// ftp备注面板
var ftpRemarksPanel = new Ext.form.Panel({
	border : false,
	bodyStyle : 'padding:15px;background-color: #CED9E7;',
	html : '<ul>'
			+ '<li>（备注：）FTP文件名称可以使用以下变量模式</li>'
			+ '<li>${YYYYMM}&nbsp;&nbsp;${YYYYMMDD}&nbsp;&nbsp;${YYYYMMDDHH}</li>'
			+ '<li>${YYYYMMDDHHmm}' + '</ul>'
});
ftpRemarksPanel.hide();

var remoteMarketDatabaseRemarksPanel = new Ext.form.Panel({
	border : false,
	bodyStyle : 'padding:15px;background-color: #CED9E7;',
	html : '<ul>'
			+ '<li>（备注：）数据集市选择分月表名可以使用以下变量模式</li>'
			+ '<li>${YYYYMM}&nbsp;&nbsp;${YYYYMMDD}&nbsp;&nbsp;${YYYYMMDDHH}</li>'
			+ '<li>${YYYYMMDDHHmm}' + '</ul>'
});
remoteMarketDatabaseRemarksPanel.hide();

var targetDatabaseRemarksPanel = new Ext.form.Panel({
	border : false,
	bodyStyle : 'padding:15px;background-color: #CED9E7;',
	html : '<ul>'
			+ '<li>（备注：）选择分月表名可以使用以下变量模式</li>'
			+ '<li>${YYYYMM}&nbsp;&nbsp;${YYYYMMDD}&nbsp;&nbsp;${YYYYMMDDHH}</li>'
			+ '<li>${YYYYMMDDHHmm}' + '</ul>'
});
targetDatabaseRemarksPanel.hide();

// Hadoop备注面板
var hadoopRemarksPanel = new Ext.form.Panel({
	border : false,
	bodyStyle : 'padding:15px;background-color: #CED9E7;',
	html : '<ul>'
			+ '<li>（备注：）Hadoop文件名称可以使用以下变量模式</li>'
			+ '<li>${YYYYMM}&nbsp;&nbsp;${YYYYMMDD}&nbsp;&nbsp;${YYYYMMDDHH}</li>'
			+ '<li>${YYYYMMDDHHmm}' + '</ul>'
});
hadoopRemarksPanel.hide();

//
var addDispatchFormPanel = new Ext.form.FormPanel({
			id : 'addDispatchFormPanel',
			name : 'addDispatchFormPanel',
			// defaultType : 'textfield',
			// labelAlign : 'right',
			border : false,
			bodyStyle : 'padding:15px;background-color: #CED9E7;',
			frame : false,
			items : [{
						xtype : 'textfield',
						fieldLabel : '作业名称',
						name : 'name',
						id : 'homeworkName',
					    blankText : '作业名称不能为空',//验证错误信息
						allowBlank : false,
						anchor : '99%'

					}, {
						xtype : 'component',
						html : '数据源',
						cls : 'x-form-check-group-label'

					}, {
						xtype : 'component',
						html : '<hr/>'

					}, {
						xtype : 'radio',
						boxLabel : '数据集市(远程)',
						name : 'datalist',
						// checked : true,
						id : 'getRemoteMarket',
						inputValue : '1',
						anchor : '99%',
						listeners : {
							change : function(checkbox, checked) {
								if (checked) {
									planNameSorte.load();//初始化
									databasePanel.hide();
									databaseRemarksPanel.hide();
									ftpPanel.hide();
									ftpRemarksPanel.hide();
									remoteMarketPanel.show();
									 							
//							Ext.Ajax.request({
//							   url: '',
//							   method : 'POST',
//							   //async : false,
//							    success: function(response){
//							        var text =Ext.JSON.decode(response.responseText);
//							        var sc=eval(text.schemaNames);
//							        for(var i=0;i<sc.length;i=i+1)
//							        {
//							          schemaNames.push([sc[i].name]);
//							        }							        						        
//							        //console.log(text.schemaLibrary);
////							        var st= eval(text.schemaLibrary);
////							        for(var i=0;i<st.length;i=i+1)
////							        {
////							          schemaMamesORTables.push([st[i].schemaName,st[i].tableName]);
////							          //console.log(st[i].schemaName+"---"+st[i].tableName);
////							        }
//							        planNameSorte.load();
//							      //alert();
//							    },
//							    failure: function (response, options) {
//				                    Ext.MessageBox.alert('失败', '请求超时或网络故障,错误编号：' + response.status);
//				                }
//							});
							  
								}

							}
						}
					},remoteMarketPanel, {
						xtype : 'radio',
						boxLabel : '数据源',
						name : 'datalist',
						id : 'getDatabase',
						inputValue : '2',
						anchor : '99%',
						listeners : {
							change : function(checkbox, checked) {
								if (checked) {

									databasePanel.show();
									databaseRemarksPanel.show();
									ftpRemarksPanel.hide();
									ftpPanel.hide();
									remoteMarketPanel.hide();
									remoteMarketDatabaseRemarksPanel.hide();
								}
							}
						}
					}, databasePanel, {
						xtype : 'radio',
						boxLabel : 'FTP',
						name : 'datalist',
						id : 'getFTP',
						inputValue : '3',
						anchor : '99%',
						listeners : {
							change : function(checkbox, checked) { // 选中时,调用的事件
								if (checked) {
									remoteMarketPanel.hide();
									databasePanel.hide();
									databaseRemarksPanel.hide();
									remoteMarketDatabaseRemarksPanel.hide();
									ftpRemarksPanel.show();
									ftpPanel.show();
								}
							}
						}
					}, ftpPanel, databaseRemarksPanel,remoteMarketDatabaseRemarksPanel, ftpRemarksPanel]
		});

// 数据目标面板
var addTargetFormPanel = new Ext.form.FormPanel({
			id : 'addTargetFormPanel',
			name : 'addTargetFormPanel',
			border : false,
			bodyStyle : 'padding:15px;background-color: #CED9E7;',
			frame : false,
			items : [{
						xtype : 'label',
						text : '作业(test1)数据目标',
						id:"jboName",
						cls : 'x-form-check-group-label'
					}, {
						xtype : 'component',
						html : '<hr/>'

					}, {
						xtype : 'radio',
						boxLabel : '数据集市(远程)',
						name : 'targetDatalist',
						// checked : true,
						id : 'getTargetRemoteMarket',
						inputValue : '1',
						anchor : '99%',
						listeners : {
							change : function(checkbox, checked) {
								if (checked) {
									targetPlanNameSorte.load();
									targetDatabasePanel.hide();
									hadoopRemarksPanel.hide();
									hadoopPanel.hide();
									targetDatabaseRemarksPanel.hide();
									targetRemoteMarketPanel.show();
								}

							}
						}
					},targetRemoteMarketPanel, {
						xtype : 'radio',
						boxLabel : '数据源',
						name : 'targetDatalist',
						id : 'getTargetDatabase',
						inputValue : '2',
						anchor : '99%',
						listeners : {
							change : function(checkbox, checked) {
								if (checked) {
									targetDatabasePanel.show();
									targetDatabaseRemarksPanel.show();
									hadoopRemarksPanel.hide();
									hadoopPanel.hide();
									targetRemoteMarketPanel.hide();
								}
							}
						}
					}, targetDatabasePanel, {
						xtype : 'radio',
						boxLabel : 'Hadoop平台',
						name : 'targetDatalist',
						id : 'getHadoop',
						inputValue : '3',
						anchor : '99%',
						listeners : {
							change : function(checkbox, checked) { // 选中时,调用的事件
								if (checked) {
									targetDatabasePanel.hide();
									targetDatabaseRemarksPanel.hide();
									hadoopRemarksPanel.show();
									hadoopPanel.show();
									targetRemoteMarketPanel.hide();
								}
							}
						}
					}, hadoopPanel, targetDatabaseRemarksPanel,
					hadoopRemarksPanel]
		});


// 数据目标面板
var addFieldMappingtFormPanel = new Ext.form.FormPanel({
			id : 'addFieldMappingtFormPanel',
			name : 'addFieldMappingtFormPanel',
			border : false,
			bodyStyle : 'padding:15px;background-color: #CED9E7;',
			frame : false,
			items : [fieldMappingPanel]
		});


// 弹出新增数据导出作业配置窗口
var beginWin = new Ext.Window({
	id : 'dispatch',
	layout : 'fit',
	title : '新增数据导出作业配置',
	width : 400,
	height : '100%',
	closeAction : 'close',
	collapsible : true,
	fieldDefaults : {
		labelAlign : 'left',
		// labelWidth: 90,
		anchor : '100%'
	},
	modal : true,
	renderTo : Ext.getBody(),
	constrain : true,
	items : [addDispatchFormPanel],
	buttons : [{
		text : '下一步',
		id : 'btnNext',
		handler : function() {
			var getRemoteMarket = Ext.getCmp('addDispatchFormPanel')
					.getComponent('getRemoteMarket').getValue();
			var getDatabase = Ext.getCmp('addDispatchFormPanel')
					.getComponent('getDatabase').getValue();
			var getFTP = Ext.getCmp('addDispatchFormPanel')
					.getComponent('getFTP').getValue();
            
            //user.save(); //POST /users
			var homeworkName = Ext.getCmp('homeworkName').getValue();
			

			//Ext.getCmp('homeworkName').load();
			//alert(homeworkName);
			if (homeworkName != "") {
				    var check; 
				    if(!checkIsUpatde)
					    {
						var workName=Ext.Ajax.request({
						    url: 'FastConfigScheduleServlet?action=checkJobName',
						    async : false,//同步请求数据
						    params: {
						        jobName: homeworkName
						    },
						    success: function(response){				    	
						        check = response.responseText;		
						        // process server response here
						    }
						});
						if(check=="yes")
						{
						  Ext.Msg.alert("提示","作业名称已经存在");
						  return;
						}				    
				    }

				if (getRemoteMarket == false) {
					if (getDatabase == false) {
						if (getFTP == false) {
							Ext.Msg.alert('消息', '您还没有选择源数据');
							return;
						}
					}
				} 
				//如果选中集市
				if(getRemoteMarket)
				{
					
				  _fastConfigData.data.idSourceType=4;
				 												
					//var remoteMarketCombo = Ext.getCmp('remoteMarketCombo').getRawValue();
					//var remoteMarketComboID = Ext.getCmp('remoteMarketCombo').getValue();
					var remoteMarketQueryMonthly = Ext.getCmp('remoteMarketQueryMonthly').getValue();
				      //判断是否选中了分月
				       if(remoteMarketQueryMonthly)
				       {
				       	 Ext.getCmp('remoteMarketDatabaseNameInput').allowBlank=false;
				       	 Ext.getCmp('remoteMarketDatabaseName').allowBlank=true;
				       	 var nonTargetMarketSchema= Ext.getCmp('nonTargetMarketSchema').getRawValue();
				         var remoteMarketDatabaseNameInput = Ext.getCmp('remoteMarketDatabaseNameInput').getValue();
				         var remoteMarketQueryCondition= Ext.getCmp('remoteMarketQueryCondition').getValue();
				       	 if(remoteMarketPanel.getForm().isValid())
				       	 {
				       	    _fastConfigData.data.sourceSchenaName=nonTargetMarketSchema;
		       	   		    _fastConfigData.data.sourceTableName=remoteMarketDatabaseNameInput;
							_fastConfigData.data.sourceCondition=remoteMarketQueryCondition;
						    _fastConfigData.data.sourceSeperator=Ext.getCmp('remoteMarketSeparator').getValue();
						   beginWin.hide();
	                       targetWin.show();
	                       Ext.getCmp('jboName').setText("作业（"+Ext.getCmp('homeworkName').getValue()+"）数据目标");
						  //alert(11);
				       	 }
				       }else{
				       	 Ext.getCmp('remoteMarketDatabaseNameInput').allowBlank=true;
				       	 Ext.getCmp('remoteMarketDatabaseName').allowBlank=false;
				       	    var nonTargetMarketSchema= Ext.getCmp('nonTargetMarketSchema').getRawValue();
				         	var remoteMarketDatabaseName = Ext.getCmp('remoteMarketDatabaseName').getRawValue();
							var remoteMarketDatabaseNameID = Ext.getCmp('remoteMarketDatabaseName').getValue();
							var remoteMarketQueryCondition= Ext.getCmp('remoteMarketQueryCondition').getValue();							
							if(remoteMarketPanel.getForm().isValid())
							{
								    _fastConfigData.data.sourceSchenaName=nonTargetMarketSchema;
									_fastConfigData.data.sourceTableName=remoteMarketDatabaseName;
									_fastConfigData.data.sourceCondition=remoteMarketQueryCondition;
									_fastConfigData.data.sourceSeperator=Ext.getCmp('remoteMarketSeparator').getValue();
			                       Ext.getCmp('jboName').setText("作业（"+Ext.getCmp('homeworkName').getValue()+"）数据目标");
								   beginWin.hide();
			                       targetWin.show();
							}
							
				       }
				}
				//如果选中数据库
				else if (getDatabase) {
					_fastConfigData.data.idSourceType=1;
					var nonTargetCombo = Ext.getCmp('nonTargetCombo').getRawValue();
					var nonTargetComboID = Ext.getCmp('nonTargetCombo').getValue();
						/*	var TEST= Ext.getCmp('nonTargetCombo').store;
							alert(TEST.getCount());
							alert(TEST.getTotalCount());
							  for(var i=0;i<TEST.getCount();i=i+1)
							  {
							    console.log(TEST.getAt(i).get('idDatase'));
							    console.log(TEST.getAt(i).get('name'));
							  }*/
							  
					var queryMonthly = Ext.getCmp('queryMonthly').getValue();
					if (nonTargetComboID != null) {
						_fastConfigData.data.idSourceDatabase=nonTargetComboID;
						if (queryMonthly) {
							var nonTargetDatabaseNameInput = Ext.getCmp('nonTargetDatabaseNameInput').getValue();							
							if (nonTargetDatabaseNameInput != "") {
								var queryCondition= Ext.getCmp('queryCondition').getValue();								
								   _fastConfigData.data.sourceTableName=nonTargetDatabaseNameInput;
								   _fastConfigData.data.sourceCondition=queryCondition;	
								   
			                       Ext.getCmp('jboName').setText("作业（"+Ext.getCmp('homeworkName').getValue()+"）数据目标");
								   beginWin.hide();
			                       targetWin.show();                            
							}else
							{
							   Ext.Msg.alert('消息', '择请输入变量类型');
			                   return;	
							}
						} else {
							var nonTargetDatabaseName = Ext.getCmp('nonTargetDatabaseName').getRawValue();
							var nonTargetDatabaseNameID = Ext.getCmp('nonTargetDatabaseName').getValue();
							if(nonTargetDatabaseNameID!=null)
							{

							 var queryCondition= Ext.getCmp('queryCondition').getValue();
								   _fastConfigData.data.sourceTableName=nonTargetDatabaseName;
								   _fastConfigData.data.sourceCondition=queryCondition;								 
			                       Ext.getCmp('jboName').setText("作业（"+Ext.getCmp('homeworkName').getValue()+"）数据目标");
								   beginWin.hide();
			                       targetWin.show();
							}else{
							   Ext.Msg.alert('消息', '请选择数据库表名');
			                   return;							  
							}
						}
					}else
					{
					   Ext.Msg.alert('消息', '请选择数据源名称');
			           return;
					}

				}//如果选择FTP
				else if(getFTP){
                   	if(ftpPanel.getForm().isValid())
                   	{	
                   	  //alert( Ext.getCmp('isFirstLineFieldName').getValue());
                   	   if(Ext.getCmp('isFirstLineFieldName').getValue())
                   	   {
                   	     _fastConfigData.data.isFirstLineFieldName=0;
                   	   }else
                   	   {
                   	     _fastConfigData.data.isFirstLineFieldName=1;
                   	   }
                   	   
                   	   _fastConfigData.data.idSourceType=2; 
                   	   _fastConfigData.data.idSourceFTP=Ext.getCmp('ftpName').getValue();
                       _fastConfigData.data.sourceFilePath=Ext.getCmp('fptPath').getValue();
                       _fastConfigData.data.sourceFileName=Ext.getCmp('fileName').getValue();
                       _fastConfigData.data.sourceSeperator=Ext.getCmp('ftpSeparator').getValue(); 
                       Ext.getCmp('jboName').setText("作业（"+Ext.getCmp('homeworkName').getValue()+"）数据目标");
					   beginWin.hide();
                       targetWin.show();
                   	}
				
				}

			}else{
			  Ext.Msg.alert('消息', '作业名称不能为空');
			  return;
			}
           // return;
			// alert(nonTargetCombo);
			// alert(nonTargetComboID);

			// alert(Ext.getCmp('homeworkName').getValue());
			/*
			 * var mode = Ext.getCmp('windowmode').getValue(); if (mode ==
			 * 'add') saveParamItem(); else updateParamItem();
			 */
		}
	}, {
		text : '关闭',
		id : 'btnClose',
		// iconCls : 'tbar_synchronizeIcon',
		handler : function() {
			beginWin.close();
		}
	}]
});

//监听beginwin
beginWin.on('close',function(){
//清理面板
clearReset();
});

// 数据目标
var targetWin = new Ext.Window({
			id : 'targetWin',
			layout : 'fit',
			title : '数据目标',
			width : 400,
			height : '100%',
			closeAction : 'close',
			collapsible : true,
			fieldDefaults : {
				labelAlign : 'left',
				// labelWidth: 90,
				anchor : '100%'
			},
			modal : true,
			renderTo : Ext.getBody(),
			constrain : true,
			items : [addTargetFormPanel],
			buttons : [{
						text : '上一步',
						id : 'btnUpTarget',
						handler : function() {
							targetWin.hide();
							beginWin.show();
						}
					}, {
						text : '下一步',
						id : 'btnNextTarget',
						handler : function() {
							var getTargetRemoteMarket = Ext
									.getCmp('addTargetFormPanel')
									.getComponent('getTargetRemoteMarket')
									.getValue();
							var getTargetDatabase = Ext
									.getCmp('addTargetFormPanel')
									.getComponent('getTargetDatabase')
									.getValue();
							var getHadoop = Ext.getCmp('addTargetFormPanel')
									.getComponent('getHadoop').getValue();
							if (getTargetRemoteMarket == false) {
								if (getTargetDatabase == false) {
									if (getHadoop == false) {
										Ext.Msg.alert('消息', '您还没有选择目标数据');
										return;
									}
								}
							}
							
							if(getTargetRemoteMarket)
							{
							_fastConfigData.data.idDestType=4;
							var targetRemoteMarketQueryMonthly = Ext.getCmp('targetRemoteMarketQueryMonthly').getValue();
						      //判断是否选中了分月
						       if(targetRemoteMarketQueryMonthly)
						       {
						       	Ext.getCmp('targetRemoteMarketDatabaseNameInput').allowBlank=false;
				       	        Ext.getCmp('targetRemoteMarketDatabaseName').allowBlank=true;
						         var targetRemoteMarketDatabaseNameInput = Ext.getCmp('targetRemoteMarketDatabaseNameInput').getValue();
						         var targetMarketSchema= Ext.getCmp('targetMarketSchema').getRawValue();
						         if(targetRemoteMarketPanel.getForm().isValid())
						         {
						         
						         			var remoteMarketFulldose= Ext.getCmp('remoteMarketFulldose').getValue();
						                    if(remoteMarketFulldose)
						                    {
						                      _fastConfigData.data.loadType=1;
						                    }else
						                    {
						                      _fastConfigData.data.loadType=2;
						                    }
						                    _fastConfigData.data.destSchenaName=targetMarketSchema;
											_fastConfigData.data.destTableName=targetRemoteMarketDatabaseNameInput;
										   targetWin.hide();
										   var d=JSON.stringify(_fastConfigData.getData());
     
									       fieldMappingDataStore.load({params:{fastConfigData: d}});
										   fieldMappingWin.show();
						         	
						         }
						         
						       }else{
						       	Ext.getCmp('targetRemoteMarketDatabaseNameInput').allowBlank=true;
				       	        Ext.getCmp('targetRemoteMarketDatabaseName').allowBlank=false;
						       		var targetMarketSchema= Ext.getCmp('targetMarketSchema').getRawValue();
						         	var targetRemoteMarketDatabaseName = Ext.getCmp('targetRemoteMarketDatabaseName').getRawValue();
									var targetRemoteMarketDatabaseNameID = Ext.getCmp('targetRemoteMarketDatabaseName').getValue();
									
									if(targetRemoteMarketPanel.getForm().isValid())
									{
							               var remoteMarketFulldose= Ext.getCmp('remoteMarketFulldose').getValue();
						                    if(remoteMarketFulldose)
						                    {
						                      _fastConfigData.data.loadType=1;
						                    }else
						                    {
						                      _fastConfigData.data.loadType=2;
						                    }
						                    //console.log("1_fastConfigData.data.loadType==="+_fastConfigData.data.loadType);
						                   _fastConfigData.data.destSchenaName=targetMarketSchema;
										   _fastConfigData.data.destTableName=targetRemoteMarketDatabaseName;											
										   targetWin.hide();
										   var d=JSON.stringify(_fastConfigData.getData());
     
									       fieldMappingDataStore.load({params:{fastConfigData: d}});
										   fieldMappingWin.show();
									}
						       }	
							}else if(getTargetDatabase)
							{
							   _fastConfigData.data.idDestType=1;
							   var targetCombo =Ext.getCmp('targetCombo').getRawValue();
							   var targetComboID =Ext.getCmp('targetCombo').getValue();
							   var targetQueryMonthly = Ext.getCmp('targetQueryMonthly').getValue();
							   if(targetComboID!=null)
							   {
							   	 if(targetQueryMonthly)
							   	 {
							   	   var targetDatabaseNameInput = Ext.getCmp('targetDatabaseNameInput').getValue();
							   	   if(targetDatabaseNameInput!="")
							   	   {
					         			var fulldose= Ext.getCmp('fulldose').getValue();
					                    if(fulldose)
					                    {
					                      _fastConfigData.data.loadType=1;
					                    }else
					                    {
					                      _fastConfigData.data.loadType=2;
					                    }
					                    _fastConfigData.data.idDestDatabase=targetComboID;
										_fastConfigData.data.destTableName=targetDatabaseNameInput;
									   targetWin.hide();
									   var d=JSON.stringify(_fastConfigData.getData());
		
									fieldMappingDataStore.load({params:{fastConfigData: d}});								
									//Ext.getCmp('fieldMappingPanel').store=fieldMappingDataStore;
							        fieldMappingWin.show();
							   	   }else
							   	   {							   	   
							   	   	 Ext.Msg.alert('消息', '请输入目标数据库名称');
						             return;	
							   	   }
							   	 }else{
							   	   	var targetDatabaseName = Ext.getCmp('targetDatabaseName').getRawValue();
									var targetDatabaseNameID = Ext.getCmp('targetDatabaseName').getValue();
									if(targetDatabaseNameID!=null)
									{
					                    var fulldose= Ext.getCmp('fulldose').getValue();
					                    if(fulldose)
					                    {
					                      _fastConfigData.data.loadType=1;
					                    }else
					                    {
					                      _fastConfigData.data.loadType=2;
					                    }
					         	        _fastConfigData.data.idDestDatabase=targetComboID;							                    
										_fastConfigData.data.destTableName=targetDatabaseName;											
									    targetWin.hide();
									    //fieldMappingWin.show();
                                        var d=JSON.stringify(_fastConfigData.getData());
     
									fieldMappingDataStore.load({params:{fastConfigData: d}});
									//Ext.getCmp('fieldMappingPanel').store=fieldMappingDataStore;
							        fieldMappingWin.show();
									}else{
										   Ext.Msg.alert('消息', '请选择目标数据库表名');
						                   return;							  
										}
							   	 
							   	 }
							   }else
							   {							   
							   	Ext.Msg.alert('消息','请选择目标数据源名称');
							   	return;
							   }
							   // _fastConfigData
							}else if(getHadoop)
							{					
							   if(hadoopPanel.getForm().isValid())
							   {
							       _fastConfigData.data.idDestType=3;               	   
			                   	   _fastConfigData.data.idDestHadoop=1;
			                   	   _fastConfigData.data.loadType=1;
			                       _fastConfigData.data.destFilePath=Ext.getCmp('hadoopPath').getValue();
			                       _fastConfigData.data.destFileName=Ext.getCmp('hadoopFileName').getValue();
			                       targetWin.hide();
									    //fieldMappingWin.show();
                                    var d=JSON.stringify(_fastConfigData.getData());
									fieldMappingDataStore.load({params:{fastConfigData: d}});
									//Ext.getCmp('fieldMappingPanel').store=fieldMappingDataStore;
							        fieldMappingWin.show();
							   }
							}
						}
					}, {
						text : '关闭',
						id : 'btnCloseTarget',
						// iconCls : 'tbar_synchronizeIcon',
						handler : function() {
							targetWin.close();
						}
					}]
		});
		
targetWin.on('close',function(){
	clearReset();
});

// The field mapping
//	fieldMappingDataStore.getModifiedRecords().
var fieldMappingWin = new Ext.Window({
			id : 'fieldMappingWin',
			layout : 'fit',
			title : '字段对应关系映射',
			width : 1000,
			height : 600,
			closeAction : 'close',
			collapsible : true,
			fieldDefaults : {
				// labelAlign : 'left',
				// labelWidth: 90,
				anchor : '100%'
			},
			modal : true,
			renderTo : Ext.getBody(),
			constrain : true,
			items : [/*addFieldMappingtFormPanel*/fieldMappingPanel],
			buttons : [{
						text : '上一步',
						id : 'btnUpField',
						handler : function() {
							fieldMappingWin.hide();
							targetWin.show();
						}
					}, {
						text : '下一步',
						id : 'btnNextField',
						handler : function() {
						  Ext.getCmp('dispatchingModeJboName').setText("作业（"+Ext.getCmp('homeworkName').getValue()+"）调度方式");
						  fieldMappingWin.hide();
						  //var _data=fieldMappingDataArrayList();
                          dispatchingModeWin.show();
                       // console.log (JSON.stringify(fieldMappingDataStore.getStore()));
						}
					}, {
						text : '关闭',
						id : 'btnCloseField',
						// iconCls : 'tbar_synchronizeIcon',
						handler : function() {
							fieldMappingWin.close();
						}
					}]
		});
fieldMappingWin.on('close',function(){
  clearReset();
});
//调度方式窗口		
var dispatchingModeWin = new Ext.Window({
			id : 'dispatchingModeWin',
			layout : 'fit',
			title : '调度方式',
			width : 420,
			height : 310,
			closeAction : 'close',
			collapsible : true,
			fieldDefaults : {
				// labelAlign : 'left',
				// labelWidth: 90,
				anchor : '100%'
			},
			modal : true,
			renderTo : Ext.getBody(),
			constrain : true,
			items : [/*addFieldMappingtFormPanel*/ dispatchingModePanel],
			buttons : [{
						text : '上一步',
						id : 'btnUpDispatchingMode',
						handler : function() {
						  fieldMappingWin.show();
                          dispatchingModeWin.hide();
						}
					}, {
						text : '测试',
						id : 'jobTest',
						handler : function() {
						   var d=JSON.stringify(_fastConfigData.getData());
						  
						   Ext.Ajax.request({
								   url: 'TestDataServlet?action=testData',
								   method : 'POST',
								    params: {
								    	fastConfigData:d
								    },
								    success: function(response){
								       var text = Ext.JSON.decode(response.responseText);
								        //console.log(text);
								        Ext.getCmp('testStateText').show();
								        Ext.getCmp('testStateText').setText(text.state1+"==>"+text.state2);
								        
								    },
								    failure: function (response, options) {
					                    Ext.MessageBox.alert('失败', '请求超时或网络故障,错误编号：' + response.status);
					                }
								});
						}
					},{
						text : '保存',
						id : 'btnSaveDispatchingMode',
						handler : function() {
						if(dispatchingModePanel.getForm().isValid())
						{
							var dispatchingModeForm= dispatchingModePanel.getForm().getValues();
		
							if(dispatchingModeForm.scheduletype==3)
							{
							  dispatchingModeForm.cycleMode="";
							}else
							{
								dispatchingModeForm.cycleModeMonth="";
							}
							//console.log(dispatchingModeForm.cycleModeMonth);
							//alert(Ext.getCmp('beginTime').getRawValue());
							dispatchingModeForm.beginTime=Ext.getCmp('beginTime').getRawValue();
							//.log(dispatchingModeForm.runDate);
							//console.log(dispatchingModeForm.endTime);
							if(dispatchingModeForm.endTimeType==2){
								if(dateComparison(dispatchingModeForm.runDate,dispatchingModeForm.endTime))
								{						  							
									var _fastConfigJson=JSON.stringify(_fastConfigData.getData());
									var _fieldMappingJson=JSON.stringify(fieldMappingDataArrayList());
									var _dispatchingModeJosn=JSON.stringify(dispatchingModeForm);
									var _jobName= Ext.getCmp('homeworkName').getValue();
									//console.log(JSON.stringify(_fastConfigData.getData()));
									//console.log(JSON.stringify(fieldMappingDataArrayList()));
									//console.log(JSON.stringify(dispatchingModeForm));
									//console.log("checkIsUpatde"+checkIsUpatde);
									Ext.Ajax.request({
									   url: 'FastConfigScheduleServlet?action=saveDispatch',
									   method : 'POST',
									    params: {
									    	fastConfigJson:_fastConfigJson,
									        fieldMappingJson: _fieldMappingJson,
									        dispatchingModeJosn:_dispatchingModeJosn,
									        jobName:_jobName,
									        checkIsUpatde:checkIsUpatde
									    },
									    success: function(response){
									        var text = response.responseText;
									        // process server response here
									        Ext.Msg.alert('作业保存成功','作业保存成功');
									        window.setTimeout(function() {
											 window.location="schedule?action=list";
										}, 3000);
									       
									        //toLoadurl('schedule?action=list','schedule_main','调度');
									        dispatchingModeWin.hide();
									    },
									    failure: function (response, options) {
						                    Ext.MessageBox.alert('失败', '请求超时或网络故障,错误编号：' + response.status);
						                }
									});
								}
							}else
							{
							  		var _fastConfigJson=JSON.stringify(_fastConfigData.getData());
									var _fieldMappingJson=JSON.stringify(fieldMappingDataArrayList());
									var _dispatchingModeJosn=JSON.stringify(dispatchingModeForm);
									var _jobName= Ext.getCmp('homeworkName').getValue();
									//console.log("checkIsUpatde"+checkIsUpatde);
									Ext.Ajax.request({
									   url: 'FastConfigScheduleServlet?action=saveDispatch',
									   method : 'POST',
									    params: {
									    	fastConfigJson:_fastConfigJson,
									        fieldMappingJson: _fieldMappingJson,
									        dispatchingModeJosn:_dispatchingModeJosn,
									        jobName:_jobName,
									        checkIsUpatde:checkIsUpatde
									    },
									    success: function(response){
									        var text = response.responseText;
									        // process server response here
									        Ext.Msg.alert('作业保存成功','作业保存成功');
										    window.setTimeout(function() {
											 window.location="schedule?action=list";
										}, 3000);
									        //toLoadurl('schedule?action=list','schedule_main','调度');
									        dispatchingModeWin.hide();
									    },
									    failure: function (response, options) {
						                    Ext.MessageBox.alert('失败', '请求超时或网络故障,错误编号：' + response.status);
						                }
									});
							}

							
							//alert("通过");
						}else{
						   // alert("不通过");
						}
						}
					}, {
						text : '关闭',
						id : 'btnCloseDispatchingMode',
						// iconCls : 'tbar_synchronizeIcon',
						handler : function() {
							dispatchingModeWin.close();
							
						}
					}]
		});
dispatchingModeWin.on('close',function(){
 clearReset();
});		
		
//删除一行
function deleteField()
{
  		var data = fieldMappingPanel.getSelectionModel().getSelection();
		// alert(data);
		if (data.length == 0) {
			Ext.MessageBox.show({
				title : "提示",
				msg : "请先选择您要操作的行!"
					// icon: Ext.MessageBox.INFO
				});
			return;
		} else {
			Ext.Msg.confirm("请确认", "是否真的要删除数据？", function(button, text) {
				if (button == "yes") {
					Ext.Array.each(data, function(record) {
							fieldMappingDataStore.remove(record);// 页面效果
						});
				}
			});

		}
  
}


//字段映射下一步
function fieldMappingDataArrayList()
{	
		var data = [];
		  fieldMappingDataStore.each(function(record){
		  data.push(record.data);
		  });
	return data;

}
//测试保存字段数据		
function saveListView()
{
  		var records = store.getUpdatedRecords();// 获取修改的行的数据，无法获取幻影数据
		var phantoms=store.getNewRecords( ) ;//获得幻影行
		records=records.concat(phantoms);//将幻影数据与真实数据合并
		if (records.length != 0) {
			Ext.Msg.confirm("请确认", "您有修改过参数是否真的要修改数据进行下一步？", function(button, text) {
				if (button == "yes") {
					var data = [];
					// alert(records);
					fieldMappingDataStore.each(function(record){
					  data.push(record.data);						
					});
//					Ext.Array.each(records, function(record) {
//						data.push(record.data);
//							// record.commit();// 向store提交修改数据，页面效果
//						});

					Ext.Ajax.request({
						url : 'alterUsers.action',
						params : {
							alterUsers : Ext.encode(data)
						},
						method : 'POST',
						timeout : 2000,

						success : function(response, opts) {
							var success = Ext.decode(response.responseText).success;
							// 当后台数据同步成功时
							if (success) {
//								Ext.Array.each(records, function(record) {
//											// data.push(record.data);
//											record.commit();// 向store提交修改数据，页面效果
//										});
							} else {
								Ext.MessageBox.show({
									title : "提示",
									msg : "数据保存失败!"
										// icon: Ext.MessageBox.INFO
									});
							}
						}
					});
				}
			});
		} else {
			Ext.Msg.confirm("请确认", "是否真的要修改数据？", function(button, text) {
				if (button == "yes") {
					var data = [];
					fieldMappingDataStore.each(function(record){
					  data.push(record.data);						
					});

					Ext.Ajax.request({
						url : 'alterUsers.action',
						params : {
							alterUsers : Ext.encode(data)
						},
						method : 'POST',
						timeout : 2000,

						success : function(response, opts) {
							var success = Ext.decode(response.responseText).success;
							// 当后台数据同步成功时
							if (success) {
//								Ext.Array.each(records, function(record) {
//											// data.push(record.data);
//											record.commit();// 向store提交修改数据，页面效果
//										});
							} else {
								Ext.MessageBox.show({
									title : "提示",
									msg : "数据提交失败!"
										// icon: Ext.MessageBox.INFO
									});
							}
						}
					});
				}
			});

		}

}

function dateComparison(a, b) {
    var arr = a.split("-");
    var starttime = new Date(arr[0], arr[1], arr[2]);
    var starttimes = starttime.getTime();

    var arrs = b.split("-");
    var lktime = new Date(arrs[0], arrs[1], arrs[2]);
    var lktimes = lktime.getTime();

    if (starttimes >= lktimes) {
        Ext.Msg.alert('消息','开始时间大于截止时间，请检查');
        return false;
    }
    else
        return true;

}

//修改
//Ext.getCmp('getDatabase').
function updateJob()
{
	var checks = document.getElementsByName('check');
	var checked_job = '';
	var check_count = 0;
	var check_
	if(checks.length>0)
	{	
		for(var i=0;i<checks.length;i++){
		if(checks[i].checked){
			check_count = check_count + 1;
			if(checked_job=='')
			checked_job = checks[i].value;
		}
	  }
	  if(check_count==0)
	  {
		 Ext.MessageBox.alert('消息','您没有选中要修改的作业');
         return;
	  }
	  if(check_count>1)
	  {
		 Ext.MessageBox.alert('消息','一次只能修改一个');
         return;
	  }
	}else
	{
		 Ext.MessageBox.alert('消息','没有作业可选');		
	  return;
	}
	Ext.Ajax.request({
	   url: 'FastConfigScheduleServlet?action=findJob',
	   method : 'POST',   
	    params: {
	        jobName:checked_job
	    },
	    success: function(response){
	        var text = response.responseText;	        
	       // console.log("===================="+text);
	        text=Ext.JSON.decode(text);
	        Ext.getCmp('homeworkName').setValue(text.jobName);	
	        switch (text.fastConfigView.idSourceType)
	        {
	         case 1:
	           //数据库
	           Ext.getCmp('getDatabase').setValue(true);
	           Ext.getCmp('getRemoteMarket').setValue(false);
	           Ext.getCmp('getFTP').setValue(false);
               var com=Ext.getCmp('nonTargetCombo');
		      //监听load事件
				com.store.on('load', function(index){
			      for(var i=0;i<com.store.getCount();i=i+1)
				  {
				  	 //console.log("------------------------idDatase1"+com.store.getAt(i).get('idDatase'));
				  	 if(text.fastConfigView.idSourceDatabase==com.store.getAt(i).get('idDatase'))
				  	 {
				  	 	//console.log("------------------------idDatase2"+com.store.getAt(i).get('idDatase'));
				  	   	com.select(com.store.getAt(i));		 	
				  	 }
				  }	
				});
			   com.store.load();		       
		       Ext.getCmp('nonTargetDatabaseName').setValue(text.fastConfigView.sourceTableName);
               if(Ext.getCmp('queryMonthly').getValue())
               {
                 //Ext.getCmp('queryMonthly').setValue();
               }
                 Ext.getCmp('queryCondition').setValue(text.fastConfigView.sourceCondition);
	            //console.log(com.getRawValue());
			    databasePanel.show();
				databaseRemarksPanel.show();
				ftpRemarksPanel.hide();
				ftpPanel.hide();
				remoteMarketPanel.hide();
				remoteMarketDatabaseRemarksPanel.hide();	
				updatTarget(text);
	         break;
	         case 2:
	          //FTP
	           Ext.getCmp('getDatabase').setValue(false);
	           Ext.getCmp('getRemoteMarket').setValue(false);
	           Ext.getCmp('getFTP').setValue(true);
	            var ftpName= Ext.getCmp('ftpName');
				//监听load事件
				ftpName.store.on('load', function(index){
				  for(var i=0;i<ftpName.store.getCount();i=i+1)
				  {
					  if(text.fastConfigView.idSourceFTP == ftpName.store.getAt(i).get('ftpID')){
						  ftpName.select(ftpName.store.getAt(i));
					  }
				  }
				});
				ftpName.store.load();
				if(text.fastConfigView.isFirstLineFieldName==0)
				{
			    	Ext.getCmp('isFirstLineFieldName').setValue(true);
				}else if(text.fastConfigView.isFirstLineFieldName==1)
				{
					Ext.getCmp('isFirstLineFieldName').setValue(false);
				}
                Ext.getCmp('fptPath').setValue(text.fastConfigView.sourceFilePath);
                Ext.getCmp('fileName').setValue(text.fastConfigView.sourceFileName);
                Ext.getCmp('ftpSeparator').setValue(text.fastConfigView.sourceSeperator);
			    remoteMarketPanel.hide();
				databasePanel.hide();
				databaseRemarksPanel.hide();
				remoteMarketDatabaseRemarksPanel.hide();
				ftpRemarksPanel.show();
				ftpPanel.show();
               updatTarget(text);
	         break;
	         case 3:
	          //hadoop
	           updatTarget(text);
	          break;
	         case 4:
	          //数据集市
	           Ext.getCmp('getDatabase').setValue(false);
	           Ext.getCmp('getRemoteMarket').setValue(true);
	           Ext.getCmp('getFTP').setValue(false);
	           var nonTargetMarketSchema=Ext.getCmp('nonTargetMarketSchema');
	           //监听load事件
				planNameSorte.on('load', function(index){
			      for(var i=0;i<planNameSorte.getCount();i=i+1)
				  {
				  	 if(text.fastConfigView.sourceSchenaName==planNameSorte.getAt(i).get('name'))
				  	 {
				  	   	nonTargetMarketSchema.select(planNameSorte.getAt(i));		 	
				  	 }
				  }	
				});
			   planNameSorte.load();
			   Ext.getCmp('remoteMarketDatabaseName').setValue(text.fastConfigView.sourceTableName);
			   Ext.getCmp('remoteMarketQueryCondition').setValue(text.fastConfigView.sourceCondition);
			   Ext.getCmp('remoteMarketSeparator').setValue(text.fastConfigView.sourceSeperator);   
				databasePanel.hide();
				databaseRemarksPanel.hide();
				ftpPanel.hide();
				ftpRemarksPanel.hide();
				remoteMarketPanel.show();
	            updatTarget(text);
	         break;
	         default:
	         break;
	        }
	        //调度配置
	        updateDispatchingMode(text);
	        
	        updateDispatch();	       
	        //toLoadurl('schedule?action=list','schedule_main','调度');
	     //   dispatchingModeWin.hide();
	    },
	    failure: function (response, options) {
            Ext.MessageBox.alert('失败', '请求超时或网络故障,错误编号：' + response.status);
        }
	});
}

//修改目标页面参数
function updatTarget(text)
{
   switch(text.fastConfigView.idDestType){
   	 //如果目标是数据库
   	 case 1:
   	   Ext.getCmp('getTargetRemoteMarket').setValue(false);
       Ext.getCmp('getTargetDatabase').setValue(true);
       Ext.getCmp('getHadoop').setValue(false);
       var com=Ext.getCmp('targetCombo');
      //监听load事件
		com.store.on('load', function(index){
	      for(var i=0;i<com.store.getCount();i=i+1)
		  {
		  	 if(text.fastConfigView.idDestDatabase==com.store.getAt(i).get('idDatase'))
		  	 {
		  	   	com.select(com.store.getAt(i));		 	
		  	 }
		  }	
		});
	   com.store.load();
       Ext.getCmp('targetDatabaseName').setValue(text.fastConfigView.destTableName);
       if(Ext.getCmp('targetQueryMonthly').getValue())
       {
         //Ext.getCmp('queryMonthly').setValue();
       }
       if(text.fastConfigView.loadType==1)
       {
         Ext.getCmp('fulldose').setValue(true);
         Ext.getCmp('increment').setValue(false);
       }else if(text.fastConfigView.loadType==2)
       {
       	 Ext.getCmp('increment').setValue(true);
       	 Ext.getCmp('fulldose').setValue(false);
       }
        targetDatabasePanel.show();
		targetDatabaseRemarksPanel.show();
		hadoopRemarksPanel.hide();
		hadoopPanel.hide();
		targetRemoteMarketPanel.hide();
   	 break;
   	 case 2:
      //FTP
     break;
     case 3:
      //hadoop
       Ext.getCmp('getTargetRemoteMarket').setValue(false);
       Ext.getCmp('getTargetDatabase').setValue(false);
       Ext.getCmp('getHadoop').setValue(true);
       Ext.getCmp('hadoopPath').setValue(text.fastConfigView.destFilePath);
       Ext.getCmp('hadoopFileName').setValue(text.fastConfigView.destFileName);
       
      break;
     case 4:
      //数据集市
   	   Ext.getCmp('getTargetRemoteMarket').setValue(true);
       Ext.getCmp('getTargetDatabase').setValue(false);
       Ext.getCmp('getHadoop').setValue(false);
       var targetMarketSchema=Ext.getCmp('targetMarketSchema');
       //监听load事件
		targetPlanNameSorte.on('load', function(index){
	      for(var i=0;i<targetPlanNameSorte.getCount();i=i+1)
		  {
		  	 if(text.fastConfigView.destSchenaName==targetPlanNameSorte.getAt(i).get('name'))
		  	 {
		  	   	targetMarketSchema.select(targetPlanNameSorte.getAt(i));		 	
		  	 }
		  }	
		});
	   Ext.getCmp('targetRemoteMarketDatabaseName').setValue(text.fastConfigView.destTableName);
	   
	   if(text.fastConfigView.loadType==1)
       {
         Ext.getCmp('remoteMarketFulldose').setValue(true);
         Ext.getCmp('remoteMarketIncrement').setValue(false);
       }else if(text.fastConfigView.loadType==2)
       {
       	 Ext.getCmp('remoteMarketIncrement').setValue(true);
       	 Ext.getCmp('remoteMarketFulldose').setValue(false);
       }
	   targetPlanNameSorte.load();  
		targetDatabasePanel.hide();
		hadoopRemarksPanel.hide();
		hadoopPanel.hide();
		targetDatabaseRemarksPanel.hide();
		targetRemoteMarketPanel.show();   
     break;
     default:
     break;           	
   }
}

function updateDispatchingMode(text)
{
  var runMode= Ext.getCmp('runMode');
//监听load事件
runMode.store.on('load', function(index){
  for(var i=0;i<runMode.store.getCount();i=i+1)
  {
  	 if(text.dispatchingModeView.runMode==runMode.store.getAt(i).get('runModeValue'))
	  	 {
	  	   	runMode.select(runMode.store.getAt(i));		 	
	  	 }
	  }	
	});
   runMode.store.load();
   var runCluster= Ext.getCmp('runCluster');
 //监听load事件
 runCluster.store.on('load', function(index){
   for(var i=0;i<runCluster.store.getCount();i=i+1)
   {
   	 if(text.dispatchingModeView.runCluster==runCluster.store.getAt(i).get('runClusterValue'))
 	  	 {
   		runCluster.select(runCluster.store.getAt(i));		 	
 	  	 }
 	  }	
 	});
 runCluster.store.load();
   Ext.getCmp('beginTime').setValue(text.dispatchingModeView.beginTime);
   var scheduletype= Ext.getCmp('scheduletype');
   //监听load事件
  scheduletype.store.on('load', function(index){
  for(var i=0;i<scheduletype.store.getCount();i=i+1)
  {
  	 if(text.dispatchingModeView.scheduletype==scheduletype.store.getAt(i).get('scheduletype'))
	  	 {
	  	   	scheduletype.select(scheduletype.store.getAt(i));	
            if(text.dispatchingModeView.scheduletype==1)
              {
                Ext.getCmp('cycleStateHtml').setText("小时");
                Ext.getCmp('cycleModeMonth').hide();
                Ext.getCmp('cycleMode').show();
                Ext.getCmp('cycleStateText').setValue(text.dispatchingModeView.cycleMode);
                Ext.getCmp('cycleStateTextMonth').allowBlank=true;
                Ext.getCmp('cycleStateText').allowBlank=false;
              }
              if(text.dispatchingModeView.scheduletype==2)
              {
                Ext.getCmp('cycleStateHtml').setText("天");
                Ext.getCmp('cycleModeMonth').hide();
                Ext.getCmp('cycleMode').show();
                Ext.getCmp('cycleStateText').setValue(text.dispatchingModeView.cycleMode);
                Ext.getCmp('cycleStateTextMonth').allowBlank=true;
                Ext.getCmp('cycleStateText').allowBlank=false;
              }
              if(text.dispatchingModeView.scheduletype==3)
              {
                Ext.getCmp('cycleStateHtml').setText("月");
                Ext.getCmp('cycleMode').hide();
                Ext.getCmp('cycleModeMonth').show();            
               var cycleModeMonth= Ext.getCmp('cycleStateTextMonth');
				//监听load事件
				cycleModeMonth.store.on('load', function(index){
				  for(var i=0;i<cycleModeMonth.store.getCount();i=i+1)
				  {
				  	 if(text.dispatchingModeView.cycleModeMonth==cycleModeMonth.store.getAt(i).get('runDateText'))
					  	 {
					  	   	cycleModeMonth.select(cycleModeMonth.store.getAt(i));		 	
					  	 }
					  }	
					});
				cycleModeMonth.store.load();
                Ext.getCmp('cycleStateTextMonth').allowBlank=false;
                Ext.getCmp('cycleStateText').allowBlank=true;
              }
	  	   	
	  	 }
	  }	
	});
   scheduletype.store.load();
   Ext.getCmp('runDate').setValue(text.dispatchingModeView.runDate);
   if(text.dispatchingModeView.endTimeType==1)
   {
     Ext.getCmp('timeDefinition').setValue(true);
     Ext.getCmp('theCustom').setValue(false);
   }else if(text.dispatchingModeView.endTimeType==2)
   {
     Ext.getCmp('timeDefinition').setValue(false);
     Ext.getCmp('theCustom').setValue(true);
     Ext.getCmp('inputEndTime').setValue(text.dispatchingModeView.endTime);
   }
   
}
//c
function clearReset()
{
addDispatchFormPanel.getForm().reset();
remoteMarketPanel.hide();
databasePanel.hide();
ftpPanel.hide();
databaseRemarksPanel.hide();
remoteMarketDatabaseRemarksPanel.hide();
ftpRemarksPanel.hide();
//目标数据源
addTargetFormPanel.getForm().reset();
targetRemoteMarketPanel.hide();
targetDatabasePanel.hide();
hadoopPanel.hide();
targetDatabaseRemarksPanel.hide();
hadoopRemarksPanel.hide();
//调度配置
dispatchingModePanel.getForm().reset();	

//测试状态
//Ext.getCmp('testStateText').reset();
Ext.getCmp('testStateText').hide();
}

// 开启窗口
function addDispatch() {
//	addDispatchFormPanel.getForm().reset();
//	addTargetFormPanel.getForm().reset();
////	fieldMappingPanel.reset();
////	dispatchingModePanel.reset();
	checkIsUpatde=false;
	beginWin.show();
	//dispatchingModeWin.show();
}
// 开启窗口
function updateDispatch() {
	checkIsUpatde=true;
	beginWin.show();
}
// 开启窗口
function addDispatch1() {
	//beginWin.show();
	dispatchingModeWin.show();
}
