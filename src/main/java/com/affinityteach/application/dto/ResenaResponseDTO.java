package com.affinityteach.application.dto;

import java.time.Instant;

public record ResenaResponseDTO(
	    String uid,
	    String estudianteNombre,
	    String comentario,
	    Integer estrellas,
	    Instant fecha,
	    Integer likes,
	    String fotoUrl
) {}
