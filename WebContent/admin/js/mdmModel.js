
Ext.onReady(function() {


	//属性类型下拉框
	var statisticTypeData = [
		['1','枚举'],
		['2','计算数值'],
		['3','非结构化文本'],
		['4','其他']
	];
	var statisticTypeStore = new Ext.data.SimpleStore(
		{
			fields:['value','text'],
			data:statisticTypeData
		}
	);

	//字段类型Store
	var fieldTypeStore = new Ext.data.JsonStore({
		fields: ['fieldTypeShow', 'fieldTypeValue'],
		url : "../mdmModelAttribute/getFieldType.shtml",
		autoLoad:true,
		root : ""
	});

	//是否主键下拉框
	var isPrimaryData = [
		['Y','是'],
		['N','否']
	];
	var isPrimaryStore = new Ext.data.SimpleStore(
		{
			fields:['value','text'],
			data:isPrimaryData
		}
	);

//---------------model start---------------------------------------------------------		

	// 定义列模型
		var smModel = new Ext.grid.CheckboxSelectionModel();
		var rownumModel = new Ext.grid.RowNumberer( {
			header : 'NO',
			width : 28
		});
		var cmModel = new Ext.grid.ColumnModel( [ rownumModel, smModel,
			{
				header : '主键',
				dataIndex : 'id_model',
				hidden : true
			},
			{
				header : '模型编码',
				dataIndex : 'model_code',
				sortable : true
			},
			{
				header : '模型名称',
				dataIndex : 'model_name',
				sortable : true,
				width : 100
			}, {
				header : '模型描述',
				dataIndex : 'model_desc',
				sortable : true,
				width : 100
			}, {
				header : '创建人',
				dataIndex : 'model_author',
				sortable : true,
				width : 100
			},{
				header : '状态值',
				dataIndex : 'model_status',
				hidden : true
			},{
				header : '状态',
				dataIndex : 'model_status_show',
				sortable : true,
				width : 100
			},{
				header : '说明',
				dataIndex : 'model_note',
				sortable : true,
				width : 100
			}]);
		
		var storeModel = new Ext.data.Store( {
			proxy : new Ext.data.HttpProxy( {
				url : '../mdmModel/query.shtml'
			}),
			reader : new Ext.data.JsonReader( {
				totalProperty : 'total',
				root : 'rows' 
			}, [ {
					name : 'id_model' 
				},{
					name : 'model_code'
				},{
					name : 'model_name' 
				}, {
					name : 'host_name'
				}, {
					name : 'model_desc'
				}, {
					name : 'model_author'
				}, {
					name : 'model_status'
				}, {
					name : 'model_status_show'
				}, {
					name : 'model_note'
				}]),
			listeners: {
				datachanged: function() {
					//autoCheckGridHead(Ext.getCmp('id_grid_model'));
				}
			}
		});


		
		var pagesize_combo_model = new Ext.form.ComboBox({
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


		
		var numberModel = parseInt(pagesize_combo_model.getValue());
		// 改变每页显示条数reload数据
		pagesize_combo_model.on("select", function(comboBox) {
			bbarModel.pageSize = parseInt(comboBox.getValue());
			numberModel = parseInt(comboBox.getValue());
			queryModel();
		});




	// 表格工具栏
		var tbarModel = new Ext.Toolbar({
			items : [{
						text : '新增',
						iconCls : 'page_addIcon',
						id : 'id_tbi_add_model',
						handler : function() {
							addModel();
						}
					},'-',{
						text : '修改',
						id : 'tbi_edit_model',
						iconCls : 'page_edit_1Icon',
						handler : function() {
							if(checkBeforeUpdate(gridModel)) {
								updateModel();
							}
						}
					},'-',{
						text : '删除',
						id : 'tbi_del_model',
						iconCls : 'page_delIcon',
						handler : function() {
							if(checkBeforeDelete(gridModel)) {
								deleteModel();
							}
						}
					},'->',{
						text : '导出Excel',
						iconCls : 'page_excelIcon',
						handler : function() {
							exportExcel();
						}
					}]
		});
		// 分页工具栏
		var bbarModel = new Ext.PagingToolbar( {
			pageSize : numberModel,
			store : storeModel,
			displayInfo : true,
			displayMsg : '显示{0}条到{1}条,共{2}条',
			plugins : new Ext.ux.ProgressBarPager(), // 分页进度条
			emptyMsg : "没有符合条件的记录",
			items : [ '-', '&nbsp;&nbsp;', pagesize_combo_model ]
		});
		// 表格实例
		var gridModel = new Ext.grid.GridPanel( {
			height : 500,
			id : 'id_grid_model',
			autoScroll : true,
			frame : true,
			region : 'center', 
			store : storeModel, 
			viewConfig : {
				forceFit : true
			},
			stripeRows : true, 
			cm : cmModel, 
			sm : smModel,
			tbar : tbarModel, 
			bbar : bbarModel,
			loadMask : {
				msg : '正在加载表格数据,请稍等...'
			}
		});

		// 监听行选中事件
		gridModel.on('rowclick', function(pGrid, rowIndex, event) {
			Ext.getCmp('tbi_edit_model').enable();
			Ext.getCmp('tbi_del_model').enable();
		});

		gridModel.on('rowdblclick', function(grid, rowIndex, event) {
			updateModel();
		});
		
		
		var statusData = [  
			['0','草稿'],  
			['1','已发布'] 
		];  
		var statusStore = new Ext.data.SimpleStore(  
			{  
				fields:['value','text'],  
				data:statusData  
			}  
		);  
		var statusCombo = new Ext.form.ComboBox({  
			fieldLabel : '状态',
			emptyText : '请选择',
			hiddenName : 'model_status',
			forceSelection : true,
			//hiddenValue:'table_id',
			anchor : '46%',
			store:statusStore,//store用来为ComboBox提供数据    
			valueField:'value',//与store定义中的名字一样。正是根据他们之间的对应关系来显示数据。  
			displayField:'text',    
			typeAhead : true,
			mode : 'local',
			triggerAction : 'all',
			selectOnFocus : true,// 设置用户能不能自己输入,true为只能选择列表中有的记录
			allowBlank : false  
		}); 		
		
	
		
		
		//表单
		var modelForm = new Ext.form.FormPanel( {
			title:'基本信息',  
			id : 'modelForm',
			name : 'modelForm',
			collapsible : false,
			border : true,
			labelWidth : 100, // 标签宽度
			frame : true, // 是否渲染表单面板背景色
			labelAlign : 'right', // 标签对齐方式
			bodyStyle : 'padding:0 0 5 0', // 表单元素和表单面板的边距
			buttonAlign : 'center',
			//height : 700,
			items : [{
				layout : 'column',
				border : false,
				anchor : '90%',
				items : [{
					columnWidth:.5,  //该列占用的宽度，标识为50％
					layout: 'form',
					border:false,
					items: [{
						xtype:'textfield',
						id:'model_code',
						fieldLabel : '模型编码', // 标签
						name : 'model_code', // name:后台根据此name属性取
						anchor:'90%',
						allowBlank : false,
						validationEvent : 'blur',
						validator : function(thisText){
							var IsExist = true;
							if(thisText != null && thisText !='' ){
								Ext.Ajax.request({
									url:"../mdmModel/checkModelCode.shtml",
									method : 'POST',
									scope : true,
									params : {
										id_model : Ext.getCmp("id_model").getValue(),
										model_code : thisText
									},
									success : function(resp,opts){
										var respText = Ext.util.JSON.decode(resp.responseText);
										if(!respText.success){

											Ext.getCmp('model_code').markInvalid('模型编码已存在');
										}
										IsExist = respText.success
									}
								});


							}else{
								IsExist = false;
								Ext.getCmp('model_code').markInvalid('模型编码不能为空');
							}

							return IsExist;
						}
					}]
				},{
					columnWidth:.5,
					layout: 'form',
					border:false,
					items: [{
						xtype:'textfield',
						id:'model_name',
						fieldLabel : '模型名称', // 标签
						name : 'model_name', // name:后台根据此name属性取值
						anchor:'90%'
					}]
				},{
					columnWidth : .99,
					layout : 'form',
					labelWidth : 99, // 标签宽度
					defaultType : 'textfield',
					border : false,
					anchor : '100%',
					items : [
					{
						xtype : 'textfield',
						id:'model_desc',
						fieldLabel : '模型描述', // 标签
						name : 'model_desc', // name:后台根据此name属性取值
						maxLength : 50,
						allowBlank : false,
						anchor : '96%',
						listeners: {
							//blur: handleSpace
						}
					},{
						id:'model_author',
						fieldLabel : '创建人', // 标签
						name : 'model_author', // name:后台根据此name属性取值
						maxLength : 50,
						allowBlank : false,
						readonly:true,
						anchor : '46%',
						listeners: {
							//blur: handleSpace
						}
					},
					 statusCombo
					,{
						fieldLabel : '说明',
						name : 'model_note',
						id : 'model_note',
						allowBlank : true,
						xtype : 'textarea',
						anchor : '96%', 
						height : 80, 
			    	},{
						id : 'modelwindowmode',
						name : 'modelwindowmode',
						hidden : true
					},{
							xtype:'textfield',
							id:'id_model',
							name : 'id_model',
							hidden : true
						}],
					buttons : [ {
						text : '保存',
						iconCls : 'acceptIcon',
						handler : function() {
							var mode = Ext.getCmp('modelwindowmode').getValue();
							if (mode == 'add')
								submitModelForm();
							else
								updateModelForm();
						}
					},{
							text : '重置',
							iconCls : 'tbar_synchronizeIcon',
							handler : function() {
								clearForm(modelForm.getForm());
							}
						},{
							text : '关闭',
							iconCls : 'deleteIcon',
							handler : function() {
							modelWindow.hide();
						}
					} ]
				} ]

			} ]

		});

//---------------------------------------------------------------
	// 定义列模型
		var smAttribute = new Ext.grid.CheckboxSelectionModel();
		var rownumAttribute = new Ext.grid.RowNumberer( {
			header : 'NO',
			width : 28
		});
		var cmAttribute = new Ext.grid.ColumnModel( [ rownumAttribute, smAttribute, 
		{
				header : '属性编码',
				dataIndex : 'id_attribute',
				hidden : true
		},{
				header : '模型ID',
				dataIndex : 'id_model',
				hidden : true
		}, {
				header : '序号',
				dataIndex : 'attribute_order',
				sortable : true,
				width : 40
		}, {
				header : '属性名称',
				dataIndex : 'attribute_name',
				sortable : true,
				width : 100,
				editor: {
					allowBlank : false,
					xtype:'textfield'
				}
		}, {
				header : '属性类型',
				dataIndex : 'statistic_type',
				sortable : true,
				width : 100,
				editor:new Ext.form.ComboBox({
					store: statisticTypeStore,
					valueField : "value",
					displayField : "text",
					mode: 'local',
					triggerAction: 'all',
					forceSelection: true,
					typeAhead: true,
					editable:false,
					allowBlank : false,
					selectOnFocus : true,// 设置用户能不能自己输入,true为只能选择列表中有的记录
					resizable : true
				}),
				renderer: function (data, metadata, record, rowIndex, columnIndex, store) {
					var statistic_type = store.getAt(rowIndex).get('statistic_type');
					for(var i = 0 ; i < statisticTypeData.length;i++){
						if(statisticTypeData[i][0] == statistic_type){
							return statisticTypeData[i][1];
						}
					}

				}
		},{
				header : '字段名称',
				dataIndex : 'field_name',
				sortable : true,
				width : 100,
				editor: {
					allowBlank : false,
					xtype:'textfield'
				}
		},{
				header : '字段类型',
				dataIndex : 'field_type',
				sortable : true,
				width : 100,
				editor:new Ext.form.ComboBox({
					anchor : '100%',
					store: fieldTypeStore,
					valueField : "fieldTypeValue",
					displayField : "fieldTypeShow",
					mode: 'local',
					triggerAction: 'all',
					forceSelection: true,
					typeAhead: true,
					editable:false,
					allowBlank : false,
					selectOnFocus : true,// 设置用户能不能自己输入,true为只能选择列表中有的记录
					resizable : true
				}),
				renderer: function (data, metadata, record, rowIndex, columnIndex, store) {
					var field_type_show = store.getAt(rowIndex).get('field_type');
					var jsonData = fieldTypeStore.reader.jsonData;
					for(var i = 0 ; i < jsonData.length;i++){
						if(jsonData[i].fieldTypeValue == field_type_show){
							return jsonData[i].fieldTypeShow;
						}
					}

				}
		},{
				header : '字段长度',
				dataIndex : 'field_length',
				sortable : true,
				width : 100,
				editor: {
					allowBlank : false,
					xtype:'numberfield'
				}
		},{
			header : '精度', 
			dataIndex : 'field_precision', 
			sortable : true,
			width : 100,
			editor: {
				allowBlank : false,
				xtype:'numberfield'
			}
		},{
				header : '主键',
				dataIndex : 'is_primary',
				sortable : true,
				width : 60,
				editor: new Ext.form.ComboBox({
					anchor : '100%',
					store:isPrimaryStore,//store用来为ComboBox提供数据
					valueField:'value',//与store定义中的名字一样。正是根据他们之间的对应关系来显示数据。
					displayField:'text',
					mode: 'local',
					triggerAction: 'all',
					forceSelection: true,
					typeAhead: true,
					editable:false,
					allowBlank : false,
					selectOnFocus : true,// 设置用户能不能自己输入,true为只能选择列表中有的记录
					resizable : true
				}),
				renderer: function (data, metadata, record, rowIndex, columnIndex, store) {
					var is_primary = store.getAt(rowIndex).get('is_primary');
					for(var i = 0 ; i < isPrimaryData.length;i++){
						if(isPrimaryData[i][0] == is_primary){
							return isPrimaryData[i][1];
						}
					}

				}
		} ]);
		
		var storeAttribute = new Ext.data.Store( {
			proxy : new Ext.data.HttpProxy( {
				url : '../mdmModelAttribute/query.shtml'
			}),
			reader : new Ext.data.JsonReader( {
				totalProperty : 'total',
				root : 'rows' 
			}, [ {
					name : 'id_attribute' 
				}, {
					name : 'id_model' 
				}, {
					name : 'attribute_order'
				}, {
					name : 'attribute_name'
				}, {
					name : 'statistic_type'
				}, {
					name : 'field_name'
				}, {
					name : 'field_type'
				}, {
					name : 'field_type_show'
				}, {
					name : 'field_length'
				}, {
					name : 'field_precision'
				},{
					name : 'is_primary'
				}, {
					name : 'is_primary_show'
				}]),
			listeners: {
				datachanged: function() {
					//autoCheckGridHead(Ext.getCmp('id_grid_attribute'));
				}
			}
		});

	storeAttribute.on('beforeload',function(){
		storeAttribute.baseParams = {id_model:Ext.getCmp('id_model').getValue()};
	});

		var pagesize_combo_Attribute = new Ext.form.ComboBox({
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
		
		var numberAttribute = parseInt(pagesize_combo_Attribute.getValue());
		// 改变每页显示条数reload数据
		pagesize_combo_Attribute.on("select", function(comboBox) {
			bbarAttribute.pageSize = parseInt(comboBox.getValue());
			numberAttribute = parseInt(comboBox.getValue());
			queryAttribute();
		});

		// 分页工具栏
		var bbarAttribute = new Ext.PagingToolbar( {
			pageSize : numberAttribute,
			store : storeAttribute,
			displayInfo : true,
			displayMsg : '显示{0}条到{1}条,共{2}条',
			plugins : new Ext.ux.ProgressBarPager(), // 分页进度条
			emptyMsg : "没有符合条件的记录",
			items : [ '-', '&nbsp;&nbsp;', pagesize_combo_Attribute ]
		});


		var checkModelId = function(){
			var id_model =  Ext.getCmp('id_model').getValue()
			if(id_model == "" || id_model == null){
				Ext.Msg.alert('错误', '请先保存模型!'); //这种方式非常常见的
				return false;
			}
			return true;
		}

		// 表格工具栏
		var tbarAttribute = new Ext.Toolbar({
			items : [{
						text : '新增',
						iconCls : 'page_addIcon',
						id : 'id_tbi_add_attribute',
						handler : function() {
							if(checkModelId()){
								Ext.getCmp('checkboxgroup').show();
								addAttribute();
							}
						}
					},'-',{
						text : '修改',
						id : 'tbi_edit_attribute',
						iconCls : 'page_edit_1Icon',
						handler : function() {
							Ext.getCmp('checkboxgroup').hide();
							if(checkBeforeUpdate(gridAttribute)) {
								updateAttribute();
							}
						}
					},'-',{
						text : '删除',
						id : 'tbi_del_attribute',
						iconCls : 'page_delIcon',
						handler : function() {
							if(checkBeforeDelete(gridAttribute)) {
								deleteAttribute();
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
		var gridAttribute = new  Ext.grid.EditorGridPanel( {
			title : '属性',
			height : 500,
			clicksToEdit:1,
			id : 'id_grid_attribute',
			autoScroll : true,
			frame : true,
			region : 'center', 
			store : storeAttribute, 
			viewConfig : {
				forceFit : true
			},
			stripeRows : true, 
			cm : cmAttribute, 
			sm : smAttribute,
			tbar : tbarAttribute, 
			bbar : bbarAttribute,
			loadMask : {
				msg : '正在加载表格数据,请稍等...'
			},
			listeners: {
				afteredit: function(val) {

					console.log(val);
					if(val.value!='' && val.value != null && val.value != undefined){
						var params = '{id_model:"'+val.record.data.id_model +'", '+val.field+':"'+val.value+'",id_attribute:"'+val.record.data.id_attribute+'" }';
						Ext.Ajax.request({
							url : '../mdmModelAttribute/update.shtml',
							params:  Ext.util.JSON.decode(params),
							method: 'POST',
							success: function (response, options) {
								var resultArray = Ext.util.JSON.decode(response.responseText);
								if(!resultArray.success){
									Ext.Msg.alert('提示', resultArray.msg);
								}
							},
							failure: function (response, options) {
								var resultArray = Ext.util.JSON.decode(response.responseText);
								if(!resultArray.success){
									Ext.Msg.alert('提示', resultArray.msg);
								}
							}
						});
					}else{
						val.record.set(val.field, val.originalValue);
					}
				}

			}
		});





		//// 监听行选中事件
		//gridAttribute.on('rowclick', function(pGrid, rowIndex, event) {
		//	Ext.getCmp('tbi_edit_attribute').enable();
		//	Ext.getCmp('tbi_del_attribute').enable();
		//});

		//gridAttribute.on('rowdblclick', function(grid, rowIndex, event) {
		//	updateAttribute();
		//});
		
		
		//字段类型下拉框
		

		var fieldTypeComboBox = new Ext.form.ComboBox({
			fieldLabel : '字段类型',
			emptyText:'请选择字段类型',
			hiddenName : "field_type",
			forceSelection: true,
			anchor : '100%',
    		store: fieldTypeStore,
			valueField : "fieldTypeValue",
			displayField : "fieldTypeShow",
			typeAhead: true,
			mode: 'local',
			triggerAction: 'all',
			selectOnFocus : true,// 设置用户能不能自己输入,true为只能选择列表中有的记录
			allowBlank : false  
		});

		
		

		var statisticTypeComboBox = new Ext.form.ComboBox({  
			fieldLabel : '属性类型',
			emptyText : '请选择属性类型',
			hiddenName : 'statistic_type',
			forceSelection : true,
			//hiddenValue:'table_id',
			anchor : '100%',
			store:statisticTypeStore,//store用来为ComboBox提供数据    
			valueField:'value',//与store定义中的名字一样。正是根据他们之间的对应关系来显示数据。  
			displayField:'text',    
			typeAhead : true,
			mode : 'local',
			triggerAction : 'all',
			selectOnFocus : true,// 设置用户能不能自己输入,true为只能选择列表中有的记录
			allowBlank : false  
		}); 	


		var isPrimaryComboBox = new Ext.form.ComboBox({  
			fieldLabel : '是否是主键',
			emptyText : '请选择是否是主键',
			hiddenName : 'is_primary',
			forceSelection : true,
			//hiddenValue:'table_id',
			anchor : '100%',
			store:isPrimaryStore,//store用来为ComboBox提供数据    
			valueField:'value',//与store定义中的名字一样。正是根据他们之间的对应关系来显示数据。  
			displayField:'text',    
			typeAhead : true,
			mode : 'local',
			triggerAction : 'all',
			selectOnFocus : true,// 设置用户能不能自己输入,true为只能选择列表中有的记录
			allowBlank : false  
		}); 			
		
		//表单
		var attributeForm = new Ext.form.FormPanel( {
			id : 'attributeForm',
			name : 'attributeForm',
			collapsible : false,
			border : true,
			labelWidth : 100, // 标签宽度
			frame : true, // 是否渲染表单面板背景色
			labelAlign : 'right', // 标签对齐方式
			bodyStyle : 'padding:0 0 5 0', // 表单元素和表单面板的边距
			buttonAlign : 'center',
			//height : 700,
			items : [{
				layout : 'column',
				border : false,
				anchor : '90%',
				items : [{
					columnWidth : .99,
					layout : 'form',
					labelWidth : 99, // 标签宽度
					defaultType : 'textfield',
					border : false,
					anchor : '110%',
					items : [{
						id:'attribute_order',
						fieldLabel : '序号', // 标签
						name : 'attribute_order', // name:后台根据此name属性取值
						maxLength : 50,
						allowBlank : true,
						anchor : '100%',
						listeners: {
							//blur: handleSpace
						}
					},{
						id:'attribute_name',
						fieldLabel : '属性名称', // 标签
						name : 'attribute_name', // name:后台根据此name属性取值
						maxLength : 50,
						allowBlank : false,
						anchor : '100%',
						listeners: {
							//blur: handleSpace
						}
					},
					statisticTypeComboBox
					,{
						id:'field_name',
						fieldLabel : '字段名称', // 标签
						name : 'field_name', // name:后台根据此name属性取值
						maxLength : 50,
						allowBlank : false,
						anchor : '100%',
						listeners: {
							//blur: handleSpace
						}
					},
					 fieldTypeComboBox
					,{
						id:'field_length',
						fieldLabel : '字段长度', // 标签
						name : 'field_length', // name:后台根据此name属性取值
						maxLength : 50,
						allowBlank : false,
						anchor : '100%',
						listeners: {
							//blur: handleSpace
						}
					},{
						id:'field_precision',
						fieldLabel : '精度', // 标签
						name : 'field_precision', // name:后台根据此name属性取值
						maxLength : 50,
						allowBlank : false,
						anchor : '100%',
						listeners: {
							//blur: handleSpace
						}
					},
					isPrimaryComboBox
					,{
							xtype: 'checkboxgroup',
							id:'checkboxgroup',
							hidden : true,
							items: [
								{ id:'batchSave', boxLabel: '批量添加', name: 'rb', inputValue: '1'  }
							]
						},{
						id : 'id_attribute_main',
						name : 'id_attribute',
							hidden : true
					},{
						id : 'id_model_attribute',
						name : 'id_model',
						hidden : true
					},{
						id : 'attributewindowmode',
						name : 'attributewindowmode',
						hidden : true
					}]

				} ]

			} ]

		});

		var attributeWindow = new Ext.Window( {
			layout : 'fit', // 设置窗口布局模式
			width : 400, // 窗口宽度
			height : 320, // 窗口高度
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
			items : [ attributeForm ], // 嵌入的表单面板
			buttons : [ {
				text : '保存',
				iconCls : 'acceptIcon',
				handler : function() {

					var mode = Ext.getCmp('attributewindowmode').getValue();
					if (mode == 'add')
						submitAttributeForm();
					else
						updateAttributeForm();
				}
			},{
				text : '重置',
				id : 'btnReset',
				iconCls : 'tbar_synchronizeIcon',
				handler : function() {
					clearForm(attributeForm.getForm());
				}
			},{
				text : '关闭',
				iconCls : 'deleteIcon',
				handler : function() {
					attributeWindow.hide();
				}
			} ]
		});

		



		
//---------------------------------------------------------------
	// 定义列模型
		var smConstaint = new Ext.grid.CheckboxSelectionModel();
		var rownumConstaint = new Ext.grid.RowNumberer( {
			header : 'NO',
			width : 28
		});
		var cmConstaint = new Ext.grid.ColumnModel( [ rownumConstaint, smConstaint, 
		{
			header : '模型约束编码',
			dataIndex : 'id_constaint',
			hidden : true
		},	
		{
			header : '模型属性编码',
			dataIndex : 'id_attribute',
			hidden : true
		},	
		{
			header : '序号', 
			dataIndex : 'constaint_order', 
			sortable : true,
			width : 100
		}, {
			header : '约束类型',
			dataIndex : 'constaint_type',
			sortable : true,
			width : 100
		}, {
			header : '约束名',
			dataIndex : 'constaint_name',
			sortable : true,
			width : 100
		},{
			header : '参照模型', 
			dataIndex : 'reference_id_model_show', 
			sortable : true,
			width : 100
		},{
			header : '参照模型值', 
			dataIndex : 'reference_id_model', 
			hidden : true
		},{
			header : '参照属性', 
			dataIndex : 'reference_id_attribute_show', 
			sortable : true,
			width : 100
		},{
			header : '参照属性值', 
			dataIndex : 'reference_id_attribute', 
			hidden : true
		},{
			header : '是否建立别名表值', 
			dataIndex : 'alias_table_flag', 
			hidden : true
		},{
			header : '建立别名表', 
			dataIndex : 'alias_table_flag_show', 
			sortable : true,
			width : 100
		},{
			header : '约束属性编码', 
			dataIndex : 'id_attributes', 
			hidden : true
		},{
			header : '约束属性', 
			dataIndex : 'id_attributes_show', 
			sortable : true,
			width : 100
		} ]);
		
		var storeConstaint = new Ext.data.Store( {
			proxy : new Ext.data.HttpProxy( {
				url : '../mdmModelConstaint/query.shtml'
			}),
			reader : new Ext.data.JsonReader( {
				totalProperty : 'total',
				root : 'rows' 
			}, [ {
					name : 'id_constaint' 
				}, {
					name : 'constaint_order' 
				}, {
					name : 'constaint_type'
				}, {
					name : 'constaint_name'
				}, {
					name : 'id_attribute'
				}, {
					name : 'reference_id_model'
				}, {
					name : 'reference_id_model_show'
				}, {
					name : 'reference_id_attribute'
				}, {
					name : 'reference_id_attribute_show'
				}, {
					name : 'alias_table_flag'
				}, {
					name : 'alias_table_flag_show'
				}, {
					name : 'id_attributes'
				}, {
					name : 'id_attributes_show'
				} ]),
			listeners: {
				datachanged: function() {
					//autoCheckGridHead(Ext.getCmp('id_grid_constaint'));
				}
			}
		});
		/*
		storeConstaint.load( {
			params : {
				id_attribute : Ext.getCmp('id_attribute_main').getValue(),
				start : 0,
				limit : 20
			}
		});
		*/
		
		var pagesize_combo_constaint = new Ext.form.ComboBox({
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
		
		var numberConstaint = parseInt(pagesize_combo_constaint.getValue());
		// 改变每页显示条数reload数据
		pagesize_combo_constaint.on("select", function(comboBox) {
			bbarConstaint.pageSize = parseInt(comboBox.getValue());
			numberConstaint = parseInt(comboBox.getValue());
			storeConstaint.reload( {
				params : {
					start : 0,
					limit : bbarConstaint.pageSize
				}
			});
		});

		// 分页工具栏
		var bbarConstaint = new Ext.PagingToolbar( {
			pageSize : numberConstaint,
			store : storeConstaint,
			displayInfo : true,
			displayMsg : '显示{0}条到{1}条,共{2}条',
			plugins : new Ext.ux.ProgressBarPager(), // 分页进度条
			emptyMsg : "没有符合条件的记录",
			items : [ '-', '&nbsp;&nbsp;', pagesize_combo_constaint ]
		});

		// 表格工具栏
		var tbarConstaint = new Ext.Toolbar({
			items : [{
						text : '新增',
						iconCls : 'page_addIcon',
						id : 'id_tbi_add_constaint',
						handler : function() {
							//if(checkBeforeUpdate(gridAttribute)){

								addConstaint();
							//}
						}
					},'-',{
						text : '修改',
						id : 'tbi_edit_constaint',
						iconCls : 'page_edit_1Icon',
						handler : function() {

							//if(checkBeforeUpdate(gridConstaint)) {
								updateConstaint();
							//}
						}
					},'-',{
						text : '删除',
						id : 'tbi_del_constaint',
						iconCls : 'page_delIcon',
						handler : function() {
							if(checkBeforeDelete(gridConstaint)) {
								deleteConstaint();
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
		var gridConstaint = new Ext.grid.GridPanel( {
			title : '约束',
			height : 500,
			id : 'id_grid_constaint',
			autoScroll : true,
			frame : true,
			region : 'center', 
			store : storeConstaint, 
			viewConfig : {
				forceFit : true
			},
			stripeRows : true, 
			cm : cmConstaint, 
			sm : smConstaint,
			tbar : tbarConstaint, 
			bbar : bbarConstaint,
			loadMask : {
				msg : '正在加载表格数据,请稍等...'
			}
		});

		// 监听行选中事件
		gridConstaint.on('rowclick', function(pGrid, rowIndex, event) {
			Ext.getCmp('tbi_edit_constaint').enable();
			Ext.getCmp('tbi_del_constaint').enable();
		});

		gridConstaint.on('rowdblclick', function(grid, rowIndex, event) {
			updateConstaint();
		});

		//约束类型下拉框
		var constaintTypeData = [  
			['1','唯一'],  
			['2','非定'],
			['3','外键'] 
		];  
		var constaintTypeStore = new Ext.data.SimpleStore(  
			{  
				fields:['value','text'],  
				data:constaintTypeData  
			}  
		);  
		var constaintTypeComboBox = new Ext.form.ComboBox({  
			fieldLabel : '约束类型',
			emptyText : '请选择约束类型',
			hiddenName : 'constaint_type',
			forceSelection : true,
			//hiddenValue:'table_id',
			anchor : '100%',
			store:constaintTypeStore,//store用来为ComboBox提供数据    
			valueField:'value',//与store定义中的名字一样。正是根据他们之间的对应关系来显示数据。  
			displayField:'text',    
			typeAhead : true,
			mode : 'local',
			triggerAction : 'all',
			selectOnFocus : true,// 设置用户能不能自己输入,true为只能选择列表中有的记录
			allowBlank : false  
		}); 
		
		//是否建立别名表下拉框
		var aliasTableFlagData = [  
			['0','是'],  
			['1','否']
		];  
		var aliasTableFlagStore = new Ext.data.SimpleStore(  
			{  
				fields:['value','text'],  
				data:aliasTableFlagData  
			}  
		);  
		var aliasTableFlagComboBox = new Ext.form.ComboBox({  
			fieldLabel : '建立别名表',
			emptyText : '请选择是否建立别名表',
			hiddenName : 'alias_table_flag',
			forceSelection : true,
			//hiddenValue:'table_id',
			anchor : '100%',
			store:aliasTableFlagStore,//store用来为ComboBox提供数据    
			valueField:'value',//与store定义中的名字一样。正是根据他们之间的对应关系来显示数据。  
			displayField:'text',    
			typeAhead : true,
			mode : 'local',
			triggerAction : 'all',
			selectOnFocus : true,// 设置用户能不能自己输入,true为只能选择列表中有的记录
			allowBlank : false  
		}); 					
		//参照模型
		var referenceIdModelStore = new Ext.data.JsonStore({
			fields: ['id_model', 'model_name'],
			url : "../mdmModel/query4ComboBox.shtml",
			autoLoad:true,
			root : ""
		});		
		var referenceIdModelComboBox = new Ext.form.ComboBox({
			fieldLabel : '参照模型',
			emptyText:'请选择参照模型',
			hiddenName : "reference_id_model",
			forceSelection: true,
			anchor : '100%',
    		store: referenceIdModelStore,
			valueField : "id_model",
			displayField : "model_name",
			typeAhead: true,
			mode: 'local',
			triggerAction: 'all',
			selectOnFocus : true,// 设置用户能不能自己输入,true为只能选择列表中有的记录
			allowBlank : false,
			listeners: {      
				select : function(referenceIdModelComboBox, record,index){ 
					//alert(referenceIdModelComboBox.value);
					//referenceIdAttributeStore.proxy= new Ext.data.HttpProxy({
					//url: '../mdmModelAttribute/query4ComboBox.shtml&id_model=' + //referenceIdModelComboBox.value});
					//referenceIdAttributeStore.load(); 
					referenceIdAttributeStore.load({
						params : {id_model : referenceIdModelComboBox.value}


					});
				}    
			}    			
		});	


		//参照属性
		var referenceIdAttributeStore = new Ext.data.JsonStore({
			fields: ['id_attribute', 'attribute_name'],
			url : "../mdmModelAttribute/query4ComboBox.shtml",
			autoLoad:false,
			root : ""
		});		
		var referenceIdAttributeComboBox = new Ext.form.ComboBox({
			fieldLabel : '参照属性',
			emptyText:'请选择参照属性',
			hiddenName : "reference_id_attribute",
			forceSelection: true,
			anchor : '100%',
    		store: referenceIdAttributeStore,
			valueField : "id_attribute",
			displayField : "attribute_name",
			typeAhead: true,
			mode: 'local',
			triggerAction: 'all',
			selectOnFocus : true,// 设置用户能不能自己输入,true为只能选择列表中有的记录
			allowBlank : false  
		});	
		
		//约束属性
		var constaintAttributeStore = new Ext.data.JsonStore({
			fields: ['id_attribute', 'attribute_name'],
			url : "../mdmModelAttribute/query4ComboBox.shtml",
			autoLoad:false,
			root : ""
		});	

			
			
		
		//表单
		var constaintForm = new Ext.form.FormPanel( {
			id : 'constaintForm',
			name : 'constaintForm',
			collapsible : false,
			border : true,
			labelWidth : 100, // 标签宽度
			frame : true, // 是否渲染表单面板背景色
			labelAlign : 'right', // 标签对齐方式
			bodyStyle : 'padding:0 0 5 0', // 表单元素和表单面板的边距
			buttonAlign : 'center',
			//height : 700,
			items : [{
				layout : 'column',
				border : false,
				anchor : '90%',
				items : [{
					columnWidth : .99,
					layout : 'form',
					labelWidth : 99, // 标签宽度
					defaultType : 'textfield',
					border : false,
					anchor : '110%',
					items : [{
						id:'id_constaint',
						fieldLabel : '模型约束编码', // 标签
						name : 'id_constaint', // name:后台根据此name属性取值
						maxLength : 50,
						allowBlank : true,
						regex: /^[\u0391-\uFFE5a-zA-Z0-9\-]{1,32}$/,
						regexText: '只能输入汉字、数字、减号和字母，且长度不得超过32',
						anchor : '100%',
						listeners: {
							//blur: handleSpace
						}
					},{
						id:'constaint_order',
						fieldLabel : '序号', // 标签
						name : 'constaint_order', // name:后台根据此name属性取值
						maxLength : 50,
						allowBlank : false,
						anchor : '100%',
						listeners: {
							//blur: handleSpace
						}
					},
					constaintTypeComboBox
					,{
						id:'constaint_name',
						fieldLabel : '约束名称', // 标签
						name : 'constaint_name', // name:后台根据此name属性取值
						maxLength : 50,
						allowBlank : false,
						anchor : '100%',
						listeners: {
							//blur: handleSpace
						}
					},
					referenceIdModelComboBox
					,
					referenceIdAttributeComboBox
					,
					aliasTableFlagComboBox
					,{  
						xtype: 'lovcombo',  
						fieldLabel: '约束属性',  
						hiddenName : "id_attributes",
						mode: 'local',  
						triggerAction: 'all',  
						store: constaintAttributeStore,  
						valueField: 'id_attribute',  
						displayField: 'attribute_name' ,
						editable : false,     
					
					},{
						id : 'id_attribute_constaint',
						name : 'id_attribute',
						hidden : true
					},{
						id : 'constaintwindowmode',
						name : 'constaintwindowmode',
						hidden : true
					}]

					
				} ]

			} ]

		});

		var constaintWindow = new Ext.Window( {
			layout : 'fit', // 设置窗口布局模式
			width : 400, // 窗口宽度
			height : 300, // 窗口高度
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
			items : [ constaintForm ], // 嵌入的表单面板
			buttons : [ {
				text : '保存',
				iconCls : 'acceptIcon',
				handler : function() {
					var mode = Ext.getCmp('constaintwindowmode').getValue();
					if (mode == 'add')
						submitConstaintForm();
					else
						updateConstaintForm();
				}
			},{
				text : '重置',
				iconCls : 'tbar_synchronizeIcon',
				handler : function() {
					clearForm(constaintForm.getForm());
				}
			},{
				text : '关闭',
				iconCls : 'deleteIcon',
				handler : function() {
					constaintWindow.hide();
				}
			} ]

		});

		


//---------------------------------------------------------------		
		
		var tabs=new Ext.TabPanel({
			region:'center',
			margins:'3 3 3 0',
			activeTab:0,
			frame:true, 
			defaults:{autoScroll:true},
			items:[
				modelForm,
				gridAttribute,
				gridConstaint
			],
			listeners : {
				'tabchange' : function(tab, newc, oldc) {
					console.log(tab);
					console.log(newc);
					console.log(oldc);
					if(newc.id == "id_grid_attribute"){
						var mode = Ext.getCmp('modelwindowmode').getValue();
						if (mode == 'add'){
							queryAttribute();
						}else{
							if(checkModelId()){
								queryAttribute();
							}

						}
					}else if(newc.id == "id_grid_constaint"){
						//点击约束页签时加载约束属性复选框数据
						constaintAttributeStore.load({
							params : {id_model : Ext.getCmp('id_model').getValue()}
						});						
						
						var mode = Ext.getCmp('modelwindowmode').getValue();

						if (mode == 'add'){	
							storeConstaint.load( {
								params : {
									id_model : Ext.getCmp('id_attribute_main').getValue(),
									start : 0,
									limit : 20
								}
							});							
						}else {
							//if(checkBeforeUpdate(gridAttribute)){
								//var id_attribute = getSelectAttibuteId(gridAttribute);
								storeConstaint.load( {
									params : {
										//id_attribute : id_attribute,
										start : 0,
										limit : 20
									}
								});	
							//}							
						}

					}
					
				}
			}
		});		
		
		var tab1GroupMgr = new Ext.WindowGroup();  
		//前置窗口
		tab1GroupMgr.zseed=0; 


		var modelWindow = new Ext.Window( {
			layout : 'fit', // 设置窗口布局模式
			width : 900, // 窗口宽度
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
			pageY : 50, // 页面定位Y坐标
			pageX : document.body.clientWidth / 2 - 600 / 2 -200, // 页面定位X坐标
			manager: tab1GroupMgr,
			items : [ tabs ], // 嵌入的表单面板
			listeners   : {
				'hide': function () {
					tabs.setActiveTab(0)

				}
			}
		});
		

		var viewport = new Ext.Viewport( {
			layout : 'border',
			items : [gridModel]
		});	

//------------------------------------------------------------------------------------		


		function setAuthor(){
			Ext.Ajax.request({
				url:"../mdmModel/getAuthor.shtml", 
				success:function(response,options){
						//alert(eval("["+response.responseText+"]")[0].user_name); 
						var  obj=eval("["+response.responseText+"]");
						var user_name=obj[0].user_name; 
						//alert(user_name);
						Ext.getCmp("model_author").setValue(user_name);
					}
			});
		}
		
		function queryModel() {
			storeModel.reload({
						params : {
							start : 0,
							limit : bbarModel.pageSize
							//,queryFTPName : Ext.getCmp('queryFTPName').getValue()
						}
					});
		}

		queryModel();
		
		/**
		 * 新增窗体初始化
		 */
		function addModel() {
			var flag = Ext.getCmp('modelwindowmode').getValue();
			if (typeof(flag) != 'undefined') {
				modelForm.form.getEl().dom.reset();
			} else {
				clearForm(modelForm.getForm());
			}
			modelWindow.show(); // 显示窗口
			modelWindow.setTitle('<span class="commoncss">新建模型</span>');
			Ext.getCmp('modelwindowmode').setValue('add');
			setAuthor();
			//Ext.getCmp('name').el.dom.readOnly = false;
			//Ext.getCmp('name').el.dom.style.color = "black";
		}

		/**
		 * 新增
		 */
		function submitModelForm() {
			if (!modelForm.getForm().isValid())
				return;
			modelForm.form.submit( {
				url : '../mdmModel/save.shtml',
				waitTitle : '提示',
				method : 'POST',
				waitMsg : '正在处理数据,请稍候...',
				success : function(form, action) {
					//modelWindow.hide();
					queryModel();
					Ext.getCmp('id_model').setValue(action.result.data.id_model);
					//form.reset();
					Ext.MessageBox.alert('提示', action.result.msg);
				},
				failure : function(form, action) {
					Ext.MessageBox.alert('提示', '数据保存失败');
				}
			});
		}
				
		
		/**
		 * 修改
		 */
		function updateModel() {
			var record = gridModel.getSelectionModel().getSelected();
			if (Ext.isEmpty(record)) {
				Ext.Msg.alert('提示:', '请先选中要修改的模型');
				return;
			}
			modelForm.getForm().loadRecord(record);
			modelWindow.show(); // 显示窗口
			modelWindow.setTitle('<span class="commoncss">修改模型</span>');
			Ext.getCmp('modelwindowmode').setValue('edit');
			setAuthor();
			//名称为主键设置为不可修改
			//Ext.getCmp('name').el.dom.readOnly = true;
			//Ext.getCmp('name').el.dom.style.color = "grey";
		}

		/**
		 * 修改
		 */
		function updateModelForm() {
			if (!modelForm.getForm().isValid())
				return;
			modelForm.form.submit( {
				url : '../mdmModel/update.shtml',
				waitTitle : '提示',
				method : 'POST',
				waitMsg : '正在处理数据,请稍候...',
				success : function(form, action) { // 回调函数有2个参数
					Ext.MessageBox.alert('提示', action.result.msg);
					//form.reset();
					//modelWindow.hide();
					queryModel();
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
		function deleteModel() {
			var record = gridModel.getSelectionModel().getSelections();
			if (Ext.isEmpty(record)) {
				Ext.Msg.alert('提示', '请先选中要删除的模型!');
				return;
			}
			var strChecked = jsArray2JsString(record, 'id_model');
			Ext.Msg.confirm('请确认', '你真的要删除吗?', function(btn, text) {
				if (btn == 'yes') {
					showWaitMsg();
					Ext.Ajax.request( {
						url : '../mdmModel/delete.shtml',
						success : function(response) {
							queryModel();
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



	function exportExcel() {
		var record = gridModel.getSelectionModel().getSelections();
		var strChecked = jsArray2JsString(record, 'id_model');

		window.location.href = "../mdmModel/exportExcel.shtml?ids="+strChecked;
	}
		
	
//---------------model end---------------------------------------------------------		
		function queryAttribute() {
			storeAttribute.load({
						params : {
							id_model : Ext.getCmp('id_model').getValue(),
							start : 0,
							limit : bbarAttribute.pageSize
							//,queryFTPName : Ext.getCmp('queryFTPName').getValue()
						}
					});
		}		
		
		/**
		 * 新增窗体初始化
		 */
		function addAttribute() {
			var flag = Ext.getCmp('attributewindowmode').getValue();
			if (typeof(flag) != 'undefined') {
				attributeForm.form.getEl().dom.reset();
			} else {
				clearForm(attributeForm.getForm());
			}
			attributeWindow.show(); // 显示窗口
			attributeWindow.setTitle('<span class="commoncss">新增</span>');
			Ext.getCmp('id_model_attribute').setValue(Ext.getCmp('id_model').getValue());
			Ext.getCmp('attributewindowmode').setValue('add');
			//Ext.getCmp('name').el.dom.readOnly = false;
			//Ext.getCmp('name').el.dom.style.color = "black";
			
		}

		/**
		 * 新增
		 */
		function submitAttributeForm() {
			if (!attributeForm.getForm().isValid())
				return;
			attributeForm.form.submit( {
				url : '../mdmModelAttribute/save.shtml',
				waitTitle : '提示',
				method : 'POST',
				waitMsg : '正在处理数据,请稍候...',
				success : function(form, action) {
					var checked = Ext.getCmp('batchSave').checked;
					if(!checked){
						attributeWindow.hide();
					}
					Ext.MessageBox.alert('提示', action.result.msg);
					queryAttribute();
					form.reset();

				},
				failure : function(form, action) {
					Ext.MessageBox.alert('提示', '数据保存失败');
				}
			});
		}
				
		
		/**
		 * 修改
		 */
		function updateAttribute() {
			var record = gridAttribute.getSelectionModel().getSelected();
			if (Ext.isEmpty(record)) {
				Ext.Msg.alert('提示:', '请先选中要修改的模型');
				return;
			}
			attributeForm.getForm().loadRecord(record);
			attributeWindow.show(); // 显示窗口
			attributeWindow.setTitle('<span class="commoncss">修改</span>');
			Ext.getCmp('attributewindowmode').setValue('edit');
			//名称为主键设置为不可修改
			//Ext.getCmp('name').el.dom.readOnly = true;	
			//Ext.getCmp('name').el.dom.style.color = "grey";
		}

		/**
		 * 修改
		 */
		function updateAttributeForm() {
			if (!attributeForm.getForm().isValid())
				return;
			attributeForm.form.submit( {
				url : '../mdmModelAttribute/update.shtml',
				waitTitle : '提示',
				method : 'POST',
				waitMsg : '正在处理数据,请稍候...',
				success : function(form, action) { // 回调函数有2个参数
					Ext.MessageBox.alert('提示', action.result.msg);
					form.reset();
					attributeWindow.hide();
					queryAttribute();
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
		function deleteAttribute() {
			var record = gridAttribute.getSelectionModel().getSelections();
			if (Ext.isEmpty(record)) {
				Ext.Msg.alert('提示', '请先选中要删除的模型!');
				return;
			}
			var strChecked = jsArray2JsString(record, 'id_attribute');
			Ext.Msg.confirm('请确认', '你真的要删除吗?', function(btn, text) {
				if (btn == 'yes') {
					showWaitMsg();
					Ext.Ajax.request( {
						url : '../mdmModelAttribute/delete.shtml',
						success : function(response) {
							queryAttribute();
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

//------------------------------------------------------------------------------------	
		function queryConstaint() {
			storeConstaint.reload({
						params : {
							//id_attribute : getSelectAttibuteId(gridAttribute),
							start : 0,
							limit : bbarConstaint.pageSize
							//,queryFTPName : Ext.getCmp('queryFTPName').getValue()
						}
					});
		}		
		
		/**
		 * 新增窗体初始化
		 */
		function addConstaint() {
			var flag = Ext.getCmp('constaintwindowmode').getValue();
			if (typeof(flag) != 'undefined') {
				constaintForm.form.getEl().dom.reset();
			} else {
				clearForm(constaintForm.getForm());
			}
			constaintWindow.show(); // 显示窗口
			constaintWindow.setTitle('<span class="commoncss">新增</span>');
			//Ext.getCmp('id_attribute_constaint').setValue(getSelectAttibuteId(gridAttribute));
			Ext.getCmp('constaintwindowmode').setValue('add');
			//Ext.getCmp('name').el.dom.readOnly = false;
			//Ext.getCmp('name').el.dom.style.color = "black";
		}

		/**
		 * 新增
		 */
		function submitConstaintForm() {
			if (!constaintForm.getForm().isValid())
				return;
			constaintForm.form.submit( {
				url : '../mdmModelConstaint/save.shtml',
				waitTitle : '提示',
				method : 'POST',
				waitMsg : '正在处理数据,请稍候...',
				success : function(form, action) {
					constaintWindow.hide();
					queryConstaint();
					form.reset();
					Ext.MessageBox.alert('提示', action.result.msg);
				},
				failure : function(form, action) {
					Ext.MessageBox.alert('提示', '数据保存失败');
				}
			});
		}
				
		
		/**
		 * 修改
		 */
		function updateConstaint() {
			var record = gridConstaint.getSelectionModel().getSelected();
			if (Ext.isEmpty(record)) {
				Ext.Msg.alert('提示:', '请先选中要修改的模型约束');
				return;
			}
			constaintForm.getForm().loadRecord(record);
			constaintWindow.show(); // 显示窗口
			constaintWindow.setTitle('<span class="commoncss">修改</span>');
			Ext.getCmp('constaintwindowmode').setValue('edit');
			//名称为主键设置为不可修改
			//Ext.getCmp('name').el.dom.readOnly = true;	
			//Ext.getCmp('name').el.dom.style.color = "grey";
		}

		/**
		 * 修改
		 */
		function updateConstaintForm() {
			if (!constaintForm.getForm().isValid())
				return;
			constaintForm.form.submit( {
				url : '../mdmModelConstaint/update.shtml',
				waitTitle : '提示',
				method : 'POST',
				waitMsg : '正在处理数据,请稍候...',
				success : function(form, action) { // 回调函数有2个参数
					Ext.MessageBox.alert('提示', action.result.msg);
					form.reset();
					constaintWindow.hide();
					queryConstaint();
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
		function deleteConstaint() {
			var record = gridConstaint.getSelectionModel().getSelections();
			if (Ext.isEmpty(record)) {
				Ext.Msg.alert('提示', '请先选中要删除的模型!');
				return;
			}
			var strChecked = jsArray2JsString(record, 'id_constaint');
			Ext.Msg.confirm('请确认', '你真的要删除吗?', function(btn, text) {
				if (btn == 'yes') {
					showWaitMsg();
					Ext.Ajax.request( {
						url : '../mdmModelConstaint/delete.shtml',
						success : function(response) {
							queryConstaint();
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

		function getSelectModelId(grid) {
			var selModel = grid.getSelectionModel();
			if(selModel) {
				var selects = selModel.getSelections();
				if((selects && selects.length == 1)) {
					return selects[0].get("id_model");
				}
			}
			return "";
		}

		function getSelectAttibuteId(grid) {
			var selModel = grid.getSelectionModel();
			if(selModel) {
				var selects = selModel.getSelections();
				if((selects && selects.length == 1)) {
					return selects[0].get("id_attribute");
				}
			}
			return "";
		}

//------------------------------------------------------------------------------------	
		/**
		 * 在点击【修改】按钮前，判断是不是有选择且只选择了一条记录
		 */
		function checkBeforeUpdate(grid) {
			var selModel = grid.getSelectionModel();
			if(selModel) {
				var selects = selModel.getSelections();
				if(!(selects && selects.length == 1)) {
					Ext.MessageBox.alert('提示', '请先选择一条记录！');
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

