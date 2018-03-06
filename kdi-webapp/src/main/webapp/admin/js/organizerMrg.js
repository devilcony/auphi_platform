
Ext.onReady(function() {
	// 定义列模型
		var sm = new Ext.grid.CheckboxSelectionModel();
		var rownum = new Ext.grid.RowNumberer( {
			header : 'NO',
			width : 28
		});
		var cm = new Ext.grid.ColumnModel( [ rownum, sm, 
		{
			header : '机构编号',
			dataIndex : 'organizer_id',
			hidden : true
		},		
		{
			header : '机构名称', 
			dataIndex : 'organizer_name', 
			sortable : true,
			width : 100
		}, {
			header : '联系人',
			dataIndex : 'organizer_contact',
			sortable : true,
			width : 100
		}, {
			header : '邮箱',
			dataIndex : 'organizer_email',
			sortable : true,
			width : 100
		},{
			header : '固定电话', 
			dataIndex : 'organizer_telphone', 
			sortable : true,
			width : 100
		},{
			header : '手机', 
			dataIndex : 'organizer_mobile', 
			sortable : true,
			width : 100
		},{
			header : '地址', 
			dataIndex : 'organizer_address', 
			sortable : true,
			width : 100
		},{
			header : '状态', 
			dataIndex : 'organizer_status', 
			sortable : true,
			width : 100,
			renderer : function(v) {
				if (v == 1) {
					return "已激活";
				} else if (v == 0) {
					return "未激活";
				}else if (v == 2) {
					return "已停用";
				} else {
					return v;
				}
			}
		} ]);
		
		var store = new Ext.data.Store( {
			proxy : new Ext.data.HttpProxy( {
				url : '../organizerMrg/query.shtml'
			}),
			reader : new Ext.data.JsonReader( {
				totalProperty : 'total',
				root : 'rows' 
			}, [ {
					name : 'organizer_id' 
				}, {
					name : 'organizer_name' 
				}, {
					name : 'organizer_contact'
				}, {
					name : 'organizer_email'
				}, {
					name : 'organizer_telphone'
				}, {
					name : 'organizer_mobile'
				}, {
					name : 'organizer_address'
				}, {
					name : 'organizer_status'
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
							addOrganizer();
						}
					},'-',{
						text : '修改',
						id : 'tbi_edit',
						iconCls : 'page_edit_1Icon',
						handler : function() {
							if(checkBeforeUpdate(grid)) {
								updateOrganizer();
							}
						}
					},'-',{
						text : '激活',
						id : 'tbi_active',
						iconCls : 'page_edit_1Icon',
						handler : function() {
							if(checkBeforeDelete(grid)) {
								activeOrganizer();
							}
						}
					},'-',{
						text : '停用',
						id : 'tbi_stop',
						iconCls : 'page_delIcon',
						handler : function() {
							if(checkBeforeDelete(grid)) {
								stopOrganizer();
							}
						}
					},'-',{
						text : '删除',
						id : 'tbi_del',
						iconCls : 'page_delIcon',
						handler : function() {
							if(checkBeforeDelete(grid)) {
								deleteOrganizer();
							}
						}
					}
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
			updateOrganizer();
		});
		
		
		//表单
		var addOrganizerForm = new Ext.form.FormPanel( {
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
						id:'organizer_name',
						fieldLabel : '机构名称', // 标签
						name : 'organizer_name', // name:后台根据此name属性取值
						maxLength : 50,
						allowBlank : true,
						regex: /^[\u0391-\uFFE5a-zA-Z0-9\-]{1,32}$/,
						regexText: '只能输入汉字、数字、减号和字母，且长度不得超过32',
						anchor : '100%',
						listeners: {
							//blur: handleSpace
						}
					},{
						id:'organizer_contact',
						fieldLabel : '联系人', // 标签
						name : 'organizer_contact', // name:后台根据此name属性取值
						maxLength : 50,
						allowBlank : false,
						anchor : '100%',
						listeners: {
							//blur: handleSpace
						}
					},{
						id:'organizer_email',
						fieldLabel : '邮箱', // 标签
						name : 'organizer_email', // name:后台根据此name属性取值
						maxLength : 50,
						allowBlank : false,
						anchor : '100%',
						listeners: {
							//blur: handleSpace
						}
					},{
						inputType:'password',
						id:'organizer_password',
						fieldLabel : '密码', // 标签
						name : 'organizer_password', // name:后台根据此name属性取值
						maxLength : 50,
						allowBlank : false,
						anchor : '100%',
						listeners: {
							//blur: handleSpace
						}
					},{
						id:'organizer_telphone',
						fieldLabel : '固定电话', // 标签
						name : 'organizer_telphone', // name:后台根据此name属性取值
						maxLength : 50,
						allowBlank : false,
						anchor : '100%',
						listeners: {
							//blur: handleSpace
						}
					},{
						id:'organizer_mobile',
						fieldLabel : '手机', // 标签
						name : 'organizer_mobile', // name:后台根据此name属性取值
						maxLength : 50,
						allowBlank : false,
						anchor : '100%',
						listeners: {
							//blur: handleSpace
						}
					},{
						id:'organizer_address',
						fieldLabel : '地址', // 标签
						name : 'organizer_address', // name:后台根据此name属性取值
						maxLength : 50,
						allowBlank : false,
						anchor : '100%',
						listeners: {
							//blur: handleSpace
						}
					},{
						id : 'organizer_id',
						name : 'organizer_id',
						hidden : true
					}, {
						id : 'windowmode',
						name : 'windowmode',
						hidden : true
					}]
				} ]

			} ]

		});

		var addOrganizerWindow = new Ext.Window( {
			layout : 'fit', // 设置窗口布局模式
			width : 330, // 窗口宽度
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
			items : [ addOrganizerForm ], // 嵌入的表单面板
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
					text : '重置',
					id : 'btnReset',
					iconCls : 'tbar_synchronizeIcon',
					handler : function() {
						clearForm(addOrganizerForm.getForm());
					}
			},{
					text : '关闭',
					iconCls : 'deleteIcon',
					handler : function() {
						addOrganizerWindow.hide();
					}
			}]
		});
		var viewport = new Ext.Viewport( {
			layout : 'border',
			items : [grid]
		});
		
		function queryOrganizer() {
			store.reload({
						params : {
							start : 0,
							limit : bbar.pageSize
							//,queryOrganizerName : Ext.getCmp('queryOrganizerName').getValue()
						}
					});
		}		
		
		/**
		 * 新增窗体初始化
		 */
		function addOrganizer() {
			var flag = Ext.getCmp('windowmode').getValue();
			if (typeof(flag) != 'undefined') {
				addOrganizerForm.form.getEl().dom.reset();
			} else {
				clearForm(addOrganizerForm.getForm());
			}
			addOrganizerWindow.show(); // 显示窗口
			addOrganizerWindow.setTitle('<span class="commoncss">新增机构</span>');
			Ext.getCmp('windowmode').setValue('add');
			//Ext.getCmp('name').el.dom.readOnly = false;
			//Ext.getCmp('name').el.dom.style.color = "black";
		}

		/**
		 * 新增
		 */
		function submitTheForm() {
			if (!addOrganizerForm.getForm().isValid())
				return;
			addOrganizerForm.form.submit( {
				url : '../organizerMrg/save.shtml',
				waitTitle : '提示',
				method : 'POST',
				waitMsg : '正在处理数据,请稍候...',
				success : function(form, action) {
					addOrganizerWindow.hide();
					queryOrganizer();
					form.reset();
					Ext.MessageBox.alert('提示', action.result.msg);
				},
				failure : function(form, action) {
					Ext.MessageBox.alert('提示', '数据保存失败');
				}
			});
		}
		
		/**
		 * 激活用户
		 */
		function activeOrganizer() {
			var record = grid.getSelectionModel().getSelections();
			if (Ext.isEmpty(record)) {
				Ext.Msg.alert('提示', '请先选中要激活的机构!');
				return;
			}
			var strChecked = jsArray2JsString(record, 'organizer_id');
			showWaitMsg();
			Ext.Ajax.request( {
				url : '../organizerMrg/activation.shtml',
				success : function(response) {
					queryOrganizer();
					var resultArray = Ext.util.JSON
					.decode(response.responseText);
					Ext.MessageBox.alert('提示', resultArray.msg);
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
		
		/**
		 * 修改
		 */
		function updateOrganizer() {
			var record = grid.getSelectionModel().getSelected();
			if (Ext.isEmpty(record)) {
				Ext.Msg.alert('提示:', '请先选中要修改的机构');
				return;
			}
			addOrganizerForm.getForm().loadRecord(record);
			addOrganizerWindow.show(); // 显示窗口
			addOrganizerWindow.setTitle('<span class="commoncss">修改机构</span>');
			Ext.getCmp('windowmode').setValue('edit');
			//名称为主键设置为不可修改
			//Ext.getCmp('name').el.dom.readOnly = true;	
			//Ext.getCmp('name').el.dom.style.color = "grey";
		}

		/**
		 * 修改
		 */
		function updateTheForm() {
			if (!addOrganizerForm.getForm().isValid())
				return;
			addOrganizerForm.form.submit( {
				url : '../organizerMrg/update.shtml',
				waitTitle : '提示',
				method : 'POST',
				waitMsg : '正在处理数据,请稍候...',
				success : function(form, action) { // 回调函数有2个参数
					Ext.MessageBox.alert('提示', action.result.msg);
					form.reset();
					addOrganizerWindow.hide();
					queryOrganizer();
				},
				failure : function(form, action) {
					Ext.Msg.alert('提示', '数据保存失败,错误类型:' + action.failureType);
				}
			});
		}

		/**
		 * 停用
		 * 
		 */
		function stopOrganizer() {
			var record = grid.getSelectionModel().getSelections();
			if (Ext.isEmpty(record)) {
				Ext.Msg.alert('提示', '请先选中要停用的机构!');
				return;
			}
			var strChecked = jsArray2JsString(record, 'organizer_id');
			Ext.Msg.confirm('请确认', '你真的要停用吗?', function(btn, text) {
				if (btn == 'yes') {
					showWaitMsg();
					Ext.Ajax.request( {
						url : '../organizerMrg/stop.shtml',
						success : function(response) {
							queryOrganizer();
							var resultArray = Ext.util.JSON.decode(response.responseText);
							Ext.Msg.alert('提示', resultArray.msg);
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
		 * 删除
		 * 
		 */
		function deleteOrganizer() {
			var record = grid.getSelectionModel().getSelections();
			if (Ext.isEmpty(record)) {
				Ext.Msg.alert('提示', '请先选中要删除的机构!');
				return;
			}
			var strChecked = jsArray2JsString(record, 'organizer_id');
			Ext.Msg.confirm('请确认', '你真的要删除吗?', function(btn, text) {
				if (btn == 'yes') {
					showWaitMsg();
					Ext.Ajax.request( {
						url : '../organizerMrg/delete.shtml',
						success : function(response) {
							queryOrganizer();
							var resultArray = Ext.util.JSON.decode(response.responseText);
							Ext.Msg.alert('提示', resultArray.msg);
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
		 * 在点击【更新】按钮前，判断是不是有选择且只选择了一条记录
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

