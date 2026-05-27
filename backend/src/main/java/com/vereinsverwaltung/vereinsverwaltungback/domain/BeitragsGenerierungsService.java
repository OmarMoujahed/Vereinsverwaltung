package com.vereinsverwaltung.vereinsverwaltungback.domain;

import com.vereinsverwaltung.vereinsverwaltungback.entity.BeitragsStatus;
import com.vereinsverwaltung.vereinsverwaltungback.entity.BeitragsTyp;
import com.vereinsverwaltung.vereinsverwaltungback.entity.Mitglied;
import com.vereinsverwaltung.vereinsverwaltungback.entity.Mitgliedsbeitrag;
import com.vereinsverwaltung.vereinsverwaltungback.repository.MitgliedRepository;
import com.vereinsverwaltung.vereinsverwaltungback.repository.MitgliedsbeitragRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class BeitragsGenerierungsService {

    private final MitgliedsbeitragRepository beitragRepository;
    private final MitgliedRepository mitgliedRepository;

    public BeitragsGenerierungsService(MitgliedsbeitragRepository beitragRepository, MitgliedRepository mitgliedRepository) {
        this.beitragRepository = beitragRepository;
        this.mitgliedRepository = mitgliedRepository;
    }

    @Transactional
    public List<Mitgliedsbeitrag> generiereWiederkehrendeBeitraege() {
        LocalDate heute = LocalDate.now();
        List<Mitgliedsbeitrag> neueBeitraege = new ArrayList<>();

        List<Mitgliedsbeitrag> bezahlteBeitraege = beitragRepository.findByStatus(BeitragsStatus.BEZAHLT);

        for (Mitgliedsbeitrag beitrag : bezahlteBeitraege) {
            if (beitrag.istWiederkehrend()) {
                LocalDate naechstesFaelligkeitsdatum = beitrag.berechneNaechstesFaelligkeitsdatum();

                if (beitrag.getFaelligkeitsdatum().isBefore(heute) || beitrag.getFaelligkeitsdatum().isEqual(heute)) {
                    if (!existiertFolgeBeitrag(beitrag, naechstesFaelligkeitsdatum)) {
                        Mitgliedsbeitrag neuerBeitrag = erstelleFolgeBeitrag(beitrag, naechstesFaelligkeitsdatum);
                        neueBeitraege.add(neuerBeitrag);
                    }
                }
            }
        }

        return beitragRepository.saveAll(neueBeitraege);
    }

    @Transactional
    public List<Mitgliedsbeitrag> generiereWiederkehrendeBeitraegeFuerMitglied(Long mitgliedId) {
        Mitglied mitglied = mitgliedRepository.findById(mitgliedId)
                .orElseThrow(() -> new IllegalArgumentException("Mitglied nicht gefunden"));

        LocalDate heute = LocalDate.now();
        List<Mitgliedsbeitrag> neueBeitraege = new ArrayList<>();

        List<Mitgliedsbeitrag> beitraege = beitragRepository.findByMitglied(mitglied);

        for (Mitgliedsbeitrag beitrag : beitraege) {
            if (beitrag.istWiederkehrend() && beitrag.getStatus() == BeitragsStatus.BEZAHLT) {
                LocalDate naechstesFaelligkeitsdatum = beitrag.berechneNaechstesFaelligkeitsdatum();

                if (!naechstesFaelligkeitsdatum.isAfter(heute)) {
                    boolean existiertBereits = existiertFolgeBeitrag(beitrag, naechstesFaelligkeitsdatum);

                    if (!existiertBereits) {
                        Mitgliedsbeitrag neuerBeitrag = erstelleFolgeBeitrag(beitrag, naechstesFaelligkeitsdatum);
                        neueBeitraege.add(neuerBeitrag);
                    }
                }
            }
        }

        return beitragRepository.saveAll(neueBeitraege);
    }

    private Mitgliedsbeitrag erstelleFolgeBeitrag(Mitgliedsbeitrag ursprung, LocalDate faelligkeitsdatum) {
        Mitgliedsbeitrag neuerBeitrag = new Mitgliedsbeitrag();
        neuerBeitrag.setMitglied(ursprung.getMitglied());
        neuerBeitrag.setBetrag(ursprung.getBetrag());
        neuerBeitrag.setTyp(ursprung.getTyp());
        neuerBeitrag.setFaelligkeitsdatum(faelligkeitsdatum);
        neuerBeitrag.setStatus(BeitragsStatus.OFFEN);

        if (ursprung.getTyp() == BeitragsTyp.MONATLICH) {
            String monat = getMonatsName(faelligkeitsdatum.getMonth().getValue());
            neuerBeitrag.setZeitraum(monat + " " + faelligkeitsdatum.getYear());
        }else {
            neuerBeitrag.setZeitraum("Jahr " + faelligkeitsdatum.getYear());
        }

        neuerBeitrag.setUrsprungsBeitrag(ursprung.getUrsprungsBeitrag() != null
                ? ursprung.getUrsprungsBeitrag()
                : ursprung);

        return neuerBeitrag;
    }

    private boolean existiertFolgeBeitrag(Mitgliedsbeitrag ursprung, LocalDate faelligkeitsdatum) {
        List<Mitgliedsbeitrag> alleBeitraege = beitragRepository.findByMitglied(ursprung.getMitglied());
        return alleBeitraege.stream()
                .anyMatch(b -> b.getFaelligkeitsdatum() != null
                        && b.getFaelligkeitsdatum().equals(faelligkeitsdatum)
                        && b.getTyp() == ursprung.getTyp());
    }

    public int zaehleGenerierteWiederkehrendeBeitraege(){
        LocalDate heute = LocalDate.now();
        int anzahl = 0;
        List<Mitgliedsbeitrag> bezahlteBeitraege = beitragRepository.findByStatus(BeitragsStatus.BEZAHLT);
        for (Mitgliedsbeitrag beitrag : bezahlteBeitraege) {
            if (beitrag.istWiederkehrend()) {
                LocalDate naechstesFaelligkeitsdatum = beitrag.berechneNaechstesFaelligkeitsdatum();
                if (!naechstesFaelligkeitsdatum.isAfter(heute)) {
                    boolean existiertBereits = existiertFolgeBeitrag(beitrag, naechstesFaelligkeitsdatum);
                    if (!existiertBereits) {
                        anzahl++;
                    }
                }
            }
        }
        return anzahl;
    }
    private String getMonatsName(int monatNummer){
        return switch (monatNummer) {
            case 1 -> "Januar";
            case 2 -> "Februar";
            case 3 -> "März";
            case 4 -> "April";
            case 5 -> "Mai";
            case 6 -> "Juni";
            case 7 -> "Juli";
            case 8 -> "August";
            case 9 -> "September";
            case 10 -> "Oktober";
            case 11 -> "November";
            case 12 -> "Dezember";
            default -> "Unbekannt";
        };
    }
}