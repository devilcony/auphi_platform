GuidePanel = Ext.extend(Ext.TabPanel, {
	activeTab: 0,
	plain: true,
	
	initComponent: function() {
		var me = this;
		
		var transTree = new Ext.tree.TreePanel({
			title: '核心对象',
			useArrows: true,
			root: new Ext.tree.AsyncTreeNode({text: 'root'}),
			loader: new Ext.tree.TreeLoader({
				dataUrl: GetUrl('system/steps.do')
			}),
			enableDD:true,
			ddGroup:'TreePanelDDGroup',
			autoScroll: true,
			animate: false,
			rootVisible: false
		});
		
		var jobTree = new Ext.tree.TreePanel({
			title: '核心对象',
			useArrows: true,
			root: new Ext.tree.AsyncTreeNode({text: 'root'}),
			loader: new Ext.tree.TreeLoader({
				dataUrl: GetUrl('system/jobentrys.do')
			}),
			enableDD:true,
			ddGroup:'TreePanelDDGroup',
			autoScroll: true,
			animate: false,
			rootVisible: false
		});
		
		this.activeCom = function(item) {
			this.remove(transTree, false);
			this.remove(jobTree, false);
			jobTree.hide();
			transTree.hide();
			
			if(item && item.getXType() == 'JobGraph') {
				jobTree.show();
				this.add(jobTree);
				this.setActiveTab(jobTree.getId());
			} else if(item && item.getXType() == 'TransGraph') {
				transTree.show();
				this.add(transTree);
				this.setActiveTab(transTree.getId());
			}
		};
		
	    jobTree.on("nodedragover", function(e){
	    	return false;
	    }); 
	    
	    transTree.on("nodedragover", function(e){
	    	return false;
	    });
	    
	    var repositoryTree = new RepositoryManageTree({title: '资源库'});
		
	    this.items = [repositoryTree];
		
	    GuidePanel.superclass.initComponent.call(this);
	    
	}
});