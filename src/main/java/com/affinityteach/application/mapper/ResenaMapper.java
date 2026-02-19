package com.affinityteach.application.mapper;

import org.springframework.stereotype.Component;

import com.affinityteach.application.dto.ResenaResponseDTO;
import com.affinityteach.domain.model.Resena;
import com.google.cloud.Timestamp;

@Component
public class ResenaMapper {
	// Domain → ResponseDTO
    public ResenaResponseDTO toResponse(Resena resena) {

        Timestamp timestamp = Timestamp.ofTimeSecondsAndNanos(
                resena.getFecha().getEpochSecond(),
                resena.getFecha().getNano()
        );

        return new ResenaResponseDTO(
                resena.getUid(),
                resena.getEstudianteNombre(),
                resena.getComentario(),
                resena.getEstrellas(),
                timestamp,
                resena.getLikes(),
                resena.getFotoUrl()
        );
    }
}
