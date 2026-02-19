package com.affinityteach.domain.port;

import java.util.List;
import java.util.Optional;

import com.affinityteach.domain.model.Resena;

public interface ResenaRepositoryPort {
    List<Resena> findByDocenteId(String docenteUid);

    Optional<Resena> findByDocenteIdAndEmail(String docenteUid, String email);
    
    Optional<Resena> findByDocenteIdAndId(String docenteUid, String resenaId);

    Resena save(Resena resena);
    
    Resena update(Resena resena);

    void deleteById(String docenteUid, String uid);
}
