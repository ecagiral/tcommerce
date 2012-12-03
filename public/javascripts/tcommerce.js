/*function addComment(){
	var commentText = $("#message").val();
	var announcerId = $("#comment-announcer-id").val();
	var productId = $("#comment-product-id").val();
	var comment = new Comment(commentText, announcerId, productId);
	comment.add();
}*/

$.postJSON = function(url, data, callback) {
    return $.ajax({
        'type' : 'POST',
        'url' : url,
        'data' : data,
        'dataType' : 'json',
        'success' : callback,
        'error' : error_callback
    });
};

function error_callback(XMLHttpRequest, textStatus, errorThrown){
	alert(errorThrown);
}


function showAddProductModal(){
	$("#addProductModal").modal().show();
}

function getCookie(c_name)
{
var i,x,y,ARRcookies=document.cookie.split(";");
for (i=0;i<ARRcookies.length;i++)
{
  x=ARRcookies[i].substr(0,ARRcookies[i].indexOf("="));
  y=ARRcookies[i].substr(ARRcookies[i].indexOf("=")+1);
  x=x.replace(/^\s+|\s+$/g,"");
  if (x==c_name)
    {
    return unescape(y);
    }
  }
}

function signout(signoutUrl) {
	deletecookie("id");
	deletecookie("oauthtoken");
	document.location = signoutUrl;
}

function deletecookie(cookieName) {
	document.cookie = cookieName + "=;expires=Thu, 01-Jan-1970 00:00:01 GMT;";
}
function getCookie(c_name) {
	if (document.cookie.length > 0) {
		c_start = document.cookie.indexOf(c_name + "=");
		if (c_start != -1) {
			c_start = c_start + c_name.length + 1;
			c_end = document.cookie.indexOf(";", c_start);
			if (c_end == -1)
				c_end = document.cookie.length;
			return unescape(document.cookie.substring(c_start, c_end));
		}
	}
	return "";
}



/*function Comment(comment, userId, productId){
	this.comment = comment;
	this.announcerId = userId;
	this.productId = productId;
	
	this.add = function(){
		$.postJSON(ctx + "/rest/announcers/comment", this, this.add_CallBack);
	};
	
	this.add_CallBack = function (data){
		var html = parseTemplate($("#commentTemplate").html(), 
		           {comment: data});
		$(html).fadeIn("slow")
		.appendTo("#commentContainer");
		$("#message").val("");
	};
}*/


//////////////////ERMAN

function Comment(comment, itemId){
	this.text = comment;
	this.itemId = itemId;
	
	this.add = function(){
		var data = {itemId:itemId,'text':comment};
		$.postJSON("/addComment", data, this.add_CallBack);
	};
	
	this.add_CallBack = function (data){
		if(data.error){
			alert(data.error);	
		}else{
			//console.log("comment : \""+data.text+"\" added to item : "+data.item);
			var html = parseTemplate($("#commentTemplate").html(), {comment: data});
			$(html).fadeIn("slow").appendTo(".commentContainer");
			$("#commentText").val("");
		}
		
	};
}

function addComment(item){
	var text = $("#commentText").val();
	var comment = new Comment(text,item);
	comment.add();
}
