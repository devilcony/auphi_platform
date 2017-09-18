var store_reload;//表格刷新方法

//分组
var compareTableGroupStore = new Ext.data.JsonStore({
    fields: ['profielTableGroupId', 'profielTableGroupName'],
    url : "../profileTableGroup/getTableGroupList.shtml",
    autoLoad:true,
    root : "",
    listeners:{
        load : function( store, records, successful, operation){
            if(successful){
                var rec = new (store.recordType)();
                rec.set('profielTableGroupId', '-1');
                rec.set('profielTableGroupName', '+添加新组');
                store.add(rec)
            }
        }
    }
});

var compareTableGroupComboBox = new Ext.form.ComboBox({
    fieldLabel : '所属组',
    emptyText:'请选择所属组',
    hiddenName : "compareTableGroup.profielTableGroupId",
    name:'compareTableGroup.profielTableGroupId',
    forceSelection: true,
    anchor : '100%',
    store: compareTableGroupStore,
    valueField : "profielTableGroupId",
    displayField : "profielTableGroupName",
    typeAhead: true,
    mode: 'local',
    triggerAction: 'all',
    editable:false,
    allowBlank : false,
    selectOnFocus : true,// 设置用户能不能自己输入,true为只能选择列表中有的记录
    resizable : true,
    width: 120,
    listeners: {
        select : function(compareTableGroupComboBox, record,index){
            var v = compareTableGroupComboBox.value;
            if(v == -1 || v == '-1'){
                compareTableGroupComboBox.clearValue();
                showGroupGridWindow(compareTableGroupComboBox);
            }
        }
    }
});

var compareName = {
    xtype:'textfield',
    id:'compareName',
    fieldLabel : '名称',
    name : 'compareName',

    allowBlank : false,
    anchor : '100%'
}

var compareDesc = {
    xtype:'textfield',
    id:'compareDesc',
    fieldLabel : '描述',
    name : 'compareDesc',

    allowBlank : false,
    anchor : '100%'
}


//是否主键下拉框
var compareTypeData = [
    [1,'单条数据对比'],
    [2,'多条数据对比']
];
var compareTypeStore = new Ext.data.SimpleStore(
    {
        fields:['value','text'],
        data:compareTypeData
    }
);
var compareTypeComboBox = new Ext.form.ComboBox({
    fieldLabel : '类型',
    emptyText : '请选择类型',
    hiddenName : 'compareType',
    forceSelection: true,
    anchor : '100%',
    store: compareTypeStore,
    valueField : "value",
    displayField : "text",
    typeAhead: true,
    mode: 'local',
    triggerAction: 'all',
    editable:false,
    allowBlank : false,
    selectOnFocus : true,// 设置用户能不能自己输入,true为只能选择列表中有的记录
    resizable : true,
    width: 120
});

compareTypeComboBox.setValue(1);

// 数据源
var databaseStore = new Ext.data.JsonStore({
    fields: ['sourceId', 'sourceName'],
    url : "../datasource/getDataSourceList.shtml",
    autoLoad:true,
    root : ""
});
// 数据源
var databaseId = new Ext.form.ComboBox({
    fieldLabel : '测试数据源',
    emptyText:'请选择数据源',
    hiddenName : "databaseId",
    forceSelection: true,
    anchor : '100%',
    store: databaseStore,
    valueField : "sourceId",
    displayField : "sourceName",
    typeAhead: true,
    mode: 'local',
    triggerAction: 'all',
    selectOnFocus : true,// 设置用户能不能自己输入,true为只能选择列表中有的记录
    allowBlank : false,
    resizable : true,
    editable:false,
    listeners: {
        select : function(formDatasourceComboBox, record,index){

        }
    }
});

