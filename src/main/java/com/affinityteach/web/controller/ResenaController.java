package com.affinityteach.web.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.affinityteach.application.dto.ResenaRequestDTO;
import com.affinityteach.application.dto.ResenaResponseDTO;
import com.affinityteach.application.dto.UsuarioAutenticadoDTO;
import com.affinityteach.application.service.ResenaService;
import com.affinityteach.security.UsuarioAutenticadoProvider;

import jakarta.validation.Valid;

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
    public List<ResenaResponseDTO> listarPorDocente(
            @PathVariable String docenteId) {

        return resenaService.obtenerPorDocente(docenteId);
    }

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public ResenaResponseDTO crear(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String docenteId,
            @RequestBody @Valid ResenaRequestDTO dto) {

        UsuarioAutenticadoDTO usuario =
                usuarioProvider.getCurrentUser(jwt);

        return resenaService.crear(docenteId, usuario, dto);
    }
    
    @PostMapping("/{resenaId}/like")
    @ResponseStatus(HttpStatus.OK)
    public ResenaResponseDTO darLike(
            @PathVariable String docenteId,
            @PathVariable String resenaId) {

        return resenaService.darLike(docenteId, resenaId);
    }
}
