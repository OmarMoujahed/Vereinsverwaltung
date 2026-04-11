package com.vereinsverwaltung.vereinsverwaltungback.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
public class Veranstaltung {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private LocalDate datum;
    private String ort;
    private String beschreibung;

    @Enumerated(EnumType.STRING)
    private VeranstaltungStatus status;

    @Column(updatable = false)
    private LocalDateTime erstelltAm;

    @PrePersist
    protected void onCreate() {
        erstelltAm = LocalDateTime.now();
    }

    public LocalDateTime getErstelltAm() {
        return erstelltAm;
    }

    // Beziehungen
    @ManyToOne
    @JoinColumn(name = "verein_id")
    private Verein verein;

    @ManyToOne
    @JoinColumn(name = "gruppe_id")
    private Gruppe gruppe;


    //Getter und Setter
    public VeranstaltungStatus getStatus() {
        return status;
    }

    public void setStatus(VeranstaltungStatus status) {
        this.status = status;
    }

    public void setErstelltAm(LocalDateTime erstelltAm) {
        this.erstelltAm = erstelltAm;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDatum() {
        return datum;
    }

    public void setDatum(LocalDate datum) {
        this.datum = datum;
    }

    public String getOrt() {
        return ort;
    }

    public void setOrt(String ort) {
        this.ort = ort;
    }

    public Verein getVerein() {
        return verein;
    }

    public void setVerein(Verein verein) {
        this.verein = verein;
    }

    public Gruppe getGruppe() {
        return gruppe;
    }

    public void setGruppe(Gruppe gruppe) {
        this.gruppe = gruppe;
    }

    public String getBeschreibung() {
        return beschreibung;
    }

    public void setBeschreibung(String beschreibung) {
        this.beschreibung = beschreibung;
    }
}