/**
 * 会话监控
 */
Ext.onReady(function() {
			var sm = new Ext.grid.CheckboxSelectionModel();
			var cm = new Ext.grid.ColumnModel([new Ext.grid.RowNumberer(), sm, {
						header : '用户编号',
						dataIndex : 'userid',
						width : 75,
						hidden:true,
						sortable : true
					}, {
						header : '会话创建时间',
						dataIndex : 'sessionCreatedTime',
						width : 140
					}, {
						header : '登录账户',
						dataIndex : 'account',
						width : 150
					}, {
						header : '姓名',
						dataIndex : 'username',
						width : 90
					}, {
						header : '客户端IP',
						dataIndex : 'loginIP',
						width : 100
					}, {
						header : '客户端浏览器',
						dataIndex : 'explorer',
						width : 120
					}, {
						header : '会话ID',
						dataIndex : 'sessionID',
						width : 250
					},{
						dataIndex : '_blank',
						id : '_blank'
					}]);

			var store = new Ext.data.Store({
						proxy : new Ext.data.HttpProxy({
									url : '../session/list.shtml'
								}),
						reader : new Ext.data.JsonReader({
									totalProperty : 'total',
									root : 'rows'
								}, [{
											name : 'sessionID'
										}, {
											name : 'userid'
										}, {
											name : 'username'
										}, {
											name : 'account'
										}, {
											name : 'loginIP'
										}, {
											name : 'explorer'
										}, {
											name : 'sessionCreatedTime'
										}])
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
									data : [[10, '10条/页'], [20, '20条/页'], [50, '50条/页'], [100, '100条/页'], [250, '250条/页'], [500, '500条/页'], [1000, '1000条/页']]
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
					})

			var grid = new Ext.grid.GridPanel({
						title : '<span class="commoncss">会话监控</span>',
						iconCls: 'user_commentIcon',
						height : 510,
						store : store,
						region : 'center',
						loadMask : {
							msg : '正在加载数据,请稍等...'
						},
						stripeRows : true,
						frame : true,
						autoExpandColumn : '_blank',
						cm : cm,
						sm : sm,
						tbar : [{
									text : '杀死会话',
									iconCls : 'deleteIcon',
									handler : function() {
										killSession();
									}
								}, '-', {
									text : '刷新',
									iconCls : 'arrow_refreshIcon',
									handler : function() {
										refreshSessionTable();
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
						//grid.getSelectionModel().selectFirstRow();
					});

			bbar.on("change", function() {
						//grid.getSelectionModel().selectFirstRow();
					});
			/**
			 * 布局
			 */
			var viewport = new Ext.Viewport({
						layout : 'border',
						items : [grid]
					});

			/**
			 * 杀死会话
			 */
			function killSession() {
				var rows = grid.getSelectionModel().getSelections();
				if (Ext.isEmpty(rows)) {
					Ext.Msg.alert('提示', '请先选中杀死的会话!');
					return;
				}
				var strChecked = jsArray2JsString(rows, 'sessionID');
				showWaitMsg('正在杀死会话,请等待...');
				Ext.Ajax.request({
							url : '../session/kill.shtml',
							success : function(response) {
								var resultArray = Ext.util.JSON.decode(response.responseText);
								refreshSessionTable();
								Ext.Msg.alert('提示', resultArray.msg);
							},
							failure : function(response) {
								var resultArray = Ext.util.JSON.decode(response.responseText);
								Ext.Msg.alert('提示', resultArray.msg);
							},
							params : {
								strChecked : strChecked
							}
						});
			}

			/**
			 * 刷新会话监控表格
			 */
			function refreshSessionTable() {
				store.load({
							params : {
								start : 0,
								limit : bbar.pageSize
							}
						});
			}
		});