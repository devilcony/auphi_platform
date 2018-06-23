KettleTreePanel =  Ext.extend(Ext.tree.TreePanel, {
    filterByText : function(text) {
        this.filterBy(text, 'text');
    },
    filterBy : function(text, by) {
        this.clearFilter();
        var view = this.getView(), me = this, nodesAndParents = [];
        this.getRootNode().cascadeBy(function(tree, view) {

            var currNode = this;

            if (currNode && currNode.data[by] && currNode.data[by].toString().toLowerCase().indexOf(text.toLowerCase()) > -1) {

                me.expandPath(currNode.getPath());

                while (currNode.parentNode) {
                    nodesAndParents.push(currNode.id);
                    currNode = currNode.parentNode;
                }
            }
        }, null, [ me, view ]);
        this.getRootNode().cascadeBy(function(tree, view) {
            var uiNode = view.getNodeByRecord(this);
            if (uiNode && !Ext.Array.contains(nodesAndParents, this.id)) {
                Ext.get(uiNode).setDisplayed('none');

            }
        }, null, [ me, view ]);

    },
    clearFilter : function() {
        var view = this.getView();
        this.getRootNode().cascadeBy(function(tree, view) {
            var uiNode = view.getNodeByRecord(this);
            if (uiNode) {
                Ext.get(uiNode).setDisplayed('table-row');
            }
        }, null, [ this, view ]);
    }

});
Ext.reg('KettleTreePanel', KettleTreePanel);

KettleForm = Ext.extend(Ext.form.FormPanel, {
    labeWidth: 70,
    labelAlign: 'right',
    defaultType: 'textfield',
    bodyStyle: 'padding: 10px px',
    autoHeight: true
});
Ext.reg('KettleForm', KettleForm);

KettleDialog = Ext.extend(Ext.Window, {
    modal:true,
    resizable : true,// 是否可以改变大小，默认可以
    maskdisabled : true,
    closeAction : 'hide',
    closable : true, // 是否可关闭
    collapsible : false, // 是否可收缩
    border : false, // 边框线设置
    layout: 'border',
    defaults: {border: false},
    enterEnable: true,
    stepNameLabel: '步骤名称',

    initComponent: function() {
        var kettleDialog = this;
        this.items = [{
            xtype: 'KettleForm',
            region: 'north',
            height: 35,
            labelWidth: 70
        }, {
            region: 'center',
            bodyStyle: 'padding: 0px',
            layout: 'fit',
            items: this.fitItems
        }];


        var tbItems = ['->', {
            text: '取消',
            handler: function() {
                kettleDialog.close();
            }
        }];


        if(this.showPreview){
            tbItems.push({
                text: '保存',
                handler: function() {
                    if(kettleDialog.checkData()) {
                        kettleDialog.onSure(false);
                    }
                }
            });
        }



        tbItems.push({text: '保存并关闭',
            handler: function() {
                if(kettleDialog.checkData()) {
                    kettleDialog.onSure(true);
                }
            }
        });


        this.buttons = tbItems;

        KettleDialog.superclass.initComponent.call(this);
        this.addEvents('beforesave', 'save');
    },

    afterRender: function() {
        KettleDialog.superclass.afterRender.call(this);

        if(this.enterEnable) {
            new Ext.KeyMap(this.el, {
                key: 13,
                fn: function() {
                    if(this.checkData()) {
                        this.onSure();
                        this.close();
                    }
                },
                scope: this
            });
        }

        this.initData();
    },

    initBottomBar: Ext.emptyFn,

    getInitData: function() {
        return this.value ;
    },

    initData: function() {

    },

    checkData: function() {

        return true;
    },

    tabselect:function (tab, newc, oldc) {
        return true;
    },
    getData: function() {
        return Ext.apply({}, this.saveData());
    },

    saveData: function() {
        return Ext.apply({}, this.getValues());
    },


    onSure: function(close) {
        var data = this.getData();
        if(this.fireEvent('beforesave', this, data) !== false) {

            this.fireEvent('save', this, data,close)
        }
    },

    /**
     * 该方法已过时，使用saveData取代
     *
     * */
    getValues: function() {
        return {};
    }

});

