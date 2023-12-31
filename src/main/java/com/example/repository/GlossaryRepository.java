package com.example.repository;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.model.Glossary;
@Repository
public interface GlossaryRepository extends JpaRepository<Glossary, Integer> {
	@Query(value = "select * from glossary where vietnamese like %:key% or english like %:key%",
			nativeQuery = true)
	Page<Glossary> findByKey(Pageable pageable, @Param("key")String key);
	
	List<Glossary>findByVietnameseContaining(String key);
}
