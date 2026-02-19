package com.affinityteach.application.mapper;

import org.springframework.stereotype.Component;

import com.affinityteach.application.dto.ResenaResponseDTO;
import com.affinityteach.domain.model.Resena;

@Component
public class ResenaMapper {
	// Domain → ResponseDTO
    public ResenaResponseDTO toResponse(Resena resena) {

        return new ResenaResponseDTO(
                resena.getUid(),
                resena.getEstudianteNombre(),
                resena.getComentario(),
                resena.getEstrellas(),
                resena.getFecha(),
                resena.getLikes(),
                resena.getFotoUrl()
        );
    }
}
