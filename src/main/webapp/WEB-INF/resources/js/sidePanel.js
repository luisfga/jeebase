        let transitionDuration = 1000;
        const sidePanelTransitionRatio = 0.75;
        const contentTransitionRatio = 0.25;
        let sidePanelTransitionDuration = transitionDuration*sidePanelTransitionRatio;
        let contentTransitionDuration = transitionDuration*contentTransitionRatio;

        function toggleSidePanel(id){
            let sidePanelOverlay = document.getElementById(id+'Overlay');
            if (sidePanelOverlay.style.visibility === 'visible'){
                hideSidePanel(id);
            } else {
                showSidePanel(id);
            }
        }

        function showSidePanel(id){
            let sidePanelOverlay = document.getElementById(id+'Overlay');
            let sidePanel = document.getElementById(id);
            let sidePanelContent = document.getElementById(id+'Content');
            
            sidePanelOverlay.style.visibility = 'visible';
            
            if(window.matchMedia("(max-width: 478px)").matches){
                sidePanel.style.width = '90%';
            } else if(window.matchMedia("(max-width: 1024px)").matches){
                sidePanel.style.width = '40%';
            } else {
                sidePanel.style.width = '30%';
            }
            
            belowTopBarStrategy(id);
            setTimeout(function(){sidePanelContent.style.opacity = '1';},sidePanelTransitionDuration);
        }
        function hideSidePanel(id){
            let sidePanelOverlay = document.getElementById(id+'Overlay');
            let sidePanel = document.getElementById(id);
            let sidePanelContent = document.getElementById(id+'Content');
            sidePanelContent.style.opacity = '0';
            setTimeout(function(){sidePanel.style.width = '0';},contentTransitionDuration);
            setTimeout(function(){sidePanelOverlay.style.visibility = 'hidden';},transitionDuration);
        }
        
        function setTransitionDuration(id, duration){
            transitionDuration = duration;
            sidePanelTransitionDuration = transitionDuration*sidePanelTransitionRatio;
            contentTransitionDuration = transitionDuration*contentTransitionRatio;
            let sidePanel = document.getElementById(id);
            sidePanel.style.setProperty('--width-transition', 'width '+(sidePanelTransitionDuration)+'ms');
            let sidePanelContent = document.getElementById(id+'Content');
            sidePanelContent.style.setProperty('--opacity-transition', 'opacity '+(contentTransitionDuration)+'ms');
        }
        
        function setContentMargin(id, margin){
            let sidePanelContent = document.getElementById(id+'Content');
            sidePanelContent.style.setProperty('--content-margin', margin);
        }
        
        function setOverlayBgColor(id, overlayBgColor){
            let sidePanelOverlay = document.getElementById(id+'Overlay');
            sidePanelOverlay.style.setProperty('--overlay-bg-color', overlayBgColor);
        }
        
        function setSidePanelBgColor(id, sidePanelBgColor){
            let sidePanel = document.getElementById(id);
            sidePanel.style.setProperty('--sidepanel-bg-color', sidePanelBgColor);
        }
        
        function setAlignToRight(id){
            let sidePanelOverlay = document.getElementById(id+'Overlay');
            sidePanelOverlay.style.setProperty('justify-content', 'flex-end');
        }
        
        function setupSidePanel(id){
            let sidePanelOverlay = document.getElementById(id+'Overlay');
            sidePanelOverlay.onclick = function(event){
                event.stopPropagation();
                hideSidePanel(id);
            };
            
            let sidePanel = document.getElementById(id);
            sidePanel.onclick = function(event){
                event.stopPropagation();
            };
        }
        
        function belowTopBarStrategy(id){
            let topbarDiv = document.getElementById('topbar');
            let sidePanelContent = document.getElementById(id+'Content');
            sidePanelContent.style.marginTop = (topbarDiv.clientHeight+20)+'px';
        }