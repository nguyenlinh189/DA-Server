package com.example.model;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Calendar;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// nó liên kết với người dùng qua quan hệ một chiều
// nó sẽ được tạo ngya sau khi đăng kí
// nó sẽ hết hạn sau 24 h sau khi đăng kí
// có một giá trị duy nhất và được tạo ngẫu nhiên
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name="verification_token")
public class VerificationToken implements Serializable {
	private static final int EXPIRATION=60*24;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private String token;
	
	@OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
	private User user;
	
	private Date expiryDate;
	@PrePersist
	void calculateExpiryDate() {
		resetExpiryDate();
	}
	public VerificationToken(String token, User user) {
		this.token=token;
		this.user=user;
	}
	public void resetExpiryDate() {
		Calendar cal=Calendar.getInstance();
		cal.setTime(new Timestamp(cal.getTime().getTime()));
		cal.add(Calendar.MINUTE, EXPIRATION);
		this.expiryDate= new Date(cal.getTime().getTime());
	}
	
}
