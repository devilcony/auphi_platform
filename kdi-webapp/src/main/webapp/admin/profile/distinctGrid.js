var columnId = '';


var  distinctGridPageSizeCombo = new Ext.form.ComboBox({
    name : 'pagesize',
    hiddenName : 'pagesize',
    typeAhead : true,
    triggerAction : 'all',
    lazyRender : true,
    mode : 'local',
    store : new Ext.data.ArrayStore({
        fields : ['value', 'text'],
        data : [[10, '10条'],
            [50, '50条/页'], [100, '100条'],
            [250, '250条'], [500, '500条'],[1000, '1000条']]
    }),
    valueField : 'value',
    displayField : 'text',
    value : '50',
    editable : false,
    width : 100,
    listeners: {
        select : function(distinctGridPageSizeCombo, record,index){
            loadGridStore();
        }
    }
});
var  distinctGridOrderByCombo = new Ext.form.ComboBox({
    name : 'dir',
    hiddenName : 'dir',
    typeAhead : true,
    triggerAction : 'all',
    lazyRender : true,
    mode : 'local',
    store : new Ext.data.ArrayStore({
        fields : ['value', 'text'],
        data : [['ASC', '顺序'],['DESC','倒序']]
    }),
    valueField : 'value',
    displayField : 'text',
    value : 'DESC',
    editable : false,
    width : 100,
    listeners: {
        select : function(distinctGridOrderByCombo, record,index){
            loadGridStore();
        }
    }
});


var tbar = new Ext.Toolbar({
    items : [{
        text : '新增',
        iconCls : 'page_addIcon',
        id : 'id_tbi_add',
        handler : function() {
        }
    }]
});

// 分页工具栏
var distinctGridBbar = new Ext.Toolbar( {
    id:'distinctGridBbar',
    items : ['限制条数:','-',distinctGridPageSizeCombo,'-','排序:',distinctGridOrderByCombo ]
});

;



// 定义列模型
var compareSqlColumnSm = new Ext.grid.CheckboxSelectionModel();
var compareSqlColumnRownum = new Ext.grid.RowNumberer( {
    header : 'NO',
    width : 28
});


var compareSqlColumnCm = new Ext.grid.ColumnModel( [ compareSqlColumnRownum,
    {
        header : '列值',
        dataIndex : 'columnName'
    },{
        header : '行数',
        sortable : true,
        dataIndex : 'columnCount'
    }]);

var compareSqlColumnStore = new Ext.data.Store( {
    proxy : new Ext.data.HttpProxy( {
        url : '../profileTableColumn/listDistinct.shtml',
        timeout:1800000000
    }),
    reader : new Ext.data.JsonReader( {
        totalProperty : 'total',
        root : 'rows'
    }, [ {
        name : 'columnName'
    }, {
        name : 'columnCount'
    }])

});

// 表格实例
var compareSqlColumnGrid =new Ext.grid.EditorGridPanel( {
    height : 150,
    id : 'compareSqlColumnGrid',
    autoScroll : true,
    region : 'center',
    store : compareSqlColumnStore,
    viewConfig : {
        forceFit : true
    },
    cm : compareSqlColumnCm,
    sm : compareSqlColumnSm,
    bbar:distinctGridBbar,
    selModel: {
        selection: "rowmodel",
        mode: "MULTI"
    },
    loadMask : {
        msg : '正在加载表格数据,请稍等...'
    }
});

/**
 * 编辑窗口
 */
var distinctGridWindow = new Ext.Window( {
    layout : 'fit', // 设置窗口布局模式
    width : 400, // 窗口宽度
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
    items : [compareSqlColumnGrid], // 嵌入的表单面板
    buttons : [{
        text : '关闭',
        iconCls : 'deleteIcon',
        handler : function() {
            distinctGridWindow.hide();
        }
    } ]
});

function showGridWindow(cid){
    columnId = cid
    distinctGridPageSizeCombo.setValue(50);
    loadGridStore();
    distinctGridWindow.show();
}

function loadGridStore(){
    compareSqlColumnStore.load( {
        params : {
            limit :distinctGridPageSizeCombo.value,
            dir :distinctGridOrderByCombo.value,
            profileTableColumnId:columnId
        }
    });
}

