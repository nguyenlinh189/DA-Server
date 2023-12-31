package com.example.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.model.Blog;

public interface BlogRepository extends JpaRepository<Blog, Integer>{
	List<Blog> findByStatus(String status);
	Page<Blog> findByTitleContainingAndCategoryBlogIdAndStatus(Pageable pageable,String key, int cateId, String status);
	Page<Blog> findByTitleContainingAndStatus(Pageable pageable,String key, String status);
	@Query(value = "select * from blog order by dateUpdate desc limit 8", nativeQuery = true)
	List<Blog> getLimit5();
	Blog findById(int id);
	
	@Query(value = "select count(*) from blog", nativeQuery = true)
	int getAmountBlog();
}
