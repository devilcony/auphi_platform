/**
 * 全局参数表管理
 * 
 */
Ext.onReady(function() {

	var sm = new Ext.grid.CheckboxSelectionModel();
	var cm = new Ext.grid.ColumnModel([ new Ext.grid.RowNumberer(), sm, {
		header : '增量编号',
		dataIndex : 'INCREMENT_ID',
		hidden : true
	}, {
		id : 'CONFIG_ID',
		header : '配置编号',
		dataIndex : 'CONFIG_ID',
		width : 150
	}, {
		id : 'ROWNUM',
		header : '行号',
		dataIndex : 'ROWNUM',
		width : 150
	}, {
		id : 'LAST_DATE',
		header : '结束时间',
		dataIndex : 'LAST_DATE',
		width : 150,
		sortable:true
	} ]);

	/**
	 * 数据存储
	 */
	var store = new Ext.data.Store({
		proxy : new Ext.data.HttpProxy({
			url : '../dataExportIncrement/list.shtml'
		}),
		reader : new Ext.data.JsonReader({
			totalProperty : 'total',
			root : 'rows'
		}, [ {
			name : 'INCREMENT_ID'
		}, {
			name : 'CONFIG_ID'
		}, {
			name : 'ROWNUM'
		}, {
			name : 'LAST_DATE'
		} ])
	});

	// 翻页排序时带上查询条件
	store.on('beforeload', function() {
		
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
		title : '<span class="commoncss">数据集市数据导出增量抽取信息列表</span>',
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
		tbar : [ /**{
			text : '新增',
			iconCls : 'page_addIcon',
			handler : function() {
				addInit();
			}
		}, '-', {
			text : '修改',
			iconCls : 'page_edit_1Icon',
			handler : function() {
				editInit();
			}
		}, '-', {
			text : '删除',
			iconCls : 'page_delIcon',
			handler : function() {
				deleteParamItems();
			}
		}, '-', {
			text : '运行任务',
			iconCls : 'page_delIcon',
			handler : function() {
				runTask();
			}
		}, '-', {
			text : '任务运行信息',
			iconCls : 'page_delIcon',
			handler : function() {
				viewTaskInfo();
			}
		} , '-', 
		{
			text : '查看信息',
			iconCls : 'page_delIcon',
			handler : function() {
				viewIncrementInfo();
			}
		} */
		],
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

	var incComboData = [ [ 0, '否' ], [ 1, '是' ]];
	incCombo = new Ext.form.ComboBox({
		store : new Ext.data.SimpleStore({
			fields : [ "IS_INCREMENT", "IS_INCREMENT_NAME" ],
			data : incComboData
		}),
		valueField : "IS_INCREMENT",
		displayField : "IS_INCREMENT_NAME",
		mode : 'local',
		forceSelection : true,
		hiddenName : 'IS_INCREMENT',
		emptyText : '是否增量抽取',
		editable : false,
		value : 0,
		triggerAction : 'all',
		fieldLabel : '是否增量抽取',
		autoWidth : true,
		name : ''
	});



	var addParamFormPanel = new Ext.form.FormPanel({
		id : 'addParamFormPanel',
		name : 'addParamFormPanel',
		defaultType : 'textfield',
		labelAlign : 'right',
		labelWidth : 100,
		frame : false,
		bodyStyle : 'padding:5 5 0',
		items : [ {
			fieldLabel : '配置名称',
			name : 'p_TASK_NAME',
			id : 'p_TASK_NAME',
			allowBlank : false,
			anchor : '99%'
		}, {
			fieldLabel : '表名',
			name : 'p_TABLE_NAME',
			id : 'p_TABLE_NAME',
			anchor : '99%'
		}, incCombo,{
			fieldLabel : '结果分隔符',
			name : 'p_RESULT_SEP',
			id : 'p_RESULT_SEP',
			allowBlank : false,
			anchor : '99%'
		}, {
			fieldLabel : '查询条件',
			name : 'p_CONDITIONS',
			id : 'p_CONDITIONS',
			anchor : '99%'
		},  {
			fieldLabel : '返回字段',
			name : 'p_FIELDS',
			id : 'p_FIELDS',
			anchor : '99%'
		}, {
			id : 'p_CONFIG_ID',
			name : 'p_CONFIG_ID',
			hidden : true
		}, {
			id : 'windowmode',
			name : 'windowmode',
			hidden : true
		} ]
	});

	var addParamWindow = new Ext.Window({
		layout : 'fit',
		width : 400,
		height : 300,
		resizable : false,
		draggable : true,
		closeAction : 'hide',
		title : '<span class="commoncss">新增数据导出任务配置</span>',
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
		items : [ addParamFormPanel ],
		buttons : [ {
			text : '保存',
			iconCls : 'acceptIcon',
			handler : function() {
				var mode = Ext.getCmp('windowmode').getValue();
				if (mode == 'add')
					saveParamItem();
				else
					updateParamItem();
			}
		}, {
			text : '重置',
			id : 'btnReset',
			iconCls : 'tbar_synchronizeIcon',
			handler : function() {
				clearForm(addParamFormPanel.getForm());
			}
		}, {
			text : '关闭',
			iconCls : 'deleteIcon',
			handler : function() {
				addParamWindow.hide();
			}
		} ]
	});


	/**
	 * 布局
	 */
	var viewport = new Ext.Viewport({
		layout : 'border',
		items : [ grid ]
	});

	
	var typeComboData = [ [ 0, '时间间隔' ], [ 1, '天' ], [ 2, '周' ], [ 3, '月' ]];
	typeCombo = new Ext.form.ComboBox({
		store : new Ext.data.SimpleStore({
			fields : [ "scheduletype", "scheduletypeName" ],
			data : typeComboData
		}),
		valueField : "scheduletype",
		displayField : "scheduletypeName",
		mode : 'local',
		forceSelection : true,
		value : 0,
		hiddenName : 'scheduletype',
		editable : false,
		triggerAction : 'all',
		fieldLabel : '调度类型',
		autoWidth : true,
		anchor : '80%',
		name : 'scheduletype',
		listeners:{ 
			   select:function(combo,record,opts) {  
				  var type = combo.getValue();
				 
			      if(type == 0){//时间间隔
			    	  Ext.getCmp('second').setDisabled(false);
			    	  Ext.getCmp('minute').setDisabled(false);
			    	  Ext.getCmp('veryDay').setDisabled(true);//=true;
			    	  weekCombo.disable();
			    	  Ext.getCmp('veryMonth').setDisabled(true);
			      } else if(type == 1){//按天运行
			    	  Ext.getCmp('second').setDisabled(true);
			    	  Ext.getCmp('minute').setDisabled(true);
			    	  Ext.getCmp('veryDay').setDisabled(false);//=true;
			    	  weekCombo.disable();
			    	  Ext.getCmp('veryMonth').setDisabled(true);
			      } else if(type == 2){//每周
			    	  Ext.getCmp('second').setDisabled(true);
			    	  Ext.getCmp('minute').setDisabled(true);
					  Ext.getCmp('veryDay').setDisabled(false);//=true;
					  weekCombo.enable();
			      	  Ext.getCmp('veryMonth').setDisabled(true);
			      } else if(type == 3){//每月
			    	  Ext.getCmp('second').setDisabled(true);
			    	  Ext.getCmp('minute').setDisabled(true);
			     	  Ext.getCmp('veryDay').setDisabled(false);//=true;
			     	  weekCombo.disable();
			    	  Ext.getCmp('veryMonth').setDisabled(false);
			      }
			    
			   }  
			  }  
	});
	
	
	var weekComboData = [ [ 1, '星期一' ], [ 2, '星期二' ],[ 3, '星期三' ], [ 4, '星期四' ],[ 5, '星期五' ],[ 6, '星期六' ],[ 7, '星期天' ]];
	weekCombo = new Ext.form.ComboBox({
		store : new Ext.data.SimpleStore({
			fields : [ "veryWeek", "weekName" ],
			data : weekComboData
		}),
		valueField : "veryWeek",
		displayField : "weekName",
		mode : 'local',
		forceSelection : true,
		value : 1,
		hiddenName : 'veryWeek',
		editable : false,
		disabled : true,
		triggerAction : 'all',
		fieldLabel : '每周',
		autoWidth : true,
		anchor : '80%',
		name : 'veryWeek'
	});
	
	var runTaskFormPanel = new Ext.form.FormPanel({
		id : 'runTaskFormPanel',
		name : 'runTaskFormPanel',
		defaultType : 'textfield',
		labelAlign : 'right',
		labelWidth : 140,
		frame : false,
		bodyStyle : 'padding:5 5 0',
		items : [ new Ext.form.Checkbox({
			name : 're',
			boxLabel : '重复执行'
		}),typeCombo,{
			fieldLabel : '以秒计算的时间间隔',
			name : 'second',
			id : 'second',
			allowBlank : false,
			anchor : '80%',
			value : 0
		}, {
			fieldLabel : '以分钟计算的时间间隔',
			name : 'minute',
			id : 'minute',
			anchor : '80%',
			value : 0
		}, {
			fieldLabel : '每天',
			name : 'veryDay',
			id : 'veryDay',
			disabled : true,
			allowBlank : false,
			value : '13:00',
			anchor : '80%'
		},weekCombo,{
			fieldLabel : '每月',
			name : 'veryMonth',
			disabled : true,
			id : 'veryMonth',
			value : 1,
			anchor : '80%'
		}]
	});

	var runTaskWindow = new Ext.Window({
		layout : 'fit',
		width : 400,
		height : 300,
		resizable : false,
		draggable : true,
		closeAction : 'hide',
		title : '<span class="commoncss">任务运行配置</span>',
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
		items : [ runTaskFormPanel ],
		buttons : [ {
			text : '保存',
			iconCls : 'acceptIcon',
			handler : function() {
			}
		}, {
			text : '关闭',
			iconCls : 'deleteIcon',
			handler : function() {
				runTaskWindow.hide();
			}
		} ]
	});
	
	/**
	 * 运行任务
	 */
	function runTask(){
		runTaskWindow.show();
		runTaskWindow.setTitle('<span class="commoncss">任务运行配置</span>');
	}
	
	/**
	 * 查看任务运行信息
	 */
	function viewTaskInfo(){
		
	}
	

	/**
	 * 新增参数初始化
	 */
	function addInit() {
		Ext.getCmp('btnReset').hide();
		var flag = Ext.getCmp('windowmode').getValue();
		if (typeof (flag) != 'undefined') {
			addParamFormPanel.form.getEl().dom.reset();
		} else {
			clearForm(addParamFormPanel.getForm());
		}
		addParamWindow.show();
		addParamWindow.setTitle('<span class="commoncss">新增数据导出任务配置</span>');
		Ext.getCmp('windowmode').setValue('add');

	}

	/**
	 * 保存参数数据
	 */
	function saveParamItem() {

		addParamFormPanel.form.submit({
			url : '../dataExport/save.shtml',
			waitTitle : '提示',
			method : 'POST',
			waitMsg : '正在处理数据,请稍候...',
			success : function(form, action) {
				addParamWindow.hide();
				store.reload();
				form.reset();
			},
			failure : function(form, action) {
				var msg = action.result.msg;
				Ext.MessageBox.alert('提示', '数据保存失败:<br>' + msg);
			}
		});
	}

	/**
	 * 删除参数
	 */
	function deleteParamItems() {
		var rows = grid.getSelectionModel().getSelections();
		if (Ext.isEmpty(rows)) {
			Ext.Msg.alert('提示', '请先选中要删除的项目!');
			return;
		}
		var strChecked = jsArray2JsString(rows, 'contactid');
		Ext.Msg.confirm('请确认', '确认删除选中的配置任务信息吗?', function(btn, text) {
			if (btn == 'yes') {

				Ext.Ajax.request({
					url : '../dataExport/deletet.shtml',
					success : function(response) {
						var resultArray = Ext.util.JSON
								.decode(response.responseText);
						store.reload();
						Ext.Msg.alert('提示', resultArray.msg);
					},
					failure : function(response) {
						var resultArray = Ext.util.JSON
								.decode(response.responseText);
						Ext.Msg.alert('提示', resultArray.msg);
					},
					params : {
						strChecked : strChecked
					}
				});
			}
		});
	}

	/**
	 * 修改参数初始化
	 */
	function editInit() {
		var record = grid.getSelectionModel().getSelected();
		if (Ext.isEmpty(record)) {
			Ext.MessageBox.alert('提示', '请先选中要修改的配置任务信息');
			return;
		}
		var editRecord = Ext.data.Record.create( {
			name : 'p_TASK_NAME',
			mapping : 'TASK_NAME'
		}, {
			name : 'p_CONFIG_ID',
			mapping : 'CONFIG_ID'
		}, {
			name : 'p_TABLE_NAME',
			mapping : 'TABLE_NAME'
		}, {
			name : 'p_FIELDS',
			mapping : 'FIELDS'
		}, {
			name : 'p_RESULT_SEP',
			mapping : 'RESULT_SEP'
		}, {
			name : 'p_CONDITIONS',
			mapping : 'CONDITIONS'
		}, {
			name : 'p_IS_INCREMENT',
			mapping : 'IS_INCREMENT'
		});
		copyRecord = new editRecord( {
			p_IS_INCREMENT : record.get("IS_INCREMENT"),
			p_CONDITIONS : record.get("CONDITIONS"),
			p_RESULT_SEP:record.get("RESULT_SEP"),
			p_TABLE_NAME : record.get("TABLE_NAME"),
			p_FIELDS : record.get("FIELDS"),
			p_CONFIG_ID : record.get("CONFIG_ID"),
			p_TASK_NAME : record.get("TASK_NAME")
				});
		
		addParamFormPanel.getForm().loadRecord(copyRecord);
		addParamWindow.show();
		addParamWindow.setTitle('<span class="commoncss">修改数据导出任务配置</span>');
		Ext.getCmp('windowmode').setValue('edit');
		Ext.getCmp('btnReset').hide();
	}

	/**
	 * 修改参数数据
	 */
	function updateParamItem() {
		if (!addParamFormPanel.form.isValid()) {
			return;
		}
		update();
	}

	/**
	 * 更新
	 */
	function update() {
		addParamFormPanel.form.submit({
			url : '../dataExport/update.shtml',
			waitTitle : '提示',
			method : 'POST',
			waitMsg : '正在处理数据,请稍候...',
			success : function(form, action) {
				addParamWindow.hide();
				store.reload();
				form.reset();
			},
			failure : function(form, action) {
				var msg = action.result.msg;
				Ext.MessageBox.alert('提示', '数据修改失败:<br>' + msg);
			}
		});
	}
});