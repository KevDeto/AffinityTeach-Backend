package com.affinityteach.service;

import com.affinityteach.firebase.FirebaseInitializer;
import com.affinityteach.model.dto.DocenteRequestDTO;
import com.affinityteach.model.dto.ResenaRequestDTO;
import com.affinityteach.model.entity.DocenteEntity;
import com.affinityteach.model.entity.ResenaEntity;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

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
public class DocenteService {
    
    private final FirebaseInitializer firebaseInitializer;
    private Firestore firestore;
    private CollectionReference docentesCollection;

    private static final String COLECCION_DOCENTES = "docentes";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    
    public DocenteService(FirebaseInitializer firebaseInitializer) {
		this.firebaseInitializer = firebaseInitializer;
	}

	@PostConstruct
    private void init() {
        this.firestore = firebaseInitializer.getFirestore();
        this.docentesCollection = firestore.collection(COLECCION_DOCENTES);
    }
    
    // 1. Obtener todos los docentes (ordenados por nombre)
    public List<DocenteEntity> getAllDocentes() {
        try {
            ApiFuture<QuerySnapshot> future = docentesCollection.get();
            QuerySnapshot snapshot = future.get();
            
            List<DocenteEntity> docentes = new ArrayList<>();
            for (DocumentSnapshot doc : snapshot.getDocuments()) {
                DocenteEntity docente = doc.toObject(DocenteEntity.class);
                if (docente != null) {
                    docente.setId(doc.getId());
                    docentes.add(docente);
                }
            }
            
            // Ordenar por nombre
            docentes.sort(Comparator.comparing(DocenteEntity::getNombre));
            return docentes;
            
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error obteniendo docentes de Firebase", e);
        }
    }
    
    // 2. Obtener docente por ID
    public Optional<DocenteEntity> getDocenteById(String id) {
        try {
            DocumentSnapshot doc = docentesCollection.document(String.valueOf(id)).get().get();
            if (doc.exists()) {
                DocenteEntity docente = doc.toObject(DocenteEntity.class);
                if (docente != null) {
                    docente.setId(doc.getId());
                }
                return Optional.ofNullable(docente);
            }
            return Optional.empty();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error obteniendo docente de Firebase", e);
        }
    }
    
    // 3. Agregar reseña a un docente usando DTO
    public Optional<DocenteEntity> agregarResena(String docenteId, ResenaRequestDTO resenaRequest) {
        try {
            DocumentSnapshot doc = docentesCollection.document(String.valueOf(docenteId)).get().get();
            if (!doc.exists()) {
                return Optional.empty();
            }
            
            DocenteEntity docente = doc.toObject(DocenteEntity.class);
            if (docente == null) {
                return Optional.empty();
            }
            docente.setId(doc.getId());
            
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
            if (docente.getResenas() == null) {
                docente.setResenas(new ArrayList<>());
            }
            docente.getResenas().add(resena);
            docente.setCantResenas(docente.getResenas().size());
            
            // Calcular nuevo promedio de puntaje
            calcularPuntajePromedio(docente);
            
            // Guardar en Firebase
            Map<String, Object> docenteMap = convertirDocenteAMap(docente);
            ApiFuture<WriteResult> future = docentesCollection
                    .document(String.valueOf(docenteId))
                    .set(docenteMap);
            future.get();
            
            return Optional.of(docente);
            
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error agregando reseña en Firebase", e);
        }
    }
    
    // 4. Dar like a una reseña
    public Optional<DocenteEntity> darLike(String docenteId, String resenaId) {
        try {
            DocumentSnapshot doc = docentesCollection.document(String.valueOf(docenteId)).get().get();
            if (!doc.exists()) {
                return Optional.empty();
            }
            
            DocenteEntity docente = doc.toObject(DocenteEntity.class);
            if (docente == null || docente.getResenas() == null) {
                return Optional.empty();
            }
            docente.setId(doc.getId());
            
            // Buscar y actualizar la reseña
            boolean encontrado = false;
            for (ResenaEntity resena : docente.getResenas()) {
                if (resena.getId() != null && resena.getId().equals(resenaId)) {
                    resena.setLikes(resena.getLikes() + 1);
                    encontrado = true;
                    break;
                }
            }
            
            if (!encontrado) {
                return Optional.empty();
            }
            
            // Guardar en Firebase
            Map<String, Object> docenteMap = convertirDocenteAMap(docente);
            ApiFuture<WriteResult> future = docentesCollection
                    .document(String.valueOf(docenteId))
                    .set(docenteMap);
            future.get();
            
            return Optional.of(docente);
            
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error dando like en Firebase", e);
        }
    }
    
