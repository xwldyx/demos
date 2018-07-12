package demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import demo.entity.Account;


public interface AccountRepository extends CrudRepository<Account, Integer>,JpaRepository<Account, Integer> {
	
	public Optional<Account> findByUserName(String name);
}
