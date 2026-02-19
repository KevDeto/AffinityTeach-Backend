package com.affinityteach.domain.model;

import java.time.Instant;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Resena {
	private String uid;
	private String docenteUid;
	private String estudianteNombre;
	private String comentario;
	private Integer estrellas;
	private Instant fecha;
	private Integer likes;
	private String fotoUrl;
	private String email;
	
    public Resena(
    		String uid,
    		String docenteUid,
    		String estudianteNombre,
    		String comentario,
    		Integer estrellas,
    		Instant fecha,
    		Integer likes,
    		String fotoUrl,
    		String email) {
        this.uid = uid;
        this.docenteUid = docenteUid;
        this.estudianteNombre = estudianteNombre;
        this.comentario = comentario;
        this.estrellas = estrellas;
        this.fecha = fecha;
        this.likes = likes;
        this.fotoUrl = fotoUrl;
        this.email = email;
    }
    
    public void incrementarLikes() {
        this.likes = this.likes + 1;
    }
}
