package br.com.luisfga.service.security;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.DisabledAccountException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.DefaultPasswordService;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.jdbc.JdbcRealm;
import org.apache.shiro.subject.PrincipalCollection;

public class ApplicationShiroJdbcRealm extends JdbcRealm {

    public ApplicationShiroJdbcRealm() {
        super.setName("ApplicationDataSourceRealm");
    }
    
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        
        String username = principals.getPrimaryPrincipal().toString();
        Set<String> roles = new HashSet<>();
        
        try {
            Connection conn = dataSource.getConnection();
            
            PreparedStatement ps = conn.prepareStatement("SELECT role_name FROM user_role WHERE username = ?");
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()){
                roles.add(rs.getString(1));
            }
            
            return new SimpleAuthorizationInfo(roles);
            
        } catch (SQLException ex) {
            Logger.getLogger(ApplicationShiroJdbcRealm.class.getName()).log(Level.SEVERE, ex.getMessage());
        }
        
        return null;
        
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws LockedAccountException, AuthenticationException {
        
        UsernamePasswordToken uToken = (UsernamePasswordToken) token;

        try {
            Connection conn = dataSource.getConnection();
            
            PreparedStatement ps = conn.prepareStatement("SELECT username,password,status FROM app_user WHERE username = ?");
            ps.setString(1, uToken.getUsername());
            ResultSet rs = ps.executeQuery();
            
            if(rs.next()) {
                String hashedPassword = rs.getString(2);
                DefaultPasswordService defaultPasswordService = new DefaultPasswordService();

                String password = new String(uToken.getPassword());
                if(!defaultPasswordService.passwordsMatch(password, hashedPassword)){
                    throw new IncorrectCredentialsException();
                }
                
                String status = rs.getString(3);
                if ("locked".equals(status)) {
                    throw new LockedAccountException();
                } else if ("new".equals(status)) {
                    throw new DisabledAccountException();
                }
                
                return new SimpleAuthenticationInfo(rs.getString(1), hashedPassword, getName());
                
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(ApplicationShiroJdbcRealm.class.getName()).log(Level.SEVERE, ex.getMessage());
            throw new AuthenticationException(ex);
        }
     
        return null;
    }

    @Override
    public boolean supports(AuthenticationToken token) {
        return (token instanceof UsernamePasswordToken);
    }

}