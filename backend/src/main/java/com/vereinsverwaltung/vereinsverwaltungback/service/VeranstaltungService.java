package com.vereinsverwaltung.vereinsverwaltungback.service;

import com.vereinsverwaltung.vereinsverwaltungback.entity.Gruppe;
import com.vereinsverwaltung.vereinsverwaltungback.entity.Veranstaltung;
import com.vereinsverwaltung.vereinsverwaltungback.entity.Verein;
import com.vereinsverwaltung.vereinsverwaltungback.repository.GruppeRepository;
import com.vereinsverwaltung.vereinsverwaltungback.repository.VeranstaltungRepository;
import com.vereinsverwaltung.vereinsverwaltungback.repository.VereinRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VeranstaltungService {
    private final VeranstaltungRepository veranstaltungRepository;
    private final VereinRepository vereinRepository;
    private final GruppeRepository gruppeRepository;

    public VeranstaltungService(VeranstaltungRepository veranstaltungRepository,
                                VereinRepository vereinRepository,
                                GruppeRepository gruppeRepository) {
        this.veranstaltungRepository = veranstaltungRepository;
        this.vereinRepository = vereinRepository;
        this.gruppeRepository = gruppeRepository;
    }

    public List<Veranstaltung> alleVeranstaltungen() {
        return veranstaltungRepository.findAll();
    }

    public List<Veranstaltung> veranstaltungenVonVerein(Long vereinId) {
        Verein verein = vereinRepository.findById(vereinId)
                .orElseThrow(() -> new IllegalArgumentException("Verein nicht gefunden!"));
        return veranstaltungRepository.findByVerein(verein);
    }


    public List<Veranstaltung> veranstaltungenVonGruppe(Long gruppeId) {
        Gruppe gruppe = gruppeRepository.findById(gruppeId)
                .orElseThrow(() -> new IllegalArgumentException("Gruppe nicht gefunden!"));
        return veranstaltungRepository.findByGruppe(gruppe);
    }


    public Optional<Veranstaltung> veranstaltungFinden(Long id) {
        return veranstaltungRepository.findById(id);
    }

    public Veranstaltung veranstaltungAnlegen(Veranstaltung veranstaltung) {
        if (veranstaltung.getVerein() == null) {
            throw new IllegalArgumentException("Veranstaltung muss einem Verein zugeordnet sein!");
        }

        Verein verein = vereinRepository.findById(veranstaltung.getVerein().getVerein_id())
                .orElseThrow(() -> new IllegalArgumentException("Verein nicht gefunden!"));
        veranstaltung.setVerein(verein);

        if (veranstaltung.getGruppe() != null) {
            Gruppe gruppe = gruppeRepository.findById(veranstaltung.getGruppe().getGruppe_id())
                    .orElseThrow(() -> new IllegalArgumentException("Gruppe nicht gefunden!"));

            if (!gruppe.getVerein().getVerein_id().equals(verein.getVerein_id())) {
                throw new IllegalArgumentException("Die Gruppe gehört nicht zum selben Verein wie die Veranstaltung!");
            }

            veranstaltung.setGruppe(gruppe);
        }

        return veranstaltungRepository.save(veranstaltung);
    }

    public Veranstaltung veranstaltungBearbeiten(Long id, Veranstaltung neueDaten) {
        Veranstaltung veranstaltung = veranstaltungRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Veranstaltung nicht gefunden!"));

        if (neueDaten.getGruppe() == null) {
            veranstaltung.setGruppe(null);
        } else {
            Gruppe neueGruppe = gruppeRepository.findById(neueDaten.getGruppe().getGruppe_id())
                    .orElseThrow(() -> new IllegalArgumentException("Gruppe nicht gefunden!"));

            if (!neueGruppe.getVerein().getVerein_id().equals(veranstaltung.getVerein().getVerein_id())) {
                throw new IllegalArgumentException("Die Gruppe gehört nicht zum selben Verein!");
            }

            veranstaltung.setGruppe(neueGruppe);
        }

        veranstaltung.setName(neueDaten.getName());
        veranstaltung.setBeschreibung(neueDaten.getBeschreibung());
        veranstaltung.setDatum(neueDaten.getDatum());
        veranstaltung.setOrt(neueDaten.getOrt());

        return veranstaltungRepository.save(veranstaltung);
    }

    public void veranstaltungLoeschen(Long id) {
        if (!veranstaltungRepository.existsById(id)) {
            throw new IllegalArgumentException("Veranstaltung nicht gefunden!");
        }
        veranstaltungRepository.deleteById(id);
    }
}
