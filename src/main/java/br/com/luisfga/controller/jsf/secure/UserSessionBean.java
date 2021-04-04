package br.com.luisfga.controller.jsf.secure;

import br.com.luisfga.domain.entities.AppUser;
import java.io.Serializable;
import java.math.BigDecimal;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

@Named
@SessionScoped
public class UserSessionBean implements Serializable{
    
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
    
    public void clear(){
        this.appUser = new AppUser();
        this.cartTotal = new BigDecimal(0);
    }
    
}