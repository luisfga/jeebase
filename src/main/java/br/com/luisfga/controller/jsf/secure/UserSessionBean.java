package br.com.luisfga.controller.jsf.secure;

import br.com.luisfga.domain.entities.AppUser;
import br.com.luisfga.service.events.LoginEvent;
import br.com.luisfga.service.events.LogoutEvent;
import br.com.luisfga.service.secure.UserService;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Observes;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
@SessionScoped
public class UserSessionBean implements Serializable{
    
    private final static Logger logger = LoggerFactory.getLogger(UserSessionBean.class);

    @Inject private UserService userService;

    private AppUser appUser;

    @PostConstruct
    public void init(){
        this.appUser = new AppUser();
    }
    
    public AppUser getAppUser() {
        return appUser;
    }
    public void setAppUser(AppUser appUser) {
        this.appUser = appUser;
    }
    public void handleLogout(@Observes @LogoutEvent String username){
        logger.debug("Handling logout event for " + username);
        clear();
    }
    
    public void handleLogin(@Observes @LoginEvent String username){
        logger.debug("Handling login event for " + username);
        this.appUser = userService.loadUser(username);
    }
    
    public void clear(){
        this.appUser = new AppUser();
    }
    
    public boolean isAuthenticated(){
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        return request.getRemoteUser() != null;
    }
    
}