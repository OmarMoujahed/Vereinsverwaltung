package com.vereinsverwaltung.vereinsverwaltungback.controller;


import com.vereinsverwaltung.vereinsverwaltungback.entity.Veranstaltung;
import com.vereinsverwaltung.vereinsverwaltungback.service.VeranstaltungService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/veranstaltungen")
public class VeranstaltungController {

    private final VeranstaltungService veranstaltungService;

    public VeranstaltungController(VeranstaltungService veranstaltungService) {
        this.veranstaltungService = veranstaltungService;
    }

    @GetMapping
    public List<Veranstaltung> alleVeranstaltungen() {
        return veranstaltungService.alleVeranstaltungen();
    }

    @GetMapping("/verein/{vereinId}")
    public List<Veranstaltung> veranstaltungenVonVerein(@PathVariable Long vereinId) {
        return veranstaltungService.veranstaltungenVonVerein(vereinId);
    }

    @GetMapping("/gruppe/{gruppeId}")
    public List<Veranstaltung> veranstaltungenVonGruppe(@PathVariable Long gruppeId) {
        return veranstaltungService.veranstaltungenVonGruppe(gruppeId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Veranstaltung> veranstaltungHolen(@PathVariable Long id) {
        return veranstaltungService.veranstaltungFinden(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Veranstaltung veranstaltungAnlegen(@RequestBody Veranstaltung veranstaltung) {
        return veranstaltungService.veranstaltungAnlegen(veranstaltung);
    }

    @PutMapping("/{id}")
    public Veranstaltung veranstaltungBearbeiten(@PathVariable Long id, @RequestBody Veranstaltung veranstaltung) {
        return veranstaltungService.veranstaltungBearbeiten(id, veranstaltung);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> veranstaltungLoeschen(@PathVariable Long id) {
        veranstaltungService.veranstaltungLoeschen(id);
        return ResponseEntity.ok().build();
    }
}