KettleTabDialog = Ext.extend(KettleDialog, {
    initComponent: function() {
        var kettleTabDialog = this;
        this.fitItems = new Ext.TabPanel({
            id:'fitItems',
            region: 'center',
            activeTab: 0,
            items: this.tabItems,
            listeners:{
                'tabchange':function(tab, newc, oldc){
                   return  kettleTabDialog.tabselect(tab, newc, oldc);
                }
            }
        });



        KettleTabDialog.superclass.initComponent.call(this);
    }
});

KettleEditor = Ext.extend(Ext.Panel, {

    theme: 'javascript',
    autoScroll: true,

    // private
    afterRender : function(){
        KettleEditor.superclass.afterRender.call(this);
        var textArea = Ext.DomHelper.append(this.body, {tag: 'TEXTAREA'});
        var editor = this.editor = CodeMirror.fromTextArea(textArea, {
            mode: this.theme,
            lineNumbers: false,
            lineWrapping: true
        });

        if(!Ext.isEmpty(this.value))
            editor.setValue(this.value);

        new Ext.dd.DropTarget(this.body,
            {
                ddGroup: 'JsWriteGroup',
                notifyDrop: function(ddSource, e, data) {
                    var text = data.node.text;

                    editor.replaceSelection(text, null, "paste");
                    return true;
                }
            });
    },

    getValue: function() {
        return this.editor.getValue();
    },

    setValue: function(v) {
        this.editor.setValue(v);
    }
});

Ext.reg('KettleEditor', KettleEditor);


KettleEditorDialog = Ext.extend(Ext.Window, {
    title: '脚本编辑',
    width: 800,
    height: 600,
    layout: 'fit',
    modal: true,

    theme: 'javascript',
    canceltext: '取消',
    suretext: '确定',

    initComponent: function() {
        var ke = this.items = new KettleEditor({theme: this.theme});
//		var ke = this.items = new Ext.form.TextArea();

        this.bbar = ['->', {
            text: this.canceltext, scope: this, handler: function() {this.close()}
        }, {
            text: this.suretext, scope: this, handler: function() {
                this.fireEvent('ok', ke.getValue());
            }
        }]

        KettleEditorDialog.superclass.initComponent.call(this);

        this.initData = function(data) {
            ke.setValue(data);
        };

        this.addEvents('ok');
    }
});

