package com.vereinsverwaltung.vereinsverwaltungback.controller;

import com.vereinsverwaltung.vereinsverwaltungback.domain.BeitragsPruefService;
import com.vereinsverwaltung.vereinsverwaltungback.entity.Mitglied;
import com.vereinsverwaltung.vereinsverwaltungback.service.MitgliedService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mitglieder")
public class MitgliedController {

    private final MitgliedService mitgliedService;
    private final BeitragsPruefService beitragsPruefService;

    public MitgliedController(MitgliedService mitgliedService, BeitragsPruefService beitragsPruefService) {
        this.mitgliedService = mitgliedService;
        this.beitragsPruefService = beitragsPruefService;
    }

    @GetMapping
    public List<Mitglied> alleMitglieder() {
        return mitgliedService.alleMitglieder();
    }

    @GetMapping("/verein/{vereinId}")
    public List<Mitglied> mitgliederVonVerein(@PathVariable Long vereinId) {
        return mitgliedService.mitgliederVonVerein(vereinId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Mitglied> mitgliedHolen(@PathVariable Long id) {
        return mitgliedService.mitgliedFinden(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/hat-offene-beitraege")
    public ResponseEntity<Boolean> hatOffeneBeitraege(@PathVariable Long id) {
        return mitgliedService.mitgliedFinden(id)
                .map(m -> ResponseEntity.ok(beitragsPruefService.hatOffeneBeitraege(m)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mitglied mitgliedAnlegen(@RequestBody Mitglied mitglied) {
        return mitgliedService.mitgliedAnlegen(mitglied);
    }

    @PutMapping("/{id}")
    public Mitglied mitgliedBearbeiten(@PathVariable Long id, @RequestBody Mitglied mitglied) {
        return mitgliedService.mitgliedBearbeiten(id, mitglied);
    }

    @PostMapping("/{mitgliedId}/gruppen/{gruppeId}")
    public Mitglied mitgliedZuGruppeHinzufuegen(@PathVariable Long mitgliedId,
                                                @PathVariable Long gruppeId) {
        return mitgliedService.mitgliedZuGruppeHinzufuegen(mitgliedId, gruppeId);
    }

    @DeleteMapping("/{mitgliedId}/gruppen/{gruppeId}")
    public Mitglied mitgliedAusGruppeEntfernen(@PathVariable Long mitgliedId,
                                               @PathVariable Long gruppeId) {
        return mitgliedService.mitgliedAusGruppeEntfernen(mitgliedId, gruppeId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> mitgliedLoeschen(@PathVariable Long id) {
        mitgliedService.mitgliedLoeschen(id);
        return ResponseEntity.ok().build();
    }
}