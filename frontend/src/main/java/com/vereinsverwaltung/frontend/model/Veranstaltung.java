package com.vereinsverwaltung.frontend.model;

import java.time.LocalDate;

public class Veranstaltung {

    private Long id;
    private String name;
    private LocalDate datum;
    private String ort;
    private String beschreibung;
    private Verein verein;
    private Gruppe gruppe;

    // Getter
    public Long getId() { return id; }
    public String getName() { return name; }
    public LocalDate getDatum() { return datum; }
    public String getOrt() { return ort; }
    public String getBeschreibung() { return beschreibung; }
    public Verein getVerein() { return verein; }
    public Gruppe getGruppe() { return gruppe; }
    // Setter
    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setDatum(LocalDate datum) { this.datum = datum; }
    public void setOrt(String ort) { this.ort = ort; }
    public void setBeschreibung(String beschreibung) { this.beschreibung = beschreibung; }
    public void setVerein(Verein verein) { this.verein = verein; }
    public void setGruppe(Gruppe gruppe) { this.gruppe = gruppe; }
}
