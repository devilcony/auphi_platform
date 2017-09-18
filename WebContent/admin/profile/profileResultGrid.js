
Ext.onReady(function() {


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
            dataIndex : 'profileTableColumn.profileTableColumnId',
            hidden : true
        },{
            header : '组ID',
            dataIndex : 'profielTableGroup.profielTableGroupId',
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
            width : 80,
            renderer: function (data, metadata, record, rowIndex, columnIndex, store) {
                var columnId = store.getAt(rowIndex).get('profileTableColumn.profileTableColumnId');
                var indicatorDistinctCount = store.getAt(rowIndex).get('indicatorDistinctCount');
                if(indicatorDistinctCount>0){
                    return "<a href='#' onclick='showGridWindow("+columnId+")'> "+indicatorDistinctCount+"</a>"
                }else{
                    return indicatorDistinctCount;
                }

            }
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
            name : 'profileTableColumn.profileTableColumnId'
        }, {
            name : 'profielTableGroup.profielTableGroupName'
        },{
            name : 'profielTableGroup.profielTableGroupId'
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
        fields: ['profileTableId', 'profielName'],
        url : "../profileTable/getProfileTableList.shtml",
        autoLoad:true,
        root : "",
        listeners:{
            load : function( store, records, successful, operation){
                if(successful){
                    var rec = new (store.recordType)();
                    rec.set('profileTableId', '');
                    rec.set('profielName', '全部表名');
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
        displayField : "profielName",
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
        items : [{
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
                deleteItem();

            }
        },'-',{
            text : '统计',
            icon : '../images/icons/dashboard.png',
            handler : function() {
                showDashboard();
            }

        },"->",
            '所属组:',profileTableGroupComboBox,'-','表名:',profileTableComboBox,'-','列名:',profileTableColumnComboBox
            ,"-",{
                text :'刷新',
                id:'reset',
                handler : function() {
                    store_reload(true);
                }
            },'-',{
                text : '导出',
                id:'export',
                handler : function() {

                }
            }]
    });

    var table_id = '-1';
    var tmp = false;
    var cls = 'white-row';
    // 表格实例
    var grid = new Ext.grid.GridPanel( {
        height : 500,
        id : 'id_grid_sfxm',
        region : 'center',
        store : store,
        viewConfig : {
            getRowClass:function(record,rowIndex,rowParams,store){
                var id_table = store.getAt(rowIndex).get('profileTable.profileTableId');
                if(table_id != id_table ){
                    table_id = id_table
                    tmp = tmp?false:true
                    if(tmp){
                        cls = 'x-grid-record-rosybrown1';
                    }else{
                        cls = 'white-row';
                    }
                }
                return cls;

            }
        },
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
        frame : false,
        items:[grid]
    });




   var store_reload =  function (renovate){

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



    /**
     * 新增窗体初始化
     */
    function addItem() {
        clearForm(profileResultFromPanel.getForm());
        profileResultFromWindow.setTitle('<span class="commoncss">新增</span>');
        Ext.getCmp('windowAction').setValue('add');
        var rg = Ext.getCmp('tableMode');
        rg.setValue(1);

        formDatasourceComboBox.setDisabled(false);
        formSchemaNameComboBox.setDisabled(false);
        formTableNameComboBox.setDisabled(false);
        Ext.getCmp("formCondition").setDisabled(false);

        Ext.getCmp("sql").setDisabled(true);
        Ext.getCmp("exeSql").setDisabled(true);


        profileResultFromTableGrid.store.removeAll();
        formTableColumnGrid.store.removeAll();

       // profileResultFromWindow.show();

        showProfileResultFromWindow(store_reload)
    }

    function  updateItem(){
        var record = grid.getSelectionModel().getSelections();
        if (Ext.isEmpty(record) || record.length > 1) {
            Ext.Msg.alert('提示:', '请先选中一条您要修改的数据');
            return;
        }

        clearForm(profileResultFromPanel.getForm());
        profileResultFromWindow.setTitle('<span class="commoncss">编辑</span>');
        Ext.getCmp('windowAction').setValue('update');
        profileTableColumnStore.removeAll();

        Ext.Ajax.request( {
            url : '../profileTable/getProfileTable.json',
            success : function(response) {


                var obj = Ext.util.JSON.decode(response.responseText);

                var rg = Ext.getCmp('tableMode');
                rg.setValue(obj.tableNameTag);
                profileResultFromPanel.getForm().setValues(obj)

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

                //加载Group下的表信息
                profileResultFromTableStore.load({
                    params : {
                        'profielTableGroup.profielTableGroupId' :obj.profielTableGroup.profielTableGroupId
                    }
                });

                if(obj.tableNameTag ==2){
                    formTableNameComboBox.clearValue()
                }

                //获取表所有的Column
                fromTableColumnStore.load( {
                    params : {
                        "tableNameTag":obj.tableNameTag,
                        "databaseId":obj.databaseId,
                        "schemaName":obj.tableNameTag ==1 ? obj.schemaName:'',
                        "tableName":obj.tableNameTag ==1 ?obj.tableName :obj.sql
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



       // profileResultFromWindow.show();
        showProfileResultFromWindow(store_reload)
    }

    function deleteItem(){
        var record = grid.getSelectionModel().getSelections();
        if (Ext.isEmpty(record)) {
            Ext.Msg.alert('提示', '请先选中要删除的数据!');
            return;
        }
        var strChecked = jsArray2JsString(record, 'profileTableColumn.profileTableColumnId');

        Ext.MessageBox.show({
            title:'删除提醒',
            msg: '请选择您要删除的数据',
            buttons:{ok:'选中的表及表所有字段',yes:'只删除选择的字段',cancel:'取消'},
            fn: function(btn, text){

                var dl = "";
                if(btn == 'ok'){
                    dl = 't';
                }
                if(btn == 'yes'){
                    dl = 'c';

                }
                if(dl == 't' || dl == 'c'){
                    Ext.Ajax.request( {
                        url : '../profileTable/delete.shtml',
                        success : function(response) {
                            Ext.MessageBox.alert('提示', '删除成功');
                            store_reload(false);

                        },
                        failure : function(response) {
                            var resultArray = Ext.util.JSON
                                .decode(response.responseText);
                            Ext.Msg.alert('提示', resultArray.msg);
                        },
                        params : {
                            ids : strChecked,
                            dl:dl
                        }
                    });
                }

            },
            icon: Ext.MessageBox.QUESTION
        });
    }




    function isContains(columnName,columns){
        for(var i = 0 ; i < columns.length;i++){

            if(columnName == columns[i].profileTableColumnName){
                return true;

            }
        }
    }



    var dashboardTypeStore = new Ext.data.JsonStore({
        data: [
            { 'value': 'nullValueDashboard', 'text': '空值统计' },
            { 'value': 'differentValueDashboard', 'text': '不同值统计' }
        ],
        fields: ['value', 'text']
    });
    var dashboardTypeCombo = new Ext.form.ComboBox({
        hiddenName : 'model_status',
        forceSelection : true,
        store:dashboardTypeStore,//store用来为ComboBox提供数据
        valueField:'value',//与store定义中的名字一样。正是根据他们之间的对应关系来显示数据。
        displayField:'text',
        typeAhead : true,
        mode : 'local',
        width: 100,
        triggerAction : 'all',
        selectOnFocus : true,// 设置用户能不能自己输入,true为只能选择列表中有的记录
        allowBlank : false,
        listeners: {
            afterRender: function(combo) {
                var firstValue = dashboardTypeStore.reader.jsonData[0].value;
                dashboardTypeCombo.setValue(firstValue);//同时下拉框会将与name为firstValue值对应的 text显示
            },
            select : function(combo, record,index){
                changingPage();
            }
        }
    });

    //分组
    var  dashboardTableGroupStore = new Ext.data.JsonStore({
        fields: ['profielTableGroupId', 'profielTableGroupName'],
        url : "../profileTableGroup/getTableGroupList.shtml",
        autoLoad:true,
        root : ""
    });


    var  dashboardTableGroupComboBox = new Ext.form.ComboBox({
        emptyText:'请选择所属组',
        hiddenName : "profileTableId",
        forceSelection: true,
        anchor : '100%',
        store: dashboardTableGroupStore,
        valueField : "profielTableGroupId",
        displayField : "profielTableGroupName",
        typeAhead: true,
        mode: 'local',
        triggerAction: 'all',
        editable:false,
        selectOnFocus : true,// 设置用户能不能自己输入,true为只能选择列表中有的记录
        resizable : true,
        width: 100,
        listeners: {
            select : function(comboBox, record,index){
                dashboardTableStore.removeAll();
                dashboardTableComboBox.clearValue();
                dashboardTableStore.load({
                    params : {
                        "profielTableGroup.profielTableGroupId" : comboBox.value
                    }
                });
            }
        }
    });


    //表名
    var dashboardTableStore = new Ext.data.JsonStore({
        fields: ['profileTableId', 'profielName'],
        url : "../profileTable/getProfileTableList.shtml",
        root : ""
    });


    var dashboardTableComboBox = new Ext.form.ComboBox({
        fieldLabel : '表名',
        emptyText:'请选择表名',
        hiddenName : "profileTableId",
        forceSelection: true,
        anchor : '100%',
        store: dashboardTableStore,
        valueField : "profileTableId",
        displayField : "profielName",
        typeAhead: true,
        mode: 'local',
        triggerAction: 'all',
        editable:false,
        selectOnFocus : true,// 设置用户能不能自己输入,true为只能选择列表中有的记录
        resizable : true,
        width: 200,
        listeners: {
            select : function(comboBox, record,index){

                dashboardTableColumnStore.removeAll();
                dashboardTableColumnComboBox.clearValue();
                dashboardTableColumnStore.load({
                    params : {
                        "profielTableGroup.profielTableGroupId" : dashboardTableGroupComboBox.value,
                        "profileTable.profileTableId" :dashboardTableComboBox.value
                    }
                });

                changingPage();
            }
        }
    });


    //列名
    var dashboardTableColumnStore = new Ext.data.JsonStore({
        fields: ['profileTableColumnId', 'profileTableColumnName'],
        url : "../profileTableColumn/getProfileTableColumnList.shtml",
        root : ""
    });
    var dashboardTableColumnComboBox = new Ext.form.ComboBox({
        fieldLabel : '列名',
        emptyText:'请选择列名',
        hiddenName : "profileTableColumnId",
        forceSelection: true,
        anchor : '100%',
        store: dashboardTableColumnStore,
        valueField : "profileTableColumnId",
        displayField : "profileTableColumnName",
        typeAhead: true,
        mode: 'local',
        triggerAction: 'all',
        editable:false,
        selectOnFocus : true,// 设置用户能不能自己输入,true为只能选择列表中有的记录
        resizable : true,
        width: 200,
        listeners: {
            select : function(profileTableColumnComboBox, record,index){
                changingPage();
            }
        }
    });


    // 表格工具栏
    var dashboardTbar = new Ext.Toolbar({
        items : ['统计类型:',dashboardTypeCombo,'-','所属组:',dashboardTableGroupComboBox,'-','表名:',dashboardTableComboBox,'-','列名:',dashboardTableColumnComboBox]
    });


    var dashboardWindow =  new Ext.Window({
        title:'统计信息',
        layout: 'fit', // 设置窗口布局模式
        width: 950, // 窗口宽度
        height: 500, // 窗口高度
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
        tbar:dashboardTbar,
        items: [{
            html:'<iframe id="changingPage" name="changingPage" frameborder="0" width="100%" height="100%" src="dashboard.shtml"></iframe>'
        }], // 嵌入的表单面板
    });

    showDashboard = function(){
        var record = grid.getSelectionModel().getSelections();
        if (!Ext.isEmpty(record) &&  record.length == 1) {
            dashboardTableGroupComboBox.setValue(record[0].get("profielTableGroup.profielTableGroupId"));
            dashboardTableStore.load({
                params : {
                    "profielTableGroup.profielTableGroupId" : dashboardTableGroupComboBox.value
                },
                callback:function(r,options,success){
                    dashboardTableComboBox.setValue(record[0].get("profileTable.profileTableId"));
                    changingPage();
                }
            });

            dashboardTableColumnStore.load({
                params : {
                    "profielTableGroup.profielTableGroupId" : dashboardTableGroupComboBox.value,
                    "profileTable.profileTableId" :dashboardTableComboBox.value
                },
                callback:function(r,options,success){
                    dashboardTableColumnComboBox.setValue(record[0].get("profileTableColumn.profileTableColumnId"));
                    changingPage();
                }
            });

        }
        dashboardWindow.show();

    }

    changingPage = function(){
        var profileTableId = dashboardTableComboBox.value == null? '':dashboardTableComboBox.value;
        var profileTableColumnId = dashboardTableColumnComboBox.value == null? '':dashboardTableColumnComboBox.value;
        document.getElementById("changingPage").src='dashboard.shtml?profileTableId='+profileTableId+"&profileTableColumnId="+profileTableColumnId+"&type="+dashboardTypeCombo.value
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

