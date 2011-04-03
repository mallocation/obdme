var popupStatus = 0;
var fadeRate = 500;
function loadPopup(popUpContent, backgroundPopup) {
    if (popupStatus == 0) {
        $(backgroundPopup).css({
            "opacity": "0.7"
        });
        $(backgroundPopup).fadeIn(fadeRate);
        $(popUpContent).fadeIn(fadeRate);
        popupStatus = 1;
    }
}

function disablePopup(popUpContent, backgroundPopup) {
    if (popupStatus == 1) {
        $(backgroundPopup).fadeOut(fadeRate);
        $(popUpContent).fadeOut(fadeRate);
        popupStatus = 0;
    }
}

function centerPopup(popUpContent, backgroundPopup) {
    var windowWidth = document.documentElement.clientWidth;
    var windowHeight = document.documentElement.clientHeight;
    var popupHeight = $(popUpContent).height();
    var popupWidth = $(popUpContent).width();
    $(popUpContent).css({
        "position": "absolute",
        "top": windowHeight / 2 - popupHeight / 2,
        "left": windowWidth / 2 - popupWidth / 2
    });
    $(backgroundPopup).css({
        "height": windowHeight
    });

}

$(document).ready(function() {

    $("#button").click(function() {
        centerPopup();
        loadPopup();
    });

    $("#popupContentClose").click(function() {
        disablePopup();
    });

    $("#backgroundPopup").click(function() {
        disablePopup();
    });

    $(document).keypress(function(e) {
        if (e.keyCode == 27 && popupStatus == 1) {
            disablePopup();
        }
    });

});