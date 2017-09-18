Ext.onReady(function(){
	var sm = new Ext.grid.CheckboxSelectionModel();
	var cm = new Ext.grid.ColumnModel([new Ext.grid.RowNumberer(),sm,{
		header:'权限编号',
		dataIndex:'authId',
		hidden:true
	},{
		id:"service_name",
		header:'接口服务名称',
		dataIndex:'service_name',
		width:200
	},{
		header:'接口服务地址',
		dataIndex:'service_url',
		width:270
	},{
		header:'授权IP',
		dataIndex:'authIP',
		width:200
	},{
		header:'授权用户',
		dataIndex:'userName',
		width:170
	},{
		header:'使用部门',
		dataIndex:'use_dept',
		width:200
	},{
		header:'使用人员',
		dataIndex:'user_name',
		width:170
	}]);
	
	/**
	 * 数据存储
	 */
	var store = new Ext.data.Store({
		proxy : new Ext.data.HttpProxy({
			url:'../serviceAuth/list.shtml'	
		}),
		reader: new Ext.data.JsonReader({
			totalProperty : 'total',
			root : 'rows'
		},[{
			name : 'authId' 
		},{
			name : 'userId' 
		},{
			name : 'serviceId' 
		},{
			name : 'service_name'
		},{
			name : 'service_url'
		},{
			name : 'authIP'
		},{
			name : 'userName'
		},{
			name : 'use_dept'
		},{
			name : 'user_name'
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
		title : '<span class="commoncss">服务授权管理</span>',
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
		tbar : [{
					text : '服务接口授权',
					iconCls : 'page_addIcon',
					handler : function(){
						addInit();
					}
		        },'-',{
		        	text : '修改授权',
		        	iconCls : 'page_edit_1Icon',
		        	handler : function(){
		        		editInit();
					}
		        },'-',{
		        	text : '删除授权',
		        	iconCls : 'page_delIcon',
		        	handler : function(){
		        		deleteServiceAuth();
					}
		        },'-','->',
		        new Ext.form.TextField({
		        		id : 'queryServiceUserName',
		        		name : 'queryServiceUserName',
		        		emptyText : '请输入授权用户名',
		        		enableKeyEvents : true,	
		        		listeners : {
		        			specialkey : function(field,e){
		        				if(e.getKey() == Ext.EventObject.ENTER){
		        					queryServiceAuth();
		        				}
		        			}
		        		},
		        		width : 150
		        }),{
		        	text : '查询',
		        	iconCls : 'previewIcon',
		        	handler : function(){
						//TODO在此做查询
		        		queryServiceAuth();
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
	 * 服务接口多选框数据源
	 */
	var service = new Ext.data.Store({
		proxy : new Ext.data.HttpProxy({
			url : '../service/getAllServiceList.shtml'
		}),
		autoLoad : true,
		reader : new Ext.data.JsonReader({}, ['serviceId', 'serviceName'])
	});
	
	/**
	 * 服务接口多选框
	 */
	 serviceCombox = new Ext.form.ComboBox({
		 	store : service,
			valueField : "serviceId",
			mode : 'remote',
			displayField : "serviceName",
			forceSelection : true,
			emptyText : '服务接口',
			editable : false,
			allowBlank : false,
			triggerAction : 'all',
			anchor : '70%',
			fieldLabel : '服务接口',
			autoWidth : true,
			hiddenName : "serviceId",
			autoShow : true,
			selectOnFocus : true,
			name : "serviceId",
			id: "service_id"	
	 });
	
	 /**
	  * 服务用户多选框数据源
	  */
	 var serviceUser = new Ext.data.Store({
			proxy : new Ext.data.HttpProxy({
				url : '../serviceUser/getAllServiceUserList.shtml'
			}),
			autoLoad : true,
			reader : new Ext.data.JsonReader({},['userId', 'userName'])
	 });
	 
	 /**
	  * 服务用户多选框
	  */
	 serviceUserCombox = new Ext.form.ComboBox({
		 store : serviceUser,
		 valueField : "userId",
		 mode : 'remote',
		 displayField : "userName",
		 forceSelection : true,
		 emptyText : '授权用户',
		 editable : false,
		 allowBlank : false,
		 triggerAction : 'all',
		 anchor : '70%',
		 fieldLabel : '授权用户',
		 autoWidth : true,
		 hiddenName : "userId",
		 autoShow : true,
		 selectOnFocus : true,
		 name : "userId", 
		 id: 'user_Id'	 
	});
	
	
	/**
	 * 创建FormPanel
	 */
	var serviceAuthPanel = new Ext.form.FormPanel({
		id : 'serviceAuthPanel',
		name : 'serviceAuthPanel',
		defaultType : 'textfield',
		labelAlign : 'right',
		labelWidth : 65,
		frame : false,
		bodyStyle : 'padding:5 5 0',
		items : [serviceCombox,serviceUserCombox,{
					fieldLabel : '授权IP',
					name : 'authIP',
					id : 'authIP',
					allowBlank : false,
					regex: /^\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}$/,
					//regex: [\d|\.|,]+,
					regexText: '请输入正确IP地址，例：127.0.0.1',
					anchor : '70%'
				},{
					fieldLabel : '使用部门',
					name : 'use_dept',
					id : 'use_dept',
					allowBlank : true,
					anchor : '70%'
				},{
					fieldLabel : '使用人员',
					name : 'user_name',
					id : 'user_name',
					allowBlank : true,
					anchor : '70%'
				},{
					fieldLabel : '业务用途',
					name : 'use_desc',
					id : 'use_desc',
					allowBlank : true,
					xtype : 'textarea',
					width : 500, 
					height : 80, 
			    },{
			    	id : 'authId',
					name : 'authId',
					hidden : true,
			    },{
			    	id : 'windowmode',
					name : 'windowmode',
					hidden : true
			    }]
	});
	
	var serviceAuthWindow = new Ext.Window({
			layout : 'fit',
			width : 600,
			height : 300,
			resizable : false,
			draggable : true,
			closeAction : 'hide',
			title : '<span class="commoncss">服务接口授权对话框</span>',
			modal : true,
			collapsible : true,
			titleCollapse : true,
			maximizable : false,
			buttonAlign : 'right',
			border : false,
			animCollapse : true,
			//pageY : 20,
			//pageX : document.body.clientWidth / 2 - 450 / 2,
			animateTarget : Ext.getBody(),
			constrain : true,
			items : [serviceAuthPanel],
			buttons :[{
				text : '保存',
				iconCls : 'acceptIcon',
				handler : function(){
					//在此处处理服务对象
					var mode = Ext.getCmp('windowmode').getValue();
					if(mode == 'add'){
						 saveServiceAuthItem();
					}else{
						 updateServiceAuthItem();
					}
				}
			},{
				text : '关闭',
				iconCls : 'deleteIcon',
				handler : function(){
					serviceAuthWindow.hide();
				}
			}]
	});

	/**
	 * 新增参数初始化
	 */
	function addInit(){
		var flag = Ext.getCmp('windowmode').getValue();
		if (typeof(flag) != 'undefined') {
			serviceAuthPanel.form.getEl().dom.reset();
		} else {
			clearForm(serviceAuthPanel.getForm());
		}
		serviceAuthWindow.show();
		serviceAuthWindow.setTitle('<span class="commoncss">服务接口授权</span>');
		Ext.getCmp('windowmode').setValue('add');	
	}
	
	/**
	 * 保存授权信息
	 */
	function saveServiceAuthItem(){
		if(!serviceAuthPanel.form.isValid()){
			return;
		}
		serviceAuthPanel.form.submit({
			url : '../serviceAuth/saveServiceAuth.shtml',
			waitTitle : '提示',
			method : 'POST',
			waitMsg : '正在处理数据,请稍候...',
			success : function(form, action) {
				serviceAuthWindow.hide();
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
	 * 修改参数初始化
	 */
	function editInit(){
		var record = grid.getSelectionModel().getSelected();
		if (Ext.isEmpty(record)) {
			Ext.MessageBox.alert('提示', '请先选中要修改的项目');
			return;
		}
		serviceAuthPanel.getForm().loadRecord(record);
		serviceAuthWindow.show();
		serviceAuthWindow.setTitle('<span class="commoncss">修改服务授权</span>');
		Ext.getCmp('windowmode').setValue('edit');
		Ext.getCmp('authId').setValue(record.get('authId'));
		Ext.getCmp('service_id').setValue(record.get('serviceId'));
		Ext.getCmp('user_Id').setValue(record.get('userId'));
		Ext.getCmp('btnReset').hide();
	}
	
	
	/**
	 * 修改用户权限
	 */
	function updateServiceAuthItem(){
		if (!serviceAuthPanel.form.isValid()){
			return;
		}
		serviceAuthPanel.form.submit({
			url : '../serviceAuth/updateServiceAuth.shtml',
			waitTitle : '提示',
			method : 'POST',
			waitMsg : '正在处理数据,请稍候...',
			success : function(form, action) {
			 	serviceAuthWindow.hide();
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
	 * 根据条件查询服务用户
	 */
	function queryServiceAuth(){
		store.load({
			params : {
				start : 0,
				limit : bbar.pageSize,
				queryParam : Ext.getCmp('queryServiceUserName').getValue()
			}
		});
	}
	
	/**
	 * 删除服务权限
	 */
	function  deleteServiceAuth(){
		var rows = grid.getSelectionModel().getSelections();
		if(Ext.isEmpty(rows)){
			Ext.Msg.alert('提示', '请先选中要删除的项目!');
			return;
		}
		var strChecked = jsArray2JsString(rows,'authId');
		Ext.Msg.confirm('请确认', '确认删除选中的数据吗?', function(btn, text) {
			if(btn == 'yes'){
				Ext.Ajax.request({
					url : '../serviceAuth/deleteServiceAuth.shtml',
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