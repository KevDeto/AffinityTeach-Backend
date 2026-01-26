package com.affinityteach.model.entity;

import java.util.ArrayList;
import java.util.List;

public class DocenteEntity {
	private String id;

	private String nombre;
	private Double puntaje;
	private Integer cantResenas;

	private List<String> materias = new ArrayList<>();

	private List<ResenaEntity> resenas = new ArrayList<>();

	public DocenteEntity(String id, String nombre, Double puntaje, Integer cantResenas, List<String> materias,
			List<ResenaEntity> resenas) {
		this.id = id;
		this.nombre = nombre;
		this.puntaje = puntaje;
		this.cantResenas = cantResenas;
		this.materias = materias != null ? materias : new ArrayList<>();
		this.resenas = resenas != null ? resenas : new ArrayList<>();
	}

	public DocenteEntity() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
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
