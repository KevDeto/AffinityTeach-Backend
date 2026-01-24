package com.affinityteach.model.dto;

import java.util.List;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

public class DocenteRequestDTO {
//    @NotBlank(message = "El nombre del docente es requerido")
	private String nombre;

	private List<String> materias;

	public DocenteRequestDTO(String nombre, List<String> materias) {
		super();
		this.nombre = nombre;
		this.materias = materias;
	}

	public DocenteRequestDTO() {
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public List<String> getMaterias() {
		return materias;
	}

	public void setMaterias(List<String> materias) {
		this.materias = materias;
	}

}