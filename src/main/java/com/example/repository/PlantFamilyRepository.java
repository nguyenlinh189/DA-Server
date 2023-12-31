package com.example.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.model.PlantFamily;

public interface PlantFamilyRepository extends JpaRepository<PlantFamily, Integer>{
	List<PlantFamily> findByVietnameseContainingOrEnglishContaining(String key1, String key2);
//	List<PlantFamily> findByVietnameseNotNull();
	
	@Query(value = "select distinct plant_family.* from plant_family, wood where plant_family.id=wood.family_id and wood.categoryWood_id=?", nativeQuery = true)
	List<PlantFamily> getFamilyByCategory(int category);
}
