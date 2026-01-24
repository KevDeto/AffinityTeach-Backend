package com.affinityteach.model.entity;

import java.time.LocalDateTime;

public class ResenaEntity {
	private String id;

	private String estudiante;
	private String comentario;
	private Integer estrellas;
	private LocalDateTime fecha;
	private Integer likes = 0;

	public ResenaEntity(String id, String estudiante, String comentario, Integer estrellas, LocalDateTime fecha,
			Integer likes) {
		super();
		this.id = id;
		this.estudiante = estudiante;
		this.comentario = comentario;
		this.estrellas = estrellas;
		this.fecha = fecha;
		this.likes = likes;
	}

	public ResenaEntity() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getEstudiante() {
		return estudiante;
	}

	public void setEstudiante(String estudiante) {
		this.estudiante = estudiante;
	}

	public String getComentario() {
		return comentario;
	}

	public void setComentario(String comentario) {
		this.comentario = comentario;
	}

	public Integer getEstrellas() {
		return estrellas;
	}

	public void setEstrellas(Integer estrellas) {
		this.estrellas = estrellas;
	}

	public LocalDateTime getFecha() {
		return fecha;
	}

	public void setFecha(LocalDateTime fecha) {
		this.fecha = fecha;
	}

	public Integer getLikes() {
		return likes;
	}

	public void setLikes(Integer likes) {
		this.likes = likes;
	}

}
