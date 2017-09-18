
Ext.onReady(function() {

	var sm = new Ext.grid.CheckboxSelectionModel();
	var cm = new Ext.grid.ColumnModel([new Ext.grid.RowNumberer(), sm,
			{
				header : '数据源编号',
				dataIndex : 'sourceId',
				hidden : true
			},{
				id : 'sourceName',
				header : '数据源名称',
				dataIndex : 'sourceName',
				width : 220
			},{
				header : '数据源类型',
				dataIndex : 'sourceType',
				width :200,
				renderer : function(v) {
					if (v == 32) {
						return "MySQL";
					} else if (v ==30) {
						return "MS SQL Server";
					} else if(v == 35){
						return "Oracle";
					}else if(v == 15){
						return "Hadoop Hive 2";
					}
				}
			}, {
				header : '主机IP',
				dataIndex : 'sourceIp',
				width :200
			}, {
				header : '端口',
				dataIndex : 'sourcePort',
				width :120
			}, {
				header : '数据库名称',
				dataIndex : 'sourceDataBaseName',
				width :200
			},{
				header : '用户名',
				dataIndex : 'sourceUserName',
				width :200
			}]);

	/**
	 * 数据存储
	 */
	var store = new Ext.data.Store({
				proxy : new Ext.data.HttpProxy({
							url : '../datasource/list.shtml'
						}),
				reader : new Ext.data.JsonReader({
							totalProperty : 'total',
							root : 'rows'
						}, [ {
									name : 'sourceType'
								}, {
									name : 'sourceId'
								}, {
									name : 'sourceName'
								}, {
									name : 'sourceUserName'
								}, {
									name : 'sourceDataBaseName'
								}, {
									name : 'sourceIp'
								}, {
									name : 'sourcePort'
								}])
			});

	// 翻页排序时带上查询条件
	store.on('beforeload', function() {
				this.baseParams = {
					companyName : Ext.getCmp('querySourceName').getValue()
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
							fields : ['value', 'text'],
							data : [[10, '10条/页'], [20, '20条/页'],
									[50, '50条/页'], [100, '100条/页'],
									[250, '250条/页'], [500, '500条/页']]
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
								limit : bbar.pageSize,
								repId : rep_combo.getValue()
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
				items : ['-', '&nbsp;&nbsp;', pagesize_combo]
			});

	var rep_combo = new Ext.form.ComboBox({
		fieldLabel : '资源库',
		name : 'sel_rep',
		forceSelection : true,
		hiddenName : 'sel_rep',
		anchor : '60%',
		store : new Ext.data.JsonStore({
			fields : [ 'repId', 'repName' ],
			url : '../datasource/getRepList.shtml'
		}),
		valueField : 'repId',
		displayField : 'repName',
		typeAhead : true,
		mode : 'local',
		triggerAction : 'all',
		selectOnFocus : true
	});
	
	rep_combo.on("select", function(comboBox) {
		store.reload({
					params : {
						start : 0,
						limit : bbar.pageSize,
						repId : rep_combo.getValue()
					}
				});
		Ext.getCmp('querySourceName').setValue('');
	});
	
	rep_combo.store.on("load", function(comboBox) {
		rep_combo.setValue(comboBox.reader.jsonData[0].repId);
		store.load({
			params : {
				start : 0,
				limit : bbar.pageSize,
				repId : rep_combo.getValue()
			}
		});
	});
	
	var grid = new Ext.grid.GridPanel({
				title : '<span class="commoncss">数据源管理</span>',
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
				tbar : [rep_combo, '-', {
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
								deleteDataSource();
							}
						} ,  '-', '->',
						new Ext.form.TextField({
									id : 'querySourceName',
									name : 'querySourceName',
									emptyText : '数据源名称',
									enableKeyEvents : true,
									listeners : {
										specialkey : function(field, e) {
											if (e.getKey() == Ext.EventObject.ENTER) {
												queryParamItem();
											}
										}
									},
									width : 150
								}), {
							text : '查询',
							iconCls : 'previewIcon',
							handler : function() {
								queryParamItem();
							}
						}, '-', {
							text : '刷新',
							iconCls : 'arrow_refreshIcon',
							handler : function() {
								store.reload({
									params : {
										start : 0,
										limit : bbar.pageSize,
										repId : rep_combo.getValue()
									}
								});
							}
						}],
				bbar : bbar
			});
	rep_combo.store.load();
	
	grid.on('rowdblclick', function(grid, rowIndex, event) {
				editInit();
			});
	grid.on('sortchange', function() {
				// grid.getSelectionModel().selectFirstRow();
			});

	bbar.on("change", function() {
				// grid.getSelectionModel().selectFirstRow();
			});

	var sourceCombox = new Ext.form.ComboBox({
		fieldLabel : '数据源类型',
		emptyText : '数据源类型',
		name : 'sourceTypeName',
		forceSelection : true,
		hiddenName : 'sourceType',
		anchor : '60%',
		store : new Ext.data.JsonStore({
			fields : [ 'sourceType', 'sourceTypeName' ],
			url : '../datasource/getDbTypeList.shtml'
		}),
		valueField : 'sourceType',
		displayField : 'sourceTypeName',
		typeAhead : true,
		mode : 'local',
		triggerAction : 'all',
		selectOnFocus : true,
		allowBlank : false
	});

	
	var addParamFormPanel = new Ext.form.FormPanel({
				id : 'addParamFormPanel',
				name : 'addParamFormPanel',
				defaultType : 'textfield',
				labelAlign : 'right',
				labelWidth : 100,
				frame : false,
				bodyStyle : 'padding:5 5 0',
				items : [{
							fieldLabel : '数据源名称',
							name : 'sourceName',
							id : 'sourceName',
							allowBlank : false,
							anchor : '60%'
						},sourceCombox,{
							fieldLabel : '主机IP',
							name : 'sourceIp',
							id : 'sourceIp',
							allowBlank : false,
							regex: /^[a-zA-Z0-9\.]*$/,
							regexText: '请输入正确IP地址、主机名或域名',
							anchor : '60%'
						},{
							fieldLabel : '端口',
							name : 'sourcePort',
							id : 'sourcePort',
							allowBlank : false,
							anchor : '60%'
						},{
							fieldLabel : '数据库名称',
							name : 'sourceDataBaseName',
							id : 'sourceDataBaseName',
							allowBlank : false,
							anchor : '60%'
						}, {
							fieldLabel : '用户名',
							name : 'sourceUserName',
							id : 'sourceUserName',
							allowBlank : false,
							anchor : '60%'
						}, {
							fieldLabel : '密码',
							name : 'sourcePassword',
							id : 'sourcePassword',
							inputType : 'password',
							allowBlank : false,
							anchor : '60%'
						}, {
							id : 'sourceId',
							name : 'sourceId',
							hidden : true
						}, {
							id : 'windowmode',
							name : 'windowmode',
							hidden : true
						}]
			});

	var addParamWindow = new Ext.Window({
				layout : 'fit',
				width : 500,
				height : 300,
				resizable : false,
				draggable : true,
				closeAction : 'hide',
				title : '<span class="commoncss">新增数据源</span>',
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
				items : [addParamFormPanel],
				buttons : [{
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
					text : '测试',
					//iconCls : 'acceptIcon',
					handler : function() {
						testDatabaseParam();
					}
				},{
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
				}]
			});

	/**
	 * 布局
	 */
	var viewport = new Ext.Viewport({
				layout : 'border',
				items : [grid]
			});

	/**
	 * 查询参数
	 */
	function queryParamItem() {
		store.load({
					params : {
						start : 0,
						limit : bbar.pageSize,
						repId : rep_combo.getValue(),
						queryParam : Ext.getCmp('querySourceName').getValue()
					}
				});
	}

	/**
	 * 新增参数初始化
	 */
	function addInit() {
		sourceCombox.store.reload({
			params : {
				repId : rep_combo.getValue()
			}
		});
		Ext.getCmp('btnReset').hide();
		var flag = Ext.getCmp('windowmode').getValue();
		if (typeof(flag) != 'undefined') {
			addParamFormPanel.form.getEl().dom.reset();
		} else {
			clearForm(addParamFormPanel.getForm());
		}
		addParamWindow.show();
		addParamWindow.setTitle('<span class="commoncss">新增数据源信息</span>');
		Ext.getCmp('windowmode').setValue('add');
	}

	/**
	 * 保存参数数据
	 */
	function saveParamItem() {
		
		addParamFormPanel.form.submit({
					url : '../datasource/saveSource.shtml?repId=' + rep_combo.getValue(),
					waitTitle : '提示',
					method : 'POST',
					waitMsg : '正在处理数据,请稍候...',
					success : function(form, action) {
						Ext.MessageBox.alert('提示信息', action.result.msg);
						addParamWindow.hide();
						store.reload({
							params : {
								start : 0,
								limit : bbar.pageSize,
								repId : rep_combo.getValue()
							}
						});
						form.reset();
					},
					failure : function(form, action) {
						var msg = action.result.msg;
						Ext.MessageBox.alert('提示', '数据保存失败:<br>' + msg);
					}
				});
	}

	function editInit() {
		var record = grid.getSelectionModel().getSelected();
		if (Ext.isEmpty(record)) {
			Ext.MessageBox.alert('提示', '请先选中要修改的数据源');
			return;
		}
		addParamFormPanel.getForm().loadRecord(record);
		addParamWindow.show();
		addParamWindow.setTitle('<span class="commoncss">修改数据源信息</span>');
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
					url : '../datasource/updateSource.shtml?repId=' + rep_combo.getValue(),
					waitTitle : '提示',
					method : 'POST',
					waitMsg : '正在处理数据,请稍候...',
					success : function(form, action) {
						Ext.MessageBox.alert('提示信息', action.result.msg);
						addParamWindow.hide();
						store.reload({
							params : {
								start : 0,
								limit : bbar.pageSize,
								repId : rep_combo.getValue()
							}
						});
						form.reset();
					},
					failure : function(form, action) {
						var msg = action.result.msg;
						Ext.MessageBox.alert('提示', '数据修改失败:<br>' + msg);
					}
				});
	}
	
	
	/**
	 * 删除数据库
	 */
	function deleteDataSource(){
		var rows = grid.getSelectionModel().getSelections();
		if(Ext.isEmpty(rows)){
			Ext.Msg.alert('提示', '请先选中要删除的项目!');
			return;
		}
		var strChecked = jsArray2JsString(rows,'sourceId');
		Ext.Msg.confirm('请确认', '确认删除选中的数据源吗?', function(btn, text) {
			if(btn == 'yes'){
				Ext.Ajax.request({
					url : '../datasource/deleteSource.shtml?repId=' + rep_combo.getValue(),
					success : function(response){
						var resultArray = Ext.util.JSON.decode(response.responseText);
						store.reload({
							params : {
								start : 0,
								limit : bbar.pageSize,
								repId : rep_combo.getValue()
							}
						});
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
	
	/**
	 * 测试数据库连接参数
	 */
	function testDatabaseParam() {
		addParamFormPanel.form.submit({
					url : '../datasource/testSource.shtml?repId=' + rep_combo.getValue(),
					waitTitle : '提示',
					method : 'POST',
					waitMsg : '正在处理数据,请稍候...',
					success : function(form, action) {
						Ext.MessageBox.alert('提示信息', action.result.msg);
					},
					failure : function(form, action) {
						var msg = action.result.msg;
						Ext.MessageBox.alert('提示', '数据库连接失败:<br>' + msg);
					}
	    });
	}

	
	
});