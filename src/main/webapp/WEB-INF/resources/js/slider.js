/**
 * This code is provided by luisfga@protonmail.com and can be used as desired, being required only the mention to the author. 
 * Please, ladies and gentlemen.
 */


//GLOBALS
let sliderDiv = document.getElementById('luisfgaSlider'); //the main character
sliderDiv.style.display = 'flex';

let parentOuterDiv = sliderDiv.parentNode;
//set parent overflow to hidden to prevent overflow on mobile device's browsers, mainly on X axis. That's the main cause we need a outer encapsulating div
parentOuterDiv.style.overflow = 'hidden';
parentOuterDiv.style.position = 'relative';
parentOuterDiv.onmouseenter = function(){
    stopAutoSliding();
};
parentOuterDiv.onmouseleave = function(){
    startAutoSliding();
};

//some default values to image exposition and transition length. Override it at the end to setup the slider
let expositionMillis = 5000;
let transitionLength = 1000;
const transitionCssValue = function(){return 'transform '+transitionLength+'ms ease-in-out';}; 
sliderDiv.style.transition = transitionCssValue();

let page = 0;//page index controller
let expanding = false;//controller for the infinite overflow (say transparent "carouseling") handling
let pageSelectorActiveBgColor = '#999999';

//set pages selectors containers div outside sliderDiv, set it inside parent, outer, div
let pageSelectorsContainerDiv = document.createElement('div');//the numbered pages SELECTORS are initialized next, on the clening part
pageSelectorsContainerDiv.id = 'pageSelectorsContainerDiv';
pageSelectorsContainerDiv.style.position = 'absolute';
pageSelectorsContainerDiv.style.top = 0;
pageSelectorsContainerDiv.style.display = 'flex';
parentOuterDiv.appendChild(pageSelectorsContainerDiv);
let pageSwitchersContainerDiv = document.createElement('div');
pageSwitchersContainerDiv.id = 'pageSwitchersContainerDiv';
pageSwitchersContainerDiv.style.position = 'absolute';
pageSwitchersContainerDiv.style.top = 0;
pageSwitchersContainerDiv.style.right = 0;
pageSwitchersContainerDiv.style.display = 'flex';
pageSwitchersContainerDiv.style.color = pageSelectorActiveBgColor;
parentOuterDiv.appendChild(pageSwitchersContainerDiv); //<- Setting selectors containers outside sliderDiv, they are brothers. If one or another made a strange choice, i wont comment
let pageForwardStepper = document.createElement('div');
pageForwardStepper.id = "pageFowardSwitcher";
pageForwardStepper.innerHTML = '&#10095;';
pageForwardStepper.style.cursor = 'pointer';
pageForwardStepper.style.backgroundColor = 'transparent';
pageForwardStepper.style.opacity = 0.75;
pageForwardStepper.style.width = 'fit-content';
pageForwardStepper.style.height = 'fit-content';
pageForwardStepper.style.padding = '4px 6.5px 4px 7px';
pageForwardStepper.style.fontSize = '0.75em';
pageForwardStepper.style.border = 0;
pageForwardStepper.style.borderRadius = '45px';
pageForwardStepper.onmouseover = function(){
    this.style.backgroundColor = pageSelectorActiveBgColor; 
    this.style.color = 'white';
};
pageForwardStepper.onmouseout = function(){
    this.style.backgroundColor = 'transparent'; 
    this.style.color = pageSelectorActiveBgColor;
};
pageForwardStepper.onclick = function(){stepFoward();};
let pageBackStepper = document.createElement('div');
pageBackStepper.id = "pageFowardSwitcher";
pageBackStepper.innerHTML = '&#10094;';
pageBackStepper.style.cursor = 'pointer';
pageBackStepper.style.backgroundColor = 'transparent';
pageBackStepper.style.opacity = 0.75;
pageBackStepper.style.width = 'fit-content';
pageBackStepper.style.height = 'fit-content';
pageBackStepper.style.padding = '4px 7px 4px 6.5px';
pageBackStepper.style.fontSize = '0.75em';
pageBackStepper.style.border = 0;
pageBackStepper.style.borderRadius = '45px';
pageBackStepper.onmouseover = function(){
    this.style.backgroundColor = pageSelectorActiveBgColor; 
    this.style.color = 'white';
};
pageBackStepper.onmouseout = function(){
    this.style.backgroundColor = 'transparent'; 
    this.style.color = pageSelectorActiveBgColor;
};
pageBackStepper.onclick = function(){stepBack();};

