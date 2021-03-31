package br.com.luisfga.config;

import br.com.luisfga.domain.entities.AppRole;
import br.com.luisfga.domain.entities.AppUser;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.persistence.TypedQuery;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.transaction.UserTransaction;
import org.apache.shiro.authc.credential.DefaultPasswordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebListener
public class DatabaseSetup implements ServletContextListener {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseSetup.class);
    
    @Resource
    private UserTransaction tx;

    @PersistenceUnit(unitName = "applicationJpaUnit")
    private EntityManagerFactory emf;
    

    
    @Inject
    private ConfigService configService;
    
    private List<String> basicRoles = Arrays.asList(new String[]{"USER", "ADMIN"});
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        checkRequiredData();
    }

    private void checkRequiredData() {
        logger.info("Looking for required data");
        
        EntityManager em = emf.createEntityManager();

        //check roles
        TypedQuery<String> rolesQuery = em.createQuery("SELECT ar.roleName FROM AppRole ar", String.class);
        List<String> roles = rolesQuery.getResultList();

        for (String basicRole : basicRoles) {
            if (roles.contains(basicRole)) {
                logger.info("Role " + basicRole + " ok");
            } else {
                logger.info("Role " + basicRole + " not found");
                AppRole userRole = new AppRole();
                userRole.setRoleName(basicRole);

                configService.saveRole(userRole);

                logger.info("Role " + basicRole + " created");
            }
        }
        
        //check ADMIN user and his roles
        AppUser admin = em.find(AppUser.class, "admin@system.admin");
        if(admin == null) {
            logger.info("Admin user not found");
            admin = new AppUser();
            admin.setUsername("admin@system.admin");
            DefaultPasswordService dps = new DefaultPasswordService();
            admin.setPassword(dps.encryptPassword("123"));
            admin.setName("admin");
            admin.setBirthday(LocalDate.now());
            admin.setStatus("ok");
            configService.saveUser(admin);
            logger.info("Admin user created");
        } else {
            logger.info("Admin user found.");
        }
        
        //check admin user roles
        Set<String> adminRoles = new HashSet<>();
        admin.getRoles().stream().forEach((appRole) -> { adminRoles.add(appRole.getRoleName()); });
        for (String basicRole : basicRoles) {
            if(!adminRoles.contains(basicRole)) {
                configService.addRoleToUser(basicRole, admin);
                logger.info("Role " + basicRole + " added to " + admin.getUsername());
            }
        }
        logger.info("Everything seams ok");
        
    }
    
}
