package br.com.luisfga.controller.jsf.secure;

import br.com.luisfga.controller.jsf.util.FloatingMessagesBean;
import br.com.luisfga.controller.jsf.util.ModalBean;
import br.com.luisfga.controller.jsf.util.ResourceBean;
import br.com.luisfga.domain.entities.AppUser;
import br.com.luisfga.service.events.LoginEvent;
import br.com.luisfga.service.events.LogoutEvent;
import br.com.luisfga.service.secure.UserService;
import java.io.Serializable;
import java.math.BigDecimal;
import java.security.Principal;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Observes;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.security.enterprise.SecurityContext;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
@SessionScoped
public class UserSessionBean implements Serializable{
    
    private final static Logger logger = LoggerFactory.getLogger(UserSessionBean.class);
    
    @Inject private UserService userService;
    @Inject private FloatingMessagesBean floatingMessagesBean;
    @Inject private ResourceBean resourceBean;
    @Inject private ModalBean modalBean;
    @Inject private transient SecurityContext securityContext;
    
    private AppUser appUser;
    private BigDecimal cartTotal = new BigDecimal(0);

    public AppUser getAppUser() {
        return appUser;
    }
    public void setAppUser(AppUser appUser) {
        this.appUser = appUser;
    }
    public BigDecimal getCartTotal() {
        return cartTotal;
    }
    public void setCartTotal(BigDecimal cartTotal) {
        this.cartTotal = cartTotal;
    }
    
    @PostConstruct
    public void init(){

        Principal principal = securityContext.getCallerPrincipal();
        if(principal != null){
            logger.debug("User "+ principal + " is authenticated");
            this.appUser = userService.loadUser(principal.getName());
        
        } else {
            this.appUser = new AppUser();
        }
    }
    
    public void handleLogout(@Observes @LogoutEvent String message){
        logger.debug("Event handling: " + message);
        ((HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest()).getSession().invalidate();
        clear();
    }
    
    public void handleLogin(@Observes @LoginEvent String message){
        logger.debug("Event handling: " + message);
        ((HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest()).changeSessionId();
        
        Principal principal = securityContext.getCallerPrincipal();
        if(principal != null) {
            //load user data from db
            this.appUser = userService.loadUser(principal.getName());
        }
    }
    
    public void clear(){
        this.appUser = new AppUser();
        this.cartTotal = new BigDecimal(0);
    }
    
}