package br.com.luisfga.service;

import br.com.luisfga.config.Property;
import br.com.luisfga.domain.entities.AppUser;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;


@Stateless
public class MailHelper {

    @PersistenceContext(unitName = "applicationJpaUnit")
    private EntityManager em;
    
    @Resource
    private Session applicationMailSession;
    
    @Inject 
    @Property("app.name")
    private String appName;

    @Inject 
    @Property("app.base.hostname")
    private String appBaseHostname;
    
    @Inject 
    @Property("app.context")
    private String appContext;
    
    @Inject 
    @Property("auth.pages.preffix")
    private String authPagesPreffix;
    
    public void enviarEmailConfirmacaoNovoUsuario(String username, Locale locale) {
        try {
            enviarEmail(username, locale, "confirmation.email.subject", "confirmation.email.message", getParametrosEmailConfirmacao(username));
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(MailHelper.class.getName()).log(Level.SEVERE, "Erro ao pegar parâmetros para o email de confirmação de nova conta", ex);
        }
    }
    
    public void enviarEmailResetSenha(String username, Locale locale, String windowToken) {
        try {
            enviarEmail(username, locale, "password.reset.email.subject", "password.reset.email.message", getParametrosEmailRedefinicaoSenha(username, windowToken));
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(MailHelper.class.getName()).log(Level.SEVERE, "Erro ao pegar parâmetros para o email de confirmação de nova conta", ex);
        }
    }    
    
    private void enviarEmail(String username, Locale locale, String subjectKey, String messageKey, Object[] messageParams){
        Executors.newSingleThreadExecutor().execute(() -> {
            
            ResourceBundle bundle = ResourceBundle.getBundle("mail", locale);
            
            try {

                String messageText = String.format(bundle.getString(messageKey),messageParams);
                
                Message message = new MimeMessage(applicationMailSession);

                message.setFrom(new InternetAddress(applicationMailSession.getProperty("mail.smtp.user"), appName));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(username));
                message.setSubject(bundle.getString(subjectKey));

                MimeBodyPart mimeBodyPart = new MimeBodyPart();
                mimeBodyPart.setContent(messageText, "text/html; charset=UTF-8");

                Multipart multipart = new MimeMultipart();
                multipart.addBodyPart(mimeBodyPart);

                message.setContent(multipart);

                Transport.send(message);
                
            } catch (AddressException ex){
                //TODO - fazer o que, aqui?
            } catch (MessagingException | UnsupportedEncodingException ex) {
                //TODO - fazer o que, aqui?
            }
            
        });
    }
    
    private String getUserName(String username){
        AppUser appUser = em.find(AppUser.class, username);
        return appUser.getName();
    }
    
    private Object[] getParametrosEmailConfirmacao(String username) throws UnsupportedEncodingException{
        return new Object[]{
            getUserName(username),//the user name of dest email
            appBaseHostname, //the app hostname for building the link
            appContext+authPagesPreffix, //the app context root to append on the link being built
            Base64.getEncoder().encodeToString(username.getBytes("UTF-8"))//dest email encoded and used as link request parameter
        };
    }
    
    private Object[] getParametrosEmailRedefinicaoSenha(String email, String windowToken) throws UnsupportedEncodingException{
        return new Object[]{
            getUserName(email),//the user name of dest email
            appBaseHostname, //the app hostname for building the link
            appContext+authPagesPreffix, //the app context root to append on the link being built
            Base64.getEncoder().encodeToString(email.getBytes("UTF-8")),//dest email encoded and used as link request parameter
            windowToken
        };
    }
}