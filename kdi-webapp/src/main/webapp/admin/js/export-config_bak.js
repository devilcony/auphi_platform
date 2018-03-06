/**
 * 全局参数表管理
 * 
 */
Ext.onReady(function() {

	var cm = new Ext.grid.ColumnModel([ new Ext.grid.RowNumberer(), sm, {
		header : '配置编号',
		dataIndex : 'CONFIG_ID',
		hidden : true
	}, {
		header : '作业名称',
		dataIndex : 'TASK_NAME',
		width : 120
	}, {
		id : 'TABLE_NAME',
		header : '表名',
		dataIndex : 'TABLE_NAME',
		width : 120
	}, {
		header : '返回字段名称',
		dataIndex : 'FIELDS',
		width : 150
	}, {
		header : '结果分隔符',
		dataIndex : 'RESULT_SEP',
		width : 120
	}, {
		header : '是否增量抽取',
		dataIndex : 'IS_INCREMENT',
		renderer:function(v){
			if(v == 1){
				return "是";
			} else if(v == 0){
				return "否";
			}
		},
		width : 150
	}, {
		header : '增量字段',
		dataIndex : 'INCREMENTFIELD',
		width : 150
		
	}, {
		header : '开始时间',
		dataIndex : 'STARTTIME',
		width : 150
		
	}, {
		id : 'CONDITIONS',
		header : '查询条件',
		dataIndex : 'CONDITIONS',
		width : 150
		
	}, {
		id : 'CREATETIME',
		header : '创建时间',
		dataIndex : 'CREATETIME',
		width : 150
	} ]);

	//新增数据导出数据第一步
	//var sm = new Ext.grid.CheckboxSelectionModel();
	var addDatabase = new Ext.grid.ColumnModel([ new Ext.grid.RowNumberer(), sm, {
		header : '配置编号',
		dataIndex : 'CONFIG_ID',
		hidden : true
	}, {
		header : '作业名称',
		dataIndex : 'TASK_NAME',
		width : 120
	}, {
		id : 'TABLE_NAME',
		header : '表名',
		dataIndex : 'TABLE_NAME',
		width : 120
	}, {
		header : '返回字段名称',
		dataIndex : 'FIELDS',
		width : 150
	}, {
		header : '结果分隔符',
		dataIndex : 'RESULT_SEP',
		width : 120
	}, {
		header : '是否增量抽取',
		dataIndex : 'IS_INCREMENT',
		renderer:function(v){
			if(v == 1){
				return "是";
			} else if(v == 0){
				return "否";
			}
		},
		width : 150
	}, {
		header : '增量字段',
		dataIndex : 'INCREMENTFIELD',
		width : 150
		
	}, {
		header : '开始时间',
		dataIndex : 'STARTTIME',
		width : 150
		
	}, {
		id : 'CONDITIONS',
		header : '查询条件',
		dataIndex : 'CONDITIONS',
		width : 150
		
	}, {
		id : 'CREATETIME',
		header : '创建时间',
		dataIndex : 'CREATETIME',
		width : 150
	} ]);

	
	/**
	 * 数据存储
	 */
	var store = new Ext.data.Store({
		proxy : new Ext.data.HttpProxy({
			url : '../dataExport/list.shtml'
		}),
		reader : new Ext.data.JsonReader({
			totalProperty : 'total',
			root : 'rows'
		}, [ {
			name : 'CONFIG_ID'
		}, {
			name : 'TASK_NAME'
		}, {
			name : 'TABLE_NAME'
		}, {
			name : 'FIELDS'
		}, {
			name : 'RESULT_SEP'
		}, {
			name : 'IS_INCREMENT'
		}, {
			name : 'INCREMENTFIELD'
		}, {
			name : 'STARTTIME'
		}, {
			name : 'CONDITIONS'
		}, {
			name : 'CREATETIME'
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
		title : '<span class="commoncss">数据集市数据导出任务配置信息列表</span>',
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
		}
//		, '-', {
//			text : '任务运行信息',
//			iconCls : 'page_delIcon',
//			handler : function() {
//				viewTaskInfo();
//			}
//		}
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

	//var incComboData = [ [ 0, '否' ], [ 1, '是' ]];
	var incComboData = [ [  '否',0 ], [ '是', 1 ]];
	var incCombo = new Ext.form.ComboBox({
		store : new Ext.data.SimpleStore({
			fields : [ "IS_INCREMENT", "IS_INCREMENT_NAME" ],
			data : incComboData
		}),
//		valueField : "IS_INCREMENT",
//		displayField : "IS_INCREMENT_NAME",
		valueField : "IS_INCREMENT_NAME",
		displayField : "IS_INCREMENT",
		mode : 'local',
		forceSelection : true,
		hiddenName : 'p_IS_INCREMENT',
		emptyText : '是否增量抽取',
		editable : false,
		value : 0,
		triggerAction : 'all',
		fieldLabel : '是否增量抽取',
		//autoWidth : true,
		name : 'p_IS_INCREMENT',
		listeners:{ 
			   select:function(combo,record,opts) {  
				  var type = combo.getValue();
				  //alert(type);
			      if(type == 1){//是增量抽取
			    	  Ext.getCmp('p_INCREMENTFIELD').setDisabled(false);
			    	  Ext.getCmp('p_STARTTIME').setDisabled(false);
			      } else if(type == 0){//全量抽取
			    	  Ext.getCmp('p_INCREMENTFIELD').setValue('');
			    	  Ext.getCmp('p_STARTTIME').setValue('');
			    	  Ext.getCmp('p_INCREMENTFIELD').setDisabled(true);
			    	  Ext.getCmp('p_STARTTIME').setDisabled(true);
			      } 
			   }  
		}
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
			allowBlank : false,
			anchor : '99%'
		}, incCombo,{
			fieldLabel : '增量字段',
			name : 'p_INCREMENTFIELD',
			id : 'p_INCREMENTFIELD',
			allowBlank : true,
			anchor : '99%'
		},{
			fieldLabel : '开始时间',
			name : 'p_STARTTIME',
			id : 'p_STARTTIME',
			allowBlank: true,
			anchor : '99%'
		},{
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

//	var typeComboData = [ [ 0, '时间间隔' ], [ 1, '天' ], [ 2, '周' ], [ 3, '月' ]];
	var typeComboData = [ [1, '时间间隔(秒)' ],[2, '时间间隔(分钟)' ], [3, '时间间隔(小时)' ],[ 4, '天' ], [ 5, '月' ], [ 6, '周' ]];
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
//		autoWidth : true,
		anchor : '80%',
		name : 'scheduletype',
		listeners:{ 
			   select:function(combo,record,opts) {  
				  var type = combo.getValue();
				 
			      if(type == 1){//时间间隔（秒）
			    	  Ext.getCmp('p_SECOND').setDisabled(false);
			    	  Ext.getCmp('p_MINUTE').setDisabled(true);
			    	  Ext.getCmp('p_HOUR').setDisabled(true);
			    	  Ext.getCmp('p_VERYDAY').setDisabled(true);//=true;
			    	  //Ext.getCmp('p_VERYMONTH').setDisabled(true);//=true;
			    	  weekCombo.disable();
			    	  jobsmonthCombo.disable();
			      } else if(type == 2){//时间间隔（分钟）
			    	  Ext.getCmp('p_SECOND').setDisabled(false);
			    	  Ext.getCmp('p_MINUTE').setDisabled(false);
			    	  Ext.getCmp('p_HOUR').setDisabled(true);
			    	  Ext.getCmp('p_VERYDAY').setDisabled(true);//=true;
			    	  weekCombo.disable();
			    	  jobsmonthCombo.disable();
			    	  //Ext.getCmp('p_VERYMONTH').setDisabled(true);
			      } else if(type == 3){//时间间隔（小时）
			    	  Ext.getCmp('p_SECOND').setDisabled(false);
			    	  Ext.getCmp('p_MINUTE').setDisabled(false);
			    	  Ext.getCmp('p_HOUR').setDisabled(false);
					  Ext.getCmp('p_VERYDAY').setDisabled(true);//=true;
					  weekCombo.disable();
					  jobsmonthCombo.disable();
			      	  //Ext.getCmp('p_VERYMONTH').setDisabled(true);
			      } else if(type == 4){//每天
			    	  Ext.getCmp('p_SECOND').setDisabled(false);
			    	  Ext.getCmp('p_MINUTE').setDisabled(false);
					  Ext.getCmp('p_HOUR').setDisabled(false);//=true;
					  weekCombo.disable();
					  jobsmonthCombo.disable();
			      } else if(type == 5){//月
			    	  Ext.getCmp('p_SECOND').setDisabled(false);
			    	  Ext.getCmp('p_MINUTE').setDisabled(false);
			     	  Ext.getCmp('p_HOUR').setDisabled(false);//=true;
			     	  weekCombo.disable();
			     	  jobsmonthCombo.enable();
			      } else if(type == 6){//周
			    	  Ext.getCmp('p_SECOND').setDisabled(false);
			    	  Ext.getCmp('p_MINUTE').setDisabled(false);
			     	  Ext.getCmp('p_HOUR').setDisabled(false);//=true;
			     	  weekCombo.enable();
			     	  jobsmonthCombo.disable();
			      }
			    
			   }  
			  }  
	});
	
	
	var weekComboData = [ [ 1, '星期一' ], [ 2, '星期二' ],[ 3, '星期三' ], [ 4, '星期四' ],[ 5, '星期五' ],[ 6, '星期六' ],[ 7, '星期天' ]];
	weekCombo = new Ext.form.ComboBox({
		store : new Ext.data.SimpleStore({
			fields : [ "p_VERYWEEK", "p_WEEKNAME" ],
			data : weekComboData
		}),
		valueField : "p_VERYWEEK",
		displayField : "p_WEEKNAME",
		mode : 'local',
		forceSelection : true,
		value : '*',
		hiddenName : 'p_VERYWEEK',
		editable : false,
		disabled : true,
		triggerAction : 'all',
		fieldLabel : '周',
//		autoWidth : true,
		anchor : '80%',
		name : 'p_VERYWEEK'
	});
	
var jobsmonthData = [ [ 1, '1' ], [ 2, '2' ],[ 3, '3' ], [ 4, '4' ],[ 5, '5' ],[ 5, '6' ],[ 7, '7' ],[ 8, '8' ],[ 9, '9' ], [ 10, '10' ],[ 11, '11' ], [ 12, '12' ],[ 13, '13' ],[14, '14' ],[15, '15' ],[16, '16' ], [17, '17' ],[18, '18' ], [19, '19' ],[21, '21' ],[22, '22' ],[23, '23' ],[ 24, '24' ], [ 25, '25' ],[ 26, '26' ], [ 27, '27' ],[28, '28' ],[ 29, '29' ],[ 30, '30' ],[ 31, '31' ]];
	
	jobsmonthCombo = new Ext.form.ComboBox({
		store : new Ext.data.SimpleStore({
			fields : [ "jobsverymonth", "jobsverymonthName" ],
			data : jobsmonthData
		}),
		valueField : "jobsverymonth",
		displayField : "jobsverymonthName",
		mode : 'local',
		forceSelection : true,
		value : '*',
		hiddenName : 'jobsverymonth',
		editable : false,
		disabled : true,
		triggerAction : 'all',
		fieldLabel : '每月',
//		autoWidth : true,
		anchor : '80%',
		name : 'jobsverymonth'
	});
	
	var runTaskFormPanel = new Ext.form.FormPanel({
		id : 'runTaskFormPanel',
		name : 'runTaskFormPanel',
		defaultType : 'textfield',
		labelAlign : 'right',
		labelWidth : 140,
		frame : false,
		bodyStyle : 'padding:5 5 0',
		items : [ 
		/*new Ext.form.Checkbox({
			name : 're',
			boxLabel : '重复执行'
		}),*/{
			fieldLabel : '任务名称',
			name : 'p_TASKNAME',
			id : 'p_TASKNAME',
			anchor : '80%',
			value : ''
		},typeCombo,{
			fieldLabel : '以秒计算的时间间隔',
			name : 'p_SECOND',
			id : 'p_SECOND',
			allowBlank : false,
			anchor : '80%',
			value : 0
		}, {
			fieldLabel : '以分钟计算的时间间隔',
			name : 'p_MINUTE',
			id : 'p_MINUTE',
			anchor : '80%',
			value : 0
		},{
			fieldLabel : '以小时计算的时间间隔',
			name : 'p_HOUR',
			id : 'p_HOUR',
			anchor : '80%',
			value : '*'
		}, {
			fieldLabel : '天',
			name : 'p_VERYDAY',
			id : 'p_VERYDAY',
			disabled : true,
			allowBlank : false,
			value : '*',
			anchor : '80%'
		},weekCombo,jobsmonthCombo
//		{
//			fieldLabel : '月',
//			name : 'p_VERYMONTH',
//			disabled : true,
//			id : 'p_VERYMONTH',
//			value : 1,
//			anchor : '80%'
//		}
		,{
			id : 'jobName',
			name : 'jobName',
			hidden : true
		}, {
			id : 'jobParams',
			name : 'jobParams',
			hidden : true
		}, {
			id : 'jobParams',
			name : 'jobParams',
			hidden : true
		}, {
			id : 'jobConfigId',
			name : 'jobConfigId',
			hidden : true
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
			text : '运行',
			iconCls : 'acceptIcon',
			handler : function() {
				runQuartzTask();
				
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
		var record = grid.getSelectionModel().getSelected();
		if (Ext.isEmpty(record)) {
			Ext.MessageBox.alert('提示', '请先选中要执行的任务');
			return;
		}
		var jobId = record.get("CONFIG_ID");
		var name = record.get("TASK_NAME");
		Ext.getCmp('jobName').setValue(name);
		Ext.getCmp('jobConfigId').setValue(jobId);
		runTaskWindow.show();
		runTaskWindow.setTitle('<span class="commoncss">任务运行信息配置</span>');
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
		var strChecked = jsArray2JsString(rows, 'CONFIG_ID');
		Ext.Msg.confirm('请确认', '确认删除选中的配置任务信息吗?', function(btn, text) {
			if (btn == 'yes') {

				Ext.Ajax.request({
					url : '../dataExport/delete.shtml',
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
		var flag = record.get("IS_INCREMENT");//Ext.getCmp('p_IS_INCREMENT').getValue();
		if (flag !=null && flag == '0') {
			Ext.getCmp('p_INCREMENTFIELD').setValue(''); 
			Ext.getCmp('p_STARTTIME').setValue('');
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
		}, {
			name : 'p_INCREMENTFIELD',
			mapping : 'INCREMENTFIELD'
		}, {
			name : 'p_STARTTIME',
			mapping : 'STARTTIME'
		});
		copyRecord = new editRecord( {
			p_IS_INCREMENT : record.get("IS_INCREMENT"),
			p_INCREMENTFIELD : record.get("INCREMENTFIELD"),
			p_STARTTIME : record.get("STARTTIME"),
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
	
	
	/**
	 * 运行定时任务
	 */
	function runQuartzTask() {
		
		runTaskFormPanel.form.submit({
//			url : '../job/runTransTask.shtml',
			url : '../dataExport/runDataExportTask.shtml',
			waitTitle : '提示',
			method : 'POST',
			waitMsg : '正在处理数据,请稍候...',
			success : function(form, action) {
				Ext.MessageBox.alert('提示信息', action.result.msg);
				runTaskWindow.hide();
				store.reload();
				form.reset();
			},
			failure : function(form, action) {
				var msg = action.result.msg;
				Ext.MessageBox.alert('提示', '运行转换任务失败:<br>' + msg);
			}
		});
	}
	

	
	
	/**
	 * 查看任务运行信息
	 */
	var resultArray=[];
	var gridTask = null;
	function viewTaskInfo(){

		Ext.Ajax.request({
			async:false,
			url : '../JobProcessServlet?jobtype=100&action=query',
			success : function(response) {
				//Ext.Msg.alert('提示', response.responseText);
				resultArray = Ext.util.JSON.decode(response.responseText);
				//Ext.Msg.alert('提示', resultArray.msg);
				//dsTask.reload();
			    showStore(resultArray);
			},
			failure : function(response) {
				var resultArray = Ext.util.JSON.decode(response.responseText);
				Ext.Msg.alert('提示', resultArray.msg);
			}
//			params : {
//				strChecked : strChecked
//			}
		});
		
		function showStore(resultArray){
			var dsTask = new Ext.data.Store({
				proxy: new Ext.data.MemoryProxy(resultArray),
				reader: new Ext.data.JsonReader({}, 
					['TRIGGER_NAME','JOB_NAME','DISPLAY_NAME','TRIGGER_GROUP',
					 'NEXT_FIRE_TIME','PREV_FIRE_TIME','PRIORITY','STATU','TRIGGER_STATE',
					 'TRIGGER_TYPE','START_TIME','END_TIME'
					 ]
				 )
			});
			dsTask.load();
			
			
			var sm1 = new Ext.grid.CheckboxSelectionModel();
			gridTask = new Ext.grid.GridPanel({
				iconCls : 'configIcon',
				tbar : [ {
					text : '暂停',
					iconCls : 'page_addIcon',
					handler : function() {
						doCmd();
					}
				}, '-', {
					text : '恢复',
					iconCls : 'page_edit_1Icon',
					handler : function() {
						doCmd();
					}
				}, '-', {
					text : '删除',
					iconCls : 'page_delIcon',
					handler : function() {
						doCmd();
					}
				} ],
				autoScroll : true,
				region : 'center',
				frame : true,
				//cm : cm,
				sm :sm1,
				height : 500,
				width : "100%",
				clickstoEdit : 1,
				store : dsTask,
				loadMask : {
					msg : '正在加载表格数据,请稍等...'
				},
				columns : [sm1,
				{
					header : "Trigger 名称",
					dataIndex : 'TRIGGER_NAME',
					name :	'TRIGGER_NAME'
				},{
					header : "Job/Trans 名称",
					dataIndex : 'JOB_NAME',
					name :	'JOB_NAME'
				},{
					header : "Job全名",
					dataIndex : 'DISPLAY_NAME',
					name :	'DISPLAY_NAME'
				},{
					header : "Trigger 分组",
					dataIndex : 'TRIGGER_GROUP',
					name :	'TRIGGER_GROUP'
				},{
					header : "下次执行时间",
					dataIndex : 'NEXT_FIRE_TIME',
					name :	'NEXT_FIRE_TIME'
				},{
					header : " 上次执行时间",
					dataIndex : 'PREV_FIRE_TIME',
					name :	'PREV_FIRE_TIME'
				},{
					header : "优先级",
					dataIndex : 'PRIORITY',
					name :	'PRIORITY'
				},{
					header : "Trigger 状态",
					dataIndex : 'STATU',
					name :	'STATU'
				},{
					header : "Trigger 类型",
					dataIndex : 'TRIGGER_TYPE',
					name :	'TRIGGER_TYPE'
				},{
					header : "开始时间",
					dataIndex : 'START_TIME',
					name :	'START_TIME'
				},{
					header : " 结束时间",
					dataIndex : 'END_TIME',
					name :	'END_TIME'
				},{
					header : " 状态",
					dataIndex : 'TRIGGER_STATE',
					name :	'TRIGGER_STATE'
//					hidden:true
				}
				]
				 
			});
			
			
			var showTaskWindow = new Ext.Window({
				layout : 'fit',
				width : 700,
				height : 400,
				resizable : true,
				draggable : true,
				closeAction : 'hide',
				modal : true,
				collapsible : true,
				titleCollapse : true,
				maximizable : false,
				buttonAlign : 'right',
				border : false,
				animCollapse : true,
				pageY : 20,
				pageX : document.body.clientWidth / 2 - 220 / 2,
				animateTarget : Ext.getBody(),
				constrain : true,
				items : [ gridTask ]
				
			});
			
			
			
			
			
			showTaskWindow.show();
			showTaskWindow.setTitle('<span class="commoncss">数据导出任务运行信息列表</span>');
		}
	}
	
	
	
	function doCmd(state,triggerName,group,triggerState){
		var record = gridTask.getSelectionModel().getSelected();
		if (Ext.isEmpty(record)) {
			Ext.MessageBox.alert('提示', '请先选中要执行的任务信息');
			return;
		}
		var triggerName =record.get("TRIGGER_NAME");
		var group =record.get("TRIGGER_GROUP");
		var state =record.get("TRIGGER_STATE");
		if(state == 'pause' && state=='PAUSED'){
			alert("该Trigger己经暂停！");
			return;
		}
		
	    if(state == 'resume' && state != 'PAUSED'){
			alert("该Trigger正在运行中！");
			return;
		}
		
		//客户端两次编码，服务端再解码，否测中文乱码 
		triggerName = encodeURIComponent(encodeURIComponent(triggerName));
		group = encodeURIComponent(encodeURIComponent(group));
		Ext.Ajax.request({
            url: '../JobProcessServlet?jobtype=200&action='+state+'&triggerName='+triggerName+'&group='+group,
            type: 'post',
            //dataType: 'xml',
           // timeout: 3000,
            error: function(){
               alert("执行失败！");		
            },
            success: function(xml){
				if (xml == 0) {
					alert("执行成功！");
					window.location.reload();
				}else{
					alert("执行失败！");	
				}		   
            }
        });
	}
	
	
	
	
	
	
	
	
	
});