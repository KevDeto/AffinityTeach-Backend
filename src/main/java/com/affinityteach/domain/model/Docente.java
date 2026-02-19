package com.affinityteach.domain.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Docente {
	private String uid;
	private String nombre;
	private Double puntaje;
	private Integer cantidadResenas;
	private List<String> materias = new ArrayList<String>();
	
	public Docente(
			String uid,
			String nombre,
			Double puntaje,
			Integer cantResenas,
			List<String> materias) {
		this.uid = uid;
		this.nombre = nombre;
		this.puntaje = puntaje;
		this.cantidadResenas = cantResenas;
		this.materias = materias != null ? materias : new ArrayList<String>();
	}
}
