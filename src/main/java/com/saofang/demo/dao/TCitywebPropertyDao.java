package com.saofang.demo.dao;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.saofang.demo.model.TCitywebProperty;

public interface TCitywebPropertyDao
		extends CrudRepository<TCitywebProperty, Integer>, JpaRepository<TCitywebProperty, Integer>, 
		JpaSpecificationExecutor<TCitywebProperty>, Serializable {

	@Query(value="select * from t_cityweb_property p where p.prop_id = :id", nativeQuery=true)
	public List<TCitywebProperty> getProp(@Param(value = "id") int id);
	
	@Transactional
	@Modifying
	@Query(value="delete from TCitywebProperty h where h.propId in ?1")
	public void deleteByIdIn(Collection<Integer> ids);
	
	public List<TCitywebProperty> findByPropKey(String key);
}