package com.affinityteach.service;

import com.affinityteach.firebase.FirebaseInitializer;
import com.affinityteach.model.dto.DocenteRequestDTO;
import com.affinityteach.model.dto.ResenaRequestDTO;
import com.affinityteach.model.entity.DocenteEntity;
import com.affinityteach.model.entity.ResenaEntity;
import com.affinityteach.model.repository.DocenteRepository;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Service
@Transactional
public class DocenteService {
    
    private final DocenteRepository docenteRepository;
    private final Firestore firestore;
    private final CollectionReference docentesCollection;

    private static final String COLECCION_DOCENTES = "docentes";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    
    public DocenteService(DocenteRepository docenteRepository, FirebaseInitializer firebaseInitializer) {
    	this.docenteRepository = docenteRepository;
    	this.firestore = firebaseInitializer.getFirestore();
        this.docentesCollection = firestore.collection(COLECCION_DOCENTES);
    }
    
    // 1. Obtener todos los docentes (ordenados por nombre)
    public List<DocenteEntity> getAllDocentes() {
        return docenteRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(DocenteEntity::getNombre))
                .toList();
    }
    
    // 2. Obtener docente por ID
    public Optional<DocenteEntity> getDocenteById(Long id) {
        return docenteRepository.findById(id);
    }
    
    // 3. Agregar reseña a un docente usando DTO
    public Optional<DocenteEntity> agregarResena(Long docenteId, ResenaRequestDTO resenaRequest) {
        return docenteRepository.findById(docenteId)
                .map(docente -> {
                    // Convertir DTO a Entity
                    ResenaEntity resena = new ResenaEntity();
                    resena.setEstudiante(resenaRequest.getEstudiante());
                    resena.setComentario(resenaRequest.getComentario());
                    resena.setEstrellas(resenaRequest.getEstrellas());
                    resena.setFecha(LocalDateTime.now());
                    resena.setLikes(0);
                    
                    // Validar estrellas
                    if (resena.getEstrellas() == null || resena.getEstrellas() < 1 || resena.getEstrellas() > 5) {
                        throw new IllegalArgumentException("Las estrellas deben estar entre 1 y 5");
                    }
                    
                    // Agregar reseña
                    docente.getResenas().add(resena);
                    docente.setCantResenas(docente.getResenas().size());
                    
                    // Calcular nuevo promedio de puntaje
                    calcularPuntajePromedio(docente);
                    
                    DocenteEntity docenteGuardado = docenteRepository.save(docente);
                    
                    sincronizarDocenteAFirestore(docenteGuardado);
                    
                    return docenteGuardado;
                });
    }
    
    // 4. Dar like a una reseña
    public Optional<DocenteEntity> darLike(Long docenteId, Long resenaId) {
        return docenteRepository.findById(docenteId)
                .map(docente -> {
                    docente.getResenas().stream()
                            .filter(r -> r.getId().equals(resenaId))
                            .findFirst()
                            .ifPresent(resena -> resena.setLikes(resena.getLikes() + 1));
                    
                    DocenteEntity docenteGuardado = docenteRepository.save(docente);
                    
                    sincronizarDocenteAFirestore(docenteGuardado);
                    
                    return docenteGuardado;
                });
    }
    
    // 5. Crear nuevo docente usando DTO
    public DocenteEntity crearDocente(DocenteRequestDTO docenteRequest) {
        DocenteEntity docente = new DocenteEntity();
        docente.setNombre(docenteRequest.getNombre());
        docente.setMaterias(docenteRequest.getMaterias() != null ? docenteRequest.getMaterias() : List.of());
        docente.setPuntaje(0.0);
        docente.setCantResenas(0);
        docente.setResenas(List.of());
        
        DocenteEntity docenteGuardado = docenteRepository.save(docente);
        
        sincronizarDocenteAFirestore(docenteGuardado);
        
        return docenteGuardado;
    }
    
    // 6. Actualizar docente
    public Optional<DocenteEntity> actualizarDocente(Long id, DocenteRequestDTO docenteRequest) {
        return docenteRepository.findById(id)
                .map(docente -> {
                    if (docenteRequest.getNombre() != null) {
                        docente.setNombre(docenteRequest.getNombre());
                    }
                    if (docenteRequest.getMaterias() != null) {
                        docente.setMaterias(docenteRequest.getMaterias());
                    }
                    
                    
                    DocenteEntity docenteGuardado = docenteRepository.save(docente);
                    
                    sincronizarDocenteAFirestore(docenteGuardado);
                    
                    return docenteGuardado;
                });
    }
    
    // 7. Eliminar docente
    public boolean eliminarDocente(Long id) {
        if (docenteRepository.existsById(id)) {
        	
            eliminarDocenteDeFirestore(id);

            docenteRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    // 8. Cargar lista de docentes iniciales desde DTOs
    public List<DocenteEntity> cargarDocentesIniciales(List<DocenteRequestDTO> docentesRequest) {
        List<DocenteEntity> docentes = docentesRequest.stream()
                .map(dto -> {
                    DocenteEntity docente = new DocenteEntity();
                    docente.setNombre(dto.getNombre());
                    docente.setMaterias(dto.getMaterias() != null ? dto.getMaterias() : List.of());
                    docente.setPuntaje(0.0);
                    docente.setCantResenas(0);
                    docente.setResenas(List.of());
                    return docente;
                })
                .toList();
        
        List<DocenteEntity> docentesGuardados = docenteRepository.saveAll(docentes);
        
        sincronizarVariosDocentesAFirestore(docentesGuardados);
        
        return docentesGuardados;
    }
    
    // 9. Buscar docentes por nombre
    public List<DocenteEntity> buscarPorNombre(String nombre) {
        return docenteRepository.findAll()
                .stream()
                .filter(docente -> docente.getNombre().toLowerCase().contains(nombre.toLowerCase()))
                .sorted(Comparator.comparing(DocenteEntity::getNombre))
                .toList();
    }
    
    // 10. Método privado para calcular puntaje promedio
    private void calcularPuntajePromedio(DocenteEntity docente) {
        if (docente.getResenas() == null || docente.getResenas().isEmpty()) {
            docente.setPuntaje(0.0);
            return;
        }
        
        double promedio = docente.getResenas().stream()
                .mapToInt(ResenaEntity::getEstrellas)
                .average()
                .orElse(0.0);
        
        // Redondear a 1 decimal
        docente.setPuntaje(Math.round(promedio * 10.0) / 10.0);
    }
    
    
    
    /*
     * 
     */
    
    private void sincronizarDocenteAFirestore(DocenteEntity docente) {
        try {
            // Convertir el docente a Map para Firestore
            Map<String, Object> docenteMap = convertirDocenteAMap(docente);
            
            // Guardar en Firestore (si no existe, lo crea; si existe, lo actualiza)
            ApiFuture<WriteResult> future = docentesCollection
                    .document(String.valueOf(docente.getId()))
                    .set(docenteMap);
            
            WriteResult result = future.get();
            System.out.println("✅ Docente ID " + docente.getId() + " sincronizado con Firestore a las " + result.getUpdateTime());
            
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("❌ Error al sincronizar docente " + docente.getId() + " con Firestore: " + e.getMessage());
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            System.err.println("❌ Error general al sincronizar con Firestore: " + e.getMessage());
        }
    }
    
    private Map<String, Object> convertirDocenteAMap(DocenteEntity docente) {
        Map<String, Object> docenteMap = new HashMap<>();
        
        // Datos básicos
        docenteMap.put("id", docente.getId());
        docenteMap.put("nombre", docente.getNombre());
        docenteMap.put("puntaje", docente.getPuntaje());
        docenteMap.put("cantResenas", docente.getCantResenas());
        docenteMap.put("materias", docente.getMaterias() != null ? docente.getMaterias() : new ArrayList<>());
        
        // Convertir reseñas
        List<Map<String, Object>> resenasList = new ArrayList<>();
        if (docente.getResenas() != null && !docente.getResenas().isEmpty()) {
            for (ResenaEntity resena : docente.getResenas()) {
                Map<String, Object> resenaMap = new HashMap<>();
                resenaMap.put("id", resena.getId());
                resenaMap.put("estudiante", resena.getEstudiante());
                resenaMap.put("comentario", resena.getComentario());
                resenaMap.put("estrellas", resena.getEstrellas());
                resenaMap.put("fecha", resena.getFecha() != null ? 
                        resena.getFecha().format(DATE_FORMATTER) : null);
                resenaMap.put("likes", resena.getLikes());
                resenasList.add(resenaMap);
            }
        }
        docenteMap.put("resenas", resenasList);
        
        // Timestamp de última actualización
        docenteMap.put("ultimaActualizacion", new Date());
        
        return docenteMap;
    }
    
    private void eliminarDocenteDeFirestore(Long docenteId) {
        try {
            ApiFuture<WriteResult> future = docentesCollection
                    .document(String.valueOf(docenteId))
                    .delete();
            
            WriteResult result = future.get();
            System.out.println("✅ Docente ID " + docenteId + " eliminado de Firestore a las " + result.getUpdateTime());
            
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("❌ Error al eliminar docente " + docenteId + " de Firestore: " + e.getMessage());
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            System.err.println("❌ Error general al eliminar de Firestore: " + e.getMessage());
        }
    }
    
    private void sincronizarVariosDocentesAFirestore(List<DocenteEntity> docentes) {
        for (DocenteEntity docente : docentes) {
            sincronizarDocenteAFirestore(docente);
        }
    }
}