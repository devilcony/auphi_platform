Ext.onReady(function() {

	var sm = new Ext.grid.CheckboxSelectionModel();
	var cm = new Ext.grid.ColumnModel([ new Ext.grid.RowNumberer(), sm, {
		header : '转换作业编号',
		dataIndex : 'TRANS_ID',
		hidden : true
	},{
		header : 'OBJECTID',
		dataIndex : 'CHANNEL_ID',
		hidden : true
	}, {
		id : '任务名称',
		header : '任务名称',
		dataIndex : 'TRANS_CN_NAME',
		width : 120
	}, {
		id : '转换名称',
		header : '转换名称',
		dataIndex : 'TRANSNAME',
		width : 120
	},{
		id : '转换状态',
		header : '转换状态',
		dataIndex : 'STATUS',
		width : 120
	},{
		id : '执行服务器',
		header : '执行服务器',
		dataIndex : 'EXECUTING_SERVER',
		width : 120
	}, {
		header : '执行方式',
		dataIndex : 'EXCUTOR_TYPE',
		width : 120,
		renderer : function(v) {
			if (v == 2) {
				return "远程";
			} else if (v == 3) {
				return "集群";
			}
		}
	}, {
		header : '开始时间',
		dataIndex : 'STARTDATE',
		width : 160,
		renderer : function(_v) {
			var _date = Ext.util.Format.date(_v, "Y-m-d H:m:s");
			return _date;
		}
	}, {
		header : '结束时间',
		dataIndex : 'ENDDATE',
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
			url : '../transMonitor/list.shtml'
		}),
		reader : new Ext.data.JsonReader({
			totalProperty : 'total',
			root : 'rows'
		}, [ {
			name : 'TRANS_ID'
		}, {
			name : 'CHANNEL_ID'
		}, {
			name : 'TRANS_CN_NAME'
		},{
			name : 'TRANSNAME'
		},{
			name : 'STATUS'
		},{
			name : 'EXECUTING_SERVER'
		},{
			name : 'EXCUTOR_TYPE'
		}, {
			name : 'STARTDATE',
			type : 'date',
			mapping : 'STARTDATE.time',
			dateFormat : 'time'
		}, {
			name : 'ENDDATE',
			type : 'date',
			mapping : 'ENDDATE.time',
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
		title : '<span class="commoncss">转换任务运行监控</span>',
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
			text : '查看日志',
			iconCls : 'page_addIcon',
			handler : function() {
				
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
	

	
	var st_sm = new Ext.grid.CheckboxSelectionModel();
	var st_cm = new Ext.grid.ColumnModel([ new Ext.grid.RowNumberer(), st_sm, {
		header : '步骤名称',
		dataIndex : 'STEPNAME',
		width : 120
	},{
		header : 'CopyNr',
		dataIndex : 'STEP_COPY',
		width : 70
	}, {
		id : '读取',
		header : '读取',
		dataIndex : 'LINES_READ',
		width : 60
	}, {
		id : '写入',
		header : '写入',
		dataIndex : 'LINES_WRITTEN',
		width : 60
	},{
		id : '输入',
		header : '输入',
		dataIndex : 'LINES_INPUT',
		width : 60
	},{
		id : '输出',
		header : '输出',
		dataIndex : 'LINES_OUTPUT',
		width : 60
	},{
		id : '更新',
		header : '更新',
		dataIndex : 'LINES_UPDATED',
		width : 60
	},{
		id : '忽略',
		header : '忽略',
		dataIndex : 'LINES_REJECTED',
		width : 60
	},{
		id : '错误',
		header : '错误',
		dataIndex : 'ERRORS',
		width : 60
	},{
		id : '状态',
		header : '状态',
		dataIndex : 'STATUS',
		width : 80
	}, {
		header : '耗时',
		dataIndex : 'COSTTIME',
		width : 80
	}, {
		header : '速度',
		dataIndex : 'SPEED',
		width : 120
	} ]);

	/**
	 * 数据存储
	 */
	var st_store = new Ext.data.Store({
		proxy : new Ext.data.HttpProxy({
			url : '../transMonitor/setpList.shtml'
		}),
		reader : new Ext.data.JsonReader({
			totalProperty : 'total',
			root : 'rows'
		}, [ {
			name : 'SPEED'
		}, {
			name : 'COSTTIME'
		}, {
			name : 'STATUS'
		},{
			name : 'ERRORS'
		},{
			name : 'LINES_REJECTED'
		},{
			name : 'LINES_UPDATED'
		},{
			name : 'LINES_OUTPUT'
		},{
			name : 'LINES_INPUT'
		},{
			name : 'LINES_WRITTEN'
		},{
			name : 'LINES_READ'
		},{
			name : 'STEP_COPY'
		},{
			name : 'STEPNAME'
		} ])
	});


	var st_grid = new Ext.grid.GridPanel({
		title : '<span class="commoncss">转换处理数据量信息</span>',
		iconCls : 'configIcon',
		height : 250,
		autoScroll : true,
		region : 'center',
		store : st_store,
		loadMask : {
			msg : '正在加载表格数据,请稍等...'
		},
		stripeRows : true,
		frame : true,
		cm : st_cm,
		sm : st_sm

	});
	
	
	function viewInfo(){
		var record = grid.getSelectionModel().getSelected();
		if (Ext.isEmpty(record)) {
			Ext.MessageBox.alert('提示', '请先选中查看的转换');
			return;
		}
		var transName = record.get("TRANSNAME");
		var serverName = record.get("EXECUTING_SERVER");
		var objectId = record.get("CHANNEL_ID");
		var transId = record.get("TRANS_ID");
		var status = record.get("STATUS");
		var transcnname = record.get("TRANS_CN_NAME");
		
		st_store.load({
			params : {
				transId : transId
			}
		});
	    var tabs = new Ext.TabPanel({
	           region: 'center',
	           margins:'3 3 3 0', 
	           activeTab: 0,
	           defaults:{autoScroll:true},
	           items:[{
	        	   title: '转换步骤图',
	        	    xtype: 'box', //或者xtype: 'component',      
	        	    width: 100, //图片宽度      
	        	    height: 200, //图片高度      
	        	    autoEl: {      
	        	        tag: 'img',    //指定为img标签      
	        	        src: '../transMonitor/viewImage.shtml?transName='+transName+'&serverName='+serverName+'&objectId='+objectId   //指定url路径      
	        	    }      
	        	} ,{
	               title: '转换步骤结果',
	               items:[ st_grid ]
	           },{
	               title: '转换日志信息',
	               autoLoad: {url: '../transMonitor/getTransLoginfo.shtml?transId='+transId}
	           }]
	       });

		var html = '<p style="font-weight:bold;">转换名称:'+transName+'&nbsp;&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;&nbsp;执行服务器:'+serverName+'</p>'+
		'<p style="font-weight:bold;">任务名称:'+transcnname+'&nbsp;&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;&nbsp;状态:'+status+'</p>'+
		'<p style="font-weight:bold;">Object ID:'+objectId+'</p>';
	    var p3 = new Ext.Panel({   
	        title: '转换状态信息',
	        region: 'north',
	        width: 400,  
	        html :html
	
	    });  	 
	   
		var win = new Ext.Window({
	        title: '转换任务运行状态信息',
	        closable:true,
	        autoDestroy:true,
	        closeaction: 'hide',
	        width:600,
	        height:400,
	        //border:false,
	        plain:true,
	        layout: 'border',

	        items: [p3,tabs],
	        buttons : [ {
				text : '关闭',
				iconCls : 'deleteIcon',
				handler : function() {
					win.hide();
				}
			}]
	    });
		win.show();
		
	}

});