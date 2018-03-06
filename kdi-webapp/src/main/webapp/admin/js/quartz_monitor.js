Ext.onReady(function() {

	var sm = new Ext.grid.CheckboxSelectionModel();
	var cm = new Ext.grid.ColumnModel([ new Ext.grid.RowNumberer(), sm, {
		header : '任务编号',
		dataIndex : 'TRIGGER_NAME',
		hidden : true
	},{
		header : '分组',
		dataIndex : 'TRIGGER_GROUP',
		hidden : true
	}, {
		id : '任务名称',
		header : '任务名称',
		dataIndex : 'display_name',
		width : 160
	}, {
		id : '任务类型',
		header : '任务类型',
		dataIndex : 'TASK_TYPE',
		width : 100,
		renderer : function(v) {
			if (v == 1) {
				return "Trans";
			} else if (v == 2) {
				return "Job";
			} else {
				return "其他";
			}
		}
	}, {
		id : ' Job/Trans名称',
		header : ' Job/Trans名称',
		dataIndex : 'TASK_NAME',
		width : 120
	},{
		id : '下次执行时间',
		header : '下次执行时间',
		dataIndex : 'NEXT_FIRE_TIME',
		width : 140
	},{
		id : '上次执行时间',
		header : '上次执行时间',
		dataIndex : 'PREV_FIRE_TIME',
		width : 140
	}, {
		header : '状态',
		dataIndex : 'TRIGGER_STATE',
		width : 80
	}, {
		header : '类型',
		dataIndex : 'TRIGGER_TYPE',
		width : 80
	}, {
		header : '开始时间',
		dataIndex : 'START_TIME',
		width : 140
	}, {
		header : '结束时间',
		dataIndex : 'END_TIME',
		width : 140
	} ]);

	/**
	 * 数据存储
	 */
	var store = new Ext.data.Store({
		proxy : new Ext.data.HttpProxy({
			url : '../triggers/list.shtml'
		}),
		reader : new Ext.data.JsonReader({
			totalProperty : 'total',
			root : 'rows'
		}, [ {
			name : 'TRIGGER_NAME'
		}, {
			name : 'TRIGGER_GROUP'
		}, {
			name : 'display_name'
		},{
			name : 'TASK_TYPE'
		},{
			name : 'TASK_NAME'
		},{
			name : 'NEXT_FIRE_TIME'
		},{
			name : 'PREV_FIRE_TIME'
		}, {
			name : 'TRIGGER_STATE'
		}, {
			name : 'TRIGGER_TYPE'
		} , {
			name : 'START_TIME'
		} , {
			name : 'END_TIME'
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
		title : '<span class="commoncss">定时任务运行监控</span>',
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
			text : '暂停任务',
			iconCls : 'page_addIcon',
			handler : function() {
				pauseTrigger();
			}
		},'-',{
			text : '恢复任务',
			iconCls : 'page_addIcon',
			handler : function() {
				resumeTrigger();
			}
		},'-',{
			text : '删除任务',
			iconCls : 'page_addIcon',
			handler : function() {
				removeTrigdger();
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
		viewInfo();
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
	
	//删除任务
	function removeTrigdger(){
		var record = grid.getSelectionModel().getSelected();
		if (Ext.isEmpty(record)) {
			Ext.MessageBox.alert('提示', '请先选择要删除的任务!');
			return;
		}
		var triggerName = record.get("TRIGGER_NAME");
		var group = record.get("TRIGGER_GROUP");
		Ext.Msg.confirm('请确认','<span style="color:red"><b>提示:</b>你确认要删除选中的定时任务吗,请慎重.</span><br>继续删除吗?',
				function(btn, text) {
					if (btn == 'yes') {
						showWaitMsg();
						Ext.Ajax.request( {
									url : '../triggers/removeTrigdger.shtml',
									success : function(response) {
										var resultArray = Ext.util.JSON.decode(response.responseText);
										store.reload();
										Ext.Msg.alert('提示',resultArray.msg);
									},
									failure : function(response) {
										var resultArray = Ext.util.JSON.decode(response.responseText);
										Ext.Msg.alert('提示',resultArray.msg);
									},
									params : {
										triggerName : triggerName,
										group : group
									}
								});
					}
				});	
	}
	//暂停任务
	function pauseTrigger(){
		var record = grid.getSelectionModel().getSelected();
		if (Ext.isEmpty(record)) {
			Ext.MessageBox.alert('提示', '请先选择要暂停的任务!');
			return;
		}
		var triggerName = record.get("TRIGGER_NAME");
		var group = record.get("TRIGGER_GROUP");
		Ext.Msg.confirm('请确认','<span style="color:red"><b>提示:</b>你确认要暂停选中的定时任务吗,请慎重.</span><br>继续操作吗?',
				function(btn, text) {
					if (btn == 'yes') {
						showWaitMsg();
						Ext.Ajax.request( {
									url : '../triggers/pauseTrigger.shtml',
									success : function(response) {
										var resultArray = Ext.util.JSON.decode(response.responseText);
										store.reload();
										Ext.Msg.alert('提示',resultArray.msg);
									},
									failure : function(response) {
										var resultArray = Ext.util.JSON.decode(response.responseText);
										Ext.Msg.alert('提示',resultArray.msg);
									},
									params : {
										triggerName : triggerName,
										group : group
									}
								});
					}
				});
	}
	//恢复任务
	function resumeTrigger(){
		var record = grid.getSelectionModel().getSelected();
		if (Ext.isEmpty(record)) {
			Ext.MessageBox.alert('提示', '请先选择要恢复的任务!');
			return;
		}
		var triggerName = record.get("TRIGGER_NAME");
		var group = record.get("TRIGGER_GROUP");
		Ext.Msg.confirm('请确认','<span style="color:red"><b>提示:</b>你确认要恢复选中的定时任务吗,请慎重.</span><br>继续操作吗?',
				function(btn, text) {
					if (btn == 'yes') {
						showWaitMsg();
						Ext.Ajax.request( {
									url : '../triggers/resumeTrigger.shtml',
									success : function(response) {
										var resultArray = Ext.util.JSON.decode(response.responseText);
										store.reload();
										Ext.Msg.alert('提示',resultArray.msg);
									},
									failure : function(response) {
										var resultArray = Ext.util.JSON.decode(response.responseText);
										Ext.Msg.alert('提示',resultArray.msg);
									},
									params : {
										triggerName : triggerName,
										group : group
									}
								});
					}
				});
	}


});