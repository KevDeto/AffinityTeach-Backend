package com.affinityteach.model.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.affinityteach.model.entity.DocenteEntity;

public class DocentePublicDTO {
    private String id;
    private String nombre;
    private Double puntaje;
    private Integer cantResenas;
    private List<String> materias;
    private List<ReviewPublicDTO> resenas;
    
    public DocentePublicDTO(DocenteEntity entity) {
        this.id = entity.getId();
        this.nombre = entity.getNombre();
        this.puntaje = entity.getPuntaje();
        this.cantResenas = entity.getCantResenas();
        this.materias = entity.getMaterias() != null ? 
            new ArrayList<>(entity.getMaterias()) : new ArrayList<>();
        
        if (entity.getResenas() != null) {
            this.resenas = entity.getResenas().stream()
                .map(ReviewPublicDTO::new)
                .collect(Collectors.toList());
        } else {
            this.resenas = new ArrayList<>();
        }
    }

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public Double getPuntaje() {
		return puntaje;
	}

	public void setPuntaje(Double puntaje) {
		this.puntaje = puntaje;
	}

	public Integer getCantResenas() {
		return cantResenas;
	}

	public void setCantResenas(Integer cantResenas) {
		this.cantResenas = cantResenas;
	}

	public List<String> getMaterias() {
		return materias;
	}

	public void setMaterias(List<String> materias) {
		this.materias = materias;
	}

	public List<ReviewPublicDTO> getResenas() {
		return resenas;
	}

	public void setResenas(List<ReviewPublicDTO> resenas) {
		this.resenas = resenas;
	}
    
    
}
