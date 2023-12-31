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

import com.example.model.PlantFamily;
import com.example.repository.PlantFamilyRepository;

@RestController
@RequestMapping("/plantfamily")
public class PlantFamilyController {
	@Autowired
	private PlantFamilyRepository familyRepo;
	
	@GetMapping("/getAll")
	private List<PlantFamily> get(@RequestParam(name="key", defaultValue = "", required = false)String key){
		if(key.isEmpty())
			return familyRepo.findAll();
		else
			return familyRepo.findByVietnameseContainingOrEnglishContaining(key, key);
	}
	@GetMapping("/getByCategory")
	private List<PlantFamily> getByCategory(@RequestParam(name="category", defaultValue ="1", required = false) int category){
		return familyRepo.getFamilyByCategory(category);
	}

	@PostMapping("/save")
	private PlantFamily saveItem(@RequestBody PlantFamily family) {
		return familyRepo.save(family);
	}
	@DeleteMapping("/delete")
	private void deleteItem(@RequestParam(name="id")int id) {
		familyRepo.deleteById(id);
	}
}

//@GetMapping("/get-vietnam")
//private List<PlantFamily> getVietNam(@RequestParam(name="key", defaultValue = "", required = false)String key){
//	if(key.isEmpty())
//		return familyRepo.findByVietnameseNotNull();
//	else
//		return familyRepo.findByVietnameseContainingOrEnglishContaining(key, key);
//}
