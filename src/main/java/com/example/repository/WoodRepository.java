package com.example.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.example.model.Wood;

public interface WoodRepository extends JpaRepository<Wood,Integer>, JpaSpecificationExecutor<Wood>{
//	@Query(value = "select max(specificGravity) as 'max' from wood", nativeQuery = true)
//	List[] findMaxSpecificGravity();
//	@Query(value = "select min(specificGravity) as 'min' from wood", nativeQuery = true)
//	List[] findMinSpecificGravity();
//	
//	Page<Wood> findByVietnameNameNotNull(Specification<Wood> filter,Pageable pageable);
	Wood findById(int id);
	@Query(value = "select * from wood  where vietnameName like ?", nativeQuery = true)
	Wood findByVietnameName(String name);
	
	@Query(value = "select count(*) from wood", nativeQuery = true)
	int getAmountWood();
	
	List<Wood> findByCategoryWoodId(int id);
}
