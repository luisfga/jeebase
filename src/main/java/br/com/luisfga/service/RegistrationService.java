package br.com.luisfga.service;

import br.com.luisfga.config.TomEEPbkdf2PasswordHash;
import br.com.luisfga.domain.entities.AppRole;
import br.com.luisfga.domain.entities.AppUser;
import br.com.luisfga.domain.entities.AppUserOperationWindow;
import br.com.luisfga.service.repositories.UserRepository;
import br.com.luisfga.service.exceptions.ConfirmationLinkException;
import br.com.luisfga.service.exceptions.EmailAlreadyTakenException;
import br.com.luisfga.service.exceptions.ForbidenOperationException;
import br.com.luisfga.service.exceptions.InvalidDataException;
import br.com.luisfga.service.exceptions.TimeHasExpiredException;
import br.com.luisfga.service.exceptions.WrongInfoException;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import javax.ejb.Stateless;
import javax.inject.Inject;

@Stateless
public class RegistrationService {
    
    @Inject private UserRepository userRepository;
    @Inject private MailService mailHelper;
    @Inject private TomEEPbkdf2PasswordHash defaultPasswordService;
    
    public void registerNewUser(AppUser newAppUser) throws EmailAlreadyTakenException {
        //verifica se o email informado está disponível
        if(userRepository.findBy(newAppUser.getUsername()) != null) 
            throw new EmailAlreadyTakenException();
        
        //configura novo usuário
        newAppUser.setStatus("new");
        newAppUser.setJoinTime(OffsetDateTime.now());

        //encrypt password
        String encryptedPassword = defaultPasswordService.generate(newAppUser.getPassword().toCharArray());
        newAppUser.setPassword(encryptedPassword);
        
        //configura ROLE
        Set<AppRole> roles = new HashSet<>(userRepository.findRolesForNewUsers());
        newAppUser.setRoles(roles);
        
        //salva novo usuário
        userRepository.save(newAppUser);
    }
    
    public void sendMailForNewUserConfirmation(String destEmail, Locale locale) {
        mailHelper.enviarEmailConfirmacaoNovoUsuario(destEmail, locale);
    }
    
    public void confirmRegistration(String encodedEmail) throws ConfirmationLinkException {
        if (encodedEmail == null || encodedEmail.isEmpty()) {
            throw new ConfirmationLinkException();
        }
        String username = decodeEmail(encodedEmail);

        //everythings ok, pega o usuário e seta 'ok'
        AppUser appUser = userRepository.findBy(username);
        appUser.setStatus("ok");//seta status para OK, i.e. CONFIRMADO
        userRepository.save(appUser);
    }
    
    public void prepareRecovery(String username, LocalDate birthday, String token) throws WrongInfoException {

        //validate birtyday
        AppUser appUser = userRepository.findBy(username);
        if (appUser == null || !appUser.getBirthday().equals(birthday)) {
            throw new WrongInfoException();
        }
        
        //reaproveitamento de janela de operação. Caso já exista uma, use-a
        AppUserOperationWindow operationWindow = userRepository.findOperationWindow(username).getOptionalResult();
        if(operationWindow == null){
            operationWindow = new AppUserOperationWindow();
            operationWindow.setAppUser(appUser);
        }
        operationWindow.setWindowToken(token);
        operationWindow.setInitTime(OffsetDateTime.now());

        appUser.setOperationWindow(operationWindow);
        userRepository.save(appUser);
    }
    
    public void sendMailForPasswordReset(String username, Locale locale,String windowToken) {
        mailHelper.enviarEmailResetSenha(username, locale, windowToken);
    }
    
    /**
     * Valida se o usuário tem uma janela de operação "aberta".Essa janela serve para estabelecer um tempo limite 
     * para operação de redefinição de senha, por exemplo.A janela se "fecha" após 7 minutos.
     * @param encodedUserEmail - email codificação em Base64, para mascarar sua passagem no request por link.
     * @param token - token salvo no banco e que foi enviado por email, retornando como parâmetro no link.
     * @return - retorna o email decodificado.
     * @throws InvalidDataException - quando houver erros nos dados
     * @throws ForbidenOperationException - para o caso de
     * estar faltando algum parâmetro, o que pode significar tentativa de fraudar a operação.
     * @throws TimeHasExpiredException  - para quando já houver decorrido os 7 minutos de limite.

     */
    public AppUser validateOperationWindow(String encodedUserEmail, String token) 
            throws InvalidDataException, ForbidenOperationException, TimeHasExpiredException {

        //validate email and token uuid
        if (encodedUserEmail == null || encodedUserEmail.isEmpty() || token == null || token.isEmpty()) {
            throw new InvalidDataException();
        }
        String decodedEmail = decodeEmail(encodedUserEmail);

        //verify timeout
        AppUser appUser = userRepository.findBy(decodedEmail);
        if (appUser.getOperationWindow() == null) {
            throw new ForbidenOperationException(decodedEmail);

        } else if (appUser.getOperationWindow().getInitTime().plusMinutes(7).isBefore(OffsetDateTime.now())){
            //se o tempo salvo + 7 minutos for anterior a agora, o limite de 7 minutos já passou

            appUser.setOperationWindow(null); //(deleta/fecha) janela de operação
            userRepository.save(appUser);
            throw new TimeHasExpiredException();

        } else if (!appUser.getOperationWindow().getWindowToken().equals(token)){
            throw new ForbidenOperationException(decodedEmail);
        }
        return appUser;
    }
    
    public void resetPassword(String username, String password) {
        AppUser appUser = userRepository.findBy(username);

        appUser.setPassword(defaultPasswordService.generate(password.toCharArray()));

        appUser.setOperationWindow(null); //deleta/fecha janela de operação
        userRepository.save(appUser);
    }
    
    private String decodeEmail(String encodedEmail){
        byte[] decodedUserEmailBytes = Base64.getDecoder().decode(encodedEmail);
        return new String(decodedUserEmailBytes);
    }
    
}