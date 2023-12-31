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

import com.example.model.Preservation;
import com.example.repository.PreservationRepository;
import com.google.api.client.util.Key;

@RestController
@RequestMapping("/preservation")
public class PreservationController {
	@Autowired
	private PreservationRepository preRepo;
	
	@GetMapping("/get")
	private List<Preservation> get(@RequestParam(name="key",defaultValue = "", required = false)String key){
		if(key.isEmpty())
			return preRepo.findAll();
		else
			return preRepo.findByVietnameseContainingOrEnglishContainingOrAcronymContaining(key, key, key);
	}
	@PostMapping("/save")
	private Preservation save(@RequestBody Preservation pre) {
		return preRepo.save(pre);
	}
	@DeleteMapping("/delete")
	private void delete(@RequestParam(name="id")int id) {
		preRepo.deleteById(id);
	}
}
