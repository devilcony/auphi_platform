
Ext.onReady(function() {

	var sm = new Ext.grid.CheckboxSelectionModel();
	var cm = new Ext.grid.ColumnModel([new Ext.grid.RowNumberer(), sm,
			{
				header : '表名',
				dataIndex : 'tableName',
				width : 120
			}, {
				id : 'tableDesc',
				header : '表名备注',
				dataIndex : 'tableDesc',
				width : 120
			}]);

	/**
	 * 数据存储
	 */
	var store = new Ext.data.Store({
				proxy : new Ext.data.HttpProxy({
							url : '../exportOracleTable/list.shtml'
						}),
				reader : new Ext.data.JsonReader({
							totalProperty : 'total',
							root : 'rows'
							}, [ {
								name : 'tableName'
							}, {
								name : 'tableDesc'
							}
							])
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
				title : '<span class="commoncss">导出数据库用户表</span>',
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
							text : '导出用户表结构',
							iconCls : 'page_addIcon',
							handler : function() {
								exportTable();
							}
						}
						/*, '-', {
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
						}*/
						],
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
	 * 导出oracle用户表
	 */
	function exportTable(){
		Ext.Msg.confirm('请慎重', '确认要导出用户表吗?', function(btn, text) {
					if (btn == 'yes') {alert(123);
						Ext.Ajax.request({
									url : '../exportOracleTable/exportTable.shtml',
									success : function(response) {
										var resultArray = Ext.util.JSON.decode(response.responseText);
										store.reload();
										Ext.Msg.alert('提示', resultArray.msg);
									},
									failure : function(response) {
										var resultArray = Ext.util.JSON
												.decode(response.responseText);
										Ext.Msg.alert('提示', resultArray.msg);
									}
								});
					}
				});
	}
});