var referenceDbId = new Ext.form.ComboBox({
    fieldLabel : '参照数据源',
    emptyText:'请选择数据源',
    hiddenName : "referenceDbId",
    forceSelection: true,
    anchor : '100%',
    store: databaseStore,
    valueField : "sourceId",
    displayField : "sourceName",
    typeAhead: true,
    mode: 'local',
    triggerAction: 'all',
    selectOnFocus : true,// 设置用户能不能自己输入,true为只能选择列表中有的记录
    allowBlank : false,
    resizable : true,
    editable:false,
    listeners: {
        select : function(formDatasourceComboBox, record,index){

        }
    }
});

var sql = {
    xtype:'textarea',
    id:'sql',
    fieldLabel : '测试SQL',
    name : 'sql',
    height: 85,
    allowBlank : false,
    anchor : '100%'
}
var referenceSql = {
    xtype:'textarea',
    id:'referenceSql',
    height: 85,
    fieldLabel : '参照SQL',
    name : 'referenceSql',
    allowBlank : false,
    anchor : '100%'
}

// 定义列模型
var compareSqlColumnSm = new Ext.grid.CheckboxSelectionModel();
var compareSqlColumnRownum = new Ext.grid.RowNumberer( {
    header : 'NO',
    width : 28
});


var sel_value = "";

var compareSqlColumnCm = new Ext.grid.ColumnModel( [compareSqlColumnSm, compareSqlColumnRownum,
    {
        header : '主键',
        dataIndex : 'compareSqlColumnId',
        hidden : true
    },{
        header : '列名',
        sortable : true,
        dataIndex : 'columnName'
    },{
        header : '参照列名',
        sortable : true,
        dataIndex : 'referenceColumnName'
    },{
        header : '字段类型',
        dataIndex : 'columnType'
    },{
        header : '列描述',
        dataIndex : 'columnDesc',
        editor:new Ext.form.TextField({
            allowBlank:false
        })
    },{
        header : '通过标准',
        dataIndex : 'compareStyle',
        editor: new Ext.form.ComboBox({
            store: new Ext.data.SimpleStore({
                    fields:['value','text'],
                    data: [
                        ['等于参照值','等于参照值'],
                        ['在参照值之间','在参照值之间']
                    ]
                }
            ),
            valueField : "value",
            displayField : "text",
            mode: 'local',
            triggerAction: 'all',
            forceSelection: true,
            typeAhead: true,
            editable:false,
            allowBlank : false,
            selectOnFocus : true,// 设置用户能不能自己输入,true为只能选择列表中有的记录
            resizable : true,
            listeners: {
                select: function (comboBox, record, index) {
                    sel_value = index;
                }
            }
        }),
        renderer: function (data, metadata, record, rowIndex, columnIndex, store) {
            var compareResult = store.getAt(rowIndex).get('compareStyle');
            return compareResult == 1 ? '在参照值之间' : '等于参照值'
        }

    },{
        header : '最小系数',
        dataIndex : 'minRatio',
        editor: {
            xtype:'numberfield'
        }
    },{
        header : '最大系数',
        dataIndex : 'maxRatio',
        editor:{
            xtype:'numberfield'
        }
    }]);

var compareSqlColumnStore = new Ext.data.Store( {
    proxy : new Ext.data.HttpProxy( {
        url : '../compareSqlColumn/getSqlColumn.shtml',
        timeout:1800000000
    }),
    reader : new Ext.data.JsonReader( {
        totalProperty : 'total',
        root : 'rows'
    }, [ {
        name : 'compareSqlColumnId'
    }, {
        name : 'columnName'
    },{
        name : 'columnType'
    },{
        name : 'referenceColumnName'
    }, {
        name : 'columnDesc'
    },{
        name : 'compareStyle'
    },{
        name : 'minRatio'
    },{
        name : 'maxRatio'
    }])

});

