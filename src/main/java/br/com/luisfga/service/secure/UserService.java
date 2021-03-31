package br.com.luisfga.service.secure;

import br.com.luisfga.domain.entities.AppUser;
import br.com.luisfga.domain.repositories.UserRepository;
import br.com.luisfga.service.util.SearchPaginator;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
public class UserService {
    
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    
    @Inject
    private UserRepository userRepository;
    
    public AppUser loadUser(String username){
        AppUser appUser = userRepository.findBy(username);
        return appUser;
    }
    
    public void saveUser(AppUser appUser){
        userRepository.save(appUser);
    }

    public void search(String namePart, String orderBy, boolean orderAsc, SearchPaginator<AppUser> paginator){
        
        EntityManager em = userRepository.getEntityManagerFactory().createEntityManager();
        CriteriaBuilder builder = em.getCriteriaBuilder();
        
        CriteriaQuery<AppUser> searchQuery = builder.createQuery(AppUser.class);
        Root<AppUser> entity = searchQuery.from(AppUser.class);
        
        List<Predicate> predicates = new ArrayList<>();
        
        if(namePart != null && !namePart.trim().isEmpty()){
            predicates.add(
                    builder.like(
                            builder.lower(entity.get("name")), 
                            "%"+namePart.toLowerCase()+"%")
            );
        }
        
        CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);
        countQuery.select(builder.count(countQuery.from(AppUser.class)))
                .where(predicates.toArray(new Predicate[0]));
        paginator.setCount(em.createQuery(countQuery).getSingleResult());
        
        searchQuery.select(entity).where(predicates.toArray(new Predicate[0]));
        if(orderBy != null && !orderBy.trim().isEmpty()){
            Path<AppUser> orderByPath = entity.get(orderBy);
            if(orderByPath != null){
                if(orderAsc){
                    searchQuery.orderBy(builder.asc(orderByPath));
                } else {
                    searchQuery.orderBy(builder.desc(orderByPath));
                }
            }
        } else {
            Path<AppUser> orderByPath = entity.get("name");
            searchQuery.orderBy(builder.asc(orderByPath));
        }
        paginator.setTypedQuery(em.createQuery(searchQuery));
        
    }
}
