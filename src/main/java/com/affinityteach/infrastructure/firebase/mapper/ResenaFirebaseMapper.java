package com.affinityteach.infrastructure.firebase.mapper;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.affinityteach.domain.model.Resena;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentSnapshot;

@Component
public class ResenaFirebaseMapper {
    public Map<String, Object> toFirestore(Resena resena) {

        Map<String, Object> data = new HashMap<>();

        data.put("docenteUid", resena.getDocenteUid());
        data.put("estudianteNombre", resena.getEstudianteNombre());
        data.put("comentario", resena.getComentario());
        data.put("estrellas", resena.getEstrellas());
        data.put("likes", resena.getLikes());
        data.put("fotoUrl", resena.getFotoUrl());
        data.put("email", resena.getEmail());

        if (resena.getFecha() != null) {
            Timestamp timestamp = Timestamp.ofTimeSecondsAndNanos(
                    resena.getFecha().getEpochSecond(),
                    resena.getFecha().getNano()
            );
            data.put("fecha", timestamp);
        }

        return data;
    }

    public Resena toDomain(DocumentSnapshot document) {

        Timestamp timestamp = document.getTimestamp("fecha");

        Instant instant = null;

        if (timestamp != null) {
            instant = Instant.ofEpochSecond(
                    timestamp.getSeconds(),
                    timestamp.getNanos()
            );
        }
        
        Long estrellasLong = document.getLong("estrellas");
        int estrellas = estrellasLong != null ? estrellasLong.intValue() : 0;

        Long likesLong = document.getLong("likes");
        int likes = likesLong != null ? likesLong.intValue() : 0;

        Resena resena = new Resena(
                document.getId(),
                document.getString("docenteUid"),
                document.getString("estudianteNombre"),
                document.getString("comentario"),
                estrellas,
                instant,
                likes,
                document.getString("fotoUrl"),
                document.getString("email")
        );

        return resena;
    }
}