KettleEditorGrid = Ext.extend(Ext.grid.EditorGridPanel, {

    initComponent: function() {


        var menu = new Ext.menu.Menu({
            items: [{
                text: '插入', scope: this, handler: this.insert
            }, {
                text: '当前行前面插入', scope: this, handler: this.insertBefore
            }, {
                text: '当前行后面插入', scope: this, handler: this.insertAfter
            }, '-', {
                text: '上移', scope: this, handler: this.rowUp
            }, {
                text: '下移', scope: this, handler: this.rowDown
            }, '-', {
                text: '删除选中的行', scope: this, handler: this.deleteRow
            }, {
                text: '删除全部', scope: this, handler: this.deleteAll
            }]
        });
        this.menuAdd(menu);

        this.on('contextmenu', function(e) {
            menu.showAt(e.getXY());
            e.preventDefault();
        });

        this.on('cellmousedown', function(grid, row, col, e) {
            grid.getSelectionModel().select(row, col);
        });

        KettleEditorGrid.superclass.initComponent.call(this);
    },

    menuAdd: Ext.emptyFn,

    insert: function() {
        var store = this.getStore();
        var i = store.getCount();
        this.stopEditing();
        store.insert( i, new store.recordType(this.getDefaultValue()));
        this.startEditing(i, 0);	// 0 is rowindex
    },

    insertBefore: function() {
        var sm = this.getSelectionModel();
        if(sm.hasSelection()) {
            var cell = sm.getSelectedCell();
            var store = this.getStore();

            this.stopEditing();
            store.insert(cell[0], new store.recordType(this.getDefaultValue()));
            this.startEditing(cell[0], 0);	// 0 is rowindex
        }
    },

    insertAfter: function() {
        var sm = this.getSelectionModel();
        if(sm.hasSelection()) {
            var cell = sm.getSelectedCell();
            var store = this.getStore();

            this.stopEditing();
            store.insert(cell[0] + 1, new store.recordType(this.getDefaultValue()));
            this.startEditing(cell[0] + 1, 1);	// 0 is rowindex
        }
    },

    deleteRow: function() {
        var sm = this.getSelectionModel();
        if(sm.hasSelection()) {
            var cell = sm.getSelectedCell();
            var store = this.getStore();
            store.removeAt(cell[0]);

            if(store.getCount() > cell[0])
                sm.select(cell[0], cell[1]);
        }
        this.getView().refresh(); //刷新
    },

    rowUp: function() {
        var sm = this.getSelectionModel();
        if(sm.hasSelection()) {
            var row = sm.getSelectedCell()[0];
            var store = this.getStore();

            if(row > 0) {
                var rec = store.getAt(row);
                store.removeAt(row);
                store.insert(row-1, rec);
            }
        }
    },

    rowDown: function() {
        var sm = this.getSelectionModel();
        if(sm.hasSelection()) {
            var row = sm.getSelectedCell()[0];
            var store = this.getStore();

            if((row + 1) < store.getCount()) {
                var rec = store.getAt(row);
                store.removeAt(row);
                store.insert(row+1, rec);
            }
        }

    },

    deleteAll: function() {

        var store = this.getStore();
        store.removeAll();
        console.log('store：',store);

    },

    getDefaultValue: function() {
        return {};
    },

    // private
    afterRender : function(){
        KettleEditorGrid.superclass.afterRender.call(this);

        new Ext.KeyMap(this.el, {
            key: 46,
            fn: this.deleteRow,
            scope: this
        });
    }
});

Ext.reg('KettleEditorGrid', KettleEditorGrid);

KettleDynamicGrid = Ext.extend(KettleEditorGrid, {
    initComponent: function(){
        this.store = new Ext.data.JsonStore();
        this.columns = [];

        KettleDynamicGrid.superclass.initComponent.apply(this, arguments);
    },
    onRender: function(ct, position){
        this.colModel.defaultSortable = true;
        KettleDynamicGrid.superclass.onRender.call(this, ct, position);
    },

    loadMetaAndValue: function(mv) {
        var columns = [], fields = [];
        columns.push(new Ext.grid.RowNumberer());
        for(var i=0, len=mv.columns.length; i<len; i++) {
            columns.push(mv.columns[i]);
            fields.push(mv.columns[i].dataIndex);
        }
        this.getColumnModel().setConfig(columns);

        var store = this.getStore();
        store.recordType = Ext.data.Record.create(fields);
        var recordType = Ext.data.Record.create(fields);
        store.reader = new Ext.data.JsonReader({}, recordType);

        store.loadData(mv.records);
    }
});

