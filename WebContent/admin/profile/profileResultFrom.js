var profileResultGridStore;


// 数据源
var formDatasourceStore = new Ext.data.JsonStore({
    fields: ['sourceId', 'sourceName'],
    url : "../datasource/getDataSourceList.shtml",
    autoLoad:true,
    root : ""
});
// 数据源
var formDatasourceComboBox = new Ext.form.ComboBox({
    fieldLabel : '数据源',
    emptyText:'请选择数据源',
    hiddenName : "databaseId",
    forceSelection: true,
    anchor : '100%',
    store: formDatasourceStore,
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
            formSchemaNameComboBox.clearValue();
            formTableNameComboBox.clearValue();
            var id_database = formDatasourceComboBox.value;
            formSchemaNameStore.load({
                params : {id_database : id_database},
                callback:function(r,options,success){
                    formTableNameStore.load({
                        params : {
                            id_database : id_database,
                            schema_name : ""
                        }
                    });
                }
            });

        }
    }
});

//模式名
var formSchemaNameStore = new Ext.data.JsonStore({
    fields: ['value', 'text'],
    url : "../mdmTable/getSchemaName.shtml",
    autoLoad:true,
    root : "",
    listeners:{
        load : function( store, records, successful, operation){

        }
    }
});


//模式名
var formSchemaNameComboBox = new Ext.form.ComboBox({
    fieldLabel : '模式名',
    emptyText:'请选择模式名',
    hiddenName : "schemaName",
    forceSelection: false,
    anchor : '100%',
    store: formSchemaNameStore,
    valueField : "value",
    displayField : "text",
    typeAhead: true,
    mode: 'local',
    triggerAction: 'all',
    selectOnFocus : true,// 设置用户能不能自己输入,true为只能选择列表中有的记录
    resizable : true,
    editable:false,
    listeners: {
        select : function(formSchemaNameComboBox, record,index){
            loadTableNameStore();

        }
    }
});

function loadTableNameStore(){

    var id_database = formDatasourceComboBox.value;
    var schemaName = formSchemaNameComboBox.value;
    if(id_database !=null && id_database !=''){
        formTableNameStore.load({
            params : {
                id_database : id_database,
                schema_name : schemaName
            }
        });
    }
}

//表名
var formTableNameStore = new Ext.data.JsonStore({
    fields: ['value', 'text'],
    url : "../mdmTable/getTableName.shtml",
    autoLoad:true,
    root : ""
});

var formTableNameComboBox = new Ext.form.ComboBox({
    id : 'formProfileTable',
    fieldLabel : '表名',
    emptyText:'请选择表名',
    hiddenName : "tableName",
    anchor : '100%',
    store: formTableNameStore,
    valueField : "value",
    displayField : "text",
    typeAhead: true,
    mode: 'local',
    triggerAction: 'all',
    selectOnFocus : true,// 设置用户能不能自己输入,true为只能选择列表中有的记录
    allowBlank : false,
    resizable : true,
    editable:false,
    listeners: {
        select : function(formTableNameComboBox, record,index){
            loadTableColumnStore();

        }
    }
});


function loadTableColumnStore(){
    var databaseId = formDatasourceComboBox.value;
    var schemaName = formSchemaNameComboBox.value;
    var tableName = formTableNameComboBox.value;

    if(databaseId!=null && databaseId !='' && tableName!=null && tableName!=''){
        fromTableColumnStore.load( {
            params : {
                "tableNameTag":1,
                "databaseId":databaseId,
                "schemaName":schemaName,
                "tableName":tableName
            }
        });
    }

}

