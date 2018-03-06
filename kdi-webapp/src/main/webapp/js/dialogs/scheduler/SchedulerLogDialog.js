SchedulerLogDialog = Ext.extend(Ext.Window, {
	title: '历史日志',
	width: 900,
	height: 500,
	layout: 'fit',
	modal: true,
	initComponent: function() {
		
		var store = new Ext.data.JsonStore( {
			fields : [ 'fireId', 'jobName', 'startTime', 'endTime','execMethod', 'status', 'caozuo' ],
			baseParams: {jobName: this.jobName},
			url : GetUrl('schedule/list.do')
		});
		
		var renderCaozuo = function(v, m, record) {
			var str = '<a href="javascript:showDetails(\'' + record.get('fireId') + '\');">查看详细</a>';
			return str;
		};
		
		var grid = this.items = new Ext.grid.GridPanel({
			border: false,
			store : store,
			columns : [ 
			   {header : '', dataIndex : 'fireId', width : 140, hidden : true}, 
	           {header : '任务名', dataIndex : 'jobName', width : 200}, 
	           {header : '开始时间', dataIndex : 'startTime', width : 180}, 
	           {header : '完成时间', dataIndex : 'endTime', width : 180}, 
	           {header : '执行方式', dataIndex : 'execMethod', width : 100}, 
	           {header : '状态', dataIndex : 'status', width : 70},
	           {header : '', dataIndex : 'caozuo', width : 120, renderer : renderCaozuo}
	           ]
			});
		
		store.load();
			
		SchedulerLogDialog.superclass.initComponent.call(this);
	}
});

function showDetails(fireId)
{
	Ext.Ajax.request({
		url: GetUrl('schedule/logDetail.do'),
		method: 'POST',
		params: {fireId: fireId},
		success: function(response) {
			var dialog = new LogDetailDialog({data: Ext.decode(response.responseText)});
			dialog.show();
		}
	});
}

LogDetailDialog = Ext.extend(Ext.Window, {
	title: '执行日志',
	width: 900,
	height: 600,
	layout: 'fit',
	modal: true,
	initComponent: function() {
		var graphPanel = Ext.create({border: false, readOnly: true, showResult: true}, this.data.GraphType);
		
		var xmlDocument = mxUtils.parseXml(decodeURIComponent(this.data.graphXml));
		var decoder = new mxCodec(xmlDocument);
		var node = xmlDocument.documentElement;
		
		this.items = graphPanel;
		this.on('afterrender', function() {
			var graph = graphPanel.getGraph();
			decoder.decode(node, graph.getModel());
			
			graphPanel.setTitle(graph.getDefaultParent().getAttribute('name'));
			
			graphPanel.loadLocal(Ext.decode(this.data.executionLog));
		}, this);
		
		LogDetailDialog.superclass.initComponent.call(this);
	}
});