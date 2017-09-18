
Ext.onReady(function() {

    alert()
    // 定义列模型
    var sm = new Ext.grid.CheckboxSelectionModel({
        selModel: {
            selection: "rowmodel",
            mode: "MULTI"
        }
    });
    var rownum = new Ext.grid.RowNumberer( {
        header : 'NO',
        width : 28
    });



    var cm = new Ext.grid.ColumnModel( [sm, rownum,
        {
            header : '主键',
            dataIndex : 'ID_PROFILE_TABLE_RESULT',
            hidden : true
        },{
            header : '组名',
            sortable : true,
            dataIndex : 'profielTableGroup.profielTableGroupName',
        },{
            header : '数据源名',
            dataIndex : 'profileTable.databaseName',
        },{
            header : '表ID',
            dataIndex : 'profileTable.profileTableId',
            hidden : true
        },{
            header : '表名',
            dataIndex : 'profileTable.tableName'
        },{
            header : '表描述',
            dataIndex : 'profileTable.profielName'
        }, {
            header : '列名',
            dataIndex : 'profileTableColumn.profileTableColumnName',
        },{
            header : '列描述',
            dataIndex : 'profileTableColumn.profileTableColumnDesc',
        }, {
            header : '类型',
            dataIndex : 'indicatorDataType',
            sortable : true,
            width : 50
        }, {
            header : '长度',
            dataIndex : 'indicatorDataLength',
            hidden : true,
            width : 50
        },{
            header : '精度',
            dataIndex : 'indicatorDataPrecision',
            hidden : true,
            width : 50
        },{
            header : '小数长度',
            dataIndex : 'indicatorDataScale',
            sortable : true,
            hidden : true,
            width : 50
        },{
            header : '总数',
            dataIndex : 'indicatorAllCount',
            sortable : true,
            width : 50
        },{
            header : '不同值数',
            dataIndex : 'indicatorDistinctCount',
            sortable : true,
            width : 50
        },{
            header : '空值数',
            dataIndex : 'indicatorNullCount',
            sortable : true,
            width : 50
        },{
            header : '零个数',
            dataIndex : 'indicatorZeroCount',
            sortable : true,
            width : 50
        },{
            header : '平均值',
            dataIndex : 'indicatorAggAvg',
            sortable : true,
            width : 50
        },{
            header : '最大值',
            dataIndex : 'indicatorAggMax',
            sortable : true,
            width : 50
        },{
            header : '最小值',
            dataIndex : 'indicatorAggMin',
            sortable : true,
            width : 50
        },{
            header : '时间',
            dataIndex : 'createTime',
            sortable : true,
            width : 120
        },{
            header : '操作',
            dataIndex : 'table_operation',
            sortable : true,
            width : 100,
            renderer: function (data, metadata, record, rowIndex, columnIndex, store) {
                var id_table = store.getAt(rowIndex).get('profileTable.profileTableId');
                return "<a href='#' onclick='showDetailsWindow("+id_table+")'> 详情</a>"
            }
        } ]);




    var store = new Ext.data.Store( {
        proxy : new Ext.data.HttpProxy( {
            url : '../profileTableResult/listResult.shtml',
            timeout:1800000000
        }),
        reader : new Ext.data.JsonReader( {
            totalProperty : 'total',
            root : 'rows'
        }, [ {
            name : 'profileTableResultId'
        }, {
            name : 'profielTableGroup.profielTableGroupName'
        },{
            name : 'profileTable.databaseName'
        }, {
            name : 'profileTable.tableName'
        },{
            name : 'profileTable.profileTableId'
        },{
            name:'profileTable.profielName'
        }, {
            name : 'profileTableColumn.profileTableColumnName'
        }, {
            name : 'profileTableColumn.profileTableColumnDesc'
        }, {
            name : 'indicatorDataType'
        }, {
            name : 'indicatorNullCount'
        },{
            name:'indicatorZeroCount'
        },{
            name:'indicatorDataLength'
        },{
            name:'indicatorDataPrecision'
        },{
            name:'indicatorDataScale'
        },{
            name:'indicatorAllCount'
        },{
            name:'indicatorDistinctCount'
        },{
            name:'indicatorAggAvg'
        },{
            name:'indicatorAggMax'
        },{
            name:'indicatorAggMin'
        },{
            name:'createTime'
        }]),
        listeners: {
            datachanged: function() {
                //autoCheckGridHead(Ext.getCmp('id_grid_sfxm'));
            }
        }
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

    var number = parseInt(pagesize_combo.getValue());
    // 改变每页显示条数reload数据
    pagesize_combo.on("select", function(comboBox) {
        bbar.pageSize = parseInt(comboBox.getValue());
        number = parseInt(comboBox.getValue());
        store_reload(false);
    });



    // 分页工具栏
    var bbar = new Ext.PagingToolbar( {
        id:'bbar',
        pageSize : number,
        store : store,
        displayInfo : true,
        displayMsg : '显示{0}条到{1}条,共{2}条',
        plugins : new Ext.ux.ProgressBarPager(), // 分页进度条
        emptyMsg : "没有符合条件的记录",
        items : [ '-', '&nbsp;&nbsp;', pagesize_combo ]
    });


    //分组
    var profileTableGroupStore = new Ext.data.JsonStore({
        fields: ['profielTableGroupId', 'profielTableGroupName'],
        url : "../profileTableGroup/getTableGroupList.shtml",
        autoLoad:true,
        root : "",
        listeners:{
            load : function( store, records, successful, operation){
                if(successful){
                    var rec = new (store.recordType)();
                    rec.set('profielTableGroupId', '');
                    rec.set('profielTableGroupName', '全部分组');
                    store.insert(0,rec);
                }
            }
        }
    });


    var profileTableGroupComboBox = new Ext.form.ComboBox({
        fieldLabel : '表名',
        emptyText:'请选择所属组',
        hiddenName : "profileTableId",
        forceSelection: true,
        anchor : '100%',
        store: profileTableGroupStore,
        valueField : "profielTableGroupId",
        displayField : "profielTableGroupName",
        typeAhead: true,
        mode: 'local',
        triggerAction: 'all',
        editable:false,
        selectOnFocus : true,// 设置用户能不能自己输入,true为只能选择列表中有的记录
        resizable : true,
        width: 120,
        listeners: {
            select : function(profileTableGroupComboBox, record,index){
                profileTableComboBox.clearValue();
                profileTableColumnComboBox.clearValue();
                profileTableStore.load({
                    params : {
                        "profielTableGroup.profielTableGroupId" : profileTableGroupComboBox.value
                    }
                });

                store_reload(false);
            }
        }
    });




    //表名
    var profileTableStore = new Ext.data.JsonStore({
        fields: ['profileTableId', 'tableName'],
        url : "../profileTable/getProfileTableList.shtml",
        autoLoad:true,
        root : "",
        listeners:{
            load : function( store, records, successful, operation){
                if(successful){
                    var rec = new (store.recordType)();
                    rec.set('profileTableId', '');
                    rec.set('tableName', '全部表名');
                    store.insert(0,rec);
                }
            }
        }
    });



    var profileTableComboBox = new Ext.form.ComboBox({
        fieldLabel : '表名',
        emptyText:'请选择表名',
        hiddenName : "profileTableId",
        forceSelection: true,
        anchor : '100%',
        store: profileTableStore,
        valueField : "profileTableId",
        displayField : "tableName",
        typeAhead: true,
        mode: 'local',
        triggerAction: 'all',
        editable:false,
        selectOnFocus : true,// 设置用户能不能自己输入,true为只能选择列表中有的记录
        resizable : true,
        width: 240,
        listeners: {
            select : function(profileTableComboBox, record,index){
                profileTableColumnComboBox.clearValue();
                var id =  profileTableComboBox.value;
                if(id!=null && id != ''){
                    profileTableColumnStore.load({
                        params : {
                            "profielTableGroup.profielTableGroupId" : profileTableGroupComboBox.value,
                            "profileTable.profileTableId" :id
                        }
                    });
                }
                store_reload(false);
            }
        }
    });


    //列名
    var profileTableColumnStore = new Ext.data.JsonStore({
        fields: ['profileTableColumnId', 'profileTableColumnName'],
        url : "../profileTableColumn/getProfileTableColumnList.shtml",
        root : "",
        listeners:{
            load : function( store, records, successful, operation){
                if(successful){
                    var rec = new (store.recordType)();
                    rec.set('profileTableColumnId', '');
                    rec.set('profileTableColumnName', '全部列名');
                    store.insert(0,rec);
                }
            }
        }
    });
    var profileTableColumnComboBox = new Ext.form.ComboBox({
        fieldLabel : '列名',
        emptyText:'请选择列名',
        hiddenName : "profileTableColumnId",
        forceSelection: true,
        anchor : '100%',
        store: profileTableColumnStore,
        valueField : "profileTableColumnId",
        displayField : "profileTableColumnName",
        typeAhead: true,
        mode: 'local',
        triggerAction: 'all',
        editable:false,
        selectOnFocus : true,// 设置用户能不能自己输入,true为只能选择列表中有的记录
        resizable : true,
        width: 240,
        listeners: {
            select : function(profileTableColumnComboBox, record,index){
                store_reload(false);
            }
        }
    });


    // 表格工具栏
    var tbar = new Ext.Toolbar({
        items : [{text: '增加事件调度',iconCls: 'page_addIcon',handler: function(){}},
            {
            text : '新增',
            iconCls : 'page_addIcon',
            id : 'id_tbi_add',
            handler : function() {
                addItem();
            }
        },'-',{
            text : '编辑',
            id : 'tbi_del',
            iconCls : 'page_edit_1Icon',
            handler : function() {
                updateItem();
            }
        },'-',{
            text : '删除',
            id : 'tbi_edit',
            iconCls : 'page_delIcon',
            handler : function() {

                if(checkBeforeUpdate(grid)) {

                }

            }
        },'-',
            '所属组:',profileTableGroupComboBox]
    });

    // 表格实例
    var grid = new Ext.grid.GridPanel( {
        height : 500,
        id : 'id_grid_sfxm',
        autoScroll : true,

        frame : true,
        region : 'center',
        store : store,
        viewConfig : {
            forceFit : true
        },
        stripeRows : true,
        cm : cm,
        sm : sm,
        tbar : tbar,
        bbar : bbar,

        loadMask : {
            msg : '正在加载表格数据,请稍等...'
        }
    });


    var viewport = new Ext.Viewport( {
        layout : 'border',
        items : [grid]
    });




    function store_reload(renovate){

        store.load( {
            params : {
                start : 0,
                limit : renovate? '':pagesize_combo.value,
                "profielTableGroup.profielTableGroupId":profileTableGroupComboBox.value,
                "profileTable.profileTableId":profileTableComboBox.value,
                "profileTableColumn.profileTableColumnId":profileTableColumnComboBox.value,
                renovate:renovate
            }
        });
    }

    store_reload(false);




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
        allowBlank : true,
        editable:false,
        resizable : true,
        listeners: {
            select : function(formSchemaNameComboBox, record,index){
                var schemaName = formSchemaNameComboBox.value;
                formTableNameStore.load({
                    params : {
                        id_database : formDatasourceComboBox.value,
                        schema_name : schemaName
                    }
                });
            }
        }
    });


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
        allowBlank : false,
        listeners: {
            select : function(formTableNameComboBox, record,index){
                fromTableColumnStore.load( {
                    params : {
                        "databaseId":formDatasourceComboBox.value,
                        "schemaName":formSchemaNameComboBox.value,
                        "tableName":formTableNameComboBox.value
                    }
                });

            }, blur : function(f) {
                f.setValue(f.getEl().dom.value);// 接收用户输入的数值
            }
        }
    });


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
                    alert(1);

                    profileTableGroupComboBox.clearValue();
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
        height : 240,
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






    var formPanel = new Ext.form.FormPanel( {
        id : 'formPanel',
        name : 'formPanel',
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
            id:'createTableFieldset',
            xtype:'fieldset',
            title: '表信息',
            autoHeight:true,
            anchor : '98%',
            collapsed: false,
            items :[{
                layout : 'column',
                border : false,
                anchor : '100%',
                items : [{
                    columnWidth:.5,
                    layout: 'form',
                    border:false,
                    items: [{
                        xtype:'textfield',
                        id:'profielName',
                        fieldLabel : '表描述',
                        name : 'profielName',
                        maxLength : 200,
                        allowBlank : false,
                        anchor : '100%'
                    }]//据数据源
                },{
                    columnWidth:.5,
                    layout: 'form',
                    border:false,
                    items: [formTableGroupComboBox]//据数据源
                }]
            },{
                layout : 'column',
                border : false,
                anchor : '100%',
                items : [{
                    columnWidth:.5,
                    layout: 'form',
                    border:false,
                    items: [formDatasourceComboBox]
                },{
                    columnWidth:.5,
                    layout: 'form',
                    border:false,
                    items: [formSchemaNameComboBox]//表名
                }]

            },{
                layout : 'column',
                border : false,
                anchor : '100%',
                items : [{
                    columnWidth:.5,
                    layout: 'form',
                    border:false,
                    items: [formTableNameComboBox]
                },{
                    columnWidth:.5,
                    layout: 'form',
                    border:false,
                    items: [{
                        xtype:'textfield',
                        id:'formProfielName',
                        fieldLabel : '过滤条件',
                        name : 'condition',
                        maxLength : 200,
                        anchor : '100%'
                    }]
                }]
            }]
        },{
            id:'profileTableColumnFieldset',
            xtype:'fieldset',
            title: '字段信息',
            autoHeight:true,
            anchor : '98%',
            collapsed: false,
            items :[formTableColumnGrid]
        }]
    });

    /**
     * 编辑窗口
     */
    var fromWindow = new Ext.Window( {
        layout : 'fit', // 设置窗口布局模式
        width : 750, // 窗口宽度
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
        items : [formPanel], // 嵌入的表单面板
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
                fromWindow.hide();
            }
        } ]
    });



    /**
     * 新增窗体初始化
     */
    function addItem() {
        clearForm(formPanel.getForm());
        fromWindow.setTitle('<span class="commoncss">新增</span>');
        Ext.getCmp('windowAction').setValue('add');
        profileTableColumnStore.removeAll();
        fromWindow.show();
    }

    function  updateItem(){
        var record = grid.getSelectionModel().getSelections();
        if (Ext.isEmpty(record) || record.length > 1) {
            Ext.Msg.alert('提示:', '请先选中一条您要修改的数据');
            return;
        }

        clearForm(formPanel.getForm());
        fromWindow.setTitle('<span class="commoncss">编辑</span>');
        Ext.getCmp('windowAction').setValue('update');
        profileTableColumnStore.removeAll();

        Ext.Ajax.request( {
            url : '../profileTable/getProfileTable.shtml',
            success : function(response) {
                var obj = Ext.util.JSON.decode(response.responseText);
                formPanel.getForm().setValues(obj)
                formTableGroupComboBox.setValue(obj.profielTableGroup.profielTableGroupId);
                formSchemaNameStore.load({
                    params : {id_database : obj.databaseId},
                    callback:function(r,options,success){
                        formTableNameStore.load({
                            params : {
                                id_database : obj.databaseId,
                                schema_name : obj.schemaName
                            }
                        });
                    }
                });

                //获取表所有的Column
                fromTableColumnStore.load( {
                    params : {
                        "databaseId":obj.databaseId,
                        "schemaName":obj.schemaName,
                        "tableName":obj.tableName
                    }
                });

                fromTableColumnStore.on({load:function(){
                    setTimeout(function(){
                        var rowCount = fromTableColumnStore.getCount();
                        for(var i=0;i<rowCount;i++) {
                            if (isContains(fromTableColumnStore.getAt(i).get("columnName"),obj.profileTableColumns)) {   //找到此时初始化的行
                                formTableColumnGrid.getSelectionModel().selectRow(i,true);
                            }
                        }
                    },100);
                }
                });


            },
            failure : function(response) {
                var obj = Ext.util.JSON.decode(response.responseText);
                Ext.Msg.alert('提示', obj.msg);
            },
            params : {
                profileTableId : record[0].get("profileTable.profileTableId")
            }
        });



        fromWindow.show();

    }


    function isContains(columnName,columns){
        for(var i = 0 ; i < columns.length;i++){

            if(columnName == columns[i].profileTableColumnName){
                return true;

            }
        }
    }

    function submitTheForm(){
        if (!formPanel.getForm().isValid())
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
        formPanel.form.submit({
            url : '../profileTable/save.shtml',
            waitTitle : '提示',
            method : 'POST',
            waitMsg : '正在处理数据,请稍候...',
            params:Ext.util.JSON.decode(params),
            success : function(form, action) { // 回调函数有2个参数

                fromWindow.hide();
                store_reload(false);


            },
            failure : function(form, action) {
                Ext.Msg.alert('提示', action.result.msg);
            }
        });

    }



    /**
     * 显示详情Window
     * @param id 表Id
     */
    showDetailsWindow =  function (id){

        var url = "../profileTableResult/details.shtml?profileTable.profileTableId="+id

        Ext.Ajax.request({
            url : url,
            params : {
            },
            success : function(response, config) {
                json = Ext.util.JSON.decode(response.responseText);

                var store = new Ext.data.JsonStore({
                    data : json.data,
                    fields : json.fieldsNames
                });

                var column_  = json.columnModle;
                var grid2 = new Ext.grid.GridPanel({
                    height : 400,
                    autoScroll : true,
                    frame : true,
                    region : 'center',
                    viewConfig : {
                        forceFit : true
                    },
                    stripeRows : true,
                    split : true,
                    renderTo : detailsWindow,
                    columns: column_,
                    store:store
                });


                var detailsWindow = new Ext.Window( {
                    title:'详情',
                    layout : 'fit', // 设置窗口布局模式
                    width : 800, // 窗口宽度
                    height : 400, // 窗口高度
                    modal:true,
                    resizable : true,// 是否可以改变大小，默认可以
                    maskdisabled : true,
                    closeAction : 'close',
                    closable : true, // 是否可关闭
                    collapsible : true, // 是否可收缩
                    border : false, // 边框线设置
                    constrain : true, // 设置窗口是否可以溢出父容器
                    animateTarget : Ext.getBody(),
                    pageY : 120, // 页面定位Y坐标
                    pageX : document.body.clientWidth / 2 - 600 / 2, // 页面定位X坐标
                    items : [grid2],
                    buttons : [{
                        text : '关闭',
                        iconCls : 'deleteIcon',
                        handler : function() {
                            detailsWindow.close()
                        }
                    } ]
                });

                detailsWindow.show();
            },
            failure : function() {

            }
        });

    }



});