pageSwitchersContainerDiv.appendChild(pageBackStepper);
pageSwitchersContainerDiv.appendChild(pageForwardStepper);

//2.Proloque - THE CLEAN-UP. Lets clean it, removing garbage txts
let garbage = [];
//select the garbage nodes and set style for img nodes and selectors
tempCount = 0;
for (var i=0; i<sliderDiv.childNodes.length; i++){
    if(sliderDiv.childNodes[i].nodeType === Node.TEXT_NODE) {
        garbage.push(sliderDiv.childNodes[i]);
    } else {
        //HERE we are trying to take advantage of this loop to set imgs
        sliderDiv.childNodes[i].style.minWidth = '100%';
        sliderDiv.childNodes[i].style.display = 'flex';
        
        //for each image, set page selector
        let pageSelector = document.createElement('div');
        pageSelector.id = 'pageSelector'+tempCount;
        pageSelector.style.backgroundColor = 'transparent';
        pageSelector.style.opacity = 0.75;
        pageSelector.style.width = '10px';
        pageSelector.style.height = '10px';
        pageSelector.style.border = pageSelectorActiveBgColor +' 1px solid';
        pageSelector.style.borderRadius = '45px';
        pageSelector.style.margin = '10px';
        pageSelector.style.cursor = 'pointer';
        pageSelector.onmouseover = function(){
            this.style.backgroundColor = pageSelectorActiveBgColor;
        };
        
        pageSelector.onmouseout = function(){
            if (Number(this.getAttribute('pagenumber')) !== Number(page)){
                this.style.backgroundColor='transparent';
            }
        };
        pageSelector.setAttribute('pagenumber', tempCount);
        pageNumberString = ''+tempCount;
        pageSelector.onclick = function(){
            clearPageSelectors();
            slideToPage(pageSelector.getAttribute('pageNumber'));
            this.style.backgroundColor = pageSelectorActiveBgColor;
        };
        pageSelectorsContainerDiv.appendChild(pageSelector);
        
        tempCount++;
    }
    
}
//remove the garbage, let be only <img> tags
for (var i=0; i<garbage.length; i++){
    sliderDiv.removeChild(garbage[i]);
}

//UTILITIES
function disableSteppers(){
    pageForwardStepper.style.pointerEvents = 'none';
    pageBackStepper.style.pointerEvents = 'none';
}
function enableSteppers(){
    pageForwardStepper.style.pointerEvents = 'all';
    pageBackStepper.style.pointerEvents = 'all';    
}
function stepFoward(){
    isForward = true;
    page++;
//    console.log('slideForward -> to page ' + page);
    slideToPage(page);
    //hard set page marker because of click. TODO: improve separation of auto-logics and user interaction handling
}
function stepBack(){
    isForward = false;
    page--;
//    console.log('slideBack -> to page ' + page);
    slideToPage(page);
}

function slideToPage(pageNumber){
//    console.log('slideToPage ' + page);
    page = pageNumber;
    
    if((page >= 0) && (page < (sliderDiv.childNodes.length))) {
//        console.log('normal slide');
        sliderDiv.style.transform = 'translate(-'+(sliderDiv.clientWidth*page)+'px)';
        setPageMarkToPage(page);

    } else if(page < 0) {
//        console.log('expand back slide');
        expandBack();
        setPageMarkToLastPage();

    } else if(page >= sliderDiv.childNodes.length) {
//        console.log('expand forward slide');
        expandForward();
        setPageMarkToFirstPage();
    }    

}

