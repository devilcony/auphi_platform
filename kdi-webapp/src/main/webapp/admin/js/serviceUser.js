Ext.onReady(function(){
	var sm = new Ext.grid.CheckboxSelectionModel();
	var cm = new Ext.grid.ColumnModel([new Ext.grid.RowNumberer(),sm,{
		header:'用户编号',
		dataIndex:'userId',
		hidden:true
	},{
		id:"userName",
		header:'用户名称',
		dataIndex:'userName',
		width:220
	},{
		header:'系统名称',
		dataIndex:'systemName',
		width:220
	},{
		header:'部署IP',
		dataIndex:'systemIp',
		width:220
	},{
		header:'系统描述',
		dataIndex:'systemDesc',
		width:270
	}]);
	
	/**
	 * 数据存储 
	 */
	var store = new Ext.data.Store({
		proxy : new Ext.data.HttpProxy({
			url:'../serviceUser/list.shtml'	
		}),
		reader: new Ext.data.JsonReader({
			totalProperty : 'total',
			root : 'rows'
		},[{
			name : 'userId' 
		},{
			name : 'userName'
		},{
			name : 'password'
		},{
			name : 'systemName'
		},{
			name : 'systemIp'
		},{
			name : 'systemDesc'
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
		title : '<span class="commoncss">服务用户管理</span>',
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
		        },'-','->',
		        new Ext.form.TextField({
		        		id : 'queryServiceUserName',
		        		name : 'queryServiceUserName',
		        		emptyText : '请输入服务用户名',
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
	 * 创建FormPanel
	 */
	var serviceUserPanel = new Ext.form.FormPanel({
		id : 'serviceUserPanel',
		name : 'serviceUserPanel',
		defaultType : 'textfield',
		labelAlign : 'right',
		labelWidth : 65,
		frame : false,
		bodyStyle : 'padding:5 5 0',
		items : [{
					fieldLabel : '用户名称',
					name : 'userName',
					id : 'userName',
					allowBlank : false,
					anchor : '70%'
				},{
					fieldLabel : '用户密码',
					name : 'password',
					id : 'password',
					inputType : 'password',
					allowBlank : false,
					anchor : '70%'
				},{
					fieldLabel : '确认密码',
					name : 'confirmPassword',
					id : 'confirmPassword',
					inputType : 'password',
					allowBlank : false,
					anchor : '70%'
				},{
					fieldLabel : '系统名称',
					name : 'systemName',
					id : 'systemName',
					allowBlank : false,
					anchor : '70%'
				},{
					fieldLabel : '部署IP',
					name : 'systemIp',
					id : 'systemIp',
					allowBlank : false,
					regex: /^\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}$/,
					regexText: '请输入正确IP地址，例：127.0.0.1',
					anchor : '70%'
				},{
					fieldLabel : '系统描述',
					name : 'systemDesc',
					id : 'systemDesc',
					allowBlank : true,
					xtype : 'textarea',
					width : 500, 
					height : 80, 
			    },{
			    	id : 'userId',
					name : 'userId',
					hidden : true,
			    },{
			    	id : 'windowmode',
					name : 'windowmode',
					hidden : true
			    }]
		});
	
		var serviceUserWindow = new Ext.Window({
			layout : 'fit',
			width : 600,
			height : 300,
			resizable : false,
			draggable : true,
			closeAction : 'hide',
			title : '<span class="commoncss">服务用户新增对话框</span>',
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
			items : [serviceUserPanel],
			buttons :[{
				text : '保存',
				iconCls : 'acceptIcon',
				handler : function(){
					//在此处处理服务对象
					var mode = Ext.getCmp('windowmode').getValue();
					if(mode == 'add'){
						saveServiceItem();
					}else{
						updateServiceItem();
					}
				}
			},{
				text : '关闭',
				iconCls : 'deleteIcon',
				handler : function(){
					serviceUserWindow.hide();
				}
			}]
		});


	
		/**
		 * 新增参数初始化
		 */
		function addInit(){
			var flag = Ext.getCmp('windowmode').getValue();
			if (typeof(flag) != 'undefined') {
				serviceUserPanel.form.getEl().dom.reset();
			} else {
				clearForm(serviceUserPanel.getForm());
			}
			serviceUserWindow.show();
			serviceUserWindow.setTitle('<span class="commoncss">新增服务用户</span>');
			Ext.getCmp('windowmode').setValue('add');
		}
		
		/**
		 * 保存参数数据 
		 */
		function saveServiceItem(){
			if(!serviceUserPanel.form.isValid()){
				return;
			}
			password = Ext.getCmp('password').getValue();
			confirmPassword  = Ext.getCmp('confirmPassword').getValue();
			if(password !=confirmPassword){
				Ext.Msg.alert('提示', '两次输入的密码不匹配,请重新输入!');
				Ext.getCmp('password').setValue('');
				Ext.getCmp('confirmPassword').setValue('');
				return;
			}
			serviceUserPanel.form.submit({
				url : '../serviceUser/saveServiceUser.shtml',
				waitTitle : '提示',
				method : 'POST',
				waitMsg : '正在处理数据,请稍候...',
				success : function(form, action) {
					serviceUserWindow.hide();
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
			serviceUserPanel.getForm().loadRecord(record);
			serviceUserWindow.show();
			serviceUserWindow.setTitle('<span class="commoncss">修改服务用户</span>');
			Ext.getCmp('password').setValue('@@@@@@');
			Ext.getCmp('confirmPassword').setValue('@@@@@@');
			Ext.getCmp('windowmode').setValue('edit');
			Ext.getCmp('userId').setValue(record.get('userId'));
			Ext.getCmp('btnReset').hide();
		}			
		
		/**
		 * 修改服务用户
		 */
		function updateServiceItem(){
			if (!serviceUserPanel.form.isValid()){
				return;
			}
			password = Ext.getCmp('password').getValue();
			confirmPassword  = Ext.getCmp('confirmPassword').getValue();
			if(password == '@@@@@@' && confirmPassword == '@@@@@@'){
				
			}else{
				if(password !=confirmPassword){
					Ext.Msg.alert('提示', '两次输入的密码不匹配,请重新输入!');
					Ext.getCmp('password').setValue('');
					Ext.getCmp('confirmPassword').setValue('');
					return;
				}
		    }
			serviceUserPanel.form.submit({
				url : '../serviceUser/updateService.shtml',
				waitTitle : '提示',
				method : 'POST',
				waitMsg : '正在处理数据,请稍候...',
				success : function(form, action) {
					serviceUserWindow.hide();
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
		function queryServiceUser(){
			store.load({
				params : {
					start : 0,
					limit : bbar.pageSize,
					queryParam : Ext.getCmp('queryServiceUserName').getValue()
				}
			});
		}
		
	   /**
	    * 删除服务用户
	    */
		function deleteServiceUser(){
			var rows = grid.getSelectionModel().getSelections();
			if(Ext.isEmpty(rows)){
				Ext.Msg.alert('提示', '请先选中要删除的项目!');
				return;
			}
			var strChecked = jsArray2JsString(rows,'userId');
			Ext.Msg.confirm('请确认', '确认删除选中的数据吗?', function(btn, text) {
				if(btn == 'yes'){
					Ext.Ajax.request({
						url : '../serviceUser/deleteServiceUser.shtml',
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