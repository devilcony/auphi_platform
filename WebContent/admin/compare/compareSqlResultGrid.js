Ext.onReady(function(){

    // 定义列模型
    var sm = new Ext.grid.CheckboxSelectionModel();
    var rownum = new Ext.grid.RowNumberer( {
        header : 'NO',
        width : 28
    });

    var cm = new Ext.grid.ColumnModel( [sm, rownum,
        {
            header : 'ID',
            dataIndex : 'compareSqlColumn.compareSqlColumnId',
        },{
            header : '名称',
            sortable : true,
            dataIndex : 'compareSql.compareName',
        },{
            header : '组名',
            sortable : true,
            dataIndex : 'compareTableGroup.profielTableGroupName',
        },{
            header : '数据源名',
            dataIndex : 'compareSql.databaseName',
        },{
            header : '列名',
            dataIndex : 'compareSqlColumn.columnName',
        },{
            header : '参考列名',
            dataIndex : 'compareSqlColumn.referenceColumnName',
        },{
            header : '列描述',
            dataIndex : 'compareSqlColumn.columnDesc',
        },{
            header : '实际值',
            sortable : true,
            xtype: "numbercolumn",
            format: '0,0.0000',
            dataIndex : 'columnValue',
        },{
            header : '参照值',
            sortable : true,
            xtype: "numbercolumn",
            format: '0,0.0000',
            dataIndex : 'referenceColumnValue',
        },{
            header : '时间',
            dataIndex : 'createTime',
            sortable : true,
            width : 120
        },{
            header : '结果',
            dataIndex : 'compareResult',
            sortable : true,
            width : 120,
            renderer: function (data, metadata, record, rowIndex, columnIndex, store) {
                var compareResult = store.getAt(rowIndex).get('compareResult');
                metadata.css = 'x-grid-record-gray';
                return compareResult == 1 ? '<span style="color:green;">通过</span>' : '<span style="color:red;">不通过</span>'
            }
        },
        /*
        {
            header : '操作',
            dataIndex : 'table_operation',
            sortable : true,
            width : 100,
            renderer: function (data, metadata, record, rowIndex, columnIndex, store) {
                var id_table = store.getAt(rowIndex).get('profileTable.profileTableId');

                return "<a href='#' onclick='showDetailsWindow("+id_table+")'> 详情</a>"
            }
        }
        */
        ]);



    var store = new Ext.data.Store( {
        proxy : new Ext.data.HttpProxy( {
            url : '../compareSqlResult/listResult.shtml',
            timeout:1800000000
        }),
        reader : new Ext.data.JsonReader( {
            totalProperty : 'total',
            root : 'rows'
        }, [ {
            name : 'compareSqlColumn.compareSqlColumnId'
        },{
            name : 'compareTableGroup.profielTableGroupName'
        },{
            name:'compareSql.compareSqlId'
        },{
            name:'compareSql.databaseName'
        },{
            name : 'compareSql.compareName'
        },{
            name:'compareSqlColumn.columnName'
        },{
            name:'compareSqlColumn.columnName'
        },{
            name : 'compareSqlColumn.referenceColumnName'
        },{
            name:'compareSqlColumn.columnDesc'
        },{
            name : 'columnValue'
        },{
            name : 'referenceColumnValue'
        },{
            name:'createTime'
        },{
            name:'compareResult'
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



    var profileTableGroupComboBox = new Ext.form.ComboBox({
        emptyText:'请选择所属组',
        hiddenName : "profileTableId",
        forceSelection: true,
        anchor : '100%',
        store: new Ext.data.JsonStore({
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
        }),
        valueField : "profielTableGroupId",
        displayField : "profielTableGroupName",
        typeAhead: true,
        mode: 'local',
        triggerAction: 'all',
        editable:false,
        selectOnFocus : true,// 设置用户能不能自己输入,true为只能选择列表中有的记录
        resizable : true,
        width: 200,
        listeners: {
            select : function(profileTableGroupComboBox, record,index){

                compareNameComboBox.clearValue();
                compareNameComboBox.store.load({
                    params:{
                        'compareTableGroup.profielTableGroupId':profileTableGroupComboBox.value
                    }
                })

                store_reload(false);
            }
        }
    });


    var compareNameComboBox = new Ext.form.ComboBox({
        emptyText:'请选择名称',
        hiddenName : "compareSqlId",
        forceSelection: true,
        anchor : '100%',
        store: new Ext.data.JsonStore({
            fields: ['compareSqlId', 'compareName'],
            url : "../compareSql/getCompareSqlCombo.shtml",
            autoLoad:true,
            root : "rows",
            listeners:{
                load : function( store, records, successful, operation){
                    if(successful){
                        var rec = new (store.recordType)();
                        rec.set('compareSqlId', '');
                        rec.set('compareName', '全部名称');
                        store.insert(0,rec);
                    }
                }
            }
        }),
        valueField : "compareSqlId",
        displayField : "compareName",
        typeAhead: true,
        mode: 'local',
        triggerAction: 'all',
        editable:false,
        selectOnFocus : true,// 设置用户能不能自己输入,true为只能选择列表中有的记录
        resizable : true,
        width: 200,
        listeners: {
            select : function(profileTableGroupComboBox, record,index){
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
        },'-',"->",
            '所属组:',profileTableGroupComboBox,'-','名称',compareNameComboBox
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
            forceFit : true,
            getRowClass:function(record,rowIndex,rowParams,store){
                var id_table = store.getAt(rowIndex).get('compareSql.compareSqlId');
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
        },
    });


    var viewport = new Ext.Viewport( {
        layout : 'border',
        items : [grid]
    });

    function store_reload(renovate){

        var record = grid.getSelectionModel().getSelections();

        var ids = jsArray2JsString(record, 'compareSqlColumn.compareSqlColumnId');


        store.load( {
            params : {
                start : 0,
                limit :pagesize_combo.value,
                'compareTableGroup.profielTableGroupId':profileTableGroupComboBox.value,
                'compareSql.compareSqlId': compareNameComboBox.value,
                renovate:renovate,
                'compareSqlColumn.ids':ids
            },
            callback:function(records, options, success){
                if(!success){
                    Ext.Msg.alert('❌出错了',store.reader.jsonData.msg);
                }
            }
        });
    }

    store_reload(false);


    /**
     * 新增窗体初始化
     */
    function addItem() {
        clearForm(compareSqlFromPanel.getForm());
        compareSqlColumnStore.removeAll();
        compareSqlFromWindow.setTitle('<span class="commoncss">新增</span>');

        showCompareSqlFromWindow(store_reload);
    }


    function updateItem() {

        var record = grid.getSelectionModel().getSelections();
        if (Ext.isEmpty(record) || record.length > 1) {
            Ext.Msg.alert('提示:', '请先选中一条您要修改的数据');
            return;
        }

        clearForm(compareSqlFromPanel.getForm());
        compareSqlFromWindow.setTitle('<span class="commoncss">编辑</span>');
        compareSqlColumnGrid.store.removeAll();
        showCompareSqlFromWindow(store_reload);


        Ext.Ajax.request( {
            url : '../compareSql/getCompareSql.json',
            success : function(response) {
                var obj = Ext.util.JSON.decode(response.responseText);
                compareSqlFromPanel.getForm().setValues(obj)
                compareTableGroupComboBox.setValue(obj.compareTableGroup.profielTableGroupId);

                var compareSqlColumns = obj.compareSqlColumns;
                for(var i = 0;i < compareSqlColumns.length;i++) {

                    var rec = new (compareSqlColumnStore.recordType)();
                    rec.set('columnDesc', compareSqlColumns[i].columnDesc);
                    rec.set('columnName', compareSqlColumns[i].columnName);
                    rec.set('columnType', compareSqlColumns[i].columnType);
                    rec.set('compareSqlColumnId', compareSqlColumns[i].compareSqlColumnId);
                    rec.set('compareStyle', compareSqlColumns[i].compareStyle);
                    rec.set('maxRatio', compareSqlColumns[i].maxRatio);
                    rec.set('minRatio', compareSqlColumns[i].minRatio);
                    rec.set('referenceColumnName', compareSqlColumns[i].referenceColumnName);
                    compareSqlColumnStore.add(rec);
                    compareSqlColumnGrid.getSelectionModel().selectRow(i,true);
                }




            },
            failure : function(response) {
                var obj = Ext.util.JSON.decode(response.responseText);
                Ext.Msg.alert('提示', obj.msg);
            },
            params : {
                compareSqlId : record[0].get("compareSql.compareSqlId")
            }
        });

    }


    function deleteItem(){
        var record = grid.getSelectionModel().getSelections();
        if (Ext.isEmpty(record)) {
            Ext.Msg.alert('提示', '请先选中要删除的数据!');
            return;
        }
        var strChecked = jsArray2JsString(record, 'compareSqlColumn.compareSqlColumnId');

        Ext.MessageBox.show({
            title:'删除提醒',
            msg: '请选择您要删除的数据',
            buttons:{ok:'选中的SQL及所有字段',yes:'只删除选择的字段',cancel:'取消'},
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
                        url : '../compareSql/delete.shtml',
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

        var res = [];
        for(var i = 0 ; i < columns.length;i++){

            if(columnName == columns[i].columnName){

                res.push(true);
                res.push(columns[i].columnDesc);
                return res;
            }
        }

        res.push(false);
        res.push("");
        return res;
    }

});