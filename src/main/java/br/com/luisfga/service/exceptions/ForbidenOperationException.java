package br.com.luisfga.service.exceptions;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ForbidenOperationException extends Exception {
    public ForbidenOperationException(String username){
        Logger.getLogger(ForbidenOperationException.class.getName()).log(Level.SEVERE, "Tentativa suspeita de resetar senha do usuário '{'{0}'}'", username);
    }
}