//分组
var formTableGroupStore = new Ext.data.JsonStore({
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

var formTableGroupComboBox = new Ext.form.ComboBox({
    fieldLabel : '所属组',
    emptyText:'请选择所属组',
    hiddenName : "profielTableGroup.profielTableGroupId",
    forceSelection: true,
    anchor : '100%',
    store: formTableGroupStore,
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
        select : function(profileTableGroupComboBox, record,index){
            var v = profileTableGroupComboBox.value;
            if(v == -1 || v == '-1'){

                profileTableGroupComboBox.clearValue();
                showGroupGridWindow();
            }else{
                profileResultFromTableStore.load({
                    params : {
                        'profielTableGroup.profielTableGroupId' : v
                    }
                });
            }


        }
    }
});



// 定义列模型
var profileTableColumnSm = new Ext.grid.CheckboxSelectionModel();
var profileTableColumnRownum = new Ext.grid.RowNumberer( {
    header : 'NO',
    width : 28
});


var profileTableColumnCm = new Ext.grid.ColumnModel( [profileTableColumnSm, profileTableColumnRownum,
    {
        header : '主键',
        dataIndex : 'ID_PROFILE_TABLE_RESULT',
        hidden : true
    },{
        header : '列名',
        sortable : true,
        dataIndex : 'columnName'
    },{
        header : '类型',
        width : 50,
        dataIndex : 'typeName'
    },{
        header : '长度',
        width : 50,
        dataIndex : 'columnSize'
    },{
        header : '描述',
        dataIndex : 'remarks',
        editor:new Ext.form.TextField({
            allowBlank:false
        })
    }]);




var fromTableColumnStore = new Ext.data.Store( {
    proxy : new Ext.data.HttpProxy( {
        url : '../profileTableColumn/getTableColumnList.shtml',
        timeout:1800000000
    }),
    reader : new Ext.data.JsonReader( {
        totalProperty : 'total',
        root : 'rows'
    }, [ {
        name : 'columnName'
    }, {
        name : 'typeName'
    },{
        name : 'columnSize'
    }, {
        name : 'remarks'
    }, {
        name : 'decimalDigits'
    }]),
    listeners: {
        datachanged: function() {
            //autoCheckGridHead(Ext.getCmp('id_grid_sfxm'));
        }
    }
});

// 表格实例
var formTableColumnGrid = new Ext.grid.EditorGridPanel( {
    autoHeight:true,
    id : 'profileTableColumnGrid',
    autoScroll : true,
    region : 'center',
    store : fromTableColumnStore,
    viewConfig : {
        forceFit : true
    },
    cm : profileTableColumnCm,
    sm : profileTableColumnSm,
    selModel: {
        selection: "rowmodel",
        mode: "MULTI"
    },
    loadMask : {
        msg : '正在加载表格数据,请稍等...'
    }
});


// 定义列模型
var profileTableSm = new Ext.grid.CheckboxSelectionModel();
var profileTableRownum = new Ext.grid.RowNumberer( {
    header : 'NO',
    width : 28
});


var profileTableCm = new Ext.grid.ColumnModel( [ profileTableRownum,
    {
        header : '主键',
        dataIndex : 'profileTableId',
        hidden : true
    },{
        header : '查询表/SQL',
        sortable : true,
        dataIndex : 'tableName'
    },{
        header : '描述',
        dataIndex : 'profielName'

    },{
        header : '创建时间',
        width : 50,
        dataIndex : 'createTime'
    }]);

var profileResultFromTableStore = new Ext.data.Store( {
    proxy : new Ext.data.HttpProxy( {
        url : '../profileTable/getProfileTableList.shtml',
        timeout:1800000000
    }),
    reader : new Ext.data.JsonReader( {

    }, [ {
        name : 'profileTableId'
    }, {
        name : 'tableName'
    },{
        name : 'profielName'
    }, {
        name : 'createTime'
    }])

});

// 表格实例
var profileResultFromTableGrid = new Ext.grid.GridPanel( {
    autoHeight:true,
    id : 'profileTableGrid',
    autoScroll : true,
    region : 'center',
    store : profileResultFromTableStore,
    viewConfig : {
        forceFit : true
    },
    cm : profileTableCm,
    sm : profileTableSm,
    selModel: {
        selection: "rowmodel",
        mode: "MULTI"
    },
    loadMask : {
        msg : '正在加载表格数据,请稍等...'
    }
});





var profileResultFromPanel = new Ext.form.FormPanel( {
    id : 'profileResultFromPanel',
    name : 'profileResultFromPanel',
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
            id : 'windowAction',
            name : 'windowAction',
            hidden : true
        },{
            id : 'profileTableId',
            name : 'profileTableId',
            hidden : true
        }]
    },{
        xtype:'fieldset',
        title: '组信息',
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
                items: [formTableGroupComboBox]//据数据源
            },{
                columnWidth:.5,
                layout: 'form',
                border:false,
                items: [{
                    layout: 'form',
                    border:false,
                    items: [{
                        //描述
                        xtype:'textfield',
                        id:'profielName',
                        fieldLabel : '描述',
                        name : 'profielName',
                        maxLength : 200,
                        allowBlank : false,
                        anchor : '100%'
                    }]
                }]//据数据源

            }]
        },profileResultFromTableGrid]
    },{
        xtype:'fieldset',
        title: '查询表信息',
        autoHeight:true,
        anchor : '97%',
        collapsed: false,
        items:[{
            xtype:'radiogroup',
            id:'tableMode',
            width:180,
            items: [
                { boxLabel: '表', name: 'tableNameTag', inputValue: '1', checked: true},
                { boxLabel: 'SQL', name: 'tableNameTag', inputValue: '2'}
            ],
            listeners:{
                change: function(g , newValue , oldValue){
                    if(newValue.inputValue == 1){
                        formSchemaNameComboBox.setDisabled(false);
                        formTableNameComboBox.setDisabled(false);
                        Ext.getCmp("formCondition").setDisabled(false);

                        Ext.getCmp("sql").setDisabled(true);
                        Ext.getCmp("exeSql").setDisabled(true);
                    }else{
                        formSchemaNameComboBox.setDisabled(true);
                        formTableNameComboBox.setDisabled(true);
                        Ext.getCmp("formCondition").setDisabled(true);

                        Ext.getCmp("sql").setDisabled(false);
                        Ext.getCmp("exeSql").setDisabled(false);
                    }


                }
            }
        },{
            layout : 'column',
            border : false,
            anchor : '100%',
            items:[{
                columnWidth:.5,
                layout: 'form',
                border:false,
                items: [formDatasourceComboBox]//据数据源
            },{
                columnWidth:.5,
                layout: 'form',
                border:false,
                items: [{
                    layout: 'form',
                    border:false,
                    items: [formSchemaNameComboBox]
                }]//据数据源

            }]
        },{
            layout : 'column',
            border : false,
            anchor : '100%',
            items:[{
                columnWidth:.5,
                layout: 'form',
                border:false,
                items: [formTableNameComboBox]//据数据源
            },{
                columnWidth:.5,
                layout: 'form',
                border:false,
                items: [{
                    layout: 'form',
                    border:false,
                    items: [{
                        xtype:'textfield',
                        id:'formCondition',
                        fieldLabel : '条件',
                        name : 'condition',
                        maxLength : 200,
                        anchor : '100%'
                    }]
                }]//据数据源

            }]
        },{
            layout : 'column',
            border : false,
            anchor : '100%',
            items:[{
                columnWidth:.85,
                layout: 'form',
                border:false,
                items: [{
                    xtype:'textarea',
                    id:'sql',
                    fieldLabel : 'SQL',
                    name : 'sql',
                    maxLength : 200,
                    allowBlank : false,
                    anchor : '100%'
                }]//据数据源
            },{
                columnWidth:.10,
                layout: 'form',
                align:'center',
                border:false,
                items: [new Ext.Button({
                    text:'获取字段',
                    id:'exeSql',
                    style: {
                        marginLeft:'5px',//距左边宽度
                        marginTop:'35px'
                    },

                    handler:function(){

                        if(formDatasourceComboBox.value == '' || formDatasourceComboBox.value == null){

                            Ext.Msg.alert('提示', ' 请选择数据源');
                            return false;
                        }
                        //获取sql所有的Column
                        fromTableColumnStore.load( {
                            params : {
                                "tableNameTag":2,
                                "databaseId":formDatasourceComboBox.value,
                                "tableName":Ext.getCmp("sql").getValue()
                            }
                        });
                    }
                })]//据数据源
            }]
        }]
    },{
        xtype:'fieldset',
        title: '字段选择信息',
        autoHeight:true,
        anchor : '97%',
        collapsed: false,
        items:[{
            layout: 'form',
            border: false,
            items: [{
                html:'<font color="#FF0000">提示:按住CTRL+鼠标左键可进行多选</font> '
            }, formTableColumnGrid]
        }]
    }]
});

