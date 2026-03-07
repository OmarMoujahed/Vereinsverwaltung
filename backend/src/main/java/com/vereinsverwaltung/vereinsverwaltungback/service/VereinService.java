package com.vereinsverwaltung.vereinsverwaltungback.service;

import com.vereinsverwaltung.vereinsverwaltungback.entity.Verein;
import com.vereinsverwaltung.vereinsverwaltungback.repository.VereinRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VereinService {
    private final VereinRepository vereinRepository;

    public VereinService(VereinRepository vereinRepository) {
        this.vereinRepository = vereinRepository;
    }


    public List<Verein> alleVereine() {
        return vereinRepository.findAll();
    }

    //Verein per ID finden
    public Optional<Verein> vereinFinden(Long id) {
        return vereinRepository.findById(id);
    }


    public Verein vereinAnlegen(Verein verein) {
        if (vereinRepository.existsByName(verein.getName())) {
            throw new IllegalArgumentException("Ein Verein mit diesem Namen existiert bereits!");
        }
        return vereinRepository.save(verein);
    }


    public Verein vereinBearbeiten(Long id, Verein neueDaten) {
        Verein verein = vereinRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Verein nicht gefunden!"));
        if (!verein.getName().equals(neueDaten.getName())) {
            if (vereinRepository.existsByName(neueDaten.getName())) {
                throw new IllegalArgumentException("Ein Verein mit diesem Namen existiert bereits!");
            }
            verein.setName(neueDaten.getName());
        }

        verein.setBeschreibung(neueDaten.getBeschreibung());
        verein.setEmail(neueDaten.getEmail());
        verein.setTelefon(neueDaten.getTelefon());
        verein.setAdresse(neueDaten.getAdresse());

        return vereinRepository.save(verein);
    }


    public void vereinLoeschen(Long id) {
        if (!vereinRepository.existsById(id)) {
            throw new IllegalArgumentException("Verein nicht gefunden!");
        }
        vereinRepository.deleteById(id);
    }
}