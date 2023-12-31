package com.example.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.model.Contact;

public interface ContactRepository extends JpaRepository<Contact, Integer>{
	List<Contact> findByStatus(Boolean status);
}
