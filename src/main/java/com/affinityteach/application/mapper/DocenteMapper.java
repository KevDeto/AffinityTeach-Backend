package com.affinityteach.application.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.affinityteach.application.dto.DocenteRequestDTO;
import com.affinityteach.application.dto.DocenteResponseDTO;
import com.affinityteach.domain.model.Docente;

@Component
public class DocenteMapper {
    // RequestDTO → Domain
    public Docente toDomain(DocenteRequestDTO dto) {
        return new Docente(
                null,                    // uid lo genera el repository
                dto.nombre(),
                0.0,                     // puntaje inicial
                0,                       // cantidadResenas inicial
                dto.materias() != null ? dto.materias() : List.of()
        );
    }

    // Domain → ResponseDTO
    public DocenteResponseDTO toResponse(Docente docente) {
        return new DocenteResponseDTO(
                docente.getUid(),
                docente.getNombre(),
                docente.getPuntaje(),
                docente.getCantidadResenas(),
                docente.getMaterias()
        );
    }
}
