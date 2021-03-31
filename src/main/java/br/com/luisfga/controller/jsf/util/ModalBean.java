package br.com.luisfga.controller.jsf.util;

import java.io.Serializable;
import javax.el.MethodExpression;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
@ViewScoped
public class ModalBean implements Serializable{
    
    private static final Logger logger = LoggerFactory.getLogger(ModalBean.class);
    
    private boolean showModal = false;
    private String message;
    private MethodExpression actionExpression;

    public boolean isShowModal() {
        return showModal;
    }

    public void setShowModal(boolean showModal) {
        this.showModal = showModal;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public MethodExpression getActionExpression() {
        return actionExpression;
    }

    public void setActionExpression(MethodExpression actionExpression) {
        this.actionExpression = actionExpression;
    }

    public void confirm(){
        this.actionExpression.invoke(FacesContext.getCurrentInstance().getELContext(), null);
    }
}
