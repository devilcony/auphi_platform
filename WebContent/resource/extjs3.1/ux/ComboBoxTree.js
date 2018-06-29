Ext.form.ComboBoxTree = Ext.extend(Ext.form.ComboBox,{
    callback : Ext.emptyFn,
    store : new Ext.data.SimpleStore({
        fields : [],
        data : [ {} ],
    }),
    editable : this.editable || false,
    mode : 'local',
    emptyText : this.emptyText || "请选择所属目录",
    allowBlank : this.allowBlank || true,
    triggerAction : 'all',
    maxHeight : 200,
    anchor : '99.5%',
    displayField : 'text',
    valueField : 'id',
    tpl : "<tpl for='.'><div style='height:200px'><div id='tree'></div></div></tpl>",
    selectedClass : '',
    onSelect : Ext.emptyFn,
    /**
     * 根的名字
     */
    rootText : this.rootText || 'rootText',
    rootId : this.rootId || '_rootId',
    /**
     * 树的请求地址
     */
    treeUrl : this.treeUrl,
    tree : null,
    constructor : function(cfg){
        var this_ = this;

        Ext.apply(this,cfg);

        this.listeners = {
            render : function(cmp){
                if(!Ext.isEmpty(this.value)){
                    var sm = this.tree.getSelectionModel();
                    var loader = this.tree.loader;
                    loader.load(this.tree.root,function(node){
                        this.setValue(this.value);
                    },this);
                }
            },
            scope : this
        };

        Ext.form.ComboBoxTree.superclass.constructor.call(this);
    },
    initComponent : function() {
        var this_ = this;
        // 保存上次隐藏的空节点
        var hiddenPkgs = [];
        var treePanel ;
        this.field = {
            xtype: 'textfield',
            emptyText: this_.emptyText,
            id: 'filter_input',
            width: 270,
            enableKeyEvents:true,
            listeners: {
                keyup:function(){
                    var text = this.getValue();
                    // 先要显示上次隐藏掉的节点
                    Ext.each(hiddenPkgs, function(n) {
                        n.ui.show();
                    });

                    // 如果输入的数据不存在，就执行clear()
                    if (!text) {
                        filter.clear();
                        return;
                    }
                    treePanel.expandAll();

                    // 根据输入制作一个正则表达式，'i'代表不区分大小写
                    var re = new RegExp(Ext.escapeRe(text), 'i');
                    filter.filterBy(function(n) {
                        // 只过滤叶子节点，这样省去枝干被过滤的时候，底下的叶子都无法显示
                        return !n.isLeaf() || re.test(n.text);
                    });

                    // 如果这个节点不是叶子，而且下面没有子节点，就应该隐藏掉
                    hiddenPkgs = [];
                    treePanel.root.cascade(function(n) {
                        if (!n.isLeaf() && n.ui.ctNode.offsetHeight < 3) {
                            n.ui.hide();
                            hiddenPkgs.push(n);
                        }
                    });
                }
            }
        };

        this.toolbar = new Ext.Toolbar({
            buttonAlign : 'center',
            items : [this.field]
        });

        treePanel =   this.tree = new Ext.tree.TreePanel({
            height : 200,
            scope : this,
            autoScroll : true,
            tbar : this.toolbar,
            split : true,
            root : new Ext.tree.AsyncTreeNode({
                id : this.rootId,
                text : this.rootText
            }),
            loader : new Ext.tree.TreeLoader({
                url : this.treeUrl,
                preloadChildren : true
            })
        });



        var filter = new Ext.tree.TreeFilter(this.tree, {
            clearBlank : true,
            autoClear : true
        });





        /**
         * 点击选中节点并回调传值
         */
        /*this.tree.on('click', function(node) {
            if (node.id != null && node.id != '') {
                if (node.id != '_root' && node.id != '-1') {
                    this.setValue(node.attributes[this.valueField]);
                    //this.collapse();
                }
            }
        },this);*/

        this.emp = new Array();

        this.tree.on('checkchange', function(node,state) {
            if(state){
                this.emp.push(node.text)

            }else{
                this.emp.remove(node.text);
            }
            console.log(this.emp)

            this_.setValue(this.emp.toString());
            this_.value = this.emp.toString();

        },this);

        this.on('expand', function(){
            this.tree.render('tree');
            this.tree.expandAll();
        });

        Ext.form.ComboBoxTree.superclass.initComponent.call(this);
    },
    /**
     * 修改之默认选中 重写findRecord 方法
     * */
    findRecord : function(prop, value){
        var record;
        if(this.tree.root.childNodes.length > 0){
            this.tree.root.cascade(function(node){
                if(node.attributes[prop] == value){
                    record = new Ext.data.Record();
                    record.set(this.valueField,node.attributes[this.valueField]);
                    record.set(this.displayField,node.attributes[this.displayField]);
                    return record;
                }
            },this);
        }
        return record;
    }
});