package com.affinityteach.service;

import com.affinityteach.firebase.FirebaseInitializer;
import com.affinityteach.model.dto.DocenteRequestDTO;
import com.affinityteach.model.dto.ResenaRequestDTO;
import com.affinityteach.model.entity.DocenteEntity;
import com.affinityteach.model.entity.ResenaEntity;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ExecutionException;

@Service
public class DocenteService {
    
    private final FirebaseInitializer firebaseInitializer;
    private Firestore firestore;
    private CollectionReference docentesCollection;
    
    private static final String COLECCION_DOCENTES = "docentes";
    
    public DocenteService(FirebaseInitializer firebaseInitializer) {
        this.firebaseInitializer = firebaseInitializer;
    }
    
    @PostConstruct
    private void init() {
        this.firestore = firebaseInitializer.getFirestore();
        this.docentesCollection = firestore.collection(COLECCION_DOCENTES);
    }
    
    // ============ MÉTODOS PRINCIPALES ============
    
    // 1. Obtener todas las reseñas de un docente por ID
    public Optional<List<ResenaEntity>> getResenasByDocenteId(String id) {
        try {
            DocumentReference docenteRef = docentesCollection.document(id);
            DocumentSnapshot doc = docenteRef.get().get();
            
            if (!doc.exists()) {
                return Optional.empty();
            }
            
            // ¡POJO MAGIC! Firestore deserializa automáticamente
            DocenteEntity docente = doc.toObject(DocenteEntity.class);
            if (docente == null) {
                return Optional.empty();
            }
            
            docente.setId(doc.getId()); // Asignar ID desde documento
            
            // Asegurar que las reseñas no sean null
            List<ResenaEntity> resenas = docente.getResenas() != null ? 
                    docente.getResenas() : new ArrayList<>();
            
            return Optional.of(resenas);
            
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Error obteniendo reseñas de docente desde Firestore", e);
        }
    }
    
    // 2. Obtener docente por ID
    public Optional<DocenteEntity> getDocenteById(String id) {
        try {
            DocumentReference docenteRef = docentesCollection.document(id);
            DocumentSnapshot doc = docenteRef.get().get();
            
            if (!doc.exists()) {
                return Optional.empty();
            }
            
            // Deserialización automática
            DocenteEntity docente = doc.toObject(DocenteEntity.class);
            if (docente != null) {
                docente.setId(doc.getId());
            }
            return Optional.ofNullable(docente);
            
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Error obteniendo docente de Firestore", e);
        }
    }
    
    // 3. Agregar reseña a un docente
    public Optional<DocenteEntity> agregarResena(String docenteId, ResenaRequestDTO resenaRequest) {
        try {
            DocumentReference docenteRef = docentesCollection.document(docenteId);
            DocumentSnapshot doc = docenteRef.get().get();
            
            if (!doc.exists()) {
                return Optional.empty();
            }
            
            // Obtener docente actual
            DocenteEntity docente = doc.toObject(DocenteEntity.class);
            if (docente == null) {
                return Optional.empty();
            }
            docente.setId(doc.getId());
            
            // Validar estrellas
            if (resenaRequest.getEstrellas() == null || 
                resenaRequest.getEstrellas() < 1 || 
                resenaRequest.getEstrellas() > 5) {
                throw new IllegalArgumentException("Las estrellas deben estar entre 1 y 5");
            }
            
            // Crear nueva reseña
            ResenaEntity nuevaResena = new ResenaEntity(
                resenaRequest.getEstudiante(),
                resenaRequest.getComentario(),
                resenaRequest.getEstrellas(),
                resenaRequest.getPhoto(),
                resenaRequest.getEmail()
            );
            
            // Agregar a la lista
            docente.getResenas().add(nuevaResena);
            docente.setCantResenas(docente.getResenas().size());
            
            // Calcular nuevo promedio
            calcularPuntajePromedio(docente);
            
            // Guardar en Firestore (update solo los campos necesarios)
            Map<String, Object> updates = new HashMap<>();
            updates.put("resenas", docente.getResenas());
            updates.put("cantResenas", docente.getCantResenas());
            updates.put("puntaje", docente.getPuntaje());
            
            docenteRef.update(updates).get();
            
            return Optional.of(docente);
            
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Error agregando reseña en Firestore", e);
        }
    }
    
    // 4. Dar like a una reseña
    public Optional<DocenteEntity> darLike(String docenteId, String resenaId) {
        try {
            DocumentReference docenteRef = docentesCollection.document(docenteId);
            DocumentSnapshot doc = docenteRef.get().get();
            
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
            
            // Actualizar solo las reseñas
            docenteRef.update("resenas", docente.getResenas()).get();
            
            return Optional.of(docente);
            
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Error dando like en Firestore", e);
        }
    }
    
