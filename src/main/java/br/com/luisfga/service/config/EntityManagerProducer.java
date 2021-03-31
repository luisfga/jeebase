package br.com.luisfga.service.config;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@ApplicationScoped
public class EntityManagerProducer{
    
    //or manual bootstrapping
    @PersistenceContext(unitName = "applicationJpaUnit")
    private EntityManager em;

    @Produces
    @Default
    @RequestScoped
    public EntityManager create(){
        return this.em;
    }

}
