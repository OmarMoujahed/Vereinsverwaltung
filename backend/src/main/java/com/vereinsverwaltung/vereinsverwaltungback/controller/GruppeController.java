package com.vereinsverwaltung.vereinsverwaltungback.controller;

import com.vereinsverwaltung.vereinsverwaltungback.entity.Gruppe;
import com.vereinsverwaltung.vereinsverwaltungback.service.GruppeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/gruppen")
public class GruppeController {

    private final GruppeService gruppeService;

    public GruppeController(GruppeService gruppeService) {
        this.gruppeService = gruppeService;
    }

    @GetMapping
    public List<Gruppe> alleGruppen() {
        return gruppeService.alleGruppen();
    }

    @GetMapping("/verein/{vereinId}")
    public List<Gruppe> gruppenVonVerein(@PathVariable Long vereinId) {
        return gruppeService.gruppenVonVerein(vereinId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Gruppe> gruppeHolen(@PathVariable Long id) {
        return gruppeService.gruppeFinden(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Gruppe gruppeAnlegen(@RequestBody Gruppe gruppe) {
        return gruppeService.gruppeAnlegen(gruppe);
    }

    @PutMapping("/{id}")
    public Gruppe gruppeBearbeiten(@PathVariable Long id, @RequestBody Gruppe gruppe) {
        return gruppeService.gruppeBearbeiten(id, gruppe);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> gruppeLoeschen(@PathVariable Long id) {
        gruppeService.gruppeLoeschen(id);
        return ResponseEntity.ok().build();
    }
}