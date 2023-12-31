package com.example.controller;

import java.util.ArrayList;
import java.sql.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.example.model.ServiceStat;
import com.example.repository.ServiceRepository;

@RestController
@RequestMapping("/stat")
public class ServiceStatController {
	@Autowired
	private ServiceRepository serviceRepo;
	@GetMapping("/getServiceStat")
	private List<ServiceStat> getServiceStat(@RequestParam(name="dateStart")Date dateStart,@RequestParam(name="dateEnd") Date dateEnd ){
		List[] stats=serviceRepo.getServiceStat(dateStart, dateEnd);
		List<ServiceStat> list=new ArrayList<>();
		for(List o:stats) {
			ServiceStat serviceStat=new ServiceStat();
			serviceStat.setId(Integer.parseInt(o.get(0).toString()));
			serviceStat.setName(o.get(2).toString());
			serviceStat.setDuration(Integer.parseInt(o.get(1).toString()));
			serviceStat.setPrice(Integer.parseInt(o.get(3).toString()));
			serviceStat.setTotalMoney(Integer.parseInt(o.get(4).toString()));
			list.add(serviceStat);
		}
		return list;
	}
}
