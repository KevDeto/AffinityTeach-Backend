package com.affinityteach.infrastructure.firebase.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Repository;

import com.affinityteach.domain.model.Resena;
import com.affinityteach.domain.port.ResenaRepositoryPort;
import com.affinityteach.infrastructure.firebase.mapper.ResenaFirebaseMapper;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;

@Repository
public class FirebaseResenaRepository implements ResenaRepositoryPort{
	private final Firestore firestore;
    private final ResenaFirebaseMapper mapper;

    public FirebaseResenaRepository(
    		Firestore firestore, 
    		ResenaFirebaseMapper mapper) {
		this.firestore = firestore;
		this.mapper = mapper;
	}

	@Override
    public Resena save(Resena resena) {

        CollectionReference collection = firestore
                .collection("docentes")
                .document(resena.getDocenteUid())
                .collection("resenas");

        Map<String, Object> data = mapper.toFirestore(resena);

        DocumentReference docRef = collection.document();
        docRef.set(data);

        resena.setUid(docRef.getId());

        return resena;
    }

    @Override
    public List<Resena> findByDocenteId(String docenteUid) {

        CollectionReference collection = firestore
                .collection("docentes")
                .document(docenteUid)
                .collection("resenas");

        try {
            List<QueryDocumentSnapshot> documents =
                    collection.get().get().getDocuments();

            return documents.stream()
                    .map(mapper::toDomain)
                    .toList();

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error fetching resenas", e);
        }
    }

	@Override
	public Optional<Resena> findByDocenteIdAndEmail(String docenteUid, String email) {
	    CollectionReference collection = firestore
	            .collection("docentes")
	            .document(docenteUid)
	            .collection("resenas");

	    try {
	        Query query = collection.whereEqualTo("email", email).limit(1);

	        List<QueryDocumentSnapshot> documents =
	                query.get().get().getDocuments();

	        if (documents.isEmpty()) {
	            return Optional.empty();
	        }

	        return Optional.of(mapper.toDomain(documents.get(0)));

	    } catch (InterruptedException | ExecutionException e) {
	        throw new RuntimeException("Error fetching resena by email", e);
	    }
	}

	@Override
	public void deleteById(String docenteUid, String uid) {
	    firestore.collection("docentes")
        .document(docenteUid)
        .collection("resenas")
        .document(uid)
        .delete();
	}

	@Override
	public Optional<Resena> findByDocenteIdAndId(String docenteUid, String resenaId) {

	    DocumentReference ref = firestore
	            .collection("docentes")
	            .document(docenteUid)
	            .collection("resenas")
	            .document(resenaId);

	    try {
	        DocumentSnapshot snapshot = ref.get().get();

	        if (!snapshot.exists()) {
	            return Optional.empty();
	        }

	        return Optional.of(mapper.toDomain(snapshot));

	    } catch (InterruptedException e) {
	        Thread.currentThread().interrupt();
	        throw new RuntimeException("Thread interrupted while fetching resena", e);

	    } catch (ExecutionException e) {
	        throw new RuntimeException("Error fetching resena by id", e);
	    }
	}
}
