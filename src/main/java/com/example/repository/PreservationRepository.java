package com.example.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.model.Preservation;

public interface PreservationRepository extends JpaRepository<Preservation, Integer> {
	List<Preservation> findByVietnameseContainingOrEnglishContainingOrAcronymContaining(String key1, String key2, String key3);
}
