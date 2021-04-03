package br.com.luisfga.config;

import javax.enterprise.context.ApplicationScoped;
import javax.security.enterprise.authentication.mechanism.http.AutoApplySession;
import javax.security.enterprise.authentication.mechanism.http.CustomFormAuthenticationMechanismDefinition;
import javax.security.enterprise.authentication.mechanism.http.LoginToContinue;
import javax.security.enterprise.authentication.mechanism.http.RememberMe;
import javax.security.enterprise.identitystore.DatabaseIdentityStoreDefinition;

@DatabaseIdentityStoreDefinition(
        dataSourceLookup = "applicationDS",
        callerQuery = "select password from app_user where username = ?",
        groupsQuery = "select role_name from user_role where username = ?",
        hashAlgorithm = TomEEPbkdf2PasswordHash.class,
        priority = 10
)
@AutoApplySession @RememberMe
@CustomFormAuthenticationMechanismDefinition(
        loginToContinue = @LoginToContinue(
                loginPage = "/auth/login",
                errorPage = "/auth/login",
                useForwardToLogin = false
        )
)
@ApplicationScoped
public class ApplicationConfig {

}
