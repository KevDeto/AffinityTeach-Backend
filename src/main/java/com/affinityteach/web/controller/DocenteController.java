package com.affinityteach.web.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.affinityteach.application.dto.DocenteResponseDTO;
import com.affinityteach.application.service.DocenteService;

@RestController
@RequestMapping("/api/docentes")
public class DocenteController {
    private final DocenteService docenteService;

    public DocenteController(DocenteService docenteService) {
        this.docenteService = docenteService;
    }

    @GetMapping
    public List<DocenteResponseDTO> obtenerTodos() {
        return docenteService.obtenerTodos();
    }

    @GetMapping("/{id}")
    public DocenteResponseDTO obtenerPorId(@PathVariable String id) {
        return docenteService.obtenerPorId(id);
    }
}