//INFINITE OVERFLOW Controllers
function expandForward(){
//    console.log('expanding forward');
    expanding = true;
    
    //disable steppers evict click flood during the expansion
    disableSteppers();
    
    page=0; 
    setPageMarkToPage(0);

    //copy first
    let tempNode = sliderDiv.firstChild.cloneNode(true);
//    console.log('fisrtChild cloned');

    //and append to end
    sliderDiv.appendChild(tempNode);
//    console.log('tempNode appended');

//    console.log('page -> ' + page);

    sliderDiv.style.transform = 'translate(-'+(sliderDiv.clientWidth*(sliderDiv.childNodes.length-1))+'px)';
//    console.log('expanded and slided to temp last node, simulating first');

//    console.log('short timeout to evict transitions collision')
    setTimeout(function(){

        sliderDiv.style.transition = 'none';
//        console.log('transition suspended');

        sliderDiv.style.transform = 'translate(0px)';
//        console.log('translated to 0px');

        sliderDiv.removeChild(sliderDiv.lastChild);
//        console.log('temp child removed');

//        console.log('expanding forward work complete');

        //another timeout to re-change transition setting without overriding the previous
//        console.log('just that transition fix');
        setTimeout(function(){
            sliderDiv.style.transition = transitionCssValue();
//            console.log('transition settings restored');
            expanding = false;
            //re-enable steppers as this hacky cycle is complete
            enableSteppers();
        },transitionLength);

    },transitionLength+500);//<-waiting transition, a little gap to not collide

}

function expandBack(){
//    console.log('expanding back');
    expanding = true;
    
    //disable steppers evict click flood during the expansion
    disableSteppers();
    
    page=sliderDiv.childNodes.length-1;
    setPageMarkToPage(page);

    //copy last
    let tempNode = sliderDiv.lastChild.cloneNode(true);
//    console.log('lastChild cloned');

    sliderDiv.style.transition = 'none'; //blackops
//    console.log('blackops set');

    //and insert before first with sliding to temp page 1
    sliderDiv.insertBefore(tempNode, sliderDiv.firstChild);
    //last child was inserted and now is also the first, so we need to show the second, which is actually the first
    sliderDiv.style.transform = 'translate(-'+sliderDiv.clientWidth+'px)';
//    console.log('tempNode inserted before first child and slided to page 1');

//    console.log('timeout to restore transition without colliding with the suspension and slide smoothly to temp page zero');
    setTimeout(function(){ //different transition settings collision eviction (just a fix)
        sliderDiv.style.transition = transitionCssValue();
        sliderDiv.style.transform = 'translate(0px)';
//        console.log('translated to 0px');

        //another transition setting eviction, now to restore everything sliding to real last page
        setTimeout(function(){
            sliderDiv.style.transition = 'none';
//            console.log('blackops set again');
            //current, real last page
            sliderDiv.style.transform = 'translate(-'+(sliderDiv.clientWidth*(sliderDiv.childNodes.length-1))+'px)';
//            console.log('slided to last page');
            sliderDiv.removeChild(sliderDiv.firstChild);
            
            sliderDiv.style.transform = 'translate(-'+(sliderDiv.clientWidth*page)+'px)';
//            console.log('temp first child removed and re-slided to real last page');

            //another timeout to re-change transition setting without overriding the previous
            setTimeout(function(){
                sliderDiv.style.transition = transitionCssValue();
//                console.log('slider restored');
                expanding = false;
                //re-enable steppers as this hacky cycle is complete
                enableSteppers();

            },transitionLength);//another fix, a re-fix, the last
        },transitionLength+500);//wating transition
    },0);//<-just to evict collision of different style.transition settings, the second overriding the first
}

