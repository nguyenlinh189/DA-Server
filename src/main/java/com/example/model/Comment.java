package com.example.model;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name="comment")
public class Comment implements Serializable{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	@Column(columnDefinition = "TEXT")
	private String content;
	@ManyToOne(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JsonIgnore
	private Blog blog;
	@ManyToOne
	private User user;
//	// de gioi thieu danh muc goc cua no
	@OneToOne
	@JoinColumn(name="parent_id")
	@JsonIgnore
	private Comment parent;
	// de gioi thieu toi cac phan tu con cua no
	@OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
	private List<Comment> listChildren=new ArrayList<Comment>();
	
	private Date createAt;
	
}
