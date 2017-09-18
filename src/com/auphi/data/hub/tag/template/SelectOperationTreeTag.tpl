<!-- 由<skyform.SelectUserTree/>标签生成的代码开始 -->
<div id="selectOperationTreeDiv"></div>
<script type="text/javascript">
Ext.onReady(function() {
#foreach($menu in $menuList1)
	var node_${menu.menuid} = new Ext.tree.TreeNode({
		text:'${menu.menuname}',	
		id:'id_node_${menu.menuid}'
	});
#end
#foreach($oper in $operList)
	var node_${oper.functionid} = new Ext.tree.TreeNode({
		text:'${oper.functionname}',
#if(${oper.checked} == "true")
	 checked:true,
#else
    checked:false,	
#end
		functionid:'${oper.functionid}',
		id:'id_node_${oper.functionid}'
	});
#end

#foreach($menu in $menuList1)
#if(${menu.isRoot}!="true")
	node_${menu.parentid}.appendChild(node_${menu.menuid});
#end
#end
#foreach($oper in $operList)
	node_${oper.menuid}.appendChild(node_${oper.functionid});
#end

var selectOperationTree = new Ext.tree.TreePanel({
			autoHeight : false,
			autoWidth : false,
			autoScroll : true,
			animate : false,
			rootVisible : true,
			border : false,
			containerScroll : true,
			applyTo : 'selectOperationTreeDiv',
			tbar : [{
				text : '保存',
				id : 'selectOperation_saveBtn',
				iconCls : 'acceptIcon',
				handler : function() {
					     var checkedNodes = selectOperationTree.getChecked();
					     var functionid = "";
						 for(var i = 0; i < checkedNodes.length; i++) {
						   var checkNode = checkedNodes[i];
					       functionid = functionid + checkNode.attributes.functionid + "," ;  
						 }
						 saveOperation(functionid);
				 }
		    }, '-', {
				text : '展开',
				iconCls : 'expand-allIcon',
				handler : function() {
					selectOperationTree.expandAll();
				}
		    }, '-', {
				text : '收缩',
				iconCls : 'collapse-allIcon',
				handler : function() {
					selectOperationTree.collapseAll();
				}
		    }],
			root : node_01
  });
  //node_${deptid}.expand();
  selectOperationTree.expandAll();

//保存授权数据
function saveOperation(functionid){
		showWaitMsg();
		Ext.Ajax.request({
					url : '../function.ered?reqCode=saveUser',
					success : function(response) {
						var resultArray = Ext.util.JSON.decode(response.responseText);
						Ext.Msg.alert('提示', resultArray.msg);
					},
					failure : function(response) {
						var resultArray = Ext.util.JSON.decode(response.responseText);
						Ext.Msg.alert('提示', resultArray.msg);
					},
					params : {
						functionid : functionid,
						userid : ${userid}
					}
				});
}
	
})
</script>
<!-- 由<skyform.SelectUserTree/>标签生成的代码结束 -->