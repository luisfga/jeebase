function maskPhone(id) {
    
//    document.getElementById(id).addEventListener('input', function (e) {
//        var x = e.target.value.replace(/\D/g, '').match(/(\d{0,3})(\d{0,3})(\d{0,4})/);
//        e.target.value = !x[2] ? x[1] : '(' + x[1] + ') ' + x[2] + (x[3] ? '-' + x[3] : '');
//    });

    document.getElementById(id).addEventListener('input', function (e) {
        var x = e.target.value.replace(/\D/g, '').match(/(\d{0,11})/);
        e.target.value = x[1];
    });
}

function customTitle(event, textHeader, text){
    let eventOrigin = event.target || event.srcElement;
    eventOrigin.title = textHeader + '\n\n' + text;
}

function hideAllByClass(className){
    let nodes = document.getElementsByClassName(className);
    for (var i = 0; i < nodes.length; i++) {
        nodes[i].style.display = 'none';
    }
}
function unselectByClass(className){
    let nodes = document.getElementsByClassName(className);
    for (var i = 0; i < nodes.length; i++) {
        nodes[i].classList.remove('selected');
    }
}

/**
 * This function considers the page having one and only one <footer> tag.
 * One should set footer to position absolute with bottom 0 (zero), so if
 * page content overflows this function sets footer to 'initial' position,
 * placing it at the end of body content and not fixed at window bottom no more.
 */
function footerHandler(){
    let contentDiv = document.getElementById('content');
    let contentBottomY = contentDiv.getBoundingClientRect().bottom;
    let footer = document.getElementsByTagName('footer')[0];
    let footerHeight = parseInt(document.defaultView.getComputedStyle(footer).height);
    let computedHeight = contentBottomY + footerHeight;
    let isContentOverflowing = computedHeight >= window.innerHeight;
    if(isContentOverflowing){
        document.getElementsByTagName('footer')[0].style.position = 'initial';
    }
}
