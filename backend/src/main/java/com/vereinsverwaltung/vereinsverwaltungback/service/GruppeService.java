package com.vereinsverwaltung.vereinsverwaltungback.service;

import com.vereinsverwaltung.vereinsverwaltungback.entity.Gruppe;
import com.vereinsverwaltung.vereinsverwaltungback.entity.Verein;
import com.vereinsverwaltung.vereinsverwaltungback.repository.GruppeRepository;
import com.vereinsverwaltung.vereinsverwaltungback.repository.VereinRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GruppeService {
    private final GruppeRepository gruppeRepository;
    private final VereinRepository vereinRepository;

    public GruppeService(GruppeRepository gruppeRepository, VereinRepository vereinRepository) {
        this.gruppeRepository = gruppeRepository;
        this.vereinRepository = vereinRepository;
    }

    public List<Gruppe> alleGruppen() {
        return gruppeRepository.findAll();
    }

    public List<Gruppe> gruppenVonVerein(Long vereinId) {
        Verein verein = vereinRepository.findById(vereinId)
                .orElseThrow(() -> new IllegalArgumentException("Verein nicht gefunden!"));
        return gruppeRepository.findByVerein(verein);
    }

    public Optional<Gruppe> gruppeFinden(Long id) {
        return gruppeRepository.findById(id);
    }

    public Gruppe gruppeAnlegen(Gruppe gruppe) {
        if (gruppe.getVerein() == null) {
            throw new IllegalArgumentException("Gruppe muss einem Verein zugeordnet sein!");
        }

        Verein verein = vereinRepository.findById(gruppe.getVerein().getVerein_id())
                .orElseThrow(() -> new IllegalArgumentException("Verein nicht gefunden!"));
        if (gruppeRepository.existsByNameAndVerein(gruppe.getName(), verein)) {
            throw new IllegalArgumentException("Eine Gruppe mit diesem Namen existiert bereits in diesem Verein!");
        }
        gruppe.setVerein(verein);
        return gruppeRepository.save(gruppe);
    }

    public Gruppe gruppeBearbeiten(Long id, Gruppe neueDaten) {
        Gruppe gruppe = gruppeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Gruppe nicht gefunden!"));

        if (!gruppe.getName().equals(neueDaten.getName())) {
            if (gruppeRepository.existsByNameAndVerein(neueDaten.getName(), gruppe.getVerein())) {
                throw new IllegalArgumentException("Eine Gruppe mit diesem Namen existiert bereits in diesem Verein!");
            }
        }

        gruppe.setName(neueDaten.getName());
        gruppe.setBeschreibung(neueDaten.getBeschreibung());

        return gruppeRepository.save(gruppe);
    }

    public void gruppeLoeschen(Long id) {
        if (!gruppeRepository.existsById(id)) {
            throw new IllegalArgumentException("Gruppe nicht gefunden!");
        }
        gruppeRepository.deleteById(id);
    }
}