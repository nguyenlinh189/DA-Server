package com.example.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.model.Contact;
import com.example.repository.ContactRepository;

@RestController
@RequestMapping("/contact")
public class ContactController {
	@Autowired
	private ContactRepository contactRepo;
	
	@GetMapping("/getAll")
	private List<Contact>getListCOntact(@RequestParam(name="status")int status){
		if(status==-1)
			return contactRepo.findAll();
		else {
			if(status==0)
				return contactRepo.findByStatus(false);
			else
				return contactRepo.findByStatus(true);
		}	
	}
	@PostMapping("/save")
	private Contact saveContact(@RequestBody Contact contact) {
		return contactRepo.save(contact);
	}
	@PostMapping("/changeStatus")
	private Contact changeStatus(@RequestBody Contact contact) {
		return contactRepo.save(contact);
	}
	@GetMapping("/delete")
	private void deleteContact(@RequestParam(name="id")int id) {
		contactRepo.deleteById(id);
	}
}
