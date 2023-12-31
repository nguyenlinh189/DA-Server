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

import com.example.model.Voucher;
import com.example.repository.VoucherRepository;

@RestController
@RequestMapping("/voucher")
public class VoucherController {
	@Autowired
	private VoucherRepository voucherRepo;
	
	@GetMapping("/getAll")
	private List<Voucher> getListVoucher(){
		return voucherRepo.findAll();
	}
	@PostMapping("/save")
	private Voucher saveVoucher(@RequestBody Voucher voucher) {
		return voucherRepo.save(voucher);
	}
	@DeleteMapping("/delete")
	private void deleteVoucher(@RequestParam("code")String code) {
		voucherRepo.deleteById(code);
	}
}
