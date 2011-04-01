/*
 * jQuery Placeholder Plug-in
 *
 * Copyright (c) 2010 Max Davis
 *
 * Dual licensed under the MIT and GPL licenses:
 *   http://www.opensource.org/licenses/mit-license.php
 *   http://www.gnu.org/licenses/gpl.html
 *
 * Revision: 1
 * Version: 0.1
 *
 * v0.1
 * - First public release
 *
*/

(function($){
	
	$.fn.placeholder = function() {
		
		if($(this).attr("type") == "password") {
		
			var original_pass_field = $(this);

			if(original_pass_field.val() == "") {
				$(this).parent().append("<input type=\"text\" value=\"\" + $(this).attr("placeholder") +"\" name=\"pass_placeholder\" class=\"profileInputFields\" id=\"pass_placeholder\">");
				$(this).css("display","none");
			}

			var original_pass_field = $(this);

			$("#pass_placeholder").focus(function() {
				if(original_pass_field.val() == "") {
					$("#pass_placeholder").css("display","none");
					original_pass_field.css("display","");
					original_pass_field.focus();
				}
			});

			original_pass_field.blur(function() {
				if(original_pass_field.val() == "") {
					$("#pass_placeholder").css("display","");
					original_pass_field.css("display","none");
				}
			});

		} else {

			if($(this).val() === "") {
				$(this).val($(this).attr("placeholder"));
				$(this).css("color","#777777");
			}

			$(this).focus(function() {
				if($(this).val() === $(this).attr("placeholder")) {
					$(this).css("color","#000000");
					$(this).val("");
				}
			}).blur(function() {
				if($(this).val() === "") {
					$(this).css("color","#777777");
					$(this).val($(this).attr("placeholder"));
				}
			});
		}

	} 
	
})(jQuery);