Ext.override(Ext.data.Store, {
    toArray: function(fields) {
        var data = [];
        this.each(function(rec) {
            var obj = new Object();
            Ext.each(fields, function(field) {
                if(Ext.isString(field))
                    obj[field] = rec.get(field);
                else if(Ext.isObject(field)) {
                    if(field.value)
                        obj[field.name] = field.value;
                    else
                        obj[field.name] = rec.get(field.field);
                }
            });
            data.push(obj);
        });
        return data;
    },
    toJson: function() {
        var data = [];
        this.each(function(rec) {
            var obj = new Object();
            rec.fields.each(function(field) {
                obj[field.name] = rec.get(field.name);
            });
            data.push(obj);
        });
        return data;
    },
    merge: function(store, fields) {
        var me = this;
        if(store.getCount() <= 0) return;
        var data = store.toArray(fields);

        if(this.getCount() > 0) {
            var answerDialog = new AnswerDialog({has: me.getCount(), found: data.length});
            answerDialog.on('addNew', function() {
                me.loadData(data, true);
            });
            answerDialog.on('addAll', function() {
                Ext.each(data, function(d) {
                    var record = new store.recordType(d);
                    me.insert(0, record);
                });
            });
            answerDialog.on('clearAddAll', function() {
                me.removeAll();
                me.loadData(data);
            });
            answerDialog.show();
        } else {
            me.loadData(data);
        }
    }
});

