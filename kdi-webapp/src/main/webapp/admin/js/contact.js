/**
 * 全局参数表管理
 * 
 */
Ext.onReady(function() {

	var sm = new Ext.grid.CheckboxSelectionModel();
	var cm = new Ext.grid.ColumnModel([new Ext.grid.RowNumberer(), sm,
			{
				header : '联系人编号',
				dataIndex : 'contactid',
				hidden : true
			},{
				header : '公司编号',
				dataIndex : 'companyid',
				hidden : true
			}, {
				id : 'contactName',
				header : '联系人姓名',
				dataIndex : 'contactName',
				width : 120
			}, {
				header : '性别',
				dataIndex : 'sex',
				width :40
			}, {
				header : '生日',
				dataIndex : 'brithday',
				width :120
			}, {
				header : '所属公司',
				dataIndex : 'companyName',
				width :150
			}, {
				header : '职位',
				dataIndex : 'position',
				width :120
			}, {
				header : '电话',
				dataIndex : 'telphone',
				width :120
			}, {
				header : '手机',
				dataIndex : 'phoneNumber',
				width :120
			}, {
				header : '个人介绍',
				dataIndex : 'introduce',
				hidden : true
			}, {
				header : '电子邮件',
				dataIndex : 'email',
				width :120
			}, {
				header : '信息登记日期',
				dataIndex : 'recordDate',
				width :120
			}, {
				header : '信息登记人',
				dataIndex : 'recordor',
				width :120
			},{
				header : '联系人备注信息',
				dataIndex : 'contactRemark',
				hidden : true
			}, {
				id : 'contactMark',
				header : '联系人标示',
				dataIndex : 'contactMark',
				hidden : true
			}]);

	/**
	 * 数据存储
	 */
	var store = new Ext.data.Store({
				proxy : new Ext.data.HttpProxy({
							url : '../contact/list.shtml'
						}),
				reader : new Ext.data.JsonReader({
							totalProperty : 'total',
							root : 'rows'
						}, [{
							name : 'contactid'
								}, {
									name : 'companyid'
								}, {
									name : 'contactName'
								}, {
									name : 'companyName'
								}, {
									name : 'sex'
								}, {
									name : 'brithday'
								}, {
									name : 'age'
								}, {
									name : 'position'
								}, {
									name : 'telphone'
								}, {
									name : 'phoneNumber'
								}, {
									name : 'introduce'
								}, {
									name : 'email'
								} ,{
							name : 'recordDate'
						}, {
							name : 'recordor'
						}, {
							name : 'contactMark'
						}])
			});

	// 翻页排序时带上查询条件
	store.on('beforeload', function() {
				this.baseParams = {
					contactName : Ext.getCmp('contactName_1').getValue()
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
				items : ['-', '&nbsp;&nbsp;', pagesize_combo]
			});

	var grid = new Ext.grid.GridPanel({
				title : '<span class="commoncss">客户信息管理</span>',
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
				tbar : [{
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
						},  '-', '->',
						new Ext.form.TextField({
									id : 'contactName_1',
									name : 'contactName_1',
									emptyText : '请输入客户名称',
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
	grid.on('rowdblclick', function(grid, rowIndex, event) {
				editInit();
			});
	grid.on('sortchange', function() {
				// grid.getSelectionModel().selectFirstRow();
			});

	bbar.on("change", function() {
				// grid.getSelectionModel().selectFirstRow();
			});

	var sexComboData = [ [ '未知', '未知' ],[ '男', '男' ], [ '女', '女' ] ];
	sexCombo = new Ext.form.ComboBox( {
		store : new Ext.data.SimpleStore( {
			fields : [ "sex", "sexName" ],
			data : sexComboData
		}),
		valueField : "sex",
		displayField : "sexName",
		mode : 'local',
		forceSelection : true,
		hiddenName : 'sex',
		emptyText : '性别',
		editable : false,
		value : 0,
		triggerAction : 'all',
		fieldLabel : '性别',
		autoWidth : true,
		name : ''
	});
	
	
	var companyData = new Ext.data.Store({
		proxy : new Ext.data.HttpProxy({
					url : '../company/combo.shtml'
				}),
		autoLoad:true,
		reader : new Ext.data.JsonReader({}, ['companyid','companyName'])
	});
	companyCombo = new Ext.form.ComboBox({
		store : companyData,
		valueField : "companyid",
		mode : 'remote',
		displayField : "companyName",
		forceSelection : true,
		emptyText : '选择公司',
		editable : false,
		triggerAction : 'all',
		fieldLabel : '所属公司',
		autoWidth : true,
		hiddenName : "companyid",
		autoShow : true,
		selectOnFocus : true,
		name : "companyid"
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
							fieldLabel : '联系人姓名',
							name : 'contactName',
							id : 'contactName',
							allowBlank : false,
							anchor : '60%'
						},sexCombo, {
							fieldLabel : '生日',
							name : 'brithday',
							id : 'brithday',
							format:'Y-m-d', 
							xtype : 'datefield',
							allowBlank : false,
							anchor : '60%'
						},companyCombo, {
							fieldLabel : '职位',
							name : 'position',
							id : 'position',
							allowBlank : false,
							anchor : '60%'
						}, {
							fieldLabel : '办公电话',
							name : 'telphone',
							id : 'telphone',
							allowBlank : false,
							anchor : '60%'
						}, {
							fieldLabel : '手机号码',
							name : 'phoneNumber',
							id : 'phoneNumber',
							allowBlank : false,
							anchor : '60%'
						}, {
							fieldLabel : '电子邮件',
							name : 'email',
							id : 'email',
							allowBlank : false,
							anchor : '60%'
						}, {
							fieldLabel : '信息登记人',
							name : 'recordor',
							id : 'recordor',
							allowBlank : false,
							anchor : '60%'
						}, {
							fieldLabel : '联系人标示',
							name : 'contactMark',
							id : 'contactMark',
							allowBlank : true,
							anchor : '60%'
						}, {
							fieldLabel : '个人介绍',
							name : 'introduce',
							id : 'introduce',
							allowBlank : true,
							anchor : '99%',
							xtype : 'htmleditor',
							width : 500, 
							height : 150, 
						}, {
							id : 'contactid',
							name : 'contactid',
							hidden : true
						}, {
							id : 'windowmode',
							name : 'windowmode',
							hidden : true
						}]
			});

	var addParamWindow = new Ext.Window({
				layout : 'fit',
				width : 600,
				height : 500,
				resizable : false,
				draggable : true,
				closeAction : 'hide',
				title : '<span class="commoncss">新增联系人</span>',
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
						contactName : Ext.getCmp('contactName_1').getValue()
					}
				});
	}

	/**
	 * 新增参数初始化
	 */
	function addInit() {
		Ext.getCmp('btnReset').hide();
		var flag = Ext.getCmp('windowmode').getValue();
		if (typeof(flag) != 'undefined') {
			addParamFormPanel.form.getEl().dom.reset();
		} else {
			clearForm(addParamFormPanel.getForm());
		}
		addParamWindow.show();
		addParamWindow.setTitle('<span class="commoncss">新增联系人信息</span>');
		Ext.getCmp('windowmode').setValue('add');

	}

	/**
	 * 保存参数数据
	 */
	function saveParamItem() {
		
		addParamFormPanel.form.submit({
					url : '../contact/saveContact.shtml',
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
		var strChecked = jsArray2JsString(rows, 'contactid');
		Ext.Msg.confirm('请确认', '确认删除选中的全局参数吗?', function(btn, text) {
					if (btn == 'yes') {
	
						Ext.Ajax.request({
									url : '../contact/deleteContact.shtml',
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
			Ext.MessageBox.alert('提示', '请先选中要修改的项目');
			return;
		}
		addParamFormPanel.getForm().loadRecord(record);
		addParamWindow.show();
		addParamWindow
				.setTitle('<span class="commoncss">修改联系人信息</span>');
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
					url : '../contact/updateContact.shtml',
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
});