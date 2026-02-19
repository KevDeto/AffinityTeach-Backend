package com.affinityteach.web.admin;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.affinityteach.application.dto.DocenteRequestDTO;
import com.affinityteach.application.dto.DocenteResponseDTO;
import com.affinityteach.application.dto.UsuarioAutenticadoDTO;
import com.affinityteach.application.service.DocenteService;
import com.affinityteach.security.UsuarioAutenticadoProvider;

@RestController
@RequestMapping("/api/docentes/admin")
public class AdminDocenteController {
    private final DocenteService docenteService;
    private final UsuarioAutenticadoProvider usuarioProvider;

    public AdminDocenteController(
    		DocenteService docenteService,
            UsuarioAutenticadoProvider usuarioProvider) {
        this.docenteService = docenteService;
        this.usuarioProvider = usuarioProvider;
    }

    @PostMapping
    public ResponseEntity<DocenteResponseDTO> crear(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody DocenteRequestDTO dto) {

    	UsuarioAutenticadoDTO usuario =
                usuarioProvider.getCurrentUser(jwt);

        System.out.println("Admin que crea un docente: " + usuario.email());

        return ResponseEntity.ok(docenteService.crear(dto));
    }
    
    @PostMapping("/bulk")
    public ResponseEntity<List<DocenteResponseDTO>> crearMultiples(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody List<DocenteRequestDTO> dtos) {

        UsuarioAutenticadoDTO usuario =
                usuarioProvider.getCurrentUser(jwt);

        System.out.println("Admin que importa docentes: " + usuario.email());

        return ResponseEntity.ok(
                docenteService.crearMultiples(dtos)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<DocenteResponseDTO> actualizar(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String id,
            @RequestBody DocenteRequestDTO dto) {

        UsuarioAutenticadoDTO usuario =
                usuarioProvider.getCurrentUser(jwt);

        System.out.println("Admin que actualiza un docente: " + usuario.email());

        return ResponseEntity.ok(docenteService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String id) {

        UsuarioAutenticadoDTO usuario =
                usuarioProvider.getCurrentUser(jwt);

        System.out.println("Admin que elimina un docente: " + usuario.email());

        docenteService.eliminar(id);

        return ResponseEntity.noContent().build();
    }
}
