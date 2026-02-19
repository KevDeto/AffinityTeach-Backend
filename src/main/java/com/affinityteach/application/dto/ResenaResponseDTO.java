package com.affinityteach.application.dto;

import com.google.cloud.Timestamp;

public record ResenaResponseDTO(
	    String uid,
	    String estudianteNombre,
	    String comentario,
	    Integer estrellas,
	    Timestamp fecha,
	    Integer like,
	    String fotoUrl
) {}
