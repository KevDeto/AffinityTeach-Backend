package com.affinityteach.application.service;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;

import com.affinityteach.application.cache.DocenteCache;
import com.affinityteach.application.dto.ResenaRequestDTO;
import com.affinityteach.application.dto.ResenaResponseDTO;
import com.affinityteach.application.dto.UsuarioAutenticadoDTO;
import com.affinityteach.application.mapper.ResenaMapper;
import com.affinityteach.domain.model.Docente;
import com.affinityteach.domain.model.Resena;
import com.affinityteach.domain.port.DocenteRepositoryPort;
import com.affinityteach.domain.port.ResenaRepositoryPort;

@Service
public class ResenaService {
    private final ResenaRepositoryPort resenaRepository;
    private final DocenteRepositoryPort docenteRepository;
    private final DocenteCache docenteCache;
    private final ResenaMapper resenaMapper;

    public ResenaService(ResenaRepositoryPort resenaRepository,
                         DocenteRepositoryPort docenteRepository,
                         DocenteCache docenteCache,
                         ResenaMapper resenaMapper) {
        this.resenaRepository = resenaRepository;
        this.docenteRepository = docenteRepository;
        this.docenteCache = docenteCache;
        this.resenaMapper = resenaMapper;
    }

    public List<ResenaResponseDTO> obtenerPorDocente(String docenteUid) {

        List<Resena> resenas = resenaRepository.findByDocenteId(docenteUid);

        return resenas.stream()
                .map(resenaMapper::toResponse)
                .toList();
    }

	public ResenaResponseDTO crear(
			String docenteUid, 
			UsuarioAutenticadoDTO usuarioAutenticado,
            ResenaRequestDTO dto) {
		
        resenaRepository.findByDocenteIdAndEmail(docenteUid, usuarioAutenticado.email())
        .ifPresent(r -> {
            throw new RuntimeException("Ya has reseñado este docente");
        });
        
        Resena nueva = new Resena(
                null,
                docenteUid,
                usuarioAutenticado.nombre(),
                dto.comentario(),
                dto.estrellas(),
                Instant.now(),
                0,
                usuarioAutenticado.fotoUrl(),
                usuarioAutenticado.email()
        );
        
        Resena guardada = resenaRepository.save(nueva);
        actualizarPromedioDocente(docenteUid, dto.estrellas());
        return resenaMapper.toResponse(guardada);
	}
	
    public void eliminar(
    		String docenteUid,
    		String resenaUid,
    		Integer estrellas) {
    	
        resenaRepository.deleteById(docenteUid, resenaUid);
        actualizarPromedioAlEliminar(docenteUid, estrellas);
    }
    
    public ResenaResponseDTO darLike(String docenteId, String resenaId) {

        Resena resena = resenaRepository
                .findByDocenteIdAndId(docenteId, resenaId)
                .orElseThrow(() -> new RuntimeException("Reseña no encontrada"));

        resena.incrementarLikes();

        Resena actualizada = resenaRepository.save(resena);

        return resenaMapper.toResponse(actualizada);
    }
    
    /*
     * LOGICA DE NEGOCIO
     */
    
    private void actualizarPromedioDocente(
    		String docenteUid,
    		Integer nuevasEstrellas) {
    	
        Docente docente = docenteCache.getById(docenteUid)
                .orElseThrow(() -> new RuntimeException("Docente no encontrado"));

        double puntajeActual = docente.getPuntaje();
        int cantidadActual = docente.getCantidadResenas();

        double nuevoPromedio =
                ((puntajeActual * cantidadActual) + nuevasEstrellas)
                        / (cantidadActual + 1);

        docente.setPuntaje(redondear(nuevoPromedio));
        docente.setCantidadResenas(cantidadActual + 1);

        docenteRepository.update(docenteUid, docente);

        docenteCache.updateSingle(docente);
    }

    private void actualizarPromedioAlEliminar(
    		String docenteUid,
    		Integer estrellasEliminadas) {
        Docente docente = docenteCache.getById(docenteUid)
                .orElseThrow(() -> new RuntimeException("Docente no encontrado"));

        int cantidadActual = docente.getCantidadResenas();

        if (cantidadActual <= 1) {
            docente.setPuntaje(0.0);
            docente.setCantidadResenas(0);
        } else {

            double puntajeActual = docente.getPuntaje();

            double nuevoPromedio =
                    ((puntajeActual * cantidadActual) - estrellasEliminadas)
                            / (cantidadActual - 1);

            docente.setPuntaje(redondear(nuevoPromedio));
            docente.setCantidadResenas(cantidadActual - 1);
        }

        docenteRepository.update(docenteUid, docente);

        docenteCache.updateSingle(docente);
    }

    private double redondear(double valor) {
        return Math.round(valor * 10.0) / 10.0; // 1 decimal
    }
}
