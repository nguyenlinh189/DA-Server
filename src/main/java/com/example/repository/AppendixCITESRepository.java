package com.example.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.model.AppendixCITES;

@Repository
public interface AppendixCITESRepository extends JpaRepository<AppendixCITES, Integer> {
	@Query(value = "select * from appendix_cites where name like ?", nativeQuery = true)
	List<AppendixCITES> findBykey(String key);
}
