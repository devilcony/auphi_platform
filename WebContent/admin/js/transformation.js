
Ext.onReady(function() {
	var sm = new Ext.grid.CheckboxSelectionModel();
	var cm = new Ext.grid.ColumnModel([ new Ext.grid.RowNumberer(), sm, {
		header : '转换编号',
		dataIndex : 'id',
		hidden : true
	}, {
		id : 'name',
		header : '转换名称',
		dataIndex : 'name',
		width : 120
	}, {
		header : '转换类型',
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
			url : '../trans/list.shtml'
		}),
		reader : new Ext.data.JsonReader({
			totalProperty : 'total',
			root : 'rows'
		}, [ {
			name : 'id'
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
		title : '<span class="commoncss">公司信息管理</span>',
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
			text : '运行转换',
			iconCls : 'page_addIcon',
			handler : function() {
				runTrans();
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

	function runTrans() {
		var record = grid.getSelectionModel().getSelected();
		if (Ext.isEmpty(record)) {
			Ext.Msg.alert('提示', '请选择你要运行的转换配置!');
			return;
		}
		var record = grid.getSelectionModel().getSelected();
		var name = record.get("name");
		var type = record.get("type");

		Ext.Msg.confirm('请确认', '你确定要运行该任务吗?', function(btn, text) {
			if (btn == 'yes') {
				Ext.Ajax.request({
					url : '../job/runJob.shtml',
					timeout : 120000,
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
						name : name,
						type : type
					}
				});
			}
		});
	}

	
	/*
	 * 远程执行转换
	 */
	function RemoteTrans() {
		var record = grid.getSelectionModel().getSelected();
		if (Ext.isEmpty(record)) {
			Ext.Msg.alert('提示', '请选择你要运行的任务!');
			return;
		}
		var record = grid.getSelectionModel().getSelected();
		var name = record.get("name");
		var type = record.get("type");

		Ext.Msg.confirm('请确认', '你确定要运行该任务吗?', function(btn, text) {
			if (btn == 'yes') {
				Ext.Ajax.request({
					url : '../trans/runRemoteTrans.shtml',
					timeout : 640000,
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
						name : name,
						type : type
					}
				});
			}
		});
	}
	
	/*
	 * 集群执行转换
	 */
	function runTransForCluster() {
		
		var record = grid.getSelectionModel().getSelected();
		if (Ext.isEmpty(record)) {
			Ext.Msg.alert('提示', '请选择你要运行的任务!');
			return;
		}
		var record = grid.getSelectionModel().getSelected();
		var name = record.get("name");
		var type = record.get("type");

		Ext.Msg.confirm('请确认', '你确定要运行该任务吗?', function(btn, text) {
			if (btn == 'yes') {
				Ext.Ajax.request({
					url : '../trans/runTransForCluster.shtml',
					timeout : 640000,
					success : function(response) {
						var resultArray = Ext.util.JSON
								.decode(response.responseText);
						store.reload();
						Ext.Msg.alert('提示', resultArray.msg);
						//Ext.Msg.alert('提示', 'chenggong!');
					},
					failure : function(response) {
						var resultArray = Ext.util.JSON
								.decode(response.responseText);
						Ext.Msg.alert('提示', resultArray.msg);
						//Ext.Msg.alert('提示', 'fail!');
					},
					params : {
						name : name,
						type : type
					}
				});
			}
		});

	}

});
	
	
	