    // 5. Obtener todos los docentes
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
            Thread.currentThread().interrupt();
            throw new RuntimeException("Error obteniendo docentes de Firestore", e);
        }
    }
    
    // 6. Crear nuevo docente
    public DocenteEntity crearDocente(DocenteRequestDTO docenteRequest) {
        try {
            // Generar nuevo documento
            DocumentReference nuevoDoc = docentesCollection.document();
            String nuevoId = nuevoDoc.getId();
            
            // Crear entidad
            DocenteEntity docente = new DocenteEntity();
            docente.setId(nuevoId);
            docente.setNombre(docenteRequest.getNombre());
            docente.setMaterias(docenteRequest.getMaterias() != null ? 
                docenteRequest.getMaterias() : new ArrayList<>());
            docente.setPuntaje(0.0);
            docente.setCantResenas(0);
            docente.setResenas(new ArrayList<>());
            
            // Guardar en Firestore
            nuevoDoc.set(docente).get();
            
            return docente;
            
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Error creando docente en Firestore", e);
        }
    }
    
    //6.5 Importar lista de docentes
    public void importarDocentes(List<DocenteRequestDTO> docenteRequest) {
        for(DocenteRequestDTO dto : docenteRequest) {
            crearDocente(dto);
        }
    }
    
    // 7. Actualizar docente
    public Optional<DocenteEntity> actualizarDocente(String id, DocenteRequestDTO docenteRequest) {
        try {
            DocumentReference docenteRef = docentesCollection.document(id);
            DocumentSnapshot doc = docenteRef.get().get();
            
            if (!doc.exists()) {
                return Optional.empty();
            }
            
            // Obtener docente actual
            DocenteEntity docente = doc.toObject(DocenteEntity.class);
            if (docente == null) {
                return Optional.empty();
            }
            docente.setId(doc.getId());
            
            // Actualizar campos
            if (docenteRequest.getNombre() != null) {
                docente.setNombre(docenteRequest.getNombre());
            }
            if (docenteRequest.getMaterias() != null) {
                docente.setMaterias(docenteRequest.getMaterias());
            }
            
            // Guardar cambios
            Map<String, Object> updates = new HashMap<>();
            updates.put("nombre", docente.getNombre());
            updates.put("materias", docente.getMaterias());
            
            docenteRef.update(updates).get();
            
            return Optional.of(docente);
            
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Error actualizando docente en Firestore", e);
        }
    }
    
    // 8. Eliminar docente
    public boolean eliminarDocente(String id) {
        try {
            DocumentReference docenteRef = docentesCollection.document(id);
            DocumentSnapshot doc = docenteRef.get().get();
            
            if (!doc.exists()) {
                return false;
            }
            
            docenteRef.delete().get();
            return true;
            
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Error eliminando docente en Firestore", e);
        }
    }
    
    // 9. Buscar docentes por nombre
    public List<DocenteEntity> buscarPorNombre(String nombre) {
        try {
            Query query = docentesCollection
                .whereGreaterThanOrEqualTo("nombre", nombre)
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
            Thread.currentThread().interrupt();
            throw new RuntimeException("Error buscando docentes por nombre en Firestore", e);
        }
    }
    
    // 10. Cargar docentes iniciales
    public List<DocenteEntity> cargarDocentesIniciales(List<DocenteRequestDTO> docentesRequest) {
        List<DocenteEntity> docentesCreados = new ArrayList<>();
        
        try {
            for (DocenteRequestDTO dto : docentesRequest) {
                DocumentReference nuevoDoc = docentesCollection.document();
                String nuevoId = nuevoDoc.getId();
                
                DocenteEntity docente = new DocenteEntity();
                docente.setId(nuevoId);
                docente.setNombre(dto.getNombre());
                docente.setMaterias(dto.getMaterias() != null ? 
                    dto.getMaterias() : new ArrayList<>());
                docente.setPuntaje(0.0);
                docente.setCantResenas(0);
                docente.setResenas(new ArrayList<>());
                
                // Guardar en Firestore
                nuevoDoc.set(docente).get();
                
                docentesCreados.add(docente);
            }
            return docentesCreados;
            
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Error cargando docentes iniciales en Firestore", e);
        }
    }
    
    // ============ MÉTODOS PRIVADOS ============
    
    private void calcularPuntajePromedio(DocenteEntity docente) {
        if (docente.getResenas() == null || docente.getResenas().isEmpty()) {
            docente.setPuntaje(0.0);
            return;
        }
        
        double suma = 0;
        for (ResenaEntity resena : docente.getResenas()) {
            suma += resena.getEstrellas();
        }
        
        double promedio = suma / docente.getResenas().size();
        // Redondear a 1 decimal
        docente.setPuntaje(Math.round(promedio * 10.0) / 10.0);
    }
}