// 定义列模型
var g_sm = new Ext.grid.CheckboxSelectionModel({
    selModel: {
        selection: "rowmodel",
        mode: "MULTI"
    }
});
var g_rownum = new Ext.grid.RowNumberer( {
    header : 'NO',
    width : 28
});

var g_cm = new Ext.grid.ColumnModel( [g_sm, g_rownum,
    {
        header : '主键',
        dataIndex : 'profielTableGroupId',
        hidden : true
    },{
        header : '组名',
        sortable : true,
        dataIndex : 'profielTableGroupName',
    },{
        header : '描述',
        dataIndex : 'profielTableGroupDesc',
    } ]);

var g_store = new Ext.data.Store( {
    proxy : new Ext.data.HttpProxy( {
        url : "../profileTableGroup/getTableGroupList.shtml",
        timeout:60000
    }),
    reader : new Ext.data.JsonReader( {

    }, [ {
        name : 'profielTableGroupId'
    }, {
        name : 'profielTableGroupName'
    },{
        name : 'profielTableGroupDesc'
    }]),
    listeners: {
        datachanged: function() {
            //autoCheckGridHead(Ext.getCmp('id_grid_sfxm'));
        }
    }
});

var g_pagesize_combo = new Ext.form.ComboBox({
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
    width : 100
});

var g_number = parseInt(g_pagesize_combo.getValue());
// 改变每页显示条数reload数据
g_pagesize_combo.on("select", function(comboBox) {
    bbar.pageSize = parseInt(comboBox.getValue());
    g_number = parseInt(comboBox.getValue());
    g_store_reload();
});

var g_store_reload =  function(){

    g_store.load( {
        params : {
            start : 0,
            limit : g_pagesize_combo.value
        }
    });
}



// 表格工具栏
var g_tbar = new Ext.Toolbar({
    items : [{
        text : '新增',
        iconCls : 'page_addIcon',
        handler : function() {
            profileTableGroupFromPanel.getForm().reset();
            showProfileTableGroupFromWindow(g_store_reload);
        }
    },'-',{
        text : '编辑',
        iconCls : 'page_edit_1Icon',
        handler : function() {

            updateGroup();
        }
    },'-',{
        text : '删除',
        iconCls : 'page_delIcon',
        handler : function() {

            deleteItem();

        }
    }]
});

// 表格实例
var g_grid = new Ext.grid.GridPanel( {
    height : 500,
    autoScroll : true,

    frame : true,
    region : 'center',
    store : g_store,
    viewConfig : {
        forceFit : true
    },
    stripeRows : true,
    cm : g_cm,
    sm : g_sm,
    tbar : g_tbar,

    loadMask : {
        msg : '正在加载表格数据,请稍等...'
    }
});

var _formTableGroupComboBox ;

/**
 * 编辑窗口
 */
var groupGridWindow = new Ext.Window( {
    layout : 'fit', // 设置窗口布局模式
    width : 760, // 窗口宽度
    height : 530, // 窗口高度
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
    items : [g_grid], // 嵌入的表单面板
    buttons : [ {
        text : '关闭',
        iconCls : 'deleteIcon',
        handler : function() {
            groupGridWindow.hide();
        }
    } ],
    listeners:{
        hide:function(){
            _formTableGroupComboBox.store.load();
        }
    }
});

function updateGroup() {
    var record = g_grid.getSelectionModel().getSelected();

    if (Ext.isEmpty(record)) {
        Ext.Msg.alert('提示:', '请先选中要修改的数据');
        return;
    }
    profileTableGroupFromPanel.getForm().reset();
    profileTableGroupFromPanel.getForm().loadRecord(record);
    showProfileTableGroupFromWindow(g_store_reload);

}

/**
 * 删除
 *
 */
function deleteItem() {
    var record = g_grid.getSelectionModel().getSelections();
    if (Ext.isEmpty(record)) {
        Ext.Msg.alert('提示', '请先选中要删除的数据!');
        return;
    }
    var strChecked = jsArray2JsString(record, 'profielTableGroupId');
    Ext.Msg.confirm('请确认', '你真的要删除吗?', function(btn, text) {
        if (btn == 'yes') {
            showWaitMsg();
            Ext.Ajax.request( {
                url : '../profileTableGroup/delete.shtml',
                success : function(response) {
                    Ext.MessageBox.alert('提示', '删除成功');
                    g_store_reload();
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

function showGroupGridWindow(formTableGroupComboBox){
    groupGridWindow.show();
    _formTableGroupComboBox = formTableGroupComboBox;
    g_store_reload();
}