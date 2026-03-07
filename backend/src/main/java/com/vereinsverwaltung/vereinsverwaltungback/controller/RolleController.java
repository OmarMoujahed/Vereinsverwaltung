package com.vereinsverwaltung.vereinsverwaltungback.controller;

import com.vereinsverwaltung.vereinsverwaltungback.entity.Rolle;
import com.vereinsverwaltung.vereinsverwaltungback.service.RolleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rollen")
public class RolleController {

    private final RolleService rolleService;

    public RolleController(RolleService rolleService) {
        this.rolleService = rolleService;
    }

    @GetMapping
    public List<Rolle> alleRollen() {
        return rolleService.alleRollen();
    }

    @GetMapping("/verein/{vereinId}")
    public List<Rolle> rollenVonVerein(@PathVariable Long vereinId) {
        return rolleService.rollenVonVerein(vereinId);
    }

    @GetMapping("/global")
    public List<Rolle> globaleRollen() {
        return rolleService.globaleRollen();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Rolle> rolleHolen(@PathVariable Long id) {
        return rolleService.rolleFinden(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Rolle rolleAnlegen(@RequestBody Rolle rolle) {
        return rolleService.rolleAnlegen(rolle);
    }

    @PutMapping("/{id}")
    public Rolle rolleBearbeiten(@PathVariable Long id, @RequestBody Rolle rolle) {
        return rolleService.rolleBearbeiten(id, rolle);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> rolleLoeschen(@PathVariable Long id) {
        rolleService.rolleLoeschen(id);
        return ResponseEntity.ok().build();
    }
}