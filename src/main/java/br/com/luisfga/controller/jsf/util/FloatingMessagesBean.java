package br.com.luisfga.controller.jsf.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.annotation.Resource;
import javax.enterprise.concurrent.ManagedScheduledExecutorService;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author luisfga
 */
@Named
@SessionScoped
public class FloatingMessagesBean implements Serializable{
    
    private static final Logger logger = LoggerFactory.getLogger(FloatingMessagesBean.class);
    
    @Resource 
    private transient ManagedScheduledExecutorService applicationScheduledExecutorService;
    
    private List<FloatingMessage> messages;

    public FloatingMessagesBean() {
        this.messages = new ArrayList<>();
    }
    
    public List<FloatingMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<FloatingMessage> messages) {
        this.messages = messages;
    }
    
    public void addMessage(FloatingMessage floatingMsg){
        this.messages.add(floatingMsg);
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
        session.setAttribute(FloatingMessage.MESSAGES_LIST_SESSION_KEY, this.messages);
        logger.debug("Executor service? " + applicationScheduledExecutorService);

        //tomee managed executor null after development hot reload (happens only in devel)
        if(applicationScheduledExecutorService != null){
            applicationScheduledExecutorService.schedule(() -> {
                messages.remove(floatingMsg);
                logger.debug("Executing scheduled task.");
            }, floatingMsg.getTimeToLive(), TimeUnit.MILLISECONDS);
        }

    }
    
}
