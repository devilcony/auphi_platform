Ext.override(Ext.form.RadioGroup, {
    getValue : function() {
        var v;
        this.items.each(function(item) {
        	if(item.checked===true){
        		 v=item.inputValue;
        	}
            //if (item.getValue()) {
            //    v = item.getRawValue();
            //    return false;
        	//}
  });
  return v;
 },
 setValue : function(v) {
        if (this.rendered) {
            this.items.each(function(item) {
                item.setValue(item.getRawValue() == v);
   });
        } else {
            for (k in this.items) {
                this.items[k].checked = this.items[k].inputValue == v;
            }
        }
 }
});