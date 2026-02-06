package com.affinityteach.cache;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import com.affinityteach.model.entity.DocenteEntity;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;

import jakarta.annotation.PostConstruct;

public class DocenteCache {
	private List<DocenteEntity> cache = new ArrayList<>();
	private long lastUpdate = 0;
	private final Firestore firestore;
	private static final long CACHE_TTL = 30 * 60 * 1000; // 30 minutos

	public DocenteCache(Firestore firestore) {
		this.firestore = firestore;
	}

	@PostConstruct
	public void init() {
		refreshCache();
	}

	public synchronized List<DocenteEntity> getDocentes() {
		if (System.currentTimeMillis() - lastUpdate > CACHE_TTL) {
			refreshCache();
		}
		return new ArrayList<>(cache);
	}

	public synchronized Optional<DocenteEntity> getDocenteById(String id) {
		// Primero busco en cache
		return cache.stream().filter(d -> id.equals(d.getId())).findFirst();
	}

	public synchronized void refreshCache() {
		System.out.println("Actualizando cache de docentes desde Firestore...");

		try {
			CollectionReference docentesCollection = firestore.collection("docentes");
			ApiFuture<QuerySnapshot> future = docentesCollection.get();
			QuerySnapshot snapshot = future.get();

			List<DocenteEntity> nuevosDocentes = new ArrayList<>();
			for (DocumentSnapshot doc : snapshot.getDocuments()) {
				DocenteEntity docente = doc.toObject(DocenteEntity.class);
				if (docente != null) {
					docente.setId(doc.getId());
					nuevosDocentes.add(docente);
				}
			}

			// Ordenar por nombre
			nuevosDocentes.sort(Comparator.comparing(DocenteEntity::getNombre));

			cache = nuevosDocentes;
			lastUpdate = System.currentTimeMillis();

			System.out.println("Cache actualizado: " + cache.size() + " docentes");

		} catch (InterruptedException | ExecutionException e) {
			System.err.println("Error actualizando cache: " + e.getMessage());
			Thread.currentThread().interrupt();
		}
	}

	public synchronized void actualizarDocenteIndividual(String docenteId) {
		try {
			DocumentReference docenteRef = firestore.collection("docentes").document(docenteId);
			DocumentSnapshot doc = docenteRef.get().get();

			if (doc.exists()) {
				DocenteEntity docenteActualizado = doc.toObject(DocenteEntity.class);
				if (docenteActualizado != null) {
					docenteActualizado.setId(doc.getId());

					// Encontrar y reemplazar en cache
					boolean encontrado = false;
					for (int i = 0; i < cache.size(); i++) {
						if (docenteId.equals(cache.get(i).getId())) {
							cache.set(i, docenteActualizado);
							encontrado = true;
							break;
						}
					}

					if (!encontrado) {
						cache.add(docenteActualizado);
						cache.sort(Comparator.comparing(DocenteEntity::getNombre));
					}
				}
			}
		} catch (Exception e) {
			System.err.println("Error actualizando docente individual: " + e.getMessage());
		}
	}
	
	public synchronized void actualizarDocenteIndividualConDatos(String docenteId, DocenteEntity docenteActualizado) {
	    
	    boolean encontrado = false;
	    for (int i = 0; i < cache.size(); i++) {
	        if (docenteId.equals(cache.get(i).getId())) {
	            cache.set(i, docenteActualizado);
	            encontrado = true;
	            break;
	        }
	    }
	    
	    if (!encontrado) {
	        cache.add(docenteActualizado);
	        cache.sort(Comparator.comparing(DocenteEntity::getNombre));
	    }
	    
	}

	public int getCacheSize() {
		return cache.size();
	}

	public long getLastUpdate() {
		return lastUpdate;
	}
}
