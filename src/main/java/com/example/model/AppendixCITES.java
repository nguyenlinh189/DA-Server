package com.example.model;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "appendix_cites")
public class AppendixCITES implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private String name;
	@Column(columnDefinition = "TEXT")
	private String content;
	
	private Date updateAt;
	
	@PrePersist
	void setEnable() {
		try {
		SimpleDateFormat format=new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
		Date date=new Date(System.currentTimeMillis());
		this.updateAt=format.parse(format.format(date));
		}catch(ParseException e) {
			e.printStackTrace();
		}
	}
}
