
Ext.onReady(function() {	
	
	var addParamFormPanel = new Ext.form.FormPanel({
		id : 'addParamFormPanel',
		name : 'addParamFormPanel',
		defaultType : 'textfield',
		labelAlign : 'right',
		labelWidth : 100,
		frame : false,
		bodyStyle : 'padding:5 5 0',
		items : [{
					fieldLabel : '数据库名称',
					name : 'databaseName',
					id : 'databaseName',
					allowBlank : false,
					anchor : '60%'
				},{
					fieldLabel : '主机IP',
					name : 'hostIP',
					id : 'hostIP',
					allowBlank : false,
					regex: /^\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}$/,
					regexText: '请输入正确IP地址，例：127.0.0.1',
					anchor : '60%'
				},{
					fieldLabel : '端口',
					name : 'port',
					id : 'port',
					allowBlank : false,
					anchor : '60%'
				},{
					fieldLabel : '用户名',
					name : 'userName',
					id : 'userName',
					allowBlank : false,
					anchor : '60%'
				}, {
					fieldLabel : '密码',
					name : 'password',
					id : 'password',
					allowBlank : false,
					anchor : '60%'
				},{
					id : 'windowmode',
					name : 'windowmode',
					hidden : true
				}]
	});

	var addParamWindow = new Ext.Window({
		layout : 'fit',
		width : 500,
		height : 250,
		resizable : false,
		draggable : true,
		closeAction : 'hide',
		title : '<span class="commoncss">配置数据集市参数</span>',
		modal : false,
		collapsible : true,
		titleCollapse : true,
		maximizable : false,
		buttonAlign : 'right',
		border : false,
		animCollapse : false,
		pageY : 20,
		pageX : document.body.clientWidth / 2 - 420 / 2,
		constrain : true,
		items : [addParamFormPanel],
		listeners:{  
           "show":function(){
				Ext.Ajax.request({
			        url:'../oracleDatasource/getDbList.shtml',
			        success:function(response){
			           var data=Ext.decode(response.responseText); 
			           Ext.getCmp('databaseName').setValue(data[0].ssid);
			           Ext.getCmp('hostIP').setValue(data[0].ip);
			           Ext.getCmp('port').setValue(data[0].port);
			           Ext.getCmp('userName').setValue(data[0].userName);
			           Ext.getCmp('password').setValue(data[0].password);
			        },
			        failure:function(){Ext.Msg.alert("错误", "与后台联系的时候出现了问题");}

				});	
		   }  
        },  
		buttons : [{
			text : '保存',
			iconCls : 'acceptIcon',
			handler : function() {
				saveParamItem();
			}
		},{
			text : '重置',
			id : 'btnReset',
			iconCls : 'tbar_synchronizeIcon',
			handler : function() {
				clearForm(addParamFormPanel.getForm());
				Ext.getCmp('databaseName').setValue("");
				Ext.getCmp('databaseName').setValue("");
		        Ext.getCmp('hostIP').setValue("");
		        Ext.getCmp('port').setValue("");
		        Ext.getCmp('userName').setValue("");
		        Ext.getCmp('password').setValue("");
			}
		}, {
			text : '关闭',
			iconCls : 'deleteIcon',
			handler : function() {
				addParamWindow.hide();
			}
		}]
	}).show();

		
	/**
	 * 布局
	 */
	var viewport = new Ext.Viewport({
		 layout : 'border',
		 items : [addParamWindow]
	});
	
	/**
	 * 保存参数数据
	 */
	function saveParamItem() {
		 
		addParamFormPanel.form.submit({
					url : '../oracleDatasource/saveSource.shtml',
					waitTitle : '提示',
					method : 'POST',
					waitMsg : '正在处理数据,请稍候...',
					success : function(form, action) {
						Ext.MessageBox.alert('提示信息', action.result.msg);
						//addParamWindow.hide();
						store.reload();
						form.reset();
					},
					failure : function(form, action) {
						var msg = action.result.msg;
						Ext.MessageBox.alert('提示', '数据保存失败:<br>' + msg);
					}
				});
	}

	
	
});