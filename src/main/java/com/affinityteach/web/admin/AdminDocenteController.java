package com.affinityteach.web.admin;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.affinityteach.application.dto.DocenteRequestDTO;
import com.affinityteach.application.dto.DocenteResponseDTO;
import com.affinityteach.application.service.DocenteService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/docentes/admin")
public class AdminDocenteController {
    private final DocenteService docenteService;

    public AdminDocenteController(
    		DocenteService docenteService) {
        this.docenteService = docenteService;
    }

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public DocenteResponseDTO crear(
            @RequestBody @Valid DocenteRequestDTO dto) {

        return docenteService.crear(dto);
    }
    
    @PostMapping("/bulk")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(code = HttpStatus.CREATED)
    public List<DocenteResponseDTO> crearMultiples(
            @RequestBody List<DocenteRequestDTO> dtos) {

        return docenteService.crearMultiples(dtos);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    public DocenteResponseDTO actualizar(
            @PathVariable String id,
            @RequestBody @Valid DocenteRequestDTO dto) {

        return docenteService.actualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void eliminar(
    		@PathVariable String id) {

        docenteService.eliminar(id);
    }
}
