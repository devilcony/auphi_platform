
    
function openClose(obj){
	obj.innerHTML = obj.innerHTML=="-"?"+":"-";
	while(obj.tagName != "TBODY"){
		obj = obj.parentNode;
	}
	for(i=0; i<obj.childNodes.length; i++){
		if(obj.childNodes[i].nodeName == "#text"
			 || obj.childNodes[i].getAttribute("bar")){ 
			 	continue; 
		}
		obj.childNodes[i].style.display=obj.childNodes[i].style.display==""?"none":"";
	}
	
    var e = jQuery.Event("mousedown");
    $(obj).trigger(e);
    
    e = jQuery.Event("mousemove"); 
    $(obj).trigger(e);
    
    e = jQuery.Event("mouseup");
    $(obj).trigger(e);

}