package br.com.luisfga.controller.jsf.util;

import java.util.ResourceBundle;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

@Named
@ApplicationScoped
public class ResourceBean {
    
    @Inject
    private LocaleBean localeBean;
    
    public String getMsgText(String bundle,String key) {
        return ResourceBundle.getBundle(bundle,localeBean.getLocale()).getString(key);
    }
    
    public String getMsgText(String key) {
        return ResourceBundle.getBundle("global",localeBean.getLocale()).getString(key);
    }

    public String getParameterizedMsgText(String key, String[] params) {
        return getParameterizedMsgText("global", key, params);
    }
    
    public String getParameterizedMsgText(String bundle,String key, String[] params) {
        
        String message = ResourceBundle.getBundle(bundle,localeBean.getLocale()).getString(key);
        
        for (int i = 0; i < params.length; i++) {
            String parameterToken = "{"+i+"}";
            message = message.replace(parameterToken, params[i]);
        }
        
        /*
        TODO se for reaproveitar esse método, é uma boa ideia tratar 
        a quantidade de parâmetros, lançando um excessão se necessário.
        */
        
        return message;
    }    
}
