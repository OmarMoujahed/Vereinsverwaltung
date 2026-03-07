package com.vereinsverwaltung.vereinsverwaltungback.service;


import com.vereinsverwaltung.vereinsverwaltungback.entity.BeitragsStatus;
import com.vereinsverwaltung.vereinsverwaltungback.entity.Mitglied;
import com.vereinsverwaltung.vereinsverwaltungback.entity.Mitgliedsbeitrag;
import com.vereinsverwaltung.vereinsverwaltungback.repository.MitgliedRepository;
import com.vereinsverwaltung.vereinsverwaltungback.repository.MitgliedsbeitragRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class MitgliedsbeitragService {

    private final MitgliedsbeitragRepository beitragRepository;
    private final MitgliedRepository mitgliedRepository;

    public MitgliedsbeitragService(MitgliedsbeitragRepository beitragRepository,
                                   MitgliedRepository mitgliedRepository) {
        this.beitragRepository = beitragRepository;
        this.mitgliedRepository = mitgliedRepository;
    }

    public List<Mitgliedsbeitrag> alleBeitraege() {
        return beitragRepository.findAll();
    }

    public List<Mitgliedsbeitrag> beitraegeVonMitglied(Long mitgliedId) {
        Mitglied mitglied = mitgliedRepository.findById(mitgliedId)
                .orElseThrow(() -> new IllegalArgumentException("Mitglied nicht gefunden!"));
        return beitragRepository.findByMitglied(mitglied);
    }

    public List<Mitgliedsbeitrag> beitraegeNachStatus(BeitragsStatus status) {
        return beitragRepository.findByStatus(status);
    }

    public Optional<Mitgliedsbeitrag> beitragFinden(Long id) {
        return beitragRepository.findById(id);
    }

    public Mitgliedsbeitrag beitragAnlegen(Mitgliedsbeitrag beitrag) {
        if (beitrag.getMitglied() == null) {
            throw new IllegalArgumentException("Beitrag muss einem Mitglied zugeordnet sein!");
        }

        Mitglied mitglied = mitgliedRepository.findById(beitrag.getMitglied().getMitglied_id())
                .orElseThrow(() -> new IllegalArgumentException("Mitglied nicht gefunden!"));
        beitrag.setMitglied(mitglied);

        statusAktualisieren(beitrag);
        return beitragRepository.save(beitrag);
    }

    public Mitgliedsbeitrag beitragBearbeiten(Long id, Mitgliedsbeitrag neueDaten) {
        Mitgliedsbeitrag beitrag = beitragRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Beitrag nicht gefunden!"));

        beitrag.setBetrag(neueDaten.getBetrag());
        beitrag.setZeitraum(neueDaten.getZeitraum());
        beitrag.setFaelligkeitsdatum(neueDaten.getFaelligkeitsdatum());
        beitrag.setStatus(neueDaten.getStatus());

        statusAktualisieren(beitrag);
        return beitragRepository.save(beitrag);
    }

    public Mitgliedsbeitrag beitragAlsBezahltMarkieren(Long id) {
        Mitgliedsbeitrag beitrag = beitragRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Beitrag nicht gefunden!"));

        beitrag.setStatus(BeitragsStatus.BEZAHLT);
        return beitragRepository.save(beitrag);
    }

    public void ueberfaelligeBeitraegeAktualisieren() {
        List<Mitgliedsbeitrag> offeneBeitraege = beitragRepository.findByStatus(BeitragsStatus.OFFEN);
        offeneBeitraege.forEach(this::statusAktualisieren);
        beitragRepository.saveAll(offeneBeitraege);
    }

    public void beitragLoeschen(Long id) {
        if (!beitragRepository.existsById(id)) {
            throw new IllegalArgumentException("Beitrag nicht gefunden!");
        }
        beitragRepository.deleteById(id);
    }

    private void statusAktualisieren(Mitgliedsbeitrag beitrag) {
        if (beitrag.getFaelligkeitsdatum() != null &&
                beitrag.getFaelligkeitsdatum().isBefore(LocalDate.now()) &&
                beitrag.getStatus() == BeitragsStatus.OFFEN) {
            beitrag.setStatus(BeitragsStatus.UEBERFAELLIG);
        }
    }
}