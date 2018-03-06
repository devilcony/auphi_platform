/**
 * 操作员事件跟踪
 * 
 */
Ext.onReady(function() {
			var expander = new Ext.grid.RowExpander(
					{
						tpl : new Ext.Template(
								'<p style=margin-left:70px;><span style=color:Teal;>描述信息</span><br><span>{description}</span></p>',
								'<p style=margin-left:70px;><span style=color:Teal;>请求路径</span><br><span>{requestpath}</span></p>',
								'<p style=margin-left:70px;><span style=color:Teal;>请求方法</span><br><span>{methodname}</span></p>',
								'<p style=margin-left:70px;><span style=color:Teal;>耗时</span><br><span>{costtime}</span></p>'),
						// 屏蔽双击事件
						expandOnDblClick : true
					});
			var sm = new Ext.grid.CheckboxSelectionModel();
			var cm = new Ext.grid.ColumnModel( [ new Ext.grid.RowNumberer(),
					sm, expander, {
						header : '激活时间',
						dataIndex : 'activetime',
						sortable : true,
						width : 130
					}, {
						header : '描述信息',
						dataIndex : 'description',
						width : 350,
						sortable : true
					}, {
						header : '耗时',
						dataIndex : 'costtime',
						width : 80,
						sortable : true
					}, {
						header : '请求路径',
						dataIndex : 'requestpath',
						sortable : true,
						width : 200
					}, {
						header : '请求方法',
						dataIndex : 'methodname',
						sortable : true,
						width : 150
					}, {
						header : '用户名',
						dataIndex : 'username',
						width : 80,
						sortable : true
					}, {
						header : '事件编号',
						dataIndex : 'eventid',
						hidden : true,
						width : 120,
						sortable : true
					}, {
						header : '用户ID',
						dataIndex : 'userid',
						hidden : true
					}, {
						header : '帐户',
						dataIndex : 'account',
						width : 80,
						sortable : true
					}, {
						id : '_blank',
						dataIndex : '_blank'
					} ]);

			/**
			 * 数据存储
			 */
			var store = new Ext.data.Store( {
				proxy : new Ext.data.HttpProxy( {
					url : '../monitor/list.shtml'
				}),
				reader : new Ext.data.JsonReader( {
					totalProperty : 'total',
					root : 'rows'
				}, [ {
					name : 'eventid'
				}, {
					name : 'userid'
				}, {
					name : 'account'
				}, {
					name : 'username'
				}, {
					name : 'activetime'
				}, {
					name : 'description'
				}, {
					name : 'requestpath'
				}, {
					name : 'methodname'
				}, {
					name : 'costtime'
				} ])
			});

			// 翻页排序时带上查询条件
			store.on('beforeload', function() {
				var ksrq = Ext.getCmp('ksrq').getValue();
				if (!Ext.isEmpty(ksrq)) {
					ksrq = ksrq.format('Y-m-d').toString();
				}
				var jsrq = Ext.getCmp('jsrq').getValue();
				if (!Ext.isEmpty(jsrq)) {
					jsrq = jsrq.format('Y-m-d').toString();
				}
				this.baseParams = {
					jsrq : jsrq,
					ksrq : ksrq,
					account : Ext.getCmp('account').getValue(),
					username : Ext.getCmp('username').getValue(),
					keyword : Ext.getCmp('keyword').getValue()
				};
			});

			var pagesize_combo = new Ext.form.ComboBox( {
				name : 'pagesize',
				hiddenName : 'pagesize',
				typeAhead : true,
				triggerAction : 'all',
				lazyRender : true,
				mode : 'local',
				store : new Ext.data.ArrayStore( {
					fields : [ 'value', 'text' ],
					data : [ [ 10, '10条/页' ], [ 20, '20条/页' ], [ 50, '50条/页' ],
							[ 100, '100条/页' ], [ 250, '250条/页' ],
							[ 500, '500条/页' ] ]
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
				store.reload( {
					params : {
						start : 0,
						limit : bbar.pageSize
					}
				});
			});

			var bbar = new Ext.PagingToolbar( {
				pageSize : number,
				store : store,
				displayInfo : true,
				displayMsg : '显示{0}条到{1}条,共{2}条',
				plugins : new Ext.ux.ProgressBarPager(), // 分页进度条
				// emptyMsg
				// :
				// "没有符合条件的记录",
				items : [ '-', '&nbsp;&nbsp;', pagesize_combo ]
			});

			var grid = new Ext.grid.GridPanel(
					{
						title : '<span class="commoncss">Rquest请求跟踪</span>',
						iconCls: 'server_connectIcon',
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
						autoExpandColumn : '_blank',
						cm : cm,
						sm : sm,
						plugins : expander,
						tbar : [
								{
									text : '删除',
									iconCls : 'page_delIcon',
									handler : function() {
										deleteEvents('del');
									}
								},
								'-',
								{
									text : '重置',
									iconCls : 'tbar_synchronizeIcon',
									handler : function() {
										deleteEvents('reset');
									}
								},
								'->',
								{
									id : 'ksrq',
									name : 'ksrq',
									xtype : 'datefield',
									emptyText : '开始日期',
									format : 'Y-m-d',
									width : 100
								},
								'-',
								{
									id : 'jsrq',
									name : 'jsrq',
									xtype : 'datefield',
									emptyText : '结束日期',
									format : 'Y-m-d',
									width : 100
								},
								'-',
								new Ext.form.TextField(
										{
											id : 'account',
											name : 'account',
											emptyText : '帐户',
											enableKeyEvents : true,
											listeners : {
												specialkey : function(field, e) {
													if (e.getKey() == Ext.EventObject.ENTER) {
														queryEvents();
													}
												}
											},
											width : 80
										}),
								'-',
								new Ext.form.TextField(
										{
											id : 'username',
											name : 'username',
											emptyText : '用户名',
											enableKeyEvents : true,
											listeners : {
												specialkey : function(field, e) {
													if (e.getKey() == Ext.EventObject.ENTER) {
														queryEvents();
													}
												}
											},
											width : 80
										}),
								'-',
								new Ext.form.TextField(
										{
											id : 'keyword',
											name : 'keyword',
											emptyText : '关键字过滤',
											enableKeyEvents : true,
											listeners : {
												specialkey : function(field, e) {
													if (e.getKey() == Ext.EventObject.ENTER) {
														queryEvents();
													}
												}
											},
											width : 120
										}), {
									text : '查询',
									iconCls : 'previewIcon',
									handler : function() {
										queryEvents();
									}
								},
								'-',
								{
									text : '刷新',
									iconCls : 'arrow_refreshIcon',
									handler : function() {
										store.reload();
									}
								} ],
						bbar : bbar
					});
			store.load( {
				params : {
					start : 0,
					limit : bbar.pageSize
				}
			});
			grid.on('sortchange', function() {
				//grid.getSelectionModel().selectFirstRow();
			});

			bbar.on("change", function() {
				//grid.getSelectionModel().selectFirstRow();
			});

			/**
			 * 布局
			 */
			var viewport = new Ext.Viewport( {
				layout : 'border',
				items : [ grid ]
			});

			/**
			 * 查询事件
			 */
			function queryEvents() {
				var ksrq = Ext.getCmp('ksrq').getValue();
				if (!Ext.isEmpty(ksrq)) {
					ksrq = ksrq.format('Y-m-d').toString();
				}
				var jsrq = Ext.getCmp('jsrq').getValue();
				if (!Ext.isEmpty(jsrq)) {
					jsrq = jsrq.format('Y-m-d').toString();
				}
				store.load( {
					params : {
						start : 0,
						limit : bbar.pageSize,
						account : Ext.getCmp('account').getValue(),
						username : Ext.getCmp('username').getValue(),
						keyword : Ext.getCmp('keyword').getValue(),
						jsrq : jsrq,
						ksrq : ksrq
					}
				});
			}

			/**
			 * 删除事件
			 */
			function deleteEvents(type) {
				var rows = grid.getSelectionModel().getSelections();
				if(type == 'del'){
					if (Ext.isEmpty(rows)) {
						Ext.Msg.alert('提示', '请先选中要删除的项目!');
						return;
					}
				}
				var strChecked = jsArray2JsString(rows, 'eventid');
				var msg = "确认删除选中的记录吗?";
				if(type == 'reset') msg = "确认重置所有的记录吗,记录信息将被清空?";
				Ext.Msg.confirm('请确认', msg, function(btn, text) {
					if (btn == 'yes') {
						showWaitMsg();
						Ext.Ajax.request( {
							url : '../monitor/delete.shtml',
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
								strChecked : strChecked,
								type : type
							}
						});
					}
				});
			}

		});