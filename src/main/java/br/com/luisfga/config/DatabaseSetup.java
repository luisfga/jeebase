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
import javax.persistence.NoResultException;
import javax.persistence.PersistenceUnit;
import javax.persistence.TypedQuery;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.transaction.UserTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebListener
public class DatabaseSetup implements ServletContextListener {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseSetup.class);
    
    @Resource
    private UserTransaction tx;

    @PersistenceUnit(unitName = "jeebaseJpaUnit")
    private EntityManagerFactory emf;
    
    @Inject
    private TomEEPbkdf2PasswordHash defaultPasswordService;
    
    @Inject
    private ConfigService configService;
    
    private List<String> basicRoles = Arrays.asList(new String[]{"USER", "ADMIN"});
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        checkRequiredData();
//        setupDeveloperUser();
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
            admin.setPassword(defaultPasswordService.generate("123".toCharArray()));
            admin.setName("admin");
            admin.setBirthday(LocalDate.now());
            admin.setStatus("ok");
            
            Set<AppRole> adminRoles = new HashSet<>();
            basicRoles.forEach((basicRoleName) -> {
                adminRoles.add(configService.findRole(basicRoleName));
            });
            
            admin.setRoles(adminRoles);
            configService.saveUser(admin);
            logger.info("ADMIN USER CREATED with roles " + basicRoles);
        } else {
            logger.info("Admin user found.");
        }
        
        logger.info("Everything seems ok");
        
    }
    
    private void setupDeveloperUser(){
        
        EntityManager em = emf.createEntityManager();

        //check roles
        TypedQuery<AppUser> develQuery = em.createQuery("SELECT au FROM AppUser au WHERE au.username = 'developer@system.devel'", AppUser.class);
        
        try {
            AppUser appUser = develQuery.getSingleResult();
            logger.info("Developer user found.");
            
        } catch (NoResultException nre){
            logger.info("Setting up new developer user");
            AppUser developer = new AppUser();
            developer.setUsername("developer@system.devel");
            developer.setPassword(defaultPasswordService.generate("123".toCharArray()));
            developer.setName("developer");
            developer.setBirthday(LocalDate.now());
            developer.setStatus("ok");
            configService.saveUser(developer);
            logger.info("New developer user created.");
        }
        
    }

    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
        
    }
}
