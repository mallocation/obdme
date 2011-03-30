$(document).ready(function() {
			
	$("div.panel_button").click(function(){
		$("div#panel").animate({
			height: "115px"
		}, 1000, "easeOutBounce");
		$("div.panel_button").toggle();
	
	});	
	
   $("div#hide_button").click(function() {
		$("div#panel").stop().animate({
			height: "0px"
		}, 500, "easeInBack");
   });	
	
});