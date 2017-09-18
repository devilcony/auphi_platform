Ext.onReady(function() {

	var sm = new Ext.grid.CheckboxSelectionModel();
	var cm = new Ext.grid.ColumnModel([ new Ext.grid.RowNumberer(), sm, {
		header : '接口编号',
		dataIndex : 'serviceId',
		hidden : true
	}, {
		id : 'serviceName',
		header : '服务接口名称',
		dataIndex : 'serviceName',
		width : 200
	},  {
		header : '服务标识',
		dataIndex : 'serviceIdentify',
		width : 160
	}, {
		header : '服务接口地址',
		dataIndex : 'serviceUrl',
		width : 250
	},{
		header : '服务接口类型',
		dataIndex : 'returnType',
		width : 120,
		renderer : function(v) {
			if (v == 1) {
				return "FTP";
			} else if (v == 2) {
				return "Webservice";
			}
		}
	}, {
		id : 'datasource',
		header : ' 数据源',
		dataIndex : 'datasource',
		width : 100
	}, {
		id : 'tableName',
		header : ' 连接表名',
		dataIndex : 'tableName',
		width : 100
	}, {
		id : 'fields',
		header : ' 输出字段',
		dataIndex : 'fields',
		width : 100
	}, {
		id : 'conditions',
		header : ' 条件表达式',
		dataIndex : 'conditions',
		width : 100
	},{
		header : '结果超时时间 (小时)',
		dataIndex : 'timeout',
		width : 80
	}, {
		header : '是否压缩',
		dataIndex : 'isCompress',
		width : 60,
		renderer : function(v) {
			if (v == 1) {
				return "是";
			} else if (v ==2) {
				return "否";
			} 
		}
	}, {
		header : '创建时间',
		dataIndex : 'createDate',
		width : 140
	} , {
		header : '查看',
		dataIndex : 'serviceId',
		width : 140,
		renderer : function(v) {
			return "<a href='../service/viewService.shtml?serviceId="+v+"' target='_blank'>查看服务接口使用说明</a>";
		}
	},{
		header : '分隔符',
		dataIndex : 'delimiter',
		hidden : true
	},{
		header : '数据源ID',
		dataIndex : 'idDatabase',
		hidden : true
	},{
		header : 'Job Name',
		dataIndex : 'transName',
		hidden : true
	}, {
		header : '查询条件',
		dataIndex : 'conditions',
		hidden : true
	}, {
		header : '接口描述',
		dataIndex : 'interfaceDesc',
		hidden : true
	}, {
		header : '服务标识',
		dataIndex : 'serviceIdentify',
		hidden : true
	}]);

	/**
	 * 数据存储
	 */
	var store = new Ext.data.Store({
		proxy : new Ext.data.HttpProxy({
			url : '../service/list.shtml'
		}),
		reader : new Ext.data.JsonReader({
			totalProperty : 'total',
			root : 'rows'
		}, [ {
			name : 'serviceId'
		}, {
			name : 'serviceName'
		},{
			name : 'serviceIdentify'
		},{
			name : 'serviceUrl'
		}, {
			name : 'returnType'
		}, {
			name : 'jobType'
		}, {
			name : 'createDate'
		}, {
			name : 'datasource'
		}, {
			name : 'timeout'
		}, {
			name : 'isCompress'
		}, {
			name : 'tableName'
		},{
			name : 'transName'
		}, {
			name : 'delimiter'
		} , {
			name : 'fields'
		} , {
			name : 'conditions'
		} ,{
			name : 'idDatabase'
		} , {
			name : 'interfaceDesc'
		} ])
	});

	// 翻页排序时带上查询条件
	store.on('beforeload', function() {
		this.baseParams = {

		};
	});

	var pagesize_combo = new Ext.form.ComboBox({
		name : 'pagesize',
		hiddenName : 'pagesize',
		typeAhead : true,
		triggerAction : 'all',
		lazyRender : true,
		mode : 'local',
		store : new Ext.data.ArrayStore({
			fields : [ 'value', 'text' ],
			data : [ [ 10, '10条/页' ], [ 20, '20条/页' ], [ 50, '50条/页' ],
					[ 100, '100条/页' ], [ 250, '250条/页' ], [ 500, '500条/页' ] ]
		}),
		valueField : 'value',
		displayField : 'text',
		value : '50',
		editable : false,
		width : 85
	});
	var number = parseInt(pagesize_combo.getValue());
	pagesize_combo.on("select", function(comboBox) {
		bbar.pageSize = parseInt(comboBox.getValue());
		number = parseInt(comboBox.getValue());
		store.reload({
			params : {
				start : 0,
				limit : bbar.pageSize
			}
		});
	});

	var bbar = new Ext.PagingToolbar({
		pageSize : number,
		store : store,
		displayInfo : true,
		displayMsg : '显示{0}条到{1}条,共{2}条',
		plugins : new Ext.ux.ProgressBarPager(), // 分页进度条
		emptyMsg : "没有符合条件的记录",
		items : [ '-', '&nbsp;&nbsp;', pagesize_combo ]
	});

	var grid = new Ext.grid.GridPanel({
		title : '<span class="commoncss">对外服务接口管理</span>',
		iconCls : 'configIcon',
		height : 500,
		// width:600,
		autoScroll : true,
		region : 'center',
		store : store,
		loadMask : {
			msg : '正在加载表格数据,请稍等...'
		},
		stripeRows : true,
		frame : true,
		cm : cm,
		sm : sm,
		tbar : [ {
			text : '添加接口',
			iconCls : 'addIcon',
			handler : function() {
				addInit();
			}
		},'-',{
			text : '编辑接口',
			iconCls : 'edit1Icon',
			handler : function() {
				editInit();
			}
		},'-',{
			text : '删除接口',
			iconCls : 'deleteIcon',
			handler : function() {
				deleteService();
			}
		}],
		bbar : bbar
	});
	store.load({
		params : {
			start : 0,
			limit : bbar.pageSize
		}
	});
	grid.on('rowdblclick', function(grid, rowIndex, event) {
		editInit();
	});
	grid.on('sortchange', function() {
		// grid.getSelectionModel().selectFirstRow();
	});

	bbar.on("change", function() {
		// grid.getSelectionModel().selectFirstRow();
	});

	/**
	 * 布局
	 */
	var viewport = new Ext.Viewport({
		layout : 'border',
		items : [ grid ]
	});

	/**
	 * 查询参数
	 */
	function queryParamItem() {
		store.load({
			params : {
				start : 0,
				limit : bbar.pageSize
			}
		});
	}

	//数据返回方式下拉列表
	var returnTypeData = [ [ 1, 'FTP' ],[ 2, 'Webservice' ]];
	returnTypeCombo = new Ext.form.ComboBox( {
		store : new Ext.data.SimpleStore( {
			fields : [ "returnType", "returnTypeName" ],
			data : returnTypeData
		}),
		valueField : "returnType",
		displayField : "returnTypeName",
		mode : 'local',
		forceSelection : true,
		hiddenName : 'returnType',
		emptyText : '数据返回方式',
		editable : false,
		value : 1,
		triggerAction : 'all',
		fieldLabel : '数据返回方式',
		autoWidth : true,
		name : 'returnType',
		listeners:{
		    select:function(combo,record,opts){
				var returnType = combo.getStore().getAt(combo.selectedIndex).data.returnType;
				//alert("returnType="+returnType);
				//reLoadData(returnType);
			}
		}
	});

  /*var trans = new Ext.data.Store({
		proxy : new Ext.data.HttpProxy({
			url : '../service/getJobList.shtml?returnType=0'
		}),
		autoLoad : true,
		reader : new Ext.data.JsonReader({}, [ 'transName', 'transDisplayName' ])
	});
	
	function reLoadData(returnType){
		transCombox.reset();
		if(returnType==1){
			trans.proxy=new Ext.data.HttpProxy({url:'../service/getJobList.shtml'});
		}else if(returnType==2){
			trans.proxy=new Ext.data.HttpProxy({url:'../service/getTransList.shtml'});
		}
		trans.load();
	}

	transCombox = new Ext.form.ComboBox({
		store : trans,
		valueField : "transName",
		mode : 'remote',
		displayField : "transDisplayName",
		forceSelection : true,
		emptyText : '作业流程名称',
		editable : false,
		allowBlank : false,
		triggerAction : 'all',
		anchor : '70%',
		fieldLabel : '作业流程名称',
		autoWidth : true,
		hiddenName : "transName",
		autoShow : true,
		selectOnFocus : true,
		name : "transName"
	});*/
	
	
	var trans = new Ext.data.Store({
		proxy : new Ext.data.HttpProxy({
			url : '../service/getDataSourceList.shtml?'
		}),
		autoLoad : true,
		reader : new Ext.data.JsonReader({}, [ 'idDatabase', 'dispalySourceName' ])
	});
	
	dbsCombox = new Ext.form.ComboBox({
		store : trans,
		valueField : "idDatabase",
		mode : 'remote',
		displayField : "dispalySourceName",
		forceSelection : true,
		emptyText : '数据源名称',
		editable : false,
		allowBlank : false,
		triggerAction : 'all',
		anchor : '70%',
		fieldLabel : '数据源',
		autoWidth : true,
		hiddenName : "idDatabase",
		autoShow : true,
		selectOnFocus : true,
		name : "idDatabase"
	});
	
	
	var servicePanel = new Ext.form.FormPanel({
		id : 'servicePanel',
		name : 'servicePanel',
		labelWidth : 100,
		defaultType : 'textfield',
		labelAlign : 'right',
		frame : false,
		bodyStyle : 'padding:5 5 0',
		items : [{
					fieldLabel : '服务接口名称',
					name : 'serviceName',
					id : 'serviceName',
					allowBlank : false,
					anchor : '70%'
				},returnTypeCombo, 
				  dbsCombox,
				{
						fieldLabel : '连接表名',
						name : 'tableName',
						id : 'tableName',
						allowBlank : false,
						anchor : '70%'
			    },
			    {
					fieldLabel : '输出字段',
					name : 'fields',
					id : 'fields',
					allowBlank : false,
					anchor : '70%'
				},
				{
					fieldLabel : '条件表达式',
					name : 'conditions',
					id : 'conditions',
					allowBlank : false,
					anchor : '70%'
				},
				{
					fieldLabel : '数据超时时间(小时)',
					name : 'timeout',
					id : 'timeout',
					allowBlank : false,
					anchor : '70%'
				}, {
					fieldLabel : '接口说明',
					name : 'interfaceDesc',
					id : 'interfaceDesc',
					allowBlank : true,
					xtype : 'textarea',
					width : 500, 
					height : 80, 
				},{
					id : 'datasource',
					name : 'datasource',
					hidden : true,
					value : ''
				},{
					id : 'serviceId',
					name : 'serviceId',
					hidden : true,
					value : '1'
				}, {
					id : 'windowmode',
					name : 'windowmode',
					hidden : true
				}]
	});
	
	
	var serviceWindow = new Ext.Window({
		layout : 'fit',
		width : 700,
		height : 450,
		resizable : false,
		draggable : true,
		closeAction : 'hide',
		title : '<span class="commoncss">对外服务接口维护对话框</span>',
		modal : true,
		collapsible : true,
		titleCollapse : true,
		maximizable : false,
		buttonAlign : 'right',
		border : false,
		animCollapse : true,
		pageY : 20,
		pageX : document.body.clientWidth / 2 - 420 / 2,
		animateTarget : Ext.getBody(),
		constrain : true,
		items : [ servicePanel ],
		buttons : [{
			text : '保存',
			iconCls : 'acceptIcon',
			handler : function() {
				var mode = Ext.getCmp('windowmode').getValue();
				if (mode == 'add')
					saveServiceItem();
				else
					updateServiceItem();
			}
		}, {
			text : '关闭',
			iconCls : 'deleteIcon',
			handler : function() {
				serviceWindow.hide();
			}
		}]
	});


	
	/**
	 * 新增参数初始化
	 */
	function addInit() {
		var flag = Ext.getCmp('windowmode').getValue();
		if (typeof(flag) != 'undefined') {
			servicePanel.form.getEl().dom.reset();
		} else {
			clearForm(servicePanel.getForm());
		}
		serviceWindow.show();
		serviceWindow.setTitle('<span class="commoncss">新增对外服务接口</span>');
		Ext.getCmp('windowmode').setValue('add');
	}

	/**
	 * 保存参数数据
	 */
	function saveServiceItem() {
		servicePanel.form.submit({
					url : '../service/saveService.shtml',
					waitTitle : '提示',
					method : 'POST',
					waitMsg : '正在处理数据,请稍候...',
					success : function(form, action) {
						serviceWindow.hide();
						store.reload();
						form.reset();
					},
					failure : function(form, action) {
						var msg = action.result.msg;
						Ext.MessageBox.alert('提示', '数据保存失败:<br>' + msg);
					}
				});
	}

	function editInit() {
		var record = grid.getSelectionModel().getSelected();
		if (Ext.isEmpty(record)) {
			Ext.MessageBox.alert('提示', '请先选中要修改的项目');
			return;
		}
		servicePanel.getForm().loadRecord(record);
		serviceWindow.show();
		serviceWindow
				.setTitle('<span class="commoncss">编辑对外服务接口</span>');
		Ext.getCmp('windowmode').setValue('edit');
		Ext.getCmp('btnReset').hide();
	}

	/**
	 * 修改参数数据
	 */
	function updateServiceItem() {
		if (!servicePanel.form.isValid()) {
			return;
		}
		update();
	}

	/**
	 * 更新
	 */
	function update() {
		
		servicePanel.form.submit({
					url : '../service/updateService.shtml',
					waitTitle : '提示',
					method : 'POST',
					waitMsg : '正在处理数据,请稍候...',
					success : function(form, action) {
						serviceWindow.hide();
						store.reload();
						form.reset();
					},
					failure : function(form, action) {
						var msg = action.result.msg;
						Ext.MessageBox.alert('提示', '数据修改失败:<br>' + msg);
					}
				});
	}
	
	
	/**
	 * 删除服务权限
	 */
	function  deleteService(){
		var rows = grid.getSelectionModel().getSelections();
		if(Ext.isEmpty(rows)){
			Ext.Msg.alert('提示', '请先选中要删除的项目!');
			return;
		}
		var strChecked = jsArray2JsString(rows,'serviceId');
		Ext.Msg.confirm('请确认', '确认删除选中的数据吗?', function(btn, text) {
			if(btn == 'yes'){
				Ext.Ajax.request({
					url : '../service/deleteService.shtml',
					success : function(response){
						var resultArray = Ext.util.JSON.decode(response.responseText);
						store.reload();
						Ext.Msg.alert('提示', resultArray.msg);
					},
					failure : function(response) {
						var resultArray = Ext.util.JSON.decode(response.responseText);
						Ext.Msg.alert('提示', resultArray.msg);
					},
					params : {
						strChecked : strChecked
					}
				});
			}
		});
	}
	
	
});