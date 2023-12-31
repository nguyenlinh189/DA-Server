package com.example.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.model.CategoryBlog;
import com.example.repository.CategoryBlogRepository;



@RestController
@RequestMapping("/category-blog")
public class CategoryBlogController {
	@Autowired
	private CategoryBlogRepository cateRepo;
	@GetMapping("/getAll")
	private List<CategoryBlog> getAllList(){
		return cateRepo.findAll();
	}
	@PostMapping("/save")
	private CategoryBlog saveCategoryBlog(@RequestBody CategoryBlog cate) {
		
		return cateRepo.save(cate);
	}
	@DeleteMapping("/delete")
	private void deleteCategoryBlog(@RequestParam(name="id")int id) {
		cateRepo.deleteById(id);
	}
}
