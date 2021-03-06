package br.com.luisfga.service.repositories;

import br.com.luisfga.domain.entities.AppRole;
import br.com.luisfga.domain.entities.AppUser;
import br.com.luisfga.domain.entities.AppUserOperationWindow;
import java.util.List;
import java.util.Set;
import org.apache.deltaspike.data.api.AbstractFullEntityRepository;
import org.apache.deltaspike.data.api.Query;
import org.apache.deltaspike.data.api.QueryResult;
import org.apache.deltaspike.data.api.Repository;

/**
 *
 * @author luis
 */

@Repository
public abstract class UserRepository extends AbstractFullEntityRepository<AppUser, String>{

    @Query("SELECT au.status FROM AppUser au WHERE au.username = ?1")
    public abstract String getStatus(String username);
    
    @Query("SELECT ar FROM AppRole ar WHERE ar.roleName IN ('USER')")
    public abstract List<AppRole> findRolesForNewUsers();

    @Query("SELECT ow FROM AppUserOperationWindow ow WHERE ow.username = ?1")
    public abstract QueryResult<AppUserOperationWindow> findOperationWindow(String username);
    
    @Query("SELECT ar FROM AppRole ar WHERE ar.roleName = ?1")
    public abstract AppRole findRoleByName(String roleName);
    
    public void saveAppRole(AppRole appRole){
        entityManager().persist(appRole);
    }
}
