package com.affinityteach.application.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.affinityteach.application.cache.DocenteCache;
import com.affinityteach.application.dto.DocenteRequestDTO;
import com.affinityteach.application.dto.DocenteResponseDTO;
import com.affinityteach.application.mapper.DocenteMapper;
import com.affinityteach.domain.exception.NotFoundException;
import com.affinityteach.domain.model.Docente;
import com.affinityteach.domain.port.DocenteRepositoryPort;

@Service
public class DocenteService {
    private final DocenteRepositoryPort docenteRepository;
    private final DocenteCache docenteCache;
    private final DocenteMapper docenteMapper;
	
    public DocenteService(DocenteRepositoryPort docenteRepository,
            DocenteCache docenteCache,
            DocenteMapper docenteMapper) {
	this.docenteRepository = docenteRepository;
	this.docenteCache = docenteCache;
	this.docenteMapper = docenteMapper;
}

	public List<DocenteResponseDTO> obtenerTodos() {
        return docenteCache.getAll()
                .stream()
                .map(docenteMapper::toResponse)
                .toList();
	}

	public DocenteResponseDTO obtenerPorId(String id) {
        Docente docente = docenteCache.getById(id)
                .orElseThrow(() -> new NotFoundException("Docente no encontrado"));

        return docenteMapper.toResponse(docente);
	}

	public DocenteResponseDTO crear(DocenteRequestDTO dto) {
        Docente docente = docenteMapper.toDomain(dto);
        Docente guardado = docenteRepository.save(docente);

        docenteCache.updateSingle(guardado);

        return docenteMapper.toResponse(guardado);
	}
	
	public List<DocenteResponseDTO> crearMultiples(List<DocenteRequestDTO> dtos) {
	    return dtos.stream()
	            .map(this::crear)
	            .toList();
	}

	public DocenteResponseDTO actualizar(String id, DocenteRequestDTO dto) {
        Docente existente = docenteCache.getById(id)
                .orElseThrow(() -> new NotFoundException("Docente no encontrado"));

        // No se modifica puntaje ni cantidadResenas
        Docente actualizado = new Docente(
                existente.getUid(),
                dto.nombre(),
                existente.getPuntaje(),
                existente.getCantidadResenas(),
                dto.materias()
        );

        Docente guardado = docenteRepository.update(id, actualizado);

        docenteCache.updateSingle(guardado);

        return docenteMapper.toResponse(guardado);
	}

	public void eliminar(String id) {
        docenteRepository.deleteById(id);

        docenteCache.remove(id);
	}
}
