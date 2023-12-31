package com.example.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.model.Feature;

public interface FeatureRepository extends JpaRepository<Feature,Integer>{
	List<Feature> findByParentFeatureIsNull();
}
