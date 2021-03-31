package br.com.luisfga.controller.jsf;

import br.com.luisfga.controller.jsf.util.ResourceBean;
import br.com.luisfga.domain.entities.AppUser;
import br.com.luisfga.service.secure.UserService;
import br.com.luisfga.service.util.SearchPaginator;
import java.io.IOException;
import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
@SessionScoped
public class SearchView implements Serializable{
    
    private final Logger logger = LoggerFactory.getLogger(SearchView.class);
    
    @Inject private UserService userService;
    @Inject private ResourceBean resourceBean;
    @Inject private transient SearchPaginator<AppUser> paginator;
    
    private String searchTxt;
    private String searchInfo;
    
    private String orderBy = "name_Asc";

    public SearchPaginator<AppUser> getPaginator() {
        return paginator;
    }
    public void setPaginator(SearchPaginator<AppUser> paginator) {
        this.paginator = paginator;
    }
    public String getSearchTxt() {
        return searchTxt;
    }
    public void setSearchTxt(String searchTxt) {
        this.searchTxt = searchTxt;
    }
    public String getSearchInfo() {
        return searchInfo;
    }
    public void setSearchInfo(String searchInfo) {
        this.searchInfo = searchInfo;
    }
    public String getOrderBy() {
        return orderBy;
    }
    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public String newSearch(){
        String[] orderBySplit = orderBy.split("_");
        String orderAttr = orderBySplit[0];
        String orderDirection = orderBySplit[1];
        boolean isAsc = orderDirection.equals("Asc");
        userService.search(searchTxt, orderAttr, isAsc, paginator);
        
        this.searchInfo = resourceBean.getParameterizedMsgText("search.result", new String[]{this.searchTxt});
        
        return "/search?faces-redirect=true";
    }
    
    public void onChangePageSize(){
        paginator.goToFirstPage();
    }

    /**
     * For cases of null searchTxt, e.g., back buttons after session expiration.
     * Redirects to context root path on those cases.
     * Intended to be used with '<f:event type="preRenderView" listener="#{searchView.handleParamsNullity}"/>'.
     */
    public void handleParamsNullity(){
        if (this.searchTxt == null) {
            try {
                logger.debug("Redirecting because of null search");
                String contextPath = FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();
                FacesContext.getCurrentInstance().getExternalContext().redirect(contextPath);
            } catch (IOException ex) {
                logger.error(ex.getMessage());
            }            
        }
    }
    
    public void reOrder(){
        String[] orderBySplit = orderBy.split("_");
        String orderAttr = orderBySplit[0];
        String orderDirection = orderBySplit[1];
        boolean isAsc = orderDirection.equals("Asc");

        userService.search(searchTxt, orderAttr, isAsc, paginator);
    }

}