function setPageMarkToPage(pageIndex){
    pageIndex = Number(pageIndex);
//    console.log('setPageMarkToPage -> ' + pageIndex);
    lastPageIndex = pageSelectorsContainerDiv.childNodes.length-1;
//    console.log('lastPageIndex = ' + lastPageIndex);

    if (pageIndex < 0 || pageIndex > lastPageIndex) return;

    let unmarkIndex = isForward?pageIndex-1:pageIndex+1;
    if(pageIndex === 0 && isForward) unmarkIndex = lastPageIndex;
    if(pageIndex === lastPageIndex && !isForward) unmarkIndex = 0;
    
//    console.log('unmarkIndex = ' + unmarkIndex);
    
    //unmark
    pageSelectorsContainerDiv.childNodes[unmarkIndex].style.backgroundColor = 'transparent';
    
    //mark current
    pageSelectorsContainerDiv.childNodes[pageIndex].style.backgroundColor = pageSelectorActiveBgColor;
}
function setPageMarkToFirstPage(){
//    console.log('set selector to first page.');
    lastPageIndex = pageSelectorsContainerDiv.childNodes.length-1;
    pageSelectorsContainerDiv.childNodes[lastPageIndex].style.backgroundColor = 'transparent';
    pageSelectorsContainerDiv.childNodes[0].style.backgroundColor = pageSelectorActiveBgColor;
}
setPageMarkToFirstPage();

function setPageMarkToLastPage(){
//    console.log('set selector to last page.');
    lastPageIndex = pageSelectorsContainerDiv.childNodes.length-1;
    pageSelectorsContainerDiv.childNodes[0].style.backgroundColor = 'transparent';
    pageSelectorsContainerDiv.childNodes[lastPageIndex].style.backgroundColor = pageSelectorActiveBgColor;
}
function clearPageSelectors(){
    for (var i = 0; i < pageSelectorsContainerDiv.childNodes.length; i++) {
        pageSelectorsContainerDiv.childNodes[i].style.backgroundColor = 'transparent';
    }
}

//Start-Stop controls
let slidingIntervalID;
//let slidingRestartTimeoutID;

let isForward;
let isAutomatic;
function startAutoSliding(){
//    console.log('starting slides with isForward = ' + isForward);

//    console.log('Transition length = ' + transitionLength);
//    console.log('Exposition length = ' + expositionMillis);

    if(transitionLength > expositionMillis
        || (transitionLength > 2000 && !(transitionLength*2) < expositionMillis)){
//        console.log('Transition too high or exposition too low. Setting exposition to 3 times the transition');
        expositionMillis = transitionLength*3;
//        console.log('NEW Transition length = ' + transitionLength);
//        console.log('NEW Exposition length = ' + expositionMillis);
    }
    
    if(isForward) {
        slidingIntervalID = setInterval(autoSlideFoward, expositionMillis+transitionLength);
    } else {
        slidingIntervalID = setInterval(autoSlideBack, expositionMillis+transitionLength);
    }
    isAutomatic = true;
//    console.log('automatic turned (ON)');
//    console.log('NEW INTERVAL ID = ' + slidingIntervalID);
}

function autoSlideFoward(){
    page++;
    slideToPage(page);
}
function autoSlideBack(){
    page--;
    slideToPage(page);
}

function stopAutoSliding(){
    isAutomatic = false;
//    console.log('Auto-sliding stopped - isAutomatic turned (OFF)');
    
//    console.log('Clearing slide interval - ID = ' + slidingIntervalID);
    clearInterval(slidingIntervalID);
}

//todo: this can be 'parametrizado' when integrating with JSF
//sample setups
//sloooowwww 7000 / 2500
//balanced 4000 / 1000
//black friday with thousands of products 1500 / 500
//are you crazy? 1000 / 100
expositionMillis = 4000;
transitionLength = 1000;
sliderDiv.style.transition = transitionCssValue();
isForward = true;
startAutoSliding();

window.onresize = function(){
    sliderDiv.style.transform = 'translate(-'+(sliderDiv.clientWidth*page)+'px)';
};