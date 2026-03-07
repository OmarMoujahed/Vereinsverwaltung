package com.vereinsverwaltung.vereinsverwaltungback.controller;

import com.vereinsverwaltung.vereinsverwaltungback.entity.Verein;
import com.vereinsverwaltung.vereinsverwaltungback.service.VereinService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vereine")
public class VereinController {

    private final VereinService vereinService;

    public VereinController(VereinService vereinService) {
        this.vereinService = vereinService;
    }

    @GetMapping
    public List<Verein> alleVereine() {
        return vereinService.alleVereine();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Verein> vereinHolen(@PathVariable Long id) {
        return vereinService.vereinFinden(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Verein vereinAnlegen(@RequestBody Verein verein) {
        return vereinService.vereinAnlegen(verein);
    }

    @PutMapping("/{id}")
    public Verein vereinBearbeiten(@PathVariable Long id, @RequestBody Verein verein) {
        return vereinService.vereinBearbeiten(id, verein);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> vereinLoeschen(@PathVariable Long id) {
        vereinService.vereinLoeschen(id);
        return ResponseEntity.ok().build();
    }
}