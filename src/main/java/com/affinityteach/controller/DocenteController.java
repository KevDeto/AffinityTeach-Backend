package com.affinityteach.controller;

import com.affinityteach.model.dto.DocenteRequestDTO;
import com.affinityteach.model.dto.ResenaRequestDTO;
import com.affinityteach.model.entity.DocenteEntity;
import com.affinityteach.service.DocenteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/docentes")
public class DocenteController {
    
    private final DocenteService docenteService;
    
    public DocenteController(DocenteService docenteService) {
        this.docenteService = docenteService;
    }
    
    // ============ CRUD BÁSICO ============
    
    // 1. Obtener todos los docentes
    @GetMapping
    public ResponseEntity<List<DocenteEntity>> getAllDocentes() {
        return ResponseEntity.ok(docenteService.getAllDocentes());
    }
    
    // 2. Obtener docente por ID (cambiado a String)
    @GetMapping("/{id}")
    public ResponseEntity<?> getDocenteById(@PathVariable String id) {
        return docenteService.getDocenteById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(crearErrorResponse("Docente no encontrado con ID: " + id)));
    }
    
    // 3. Crear nuevo docente
    @PostMapping
    public ResponseEntity<?> crearDocente(@RequestBody DocenteRequestDTO docenteRequest) {
        try {
            validarDocenteRequest(docenteRequest);
            DocenteEntity docenteCreado = docenteService.crearDocente(docenteRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(docenteCreado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(crearErrorResponse(e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(crearErrorResponse("Error al crear docente: " + e.getMessage()));
        }
    }
    
    // 4. Actualizar docente (cambiado a String)
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarDocente(
            @PathVariable String id,
            @RequestBody DocenteRequestDTO docenteRequest) {
        
        try {
            validarDocenteRequest(docenteRequest);
            return docenteService.actualizarDocente(id, docenteRequest)
                    .<ResponseEntity<?>>map(ResponseEntity::ok)
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(crearErrorResponse("Docente no encontrado con ID: " + id)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(crearErrorResponse(e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(crearErrorResponse("Error al actualizar docente: " + e.getMessage()));
        }
    }
    
    // 5. Eliminar docente (cambiado a String)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarDocente(@PathVariable String id) {
        try {
            if (docenteService.eliminarDocente(id)) {
                return ResponseEntity.ok(crearMensajeResponse("Docente eliminado correctamente"));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(crearErrorResponse("Docente no encontrado con ID: " + id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(crearErrorResponse("Error al eliminar docente: " + e.getMessage()));
        }
    }
    
    // ============ OPERACIONES CON RESEÑAS ============
    
    // 6. Agregar reseña a un docente (cambiado a String)
    @PostMapping("/{id}/resenas")
    public ResponseEntity<?> agregarResena(
            @PathVariable String id,
            @RequestBody ResenaRequestDTO resenaRequest) {
        
        try {
            validarResenaRequest(resenaRequest);
            return docenteService.agregarResena(id, resenaRequest)
                    .<ResponseEntity<?>>map(ResponseEntity::ok)
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(crearErrorResponse("Docente no encontrado con ID: " + id)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(crearErrorResponse(e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(crearErrorResponse("Error al agregar reseña: " + e.getMessage()));
        }
    }
    
    // 7. Dar like a una reseña (cambiado a String)
    @PostMapping("/{docenteId}/resenas/{resenaId}/like")
    public ResponseEntity<?> darLike(
            @PathVariable String docenteId,
            @PathVariable String resenaId) {
        
        try {
            return docenteService.darLike(docenteId, resenaId)
                    .<ResponseEntity<?>>map(ResponseEntity::ok)
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(crearErrorResponse("Docente o reseña no encontrados")));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(crearErrorResponse("Error al dar like: " + e.getMessage()));
        }
    }
    
    // ============ BÚSQUEDA Y OTROS ============
    
    // 8. Buscar docentes por nombre
    @GetMapping("/buscar")
    public ResponseEntity<List<DocenteEntity>> buscarPorNombre(@RequestParam String nombre) {
        try {
            return ResponseEntity.ok(docenteService.buscarPorNombre(nombre));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList());
        }
    }
    
    // 9. Cargar datos iniciales (desde array de DTOs)
    @PostMapping("/cargar-iniciales")
    public ResponseEntity<?> cargarDocentesIniciales(@RequestBody List<DocenteRequestDTO> docentesRequest) {
        try {
            for (DocenteRequestDTO dto : docentesRequest) {
                validarDocenteRequest(dto);
            }
            List<DocenteEntity> docentesCreados = docenteService.cargarDocentesIniciales(docentesRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(docentesCreados);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(crearErrorResponse(e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(crearErrorResponse("Error al cargar docentes iniciales: " + e.getMessage()));
        }
    }
    
 // 10. Obtener todas las reseñas de un docente por ID
    @GetMapping("/{id}/resenas")
    public ResponseEntity<?> getResenasByDocenteId(@PathVariable String id) {
        try {
            return docenteService.getResenasByDocenteId(id)
                    .<ResponseEntity<?>>map(ResponseEntity::ok)
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(crearErrorResponse("Docente no encontrado con ID: " + id)));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(crearErrorResponse("Error al obtener reseñas: " + e.getMessage()));
        }
    }
    
    // ============ MÉTODOS PRIVADOS DE VALIDACIÓN ============
    
    private void validarDocenteRequest(DocenteRequestDTO request) {
        if (request.getNombre() == null || request.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del docente es requerido");
        }
    }
    
    private void validarResenaRequest(ResenaRequestDTO request) {
        if (request.getEstudiante() == null || request.getEstudiante().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del estudiante es requerido");
        }
        if (request.getComentario() == null || request.getComentario().trim().isEmpty()) {
            throw new IllegalArgumentException("El comentario es requerido");
        }
        if (request.getEstrellas() == null || request.getEstrellas() < 1 || request.getEstrellas() > 5) {
            throw new IllegalArgumentException("Las estrellas deben estar entre 1 y 5");
        }
    }
    
    private Map<String, Object> crearErrorResponse(String mensaje) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", true);
        response.put("mensaje", mensaje);
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }
    
    private Map<String, Object> crearMensajeResponse(String mensaje) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("mensaje", mensaje);
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }
}