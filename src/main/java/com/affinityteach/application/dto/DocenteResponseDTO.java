package com.affinityteach.application.dto;

import java.util.List;

public record DocenteResponseDTO(
	    String uid,
	    String nombre,
	    Double puntaje,
	    Integer cantidadResenas,
	    List<String> materias
) {}
