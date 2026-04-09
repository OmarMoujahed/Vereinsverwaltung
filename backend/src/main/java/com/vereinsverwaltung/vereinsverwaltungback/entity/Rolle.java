package com.vereinsverwaltung.vereinsverwaltungback.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Rolle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rolle_id;

    private String name;

    private String beschreibung;

    private boolean istGlobal;

    @Column(updatable = false)
    private LocalDateTime erstelltAm;

    @PrePersist
    protected void onCreate() {
        erstelltAm = LocalDateTime.now();
    }

    public LocalDateTime getErstelltAm() {
        return erstelltAm;
    }

    // Beziehung zum Verein
    @ManyToOne
    @JoinColumn(name = "verein_id")
    private Verein verein;

    public Long getRolle_id() {

        return rolle_id;
    }

    public void setRolle_id(Long rolle_id) {

        this.rolle_id = rolle_id;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Verein getVerein() {

        return verein;
    }

    public void setVerein(Verein verein) {
        this.verein = verein;
    }

    public String getBeschreibung() {
        return beschreibung;
    }

    public void setBeschreibung(String beschreibung) {
        this.beschreibung = beschreibung;
    }

    public boolean isIstGlobal() {
        return istGlobal;
    }

    public void setIstGlobal(boolean istGlobal) {
        this.istGlobal = istGlobal;
    }
}
