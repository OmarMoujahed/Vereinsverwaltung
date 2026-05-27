package com.vereinsverwaltung.vereinsverwaltungback.controller;

import com.vereinsverwaltung.vereinsverwaltungback.domain.BeitragsGenerierungsService;
import com.vereinsverwaltung.vereinsverwaltungback.entity.BeitragsStatus;
import com.vereinsverwaltung.vereinsverwaltungback.entity.Mitgliedsbeitrag;
import com.vereinsverwaltung.vereinsverwaltungback.service.MitgliedsbeitragService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/beitraege")
public class MitgliedsbeitragController {

    private final MitgliedsbeitragService beitragService;
    private final BeitragsGenerierungsService generierungsService;

    public MitgliedsbeitragController(MitgliedsbeitragService beitragService, BeitragsGenerierungsService generierungsService) {
        this.beitragService = beitragService;
        this.generierungsService = generierungsService;
    }

    @GetMapping
    public ResponseEntity<List<Mitgliedsbeitrag>> alleBeitraege() {
        return ResponseEntity.ok(beitragService.alleBeitraege());
    }

    @PostMapping("/wiederkehrende-generieren")
    public ResponseEntity<List<Mitgliedsbeitrag>> wiederkehrendeBeitraegeGenerieren() {
        List<Mitgliedsbeitrag> neueBeitraege = generierungsService.generiereWiederkehrendeBeitraege();
        return ResponseEntity.ok(neueBeitraege);
    }

    @PostMapping("/mitglied/{mitgliedId}/wiederkehrende-generieren")
    public ResponseEntity<List<Mitgliedsbeitrag>> wiederkehrendeBeitraegeFuerMitgliedGenerieren(@PathVariable Long mitgliedId) {
        List<Mitgliedsbeitrag> neueBeitraege = generierungsService.generiereWiederkehrendeBeitraegeFuerMitglied(mitgliedId);
        return ResponseEntity.ok(neueBeitraege);
    }

    @GetMapping("/wiederkehrende-zaehlen")
    public ResponseEntity<Integer> wiederkehrendeBeitraegeZaehlen() {
        int anzahl = generierungsService.zaehleGenerierteWiederkehrendeBeitraege();
        return ResponseEntity.ok(anzahl);
    }

    @GetMapping("/mitglied/{mitgliedId}")
    public ResponseEntity<List<Mitgliedsbeitrag>> beitraegeVonMitglied(@PathVariable Long mitgliedId) {
        return ResponseEntity.ok(beitragService.beitraegeVonMitglied(mitgliedId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Mitgliedsbeitrag>> beitraegeNachStatus(@PathVariable BeitragsStatus status) {
        return ResponseEntity.ok(beitragService.beitraegeNachStatus(status));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Mitgliedsbeitrag> beitragFinden(@PathVariable Long id) {
        return beitragService.beitragFinden(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Mitgliedsbeitrag> beitragAnlegen(@RequestBody Mitgliedsbeitrag beitrag) {
        return ResponseEntity.ok(beitragService.beitragAnlegen(beitrag));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Mitgliedsbeitrag> beitragBearbeiten(@PathVariable Long id, @RequestBody Mitgliedsbeitrag neueDaten) {
        return ResponseEntity.ok(beitragService.beitragBearbeiten(id, neueDaten));
    }

    @PutMapping("/{id}/bezahlt")
    public ResponseEntity<Mitgliedsbeitrag> beitragAlsBezahltMarkieren(@PathVariable Long id) {
        return ResponseEntity.ok(beitragService.beitragAlsBezahltMarkieren(id));
    }

    @PutMapping("/ueberfaellige-aktualisieren")
    public ResponseEntity<Void> ueberfaelligeBeitraegeAktualisieren() {
        beitragService.ueberfaelligeBeitraegeAktualisieren();
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> beitragLoeschen(@PathVariable Long id) {
        beitragService.beitragLoeschen(id);
        return ResponseEntity.ok().build();
    }
}