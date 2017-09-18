
Ext.onReady(function() {

	var sm = new Ext.grid.CheckboxSelectionModel();
	var cm = new Ext.grid.ColumnModel([new Ext.grid.RowNumberer(), sm,
			{
				header : '公司编号',
				dataIndex : 'companyid',
				hidden : true
			}, {
				id : 'companyName',
				header : '公司名称',
				dataIndex : 'companyName',
				width : 120
			}, {
				header : '注册资金',
				dataIndex : 'capital',
				width :120
			}, {
				header : '公司电话',
				dataIndex : 'telphone',
				width :120
			}, {
				header : '公司传真',
				dataIndex : 'fax',
				width :120
			}, {
				header : '公司地址',
				dataIndex : 'address',
				width :120
			}, {
				header : '纳税号',
				dataIndex : 'revenueNumber',
				width :120
			}, {
				header : '银行账号',
				dataIndex : 'bankNumber',
				width :120
			}, {
				header : '开户行',
				dataIndex : 'bankName',
				width :120
			}, {
				header : '法人代表',
				dataIndex : 'corporation',
				width :120
			}, {
				header : '企业性质',
				dataIndex : 'property',
				width :120
			}, {
				header : '企业资质',
				dataIndex : 'aptitude',
				width :120
			},{
				header : '企业类型',
				dataIndex : 'companyType',
				width :120
			}, {
				id : 'companyDesc',
				header : '企业介绍',
				dataIndex : 'companyDesc',
				hidden : true
			}]);

	/**
	 * 数据存储
	 */
	var store = new Ext.data.Store({
				proxy : new Ext.data.HttpProxy({
							url : '../company/list.shtml'
						}),
				reader : new Ext.data.JsonReader({
							totalProperty : 'total',
							root : 'rows'
						}, [ {
									name : 'companyid'
								}, {
									name : 'companyName'
								}, {
									name : 'capital'
								}, {
									name : 'telphone'
								}, {
									name : 'fax'
								}, {
									name : 'address'
								}, {
									name : 'revenueNumber'
								}, {
									name : 'bankNumber'
								}, {
									name : 'bankName'
								}, {
									name : 'corporation'
								} ,{
							name : 'property'
						}, {
							name : 'aptitude'
						}, {
							name : 'companyType'
						}, {
							name : 'companyDesc'
						}])
			});

	// 翻页排序时带上查询条件
	store.on('beforeload', function() {
				this.baseParams = {
					companyName : Ext.getCmp('companyName_1').getValue()
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
									id : 'companyName_1',
									name : 'companyName_1',
									emptyText : '请输入公司名称',
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

	var propertyComboData = [ [ '民营企业', '民营企业' ],[ '港澳台企业', '港澳台企业' ], [ '外资企业', '外资企业' ], [ '个体企业', '个体企业' ], [ '合资企业', '合资企业' ] ];
	propertyCombo = new Ext.form.ComboBox( {
		store : new Ext.data.SimpleStore( {
			fields : [ "property", "propertyName" ],
			data : propertyComboData
		}),
		valueField : "property",
		displayField : "propertyName",
		mode : 'local',
		forceSelection : true,
		hiddenName : 'property',
		emptyText : '企业性质',
		editable : false,
		value : 0,
		triggerAction : 'all',
		fieldLabel : '企业性质',
		autoWidth : true,
		name : ''
	});
	
	var companyTypeComboData = [ [ '信息产业', '信息产业' ],[ '制造业', '制造业' ], [ '服务业', '服务业' ], [ '建筑业', '建筑业' ], [ '化工业', '化工业' ] ];
	companyTypeCombo = new Ext.form.ComboBox( {
		store : new Ext.data.SimpleStore( {
			fields : [ "companyType", "companyTypeName" ],
			data : companyTypeComboData
		}),
		valueField : "companyType",
		displayField : "companyTypeName",
		mode : 'local',
		forceSelection : true,
		hiddenName : 'companyType',
		emptyText : '企业性质',
		editable : false,
		value : 0,
		triggerAction : 'all',
		fieldLabel : '企业性质',
		autoWidth : true,
		name : ''
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
							fieldLabel : '公司名称',
							name : 'companyName',
							id : 'companyName',
							allowBlank : false,
							anchor : '60%'
						}, {
							fieldLabel : '注册资金',
							name : 'capital',
							id : 'capital',
							allowBlank : false,
							anchor : '60%'
						},{
							fieldLabel : '公司电话',
							name : 'telphone',
							id : 'telphone',
							allowBlank : false,
							anchor : '60%'
						}, {
							fieldLabel : '公司传真',
							name : 'fax',
							id : 'fax',
							allowBlank : false,
							anchor : '60%'
						}, {
							fieldLabel : '公司地址',
							name : 'address',
							id : 'address',
							allowBlank : false,
							anchor : '60%'
						}, {
							fieldLabel : '纳税号',
							name : 'revenueNumber',
							id : 'revenueNumber',
							allowBlank : false,
							anchor : '60%'
						},{
							fieldLabel : '银行账号',
							name : 'bankNumber',
							id : 'bankNumber',
							allowBlank : false,
							anchor : '60%'
						}, {
							fieldLabel : '开户行',
							name : 'bankName',
							id : 'bankName',
							allowBlank : false,
							anchor : '60%'
						}, {
							fieldLabel : '法人代表',
							name : 'corporation',
							id : 'corporation',
							allowBlank : true,
							anchor : '60%'
						},propertyCombo,  {
							fieldLabel : '企业资质',
							name : 'aptitude',
							id : 'aptitude',
							allowBlank : true,
							anchor : '60%'
						},companyTypeCombo,{
							fieldLabel : '企业介绍',
							name : 'companyDesc',
							id : 'companyDesc',
							allowBlank : true,
							anchor : '99%',
							xtype : 'htmleditor',
							width : 500, 
							height : 150, 
						},{
							id : 'companyid',
							name : 'companyid',
							hidden : true,
							value : '1'
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
				title : '<span class="commoncss">新增公司</span>',
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
						companyName : Ext.getCmp('companyName').getValue()
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
		addParamWindow.setTitle('<span class="commoncss">新增公司信息</span>');
		Ext.getCmp('windowmode').setValue('add');

	}

	/**
	 * 保存参数数据
	 */
	function saveParamItem() {
		
		addParamFormPanel.form.submit({
					url : '../company/saveCompany.shtml',
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
		var strChecked = jsArray2JsString(rows, 'companyid');
		Ext.Msg.confirm('请确认', '确认删除选中的数据吗?', function(btn, text) {
					if (btn == 'yes') {
						Ext.Ajax.request({
									url : '../company/deleteCompany.shtml',
									success : function(response) {
										var resultArray = Ext.util.JSON.decode(response.responseText);
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


	function editInit() {
		var record = grid.getSelectionModel().getSelected();
		if (Ext.isEmpty(record)) {
			Ext.MessageBox.alert('提示', '请先选中要修改的项目');
			return;
		}
		addParamFormPanel.getForm().loadRecord(record);
		addParamWindow.show();
		addParamWindow
				.setTitle('<span class="commoncss">修改公司信息</span>');
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
					url : '../company/updateCompany.shtml',
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