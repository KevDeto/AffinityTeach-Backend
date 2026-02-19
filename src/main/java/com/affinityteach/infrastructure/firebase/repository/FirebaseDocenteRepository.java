package com.affinityteach.infrastructure.firebase.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Repository;

import com.affinityteach.domain.model.Docente;
import com.affinityteach.domain.port.DocenteRepositoryPort;
import com.affinityteach.infrastructure.firebase.mapper.DocenteFirebaseMapper;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;

@Repository
public class FirebaseDocenteRepository implements DocenteRepositoryPort {
	private final Firestore firestore;
	private final DocenteFirebaseMapper mapper;
	
	public FirebaseDocenteRepository(
			Firestore firestore,
			DocenteFirebaseMapper mapper) {
		this.firestore = firestore;
		this.mapper = mapper;
	}

	@Override
	public List<Docente> findAll() {
		try {
			List<QueryDocumentSnapshot> documents = firestore
					.collection("docentes")
					.get()
					.get()
					.getDocuments();

			return documents
					.stream()
					.map(mapper::toDomain)
					.toList();

		} catch (InterruptedException | ExecutionException e) {
			throw new RuntimeException("Error fetching docentes", e);
		}
	}

	@Override
	public Optional<Docente> findById(String uid) {
		try {
			DocumentSnapshot doc = firestore
					.collection("docentes")
					.document(uid)
					.get()
					.get();

			if (!doc.exists()) {
				return Optional.empty();
			}

			return Optional.of(mapper.toDomain(doc));

		} catch (InterruptedException | ExecutionException e) {
			throw new RuntimeException("Error fetching docente", e);
		}
	}

	@Override
	public Docente save(Docente docente) {		
	    CollectionReference collection = firestore.collection("docentes");
		
	    DocumentReference docRef;
	    
	    if (docente.getUid() == null) {
	        docRef = collection.document(); // ID automatico
	        docente.setUid(docRef.getId());
	    } else {
	        docRef = collection.document(docente.getUid());
	    }
	    
		Map<String, Object> data = new HashMap<>();
		data.put("nombre", docente.getNombre());
		data.put("puntaje", docente.getPuntaje());
		data.put("cantidadResenas", docente.getCantidadResenas());
		data.put("materias", docente.getMaterias());

		docRef.set(data);
		
		return docente;
	}

	@Override
	public void deleteById(String uid) {
		firestore
			.collection("docentes")
			.document(uid)
			.delete();
	}

	@Override
	public Docente update(String uid, Docente docente) {
	    DocumentReference docRef = firestore
	            .collection("docentes")
	            .document(uid);

	    Map<String, Object> data = new HashMap<>();
	    data.put("nombre", docente.getNombre());
	    data.put("puntaje", docente.getPuntaje());
	    data.put("cantidadResenas", docente.getCantidadResenas());
	    data.put("materias", docente.getMaterias());

	    docRef.set(data);

	    return docente;
	}
}
