
Ext.onReady(function() {
	// 定义列模型
		var sm = new Ext.grid.CheckboxSelectionModel();
		var rownum = new Ext.grid.RowNumberer( {
			header : 'NO',
			width : 28
		});
		var cm = new Ext.grid.ColumnModel( [ rownum, sm, 
		{
			header : 'FTP数据源编号',
			dataIndex : 'id_ftp',
			hidden : true
		},		
		{
			header : 'FTP数据源名称', 
			dataIndex : 'name', 
			sortable : true,
			width : 100
		}, {
			header : '主机',
			dataIndex : 'host_name',
			sortable : true,
			width : 100
		}, {
			header : '端口',
			dataIndex : 'port',
			sortable : true,
			width : 100
		},{
			header : '用户名', 
			dataIndex : 'username', 
			sortable : true,
			width : 100
		} ]);
		
		var store = new Ext.data.Store( {
			proxy : new Ext.data.HttpProxy( {
				url : '../ftpMrg/query.shtml'
			}),
			reader : new Ext.data.JsonReader( {
				totalProperty : 'total',
				root : 'rows' 
			}, [ {
					name : 'id_ftp' 
				}, {
					name : 'name' 
				}, {
					name : 'host_name'
				}, {
					name : 'port'
				}, {
					name : 'username'
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
							addFTPItem();
						}
					},'-',{
						text : '修改',
						id : 'tbi_edit',
						iconCls : 'page_edit_1Icon',
						handler : function() {
							if(checkBeforeUpdate(grid)) {
								updateFTPItem();
							}
						}
					},'-',{
						text : '删除',
						id : 'tbi_del',
						iconCls : 'page_delIcon',
						handler : function() {
							if(checkBeforeDelete(grid)) {
								deleteFTPItem();
							}
						}
					}
//					,  '-', '->',
//					new Ext.form.TextField({
//								id : 'queryFTPName',
//								name : 'queryFTPName',
//								emptyText : '查询的FTP数据源名称',
//								enableKeyEvents : true,
//								listeners : {
//									specialkey : function(field, e) {
//										if (e.getKey() == Ext.EventObject.ENTER) {
//											queryFTPItem();
//										}
//									}
//								},
//								width : 150
//					}), 
//					{
//						text : '查询',
//						iconCls : 'previewIcon',
//						handler : function() {
//							queryFTPItem();
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
			updateFTPItem();
		});
		
		
		//表单
		var addFTPForm = new Ext.form.FormPanel( {
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
						id:'name',
						fieldLabel : '名称', // 标签
						name : 'name', // name:后台根据此name属性取值
						maxLength : 50,
						allowBlank : true,
						regex: /^[\u0391-\uFFE5a-zA-Z0-9\-]{1,32}$/,
						regexText: '只能输入汉字、数字、减号和字母，且长度不得超过32',
						anchor : '100%',
						listeners: {
							//blur: handleSpace
						}
					},{
						id:'host_name',
						fieldLabel : '主机', // 标签
						name : 'host_name', // name:后台根据此name属性取值
						maxLength : 50,
						allowBlank : false,
						anchor : '100%',
						listeners: {
							//blur: handleSpace
						}
					},{
						xtype : 'textfield',
						id:'port',
						fieldLabel : '端口', // 标签
						name : 'port', // name:后台根据此name属性取值
						maxLength : 50,
						allowBlank : false,
						value : '21',
						anchor : '100%',
						listeners: {
							//blur: handleSpace
						}
					},{
						id:'username',
						fieldLabel : '用户名', // 标签
						name : 'username', // name:后台根据此name属性取值
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
						id : 'id_ftp',
						name : 'id_ftp',
						hidden : true
					}, {
						id : 'windowmode',
						name : 'windowmode',
						hidden : true
					}]
				} ]

			} ]

		});

		var addFTPWindow = new Ext.Window( {
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
			items : [ addFTPForm ], // 嵌入的表单面板
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
						testFTP();
					}
				},{
					text : '重置',
					id : 'btnReset',
					iconCls : 'tbar_synchronizeIcon',
					handler : function() {
						clearForm(addFTPForm.getForm());
					}
				},{
					text : '关闭',
					iconCls : 'deleteIcon',
					handler : function() {
					addFTPWindow.hide();
				}
			} ]
		});
		var viewport = new Ext.Viewport( {
			layout : 'border',
			items : [grid]
		});
		
		function queryFTPItem() {
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
		function addFTPItem() {
			var flag = Ext.getCmp('windowmode').getValue();
			if (typeof(flag) != 'undefined') {
				addFTPForm.form.getEl().dom.reset();
			} else {
				clearForm(addFTPForm.getForm());
			}
			addFTPWindow.show(); // 显示窗口
			addFTPWindow.setTitle('<span class="commoncss">新增FTP</span>');
			Ext.getCmp('windowmode').setValue('add');
			//Ext.getCmp('name').el.dom.readOnly = false;
			//Ext.getCmp('name').el.dom.style.color = "black";
		}

		/**
		 * 新增
		 */
		function submitTheForm() {
			if (!addFTPForm.getForm().isValid())
				return;
			addFTPForm.form.submit( {
				url : '../ftpMrg/save.shtml',
				waitTitle : '提示',
				method : 'POST',
				waitMsg : '正在处理数据,请稍候...',
				success : function(form, action) {
					addFTPWindow.hide();
					queryFTPItem();
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
		function testFTP() {
			if (!addFTPForm.getForm().isValid())
				return;
			addFTPForm.form.submit( {
				url : '../ftpMrg/test.shtml',
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
		function updateFTPItem() {
			var record = grid.getSelectionModel().getSelected();
			if (Ext.isEmpty(record)) {
				Ext.Msg.alert('提示:', '请先选中要修改的FTP');
				return;
			}
			addFTPForm.getForm().loadRecord(record);
			addFTPWindow.show(); // 显示窗口
			addFTPWindow.setTitle('<span class="commoncss">修改FTP</span>');
			Ext.getCmp('windowmode').setValue('edit');
			//名称为主键设置为不可修改
			//Ext.getCmp('name').el.dom.readOnly = true;	
			//Ext.getCmp('name').el.dom.style.color = "grey";
		}

		/**
		 * 修改
		 */
		function updateTheForm() {
			if (!addFTPForm.getForm().isValid())
				return;
			addFTPForm.form.submit( {
				url : '../ftpMrg/update.shtml',
				waitTitle : '提示',
				method : 'POST',
				waitMsg : '正在处理数据,请稍候...',
				success : function(form, action) { // 回调函数有2个参数
					Ext.MessageBox.alert('提示', action.result.msg);
					form.reset();
					addFTPWindow.hide();
					queryFTPItem();
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
		function deleteFTPItem() {
			var record = grid.getSelectionModel().getSelections();
			if (Ext.isEmpty(record)) {
				Ext.Msg.alert('提示', '请先选中要删除的FTP数据源!');
				return;
			}
			var strChecked = jsArray2JsString(record, 'id_ftp');
			Ext.Msg.confirm('请确认', '你真的要删除吗?', function(btn, text) {
				if (btn == 'yes') {
					showWaitMsg();
					Ext.Ajax.request( {
						url : '../ftpMrg/delete.shtml',
						success : function(response) {
							queryFTPItem();
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

