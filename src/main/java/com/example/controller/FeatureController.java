package com.example.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.model.Feature;
import com.example.repository.FeatureRepository;

@RestController
@RequestMapping("/feature")
public class FeatureController {
	@Autowired
	private FeatureRepository repo;
	
	@GetMapping
	private List<Feature>getList(){
		return repo.findByParentFeatureIsNull();
	}
}
