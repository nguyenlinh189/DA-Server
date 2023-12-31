package com.example.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.Null;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name="wood")
public class Wood implements Serializable{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private String vietnameName;
	private String scientificName;
	@Column(columnDefinition = "TEXT")
	private String commercialName;
	
	@ManyToOne(cascade = CascadeType.MERGE)
	@OnDelete(action = OnDeleteAction.CASCADE)
	private CategoryWood categoryWood;

	@ManyToOne(cascade = CascadeType.MERGE)
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Preservation preservation;

	@ManyToOne(cascade = CascadeType.MERGE)
	@OnDelete(action = OnDeleteAction.CASCADE)
	private PlantFamily family;

	@OneToMany(mappedBy = "wood", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<Image> listImage = new ArrayList<>();
	
	private Integer specificGravity;

	@Column(columnDefinition = "TEXT")
	private String characteristic;

	private String note;

	@ManyToOne(cascade = CascadeType.MERGE)
	@OnDelete(action = OnDeleteAction.CASCADE)
	private AppendixCITES appendixCites;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "wood_area",
	joinColumns = @JoinColumn(name = "wood_id",
	foreignKey = @ForeignKey(name = "FK_wood_id", foreignKeyDefinition = "FOREIGN KEY (wood_id) REFERENCES wood(id) ON UPDATE CASCADE ON DELETE CASCADE")),
	inverseJoinColumns = @JoinColumn(name = "geographical_area_id", foreignKey = @ForeignKey(name = "FK_geographical_area_id",
	foreignKeyDefinition = "FOREIGN KEY (geographical_area_id) REFERENCES geographical_area(id) ON UPDATE CASCADE ON DELETE CASCADE")))
	private List<GeographicalArea> listAreas = new ArrayList<GeographicalArea>();

	@Column(columnDefinition = "TEXT")
	private String color;
	
	@OneToMany(mappedBy = "wood",cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<FeatureWood>listFeatureWood=new ArrayList<>();
}
