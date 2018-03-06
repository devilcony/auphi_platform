Ext.onReady(function() {

	var sm = new Ext.grid.CheckboxSelectionModel();
	var cm = new Ext.grid.ColumnModel([ new Ext.grid.RowNumberer(), sm, {
		header : '作业编号',
		dataIndex : 'taskId',
		hidden : true
	}, {
		id : 'name',
		header : '作业名称',
		dataIndex : 'name',
		width : 120
	}, {
		header : '作业类型',
		dataIndex : 'type',
		width : 120,
		renderer : function(v) {
			if (v == 1) {
				return "转换";
			} else if (v == 0) {
				return "作业";
			}
		}
	}, {
		header : '创建时间',
		dataIndex : 'createDate',
		width : 160,
		renderer : function(_v) {
			var _date = Ext.util.Format.date(_v, "Y-m-d H:m:s");
			return _date;
		}
	}, {
		header : '创建人',
		dataIndex : 'modifiedUser',
		width : 120
	}, {
		header : '修改时间',
		dataIndex : 'modifiedDate',
		width : 160,
		renderer : function(_v) {
			var _date = Ext.util.Format.date(_v, "Y-m-d H:m:s");
			return _date;
		}
	} ]);

	/**
	 * 数据存储
	 */
	var store = new Ext.data.Store({
		proxy : new Ext.data.HttpProxy({
			url : '../job/list.shtml'
		}),
		reader : new Ext.data.JsonReader({
			totalProperty : 'total',
			root : 'rows'
		}, [ {
			name : 'taskId'
		}, {
			name : 'name'
		}, {
			name : 'type'
		}, {
			name : 'createDate',
			type : 'date',
			mapping : 'createDate.time',
			dateFormat : 'time'
		}, {
			name : 'modifiedUser'
		}, {
			name : 'modifiedDate',
			type : 'date',
			mapping : 'modifiedDate.time',
			dateFormat : 'time'
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
		title : '<span class="commoncss">作业任务管理</span>',
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
			text : '运行任务',
			iconCls : 'page_addIcon',
			handler : function() {
				runTask();
			}
		} ],
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

	var slaves = new Ext.data.Store({
		proxy : new Ext.data.HttpProxy({
			url : '../job/getSlaveList.shtml'
		}),
		autoLoad : true,
		reader : new Ext.data.JsonReader({}, [ 'slave', 'slaveName' ])
	});

	salveCombox = new Ext.form.ComboBox({
		store : slaves,
		valueField : "slave",
		mode : 'remote',
		displayField : "slaveName",
		forceSelection : true,
		emptyText : '远程主机',
		editable : false,
		allowBlank : false,
		triggerAction : 'all',
		fieldLabel : '远程主机',
		autoWidth : true,
		hiddenName : "slave",
		autoShow : true,
		selectOnFocus : true,
		name : "slave"
	});

	var myData = [ ];

	var ds = new Ext.data.Store({
		reader : new Ext.data.ArrayReader({}, [ {
			name : 'paramName'
		}, {
			name : 'paramValue'
		} ])
	});
	ds.loadData(myData);

	var colModel = new Ext.grid.ColumnModel([ {
		id : 'paramName',
		header : "参数名称",
		width : 100,
		sortable : true,
		locked : false,
		dataIndex : 'paramName',
		editor : new Ext.form.TextField({
			allowBlank : false
		})
	}, {
		header : "参数值",
		id : 'paramValue',
		width : 100,
		sortable : true,
		dataIndex : 'paramValue',
		editor : new Ext.form.TextField({
			allowBlank : false
		})
	} ]);

	
	var param_grid = new Ext.grid.EditorGridPanel({
		ds : ds,
		cm : colModel,
		autoExpandColumn : 'paramName',
		height : 350,
		title : '转换运行参数设置',
		border : true,
        clicksToEdit: 1,
        tbar: [{
            text: '添加参数',
            handler : function(){
                // access the Record constructor through the grid's store
                var Plant = param_grid.getStore().recordType;
                var p = new Plant({
                	paramName: 'param name',
                	paramValue: 'param value'
                });
                param_grid.stopEditing();
                ds.insert(0, p);
                param_grid.startEditing(0, 0);
            }
        }]
    });

	
	
	var transtypeComboData = [ [1, '时间间隔(秒)' ],[2, '时间间隔(分钟)' ], [ 3, '天' ], [ 4, '月' ], [ 5, '周' ]];
	transtypeCombo = new Ext.form.ComboBox({
		store : new Ext.data.SimpleStore({
			fields : [ "transscheduletype", "transscheduletypeName" ],
			data : transtypeComboData
		}),
		valueField : "transscheduletype",
		displayField : "transscheduletypeName",
		mode : 'local',
		forceSelection : true,
		value : 1,
		hiddenName : 'transscheduletype',
		editable : false,
		triggerAction : 'all',
		fieldLabel : '调度类型',
//		autoWidth : true,
		anchor : '80%',
		name : 'transscheduletype',
		listeners:{ 
			   select:function(combo,record,opts) {  
				  var type = combo.getValue();
				 
			      if(type == 1){//时间间隔
			    	  Ext.getCmp('transsecond').setDisabled(false);
			    	  Ext.getCmp('transminute').setDisabled(false);
			    	  Ext.getCmp('transhour').setDisabled(true);//=true;
			    	  transweekCombo.disable();
			    	  transmonthCombo.disable();
			      } else if(type == 2){//按分钟运行
			    	  Ext.getCmp('transsecond').setDisabled(true);
			    	  Ext.getCmp('transminute').setDisabled(false);
			    	  Ext.getCmp('transhour').setDisabled(true);//=true;
			    	  transweekCombo.disable();
			    	  transmonthCombo.disable();
			      } else if(type == 3){//按天运行
			    	  Ext.getCmp('transsecond').setDisabled(true);
			    	  Ext.getCmp('transminute').setDisabled(true);
			    	  Ext.getCmp('transhour').setDisabled(false);//=true;
			    	  transweekCombo.disable();
			    	  transmonthCombo.disable();
			      } else if(type == 5){//每周
			    	  Ext.getCmp('transsecond').setDisabled(true);
			    	  Ext.getCmp('transminute').setDisabled(true);
					  Ext.getCmp('transhour').setDisabled(false);//=true;
					  transweekCombo.enable();
					  transmonthCombo.disable();
			      } else if(type == 4){//每月
			    	  Ext.getCmp('transsecond').setDisabled(true);
			    	  Ext.getCmp('transminute').setDisabled(true);
			     	  Ext.getCmp('transhour').setDisabled(false);//=true;
			     	  transweekCombo.disable();
			     	  transmonthCombo.enable();
			      }
			    
			   }  
			  }  
	});
	
	
	var transweekComboData = [ [ 2, '星期一' ], [ 3, '星期二' ],[ 4, '星期三' ], [ 5, '星期四' ],[ 6, '星期五' ],[ 7, '星期六' ],[ 1, '星期天' ]];
	transweekCombo = new Ext.form.ComboBox({
		store : new Ext.data.SimpleStore({
			fields : [ "transveryWeek", "transweekName" ],
			data : transweekComboData
		}),
		valueField : "transveryWeek",
		displayField : "transweekName",
		mode : 'local',
		forceSelection : true,
		value : 1,
		hiddenName : 'transveryWeek',
		editable : false,
		disabled : true,
		triggerAction : 'all',
		fieldLabel : '每周',
//		autoWidth : true,
		anchor : '80%',
		name : 'transveryWeek'
	});

	var transmonthData = [ [ 1, '1' ], [ 2, '2' ],[ 3, '3' ], [ 4, '4' ],[ 5, '5' ],[ 5, '6' ],[ 7, '7' ],[ 8, '8' ],[ 9, '9' ], [ 10, '10' ],[ 11, '11' ], [ 12, '12' ],[ 13, '13' ],[14, '14' ],[15, '15' ],[16, '16' ], [17, '17' ],[18, '18' ], [19, '19' ],[21, '21' ],[22, '22' ],[23, '23' ],[ 24, '24' ], [ 25, '25' ],[ 26, '26' ], [ 27, '27' ],[28, '28' ],[ 29, '29' ],[ 30, '30' ],[ 31, '31' ]];
	
	transmonthCombo = new Ext.form.ComboBox({
		store : new Ext.data.SimpleStore({
			fields : [ "transverymonth", "transverymonthName" ],
			data : transmonthData
		}),
		valueField : "transverymonth",
		displayField : "transverymonthName",
		mode : 'local',
		forceSelection : true,
		value : 1,
		hiddenName : 'transverymonth',
		editable : false,
		disabled : true,
		triggerAction : 'all',
		fieldLabel : '每月',
//		autoWidth : true,
		anchor : '80%',
		name : 'transverymonth'
	});

	
	
	
	var runTransDialogPanel = new Ext.form.FormPanel({
		id : 'runTransTaskDialog',
		name : 'runTransTaskDialog',
		labelWidth : 100,
		frame : true,
		labelAlign : 'left',
		bodyStyle : 'padding:5px',
		width : 750,
		layout : 'column',
		items : [
				{
					columnWidth : 0.40,
					layout : 'fit',
					items : [param_grid]
				},
				{
					columnWidth : 0.6,
					xtype : 'fieldset',
					title : '运行信息配置',
					labelWidth : 90,
					defaults : {
						width : 160,
						border : false
					},
					defaultType : 'textfield',
					autoHeight : true,
					bodyStyle : Ext.isIE ? 'padding:0 0 5px 15px;'
							: 'padding:10px 15px;',
					border : false,
					items : [ {
						xtype : 'radiogroup',
						fieldLabel : '运行方式',
						allowBlank : false,
						anchor : '99%',
						items : [ {
							boxLabel : '远程运行',
							name : 'rb-auto',
							inputValue : 1
						}, {
							boxLabel : '集群运行',
							name : 'rb-auto',
							inputValue : 2
						} ],
						listeners : {
							'change' : function(group, checked) {
								if (checked.inputValue == 1) {
									salveCombox.enable();
								} else {
									salveCombox.disable();
								}
							}
						}
					}, salveCombox, {
						fieldLabel : '任务名称',
						name : 'transTaskName',
						id : 'transTaskName',
						allowBlank : false,
						anchor : '99%'
					}, transtypeCombo,{
						fieldLabel : '以秒计算的时间间隔',
						name : 'transsecond',
						id : 'transsecond',
						allowBlank : false,
						anchor : '80%',
						value : 10
					},{
						fieldLabel : '以分钟计算的时间间隔',
						name : 'transminute',
						id : 'transminute',
						anchor : '80%',
						value : 1
					}, {
						fieldLabel : '每天',
						name : 'transhour',
						id : 'transhour',
						disabled : true,
						allowBlank : false,
						value : '13:00',
						anchor : '80%'
					},transweekCombo,transmonthCombo,{
						id : 'transName',
						name : 'transName',
						hidden : true
					}, {
						id : 'transParams',
						name : 'transParams',
						hidden : true
					}, {
						id : 'transConfigId',
						name : 'transConfigId',
						hidden : true
					} ]
				} ]
	});

	var runTransWindow = new Ext.Window({
		layout : 'fit',
		width : 700,
		height : 400,
		resizable : false,
		draggable : true,
		closeAction : 'hide',
		title : '<span class="commoncss">转换任务运行设置对话框</span>',
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
		items : [ runTransDialogPanel ],
		buttons : [ {
			text : '运行',
			iconCls : 'acceptIcon',
			handler : function() {
				runTransTask();
			}
		}, {
			text : '关闭',
			iconCls : 'deleteIcon',
			handler : function() {
				runTransWindow.hide();
			}
		} ]
	});

	var jobSlaves = new Ext.data.Store({
		proxy : new Ext.data.HttpProxy({
			url : '../job/getSlaveList.shtml'
		}),
		autoLoad : true,
		reader : new Ext.data.JsonReader({}, [ 'slave', 'slaveName' ])
	});

	jobSlavesCombox = new Ext.form.ComboBox({
		store : jobSlaves,
		valueField : "slave",
		mode : 'remote',
		displayField : "slaveName",
		forceSelection : true,
		emptyText : '远程主机',
		editable : false,
		triggerAction : 'all',
		fieldLabel : '远程主机',
		autoWidth : true,
		hiddenName : "slave",
		autoShow : true,
		selectOnFocus : true,
		allowBlank : false,
		name : "slave"
	});
	
	var jobMyData = [ ];

	var jobDs = new Ext.data.Store({
		reader : new Ext.data.ArrayReader({}, [ {
			name : 'jobParamName'
		}, {
			name : 'jobParamValue'
		} ])
	});
	ds.loadData(jobMyData);

	var jobColModel = new Ext.grid.ColumnModel([ {
		id : 'jobParamName',
		header : "参数名称",
		width : 100,
		sortable : true,
		locked : false,
		dataIndex : 'jobParamName',
		editor : new Ext.form.TextField({
			allowBlank : false
		})
	}, {
		header : "参数值",
		id : 'jobParamValue',
		width : 100,
		sortable : true,
		dataIndex : 'jobParamValue',
		editor : new Ext.form.TextField({
			allowBlank : false
		})
	} ]);


	var job_param_grid = new Ext.grid.EditorGridPanel({
		ds : jobDs,
		cm : jobColModel,
		autoExpandColumn : 'jobParamName',
		height : 350,
		title : '作业运行参数设置',
		border : true,
        clicksToEdit: 1,
        tbar: [{
            text: '添加参数',
            handler : function(){
                // access the Record constructor through the grid's store
                var Plant = job_param_grid.getStore().recordType;
                var p = new Plant({
                	jobParamName: 'param name',
                	jobParamValue: 'param value'
                });
                job_param_grid.stopEditing();
                jobDs.insert(0, p);
                job_param_grid.startEditing(0, 0);
            }
        }]
    });

	
	
	
	
	var jobstypeComboData = [ [1, '时间间隔(秒)' ],[2, '时间间隔(分钟)' ], [ 3, '天' ], [ 4, '月' ], [ 5, '周' ]];
	jobstypeCombo = new Ext.form.ComboBox({
		store : new Ext.data.SimpleStore({
			fields : [ "jobsscheduletype", "jobsscheduletypeName" ],
			data : jobstypeComboData
		}),
		valueField : "jobsscheduletype",
		displayField : "jobsscheduletypeName",
		mode : 'local',
		forceSelection : true,
		value : 1,
		hiddenName : 'jobsscheduletype',
		editable : false,
		triggerAction : 'all',
		allowBlank : false,
		fieldLabel : '调度类型',
//		autoWidth : true,
		anchor : '80%',
		name : 'jobsscheduletype',
		listeners:{ 
			   select:function(combo,record,opts) {  
				  var type = combo.getValue();
				 
			      if(type == 1){//时间间隔
			    	  Ext.getCmp('jobssecond').setDisabled(false);
			    	  Ext.getCmp('jobsminute').setDisabled(true);
			    	  Ext.getCmp('jobshour').setDisabled(true);//=true;
			    	  jobsweekCombo.disable();
			    	  jobsmonthCombo.disable();
			      } else if(type == 2){//按分钟运行
			    	  Ext.getCmp('jobssecond').setDisabled(true);
			    	  Ext.getCmp('jobsminute').setDisabled(false);
			    	  Ext.getCmp('jobshour').setDisabled(true);//=true;
			    	  jobsweekCombo.disable();
			    	  jobsmonthCombo.disable();
			      } else if(type == 3){//按天运行
			    	  Ext.getCmp('jobssecond').setDisabled(true);
			    	  Ext.getCmp('jobsminute').setDisabled(true);
			    	  Ext.getCmp('jobshour').setDisabled(false);//=true;
			    	  jobsweekCombo.disable();
			    	  jobsmonthCombo.disable();
			      } else if(type == 5){//每周
			    	  Ext.getCmp('jobssecond').setDisabled(true);
			    	  Ext.getCmp('jobsminute').setDisabled(true);
					  Ext.getCmp('jobshour').setDisabled(false);//=true;
					  jobsweekCombo.enable();
					  jobsmonthCombo.disable();
			      } else if(type == 4){//每月
			    	  Ext.getCmp('jobssecond').setDisabled(true);
			    	  Ext.getCmp('jobsminute').setDisabled(true);
			     	  Ext.getCmp('jobshour').setDisabled(false);//=true;
			     	  jobsweekCombo.disable();
			     	  jobsmonthCombo.enable();
			      }
			    
			   }  
			  }  
	});
	
	
	var jobsweekComboData = [ [ 2, '星期一' ], [ 3, '星期二' ],[ 4, '星期三' ], [ 5, '星期四' ],[ 6, '星期五' ],[ 7, '星期六' ],[ 1, '星期天' ]];
	jobsweekCombo = new Ext.form.ComboBox({
		store : new Ext.data.SimpleStore({
			fields : [ "jobsveryWeek", "jobsweekName" ],
			data : jobsweekComboData
		}),
		valueField : "jobsveryWeek",
		displayField : "jobsweekName",
		mode : 'local',
		forceSelection : true,
		value : 1,
		hiddenName : 'jobsveryWeek',
		editable : false,
		disabled : true,
		triggerAction : 'all',
		fieldLabel : '每周',
//		autoWidth : true,
		anchor : '80%',
		name : 'jobsveryWeek'
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
		value : 1,
		hiddenName : 'jobsverymonth',
		editable : false,
		disabled : true,
		triggerAction : 'all',
		fieldLabel : '每月',
//		autoWidth : true,
		anchor : '80%',
		name : 'jobsverymonth'
	});

	
	
	
	var runJobDialogPanel = new Ext.form.FormPanel({
		id : 'runJobTaskDialog',
		name : 'runJobTaskDialog',
		labelWidth : 100,
		frame : true,
		labelAlign : 'left',
		bodyStyle : 'padding:5px',
		width : 750,
		layout : 'column',
		items : [
				{
					columnWidth : 0.40,
					layout : 'fit',
					items : [job_param_grid]
				},
				{
					columnWidth : 0.6,
					xtype : 'fieldset',
					title : '运行信息配置',
					labelWidth : 90,
					defaults : {
						width : 140,
						border : false
					},
					defaultType : 'textfield',
					autoHeight : true,
					bodyStyle : Ext.isIE ? 'padding:0 0 5px 15px;'
							: 'padding:10px 15px;',
					border : false,
					items : [ {
						xtype : 'radiogroup',
						fieldLabel : '运行方式',
						allowBlank : false,
						anchor : '60%',
						items : [ {
							boxLabel : '远程运行',
							name : 'rb-auto',
							inputValue : 1,
							checked : true
						} ]
					}, jobSlavesCombox, {
						fieldLabel : '任务名称',
						name : 'jobTaskName',
						id : 'jobTaskName',
						allowBlank : false,
						anchor : '60%'
					}, jobstypeCombo,{
						fieldLabel : '以秒计算的时间间隔',
						name : 'jobssecond',
						id : 'jobssecond',
						allowBlank : false,
						anchor : '80%',
						value : 10
					},{
						fieldLabel : '以分钟计算的时间间隔',
						name : 'jobsminute',
						id : 'jobsminute',
						anchor : '80%',
						value : 1
					}, {
						fieldLabel : '每天',
						name : 'jobshour',
						id : 'jobshour',
						disabled : true,
						allowBlank : false,
						value : '13:00',
						anchor : '80%'
					},jobsweekCombo,jobsmonthCombo,{
						id : 'jobName',
						name : 'jobName',
						hidden : true
					}, {
						id : 'jobParams',
						name : 'jobParams',
						hidden : true
					}, {
						id : 'jobConfigId',
						name : 'jobConfigId',
						hidden : true
					} ]
				} ]
	});

	var runJobWindow = new Ext.Window({
		layout : 'fit',
		width : 700,
		height : 400,
		resizable : false,
		draggable : true,
		closeAction : 'hide',
		title : '<span class="commoncss">作业任务运行设置对话框</span>',
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
		items : [ runJobDialogPanel ],
		buttons : [ {
			text : '运行',
			iconCls : 'acceptIcon',
			handler : function() {
				runJobTask();
			}
		}, {
			text : '关闭',
			iconCls : 'deleteIcon',
			handler : function() {
				runJobWindow.hide();
			}
		} ]
	});

	function runTask() {
		var record = grid.getSelectionModel().getSelected();
		if (Ext.isEmpty(record)) {
			Ext.Msg.alert('提示', '请选择你要运行的任务!');
			return;
		}
		var type = record.get("type");
		var name = record.get("name");
		var taskId = record.get("taskId");

		if (type == 1) {
			runTransDialogPanel.getForm().reset();
			runTransWindow.show();
			Ext.getCmp('transName').setValue(name);
			Ext.getCmp('transConfigId').setValue(taskId);
		} else {
			runJobDialogPanel.getForm().reset();
			runJobWindow.show();
			Ext.getCmp('jobName').setValue(name);
			Ext.getCmp('jobConfigId').setValue(taskId);
		}

	}
	
	
	function isTime(str){      
	    if(str.length!=0){    
	    	reg=/^((20|21|22|23|[0-1]\d)\:[0-5][0-9])?$/     
	        if(!reg.test(str)){    
	           return true;   
	        }
	    	return false;
	    } 
	    return false
	}  
	
	function runJobTask() {
		var param = "{";
		job_param_grid.getStore().each(function(record){
			var name = record.get("jobParamName");
			var value = record.get("jobParamValue"); 
			 if(name != "" && value != ""){
				param += "\""+name+"\":\""+value+"\",";
			 }
			 //alert(record.get("paramValue"));
		});
		if(param != ""){
			param = param.substring(0,param.lastIndexOf(","));
		}
		param +="}";
		var type = jobstypeCombo.getValue();
		var hour = Ext.getCmp('jobshour').getValue();
		if(hour != null && isTime(hour) && (type !=1 || type != 2)){
			Ext.MessageBox.alert('提示信息','<span style="color:red">对不起，您输入的日期格式不正确,正确的格式是(hh:mm),例如： 13:20.</span>');
			return ;
		}
		
		Ext.getCmp('jobParams').setValue(param);
		runJobDialogPanel.form.submit({
			url : '../job/runJobTask.shtml',
			waitTitle : '提示',
			method : 'POST',
			waitMsg : '正在处理数据,请稍候...',
			success : function(form, action) {
				Ext.MessageBox.alert('提示信息', action.result.msg);
				runJobWindow.hide();
				store.reload();
				form.reset();
			},
			failure : function(form, action) {
				var msg = action.result.msg;
				Ext.MessageBox.alert('提示', '运行作业任务失败:<br>' + msg);
			}
		});
	}

	function runTransTask() {
		var param = "{";
		param_grid.getStore().each(function(record){
			var name = record.get("paramName");
			var value = record.get("paramValue"); 
			 if(name != "" && value != ""){
				param += "\""+name+"\":\""+value+"\",";
			 }
		});
		if(param != ""){
			param = param.substring(0,param.lastIndexOf(","));
		}
		param +="}";
		Ext.getCmp('transParams').setValue(param);
		var type = transtypeCombo.getValue();
		var hour = Ext.getCmp('transhour').getValue();
		if(hour != null && isTime(hour) && (type !=1 || type != 2)){
			Ext.MessageBox.alert('提示信息','<span style="color:red">对不起，您输入的日期格式不正确,正确的格式是(hh:mm),例如： 13:20.</span>');
			return ;
		}
		runTransDialogPanel.form.submit({
			url : '../job/runTransTask.shtml',
			waitTitle : '提示',
			method : 'POST',
			waitMsg : '正在处理数据,请稍候...',
			success : function(form, action) {
				Ext.MessageBox.alert('提示信息', action.result.msg);
				runTransWindow.hide();
				store.reload();
				form.reset();
			},
			failure : function(form, action) {
				var msg = action.result.msg;
				Ext.MessageBox.alert('提示', '运行转换任务失败:<br>' + msg);
			}
		});
	}

});