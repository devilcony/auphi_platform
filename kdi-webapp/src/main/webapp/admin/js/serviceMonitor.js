Ext.onReady(function(){
	var sm = new Ext.grid.CheckboxSelectionModel();
	var cm = new Ext.grid.ColumnModel([new Ext.grid.RowNumberer(),sm,{
		header:'监控编号',
		dataIndex:'monitorId',
		hidden:true
	},{
		id:"serviceName",
		header:'服务接口名称',
		dataIndex:'serviceName',
		width:200
	},{
		id:"status",
		header:'运行状态',
		dataIndex:'status',
		width:140
	},{
		id:"userName",
		header:'用户名称',
		dataIndex:'userName',
		width:160
	},{
		id:"systemName",
		header:'系统名称',
		dataIndex:'systemName',
		width:160
	},{
		id:"startTime",
		header:'开始时间',
		dataIndex:'startTime',
		width:180
	},{
		id:"endTime",
		header:'结束时间',
		dataIndex:'endTime',
		width:180
	}]);
	
	/**
	 * 数据存储 
	 */
	var store = new Ext.data.Store({
		proxy : new Ext.data.HttpProxy({
			url:'../serviceMonitor/list.shtml'	
		}),
		reader: new Ext.data.JsonReader({
			totalProperty : 'total',
			root : 'rows'
		},[{
			name : 'monitorId' 
		},{
			name : 'serviceName'
		},{
			name : 'status'
		},{
			name : 'userName'
		},{
			name : 'systemName'
		},{
			name : 'startTime'
		},{
			name : 'endTime'
		}])
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
		title : '<span class="commoncss">服务接口监控管理</span>',
		iconCls : 'configIcon',
		height :500,
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
		tbar : [/*{
					text : '新增',
					iconCls : 'page_addIcon',
					handler : function(){
						addInit();
					}
		        },'-',{
		        	text : '修改',
		        	iconCls : 'page_edit_1Icon',
		        	handler : function(){
		        		editInit();
					}
		        },'-',{
		        	text : '删除',
		        	iconCls : 'page_delIcon',
		        	handler : function(){
		        	    deleteServiceUser();
					}
		        },'-',*/'->',
		        new Ext.form.TextField({
		        		id : 'queryServiceName',
		        		name : 'queryServiceName',
		        		emptyText : '请输入服务接口名',
		        		enableKeyEvents : true,	
		        		listeners : {
		        			specialkey : function(field,e){
		        				if(e.getKey() == Ext.EventObject.ENTER){
		        					queryServiceUser();
		        				}
		        			}
		        		},
		        		width : 150
		        }),{
		        	text : '查询',
		        	iconCls : 'previewIcon',
		        	handler : function(){
						//TODO在此做查询
		        		queryServiceUser();
				    }
		        },'-',{
		        	text : '刷新',
					iconCls : 'arrow_refreshIcon',
					handler : function() {
		        		store.reload();
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
	
	
	/**
	 * 布局
	 */
	var viewport = new Ext.Viewport({
		layout : 'border',
		items : [grid]
	})
	
	
		
		/**
		 * 根据条件查询服务用户
		 */
		function queryServiceUser(){
			store.load({
				params : {
					start : 0,
					limit : bbar.pageSize,
					queryParam : Ext.getCmp('queryServiceName').getValue()
				}
			});
		}
		
	   
		
		
		
});