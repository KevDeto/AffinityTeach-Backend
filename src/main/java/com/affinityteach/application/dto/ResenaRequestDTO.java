package com.affinityteach.application.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ResenaRequestDTO(
        @Size(max = 350, message = "El comentario no puede superar los 350 caracteres")
        String comentario,

        @NotNull(message = "Las estrellas son obligatorias")
        @Min(value = 1, message = "Mínimo 1 estrella")
        @Max(value = 5, message = "Máximo 5 estrellas")
        Integer estrellas
) {}
