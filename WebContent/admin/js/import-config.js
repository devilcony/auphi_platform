/**
 * 全局参数表管理
 * 
 */

Ext.onReady(function() {
	var tablename_edit = new Ext.form.TextField();  
	var columnname_edit = new Ext.form.TextField();  

	var data_combox = [['binary_double','binary_double'],
	            ['binary_float','binary_float'],
	            ['blob','blob'],
	            ['clob','clob'],
	            ['char()','char()'],
	            ['date','date'],
	            ['interval day to second','interval day to second'],
	            ['interval yaea to month','interval yaea to month'],
	            ['long','long'],
	            ['long raw','long raw'],
	            ['nclob','nclob'],
	            ['number','number'],
	            ['nvarchar2()','nvarchar2()'],
	            ['raw()','raw()'],
	            ['timestamp','timestamp'],
	            ['timestamp with local time','timestamp with local time'],
	            ['timestamp with time zone','timestamp with time zone'],
	            ['varchar()','varchar()']
	           ];

    //第二步：导入到store中：
    var store_combox = new Ext.data.SimpleStore({
         fields: ['genre_value','genre_text'],
         data : data_combox
    });
   
	var type_edit = new Ext.form.ComboBox({
		typeAhead : true,
		triggerAction : 'all',
		mode : 'local',
		store : store_combox,
		displayField : 'genre_text',
		valueField : 'genre_value'
	});  
	
	var nullable_edit = new Ext.form.Checkbox({   
        boxLabel : '',   
        name : 'no',   
        inputValue : 2,   
        checked : false  
    });
	
	var default1_edit =  new Ext.form.TextField();  
	var	storage_edit = new Ext.form.TextField();
	var comments_edit = new Ext.form.TextField();
	
 
	var data = null;//[['table1','id1', '2', 'name', 'BBB', 'sex', '男'],
	            //['table2','id2', '2', 'name', 'BBB', 'sex', '男']]; 
	var ds = new Ext.data.Store({
		proxy: new Ext.data.MemoryProxy(data),
		reader: new Ext.data.ArrayReader({}, 
			[{name: 'TABLENAME'},
			 {name: 'COLUMNNAME'},
			 {name: 'TYPE'}, 
			 {name: 'NULLABLE'},
			 {name: 'DEFAULT1'},
			 {name: 'STORAGE'},
			 {name: 'COMMENTS'}
			 ]
		 )
	});
	ds.load();
	
	var sm = new Ext.grid.CheckboxSelectionModel();

	var grid = new Ext.grid.EditorGridPanel({
		title : '创建表',
		autoScroll : true,
		tbar: [{text:'新增一行',
				iconCls : 'page_addIcon',
				handler: add_row
			   },
		       {text: '删除一行',
				iconCls : 'page_delIcon',
				handler:remove_row
		       },
		       {text: '保存表结构',
					iconCls : 'page_edit_1Icon',
					handler:goAllDatas
			   }
			   ], 
		renderTo : document.body,
		frame : true,
		//cm : cm,
		sm : sm,
		height : 400,
		width : "100%",
		clickstoEdit : 1,
		store : ds,
		loadMask : {
			msg : '正在加载表格数据,请稍等...'
		},
		columns : [
		{
			header : "表名",
			dataIndex : 'TABLENAME',
			editor : tablename_edit
		},{
			header : "字段名",
			dataIndex : 'COLUMNNAME',
			editor : columnname_edit
		},{
			header : "类型",
			dataIndex : 'TYPE',
			editor : type_edit
		},{
			header : "是否为空",
			dataIndex : 'NULLABLE',
			renderer : nullable_edit,
			editor : nullable_edit
		},{
			header : "默认",
			dataIndex : 'DEFAULT1',
			renderer : "",
			editor :default1_edit
		},{
			header : "贮存",
			dataIndex : 'STORAGE',
			editor : storage_edit
		},{
			header : "注释",
			dataIndex : 'COMMENTS',
			editor : comments_edit
		}
		],
		listeners: {   
			afteredit: function(e){    
			if (e.field == 'name' && e.value == 'Mel Gibson'){     
				Ext.Msg.alert('Error','Mel Gibson movies not allowed');      
				e.record.reject();     
				}else{     
					e.record.commit();     
				}    
			}   
		} 
	});
	
	
	function remove_row (){
		var sm = grid.getSelectionModel();   
		var sel = sm.getSelected();   
		if (sm.hasSelection()){      
			Ext.Msg.show({      
				title: '刪除行',      
				buttons: Ext.MessageBox.YESNOCANCEL,     
				msg: '确定要刪除您选中的行吗 ?',     
				fn: function(btn){      
					if (btn == 'yes'){      
						grid.getStore().remove(sel);       
					}      
				}     
			});    
		}else{
			Ext.Msg.show({      
				title: '提示信息',      
				buttons: Ext.MessageBox.CANCEL,     
				msg: '请您选中需要删除的行。' 
			})		
		};   
	}
	

	var ds_model = Ext.data.Record.create([
	'TABLENAME',                                  
	'COLUMNNAME',
	'TYPE',
	'NULLABLE',
	'DEFAULT1',
	'STORAGE',
	'COMMENTS'
	]);  

	var table_name = '';
	function add_row() {
//		alert(ds.getCount());
//		if(ds.getCount()>0){
//			table_name = grid.getStore().getAt('1').get('TABLENAME');
//		}
		grid.getStore().insert(grid.getStore().getCount(), new ds_model({
			TABLENAME:table_name,
			COLUMNNAME : '',
			TYPE : 'varchar()',
			NULLABLE : true,
			DEFAULT1 : '',
			STORAGE : '',
			COMMENTS: ''
		}));
		grid.startEditing(grid.getStore().getCount()-1,0);  
	} 
	
	
	function goAllDatas(){ 
		//grid是Ext.gird.GridPanel对象 var store = grid.getStore(); 
//		var count = ds.getCount(); 
//		alert(count);
//		for (var i = 0; i < count; i++) {  
//			var record = ds.getAt(i);  //接下来就是取record里面的字段了 
//			//比如取name字段  
//			var name = record.data.name;  //....  //接下来就是保存操作..... }}
//		}
		var selFuns = [];          
		ds.each(function(rec){
			//alert(rec.data);
			//alert(Ext.util.JSON.encode(rec.data));
			//selFuns.push(Ext.util.JSON.encode(rec.data));
			//alert("11========="+Ext.util.JSON.encode(rec.data));
			save(Ext.util.JSON.encode(rec.data));
		});
		//alert(selFuns);
		

		
	
	function save(selFuns){
		Ext.Ajax.request({
			url : '../dataImport/save.shtml',
			method : 'POST',
			params : {
				datas : selFuns
			},
			scope : this,
			success : function(response) {
				var resultArray = Ext.util.JSON.decode(response.responseText);
				//store.reload();
				Ext.Msg.alert('提示', resultArray.msg);
			},

			failure : function(response) {
				var resultArray = Ext.util.JSON.decode(response.responseText);
				Ext.Msg.alert('提示', resultArray.msg);
			}

		});

	}
	}
	
});
