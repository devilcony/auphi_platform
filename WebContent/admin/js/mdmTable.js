 
Ext.onReady(function() {
	// 定义列模型
		var sm = new Ext.grid.CheckboxSelectionModel();
		var rownum = new Ext.grid.RowNumberer( {
			header : 'NO',
			width : 28
		});
		var cm = new Ext.grid.ColumnModel( [ rownum, sm, 
		{
			header : '表编码',
			dataIndex : 'id_table',
			hidden : true
		},{
			header : '模型编码', 
			dataIndex : 'id_model', 
			hidden : true
		},{
			header : '模型名称', 
			dataIndex : 'id_model_show', 
			sortable : true,
			width : 100
		}, {
			header : '数据源编码',
			dataIndex : 'id_database',
			hidden : true
		}, {
			header : '数据源名称',
			dataIndex : 'id_database_show',
			sortable : true,
			width : 100
		}, {
			header : '模式名称',
			dataIndex : 'schema_name',
			sortable : true,
			width : 100
		},{
			header : '表名', 
			dataIndex : 'table_name', 
			sortable : true,
			width : 100
		},{
			header : '状态', 
			dataIndex : 'table_status', 
			sortable : true,
			width : 100
		},{
			header : '操作', 
			dataIndex : 'table_operation', 
			sortable : true,
			width : 100,
			renderer: function (data, metadata, record, rowIndex, columnIndex, store) {  
      			var id_table = store.getAt(rowIndex).get('id_table');  
				return "<a href='#' onclick='showStatWindow("+id_table+")'>统计</a>&nbsp;&nbsp;&nbsp;&nbsp;" +
				       "<a href='#' onclick='modifyMdmTable()'>编辑数据</a>";
				//return "<button type='button' onclick='showStatWindow("+id_table+")'>统计</button>";
				//return "<a href='http://www.baidu.com' target='_blank'>统计</a>"; 
  			}  
		} ]);
		
		 
		var store = new Ext.data.Store( {
			proxy : new Ext.data.HttpProxy( {
				url : '../mdmTable/query.shtml'
			}),
			reader : new Ext.data.JsonReader( {
				totalProperty : 'total',
				root : 'rows' 
			}, [ {
					name : 'id_table' 
				}, {
					name : 'id_model' 
				}, {
					name : 'id_model_show' 
				}, {
					name : 'id_database'
				}, {
					name : 'id_database_show'
				}, {
					name : 'schema_name'
				}, {
					name : 'table_name'
				}]),
			listeners: {
				datachanged: function() {
					//autoCheckGridHead(Ext.getCmp('id_grid_sfxm'));
				}
			}
		});
		store.load( {
			params : {
				start : 0,
				limit : 20
			}
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
			width : 100
		});		
		
		var number = parseInt(pagesize_combo.getValue());
		// 改变每页显示条数reload数据
		pagesize_combo.on("select", function(comboBox) {
			bbar.pageSize = parseInt(comboBox.getValue());
			number = parseInt(comboBox.getValue());
			store.reload( {
				params : {
					start : 0,
					limit : bbar.pageSize
				}
			});
		});

		// 分页工具栏
		var bbar = new Ext.PagingToolbar( {
			pageSize : number,
			store : store,
			displayInfo : true,
			displayMsg : '显示{0}条到{1}条,共{2}条',
			plugins : new Ext.ux.ProgressBarPager(), // 分页进度条
			emptyMsg : "没有符合条件的记录",
			items : [ '-', '&nbsp;&nbsp;', pagesize_combo ]
		});

		// 表格工具栏
		var tbar = new Ext.Toolbar({
			items : [{
						text : '新增',
						iconCls : 'page_addIcon',
						id : 'id_tbi_add',
						handler : function() {
							addItem();
						}
					},'-',{
						text : '删除',
						id : 'tbi_del',
						iconCls : 'page_delIcon',
						handler : function() {
							if(checkBeforeDelete(grid)) {
								deleteItem();
							}
						}
					},'-',{
						text : '编辑',
						id : 'tbi_edit',
						iconCls : 'page_edit_1Icon',
						handler : function() {
							
							if(checkBeforeUpdate(grid)) {
								updateItem();
							}
							
						}
					},'-',{
						text : '检查',
						id : 'tbi_chk',
						iconCls : 'page_delIcon',
						handler : function() {
							if(checkBeforeDelete(grid)) {
								checkItem();
							}
						}
					}
//					,  '-', '->',
//					new Ext.form.TextField({
//								id : 'queryName',
//								name : 'queryName',
//								emptyText : '查询的名称',
//								enableKeyEvents : true,
//								listeners : {
//									specialkey : function(field, e) {
//										if (e.getKey() == Ext.EventObject.ENTER) {
//											queryItem();
//										}
//									}
//								},
//								width : 150
//					}), 
//					{
//						text : '查询',
//						iconCls : 'previewIcon',
//						handler : function() {
//							queryItem();
//						}
//					},
//					'-', {
//						text : '刷新',
//						iconCls : 'arrow_refreshIcon',
//						handler : function() {
//							store.reload();
//						}
//					}
					]
		});

		// 表格实例
		var grid = new Ext.grid.GridPanel( {
			height : 500,
			id : 'id_grid_sfxm',
			autoScroll : true,
			frame : true,
			region : 'center', 
			store : store, 
			viewConfig : {
				forceFit : true
			},
			stripeRows : true, 
			cm : cm, 
			sm : sm,
			tbar : tbar, 
			bbar : bbar,
			loadMask : {
				msg : '正在加载表格数据,请稍等...'
			}
		});

		// 监听行选中事件
		grid.on('rowclick', function(pGrid, rowIndex, event) {
			Ext.getCmp('tbi_edit').enable();
			Ext.getCmp('tbi_del').enable();
		});

		grid.on('rowdblclick', function(grid, rowIndex, event) {
			updateItem();
		});
		
		//模型
		var modelStore = new Ext.data.JsonStore({
			fields: ['id_model', 'model_name'],
			url : "../mdmModel/query4ComboBox.shtml",
			autoLoad:true,
			root : ""
		});		
		var modelComboBox = new Ext.form.ComboBox({
			fieldLabel : '模型',
			emptyText:'请选择模型',
			hiddenName : "id_model",
			forceSelection: true,
			anchor : '100%',
    		store: modelStore,
			valueField : "id_model",
			displayField : "model_name",
			typeAhead: true,
			mode: 'local',
			triggerAction: 'all',
			selectOnFocus : true,// 设置用户能不能自己输入,true为只能选择列表中有的记录
			allowBlank : false,
			listeners: {      
				select : function(modelComboBox, record,index){ 
					var id_model = modelComboBox.value;
				}    
			}    			
		});	
		
		//数据源
		var datasourceStore = new Ext.data.JsonStore({
			fields: ['sourceId', 'sourceName'],
			url : "../datasource/getDataSourceList.shtml",
			autoLoad:true,
			root : ""
		});		
		var datasourceComboBox = new Ext.form.ComboBox({
			fieldLabel : '数据源',
			emptyText:'请选择数据源',
			hiddenName : "id_database",
			forceSelection: true,
			anchor : '100%',
    		store: datasourceStore,
			valueField : "sourceId",
			displayField : "sourceName",
			typeAhead: true,
			mode: 'local',
			triggerAction: 'all',
			selectOnFocus : true,// 设置用户能不能自己输入,true为只能选择列表中有的记录
			allowBlank : false,
			resizable : true,
			listWidth: 205,
			listeners: {      
				select : function(datasourceComboBox, record,index){ 
					var id_database = datasourceComboBox.value;
					schemaNameStore.load({
						params : {id_database : id_database}
					});
					
					tableNameStore.load({
						params : {
							id_database : id_database,
							schema_name : ""
						}
					});
				}    
			}    			
		});	
		
		//模式名
		var schemaNameStore = new Ext.data.JsonStore({
			fields: ['value', 'text'],
			url : "../mdmTable/getSchemaName.shtml",
			autoLoad:true,
			root : ""
		});		
		var schemaNameComboBox = new Ext.form.ComboBox({
			fieldLabel : '模式名',
			emptyText:'请选择模式名',
			hiddenName : "schema_name",
			forceSelection: false,
			anchor : '100%',
    		store: schemaNameStore,
			valueField : "value",
			displayField : "text",
			typeAhead: true,
			mode: 'local',
			triggerAction: 'all',
			selectOnFocus : true,// 设置用户能不能自己输入,true为只能选择列表中有的记录
			allowBlank : true,
			listeners: {      
				select : function(schemaNameComboBox, record,index){ 
					var schemaName = schemaNameComboBox.value;
					tableNameStore.load({
						params : {
							id_database : datasourceComboBox.value,
							schema_name : schemaName
						}
					});
				}    
			}    			
		});	
		
		//表名
		var tableNameStore = new Ext.data.JsonStore({
			fields: ['value', 'text'],
			url : "../mdmTable/getTableName.shtml",
			autoLoad:true,
			root : ""
		});		
		var tableNameComboBox = new Ext.form.ComboBox({
			id : 'table_name_comboBox',
			name :'table_name_comboBox',
			fieldLabel : '表名',
			emptyText:'请选择表名',
			hiddenName : "table_name",
			forceSelection: true,
			anchor : '100%',
    		store: tableNameStore,
			valueField : "value",
			displayField : "text",
			typeAhead: true,
			mode: 'local',
			triggerAction: 'all',
			selectOnFocus : true,// 设置用户能不能自己输入,true为只能选择列表中有的记录
			allowBlank : false,
			listeners: {      
				select : function(tableNameComboBox, record,index){ 
					var tableName = tableNameComboBox.value;
				}    
			}    			
		});											
		
		//表单
		var addForm = new Ext.form.FormPanel( {
			id : 'codeForm',
			name : 'codeForm',
			collapsible : false,
			border : true,
			labelWidth : 50, // 标签宽度
			frame : true, // 是否渲染表单面板背景色
			labelAlign : 'right', // 标签对齐方式
			bodyStyle : 'padding:0 0 0 0', // 表单元素和表单面板的边距
			buttonAlign : 'center',
			//height : 700,
			items : [{
				layout : 'column',
				border : false,
				anchor : '100%',
				items : [{
							columnWidth:.01,  //该列占用的宽度，标识为50％
							layout: 'form',
							defaultType : 'textfield',
							border:false,
							items: [{                     //这里可以为多个Item，表现出来是该列的多行
								id:'id_table',
								name : 'id_table', // name:后台根据此name属性取值
								hidden : true
							},{
								id : 'windowmode',
								name : 'windowmode',
								hidden : true
							}]
						},{
							columnWidth:.33,
							layout: 'form',
							border:false,
							items: [modelComboBox]
						},{
							columnWidth:.33,
							layout: 'form',
							border:false,
							items: [datasourceComboBox]
						},{
							columnWidth:.33,
							layout: 'form',
							border:false,
							items: [schemaNameComboBox]
						},{
							columnWidth:1,
							layout:'form',
							border:false,
							items:[{
									xtype:'radiogroup',
									columns: 2,
									id:'tableMode',
            						items: [
                						{boxLabel: '创建/更新表', id:'createTab',name: 'rb-horiz', inputValue: '1', checked: true},
                						{boxLabel: '选择表', id:'selectTab',name: 'rb-horiz', inputValue: '2'}
									],
									listeners:{
										//通过change触发
										change: function(g , newValue , oldValue){
											var rg = Ext.getCmp('tableMode');
											var r = rg.getValue();
											//关键
											var value = r.inputValue;
											//alert(value);
											if(value == '1'){
												Ext.getCmp('table_name_text').setDisabled(false);
												Ext.getCmp('sql').setDisabled(false);
												Ext.getCmp('createSQL').setDisabled(false);
												Ext.getCmp('runSQL').setDisabled(false);

												Ext.getCmp('table_name_comboBox').setDisabled(true);
												Ext.getCmp('btnCheck').setDisabled(true);


											}else{
												Ext.getCmp('table_name_text').setDisabled(true);
												Ext.getCmp('sql').setDisabled(true);
												Ext.getCmp('createSQL').setDisabled(true);
												Ext.getCmp('runSQL').setDisabled(true);

												Ext.getCmp('table_name_comboBox').setDisabled(false);
												Ext.getCmp('btnCheck').setDisabled(false);
											}

										}
									}
								}
							]
													
						},{
							columnWidth:.99,
							layout : 'form',
							labelWidth : 50, // 标签宽度
							defaultType : 'textfield',
							border : false,
							anchor : '100%',
							items : [{	
										id:'createTableFieldset',
										xtype:'fieldset',
										title: '创建/更新表',
										autoHeight:true,
										anchor : '99%',
										collapsed: false,
										items :[{
													xtype:'textfield',
													id:'table_name_text',
													fieldLabel : '表名', // 标签
													name : 'table_name', // name:后台根据此name属性取值
													maxLength : 50,
													allowBlank : false,
													anchor : '50%',
													listeners: {
														//blur: handleSpace
													}
												},
												{
													layout : 'column',
													border : false,
													anchor : '100%',
													items : [{
																columnWidth:.8,
																layout: 'form',
																border:false,
																items: [{
																	id:'sql',
																	fieldLabel : 'SQL', // 标签
																	name : 'sql', // name:后台根据此name属性取值
																	xtype:'textarea', 
																	allowBlank : true,
																	anchor : '100%',
																	height: 150,
																	listeners: {
																		//blur: handleSpace
																	}
																}]
															},{
																columnWidth:.2,
																layout: 'form',
																border:false,
																items: [
																new Ext.Button({
																	text:'生成SQL',
																	id:'createSQL',
																	name:'createSQL',
																	handler:function(){
																		 Ext.Ajax.request( {
																			url : '../mdmTable/createSQL.shtml',
																			success : function(response) {
																				var resultArray = Ext.util.JSON.decode(response.responseText);
																				if(resultArray.success==true|| "true" == resultArray.success){
																			 		Ext.getCmp('sql').setValue(resultArray.msg);
																			 		
																				}else{
																					Ext.Msg.alert('提示', resultArray.msg);
																				}
																			},
																			failure : function(response) {
																				var resultArray = Ext.util.JSON.decode(response.responseText);
																				Ext.Msg.alert('提示', resultArray.msg);
																			},
																			params : {
																				table_name : Ext.getCmp('table_name_text').getValue(),
																				schema_name: schemaNameComboBox.value,
																				id_database: datasourceComboBox.value,
																				id_model: modelComboBox.value
																			}
																		});																 
																	}        
																}),
																new Ext.Button({
																	text:'运行SQL',
																	id:'runSQL',
																	name:'runSQL',
																	handler:function(){
																		 Ext.Ajax.request( {
																			url : '../mdmTable/runSQL.shtml',
																			success : function(response) {
																				var resultArray = Ext.util.JSON.decode(response.responseText);
																				Ext.Msg.alert('提示', resultArray.msg);
																			},
																			failure : function(response) {
																				var resultArray = Ext.util.JSON.decode(response.responseText);
																				Ext.Msg.alert('提示', resultArray.msg);
																			},
																			params : {
																				table_name : tableNameComboBox.value,
																				schema_name: schemaNameComboBox.value,
																				id_database: datasourceComboBox.value,
																				sql: Ext.getCmp('sql').getValue()
																			}
																		});																 
																	}        
																})
																]
															}]
												}]
									}]
							},{
								columnWidth:.99,
								layout : 'form',
								labelWidth : 50, // 标签宽度
								defaultType : 'textfield',
								border : false,
								anchor : '100%',
								items : [{	
											id:'selectTableFieldset',
											xtype:'fieldset',
											title: '选择表',
											autoHeight:true,
											layout : 'column',
											defaults: {readOnly : true},
											anchor : '99%',
											collapsed: false,
											items : [{
														columnWidth:.5,
														layout: 'form',
														border:false,
														items : [tableNameComboBox]
													},{
														columnWidth:.2,
														layout: 'form',
														items : [
																new Ext.Button({
																	text:'检查',
																	id:'btnCheck',
																	name:'btnCheck',
																	handler:function(){
																		 Ext.Ajax.request( {
																			url : '../mdmTable/check.shtml',
																			success : function(response) {
																				Ext.MessageBox.alert('提示', '通过检查');
																			},
																			failure : function(response) {
																				var resultArray = Ext.util.JSON.decode(response.responseText);
																				Ext.Msg.alert('提示', resultArray.msg);
																			},
																			params : {
																				table_name : tableNameComboBox.value,
																				schema_name: schemaNameComboBox.value,
																				id_database: datasourceComboBox.value,
																				id_model: modelComboBox.value
																			}
																		});
																	}        
																})
															   ]	
													
												
											}]
										}]
							}]
				
						}]
				});
				
				
		var addWindow = new Ext.Window( {
			layout : 'fit', // 设置窗口布局模式
			width : 800, // 窗口宽度
			height : 500, // 窗口高度
			modal:true,
			resizable : false,// 是否可以改变大小，默认可以
			maskdisabled : true,
			closeAction : 'hide',
			closable : true, // 是否可关闭
			collapsible : true, // 是否可收缩
			border : false, // 边框线设置
			constrain : true, // 设置窗口是否可以溢出父容器
			animateTarget : Ext.getBody(),
			pageY : 30, // 页面定位Y坐标
			pageX : document.body.clientWidth / 2 - 600 / 2 -200, // 页面定位X坐标
			items : [ addForm ], // 嵌入的表单面板
			buttons : [ {
				text : '保存',
				iconCls : 'acceptIcon',
				handler : function() {
					var mode = Ext.getCmp('windowmode').getValue();
					if (mode == 'add')
						submitTheForm();
					else
						updateTheForm();
				}
			},{
					text : '重置',
					id : 'btnReset',
					iconCls : 'tbar_synchronizeIcon',
					handler : function() {
						clearForm(addForm.getForm());
					}
				},{
					text : '关闭',
					iconCls : 'deleteIcon',
					handler : function() {
					addWindow.hide();
				}
			} ]
		});
		var viewport = new Ext.Viewport( {
			layout : 'border',
			items : [grid]
		});
		
		function queryItem() {
			store.reload({
						params : {
							start : 0,
							limit : bbar.pageSize
							//,queryFTPName : Ext.getCmp('queryFTPName').getValue()
						}
					});
		}		
		
		/**


		 * 新增窗体初始化
		 */
		function addItem() {
			var flag = Ext.getCmp('windowmode').getValue();
			if (typeof(flag) != 'undefined') {
				addForm.form.getEl().dom.reset();
			} else {
				clearForm(addForm.getForm());
			}
			
			addWindow.show(); // 显示窗口
			addWindow.setTitle('<span class="commoncss">新增</span>');
			Ext.getCmp('windowmode').setValue('add');
			
			var rg = Ext.getCmp('tableMode')
			var r = rg.getValue();
			
			Ext.getCmp('table_name_text').setDisabled(false);
			Ext.getCmp('sql').setDisabled(false);
			Ext.getCmp('createSQL').setDisabled(false);
			Ext.getCmp('runSQL').setDisabled(false);
			
			Ext.getCmp('table_name_comboBox').setDisabled(true);
			Ext.getCmp('btnCheck').setDisabled(true);
			
		}

		/**
		 * 新增
		 */
		function submitTheForm() {
			if (!addForm.getForm().isValid())
				return;
			addForm.form.submit( {
				url : '../mdmTable/save.shtml',
				waitTitle : '提示',
				method : 'POST',
				waitMsg : '正在处理数据,请稍候...',
				success : function(form, action) {
					addWindow.hide();
					queryItem();
					form.reset();
					Ext.MessageBox.alert('提示', action.result.msg);
				},
				failure : function(form, action) {
					Ext.MessageBox.alert('提示', '数据保存失败');
				}
			});
		}
				
		function updateItem(){
			var record = grid.getSelectionModel().getSelected();
			
			if (Ext.isEmpty(record)) {
				Ext.Msg.alert('提示:', '请先选中要修改的数据');
				return;
			}
			
			addForm.getForm().reset();
			Ext.getCmp('windowmode').setValue('');
			
			addForm.getForm().loadRecord(record);
			
			Ext.getCmp('table_name_text').setDisabled(false);
			Ext.getCmp('sql').setDisabled(false);
			Ext.getCmp('createSQL').setDisabled(false);
			Ext.getCmp('runSQL').setDisabled(false);
			
			Ext.getCmp('table_name_comboBox').setDisabled(true);
			Ext.getCmp('btnCheck').setDisabled(true);
			
			addWindow.show(); // 显示窗口
			addWindow.setTitle('<span class="commoncss">编辑</span>');
			
		}
		
		
		
		/**
		 * 修改
		 */
		function updateItemData() {
			var record = grid.getSelectionModel().getSelected();
			if (Ext.isEmpty(record)) {
				Ext.Msg.alert('提示:', '请先选中要修改的数据');
				return;
			}
			autoGrid.store.load({
				params: {meta: true,id_table:record.data.id_table},
				callback:function(records, options, success){
					if(!autoGrid.store.reader.jsonData.success){
						Ext.Msg.alert('❌出错了',autoGrid.store.reader.jsonData.msg);
					}else{
						autoGrid.store.load({params: {meta: true,id_table:record.data.id_table}});
						autoGrid.reconfigure(autoGrid.getStore(),new Ext.grid.ColumnModel(cm2));
						editWindow.show();
					}

				}
			});

			var formPanel = Ext.getCmp("textfields");
			formPanel.removeAll();
			formPanel.add({id : 'action',
				name : 'action',
				xtype:'textfield',
				hidden : true});
			formPanel.add({id : 'id_table2',
				name : 'id_table',
				xtype:'textfield',
				hidden : true});

			formPanel.add({id : 'condition',
				name : 'condition',
				xtype:'textfield',
				hidden : true});


			Ext.getCmp('id_table2').setValue(record.data.id_table);
			Ext.getCmp('action').setValue('add');

		}

		
		/**
		 * 修改
		 */
		function updateTheForm() {
			if (!addForm.getForm().isValid())
				return;
			addForm.form.submit( {
				url : '../mdmTable/update.shtml',
				waitTitle : '提示',
				method : 'POST',
				waitMsg : '正在处理数据,请稍候...',
				success : function(form, action) { // 回调函数有2个参数
					Ext.MessageBox.alert('提示', action.result.msg);
					form.reset();
					addWindow.hide();
					queryItem();
				},
				failure : function(form, action) {
					Ext.Msg.alert('提示', '数据保存失败,错误类型:' + action.failureType);
				}
			});
		}

		/**
		 * 删除
		 * 
		 */
		function deleteItem() {
			var record = grid.getSelectionModel().getSelections();
			if (Ext.isEmpty(record)) {
				Ext.Msg.alert('提示', '请先选中要删除的数据!');
				return;
			}
			var strChecked = jsArray2JsString(record, 'id_table');
			Ext.Msg.confirm('请确认', '你真的要删除吗?', function(btn, text) {
				if (btn == 'yes') {
					showWaitMsg();
					Ext.Ajax.request( {
						url : '../mdmTable/delete.shtml',
						success : function(response) {
							queryItem();
							Ext.MessageBox.alert('提示', '删除成功');
						},
						failure : function(response) {
							var resultArray = Ext.util.JSON
									.decode(response.responseText);
							Ext.Msg.alert('提示', resultArray.msg);
						},
						params : {
							ids : strChecked
						}
					});
				}
			});
		}
		
    showStatWindow = function(id_table){    
		  
		
		var statWindow = new Ext.Window( {
			title : '统计',
			layout : 'fit', // 设置窗口布局模式
			width : 500, // 窗口宽度
			height : 350, // 窗口高度
			modal:true,
			resizable : false,// 是否可以改变大小，默认可以
			maskdisabled : true,
			closeAction : 'hide',
			closable : true, // 是否可关闭
			collapsible : true, // 是否可收缩
			border : false, // 边框线设置
			constrain : true, // 设置窗口是否可以溢出父容器
			animateTarget : Ext.getBody(),
			pageY : 130, // 页面定位Y坐标
			pageX : document.body.clientWidth / 2 - 600 / 2, // 页面定位X坐标
			items : [  ], // 嵌入的表单面板
			buttons : [ {
				text : '保存',
				iconCls : 'acceptIcon',
				handler : function() {
					
				}
			},{
					text : '重置',
					id : 'btnReset',
					iconCls : 'tbar_synchronizeIcon',
					handler : function() {
						
					}
				},{
					text : '关闭',
					iconCls : 'deleteIcon',
					handler : function() {
					
				}
			} ]
		});
		statWindow.show();
    }   
	
	modifyMdmTable = function(){
		if(checkBeforeUpdate(grid)) {
			updateItemData();
		}	
	}
	modifyMdmTableData = function (rowIndex,primary){
		var store = autoGrid.getStore();
		editForm.getForm().loadRecord(store.getAt(rowIndex));

		var str = '';
		var json  = store.getAt(rowIndex).json;

		for (var Key in json){
			if(Key != 'id_table' && json[Key]!=null){
				str = str+Key+"='"+json[Key]+"' AND "
			}
		}
		str = str + "1= 1";
		Ext.getCmp('condition').setValue(str);

		Ext.getCmp('action').setValue('edit');
	}
	inputdlg = function (c){
		 var formPanel = Ext.getCmp("textfields");
		 if(Ext.getCmp("textfield_"+c.dataIndex)==null || Ext.getCmp("textfield_"+c.dataIndex)=="undefined"){
			 var configItem={
						id:"textfield_"+c.dataIndex,
						xtype:'textfield',
						labelWidth :100, // 标签宽度
						fieldLabel :c.hidden?"": c.header, // 标签
						name : c.dataIndex, // name:后台根据此name属性取值
						maxLength : 40,
						hidden:c.hidden,
						anchor : '70%'
		         };
				 formPanel.add(configItem);
		 }
		 
		
	}
	var cm2 = new Ext.grid.ColumnModel([]);
	
	
	// 表格工具栏
	var tbar2 = new Ext.Toolbar({
		items : [{
					text : '新增数据',
					iconCls : 'page_addIcon',
					id : 'id_data_add',
					handler : function() {
						editForm.getForm().reset();
						Ext.getCmp('action').setValue('add');
						var record = grid.getSelectionModel().getSelected();
						Ext.getCmp('id_table2').setValue(record.data.id_table);
					}
				},'-',{
					text : '删除数据',
					id : 'tbi_data_del',
					iconCls : 'page_delIcon',
					handler : function() {
						if(checkBeforeDelete(autoGrid)) {
							deleteData();
						}
					}
				}]
	});
	
	var autoGrid = new Ext.ux.AutoGridPanel({
		height : 200,
		region : 'center',
		cm : cm2,
		tbar:tbar2,
		loadMask : {
			msg : '正在加载表格数据,请稍等...'
		},
		
        loadMask: true,
        region : 'center', 
        viewConfig : {
			forceFit : true
		},
		stripeRows : true, 
        store : new Ext.data.Store({
            proxy: new Ext.data.HttpProxy({
                url: 'getTableData.shtml'
            }),
            reader: new Ext.data.JsonReader(
                {root: 'rows', id: 'ksmc'}
            )
        })

    });

	function loadAutoGridStore(id_table){
		autoGrid.store.load({
			params: {meta: true,id_table:record.data.id_table},
			callback:function(records, options, success){
				if(!autoGrid.store.reader.jsonData.success){
					Ext.Msg.alert('❌出错了',store.reader.jsonData.msg);
				}
			}
		});
	}
	
	//表单
	var editForm = new Ext.form.FormPanel( {
		id : 'editForm',
		name : 'editForm',
		collapsible : false,
		border : true,
		labelWidth : 50, // 标签宽度
		frame : true, // 是否渲染表单面板背景色
		labelAlign : 'right', // 标签对齐方式
		bodyStyle : 'padding:0 0 0 0', // 表单元素和表单面板的边距
		buttonAlign : 'center',
		//height : 700,
		items : [{
			layout : 'form',
			border : false,
			anchor : '100%',
			items : [autoGrid]
		},{
			layout : 'form',
			id:"textfields",
			border : false,
			anchor : '100%',
			labelWidth : 100, // 标签宽度
			items:[{
				id : 'action',
				name : 'action',
				xtype:'textfield',
				hidden : true
			},{
				id : 'id_table',
				name : 'id_table',
				xtype:'textfield',
				hidden : true
			}]
		}]
	});
	
	
	
	var editWindow = new Ext.Window( {
			title : '编辑主数据',
			layout : 'fit', // 设置窗口布局模式
			width : 700, // 窗口宽度
			height : 500, // 窗口高度
			modal:true,
			autoScroll:true,
			resizable : true,// 是否可以改变大小，默认可以
			maskdisabled : true,
			closeAction : 'hide',
			closable : true, // 是否可关闭
			collapsible : true, // 是否可收缩
			border : false, // 边框线设置
			constrain : true, // 设置窗口是否可以溢出父容器
			animateTarget : Ext.getBody(),
			pageY : 20, // 页面定位Y坐标
			pageX : document.body.clientWidth / 2 - 600 / 2-200, // 页面定位X坐标
			items : [editForm], // 嵌入的表单面板
			buttons : [ {
				text : '保存',
				iconCls : 'acceptIcon',
				handler : function() {
					editForm.form.submit( {
						url : '../mdmTable/addOrUpdateTableData.shtml',
						waitTitle : '提示',
						method : 'POST',
						waitMsg : '正在处理数据,请稍候...',
						success : function(form, action) {
							if(action.result.success==true|| "true" == action.result.success){
								form.reset();
								var record = grid.getSelectionModel().getSelected();
								Ext.getCmp('id_table2').setValue(record.data.id_table);
								Ext.getCmp('action').setValue('add');
								Ext.MessageBox.alert('提示', action.result.msg);
								autoGrid.store.load({params: {meta: true,id_table:record.data.id_table}});

							}else{
								Ext.MessageBox.alert('提示', action.result.msg);
							}
							
						},
						failure : function(form, action) {
							Ext.MessageBox.alert('提示', action.result.msg);
						}
					});
				}
			},{
					text : '关闭',
					iconCls : 'deleteIcon',
					handler : function() {
						editWindow.hide();
				}
			} ]
		});
	
	function deleteData(){
		var record = autoGrid.getSelectionModel().getSelections();

		if (Ext.isEmpty(record)) {
			Ext.Msg.alert('提示', '请先选中要删除的数据!');
			return;
		}

		var str = "";
		for(var i = 0; i < record.length;i++){
			var data = record[i].data;
			for (var Key in data){
				str = str+Key+"='"+data[Key]+"' AND "
			}
			str = str + "1 = 1 #";
		}



		Ext.Msg.confirm('请确认', '你真的要删除吗?', function(btn, text) {
			if (btn == 'yes') {
				showWaitMsg();
				Ext.Ajax.request( {
					url : '../mdmTable/deleteTableData.shtml',
					success : function(response) {
						var action = Ext.util.JSON.decode(response.responseText);
						if(action.success==true|| "true" == action.success){
							editForm.getForm().reset();
							var record = grid.getSelectionModel().getSelected();
							Ext.getCmp('id_table2').setValue(record.data.id_table);
							Ext.getCmp('action').setValue('add');
							Ext.Msg.alert('提示', action.msg);
							autoGrid.store.load({params: {meta: true,id_table:record.data.id_table}});
						}else{
							Ext.MessageBox.alert('提示', action.result.msg);
						}
					},
					failure : function(response) {
						var resultArray = Ext.util.JSON
								.decode(response.responseText);
						Ext.Msg.alert('提示', resultArray.msg);
					},
					params : {
						condition : str,
						id_table:Ext.getCmp('id_table2').value
					}
				});
			}
		});
	}
	
		/**
		 * 在点击【修改】按钮前，判断是不是有选择且只选择了一条记录
		 */
		function checkBeforeUpdate(grid) {
			var selModel = grid.getSelectionModel();
			if(selModel) {
				var selects = selModel.getSelections();
				if(!(selects && selects.length == 1)) {
					Ext.MessageBox.alert('提示', '请先选择一条需要修改的记录！');
					return false;
				}
			}
			return true;
		}
		/**
		 * 在点击【删除】按钮前，判断是否至少选择了一条记录
		 */
		function checkBeforeDelete(grid) {
			var selModel = grid.getSelectionModel();
			if(selModel) {
				var selects = selModel.getSelections();
				if(!(selects && selects.length > 0)) {
					Ext.MessageBox.alert('提示', '请先至少选择一条记录！');
					return false;
				}
			}
			return true;
		}
		
		
		
	});

