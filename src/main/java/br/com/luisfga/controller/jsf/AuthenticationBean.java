package br.com.luisfga.controller.jsf;

import br.com.luisfga.controller.jsf.util.ResourceBean;
import br.com.luisfga.controller.jsf.util.LocaleBean;
import br.com.luisfga.controller.jsf.util.FloatingMessage;
import br.com.luisfga.controller.jsf.util.FloatingMessagesBean;
import br.com.luisfga.domain.entities.AppUser;
import br.com.luisfga.service.AuthenticationService;
import br.com.luisfga.service.exceptions.ConfirmationLinkException;
import br.com.luisfga.service.exceptions.EmailAlreadyTakenException;
import br.com.luisfga.service.exceptions.ForbidenOperationException;
import br.com.luisfga.service.exceptions.InvalidDataException;
import br.com.luisfga.service.exceptions.TimeHasExpiredException;
import br.com.luisfga.service.exceptions.WrongInfoException;
import java.io.IOException;

import java.util.StringTokenizer;
import java.util.UUID;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.security.enterprise.AuthenticationStatus;
import javax.security.enterprise.SecurityContext;
import javax.security.enterprise.authentication.mechanism.http.AuthenticationParameters;
import javax.security.enterprise.credential.UsernamePasswordCredential;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
@RequestScoped
public class AuthenticationBean {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationBean.class);

    @Inject private AuthenticationService authenticationService;
    @Inject private LocaleBean localeBean;
    @Inject private ResourceBean resourceBean;
    @Inject private FloatingMessagesBean floatingMessagesBean;

    @Inject private SecurityContext securityContext;

    //regitration
    private AppUser appUser;
    private String passwordConfirmation;
    //confirmation
    private String encodedUserEmail;
    //login
    private String from;
    //password reset
    private String windowToken;
    
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
    public String getPasswordConfirmation() {
        return passwordConfirmation;
    }
    public void setPasswordConfirmation(String passwordConfirmation) {
        this.passwordConfirmation = passwordConfirmation;
    }
    public String getEncodedUserEmail() {
        return encodedUserEmail;
    }
    public void setEncodedUserEmail(String encodedUserEmail) {
        this.encodedUserEmail = encodedUserEmail;
    }
    public String getFrom() {
        return from;
    }
    public void setFrom(String from) {
        this.from = from;
    }
    public String getWindowToken() {
        return windowToken;
    }
    public void setWindowToken(String windowToken) {
        this.windowToken = windowToken;
    }
    
    //for registration and password reset
    public void validatePasswordEquality(ComponentSystemEvent event){
        
        FacesContext fc = FacesContext.getCurrentInstance();

        UIComponent components = event.getComponent();

        // get password
        UIInput uiInputPassword = (UIInput) components.findComponent("password");
        String passwordVal = uiInputPassword.getLocalValue() == null ? ""
                : uiInputPassword.getLocalValue().toString();

        // get confirm password
        UIInput uiInputConfirmPassword = (UIInput) components.findComponent("passwordConfirmation");
        String confirmPassword = uiInputConfirmPassword.getLocalValue() == null ? ""
                : uiInputConfirmPassword.getLocalValue().toString();
        String passwordConfirmationId = uiInputConfirmPassword.getClientId();

        // Let required="true" do its job.
        if (passwordVal.isEmpty() || confirmPassword.isEmpty()) {
            return;
        }

        if (!passwordVal.equals(confirmPassword)) {

            String errorMessage = FacesContext.getCurrentInstance().getApplication().
                    getResourceBundle(FacesContext.getCurrentInstance(),"global").
                    getString("validation.error.password.confirmation");
            
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,errorMessage, errorMessage);
            
            fc.addMessage(passwordConfirmationId, message);
            fc.renderResponse();

        }

    }
    
    public String register() {
        try {
            authenticationService.registerNewUser(this.appUser);
            
            //enviar username para confirmação
            authenticationService.sendMailForNewUserConfirmation(this.appUser.getUsername(), localeBean.getLocale());
            
        } catch (EmailAlreadyTakenException ex) {
            String errorMessage = resourceBean.getMsgText("global","validation.error.email.already.taken").replace("{0}", this.appUser.getUsername());
            floatingMessagesBean.addMessage(new FloatingMessage(FloatingMessage.Severity.ERROR, errorMessage, 10000));
            
            //mensagem com um link que não será "escapado" no html
            String infoMessage = resourceBean.getMsgText("global","validation.error.account.recovery.link");
            floatingMessagesBean.addMessage(new FloatingMessage(FloatingMessage.Severity.WARN, infoMessage, 20000));
            
            return "register";
        }
        String successMessage = resourceBean.getMsgText("global","action.message.account.created");
        floatingMessagesBean.addMessage(new FloatingMessage(FloatingMessage.Severity.INFO, successMessage, 30000));
        return "register";
    }
    public String confirmRegistration(){
        try {
            authenticationService.confirmRegistration(encodedUserEmail);

        } catch (ConfirmationLinkException clException) {
            String errorMessage = resourceBean.getMsgText("global", "action.error.email.is.empty");
            floatingMessagesBean.addMessage(new FloatingMessage(FloatingMessage.Severity.ERROR, errorMessage, 10000));
            return "login";
        }
        
        String successMessage = resourceBean.getMsgText("global", "action.message.confirmation.completed");
        floatingMessagesBean.addMessage(new FloatingMessage(FloatingMessage.Severity.INFO, successMessage, 4000));
        return "login";
    }
    public String login() throws IOException {
//        try {
//            authenticationService.login(this.appUser.getUsername(), this.appUser.getPassword());
//            
//        } catch (LoginException le) { 
//            String errorMessage = resourceBean.getMsgText("global", "action.error.authentication.exception");
//            floatingMessagesBean.addMessage(new FloatingMessage(FloatingMessage.Severity.ERROR, errorMessage, 7000));
//            return "/auth/login";
//            
//        } catch (PendingEmailConfirmationException pecException) {
//            String errorMessage = resourceBean.getMsgText("global", "action.error.pending.email.confirmation");
//            floatingMessagesBean.addMessage(new FloatingMessage(FloatingMessage.Severity.ERROR, errorMessage, 15000));
//
//            //Enviar username para o usuário
//            authenticationService.sendMailForNewUserConfirmation(this.appUser.getUsername(), localeBean.getLocale());
//            return "/auth/login";
//        }
//        
//        if(from != null)
//            FacesContext.getCurrentInstance().getExternalContext().redirect(from);
        
        FacesContext facesContext = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();
        HttpServletResponse response = (HttpServletResponse)facesContext.getExternalContext().getResponse();
        
        switch (continueAuthentication()) {
            
            case SEND_FAILURE:
                logger.debug("SEND_FAILURE");
                floatingMessagesBean.addMessage(new FloatingMessage(FloatingMessage.Severity.ERROR, "Login failed", 15000));
                return "/auth/login";
            
            case SEND_CONTINUE:
                logger.debug("SEND_CONTINUE");
            case SUCCESS:
                logger.debug("SUCCESS");
                floatingMessagesBean.addMessage(new FloatingMessage(FloatingMessage.Severity.INFO, "Login succeed", 15000));
                
            case NOT_DONE:
        }
        
        logger.debug("request.getParameter(\"loginForm:username\"): " + request.getParameter("loginForm:username"));
        logger.debug("request.getParameter(\"loginForm:password\"): " + request.getParameter("loginForm:password"));
        logger.debug("request.getUserPrincipal(): " + request.getUserPrincipal());
        logger.debug("securityContext.getCallerPrincipal(): " + securityContext.getCallerPrincipal());
        logger.debug("securityContext.getCallerPrincipal().getName(): " + securityContext.getCallerPrincipal().getName());
        logger.debug("securityContext.isCallerInRole(\"ADMIN\")? " + securityContext.isCallerInRole("ADMIN"));
        logger.debug("securityContext.isCallerInRole(\"USER\")? " + securityContext.isCallerInRole("USER"));
        
        if(from != null && !from.isBlank())
            return from;
        else
            return "/secure/dashboard?faces-redirect=true";
    }
    
    private AuthenticationStatus continueAuthentication() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        return securityContext.authenticate(
                (HttpServletRequest) facesContext.getExternalContext().getRequest(),
                (HttpServletResponse) facesContext.getExternalContext().getResponse(),
                AuthenticationParameters.withParams()
                        .credential(new UsernamePasswordCredential(appUser.getUsername(), appUser.getPassword()))
                        .newAuthentication(true)
                        .rememberMe(true)
        );
    }
    
    public String logout(){
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "/index?faces-redirect=true";
    }
    public String recoverPassword() {
        this.windowToken = UUID.randomUUID().toString();
        try {//set user's timed operation window and send email
            authenticationService.prepareRecovery(this.appUser.getUsername(), this.appUser.getBirthday(), this.windowToken);
            authenticationService.sendMailForPasswordReset(this.appUser.getUsername(), localeBean.getLocale(),this.windowToken);
            
        } catch (WrongInfoException wbException) {
            String errorMessage = resourceBean.getMsgText("global","action.error.invalid.informations");
            floatingMessagesBean.addMessage(new FloatingMessage(FloatingMessage.Severity.ERROR, errorMessage, 7000));
            return "/auth/passwordRecover";
        }
        
        //enviar username para reset de senha
        String successMessage = resourceBean.getMsgText("global","action.message.reset.password.email.sent");
        floatingMessagesBean.addMessage(new FloatingMessage(FloatingMessage.Severity.INFO, successMessage, 30000));
        return "/index";
    }
    
    /**
     * Validate if the reset link is within the stipulated timeout.
     */
    public void validateOperationWindow() {
        try {
            this.appUser = authenticationService.validateOperationWindow(encodedUserEmail, windowToken);

        } catch (InvalidDataException ide) {
            String errorMessage = resourceBean.getMsgText("global","action.error.invalid.informations");
            floatingMessagesBean.addMessage(new FloatingMessage(FloatingMessage.Severity.ERROR, errorMessage, 15000));
            
        } catch (ForbidenOperationException foException){
            String errorMessage = resourceBean.getMsgText("global","action.error.forbiden.operation");
            floatingMessagesBean.addMessage(new FloatingMessage(FloatingMessage.Severity.ERROR, errorMessage, 30000));
            
            //TODO revisar essa forma de tentar pegar o ip do cliente.
            HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
            logger.warn(ForbidenOperationException.class.getSimpleName() 
                    + " -> IP suspeito '{'{0}'}'", getClientIpAddress(request));

        } catch (TimeHasExpiredException teException) {
            String errorMessage = resourceBean.getMsgText("global","action.error.invalid.user.temp.window.token");
            floatingMessagesBean.addMessage(new FloatingMessage(FloatingMessage.Severity.ERROR, errorMessage, 30000));
            
        } catch (Exception ex) {
            logger.error("Usuário não encontrado ao tentar resetar senha.", ex);
            String errorMessage = resourceBean.getMsgText("global","exception.unknown");
            floatingMessagesBean.addMessage(new FloatingMessage(FloatingMessage.Severity.ERROR, errorMessage, 30000));
        }
    }
    public String resetPassword() throws Exception {
        authenticationService.resetPassword(this.appUser.getUsername(), this.appUser.getPassword());
        String infoMessage = resourceBean.getMsgText("global","action.message.password.reset");
        floatingMessagesBean.addMessage(new FloatingMessage(FloatingMessage.Severity.INFO, infoMessage, 7000));
        return "login";
    }
    
    //TODO: revisar e usar a melhor estratégia para o fim desejado
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedForHeader = request.getHeader("X-Forwarded-For");
        if (xForwardedForHeader == null) {
            return request.getRemoteAddr();
        } else {
            // As of https://en.wikipedia.org/wiki/X-Forwarded-For
            // The general format of the field is: X-Forwarded-For: client, proxy1, proxy2 ...
            // we only want the client
            return new StringTokenizer(xForwardedForHeader, ",").nextToken().trim();
        }
    }
}