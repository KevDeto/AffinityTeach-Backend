package com.affinityteach.domain.port;

import java.util.List;
import java.util.Optional;

import com.affinityteach.domain.model.Docente;

public interface DocenteRepositoryPort {
	List<Docente> findAll();

	Optional<Docente> findById(String uid);

	Docente save(Docente docente);
	
	Docente update(String uid, Docente docente);
	
	void deleteById(String uid);
}
