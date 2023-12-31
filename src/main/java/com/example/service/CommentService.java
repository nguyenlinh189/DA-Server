package com.example.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.model.Comment;
import com.example.repository.CommentRepository;

public class CommentService {
	@Autowired
	private static CommentRepository commentRepo;
	public static List<Comment>getListCommentByBlog(int blog){
			return commentRepo.findByParentIsNullAndBlogId(blog);
		}
}
