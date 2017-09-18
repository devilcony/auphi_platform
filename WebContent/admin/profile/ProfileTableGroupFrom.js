
var profileTableGroupFromPanel = new Ext.form.FormPanel( {
    id : 'profileTableGroupFromPanel',
    name : 'profileTableGroupFromPanel',
    autoScroll:true,
    collapsible : false,
    border : true,
    labelWidth : 60, // 标签宽度
    frame : true, // 是否渲染表单面板背景色
    labelAlign : 'right', // 标签对齐方式
    bodyStyle : 'padding:0 0 0 0', // 表单元素和表单面板的边距
    buttonAlign : 'center',
    items : [{
        columnWidth:.01,  //该列占用的宽度，标识为50％
        layout: 'form',
        defaultType : 'textfield',
        border:false,
        items: [{
            id : 'profileTableGroupFromPanelAction',
            name : 'profileTableGroupFromPanelAction',
            hidden : true
        },{
            id : 'profielTableGroupId',
            name : 'profielTableGroupId',
            hidden : true
        }]
    },{
        xtype:'fieldset',
        title: "组信息",
        autoHeight:true,
        anchor : '97%',
        collapsed: false,
        items:[{
            layout: 'form',
            border:false,
            items: [{
                layout: 'form',
                border:false,
                items: [{
                    id:'profielTableGroupName',
                    xtype:'textfield',
                    fieldLabel : '组名',
                    name : 'profielTableGroupName',
                    maxLength : 200,
                    allowBlank : false,
                    anchor : '100%'
                }]
            }]
        },{
            layout: 'form',
            border:false,
            items: [{
                layout: 'form',
                border:false,
                items: [{
                    id:'profielTableGroupDesc',
                    xtype:'textfield',
                    fieldLabel : '组描述',
                    name : 'profielTableGroupDesc',
                    allowBlank : false,
                    maxLength : 200,
                    anchor : '100%'
                }]
            }]
        }]
    }]
});


/**
 * 编辑窗口
 */
var profileTableGroupFromWindow = new Ext.Window( {
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
    pageY : 20, // 页面定位Y坐标
    pageX : document.body.clientWidth / 2 - 900 / 2, // 页面定位X坐标
    items : [profileTableGroupFromPanel], // 嵌入的表单面板
    buttons : [ {
        text : '保存',
        iconCls : 'acceptIcon',
        handler : function() {

            submitGroupFromForm();
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
            profileTableGroupFromWindow.hide();
        }
    } ]
});

var g_store_reload;

function showProfileTableGroupFromWindow(g_store_reload){
    profileTableGroupFromWindow.show();
}

function submitGroupFromForm() {


    if (!profileTableGroupFromPanel.getForm().isValid())
        return;


    profileTableGroupFromPanel.form.submit({
        url : '../profileTableGroup/save.shtml',
        waitTitle : '提示',
        method : 'POST',
        waitMsg : '正在处理数据,请稍候...',
        success : function(form, action) { // 回调函数有2个参数

            profileTableGroupFromWindow.hide();
            g_store_reload(false);


        },
        failure : function(form, action) {
            Ext.Msg.alert('提示', action.result.msg);
        }
    });

}