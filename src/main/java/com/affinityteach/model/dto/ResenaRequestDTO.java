package com.affinityteach.model.dto;

public class ResenaRequestDTO {
//    @NotBlank(message = "El nombre del estudiante es requerido")
	private String estudiante;

//    @NotBlank(message = "El comentario es requerido")
	private String comentario;

//    @Min(value = 1, message = "Las estrellas deben ser mínimo 1")
//    @Max(value = 5, message = "Las estrellas deben ser máximo 5")
	private Integer estrellas;

	public ResenaRequestDTO(String estudiante, String comentario, Integer estrellas) {
		super();
		this.estudiante = estudiante;
		this.comentario = comentario;
		this.estrellas = estrellas;
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

}