function submitTheForm(){
    if (!profileResultFromPanel.getForm().isValid())
        return;

    var record = formTableColumnGrid.getSelectionModel().getSelections();
    if (Ext.isEmpty(record)) {
        Ext.Msg.alert('错误:', '请至少选择一条需要剖析的字段');
        return;
    }

    var params = '{';

    for (var i = 0; i < record.length; i++) {

        params = params+ "'profileTableColumns["+i+"].profileTableColumnName' : '"+record[i].get("columnName")+"',";
        params = params+ "'profileTableColumns["+i+"].profileTableColumnDesc' : '"+record[i].get("remarks")+"',";
    }
    params = params.substring(0, params.length - 1)
    params = params+"}";
    console.log(params);
    console.log(Ext.util.JSON.decode(params));
    profileResultFromPanel.form.submit({
        url : '../profileTable/save.shtml',
        waitTitle : '提示',
        method : 'POST',
        waitMsg : '正在处理数据,请稍候...',
        params:Ext.util.JSON.decode(params),
        success : function(form, action) { // 回调函数有2个参数
            profileResultGridStore(false);
            profileResultFromWindow.hide();



        },
        failure : function(form, action) {
            Ext.Msg.alert('提示', action.result.msg);
        }
    });

}

/**
 * 编辑窗口
 */
var profileResultFromWindow = new Ext.Window( {
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
    items : [profileResultFromPanel], // 嵌入的表单面板
    buttons : [ {
        text : '保存',
        iconCls : 'acceptIcon',
        handler : function() {
            submitTheForm();
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
            profileResultFromWindow.hide();
        }
    } ]
});


function showProfileResultFromWindow(store_reload){
    profileResultGridStore = store_reload;
    profileResultFromWindow.show();


}
