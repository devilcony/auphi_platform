TransGraph = Ext.extend(BaseGraph, {
	iconCls: 'trans',
	saveUrl: GetUrl('trans/save.do'),
	sqlUrl: GetUrl('trans/getSQL.do'),
	runDialog: 'TransExecutionConfigurationDialog',
	
	initComponent: function() {
		this.resultPanel = new TransResult({hidden: !this.showResult});
		TransGraph.superclass.initComponent.call(this);
		
		this.on('load', function() {
			var graph = this.getGraph();
			var root = graph.getDefaultParent();
			Ext.each(graph.getChildCells(root, true, false), function(cell) {
				this.showPartitioning(cell);
				
				if(cell.getAttribute('cluster_schema')) {
					this.showCluster(cell);
				}	
			}, this);
			
			var exist = function(name, inc) {
				var cells = graph.getChildCells(graph.getDefaultParent(), true, false);
				for(var i=0; i<cells.length; i++)
					if(cells[i].getAttribute('label') == name && inc != 1)
						return true;
				
				return false;
			};
			
			graph.addListener(mxEvent.CELLS_ADDED, function(sender, evt){  
				var cells = evt.getProperty('cells');
				Ext.each(cells, function(cell) {
					if(cell.isVertex() && cell.value) {
						var name = cell.getAttribute('label'), inc = 1;
						do {
							if(exist(name, inc) === false) {
								cell.setAttribute('label', name);
								break;
							} else
								inc++;
							name = cell.getAttribute('label') + inc;
						} while(true)
					}
				});
			});
			
			var me = this;
			graph.getModel().addListener(mxEvent.CHANGE, function(sender, evt){  
				Ext.each(evt.getProperty('edit').changes, function(change) {
					if (change.constructor == mxCellAttributeChange && change.cell != null)    {
						var cell = change.cell, root = graph.getDefaultParent();
						if(cell.getId() == root.getId()) {
							me.getClusterSchemaStore();
							me.getPartitionSchemaStore();
						}
					}
				});
			});
			
		}, this);
	},
	
	check: function() {
		var checkResultDialog = new CheckResultDialog();
		checkResultDialog.show();
	},
	
	clusterSchema: function() {
		var dialog = new ClusterSchemaDialog();
		dialog.show();
	},
	
	partitionSchema: function() {
		var dialog = new PartitionSchemaDialog();
		dialog.show();
	},
	
	initContextMenu: function(menu, cell, evt) {
		var graph = this.getGraph(), me = this;
		if(cell == null) {
			menu.addItem('新建注释', null, function(){alert(1);}, null, null, true);
			menu.addItem('从剪贴板粘贴步骤', null, function(){alert(1);}, null, null, true);
			menu.addSeparator(null);
			menu.addItem('全选', null, function(){me.getGraph().selectVertices();}, null, null, true);
			menu.addItem('清除选择', null, function(){me.getGraph().clearSelection();}, null, null, true);
			menu.addSeparator(null);
			menu.addItem('查看图形文件', null, function(){
				var dialog = new TextAreaDialog();
				dialog.show(null, function() {
					dialog.initData(me.toXml());
				});
			}, null, null, true);
			menu.addItem('查看引擎文件', null, function(){
				Ext.Ajax.request({
					url: GetUrl('trans/engineXml.do'),
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
			menu.addItem('转换设置', null, function() {
				var transDialog = new TransDialog();
				transDialog.show();
			}, null, null, true);
		} else if(cell.isVertex()) {
			menu.addItem('编辑步骤', null, function() {me.editCell(cell);}, null, null, true);
			menu.addItem('编辑步骤描述', null, function(){alert(1);}, null, null, true);
			menu.addSeparator(null);
			var sendMethod = menu.addItem('数据发送......', null, null, null, null, true);
			
			var text = 'Round-Robin', text1 = '复制发送模式';
			if(cell.getAttribute('distribute') == 'Y')
				text = '[√]Round-Robin';
			else
				text1 = '[√]复制发送模式';
			
			menu.addItem(text, null, function() {
				graph.getModel().beginUpdate();
		        try
		        {
					var edit = new mxCellAttributeChange(cell, 'distribute', 'Y');
	            	graph.getModel().execute(edit);
		        } finally
		        {
		            graph.getModel().endUpdate();
		        }
		        me.setDistribute(cell);
			}, sendMethod, null, true);
			menu.addItem(text1, null, function() {
				graph.getModel().beginUpdate();
		        try
		        {
					var edit = new mxCellAttributeChange(cell, 'distribute', 'N');
	            	graph.getModel().execute(edit);
		        } finally
		        {
		            graph.getModel().endUpdate();
		        }
		        me.setDistribute(cell);
			}, sendMethod, null, true);
			
			menu.addItem('改变开始复制的数量...', null, this.changeCopies.createDelegate(this, [cell]), null, null, true);
			menu.addSeparator(null);
			menu.addItem('复制到剪贴板', null, function(){
				mxClipboard.copy(graph);
			}, null, null, true);
			menu.addItem('复制步骤', null, function(){
				mxClipboard.copy(graph);
				mxClipboard.paste(graph);
			}, null, null, true);
			menu.addItem('删除步骤', null, function(){
				graph.removeCells();
			}, null, null, true);
			menu.addItem('隐藏步骤', null, function(){alert(1);}, null, null, false);
			menu.addItem('分离步骤', null, function(){alert(1);}, null, null, true);
			menu.addSeparator(null);
			menu.addItem('显示输入字段', null, function(){
				var stepFieldsDialog = new StepFieldsDialog({before: true});
				stepFieldsDialog.show();
			}, null, null, true);
			menu.addItem('显示输出字段', null, function(){
				var stepFieldsDialog = new StepFieldsDialog({before: false});
				stepFieldsDialog.show();
			}, null, null, true);
			menu.addSeparator(null);
			
			menu.addItem('定义错误处理', null, function(){
				var dialog = new StepErrorMetaDialog();
				dialog.on('ok', function(data) {
					graph.getModel().beginUpdate();
			        try
			        {
						var edit = new mxCellAttributeChange(cell, 'error', Ext.encode(data));
		            	graph.getModel().execute(edit);
			        } finally
			        {
			            graph.getModel().endUpdate();
			        }
			        me.showError(cell);
			        dialog.close();
				});
				dialog.show();
			}, null, null, cell.getAttribute('supports_error_handing') == 'Y');
			
			menu.addSeparator(null);
			menu.addItem('分区', null, function(){
				var dialog = new SelectPartitionDialog();
				dialog.on('ok', function(data) {
					graph.getModel().beginUpdate();
			        try
			        {
						var edit = new mxCellAttributeChange(cell, 'partitioning', Ext.encode(data));
		            	graph.getModel().execute(edit);
			        } finally
			        {
			            graph.getModel().endUpdate();
			        }
			        
			        me.showPartitioning(cell);
				});
				dialog.show(null, function() {
					dialog.initData(Ext.decode(cell.getAttribute('partitioning')));
				});
			}, null, null, this.getPartitionSchemaStore().getCount() > 0);
			
			menu.addItem('集群', null, function(){
				var availableClusters =  new ListView({
					valueField: 'name',
					store: me.getClusterSchemaStore(),
					columns: [{
						width: 1, dataIndex: 'name'
					}]
				});
				
				var win = new Ext.Window({
					width: 200,
					height: 300,
					title: '集群选择',
					layout: 'fit',
					modal: true,
					items: availableClusters,
					bbar: ['->', {
						text: '确定', handler: function() {
							if(!Ext.isEmpty(availableClusters.getValue())) {
								graph.getModel().beginUpdate();
						        try
						        {
									var edit = new mxCellAttributeChange(cell, 'cluster_schema', availableClusters.getValue());
					            	graph.getModel().execute(edit);
						        } finally
						        {
						            graph.getModel().endUpdate();
						        }
								
								me.showCluster(cell);
								win.close();
							}
						}
					}]
				});
				
				win.show();
			}, null, null, true);
		} else if(cell.isEdge()) {
			menu.addItem('编辑连接', null, function(){alert(1);}, null, null, true);
			menu.addItem('使节点连接失效', null, function(){ }, null, null, true);
			menu.addItem('删除节点连接', null, function(){
				graph.removeCells();
			}, null, null, true);
			menu.addItem('翻转方向', null, function(){ }, null, null, true);
		}
	},
	
	onClusterSchemaMerge: function(json) {
		var graph = this.getGraph();
		var root = graph.getDefaultParent();
		var clusterSchemas = root.getAttribute('clusterSchemas');
		var jsonArray = Ext.decode(clusterSchemas);
		
		if(jsonArray.length == 0) {
			jsonArray.push(json);
		} else {
			Ext.each(jsonArray, function(item, index) {
				if(item.name == json.name) {
					jsonArray.splice(index, 1, json);
				} else {
					if(index == jsonArray.length - 1)
						jsonArray.push(json);
				}
			});
		}
		
		graph.getModel().beginUpdate();
        try
        {
			var edit = new mxCellAttributeChange(root, 'clusterSchemas', Ext.encode(jsonArray));
        	graph.getModel().execute(edit);
        } finally
        {
            graph.getModel().endUpdate();
        }
	},
	
	onClusterSchemaDel: function(name) {
		var graph = this.getGraph();
		var root = graph.getDefaultParent();
		var clusterSchemas = root.getAttribute('clusterSchemas');
		var jsonArray = Ext.decode(clusterSchemas);
		
		Ext.each(jsonArray, function(item, index) {
			if(item.name == name) {
				jsonArray.splice(index, 1);
				return false;
			}
		});
		
		graph.getModel().beginUpdate();
        try
        {
			var edit = new mxCellAttributeChange(root, 'clusterSchemas', Ext.encode(jsonArray));
        	graph.getModel().execute(edit);
        } finally
        {
            graph.getModel().endUpdate();
        }
	},
	
	getClusterSchemaStore: function() {
		if(!this.clusterSchemaStore) {
			this.clusterSchemaStore = new Ext.data.JsonStore({
				idProperty: 'name',
				fields: ['name', 'base_port', 'sockets_buffer_size', 'sockets_flush_interval', 'sockets_compressed', 'dynamic', 'slaveservers']
			});
		}
		var graph = this.getGraph();
		var cell = graph.getDefaultParent(), data = [];
		if(cell.getAttribute('clusterSchemas') != null)
			data = Ext.decode(cell.getAttribute('clusterSchemas'));
		this.clusterSchemaStore.loadData(data);
		
		return this.clusterSchemaStore;
	},
	
	onPartitionSchemaMerge: function(json) {
		var graph = this.getGraph();
		var root = graph.getDefaultParent();
		var partitionSchemas = root.getAttribute('partitionSchemas');
		var jsonArray = Ext.decode(partitionSchemas);
		
		if(jsonArray.length == 0) {
			jsonArray.push(json);
		} else {
			Ext.each(jsonArray, function(item, index) {
				if(item.name == json.name) {
					jsonArray.splice(index, 1, json);
				} else {
					if(index == jsonArray.length - 1)
						jsonArray.push(json);
				}
			});
		}
		
		graph.getModel().beginUpdate();
        try
        {
			var edit = new mxCellAttributeChange(root, 'partitionSchemas', Ext.encode(jsonArray));
        	graph.getModel().execute(edit);
        } finally
        {
            graph.getModel().endUpdate();
        }
	},
	
	getPartitionSchemaStore: function() {
		if(!this.partitionSchemaStore) {
			this.partitionSchemaStore = new Ext.data.JsonStore({
				idProperty: 'name',
				fields: ['name', 'dynamic', 'partitions_per_slave', 'partition']
			});
		}
		var graph = this.getGraph();
		var cell = graph.getDefaultParent(), data = [];
		if(cell.getAttribute('partitionSchemas') != null)
			data = Ext.decode(cell.getAttribute('partitionSchemas'));
		
		this.partitionSchemaStore.loadData(data);
		
		return this.partitionSchemaStore;
	},
	
//	cellAdded: function(graph, child) {
//		this.showPartitioning(child);
//		
//		if(child.getAttribute('cluster_schema')) {
//			this.showCluster(child);
//		}		
//	},
	
	changeCopies: function(cell) {
		var graph = this.getGraph();
		
		Ext.MessageBox.prompt('步骤复制的数量...', '复制的数量（1或更多）：', function(btn, text) {
			if(btn == 'ok' && text != '') {
				var num = parseInt(text);
				if(num > 0) {
					graph.getModel().beginUpdate();
					try {
						var edit = new mxCellAttributeChange(cell, 'copies', num);
						graph.getModel().execute(edit);
						this.showPartitioning(cell);
					} finally {
						graph.getModel().endUpdate();
					}							
				}
			}
		}, this);
	},
	
	newStep: function(node, x, y, w, h) {
		var graph = this.getGraph();
		Ext.Ajax.request({
			url: GetUrl('trans/newStep.do'),
			params: {graphXml: this.toXml(), pluginId: node.attributes.pluginId, name: node.text},
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
				graph.container.focus();
			}
		});
	},

	newHop: function(edge) {
		var graph = this.getGraph(), found = false;
		var sourceCell = graph.getModel().getCell( edge.source.getId() );
		Ext.each(graph.getOutgoingEdges(sourceCell), function(outgoingEdge) {
			if(outgoingEdge.target.getId() == edge.target.getId() && outgoingEdge.getId() != edge.getId()) {
				found = true;
				return false;
			}
		});
		
		if(found === true) {
			graph.removeCells([edge]);
			return;
		}
		
		var doc = mxUtils.createXmlDocument();
		var hop = doc.createElement('TransHop');
		
		hop.setAttribute('from', edge.source.getAttribute('label'));
		hop.setAttribute('to', edge.target.getAttribute('label'));
		hop.setAttribute('enable', 'Y');
		edge.setValue(hop);
		
	},
	
	showError: function(cell) {
		var error = cell.getAttribute('error');
		error = error ? Ext.decode(error) : {};
		
		var graph = this.getGraph();
		Ext.each(graph.getOutgoingEdges(cell), function(edge) {
			graph.getModel().beginUpdate();
	        try
	        {
	        	if(error.is_enabled == 'Y' && error.target_step == edge.target.getAttribute('label')) {
	        		var edit = new mxCellAttributeChange(edge, 'label', kettle.imageFalse);
		        	graph.getModel().execute(edit);
		        	edge.setStyle('error');
	        	} else {
	        		var edit = new mxCellAttributeChange(edge, 'label', null);
		        	graph.getModel().execute(edit);
		        	edge.setStyle(null);
	        	}
	        } finally
	        {
	            graph.getModel().endUpdate();
	        }
			
		});
	},
	
	setDistribute: function(cell) {
		var error = cell.getAttribute('error');
		error = error ? Ext.decode(error) : {};
		
		var graph = this.getGraph();
		Ext.each(graph.getOutgoingEdges(cell), function(edge) {
			if(!(error.is_enabled == 'Y' && error.target_step == edge.target.getAttribute('label'))) {
				graph.getModel().beginUpdate();
		        try
		        {
		        	if(cell.getAttribute('distribute') == 'N') {
		        		var edit = new mxCellAttributeChange(edge, 'label', kettle.imageCopyHop);
			        	graph.getModel().execute(edit);
		        	} else {
		        		var edit = new mxCellAttributeChange(edge, 'label', null);
			        	graph.getModel().execute(edit);
		        	}
		        } finally
		        {
		            graph.getModel().endUpdate();
		        }
        	}
		}, this);
	},
	
	showPartitioning: function(cell) {
		var graph = this.getGraph();
		var overlays = graph.getCellOverlays(cell) || [];
		for(var i=0; i<overlays.length; i++) {
			var overlay = overlays[i];
			
			if(overlay.align == mxConstants.ALIGN_LEFT && overlay.verticalAlign == mxConstants.ALIGN_TOP) {
				graph.removeCellOverlay(cell, overlay);
			}
		}
		
		var partitioning = Ext.decode(cell.getAttribute('partitioning'));
		if(partitioning.method != 'none') {
			this.getPartitionSchemaStore().each(function(rec) {
				if(rec.get('name') == partitioning.schema_name) {
					var copies = rec.get('partition').length;
					var text = (rec.get('dynamic') == 'Y' ? 'D' : 'P') + 'x' + copies;
					var name = rec.get('name'), url = GetUrl('ui/text2image/partition.do?text=' + text);
					
					Ext.Ajax.request({
						url: GetUrl('ui/text2image/width.do?text=' + text),
						method: 'GET',
						success: function(response) {
							var w = parseInt(response.responseText);
							
							var offset = new mxPoint(0, -16);
							var overlay = new mxCellOverlay(new mxImage(url, w + 4, 12 + 4), name, mxConstants.ALIGN_LEFT, mxConstants.ALIGN_TOP, offset);
							graph.addCellOverlay(cell, overlay);
						}
					});
				}
			});
		} else {
			this.showCopies(cell);
		}
	},
	
	showCopies: function(cell) {
		var graph = this.getGraph();
		var overlays = graph.getCellOverlays(cell) || [];
		for(var i=0; i<overlays.length; i++) {
			var overlay = overlays[i];
			
			if(overlay.align == mxConstants.ALIGN_LEFT && overlay.verticalAlign == mxConstants.ALIGN_TOP) {
				graph.removeCellOverlay(cell, overlay);
			}
		}
		var copies = parseInt(cell.getAttribute('copies'));
		if(copies > 1) {
			Ext.Ajax.request({
				url: 'ui/text2image/width.do?text=X' + cell.getAttribute('copies'),
				method: 'GET',
				success: function(response) {
					var w = parseInt(response.responseText);
					
					var offset = new mxPoint(0, -10);
					var overlay = new mxCellOverlay(new mxImage('ui/text2image.do?text=X' + cell.getAttribute('copies'), w, 12), 'update: ', mxConstants.ALIGN_LEFT, mxConstants.ALIGN_TOP, offset);
					graph.addCellOverlay(cell, overlay);
				}
			});
		}
	},
	
	showCluster: function(cell) {
		var graph = this.getGraph();
		var overlays = graph.getCellOverlays(cell) || [];
		for(var i=0; i<overlays.length; i++) {
			var overlay = overlays[i];
			
			if(overlay.align == mxConstants.ALIGN_RIGHT && overlay.verticalAlign == mxConstants.ALIGN_TOP) {
				graph.removeCellOverlay(cell, overlay);
			}
		}
		var cluster_schema = cell.getAttribute('cluster_schema');
		if(cluster_schema) {
			Ext.Ajax.request({
				url: 'ui/text2image/width.do?text=' + cluster_schema,
				method: 'GET',
				success: function(response) {
					var w = parseInt(response.responseText);
					
					var offset = new mxPoint(0, -10);
					var overlay = new mxCellOverlay(new mxImage('ui/text2image.do?text=' + cluster_schema, w, 12), 'update: ', mxConstants.ALIGN_RIGHT, mxConstants.ALIGN_TOP, offset);
					graph.addCellOverlay(cell, overlay);
				}
			});
		}
	},
	
	inputFields: function(stepName) {
		var graph = this.getGraph();
		var store = new Ext.data.JsonStore({
			fields: ['name', 'type', 'length', 'precision', 'origin', 'storageType', 'conversionMask', 'currencySymbol', 'decimalSymbol', 'groupingSymbol', 'trimType', 'comments'],
			proxy: new Ext.data.HttpProxy({
				url: GetUrl('trans/inputOutputFields.do'),
				method: 'POST'
			})
		});
		
		store.on('loadexception', function(misc, s, response) {
			failureResponse(response);
		});
		
		
		store.baseParams.stepName = encodeURIComponent(stepName);
		store.baseParams.graphXml = this.toXml();
		store.baseParams.before = true;
		
		return store;
	},
	
	outputFields: function(stepName) {
		var graph = this.getGraph();
		var store = new Ext.data.JsonStore({
			fields: ['name', 'type', 'length', 'precision', 'origin', 'storageType', 'conversionMask', 'currencySymbol', 'decimalSymbol', 'groupingSymbol', 'trimType', 'comments'],
			proxy: new Ext.data.HttpProxy({
				url: GetUrl('trans/inputOutputFields.do'),
				method: 'POST'
			})
		});
		
		store.on('loadexception', function(misc, s, response) {
			failureResponse(response);
		});
		
		
		store.baseParams.stepName = encodeURIComponent(stepName);
		store.baseParams.graphXml = this.toXml();
		store.baseParams.before = false;
		
		return store;
	},
	
	inputOutputFields: function(stepName, before, cb) {
		var graph = this.getGraph();
		var store = new Ext.data.JsonStore({
			fields: ['name', 'type', 'length', 'precision', 'origin', 'storageType', 'conversionMask', 'currencySymbol', 'decimalSymbol', 'groupingSymbol', 'trimType', 'comments'],
			proxy: new Ext.data.HttpProxy({
				url: GetUrl('trans/inputOutputFields.do'),
				method: 'POST'
			})
		});
		
		store.on('loadexception', function(misc, s, response) {
			failureResponse(response);
		});
		
		store.on('load', function() {
			if(Ext.isFunction(cb))
				cb(store);
		});
		
		store.baseParams.stepName = encodeURIComponent(stepName);
		store.baseParams.graphXml = this.toXml();
		store.baseParams.before = before;
		store.load();
		
		return store;
	},
	
	nextSteps: function(stepName, cb) {
		var graph = this.getGraph();
		var store = new Ext.data.JsonStore({
			fields: ['name'],
			proxy: new Ext.data.HttpProxy({
				url: GetUrl('trans/nextSteps.do'),
				method: 'POST'
			})
		});
		
		store.on('loadexception', function(misc, s, response) {
			failureResponse(response);
		});
		
		store.on('load', function() {
			if(Ext.isFunction(cb))
				cb(store);
		});
		
		store.baseParams.stepName = encodeURIComponent(stepName);
		store.baseParams.graphXml = this.toXml();
		
		return store;
	},
	
	previousSteps: function(stepName, cb) {
		var graph = this.getGraph();
		var store = new Ext.data.JsonStore({
			fields: ['name'],
			proxy: new Ext.data.HttpProxy({
				url: GetUrl('trans/previousSteps.do'),
				method: 'POST'
			})
		});
		
		store.on('loadexception', function(misc, s, response) {
			failureResponse(response);
		});
		
		store.on('load', function() {
			if(Ext.isFunction(cb))
				cb(store);
		});
		
		store.baseParams.stepName = encodeURIComponent(stepName);
		store.baseParams.graphXml = this.toXml();
		
		return store;
	},
	
	updateStatus: function(status) {
		var graph = this.getGraph();
		
		for(var i=0; i<status.length; i++) {
			var cells = graph.getModel().getChildCells(graph.getDefaultParent(), true, false);
			for(var j=0; j<cells.length; j++) {
				var cell = cells[j];
				if(cell.getAttribute('label') == status[i].stepName) {
					var overlays = graph.getCellOverlays(cell) || [];
					for(var k=0; k<overlays.length; k++) {
						var overlay = overlays[k];
						
						if(overlay.align == mxConstants.ALIGN_RIGHT && overlay.verticalAlign == mxConstants.ALIGN_TOP
								&& overlay.offset.x == 0 && overlay.offset.y == 0) {
							graph.removeCellOverlay(cell, overlay);
						}
					}
					
					if(status[i].stepStatus > 0) {
						var overlay = new mxCellOverlay(new mxImage('ui/images/false.png', 16, 16), status[i].logText, mxConstants.ALIGN_RIGHT, mxConstants.ALIGN_TOP);
						graph.addCellOverlay(cell, overlay);
					} else {
						var overlay = new mxCellOverlay(new mxImage('ui/images/true.png', 16, 16), null, mxConstants.ALIGN_RIGHT, mxConstants.ALIGN_TOP);
						graph.addCellOverlay(cell, overlay);
					}
					break;
				}
			}
		}
	}
});

Ext.reg('TransGraph', TransGraph);