// 表格实例
var compareSqlColumnGrid =new Ext.grid.EditorGridPanel( {
    autoHeight:true,
    minHeight:50,
    clicksToEdit:1,
    id : 'compareSqlColumnGrid',
    autoScroll : true,
    region : 'center',
    store : compareSqlColumnStore,
    viewConfig : {
        forceFit : true
    },
    cm : compareSqlColumnCm,
    sm : compareSqlColumnSm,
    selModel: {
        selection: "rowmodel",
        mode: "MULTI"
    },
    loadMask : {
        msg : '正在加载表格数据,请稍等...'
    },
    listeners: {
        afteredit: function(val) {

            val.record.set("compareStyle", sel_value);
            if(val.field == "compareStyle" && sel_value == 0){
                val.record.set("minRatio", "");
                val.record.set("maxRatio", "");
            }else if(sel_value == 1){
                if(val.field == "minRatio"){
                    val.record.set("minRatio", val.value == ''? '0.9' :val.value );
                }else if(val.field == "maxRatio"){
                    val.record.set("maxRatio", val.value == ''? '1' :val.value );
                }else if(val.field == "compareStyle"){
                    val.record.set("minRatio", "0.9");
                    val.record.set("maxRatio", "1");
                }
            }
        },
        beforeedit:function(val){
            console.log(val);
            if((val.field == "minRatio"  && sel_value!=1) || (val.field == "maxRatio"  && sel_value!=1)){
                return false;
            }
        }
    }
});

var exeSQlButton =new Ext.Button({
    text:'获取字段',
    id:'exeSql',
    style: {
        marginLeft:'0px'//距左边宽度
    },

    handler:function(){
        if (!compareSqlFromPanel.getForm().isValid())
            return;
        compareSqlColumnStoreLoad();

    }
});

var compareSqlColumnStoreLoad = function(){
    compareSqlColumnStore.load( {
        params : {
            "databaseId":databaseId.value,
            "referenceDbId":referenceDbId.value,
            "sql":Ext.getCmp('sql').getValue(),
            "referenceSql":Ext.getCmp('referenceSql').getValue()
        },
        callback:function(records, options, success){
            if(!success){
                Ext.Msg.alert('提示',compareSqlColumnStore.reader.jsonData.msg);
            }
        }
    });
}




var compareSqlFromPanel = new Ext.form.FormPanel( {
    id : 'compareSqlFromPanel',
    autoScroll:true,
    collapsible : false,
    border : true,
    labelWidth : 80, // 标签宽度
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
            id : 'windowAction',
            name : 'windowAction',
            hidden : true
        },{
            id : 'compareSqlId',
            name : 'compareSqlId',
            hidden : true
        }]
    },{
        layout : 'column',
        border : false,
        anchor : '97%',
        items:[{
            columnWidth:.5,
            layout: 'form',
            border:false,
            items: [compareTableGroupComboBox]//分组
        },{
            columnWidth:.5,
            layout: 'form',
            border:false,
            items: [compareName]

        }]
    },{
        layout : 'column',
        border : false,
        anchor : '97%',
        items:[{
            columnWidth:.5,
            layout: 'form',
            border:false,
            items: [compareDesc]
        },{
            columnWidth:.5,
            layout: 'form',
            border:false,
            items: [compareTypeComboBox]
        }]
    },{
        xtype:'fieldset',
        title: 'SQL',
        autoHeight:true,
        anchor : '97%',
        collapsed: false,
        items:[{
            layout : 'column',
            border : false,
            anchor : '100%',
            items:[{
                columnWidth:.5,
                layout: 'form',
                border:false,
                items: [databaseId]//据数据源
            },{
                columnWidth:.5,
                layout: 'form',
                border:false,
                items: [referenceDbId]

            }]
        },{
            layout : 'column',
            border : false,
            anchor : '100%',
            items:[{
                columnWidth:.5,
                layout: 'form',
                border:false,
                items: [sql]//据数据源
            },{
                columnWidth:.5,
                layout: 'form',
                border:false,
                items: [referenceSql]

            }]
        }]
    },{
        xtype:'fieldset',
        title: '参照字段',
        autoHeight:true,
        anchor : '97%',
        collapsed: false,
        items:[{
            layout: 'form',
            border:false,
            items: [exeSQlButton]//据数据源
        },{
            layout: 'form',
            border:false,
            items: [{
                html:'<font color="#FF0000">提示:按住CTRL+鼠标左键可进行多选</font> '
            },compareSqlColumnGrid]

        }]
    }]
});


