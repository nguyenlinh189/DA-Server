package com.example.model;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import org.hibernate.annotations.Columns;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name="user")
public class User implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private String name;
	@Column(nullable = false, unique = true)
	private String email;
	
	private String password;
	private String address;
	private String phone;
	
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "favourite_wood",
	joinColumns = @JoinColumn(name = "user_id",
	foreignKey = @ForeignKey(name = "FK_user_id", foreignKeyDefinition = "FOREIGN KEY (user_id) REFERENCES user(id) ON UPDATE CASCADE ON DELETE CASCADE")),
	inverseJoinColumns = @JoinColumn(name = "wood_id", foreignKey = @ForeignKey(name = "FK_wood_id1",
	foreignKeyDefinition = "FOREIGN KEY (wood_id) REFERENCES wood(id) ON UPDATE CASCADE ON DELETE CASCADE")))
	private List<Wood> listFavouriteWood = new ArrayList<Wood>();
	
	@OneToMany(mappedBy = "user", cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
	private List<IdentifyWoodHistory>listIdentify=new ArrayList<>();
	
	private Date createAt;
	private Date updateAt;
	
	@Enumerated(EnumType.STRING)
	private ERole role;
	
	@Enumerated(EnumType.STRING)
	private EProvider provider;
	
	//khi nguoi dùng được đăng kí, enabled =false khi xác minh tài khoản thành công thì enabled=true
	private boolean enabled;
	@PrePersist
	void setEnable() {
//		this.enabled=false;
		try {
		SimpleDateFormat format=new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
		Date date=new Date(System.currentTimeMillis());
		this.createAt=format.parse(format.format(date));
		this.updateAt=format.parse(format.format(date));
		}catch(ParseException e) {
			e.printStackTrace();
		}
	}
}
