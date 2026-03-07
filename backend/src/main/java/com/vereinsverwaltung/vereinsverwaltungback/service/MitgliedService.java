package com.vereinsverwaltung.vereinsverwaltungback.service;

import com.vereinsverwaltung.vereinsverwaltungback.entity.Gruppe;
import com.vereinsverwaltung.vereinsverwaltungback.entity.Mitglied;
import com.vereinsverwaltung.vereinsverwaltungback.entity.Rolle;
import com.vereinsverwaltung.vereinsverwaltungback.entity.Verein;
import com.vereinsverwaltung.vereinsverwaltungback.repository.GruppeRepository;
import com.vereinsverwaltung.vereinsverwaltungback.repository.MitgliedRepository;
import com.vereinsverwaltung.vereinsverwaltungback.repository.RolleRepository;
import com.vereinsverwaltung.vereinsverwaltungback.repository.VereinRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MitgliedService {

    private final MitgliedRepository mitgliedRepository;
    private final VereinRepository vereinRepository;
    private final RolleRepository rolleRepository;
    private final GruppeRepository gruppeRepository;

    public MitgliedService(MitgliedRepository mitgliedRepository,
                           VereinRepository vereinRepository,
                           RolleRepository rolleRepository,
                           GruppeRepository gruppeRepository) {
        this.mitgliedRepository = mitgliedRepository;
        this.vereinRepository = vereinRepository;
        this.rolleRepository = rolleRepository;
        this.gruppeRepository = gruppeRepository;
    }


    public List<Mitglied> alleMitglieder() {
        return mitgliedRepository.findAll();
    }

    public List<Mitglied> mitgliederVonVerein(Long vereinId) {
        Verein verein = vereinRepository.findById(vereinId)
                .orElseThrow(() -> new IllegalArgumentException("Verein nicht gefunden!"));
        return mitgliedRepository.findByVerein(verein);
    }


    public Optional<Mitglied> mitgliedFinden(Long id) {
        return mitgliedRepository.findById(id);
    }

    public Mitglied mitgliedAnlegen(Mitglied mitglied) {
        if (mitglied.getVerein() == null) {
            throw new IllegalArgumentException("Mitglied muss einem Verein zugeordnet sein!");
        }

        Verein verein = vereinRepository.findById(mitglied.getVerein().getVerein_id())
                .orElseThrow(() -> new IllegalArgumentException("Verein nicht gefunden!"));
        mitglied.setVerein(verein);

        if (mitglied.getRolle() == null) {
            throw new IllegalArgumentException("Mitglied muss eine Rolle haben!");
        }

        Rolle rolle = rolleRepository.findById(mitglied.getRolle().getRolle_id())
                .orElseThrow(() -> new IllegalArgumentException("Rolle nicht gefunden!"));

        if (!rolle.isIstGlobal() && !rolle.getVerein().getVerein_id().equals(verein.getVerein_id())) {
            throw new IllegalArgumentException("Diese Rolle gehört nicht zu diesem Verein!");
        }

        mitglied.setRolle(rolle);

        return mitgliedRepository.save(mitglied);
    }

    public Mitglied mitgliedBearbeiten(Long id, Mitglied neueDaten) {
        Mitglied mitglied = mitgliedRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Mitglied nicht gefunden!"));

        if (neueDaten.getRolle() != null &&
                !neueDaten.getRolle().getRolle_id().equals(mitglied.getRolle().getRolle_id())) {

            Rolle neueRolle = rolleRepository.findById(neueDaten.getRolle().getRolle_id())
                    .orElseThrow(() -> new IllegalArgumentException("Rolle nicht gefunden!"));

            if (!neueRolle.isIstGlobal() &&
                    !neueRolle.getVerein().getVerein_id().equals(mitglied.getVerein().getVerein_id())) {
                throw new IllegalArgumentException("Diese Rolle gehört nicht zu diesem Verein!");
            }

            mitglied.setRolle(neueRolle);
        }

        mitglied.setVorname(neueDaten.getVorname());
        mitglied.setNachname(neueDaten.getNachname());
        mitglied.setEmail(neueDaten.getEmail());
        mitglied.setTelefon(neueDaten.getTelefon());
        mitglied.setGeburtsdatum(neueDaten.getGeburtsdatum());
        mitglied.setEintrittsdatum(neueDaten.getEintrittsdatum());
        mitglied.setAdresse(neueDaten.getAdresse());

        return mitgliedRepository.save(mitglied);
    }


    public Mitglied mitgliedZuGruppeHinzufuegen(Long mitgliedId, Long gruppeId) {
        Mitglied mitglied = mitgliedRepository.findById(mitgliedId)
                .orElseThrow(() -> new IllegalArgumentException("Mitglied nicht gefunden!"));

        Gruppe gruppe = gruppeRepository.findById(gruppeId)
                .orElseThrow(() -> new IllegalArgumentException("Gruppe nicht gefunden!"));

        if (!mitglied.getVerein().getVerein_id().equals(gruppe.getVerein().getVerein_id())) {
            throw new IllegalArgumentException("Mitglied und Gruppe gehören nicht zum selben Verein!");
        }

        mitglied.getGruppen().add(gruppe);
        return mitgliedRepository.save(mitglied);
    }

    public Mitglied mitgliedAusGruppeEntfernen(Long mitgliedId, Long gruppeId) {
        Mitglied mitglied = mitgliedRepository.findById(mitgliedId)
                .orElseThrow(() -> new IllegalArgumentException("Mitglied nicht gefunden!"));

        Gruppe gruppe = gruppeRepository.findById(gruppeId)
                .orElseThrow(() -> new IllegalArgumentException("Gruppe nicht gefunden!"));

        mitglied.getGruppen().remove(gruppe);
        return mitgliedRepository.save(mitglied);
    }

    public void mitgliedLoeschen(Long id) {
        if (!mitgliedRepository.existsById(id)) {
            throw new IllegalArgumentException("Mitglied nicht gefunden!");
        }
        mitgliedRepository.deleteById(id);
    }
}