/**
 * 编辑窗口
 */
var compareSqlFromWindow = new Ext.Window({
    layout: 'fit', // 设置窗口布局模式
    width: 760, // 窗口宽度
    height: 530, // 窗口高度
    modal: true,
    resizable: false,// 是否可以改变大小，默认可以
    maskdisabled: true,
    closeAction: 'hide',
    closable: true, // 是否可关闭
    collapsible: true, // 是否可收缩
    border: false, // 边框线设置
    constrain: true, // 设置窗口是否可以溢出父容器
    animateTarget: Ext.getBody(),
    pageY: 20, // 页面定位Y坐标
    pageX: document.body.clientWidth / 2 - 900 / 2, // 页面定位X坐标
    items: [compareSqlFromPanel], // 嵌入的表单面板
    buttons: [{
        text: '保存',
        iconCls: 'acceptIcon',
        handler: function () {
            submitTheForm();
        }
    }, {
        text: '重置',
        id: 'btnReset',
        iconCls: 'tbar_synchronizeIcon',
        handler: function () {
            clearForm(compareSqlFromPanel.getForm());
        }
    }, {
        text: '关闭',
        iconCls: 'deleteIcon',
        handler: function () {
            compareSqlFromWindow.hide();
        }
    }]
});


function showCompareSqlFromWindow(reload){
    store_reload = reload;

    compareSqlFromWindow.show();

}



function submitTheForm(){
    if (!compareSqlFromPanel.getForm().isValid())
        return;

    var record = compareSqlColumnGrid.getSelectionModel().getSelections();
    if (Ext.isEmpty(record)) {
        Ext.Msg.alert('错误:', '请至少选择一条需要对比的字段');
        return;
    }

    var params = '{';

    for (var i = 0; i < record.length; i++) {

        var minRatio = record[i].get("minRatio");
        if(minRatio ==null || minRatio ==undefined || 'undefined' == minRatio || 'null' == minRatio){
            minRatio = '';
        }
        var maxRatio = record[i].get("maxRatio");
        if(maxRatio ==null || maxRatio ==undefined || 'undefined' == maxRatio || 'null' == maxRatio){
            maxRatio = '';
        }

        var compareStyle = record[i].get("compareStyle");
        if(compareStyle ==null || compareStyle ==undefined || 'undefined' == compareStyle || 'null' == compareStyle){
            compareStyle = '0';
        }


        params = params+ "'compareSqlColumns["+i+"].columnName' : '"+record[i].get("columnName")+"',";
        params = params+ "'compareSqlColumns["+i+"].referenceColumnName' : '"+record[i].get("referenceColumnName")+"',";
        params = params+ "'compareSqlColumns["+i+"].columnDesc' : '"+record[i].get("columnDesc")+"',";
        params = params+ "'compareSqlColumns["+i+"].columnType' : '"+record[i].get("columnType")+"',";
        params = params+ "'compareSqlColumns["+i+"].compareStyle' : '"+ compareStyle  +"',";
        params = params+ "'compareSqlColumns["+i+"].minRatio' : '"+ minRatio +"',";
        params = params+ "'compareSqlColumns["+i+"].maxRatio' : '"+ maxRatio +"',";

    }
    params = params.substring(0, params.length - 1)
    params = params+"}";

    compareSqlFromPanel.form.submit({
        url : '../compareSql/save.shtml',
        waitTitle : '提示',
        method : 'POST',
        waitMsg : '正在处理数据,请稍候...',
        params:Ext.util.JSON.decode(params),
        success : function(form, action) { // 回调函数有2个参数
            compareSqlFromWindow.hide();
            store_reload(false);
        },
        failure : function(form, action) {
            Ext.Msg.alert('提示', action.result.msg);
        }
    });



}