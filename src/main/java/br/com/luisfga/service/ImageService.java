package br.com.luisfga.service;

import br.com.luisfga.domain.entities.Image;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class ImageService {
    
    @PersistenceContext(unitName = "jeebaseJpaUnit")
    private EntityManager em;
    
    public Image findById(long id){
        return em.find(Image.class, id);
    }
}
