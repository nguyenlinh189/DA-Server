package com.example.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceStat extends Service{
	private int totalMoney;

	public ServiceStat(int id, String name, int duration, int price, int totalMoney) {
		super(id, name, duration, price);
		this.totalMoney = totalMoney;
	}
	
}
