NormalPanel = Ext.extend(Ext.Panel, {
    defaults: {border: false},
    layout: 'border',

    initComponent: function() {

        this.dbinfo = {};
        var connectionNameBox = new Ext.form.TextField({ fieldLabel: '连接名称', anchor: '-5' });
        var connectionBox = new ListView({
            valueField: 'value',
            store:  new Ext.data.JsonStore({
                storeId: 'databaseAccessData',
                fields: ['value','text'],
                proxy: new Ext.data.HttpProxy({
                    url: 'accessData.shtml',
                    method: 'POST'
                })
            }),
            columns: [{
                dataIndex: 'value', hidden: true
            },{
                width: 1, dataIndex: 'text'
            }]
        });	//connection type

        connectionBox.getStore().load();

        var accessBox = new ListView({
            valueField: 'value',
            store: new Ext.data.JsonStore({
                storeId: 'databaseAccessMethod',
                fields: ['value','text'],
                proxy: new Ext.data.HttpProxy({
                    url: 'accessMethod.shtml',
                    method: 'POST'
                })
            }),
            columns: [{
                dataIndex: 'value', hidden: true
            },{
                width: 1, dataIndex: 'text'
            }]
        });	//connection method: jndi/jdbc/odbc...

        this.initData = function(dbinfo) {
            connectionNameBox.setValue(dbinfo.name);
            connectionBox.setValue(dbinfo.type);
            accessBox.setValue(dbinfo.access);
            this.dbinfo = dbinfo;
        };

        this.getValue = function() {
            var val = {
                name: connectionNameBox.getValue(),
                type: connectionBox.getValue(),
                access: accessBox.getValue()
            };
            Ext.apply(val, settingsForm.getForm().getValues());

            return val;
        };

        var fieldset = new Ext.form.FieldSet({
            title: '设置'
        });

        var settingsForm = new Ext.form.FormPanel({
            region: 'center',
            bodyStyle: 'padding: 0px 5px 5px 0px',
            labelWidth: 1,
            layout: 'fit',
            items: fieldset
        });

        connectionBox.on('selectionchange', function() {


            accessBox.getStore().baseParams.accessData = connectionBox.getValue();
            accessBox.getStore().load({
                callback:function(r,options,success){
                    accessBox.setValue(r[0].data.value);

                }
            });
        });
        accessBox.getStore().on('load', function() {
            if(this.dbinfo.type == connectionBox.getValue()) {
                accessBox.setValue(this.dbinfo.access);
            }
        }, this);

        accessBox.on('selectionchange', function() {
            if(Ext.isEmpty(connectionBox.getValue()) || Ext.isEmpty(accessBox.getValue()))
                return;

            Ext.Ajax.request({
                url: 'accessSettings.shtml',
                params: {accessData: connectionBox.getValue(), accessMethod: accessBox.getValue()},
                scope: this,
                success: function(response, opts) {
                    var resObj = Ext.util.JSON.decode(response.responseText);

                    fieldset.removeAll(true);
                    fieldset.doLayout();
                    var message = Ext.util.JSON.decode(resObj.message);
                    Ext.each(message, function(item) {
                        if(item.id == 'port'){

                        }
                        fieldset.add(item);
                    });
                    fieldset.doLayout();
                    settingsForm.getForm().setValues(this.dbinfo);


                },
                failure: function (response) {
                    
                }
            });
        }, this);

        this.items = [{
            xtype: 'KettleForm',
            labelWidth: 60,
            region: 'north',
            height: 40,
            items: connectionNameBox
        }, {
            defaults: {border: false},
            region: 'center',
            layout: 'border',
            items: [{
                region: 'west',
                width: 200,
                layout: 'border',
                margins: '7 5 5 8',
                items: [{
                    title: '连接类型',
                    region : 'center',
                    layout: 'fit',
                    items: connectionBox
                }, {
                    title: '连接方式',
                    region: 'south',
                    height: 130,
                    layout: 'fit',
                    margins: '5 0 0 0',
                    items: accessBox
                }]
            }, settingsForm]
        }];

        NormalPanel.superclass.initComponent.call(this);
    }
});