    // 5. Crear nuevo docente usando DTO
    public DocenteEntity crearDocente(DocenteRequestDTO docenteRequest) {
        try {
            // Generar nuevo ID
        	DocumentReference nuevoDoc = docentesCollection.document();
        	String nuevoId = nuevoDoc.getId(); // ID temporal simple
            
            DocenteEntity docente = new DocenteEntity();
            docente.setId(nuevoId);
            docente.setNombre(docenteRequest.getNombre());
            docente.setMaterias(docenteRequest.getMaterias() != null ? docenteRequest.getMaterias() : List.of());
            docente.setPuntaje(0.0);
            docente.setCantResenas(0);
            docente.setResenas(new ArrayList<>());
            
            // Guardar en Firebase
            Map<String, Object> docenteMap = convertirDocenteAMap(docente);
            ApiFuture<WriteResult> future = docentesCollection
                    .document(String.valueOf(nuevoId))
                    .set(docenteMap);
            future.get();
            
            return docente;
            
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error creando docente en Firebase", e);
        }
    }
    
    // 6. Actualizar docente
    public Optional<DocenteEntity> actualizarDocente(String id, DocenteRequestDTO docenteRequest) {
        try {
            DocumentSnapshot doc = docentesCollection.document(String.valueOf(id)).get().get();
            if (!doc.exists()) {
                return Optional.empty();
            }
            
            DocenteEntity docente = doc.toObject(DocenteEntity.class);
            if (docente == null) {
                return Optional.empty();
            }
            docente.setId(doc.getId());
            
            if (docenteRequest.getNombre() != null) {
                docente.setNombre(docenteRequest.getNombre());
            }
            if (docenteRequest.getMaterias() != null) {
                docente.setMaterias(docenteRequest.getMaterias());
            }
            
            // Guardar en Firebase
            Map<String, Object> docenteMap = convertirDocenteAMap(docente);
            ApiFuture<WriteResult> future = docentesCollection
                    .document(String.valueOf(id))
                    .set(docenteMap);
            future.get();
            
            return Optional.of(docente);
            
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error actualizando docente en Firebase", e);
        }
    }
    
    // 7. Eliminar docente
    public boolean eliminarDocente(String id) {
        try {
            DocumentSnapshot doc = docentesCollection.document(String.valueOf(id)).get().get();
            if (!doc.exists()) {
                return false;
            }
            
            ApiFuture<WriteResult> future = docentesCollection.document(String.valueOf(id)).delete();
            future.get();
            return true;
            
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error eliminando docente en Firebase", e);
        }
    }
    
    // 8. Cargar lista de docentes iniciales desde DTOs
    public List<DocenteEntity> cargarDocentesIniciales(List<DocenteRequestDTO> docentesRequest) {
        List<DocenteEntity> docentesCreados = new ArrayList<>();
        
        try {
            for (DocenteRequestDTO dto : docentesRequest) {
                DocumentReference nuevoDoc = docentesCollection.document();
                String nuevoId = nuevoDoc.getId();
                
                DocenteEntity docente = new DocenteEntity();
                docente.setId(nuevoId);
                docente.setNombre(dto.getNombre());
                docente.setMaterias(dto.getMaterias() != null ? dto.getMaterias() : List.of());
                docente.setPuntaje(0.0);
                docente.setCantResenas(0);
                docente.setResenas(new ArrayList<>());
                
                // Guardar en Firebase
                Map<String, Object> docenteMap = convertirDocenteAMap(docente);
                ApiFuture<WriteResult> future = docentesCollection
                        .document(String.valueOf(nuevoId))
                        .set(docenteMap);
                future.get();
                
                docentesCreados.add(docente);
            }
            return docentesCreados;
            
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error cargando docentes iniciales en Firebase", e);
        }
    }
    
    // 9. Buscar docentes por nombre
    public List<DocenteEntity> buscarPorNombre(String nombre) {
        try {
            Query query = docentesCollection.whereGreaterThanOrEqualTo("nombre", nombre)
                                          .whereLessThanOrEqualTo("nombre", nombre + "\uf8ff");
            
            ApiFuture<QuerySnapshot> future = query.get();
            QuerySnapshot snapshot = future.get();
            
            List<DocenteEntity> docentes = new ArrayList<>();
            for (DocumentSnapshot doc : snapshot.getDocuments()) {
                DocenteEntity docente = doc.toObject(DocenteEntity.class);
                if (docente != null) {
                    docente.setId(doc.getId());
                    docentes.add(docente);
                }
            }
            
            // Ordenar por nombre
            docentes.sort(Comparator.comparing(DocenteEntity::getNombre));
            return docentes;
            
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error buscando docentes por nombre en Firebase", e);
        }
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
}