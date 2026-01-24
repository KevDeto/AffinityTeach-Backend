package com.affinityteach.model.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
public class DocenteEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String nombre;
	private Double puntaje = 0.0;
	private Integer cantResenas = 0;

	@ElementCollection
	private List<String> materias = new ArrayList<>();

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "docente_id")
	private List<ResenaEntity> resenas = new ArrayList<>();

	public DocenteEntity(Long id, String nombre, Double puntaje, Integer cantResenas, List<String> materias,
			List<ResenaEntity> resenas) {
		super();
		this.id = id;
		this.nombre = nombre;
		this.puntaje = puntaje;
		this.cantResenas = cantResenas;
		this.materias = materias;
		this.resenas = resenas;
	}

	public DocenteEntity() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public Double getPuntaje() {
		return puntaje;
	}

	public void setPuntaje(Double puntaje) {
		this.puntaje = puntaje;
	}

	public Integer getCantResenas() {
		return cantResenas;
	}

	public void setCantResenas(Integer cantResenas) {
		this.cantResenas = cantResenas;
	}

	public List<String> getMaterias() {
		return materias;
	}

	public void setMaterias(List<String> materias) {
		this.materias = materias;
	}

	public List<ResenaEntity> getResenas() {
		return resenas;
	}

	public void setResenas(List<ResenaEntity> resenas) {
		this.resenas = resenas;
	}

}
