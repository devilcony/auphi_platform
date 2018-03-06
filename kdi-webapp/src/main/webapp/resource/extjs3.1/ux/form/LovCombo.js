if ('function' !== typeof RegExp.escape) {
	RegExp.escape = function(s) {
		if ('string' !== typeof s) {
			return s
		}
		return s.replace(/([.*+?^=!:${}()|[\]\/\\])/g, '\\$1')
	}
}
Ext.ns('Ext.ux.form');
Ext.ux.form.LovCombo = Ext.extend(Ext.form.ComboBox, {
	checkField: 'checked',
	separator: ',',
	initComponent: function() {
		Ext.ux.form.LovCombo.superclass.initComponent.call(this);
		this.tpl = ['<tpl for=".">',
					'<div class="x-combo-list-item">',
					'<img src="' + Ext.BLANK_IMAGE_URL + '" class="ux-lovcombo-icon ux-lovcombo-icon-',
					'{[values.' + this.checkField + '?"checked":"unchecked"]}">',
					'<div class="ux-lovcombo-item-text">{' + (this.displayField || 'text') + '}</div>',
					'</div>',
					'</tpl>'
				].join("");

		this.on({
			scope: this,
			expand : function(){
				this.getValue()&&this.setValue(this.getValue());
			}
		});
		
		this.onLoad = this.onLoad.createSequence(function() {
			if (this.el) {
				var v = this.el.dom.value;
				this.el.dom.value = v
			}
		});
		this.store.on("load",function(){
			this.getValue()&&this.setValue(this.getValue());
		},this);
	},
	initEvents: function() {
		Ext.ux.form.LovCombo.superclass.initEvents.apply(this, arguments);
		this.keyNav.tab = false
	},
	clearValue: function() {
		this.value = '';
		this.setRawValue(this.value);
		this.store.clearFilter();
		this.store.each(function(r) {
			r.set(this.checkField, false)
		},this);
		if(this.hiddenField) {
			this.hiddenField.value = ''
		}
		this.applyEmptyText()
	},
	getCheckedDisplay: function() {
		var re = new RegExp(this.separator, "g");
		return this.getCheckedValue(this.displayField).replace(re, this.separator + ' ')
	},
	getCheckedValue: function(field) {
		field = field || this.valueField;
		var c = [];
		var snapshot = this.store.snapshot || this.store.data;
		snapshot.each(function(r) {
			if (r.get(this.checkField)) {
				c.push(r.get(field))
			}
		},this);
		return c.join(this.separator)
	},

	onBeforeQuery: function(qe) {
		qe.query = qe.query.replace(new RegExp(this.getCheckedDisplay() + '[ ' + this.separator + ']*'), '')
	},

	onSelect: function(record, index) {
		if (this.fireEvent('beforeselect', this, record, index) !== false){
			record.set(this.checkField, !record.get(this.checkField));
			if (this.store.isFiltered()) {
				this.doQuery(this.allQuery)
			}
			this.setValue(this.getCheckedValue());
			this.fireEvent('select', this, record, index)
		}
	},
	setValue: function(v) {
		if (v) {
			v = '' + v;
			if (this.valueField) {
				this.store.clearFilter();
				this.store.each(function(r) {
					var checked = !(!v.match('(^|' + this.separator + ')' + RegExp.escape(r.get(this.valueField)) + '(' + this.separator + '|$)'));
					r.set(this.checkField, checked)
				},this);
				this.value = this.getCheckedValue() || v;
				this.setRawValue(this.store.getCount()>0 ? this.getCheckedDisplay() : this.value);
				if (this.hiddenField) {
					this.hiddenField.value = this.value
				}
			} else {
				this.value = v;
				this.setRawValue(v);
				if (this.hiddenField) {
					this.hiddenField.value = v
				}
			}
			if (this.el) {
				this.el.removeClass(this.emptyClass)
			}
			if(this.selectall){
				if(this.getCheckedValue().split(",").length == this.store.getCount()){
					this.selectall.replaceClass("ux-combo-selectall-icon-unchecked","ux-combo-selectall-icon-checked");
				}else{
					this.selectall.replaceClass("ux-combo-selectall-icon-checked","ux-combo-selectall-icon-unchecked")
				}
			}
		} else {
			this.clearValue()
		}
		
	},
	initList : function(){
        if(!this.list){
            var cls = 'x-combo-list';

            this.list = new Ext.Layer({
                parentEl: this.getListParent(),
                shadow: this.shadow,
                cls: [cls, this.listClass].join(' '),
                constrain:false
            });

            var lw = this.listWidth || Math.max(this.wrap.getWidth(), this.minListWidth);
            this.list.setSize(lw, 0);
            this.list.swallowEvent('mousewheel');
            this.assetHeight = 0;
            if(this.syncFont !== false){
                this.list.setStyle('font-size', this.el.getStyle('font-size'));
            }
            if(this.title){
                this.header = this.list.createChild({cls:cls+'-hd', html: this.title});
                this.assetHeight += this.header.getHeight();
            }
			
			if(this.showSelectAll){
				this.selectall = this.list.createChild({
					cls:cls + 'item ux-combo-selectall-icon-unchecked ux-combo-selectall-icon',
					html: "选择全部"
				});
				this.selectall.on("click",function(el){
					if(this.selectall.hasClass("ux-combo-selectall-icon-checked")){
						this.selectall.replaceClass("ux-combo-selectall-icon-checked","ux-combo-selectall-icon-unchecked");
						this.deselectAll();
					}else{
						this.selectall.replaceClass("ux-combo-selectall-icon-unchecked","ux-combo-selectall-icon-checked")
						this.selectAll();
					}
				},this);
				this.assetHeight += this.selectall.getHeight();
			}

            this.innerList = this.list.createChild({cls:cls+'-inner'});
            this.mon(this.innerList, 'mouseover', this.onViewOver, this);
            this.mon(this.innerList, 'mousemove', this.onViewMove, this);
            this.innerList.setWidth(lw - this.list.getFrameWidth('lr'));

            if(this.pageSize){
                this.footer = this.list.createChild({cls:cls+'-ft'});
                this.pageTb = new Ext.PagingToolbar({
                    store: this.store,
                    pageSize: this.pageSize,
                    renderTo:this.footer
                });
                this.assetHeight += this.footer.getHeight();
            }

            if(!this.tpl){
                this.tpl = '<tpl for="."><div class="'+cls+'-item">{' + this.displayField + '}</div></tpl>';
            }

            this.view = new Ext.DataView({
                applyTo: this.innerList,
                tpl: this.tpl,
                singleSelect: true,
                selectedClass: this.selectedClass,
                itemSelector: this.itemSelector || '.' + cls + '-item',
                emptyText: this.listEmptyText
            });

            this.mon(this.view, 'click', this.onViewClick, this);

            this.bindStore(this.store, true);

            if(this.resizable){
                this.resizer = new Ext.Resizable(this.list,  {
                   pinned:true, handles:'se'
                });
                this.mon(this.resizer, 'resize', function(r, w, h){
                    this.maxHeight = h-this.handleHeight-this.list.getFrameWidth('tb')-this.assetHeight;
                    this.listWidth = w;
                    this.innerList.setWidth(w - this.list.getFrameWidth('lr'));
                    this.restrictHeight();
                }, this);

                this[this.pageSize?'footer':'innerList'].setStyle('margin-bottom', this.handleHeight+'px');
            }
        }
    },
	expand : function(){
        if(this.isExpanded() || !this.hasFocus){
            //return;
        }
        this.list.alignTo(this.wrap, this.listAlign);
        this.list.show();
        if(Ext.isGecko2){
            this.innerList.setOverflow('auto'); // necessary for FF 2.0/Mac
        }
        Ext.getDoc().on({
            scope: this,
            mousewheel: this.collapseIf,
            mousedown: this.collapseIf
        });
        this.fireEvent('expand', this);
    },
	selectAll: function() {
		this.store.each(function(record) {
			record.set(this.checkField, true);
		},this);
		this.setValue(this.getCheckedValue());
		this.doQuery(this.allQuery);
	},
	deselectAll: function() {
		this.clearValue()
	},
	assertValue: Ext.emptyFn,
	beforeBlur: Ext.emptyFn
});
Ext.reg('lovcombo', Ext.ux.form.LovCombo);