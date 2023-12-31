package com.example.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.model.Service;


public interface ServiceRepository extends JpaRepository<Service, Integer>{
	Service findById(int id);
	
	@Query(value = "SELECT count(*) FROM service", nativeQuery = true)
	int getAmountDichVu();
	
	@Query(value = "SELECT b.*, \n"
			+ "    SUM(b.price - IFNULL(v.amount, 0)) AS 'totalMoney'\n"
			+ "FROM used_service AS a\n"
			+ "JOIN service AS b ON a.service_id = b.id\n"
			+ "LEFT JOIN voucher AS v ON a.voucher_code = v.code\n"
			+ "WHERE a.date_start >= ? \n"
			+ "    AND a.date_start <= ? \n"
			+ "    AND a.status = 'SUCCESS'\n"
			+ "GROUP BY b.id\n"
			+ "ORDER BY totalMoney DESC;", nativeQuery = true)
	List[] getServiceStat(Date dateStart, Date dateEnd);
}
