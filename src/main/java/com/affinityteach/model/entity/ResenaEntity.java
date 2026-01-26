package com.affinityteach.model.entity;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.annotation.Exclude;

public class ResenaEntity {
	private String id;

	private String estudiante;
	private String comentario;
	private Integer estrellas;
	private LocalDateTime fecha;
	private Integer likes;

    public ResenaEntity() {
        this.id = UUID.randomUUID().toString();
        this.fecha = LocalDateTime.now();
        this.likes = 0;
    }
    
    // Constructor para Firestore
    public ResenaEntity(String id, String estudiante, String comentario, 
                       Integer estrellas, Object fechaObj, Integer likes) {
        this.id = id != null ? id : UUID.randomUUID().toString();
        this.estudiante = estudiante;
        this.comentario = comentario;
        this.estrellas = estrellas;
        this.likes = likes != null ? likes : 0;
        this.fecha = parseFechaFromFirestore(fechaObj);
    }
    
    @Exclude
    private LocalDateTime parseFechaFromFirestore(Object fechaObj) {
        if (fechaObj == null) {
            return LocalDateTime.now();
        }
        
        if (fechaObj instanceof Timestamp) {
            // Si es Timestamp de Firestore
            Timestamp timestamp = (Timestamp) fechaObj;
            return timestamp.toDate().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
        } else if (fechaObj instanceof String) {
            // Si es String (como guardas en convertirDocenteAMap)
            return LocalDateTime.parse((String) fechaObj, 
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
        } else if (fechaObj instanceof LocalDateTime) {
            return (LocalDateTime) fechaObj;
        }
        
        return LocalDateTime.now();
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
