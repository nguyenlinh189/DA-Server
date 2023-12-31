package com.example.model;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="transaction_history")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TransactionHistory implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private int amount;
	private String bankCode;
	private String bankTranNo;
	private String orderInfor;
	private String cardType;
	private Date payDate;
	private String responseCode;
	private String tmnCode;
	private String transactionStatus;
	private String transactionNo;
	
	public void setPayDate(String date) throws ParseException {
		SimpleDateFormat inputFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		payDate = inputFormat.parse(date);
	}
}
