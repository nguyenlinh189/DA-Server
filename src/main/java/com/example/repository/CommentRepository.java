package com.example.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.example.model.Comment;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
	List<Comment> findByParentIsNullAndBlogId(int id);
	
	@Modifying
	@Transactional
	@Query(value = "insert into comment(content,parent_id, blog_id, user_id, createAt) value(?,?,?,?,?)", nativeQuery = true)
	int addComment1(String content, int parent_id, int blog_id, int user_id, Date createAt);
	
	@Modifying
	@Transactional
	@Query(value = "insert into comment(content,blog_id, user_id, createAt) value(?,?,?,?)", nativeQuery = true)
	int addComment2(String content, int blog_id, int user_id, Date createAt);
}
