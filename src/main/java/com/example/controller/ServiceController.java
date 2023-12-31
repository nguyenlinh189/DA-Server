package com.example.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.model.Service;
import com.example.repository.ServiceRepository;


@RestController
@RequestMapping("/service")
public class ServiceController {
	@Autowired
	private ServiceRepository serviceRepo;
	@GetMapping("/get")
	private List<Service>getServices(){
		return serviceRepo.findAll();
	}
	@GetMapping("/getAmountService")
	private int getAmountService() {
		return serviceRepo.getAmountDichVu();
	}
}
