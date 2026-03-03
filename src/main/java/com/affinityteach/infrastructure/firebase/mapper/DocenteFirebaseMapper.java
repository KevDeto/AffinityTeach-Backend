package com.affinityteach.infrastructure.firebase.mapper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.affinityteach.domain.model.Docente;
import com.google.cloud.firestore.DocumentSnapshot;


@Component
public class DocenteFirebaseMapper {
    public Docente toDomain(DocumentSnapshot doc) {
        String id = doc.getId();
        String nombre = doc.getString("nombre");
        Double puntaje = doc.getDouble("puntaje");
        Integer cantidadResenas = doc.getLong("cantidadResenas") != null ? 
				doc.getLong("cantidadResenas").intValue() : 0;

        // Firebase devuelve un List<?> y java no puede garantisar que es List<String>
        // Este manejo seguro de materias evita el warning en el constructor
        List<String> materias = new ArrayList<>();
        Object materiasObj = doc.get("materias");

        if (materiasObj instanceof List<?>) {
            for (Object item : (List<?>) materiasObj) {
                materias.add(String.valueOf(item));
            }
        }

        return new Docente(
                id,
                nombre,
                puntaje,
                cantidadResenas,
                materias
        );
    }
}
