package com.affinityteach.model.dto;

import java.time.LocalDateTime;

import com.affinityteach.model.entity.ResenaEntity;

public class ReviewPublicDTO {
    private String id;
    private String estudiante;
    private String comentario;
    private Integer estrellas;
    private LocalDateTime fecha;
    private Integer likes;
    private String photo;

    public ReviewPublicDTO(ResenaEntity resena) {
        this.id = resena.getId();
        this.estudiante = resena.getEstudiante();
        this.comentario = resena.getComentario();
        this.estrellas = resena.getEstrellas();
        this.fecha = resena.getFechaAsLocalDateTime();
        this.likes = resena.getLikes();
        this.photo = resena.getPhoto();
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

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}
    
    
}
