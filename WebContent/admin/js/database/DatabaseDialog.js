DatabaseDialog = Ext.extend(Ext.Window, {
    title: '数据库连接',
    width: 700,
    height: 550,
    closeAction: 'close',
    modal: true,
    layout: 'border',
    initComponent: function() {
        var deckOptionsBox = new ListView({
            valueField: 'value',
            store: new Ext.data.JsonStore({
                fields: ['value','text'],
                data: [
                    {value: 0, text: '一般'},
                    {value: 1, text: '高级'},
                    {value: 2, text: '选项'},
                    {value: 3, text: '连接池'},
                    {value: 4, text: '集群'}]
            }),
            columns: [{
                dataIndex: 'value', hidden: true
            },{
                width: 1, dataIndex: 'text'
            }]
        });

        var normal = new NormalPanel();
        var advance = new AdvancePanel();
        var options = new OptionsPanel();
        var pool = new PoolPanel();
        var cluster = new ClusterPanel();

        var me = this;
        this.initReposityDatabase = function(database,rep) {
            Ext.Ajax.request({
                url: 'database.shtml',
                method: 'POST',
                params: {name: database,repId:rep},
                scope: this,
                success: function(response) {
                    var dbinfo = Ext.decode(response.responseText);
                    this.initDatabase(dbinfo);
                }
            })
        };
        this.initTransDatabase = function(database) {
            debugger;
            Ext.Ajax.request({
                url: 'database.shtml',
                method: 'POST',
                params: {graphXml: getActiveGraph().toXml(), name: database},
                scope: this,
                success: function(response) {
                    var dbinfo = Ext.decode(response.responseText);
                    this.initDatabase(dbinfo);
                }
            })
        };
        this.initJobDatabase = function(database) {
            Ext.Ajax.request({
                url: 'database.shtml',
                method: 'POST',
                params: {graphXml: getActiveGraph().toXml(), name: database},
                scope: this,
                success: function(response) {
                    var dbinfo = Ext.decode(response.responseText);
                    this.initDatabase(dbinfo);
                }
            })
        };

        this.initDatabase = function(dbinfo) {
            normal.initData(dbinfo);
            advance.initData(dbinfo);
            options.initData(dbinfo);
            pool.initData(dbinfo);
            cluster.initData(dbinfo);
        };

        this.getValue = function() {
            var val = normal.getValue();
            advance.getValue(val);
            options.getValue(val);
            pool.getValue(val);
            cluster.getValue(val);

            return val;
        };

        var content = new Ext.Panel({
            region: 'center',
            defaults: {border: false},
            layout: 'card',
            activeItem: 0,
            bodyStyle: 'padding-top: 16px',
            items: [normal, advance, options, pool, cluster]
        });

        deckOptionsBox.on('selectionchange', function(v) {

            content.getLayout().setActiveItem(deckOptionsBox.getValue());
        });

        this.on('afterrender', function() {
            deckOptionsBox.setValue(0);
        });

        this.items = [{
            region: 'west',
            width: 150,
            layout: 'fit',
            autoScroll: true,
            items: deckOptionsBox
        }, content];

        var bCancel = new Ext.Button({
            text: '取消', scope: this, handler: function() {
                this.close();
            }
        });
        var bTest = new Ext.Button({
            text: '测试', scope: this, handler: function() {
                Ext.Ajax.request({
                    url: 'test.shtml',
                    method: 'POST',
                    params: {databaseInfo: Ext.encode(me.getValue())},
                    success: function(response) {
                        decodeResponse(response, function(resObj) {
                            var dialog = new EnterTextDialog();
                            dialog.show(null, function() {
                                dialog.setText(decodeURIComponent(resObj.message));
                            });
                        });
                    }
                });
            }
        });

        function decodeResponse(response, cb, opts) {
            try {
                var resinfo = Ext.decode(response.responseText);
                if(resinfo.success) {
                    cb(resinfo);
                } else {
                    Ext.Msg.show({
                        title: resinfo.title,
                        msg: resinfo.message,
                        buttons: Ext.Msg.OK,
                        icon: Ext.MessageBox.ERROR
                    });
                }
                Ext.getBody().unmask();
            } finally {
                Ext.getBody().unmask();
            }
        }

        var bFuture = new Ext.Button({
            text: '特征列表', scope: this, handler: function() {
                Ext.Ajax.request({
                    url: 'features.shtml',
                    method: 'POST',
                    params: {databaseInfo: Ext.encode(this.getValue())},
                    success: function(response) {
                        var records = Ext.decode(response.responseText);

                        var grid = new DynamicEditorGrid({rowNumberer: true});
                        var win = new Ext.Window({
                            title: '特征列表',
                            width: 1000,
                            height: 600,
                            modal: true,
                            layout: 'fit',
                            items: grid
                        });
                        win.show();

                        grid.loadMetaAndValue(records);
                    }
                });
            }
        });
        var bView = new Ext.Button({
            text: '浏览', scope: this, handler: function() {

                var dialog = new DatabaseExplorerDialog();
                dialog.show(null, function() {
                    dialog.initDatabase(this.getValue());
                }, this);

            }
        });
        var bOk = new Ext.Button({
            text: '确定', handler: function() {
                Ext.Ajax.request({
                    url: 'check.shtml',
                    method: 'POST',
                    params: {databaseInfo: Ext.encode(me.getValue())},
                    success: function(response) {
                        var json = Ext.decode(response.responseText);
                        if(!json.success) {
                            Ext.Msg.alert('系统提示', json.message);
                        } else {
                            me.fireEvent('create', me);
                        }
                    }
                });
            }
        });

        this.bbar = ['->', bCancel, bTest, bFuture, bView, bOk];

        DatabaseDialog.superclass.initComponent.call(this);

        this.addEvents('create')
    }
});

Ext.reg('DatabaseDialog', DatabaseDialog);
