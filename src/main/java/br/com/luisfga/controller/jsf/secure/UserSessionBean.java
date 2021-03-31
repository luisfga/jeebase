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
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Observes;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
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
        Subject subject = SecurityUtils.getSubject();
        if(subject.isAuthenticated()){
            logger.debug("User "+ subject.getPrincipal() + " is authenticated");
            this.appUser = userService.loadUser(subject.getPrincipal().toString());
        
            //deixando elses separados, apenas para sinalizar possível distinção futura
        } else if(subject.isRemembered()){
            logger.debug("User "+ subject.getPrincipal() + " is remembered");
            this.appUser = new AppUser();
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
        
        Subject subject = SecurityUtils.getSubject();
        if(subject != null) {

            //load user data from db
            String username = subject.getPrincipal().toString();
            this.appUser = userService.loadUser(username);
            
        }
    }
    
    public void clear(){
        this.appUser = new AppUser();
        this.cartTotal = new BigDecimal(0);
    }
    
}