/**
 * JDBC执行监控
 * 
 */
Ext.onReady(function() {
			var expander = new Ext.grid.RowExpander({
						tpl : new Ext.Template('<p style=margin-left:70px;><span style=color:Teal;>执行SQL</span><br><span>{sqltext}</span></p>',
								'<p style=margin-left:70px;><span style=color:Teal;>开始执行时间</span><br><span>{starttime}</span></p>',
								'<p style=margin-left:70px;><span style=color:Teal;>执行耗时</span><br><span>{costtime}毫秒</span></p>'),
						// 屏蔽双击事件
						expandOnDblClick : true
					});
			var cm = new Ext.grid.ColumnModel([new Ext.grid.RowNumberer({
								width : 28
							}), expander,{
								header : '激活时间',
								dataIndex : 'starttime',
								width : 140,
								sortable : true
							},{
						header : '执行SQL',
						dataIndex : 'sqltext',
						id : 'sqltext',
						width : 100
					}, {
						header : '执行耗时',
						dataIndex : 'costtime',
						width : 60,
						sortable : true
					}, {
						header : '影响行数',
						dataIndex : 'rowcount',
						width : 60,
						sortable : true
					}]);

			/**
			 * 数据存储
			 */
			var store = new Ext.data.Store({
						proxy : new Ext.data.HttpProxy({
									url : 'jdbcMonitor.ered?reqCode=queryMonitorData'
								}),
						reader : new Ext.data.JsonReader({
									totalProperty : 'TOTALCOUNT',
									root : 'ROOT'
								}, [{
											name : 'monitorid'
										}, {
											name : 'sqltext'
										}, {
											name : 'starttime'
										}, {
											name : 'costtime'
										}, {
											name : 'rowcount'
										}, {
											name : 'type'
										}])
					});

			// 翻页排序时带上查询条件
			store.on('beforeload', function() {
						var starttime = Ext.getCmp('starttime').getValue();
						if (!Ext.isEmpty(starttime)) {
							starttime = starttime.format('Y-m-d').toString();
						}
						this.baseParams = {
							type : Ext.getCmp('type_id').getValue(),
							costtime : Ext.getCmp('costtime').getValue(),
							rowcount : Ext.getCmp('rowcount').getValue(),
							sql : Ext.getCmp('sql').getValue(),
							starttime : starttime
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
									data : [[10, '10条/页'], [20, '20条/页'], [50, '50条/页'], [100, '100条/页'], [250, '250条/页'], [500, '500条/页']]
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
						// emptyMsg
						// :
						// "没有符合条件的记录",
						items : ['-', '&nbsp;&nbsp;', pagesize_combo]
					});

			var grid = new Ext.grid.GridPanel({
						title : '<span class="commoncss">JDBC执行监控</span>',
						iconCls: 'database_refreshIcon',
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
						autoExpandColumn : 'sqltext',
						cm : cm,
						plugins : expander,
						tbar : [{
									text : '重置',
									iconCls : 'tbar_synchronizeIcon',
									handler : function() {
										if (runMode == '0') {
											Ext.Msg.alert('提示', '系统正处于演示模式下运行,您的操作被取消!该模式下只能进行查询操作!');
											return;
										}
										reset();
									}
								}, '->', new Ext.form.ComboBox({
											id : 'type_id',
											hiddenName : 'type',
											triggerAction : 'all',
											store : new Ext.data.SimpleStore({
														fields : ['value', 'text'],
														data : [[1, 'INSERT'], [2, 'UPDATE'], [3, 'DELETE'], [4, 'SELECT'], [5, 'CALLPRC']]
													}),
											displayField : 'text',
											valueField : 'value',
											mode : 'local',
											listWidth : 120, // 下拉列表的宽度,默认为下拉选择框的宽度
											forceSelection : true,
											typeAhead : true,
											emptyText : '操作类型',
											// editable : false,
											resizable : true,
											width : 100
										}), '-', {
									id : 'starttime',
									name : 'starttime',
									xtype : 'datefield',
									emptyText : '执行时间大于',
									format : 'Y-m-d',
									width : 100
								}, '-', {
									id : 'costtime',
									name : 'costtime',
									xtype : 'numberfield',
									emptyText : '执行耗时大于',
									enableKeyEvents : true,
									listeners : {
										specialkey : function(field, e) {
											if (e.getKey() == Ext.EventObject.ENTER) {
												query();
											}
										}
									},
									width : 100
								}, '-', {
									id : 'rowcount',
									name : 'rowcount',
									xtype : 'numberfield',
									emptyText : '影响行数大于',
									enableKeyEvents : true,
									listeners : {
										specialkey : function(field, e) {
											if (e.getKey() == Ext.EventObject.ENTER) {
												query();
											}
										}
									},
									width : 100
								}, '-', {
									id : 'sql',
									name : 'sql',
									xtype : 'textfield',
									emptyText : 'SQL关键字',
									enableKeyEvents : true,
									listeners : {
										specialkey : function(field, e) {
											if (e.getKey() == Ext.EventObject.ENTER) {
												query();
											}
										}
									},
									width : 100
								}, '-', {
									text : '查询',
									iconCls : 'previewIcon',
									handler : function() {
										query();
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
			grid.on('sortchange', function() {
						grid.getSelectionModel().selectFirstRow();
					});

			bbar.on("change", function() {
						grid.getSelectionModel().selectFirstRow();
					});

			/**
			 * 布局
			 */
			var viewport = new Ext.Viewport({
						layout : 'border',
						items : [grid]
					});

			/**
			 * 查询
			 */
			function query() {
				var starttime = Ext.getCmp('starttime').getValue();
				if (!Ext.isEmpty(starttime)) {
					starttime = starttime.format('Y-m-d').toString();
				}
				store.load({
							params : {
								start : 0,
								limit : bbar.pageSize,
								type : Ext.getCmp('type_id').getValue(),
								costtime : Ext.getCmp('costtime').getValue(),
								rowcount : Ext.getCmp('rowcount').getValue(),
								sql : Ext.getCmp('sql').getValue(),
								starttime : starttime
							}
						});
			}

			/**
			 * 重置
			 */
			function reset() {
				Ext.Msg.confirm('请确认', '确认要重置JDBC监控记录吗?', function(btn, text) {
							if (btn == 'yes') {
								showWaitMsg();
								Ext.Ajax.request({
											url : 'jdbcMonitor.ered?reqCode=resetMonitorData',
											success : function(response) {
												var resultArray = Ext.util.JSON.decode(response.responseText);
												store.reload();
												Ext.Msg.alert('提示', resultArray.msg);
											},
											failure : function(response) {
												Ext.Msg.alert('提示', '请求失败!');
											}
										});
							}
						});
			}

			if (runMode == '0') {
				Ext.Msg.alert('提示', '为保障演示系统高速运行,监控功能已关闭.您看到的数据是历史监控数据!');
			}

		});