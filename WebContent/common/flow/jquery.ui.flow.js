(function($){
	$.fn.flow = function(option){
		var defaultOptions = {
			padding: 5,
			width : 95,//线与方框的间距
			height : 25,//线紧挨方框的位置
			lineWidth : 140,//同一行内线的长度，方框的间距
			lineHeight : 65,//不同行之间的间距
			slider : 25,//分支节点线的位置
			handle : "draw",
			img : {
				right : "common/flow/images/right_sword.png",
				up : "common/flow/images/right_sword.png"
			},
			hover : function(){
				
			},
			remove : function(){
				
			},
			click : function(){
				
			}
		}
		var opts = $.extend(defaultOptions,option);
		return this.each(function(){
			var currentQuery = $(this);
			var drawLine = function(start,end){
				if($(start).data($(end).attr("id")) == true){
					return;
				}
				var offsetLeft = $("#" + opts.handle).offset().left;
				var offsetTop = $("#" + opts.handle).offset().top;
				var x1 = start.offset().left - offsetLeft + opts.padding + opts.width;
				var x2 = start.offset().top - offsetTop + opts.padding + opts.height;
				var y1 = end.offset().left - offsetLeft;
				var y2 = end.offset().top - offsetTop + opts.padding + opts.height;
				var jg_draw = new jsGraphics(opts.handle);
				jg_draw.setStroke(2); 
				//alert(x1 + ":" + x2 + "," + y1 + ":" + y2);
				//jg_draw.setStroke(Stroke.DOTTED); 
				//jg_draw.drawImage("images/left_sword.png",0,0,10,15,"onclick='alert($(\"#draw\").html())'");
				var isShow = ($(start).attr("isShow"));
				if(isShow==0)
					return;
				if(x2 == y2){
					jg_draw.drawLine(x1,x2,y1,y2);
					jg_draw.drawImage(opts.img.right,y1-5,y2-7,10,15);
				}else{
					if(x2 < y2){
						jg_draw.drawLine(x1,x2,x1 + opts.slider,x2);
						jg_draw.drawLine(x1 + opts.slider,x2,x1 + opts.slider,y2);
						jg_draw.drawLine(x1 + opts.slider,y2,y1,y2);
						jg_draw.drawImage(opts.img.up,y1-5,y2-7,10,15);
					}else{
						jg_draw.drawLine(x1,x2,y1 - opts.slider,x2);
						jg_draw.drawLine(y1-opts.slider,x2,y1 - opts.slider,y2);
						jg_draw.drawLine(y1 - opts.slider,y2,y1,y2);
						jg_draw.drawImage(opts.img.right,y1-5,y2-7,10,15);
					}
				}
				jg_draw.paint();
				$(start).data($(end).attr("id"),true);
			}	
		
			var createObjectList = function(id,level){
				var root = {
					id:0,
					next:"",
					name:"",
					child:[],
					level : 0,
					getSize : function(){
						return this.child.length;
					},
					getChild:function(i){
						return $(".before[id=" + this.child[i].id + "]");
					}
				};
				var $rootObject = $(".before[id=" + id + "]",currentQuery);
				root.id=$rootObject.attr("id");
				root.next=$rootObject.attr("next");
				root.name=$rootObject.text();
				root.level = level;
				var childID = root.next.split(",");
				for(var i = 0;i < childID.length;i++){
					var child = $(".before[id=" + childID[i] + "]",currentQuery);
					if(child.size() == 0){
						continue;	
					}
					var childObject = createObjectList(child.attr("id"),level + 1);
					root.child.push(childObject);
				}
				return root;
			}
		
			var rootID = $(".before[begin]",currentQuery).attr("id");
			var myObjectList = createObjectList(rootID,0);
			var drawDiv = function(objectList,spanID,left,top,maxHeight){
				var span = $("#" + spanID);
				var $root = $(".before[id=" + objectList.id + "]",currentQuery).clone();
				$(".before[id=" + objectList.id + "]",currentQuery).remove();
				if($(".before[id=" + objectList.id + "]").size() > 0){
					$root = $(".before[id=" + objectList.id + "]").eq(0);
					return;
				}else{
					$("#" + opts.handle + " .before").each(function(){
						var leftPX = $(this).css("left");
						var topPX = $(this).css("top");
						if(leftPX == (left+'px') && topPX == (top+'px')){
							top = top + opts.lineHeight;
						}
					});	
					$root.appendTo(span);
				}
				$root.css("position","absolute");
				$root.css("left",left);
				$root.css("top",top);			
				if(objectList.getSize() == 0){
					return;
				};
				var nextLeft = opts.lineWidth * (objectList.level + 1);
				var nextHeight = top;
				for(var i = 0; i < objectList.getSize();i++){
					drawDiv(objectList.child[i],opts.handle,nextLeft,opts.lineHeight * (i) + top*i,nextHeight);
					try{
						drawLine($root,objectList.getChild(i));
					}catch(e){
						alert(e.description);
						break;
					}
				}
				return;
			}
			try{
				drawDiv(myObjectList,opts.handle,0,0,300);
				$("#" + opts.handle).find(".before").hover(opts.hover,opts.remove).click(opts.click);
			}catch(e){
				alert(e.description)
			}
		});
	}
})(jQuery)