JobGraph = Ext.extend(BaseGraph, {
	iconCls: 'job',
	saveUrl: GetUrl('job/save.do'),
	sqlUrl: GetUrl('job/getSQL.do'),
	runDialog: 'JobExecutionConfigurationDialog',
	
	initComponent: function() {
		this.resultPanel = new JobResult({hidden: !this.showResult});
		JobGraph.superclass.initComponent.call(this);
		
		
		this.on('load', function() {
			var graph = this.getGraph();
			var root = graph.getDefaultParent();
			
			graph.getModel().addListener(mxEvent.CHANGE, function(sender, evt){  
				Ext.each(evt.getProperty('edit').changes, function(change) {
					if (change.constructor == mxCellAttributeChange && change.cell != null)    {
						var cell = change.cell;
						if(cell.isVertex() && cell.value.nodeName == 'JobEntry' && change.attribute == 'label') {
							
							graph.getModel().beginUpdate();
					        try
					        {
					        	
					        	Ext.each(graph.getOutgoingEdges(cell), function(edge) {
					        		var edit = new mxCellAttributeChange(edge, 'from', change.value);
						        	graph.getModel().execute(edit);
								});
					        	
					        	Ext.each(graph.getIncomingEdges(cell), function(edge) {
					        		var edit = new mxCellAttributeChange(edge, 'to', change.value);
						        	graph.getModel().execute(edit);
								});
					        } finally
					        {
					            graph.getModel().endUpdate();
					        }
						}
					}
				});
			});
			
		}, this);
	},
	
	initContextMenu: function(menu, cell, evt) {
		var graph = this.getGraph(), me = this;
		
		if(cell == null) {
			menu.addItem('新建注释', null, function(){alert(1);}, null, null, true);
			menu.addItem('从剪贴板粘贴步骤', null, function(){alert(1);}, null, null, !mxClipboard.isEmpty());
			menu.addSeparator(null);
			menu.addItem('全选', null, function(){me.getGraph().selectVertices();}, null, null, true);
			menu.addItem('清除选择', null, function(){me.getGraph().clearSelection();}, null, null, !graph.isSelectionEmpty());
			menu.addSeparator(null);
			menu.addItem('查看图形文件', null, function(){
				var dialog = new TextAreaDialog();
				dialog.show(null, function() {
					dialog.initData(me.toXml());
				});
			}, null, null, true);
			menu.addItem('查看引擎文件', null, function(){
				Ext.Ajax.request({
					url: GetUrl('job/engineXml.do'),
					params: {graphXml: me.toXml()},
					method: 'POST',
					success: function(response) {
						var dialog = new TextAreaDialog();
						dialog.show(null, function() {
							dialog.initData(response.responseText);
						});
					}
				});
			}, null, null, true);
			menu.addSeparator(null);
			menu.addItem('作业设置', null, function() {
				var transDialog = new TransDialog();
				transDialog.show();
			}, null, null, true);
		} else if(cell.isVertex()) {
			menu.addItem('新节点', null, function(){alert(1);}, null, null, true);
			menu.addItem('编辑作业入口', null, function(){
				me.editCell(cell);
			}, null, null, true);
			menu.addItem('编辑作业入口描述信息', null, function(){alert(1);}, null, null, true);
			menu.addSeparator(null);
			menu.addItem('复制被选择的作业入口到剪贴板', null, function(){mxClipboard.copy(graph);}, null, null, true);
			menu.addItem('复制作业入口', null, function(){mxClipboard.copy(graph);mxClipboard.paste(graph);}, null, null, true);
			menu.addItem('删除所有该作业入口的副本', null, function(){graph.removeCells();}, null, null, true);
			menu.addItem('隐藏作业入口', null, function(){alert(1);}, null, null, true);
			menu.addItem('拆开节点', null, function(){alert(1);}, null, null, true);
			menu.addSeparator(null);
			
			var text = 'Run Next Entries in Parallel';
			if('Y' == cell.getAttribute('parallel'))
				text = "[√]Run Next Entries in Parallel";
			
			menu.addItem(text, null, function(){
				graph.getModel().beginUpdate();
		        try
		        {
		        	var edit = new mxCellAttributeChange(cell, 'parallel', 'Y' == cell.getAttribute('parallel') ? 'N' : "Y");
		        	graph.getModel().execute(edit);
		        	
		        	var edges = graph.getOutgoingEdges(cell);
		        	for(var i=0; i<edges.length; i++) {
		        		var edge = edges[i];
		        		
		        		var label = [];
						if(edge.getAttribute('unconditional') == 'Y')
							label.push(kettle.imageUnconditionalHop);
						else if(edge.getAttribute('evaluation') == 'Y')
							label.push(kettle.imageTrue);
						else
							label.push(kettle.imageFalse);
						
						if(cell.getAttribute('parallel') == 'Y')
							label.push(kettle.imageParallelHop);
						
						var edit = new mxCellAttributeChange(edge, 'label', Ext.encode(label));
			        	graph.getModel().execute(edit); 
		        	}
		        } finally
		        {
		            graph.getModel().endUpdate();
		        }
		        
			}, null, null, true);
		} else if(cell.isEdge()) {
			var submenu = menu.addItem('评价', null, null, null, null, true);
			
			var text = '无条件的', text1 = '当结果为真的时候继续下一步', text2 = '当结果为假的时候继续下一步';
			if('Y' == cell.getAttribute('unconditional'))
				text = "[√]无条件的";
			else if('Y' == cell.getAttribute('evaluation'))
				text1 = "[√]当结果为真的时候继续下一步";
			else
				text2 = "[√]当结果为假的时候继续下一步";
			
			menu.addItem(text, null, function() {
				if(cell.getAttribute('unconditional') == 'Y')
					return;
				
				graph.getModel().beginUpdate();
		        try
		        {
		        	var edit = new mxCellAttributeChange(cell, 'unconditional', 'Y');
		        	graph.getModel().execute(edit);
		        	
		    		var label = [kettle.imageUnconditionalHop];
					if(cell.source.getAttribute('parallel') == 'Y')
						label.push(kettle.imageParallelHop);
					
					var edit = new mxCellAttributeChange(cell, 'label', Ext.encode(label));
		        	graph.getModel().execute(edit); 
		        } finally
		        {
		            graph.getModel().endUpdate();
		        }
			}, submenu, null, true);
			menu.addItem(text1, null, function() {
				if(cell.getAttribute('evaluation') == 'Y' && cell.getAttribute('unconditional') == 'N')
					return;
				
				graph.getModel().beginUpdate();
		        try
		        {
		        	var edit = new mxCellAttributeChange(cell, 'evaluation', 'Y');
		        	graph.getModel().execute(edit);
		        	
		        	var edit = new mxCellAttributeChange(cell, 'unconditional', 'N');
		        	graph.getModel().execute(edit);
		        	
		    		var label = [kettle.imageTrue];
					if(cell.source.getAttribute('parallel') == 'Y')
						label.push(kettle.imageParallelHop);
					
					var edit = new mxCellAttributeChange(cell, 'label', Ext.encode(label));
		        	graph.getModel().execute(edit); 
		        } finally
		        {
		            graph.getModel().endUpdate();
		        }
			}, submenu, null, cell.source.getAttribute('start') != 'Y');
			menu.addItem(text2, null, function() {
				if(cell.getAttribute('unconditional') == 'N' && cell.getAttribute('evaluation') == 'N')
					return;
				
				if(name != null) {
					graph.getModel().beginUpdate();
			        try
			        {
			        	var edit = new mxCellAttributeChange(cell, 'unconditional', 'N');
			        	graph.getModel().execute(edit);
			        	
			        	var edit = new mxCellAttributeChange(cell, 'evaluation', 'N');
			        	graph.getModel().execute(edit);
			        	
			    		var label = [kettle.imageFalse];
						if(cell.source.getAttribute('parallel') == 'Y')
							label.push(kettle.imageParallelHop);
						
						var edit = new mxCellAttributeChange(cell, 'label', Ext.encode(label));
			        	graph.getModel().execute(edit);
			        	
			        	cell.setStyle('error');
			        } finally
			        {
			            graph.getModel().endUpdate();
			        }
				}
			}, submenu, null, cell.source.getAttribute('start') != 'Y');
			
			menu.addItem('使节点连接失效', null, function(){ }, null, null, true);
			menu.addItem('删除节点连接', null, function(){
				graph.removeCells();
			}, null, null, true);
			menu.addItem('指出方向', null, function(){}, null, null, true);
		}
	},
	
	newStep: function(node, x, y, w, h) {
		var graph = this.getGraph();
		Ext.Ajax.request({
			url: GetUrl('job/newJobEntry.do'),
			params: {graphXml: this.toXml(), pluginId: node.attributes.pluginId, name: encodeURIComponent(node.text)},
			method: 'POST',
			success: function(response) {
				var doc = response.responseXML;
         		graph.getModel().beginUpdate();
				try
				{
					var cell = graph.insertVertex(graph.getDefaultParent(), null, doc.documentElement, x, y, w, h, "icon;image=" + node.attributes.dragIcon);
					graph.setSelectionCells([cell]);
				} finally
				{
					graph.getModel().endUpdate();
				}
			}
		});
	},
	
	newHop: function(cell) {
		var doc = mxUtils.createXmlDocument();
		var hop = doc.createElement('JobHop');
		
		hop.setAttribute('from', cell.source.getAttribute('label'));
		hop.setAttribute('to', cell.target.getAttribute('label'));
		hop.setAttribute('from_nr', cell.source.getAttribute('nr'));
		hop.setAttribute('to_nr', cell.target.getAttribute('nr'));
		cell.setValue(hop);
		
		var graph = this.getGraph();
		
		graph.getModel().beginUpdate();
        try
        {
        	var edit = new mxCellAttributeChange(cell, 'enabled', 'Y');
        	graph.getModel().execute(edit);
        	edit = new mxCellAttributeChange(cell, 'evaluation', 'Y');
        	graph.getModel().execute(edit);
        	if(cell.source.getAttribute('ctype') == 'SPECIAL' && cell.source.getAttribute('start') == 'Y') {
        		edit = new mxCellAttributeChange(cell, 'unconditional', 'Y');
            	graph.getModel().execute(edit);
        	} else {
        		edit = new mxCellAttributeChange(cell, 'unconditional', 'N');
            	graph.getModel().execute(edit);
        	}
        } finally
        {
            graph.getModel().endUpdate();
        }
	},
	
	getEntries: function(cb) {
		var store = new Ext.data.JsonStore({
			idProperty: 'name',
			fields: ['name'],
			proxy: new Ext.data.HttpProxy({
				url: GetUrl('job/entries.do'),
				method: 'POST'
			})
		});
		
		store.on('load', function() {
			if(Ext.isFunction(cb))
				cb(store);
		});
		
		store.baseParams.graphXml = this.toXml();
		store.load();
		
		return store;
	}
	
});

Ext.reg('JobGraph', JobGraph);
