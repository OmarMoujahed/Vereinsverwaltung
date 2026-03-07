package com.vereinsverwaltung.vereinsverwaltungback.service;

import com.vereinsverwaltung.vereinsverwaltungback.entity.Rolle;
import com.vereinsverwaltung.vereinsverwaltungback.entity.Verein;
import com.vereinsverwaltung.vereinsverwaltungback.repository.RolleRepository;
import com.vereinsverwaltung.vereinsverwaltungback.repository.VereinRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RolleService {
    private final RolleRepository rolleRepository;
    private final VereinRepository vereinRepository;

    public RolleService(RolleRepository rolleRepository, VereinRepository vereinRepository) {
        this.rolleRepository = rolleRepository;
        this.vereinRepository = vereinRepository;
    }


    public List<Rolle> alleRollen() {
        return rolleRepository.findAll();
    }


    public List<Rolle> rollenVonVerein(Long vereinId) {
        Verein verein = vereinRepository.findById(vereinId)
                .orElseThrow(() -> new IllegalArgumentException("Verein nicht gefunden!"));
        return rolleRepository.findByVerein(verein);
    }

    //Alle globalen Rollen
    public List<Rolle> globaleRollen() {
        return rolleRepository.findByIstGlobal(true);
    }


    public Optional<Rolle> rolleFinden(Long id) {
        return rolleRepository.findById(id);
    }


    public Rolle rolleAnlegen(Rolle rolle) {
        if (!rolle.isIstGlobal() && rolle.getVerein() == null) {
            throw new IllegalArgumentException("Eine vereinsspezifische Rolle muss einem Verein zugeordnet sein!");
        }

        if (!rolle.isIstGlobal()) {
            if (rolleRepository.existsByNameAndVerein(rolle.getName(), rolle.getVerein())) {
                throw new IllegalArgumentException("Eine Rolle mit diesem Namen existiert bereits in diesem Verein!");
            }
        } else {
            if (rolleRepository.existsByNameAndIstGlobal(rolle.getName(), true)) {
                throw new IllegalArgumentException("Eine globale Rolle mit diesem Namen existiert bereits!");
            }
        }

        return rolleRepository.save(rolle);
    }


    public Rolle rolleBearbeiten(Long id, Rolle neueDaten) {
        Rolle rolle = rolleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Rolle nicht gefunden!"));

        if (!rolle.getName().equals(neueDaten.getName())) {
            if (!rolle.isIstGlobal()) {
                if (rolleRepository.existsByNameAndVerein(neueDaten.getName(), rolle.getVerein())) {
                    throw new IllegalArgumentException("Eine Rolle mit diesem Namen existiert bereits in diesem Verein!");
                }
            } else {
                if (rolleRepository.existsByNameAndIstGlobal(neueDaten.getName(), true)) {
                    throw new IllegalArgumentException("Eine globale Rolle mit diesem Namen existiert bereits!");
                }
            }
        }

        rolle.setName(neueDaten.getName());
        rolle.setBeschreibung(neueDaten.getBeschreibung());

        return rolleRepository.save(rolle);
    }

    //Rolle löschen
    public void rolleLoeschen(Long id) {
        if (!rolleRepository.existsById(id)) {
            throw new IllegalArgumentException("Rolle nicht gefunden!");
        }
        rolleRepository.deleteById(id);
    }
}

