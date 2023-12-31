package com.example.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.model.Voucher;

public interface VoucherRepository extends JpaRepository<Voucher, String> {
	
	@Query(value = "select * from voucher "
			+ "where dateStart<= ? and dateEnd >=? "
			+ "and not exists "
			+ "(select id from used_service "
			+ "where code = voucher_code and user_id=? and  status= 'SUCCESS' )", nativeQuery = true)
	List<Voucher> getListVoucherByUsedService(Date dateStart, Date dateEnd, int usedId);
}
