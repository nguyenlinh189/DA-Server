package com.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.model.Image;

public interface ImageRepository extends JpaRepository<Image, Integer>{

}
