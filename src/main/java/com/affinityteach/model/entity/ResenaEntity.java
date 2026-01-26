package com.affinityteach.model.entity;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.annotation.Exclude;

public class ResenaEntity {
	private String id;

	private String estudiante;
	private String comentario;
	private Integer estrellas;
	private Timestamp  fecha;
	private Integer likes;

    public ResenaEntity() {
        this.id = UUID.randomUUID().toString();
        this.fecha = Timestamp.now();
        this.likes = 0;
    }
        
    // Constructor para Firestore
    public ResenaEntity(String estudiante, String comentario, Integer estrellas) {
        this.id = UUID.randomUUID().toString();
        this.estudiante = estudiante;
        this.comentario = comentario;
        this.estrellas = estrellas;
        this.fecha = Timestamp.now();
        this.likes = 0;
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

    @Exclude
    public LocalDateTime getFechaAsLocalDateTime() {
        if (fecha == null) return null;
        return fecha.toDate().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }
    
    @Exclude
    public void setFechaFromLocalDateTime(LocalDateTime localDateTime) {
        if (localDateTime != null) {
            this.fecha = Timestamp.of(java.sql.Timestamp.valueOf(localDateTime));
        }
    }

	public Integer getLikes() {
		return likes;
	}

	public void setLikes(Integer likes) {
		this.likes = likes;
	}

}
