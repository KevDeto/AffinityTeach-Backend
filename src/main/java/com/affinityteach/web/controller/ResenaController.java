package com.affinityteach.web.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.affinityteach.application.dto.ResenaRequestDTO;
import com.affinityteach.application.dto.ResenaResponseDTO;
import com.affinityteach.application.dto.UsuarioAutenticadoDTO;
import com.affinityteach.application.service.ResenaService;
import com.affinityteach.security.UsuarioAutenticadoProvider;

@RestController
@RequestMapping("/api/docentes/{docenteId}/resenas")
public class ResenaController {
    private final ResenaService resenaService;
    private final UsuarioAutenticadoProvider usuarioProvider;

    public ResenaController(
            ResenaService resenaService,
            UsuarioAutenticadoProvider usuarioProvider) {
        this.resenaService = resenaService;
        this.usuarioProvider = usuarioProvider;
    }

    @GetMapping
    public ResponseEntity<List<ResenaResponseDTO>> listarPorDocente(
            @PathVariable String docenteId) {

        return ResponseEntity.ok(
                resenaService.obtenerPorDocente(docenteId)
        );
    }

    @PostMapping
    public ResponseEntity<ResenaResponseDTO> crear(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String docenteId,
            @RequestBody ResenaRequestDTO dto) {

        UsuarioAutenticadoDTO usuario =
                usuarioProvider.getCurrentUser(jwt);

        return ResponseEntity.ok(
                resenaService.crear(docenteId, usuario, dto)
        );
    }
    
    @PostMapping("/{resenaId}/like")
    public ResponseEntity<ResenaResponseDTO> darLike(
            @PathVariable String docenteId,
            @PathVariable String resenaId) {

        return ResponseEntity.ok(
                resenaService.darLike(docenteId, resenaId)
        );
    }
}
