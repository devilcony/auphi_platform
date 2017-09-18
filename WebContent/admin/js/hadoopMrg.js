
Ext.onReady(function() {
	// 定义列模型
		var sm = new Ext.grid.CheckboxSelectionModel({
			listeners: {
//				rowdeselect: function() {
					//autoCheckGridHead(Ext.getCmp('id_grid_sfxm'));
//				}
			}
		});
		var rownum = new Ext.grid.RowNumberer( {
			header : 'NO',
			width : 28
		});
		var cm = new Ext.grid.ColumnModel( [ rownum, sm, 
		{
			header : 'Hadoop数据源编号',
			dataIndex : 'id',
			hidden : true
		}, {
			header : 'Hadoop服务器',
			dataIndex : 'server',
			sortable : true,
			width : 100
		}, {
			header : '端口',
			dataIndex : 'port',
			sortable : true,
			width : 100
		},{
			header : '用户名', 
			dataIndex : 'userid', 
			sortable : true,
			width : 100
		} ]);
		
		var store = new Ext.data.Store( {
			proxy : new Ext.data.HttpProxy( {
				url : '../hadoopMrg/query.shtml'
			}),
			reader : new Ext.data.JsonReader( {
				totalProperty : 'total',
				root : 'rows' 
			}, [ {
					name : 'id' 
				}, {
					name : 'server' 
				}, {
					name : 'port'
				}, {
					name : 'userid'
				}, {
					name : 'password'
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
		
		// 每页显示条数下拉选择框
		var pagesize_combo = new Ext.form.ComboBox( {
			name : 'pagesize',
			triggerAction : 'all',
			mode : 'local',
			store : new Ext.data.ArrayStore(
					{
						fields : [ 'value', 'text' ],
						data : [ [ 10, '10条/页' ], [ 20, '20条/页' ],
								[ 50, '50条/页' ], [ 100, '100条/页' ],
								[ 250, '250条/页' ], [ 500, '500条/页' ] ]
					}),
			valueField : 'value',
			displayField : 'text',
			value : '20',
			editable : false,
			width : 85
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
							addHadoopItem();
						}
					},'-',{
						text : '修改',
						id : 'tbi_edit',
						iconCls : 'page_edit_1Icon',
						handler : function() {
							if(checkBeforeUpdate(grid)) {
								updateHadoopItem();
							}
						}
					},'-',{
						text : '删除',
						id : 'tbi_del',
						iconCls : 'page_delIcon',
						handler : function() {
							if(checkBeforeDelete(grid)) {
								deleteHadoopItem();
							}
						}
					}
//					,  '-', '->',
//					new Ext.form.TextField({
//								id : 'queryHadoopName',
//								name : 'queryHadoopName',
//								emptyText : '查询的Hadoop数据源名称',
//								enableKeyEvents : true,
//								listeners : {
//									specialkey : function(field, e) {
//										if (e.getKey() == Ext.EventObject.ENTER) {
//											queryHadoopItem();
//										}
//									}
//								},
//								width : 150
//					}), {
//						text : '查询',
//						iconCls : 'previewIcon',
//						handler : function() {
//							queryHadoopItem();
//						}
//					}, '-', {
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
			updateHadoopItem();
		});
		
		
		//表单
		var addHadoopForm = new Ext.form.FormPanel( {
			id : 'codeForm',
			name : 'codeForm',
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
						id:'server',
						fieldLabel : 'Hadoop服务器', // 标签
						name : 'server', // name:后台根据此name属性取值
						maxLength : 50,
						allowBlank : true,
//						regex: /^[\u0391-\uFFE5a-zA-Z0-9\-]{1,32}$/,
//						regexText: '只能输入汉字、数字、减号和字母，且长度不得超过32',
						anchor : '100%',
						listeners: {
							//blur: handleSpace
						}
					},{
						id:'port',
						fieldLabel : '端口', // 标签
						name : 'port', // name:后台根据此name属性取值
						maxLength : 50,
						allowBlank : false,
						value : '9000',
						anchor : '100%',
						listeners: {
							//blur: handleSpace
						}
					},{
						id:'userid',
						fieldLabel : '用户名', // 标签
						name : 'userid', // name:后台根据此name属性取值
						maxLength : 50,
						allowBlank : false,
						anchor : '100%',
						listeners: {
							//blur: handleSpace
						}
					},{
						id:'password',
						fieldLabel : '密码', // 标签
						name : 'password', // name:后台根据此name属性取值
						maxLength : 50,
						allowBlank : false,
						inputType : 'password',
						anchor : '100%',
						listeners: {
							//blur: handleSpace
						}
					},{
						id : 'id',
						name : 'id',
						hidden : true
					}, {
						id : 'windowmode',
						name : 'windowmode',
						hidden : true
					}]
				} ]

			} ]

		});

		var addHadoopWindow = new Ext.Window( {
			layout : 'fit', // 设置窗口布局模式
			width : 330, // 窗口宽度
			height : 200, // 窗口高度
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
			items : [ addHadoopForm ], // 嵌入的表单面板
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
			}, {
					text : '测试',
					//iconCls : 'acceptIcon',
					handler : function() {
						testHadoop();
					}
				},{
					text : '重置',
					id : 'btnReset',
					iconCls : 'tbar_synchronizeIcon',
					handler : function() {
						clearForm(addHadoopForm.getForm());
					}
				},{
					text : '关闭',
					iconCls : 'deleteIcon',
					handler : function() {
					addHadoopWindow.hide();
				}
			} ]
		});
		var viewport = new Ext.Viewport( {
			layout : 'border',
			items : [grid]
		});
		
		function queryHadoopItem() {
			store.load({
						params : {
							start : 0,
							limit : bbar.pageSize
							//,queryHadoopName : Ext.getCmp('queryHadoopName').getValue()
						}
					});
		}		
		
		/**
		 * 新增窗体初始化
		 */
		function addHadoopItem() {
			var flag = Ext.getCmp('windowmode').getValue();
			if (typeof(flag) != 'undefined') {
				addHadoopForm.form.getEl().dom.reset();
			} else {
				clearForm(addHadoopForm.getForm());
			}
			addHadoopWindow.show(); // 显示窗口
			addHadoopWindow.setTitle('<span class="commoncss">新增Hadoop</span>');
			Ext.getCmp('windowmode').setValue('add');
			//Ext.getCmp('server').el.dom.readOnly = false;
			//Ext.getCmp('server').el.dom.style.color = "black";
		}

		/**
		 * 新增
		 */
		function submitTheForm() {
			if (!addHadoopForm.getForm().isValid())
				return;
			addHadoopForm.form.submit( {
				url : '../hadoopMrg/save.shtml',
				waitTitle : '提示',
				method : 'POST',
				waitMsg : '正在处理数据,请稍候...',
				success : function(form, action) {
					addHadoopWindow.hide();
					queryHadoopItem();
					form.reset();
					Ext.MessageBox.alert('提示', action.result.msg);
				},
				failure : function(form, action) {
					Ext.MessageBox.alert('提示', '数据保存失败');
				}
			});
		}
		
		/**
		 * 测试连接
		 */
		function testHadoop() {
			if (!addHadoopForm.getForm().isValid())
				return;
			addHadoopForm.form.submit( {
				url : '../hadoopMrg/test.shtml',
				waitTitle : '提示',
				method : 'POST',
				waitMsg : '正在测试连接,请稍候...',
				success : function(form, action) {
					Ext.MessageBox.alert('提示', action.result.msg);
				},
				failure : function(form, action) {
					Ext.MessageBox.alert('提示', '连接失败');
				}
			});
		}		
		
		/**
		 * 修改
		 */
		function updateHadoopItem() {
			var record = grid.getSelectionModel().getSelected();
			if (Ext.isEmpty(record)) {
				Ext.Msg.alert('提示:', '请先选中要修改的Hadoop');
				return;
			}
			addHadoopForm.getForm().loadRecord(record);
			addHadoopWindow.show(); // 显示窗口
			addHadoopWindow.setTitle('<span class="commoncss">修改Hadoop</span>');
			Ext.getCmp('windowmode').setValue('edit');
			//名称为主键设置为不可修改
			//Ext.getCmp('server').el.dom.readOnly = true;	
			//Ext.getCmp('server').el.dom.style.color = "grey";
		}

		/**
		 * 修改
		 */
		function updateTheForm() {
			if (!addHadoopForm.getForm().isValid())
				return;
			addHadoopForm.form.submit( {
				url : '../hadoopMrg/update.shtml',
				waitTitle : '提示',
				method : 'POST',
				waitMsg : '正在处理数据,请稍候...',
				success : function(form, action) { // 回调函数有2个参数
					Ext.MessageBox.alert('提示', action.result.msg);
					form.reset();
					addHadoopWindow.hide();
					queryHadoopItem();
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
		function deleteHadoopItem() {
			var record = grid.getSelectionModel().getSelections();
			if (Ext.isEmpty(record)) {
				Ext.Msg.alert('提示', '请先选中要删除的Hadoop数据源!');
				return;
			}
			var strChecked = jsArray2JsString(record, 'id');
			Ext.Msg.confirm('请确认', '你真的要删除吗?', function(btn, text) {
				if (btn == 'yes') {
					showWaitMsg();
					Ext.Ajax.request( {
						url : '../hadoopMrg/delete.shtml',
						success : function(response) {
							queryHadoopItem();
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

