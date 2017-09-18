/**
 * 登陆页面
 */
Ext.onReady(function() {
	var panel = new Ext.Panel({
		el : 'hello-tabs',
		autoTabs : true,
		deferredRender : false,
		border : false,
		items : {
			xtype : 'tabpanel',
			id : 'loginTabs',
			activeTab : 0,
			height : 180,
			border : false,
			items : [{
				title : "身份认证",
				xtype : 'form',
				id : 'loginForm',
				defaults : {
					width : 260
				},
				bodyStyle : 'padding:20 0 0 50',
				defaultType : 'textfield',
				labelWidth : 40,
				labelSeparator : '：',
				items : [{
							fieldLabel : '帐&nbsp;号',
							name : 'account',
							id : 'account',
							cls : 'user',
							blankText : '帐号不能为空,请输入!',
							maxLength : 30,
							maxLengthText : '账号的最大长度为30个字符',
							allowBlank : false,
							listeners : {
								specialkey : function(field, e) {
									if (e.getKey() == Ext.EventObject.ENTER) {
										Ext.getCmp('password').focus();
									}
								}
							}
						}, {
							fieldLabel : '密&nbsp;码',
							name : 'password',
							id : 'password',
							cls : 'key',
							inputType : 'password',
							blankText : '密码不能为空,请输入!',
							maxLength : 20,
							maxLengthText : '密码的最大长度为20个字符',
							allowBlank : false,
							listeners : {
								specialkey : function(field, e) {
									if (e.getKey() == Ext.EventObject.ENTER) {
										login();
									}
								}
							}
						}]
			}, {
				title : '信息公告',
				contentEl : 'infoDiv',
				defaults : {
					width : 230
				}
			}, {
				title : '关于',
				contentEl : 'aboutDiv',
				defaults : {
					width : 230
				}
			}]
		}
	});

	// 清除按钮上下文菜单
	var mainMenu = new Ext.menu.Menu({
				id : 'mainMenu',
				items : [{
					text : '清除记忆',
					iconCls : 'status_awayIcon',
					handler : function() {
						clearCookie('eredg4.login.account');
						var account = Ext.getCmp('loginForm')
								.findById('account');
						Ext.getCmp('loginForm').form.reset();
						account.setValue('');
						account.focus();
					}
				}, {
					text : '切换到全屏模式',
					iconCls : 'imageIcon',
					handler : function() {
						window.location.href = '../fullScreen.htm';
					}
				}]
			});

	var win = new Ext.Window({
		title : "汕头移动数据枢纽系统",
		renderTo : Ext.getBody(),
		layout : 'fit',
		width : 460,
		height : 300,
		closeAction : 'hide',
		plain : true,
		modal : true,
		collapsible : true,
		titleCollapse : true,
		maximizable : false,
		draggable : false,
		closable : false,
		resizable : false,
		animateTarget : document.body,
		items : panel,
		buttons : [{
			text : '&nbsp;登录',
			iconCls : 'acceptIcon',
			handler : function() {
				if (Ext.isIE) {
					if (!Ext.isIE8) {
						Ext.MessageBox
								.alert(
										'温馨提示',
										'系统检测到您正在使用基于MSIE内核的浏览器<br>我们强烈建议立即切换到<b><a href="http://firefox.com.cn/" target="_blank">FireFox</a></b>或者<b><a href="http://www.google.com/chrome/?hl=zh-CN" target="_blank">GoogleChrome</a></b>浏览器体验飞一般的感觉!'
												+ '<br>如果您还是坚持使用IE,那么请使用基于IE8内核的浏览器登录!')
						return;
					}
					login();
				} else {
					login();
				}
			}
		}, {
			text : '&nbsp;选项',
			iconCls : 'tbar_synchronizeIcon',
			menu : mainMenu
		}]
	});

	win.show();

	win.on('show', function() {
		setTimeout(function() {
					var account = Ext.getCmp('loginForm').findById('account');
					var password = Ext.getCmp('loginForm').findById('password');
					var c_account = getCookie('eredg4.login.account');
					account.setValue(c_account);
					if (Ext.isEmpty(c_account)) {
						account.focus();
					} else {
						password.focus();
					}
				}, 200);
	}, this);




	/**
	 * 提交登陆请求
	 */
	function login() {
		if (Ext.getCmp('loginForm').form.isValid()) {
			Ext.getCmp('loginForm').form.submit({
				url : '../login/login.shtml',
				waitTitle : '提示',
				method : 'POST',
				waitMsg : '正在验证您的身份,请稍候.....',
				success : function(form, action) {
					var account = Ext.getCmp('loginForm').findById('account');
					setCookie("skyform.login.account", account.getValue(), 240);
					setCookie("skyform.login.userid", action.result.userid, 240);
					setCookie("skyform.lockflag", '0', 240);
					window.location.href = '../index/index.shtml';
				},
				failure : function(form, action) {
					var errmsg = action.result.msg;
					var errtype = action.result.errorType;
					Ext.Msg.alert('提示', errmsg, function() {
								var account = Ext.getCmp('loginForm').findById('account');
								var password = Ext.getCmp('loginForm').findById('password');
								if (errtype == '1') {
									Ext.getCmp('loginForm').form.reset();
									account.focus();
									account.validate();
								} else if (errtype == '2') {
									password.focus();
									password.setValue('');
								} else if (errtype == '3') {
									account.focus();
								}
							});
				}
			});
		}
	}

});
