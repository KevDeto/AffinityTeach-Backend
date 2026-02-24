package com.affinityteach.application.cache;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.affinityteach.domain.model.Docente;
import com.affinityteach.domain.port.DocenteRepositoryPort;

import jakarta.annotation.PostConstruct;

@Component
public class DocenteCache {
	private static final Logger log = LoggerFactory.getLogger(DocenteCache.class);

    private final DocenteRepositoryPort docenteRepository;

    private volatile List<Docente> cache = new ArrayList<>();
    private volatile long lastUpdate = 0;

    private static final long CACHE_TTL = 30 * 60 * 1000; // 30 minutos

    public DocenteCache(DocenteRepositoryPort docenteRepository) {
        this.docenteRepository = docenteRepository;
    }

    @PostConstruct
    public void init() {
    	log.info("DocenteCache inicializada (lazy mode).");
    }

    public List<Docente> getAll() {
        if (isExpired()) {
            refresh();
        }
        return new ArrayList<>(cache);
    }

    public Optional<Docente> getById(String id) {
        if (isExpired()) {
            refresh();
        }
        return cache.stream()
                .filter(d -> d.getUid().equals(id))
                .findFirst();
    }

    private boolean isExpired() {
        return System.currentTimeMillis() - lastUpdate > CACHE_TTL;
    }

    private synchronized void refresh() {
        try {
            log.info("Refrescando cache de docentes...");

            List<Docente> nuevos = docenteRepository.findAll();

            nuevos.sort(
                Comparator.comparing(
                    Docente::getNombre,
                    Comparator.nullsLast(String::compareToIgnoreCase)
                )
            );

            cache = nuevos;
            lastUpdate = System.currentTimeMillis();

            log.info("Cache actualizada con {} docentes.", cache.size());

        } catch (Exception e) {
            log.error("Error refrescando cache, se mantiene cache anterior", e);
        }
    }

    public synchronized void updateSingle(Docente docenteActualizado) {
        List<Docente> nuevaLista = new ArrayList<>(cache);

        boolean encontrado = false;

        for (int i = 0; i < nuevaLista.size(); i++) {
            if (nuevaLista.get(i).getUid().equals(docenteActualizado.getUid())) {
                nuevaLista.set(i, docenteActualizado);
                encontrado = true;
                break;
            }
        }

        if (!encontrado) {
            nuevaLista.add(docenteActualizado);
        }

        nuevaLista.sort(Comparator.comparing(Docente::getNombre));

        cache = nuevaLista;
    }

    public synchronized void remove(String uid) {
        List<Docente> nuevaLista = new ArrayList<>(cache);

        nuevaLista.removeIf(d -> d.getUid().equals(uid));

        cache = nuevaLista;
    }
    
    public int size() {
        return cache.size();
    }

    public long getLastUpdate() {
        return lastUpdate;
    }
}

