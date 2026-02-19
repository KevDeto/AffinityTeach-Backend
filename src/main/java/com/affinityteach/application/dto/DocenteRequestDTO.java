package com.affinityteach.application.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public record DocenteRequestDTO(
        @NotBlank(message = "El nombre es obligatorio")
        String nombre,

        @NotEmpty(message = "Debe tener al menos una materia")
        List<@NotBlank(message = "Materia inválida") String> materias
) {}
