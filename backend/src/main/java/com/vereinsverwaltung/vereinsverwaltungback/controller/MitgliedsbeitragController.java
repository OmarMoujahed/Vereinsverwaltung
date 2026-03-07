package com.vereinsverwaltung.vereinsverwaltungback.controller;

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
    public MitgliedsbeitragController(MitgliedsbeitragService beitragService) {
        this.beitragService = beitragService;
    }

    @GetMapping
    public List<Mitgliedsbeitrag> alleBeitraege() {
        return beitragService.alleBeitraege();
    }

    @GetMapping("/mitglied/{mitgliedId}")
    public List<Mitgliedsbeitrag> beitraegeVonMitglied(@PathVariable Long mitgliedId) {
        return beitragService.beitraegeVonMitglied(mitgliedId);
    }

    @GetMapping("/status/{status}")
    public List<Mitgliedsbeitrag> beitraegeNachStatus(@PathVariable BeitragsStatus status) {
        return beitragService.beitraegeNachStatus(status);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Mitgliedsbeitrag> beitragHolen(@PathVariable Long id) {
        return beitragService.beitragFinden(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mitgliedsbeitrag beitragAnlegen(@RequestBody Mitgliedsbeitrag beitrag) {
        return beitragService.beitragAnlegen(beitrag);
    }

    @PutMapping("/{id}")
    public Mitgliedsbeitrag beitragBearbeiten(@PathVariable Long id, @RequestBody Mitgliedsbeitrag beitrag) {
        return beitragService.beitragBearbeiten(id, beitrag);
    }

    @PatchMapping("/{id}/bezahlt")
    public Mitgliedsbeitrag beitragAlsBezahltMarkieren(@PathVariable Long id) {
        return beitragService.beitragAlsBezahltMarkieren(id);
    }

    @PostMapping("/ueberfaellige-aktualisieren")
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
