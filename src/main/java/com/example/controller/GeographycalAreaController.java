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

import com.example.model.GeographicalArea;
import com.example.repository.GeographycalAreaRepository;



@RestController
@RequestMapping("/area")
public class GeographycalAreaController {
	
	@Autowired
	private GeographycalAreaRepository areaRepo;
	
	@GetMapping("/getByCategoryWood")
	private List<GeographicalArea>getByCategoryWood(@RequestParam(name="category", defaultValue="1", required = false)int category){
		return areaRepo.getByCategoryWood(category);
	}
	@GetMapping("/getAll")
	private List<GeographicalArea>getListAll(@RequestParam(name="key", defaultValue = "", required = false)String key){
		if(!key.isEmpty())
			return areaRepo.findByEnglishContaining(key);
		return areaRepo.findAll();
	}
	@PostMapping("/save")
	private GeographicalArea saveGeographicalArea(@RequestBody GeographicalArea area) {
		
		return areaRepo.save(area);
	}
	@DeleteMapping("/delete")
	private void deleteGeographicalArea(@RequestParam(name="id")int id) {
		areaRepo.deleteById(id);
	}
}