Ext.form.CompositeField = Ext.extend(Ext.form.Field, {


    defaultMargins: '0 5 0 0',


    skipLastItemMargin: true,


    isComposite: true,


    combineErrors: true,


    labelConnector: ', ',





    initComponent: function() {
        var labels = [],
            items  = this.items,
            item;

        for (var i=0, j = items.length; i < j; i++) {
            item = items[i];

            if (!Ext.isEmpty(item.ref)){
                item.ref = '../' + item.ref;
            }

            labels.push(item.fieldLabel);


            Ext.applyIf(item, this.defaults);


            if (!(i == j - 1 && this.skipLastItemMargin)) {
                Ext.applyIf(item, {margins: this.defaultMargins});
            }
        }

        this.fieldLabel = this.fieldLabel || this.buildLabel(labels);


        this.fieldErrors = new Ext.util.MixedCollection(true, function(item) {
            return item.field;
        });

        this.fieldErrors.on({
            scope  : this,
            add    : this.updateInvalidMark,
            remove : this.updateInvalidMark,
            replace: this.updateInvalidMark
        });

        Ext.form.CompositeField.superclass.initComponent.apply(this, arguments);

        this.innerCt = new Ext.Container({
            layout  : 'hbox',
            items   : this.items,
            cls     : 'x-form-composite',
            defaultMargins: '0 3 0 0',
            ownerCt: this
        });
        this.innerCt.ownerCt = undefined;

        var fields = this.innerCt.findBy(function(c) {
            return c.isFormField;
        }, this);


        this.items = new Ext.util.MixedCollection();
        this.items.addAll(fields);

    },


    onRender: function(ct, position) {
        if (!this.el) {

            var innerCt = this.innerCt;
            innerCt.render(ct);

            this.el = innerCt.getEl();



            if (this.combineErrors) {
                this.eachItem(function(field) {
                    Ext.apply(field, {
                        markInvalid : this.onFieldMarkInvalid.createDelegate(this, [field], 0),
                        clearInvalid: this.onFieldClearInvalid.createDelegate(this, [field], 0)
                    });
                });
            }


            var l = this.el.parent().parent().child('label', true);
            if (l) {
                l.setAttribute('for', this.items.items[0].id);
            }
        }

        Ext.form.CompositeField.superclass.onRender.apply(this, arguments);
    },


    onFieldMarkInvalid: function(field, message) {
        var name  = field.getName(),
            error = {
                field: name,
                errorName: field.fieldLabel || name,
                error: message
            };

        this.fieldErrors.replace(name, error);

        if (!field.preventMark) {
            field.el.addClass(field.invalidClass);
        }
    },


    onFieldClearInvalid: function(field) {
        this.fieldErrors.removeKey(field.getName());

        field.el.removeClass(field.invalidClass);
    },


    updateInvalidMark: function() {
        var ieStrict = Ext.isIE6 && Ext.isStrict;

        if (this.fieldErrors.length == 0) {
            this.clearInvalid();


            if (ieStrict) {
                this.clearInvalid.defer(50, this);
            }
        } else {
            var message = this.buildCombinedErrorMessage(this.fieldErrors.items);

            this.sortErrors();
            this.markInvalid(message);


            if (ieStrict) {
                this.markInvalid(message);
            }
        }
    },


    validateValue: function(value, preventMark) {
        var valid = true;

        this.eachItem(function(field) {
            if (!field.isValid(preventMark)) {
                valid = false;
            }
        });

        return valid;
    },


    buildCombinedErrorMessage: function(errors) {
        var combined = [],
            error;

        for (var i = 0, j = errors.length; i < j; i++) {
            error = errors[i];

            combined.push(String.format("{0}: {1}", error.errorName, error.error));
        }

        return combined.join("<br />");
    },


    sortErrors: function() {
        var fields = this.items;

        this.fieldErrors.sort("ASC", function(a, b) {
            var findByName = function(key) {
                return function(field) {
                    return field.getName() == key;
                };
            };

            var aIndex = fields.findIndexBy(findByName(a.field)),
                bIndex = fields.findIndexBy(findByName(b.field));

            return aIndex < bIndex ? -1 : 1;
        });
    },


    reset: function() {
        this.eachItem(function(item) {
            item.reset();
        });



        (function() {
            this.clearInvalid();
        }).defer(50, this);
    },


    clearInvalidChildren: function() {
        this.eachItem(function(item) {
            item.clearInvalid();
        });
    },


    buildLabel: function(segments) {
        return Ext.clean(segments).join(this.labelConnector);
    },


    isDirty: function(){

        if (this.disabled || !this.rendered) {
            return false;
        }

        var dirty = false;
        this.eachItem(function(item){
            if(item.isDirty()){
                dirty = true;
                return false;
            }
        });
        return dirty;
    },


    eachItem: function(fn, scope) {
        if(this.items && this.items.each){
            this.items.each(fn, scope || this);
        }
    },


    onResize: function(adjWidth, adjHeight, rawWidth, rawHeight) {
        var innerCt = this.innerCt;

        if (this.rendered && innerCt.rendered) {
            innerCt.setSize(adjWidth, adjHeight);
        }

        Ext.form.CompositeField.superclass.onResize.apply(this, arguments);
    },


    doLayout: function(shallow, force) {
        if (this.rendered) {
            var innerCt = this.innerCt;

            innerCt.forceLayout = this.ownerCt.forceLayout;
            innerCt.doLayout(shallow, force);
        }
    },


    beforeDestroy: function(){
        Ext.destroy(this.innerCt);

        Ext.form.CompositeField.superclass.beforeDestroy.call(this);
    },


    setReadOnly : function(readOnly) {
        if (readOnly == undefined) {
            readOnly = true;
        }
        readOnly = !!readOnly;

        if(this.rendered){
            this.eachItem(function(item){
                item.setReadOnly(readOnly);
            });
        }
        this.readOnly = readOnly;
    },

    onShow : function() {
        Ext.form.CompositeField.superclass.onShow.call(this);
        this.doLayout();
    },


    onDisable : function(){
        this.eachItem(function(item){
            item.disable();
        });
    },


    onEnable : function(){
        this.eachItem(function(item){
            item.enable();
        });
    }
});

Ext.reg('compositefield', Ext.form.CompositeField);

EnterTextDialog = Ext.extend(Ext.Window, {
    width: 600,
    height: 400,
    layout: 'fit',
    modal: true,
    title: '数据库连接测试',
    initComponent: function() {
        var me = this;

        var textArea = this.textArea = new Ext.form.TextArea({
            readOnly: true
        });

        this.items = textArea;

        if(!this.bbar) {
            var bOk = new Ext.Button({
                text: '确定', handler: function() {
                    me.close();
                }
            });

            this.bbar = ['->', bOk];
        }

        EnterTextDialog.superclass.initComponent.call(this);
        this.addEvents('sure');
    },

    setText: function(text) {
        this.textArea.setValue(text);
    }
});