$(document).ready(function() {
			
	$("div.panel_button").click(function(){
		$("div#panel").animate({
			height: "150px"
		}, 1000, "easeOutBounce");
		$("div.panel_button").toggle();
	
	});	
	
   $("div#hide_button").click(function() {
		$("div#panel").animate({
			height: "0px"
		}, 1000, "easeInBack");
   });	
	
});