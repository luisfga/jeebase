package br.com.luisfga.controller.jsf.secure;

import br.com.luisfga.controller.jsf.util.ModalBean;
import br.com.luisfga.controller.jsf.util.FloatingMessage;
import br.com.luisfga.controller.jsf.util.FloatingMessagesBean;
import br.com.luisfga.controller.jsf.util.ResourceBean;
import br.com.luisfga.domain.entities.AppUser;
import br.com.luisfga.service.secure.UserService;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

@Named
@ViewScoped
public class myAccountView implements Serializable{
    
    @Inject UserService userService;
    
    @Inject private FloatingMessagesBean floatingMessagesBean;
    @Inject private ResourceBean resourceBean;
    @Inject private ModalBean modalBean;
    
    @Inject private UserSessionBean userSessionBean;
    
    private AppUser appUser;
    private boolean editPersonalData = false;
    private boolean showAddressInputs = false;
    
    @PostConstruct
    public void init(){
        this.appUser = userSessionBean.getAppUser();
    }
    
    public AppUser getAppUser() {
        return appUser;
    }
    public void setAppUser(AppUser appUser) {
        this.appUser = appUser;
    }
    public boolean isEditPersonalData() {
        return editPersonalData;
    }
    public void setEditPersonalData(boolean editPersonalData) {
        this.editPersonalData = editPersonalData;
    }
    public boolean isShowAddressInputs() {
        return showAddressInputs;
    }
    public void setShowAddressInputs(boolean showAddressInputs) {
        this.showAddressInputs = showAddressInputs;
    }
    
    //User Data
    public void saveUserData(){
        userService.saveUser(this.appUser);
        this.editPersonalData = false;
        String successMessage = resourceBean.getMsgText("global", "action.message.personal.data.saved");
        floatingMessagesBean.addMessage(new FloatingMessage(FloatingMessage.Severity.INFO, successMessage, 5000));
    }
    
}