/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.luisfga.config;

import br.com.luisfga.domain.entities.AppRole;
import br.com.luisfga.domain.entities.AppUser;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author luis
 */
@Stateless
public class ConfigService {
    
    @PersistenceContext(unitName = "applicationJpaUnit")
    private EntityManager em;

    public void saveRole(AppRole appRole){
        em.persist(appRole);
    }
    
    public void saveUser(AppUser appUser){
        em.persist(appUser);
    }
    
    public void addRoleToUser(String roleName, AppUser appUser){
        AppRole appRole = em.find(AppRole.class, roleName);
        appUser.getRoles().add(appRole);
        em.merge(appUser);
    }
}
