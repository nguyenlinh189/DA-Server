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

import com.example.model.AppendixCITES;
import com.example.repository.AppendixCITESRepository;

@RestController
@RequestMapping("/cites")
public class AppendixCITESController {
	@Autowired
	private AppendixCITESRepository citesRepo;
	@GetMapping("/get")
	private List<AppendixCITES> get(@RequestParam(name="key", defaultValue = "", required = false)String key){
		if(key.isEmpty())
			return citesRepo.findAll();
		else
		{
			String searchKey=key;
			if(key.contains("I"))
				searchKey="% "+key;
			else	
				searchKey="%"+key+"%";
			return citesRepo.findBykey(searchKey);
		}
	}
	
	@PostMapping("/save")
	private AppendixCITES saveAppendixCITES(@RequestBody AppendixCITES cites) {
		
		return citesRepo.save(cites);
	}
	
	@DeleteMapping("/delete")
	private void deleteAppendixCITES(@RequestParam(name="id")int id) {
		citesRepo.deleteById(id);
	}
}
