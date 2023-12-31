package com.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.model.CategoryBlog;

public interface CategoryBlogRepository extends JpaRepository<CategoryBlog, Integer> {

}
