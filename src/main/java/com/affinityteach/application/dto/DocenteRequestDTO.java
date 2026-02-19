package com.affinityteach.application.dto;

import java.util.List;

public record DocenteRequestDTO(
	    String nombre,
	    List<String> materias
) {}
