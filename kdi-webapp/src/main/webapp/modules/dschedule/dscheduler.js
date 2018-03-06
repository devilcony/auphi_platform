var winDependency ;
var i18nStrs = {"EN":"ZH"};// load i18n js 
function i18n(str){
	var i18nstr = i18nStrs[str] ;
	return i18nstr == null?str:i18nstr ;
}


function appendOptions(selector,options,label){
	var selfJobfullname = jQuery('#jobgroup')[0].value+'.'+ jQuery('#jobname')[0].value;
	
	var group=document.createElement('OPTGROUP');  
	group.label = label;
	selector.appendChild(group);
	for(var i = 0 ; i < options.length ; i ++){
		if(options[i] == selfJobfullname)
			continue ;
		group.appendChild(new Option(options[i],options[i]));
	}
}

function showAddDependency(){
	Ext.Ajax.request({
		url: 'dschedule',
		method: 'GET',
		params: {
	        'action': 'getSchedule'
	    },
		success: function(transport) {

			var data = eval('('+transport.responseText+')');
			var selectContent = '' ;
			var selector = jQuery('select#jobfullname')[0];
			jQuery('select#jobfullname').empty();
			selector.options.add(new Option('请选择调度',''));
			appendOptions(selector,data['schedule'],'普通调度');
			appendOptions(selector,data['dschedule'],'事件调度');
			
			selector = jQuery('select#djobfullname')[0];
			jQuery('select#djobfullname').empty();
			selector.options.add(new Option('请选择依赖调度',''));
			appendOptions(selector,data['schedule'],'普通调度');
			appendOptions(selector,data['dschedule'],'事件调度');
			
			jQuery('#divNewDependency').show();
		},
		error: function(){
			
		}
	});	
	
}

function addDependency(){
	var jobname = jQuery('#jobname')[0].value;
	var jobgroup = jQuery('#jobgroup')[0].value;
	var jobfullname = jQuery('#jobfullname')[0].value;
	var djobfullname = jQuery('#djobfullname')[0].value;
	
	if(jobfullname == ''){
		alert(i18n('请选择调度'));
		return ;
	} else if (jobfullname == djobfullname){
		alert(i18n('请选择不同调度'));
		return ;
	}
	
	if(!precheck(jobname,jobgroup,jobfullname,djobfullname)){
		alert(i18n('可能存在循环依赖'));
	}
	
	Ext.Ajax.request({
		url: 'dschedule',
		method: 'POST',
		params: {
	        'action': 'addDependencies',
	        'jobname': jobname,
	        'jobgroup':jobgroup,
	        'jobfullname':jobfullname,
	        'djobfullnames':djobfullname
	    },
		success: function(transport) {
			if('OK' == transport.responseText)
			{
				var content = dependencyRow(jobfullname,djobfullname);
				jQuery('#jobdependencies').append(content) ;
				//jQuery('#divNewDependency').hide();
			}
			
		},
		error: function(){
			
		}
	});
}

function deleteDependency(jobfullname,djobfullname){
	
	var jobname = jQuery('input#jobname')[0].value ;
	var jobgroup = jQuery('input#jobgroup')[0].value ;
	
	var selector = ('#'+jobfullname+'_'+djobfullname).replace(/\./g,'\\.') ;
	
	Ext.Ajax.request({
		url: 'dschedule',
		method: 'POST',
		params: {
	        'action': 'deleteDependency',
	        'jobname': jobname,
	        'jobgroup':jobgroup,
	        'jobfullname':jobfullname,
	        'djobfullname':djobfullname
	    },
		success: function(transport) {
			if('OK' == transport.responseText)
				jQuery(selector).remove() ;
			
		},
		error: function(){
			
		}
	});
}

function getCheckedJob(){
	var checked = jQuery('input[name=check]:checked');
	if(checked.length != 1){
		Ext.MessageBox.alert(i18n('警告'),i18n('请选择一个调度'));
		return '' ;
	}
	document.getElementById('checked_job').value = checked[0].value;
	return checked[0].value ;
}


function dependencyRow(jobfullname,djobfullname){
	var id = jobfullname +'_'+djobfullname ;
	var content = '<tr id="'+id+'">' ;
	content += '<td>'+jobfullname+'</td>';	
	content += '<td>'+ djobfullname+ '</td>' ;
	content += '<td><a href=# onclick="deleteDependency(\''+jobfullname+'\',\''+djobfullname+'\');">'+i18n('删除')+'</a></td>';	
	content += '</tr>' ;
	return content ;
}

function precheck(jobname,jobgroup,jobfullname,djobfullname){
	return true ;
}

function onAddDependency(){

	var selectedJob = getCheckedJob() ;
	if(!selectedJob || selectedJob == '' || selectedJob.indexOf(',') >= 0)
		return ;
	
	jQuery('input#jobname')[0].value = selectedJob ;
	jQuery('#divNewDependency').hide();
	
	Ext.Ajax.request({
		url: 'dschedule',
		method: 'GET',
		params: {
	        action: 'getDependencies',
	        jobname: selectedJob
	    },
		success: function(transport) {
			var data = eval('('+transport.responseText+')');
			var ele = jQuery('#jobdependencies') ; 
			ele.html('') ;
			
			var content = '<table width=100%>' ;
			content += '<tr><th>调度</th><th>依赖调度</th><th>操作</th></tr>';
			for(var job in data)
				for(var djob in data[job])
					content += dependencyRow(job,data[job][djob])
			
			content += '</table>' ;

			ele.html(content) ;
		},
		error: function(){
			
		}
	});
	
	if(!winDependency){
		winDependency =  Ext.create('Ext.window.Window', {
        	contentEl: 'dlg_dependencies',
        	title:i18n('依赖关系'),
        	width:450,
        	height:500,
        	autoHeight:true,
        	buttonAlign:'center',
        	closeAction:'hide',
	        buttons: [
	            /*
	        	{text:i18n('提交'),handler: function(){
	        		alert('') ;		
	    	    	}
	        	},
	        	{text:i18n('关闭'),handler: function(){winDependency.hide();}}
	        	*/
	        ]
        });
    }
	winDependency.show() ;
}
