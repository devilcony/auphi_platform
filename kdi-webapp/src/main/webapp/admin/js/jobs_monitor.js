Ext.onReady(function() {

	var sm = new Ext.grid.CheckboxSelectionModel();
	var cm = new Ext.grid.ColumnModel([ new Ext.grid.RowNumberer(), sm, {
		header : '作业编号',
		dataIndex : 'JOB_ID',
		hidden : true
	},{
		header : 'OBJECT_ID',
		dataIndex : 'CHANNEL_ID',
		hidden : true
	}, {
		id : '任务名称',
		header : '任务名称',
		dataIndex : 'JOB_CN_NAME',
		width : 120
	}, {
		id : '作业名称',
		header : '作业名称',
		dataIndex : 'JOB_NAME',
		width : 120
	},{
		id : '作业状态',
		header : '作业状态',
		dataIndex : 'STATUS',
		width : 120
	},{
		id : '执行服务器',
		header : '执行服务器',
		dataIndex : 'EXECUTING_SERVER',
		width : 120
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
			url : '../jobsMonitor/list.shtml'
		}),
		reader : new Ext.data.JsonReader({
			totalProperty : 'total',
			root : 'rows'
		}, [ {
			name : 'JOB_ID'
		}, {
			name : 'CHANNEL_ID'
		}, {
			name : 'JOB_CN_NAME'
		},{
			name : 'JOB_NAME'
		},{
			name : 'STATUS'
		},{
			name : 'EXECUTING_SERVER'
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
		title : '<span class="commoncss">作业任务运行监控</span>',
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
	

	
	
	function viewInfo(){
		var record = grid.getSelectionModel().getSelected();
		if (Ext.isEmpty(record)) {
			Ext.MessageBox.alert('提示', '请先选中查看的作业');
			return;
		}
		var jobName = record.get("JOB_NAME");
		var serverName = record.get("EXECUTING_SERVER");
		var objectId = record.get("CHANNEL_ID");
		var jobId = record.get("JOB_ID");
		var status = record.get("STATUS");
		var jobcnname = record.get("JOB_CN_NAME");
		

	    var tabs = new Ext.TabPanel({
	           region: 'center',
	           margins:'3 3 3 0', 
	           activeTab: 0,
	           defaults:{autoScroll:true},
	           items:[{
	        	   title: '作业步骤图',
	        	    xtype: 'box', //或者xtype: 'component',      
	        	    width: 100, //图片宽度      
	        	    height: 200, //图片高度      
	        	    autoEl: {      
	        	        tag: 'img',    //指定为img标签      
	        	        src: '../jobsMonitor/viewImage.shtml?jobName='+jobName+'&serverName='+serverName+'&objectId='+objectId   //指定url路径      
	        	    }      
	        	} ,{
	               title: '作业日志信息',
	               autoLoad: {url: '../jobsMonitor/getJobsLoginfo.shtml?jobId='+jobId}
	           }]
	       });

		var html = '<p style="font-weight:bold;">作业名称:'+jobName+'&nbsp;&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;&nbsp;执行服务器:'+serverName+'</p>'+
		'<p style="font-weight:bold;">任务名称:'+jobcnname+'&nbsp;&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;&nbsp;状态:'+status+'</p>'+
		'<p style="font-weight:bold;">Object ID:'+objectId+'</p>';
	    var p3 = new Ext.Panel({   
	        title: '作业状态信息',
	        region: 'north',
	        width: 400,  
	        html :html
	
	    });  	 
	   
		var win = new Ext.Window({
	        title: '作业任务运行状态信息',
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