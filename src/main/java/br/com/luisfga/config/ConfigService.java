/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.luisfga.config;

import br.com.luisfga.domain.entities.AppRole;
import br.com.luisfga.domain.entities.AppUser;
import br.com.luisfga.service.repositories.UserRepository;
import javax.ejb.Stateless;
import javax.inject.Inject;

/**
 *
 * @author luis
 */
@Stateless
public class ConfigService {
    
    @Inject
    private UserRepository userRepositoty;

    public void saveRole(AppRole appRole){
        userRepositoty.saveAppRole(appRole);
    }
    
    public AppRole findRole(String roleName){
        return userRepositoty.findRoleByName(roleName);
    }
    
    public void saveUser(AppUser appUser){
        userRepositoty.save(appUser);
    }
    
}
