package com.affinityteach.model.dto;

public class ResenaRequestDTO {
    private String estudiante;
    private String comentario;
    private Integer estrellas;
    private String photo;

	public ResenaRequestDTO(String estudiante, String comentario, Integer estrellas, String photo) {
		super();
		this.estudiante = estudiante;
		this.comentario = comentario;
		this.estrellas = estrellas;
		this.setPhoto(photo);
	}

	public ResenaRequestDTO() {
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

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

}
