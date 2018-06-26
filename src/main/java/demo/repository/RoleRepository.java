package demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import demo.entity.Role;


public interface RoleRepository extends CrudRepository<Role, Integer>,JpaRepository<Role, Integer> {
	
	@Query(value = "select r.* from role r, account_role ar where ar.acc_id = ?1 and ar.role_id = r.id", nativeQuery = true) 
	public List<Role> findByAccountId(long accId);
	
	@Query(value = "select r.* from role r, resource_role rr where rr.resource_id = ?1 and rr.role_id = r.id", nativeQuery = true)  
    public List<Role> findByResourceId(long resourceId);  
}
