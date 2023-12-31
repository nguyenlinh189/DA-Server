package com.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.model.User;

@Repository
public interface UserRepository extends JpaRepository<User,Integer>{
	boolean existsByEmail(String email);
	User findByEmail(String email);
	
	@Modifying
	@Transactional
	@Query(value="update user set address=?, name=?, phone=? where id=?", nativeQuery = true)
	int updateUser(String address, String name, String phone, int id);		
}
