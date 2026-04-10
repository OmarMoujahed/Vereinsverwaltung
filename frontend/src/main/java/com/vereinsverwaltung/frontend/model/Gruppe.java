package com.vereinsverwaltung.frontend.model;

import com.google.gson.annotations.SerializedName;

public class Gruppe {

    private Long gruppe_id;
    private String name;
    private String beschreibung;
    private Verein verein;
    @SerializedName("erstelltAm")
    private String erstelltAm;

    public String getErstelltAm() { return erstelltAm; }

    // Getter und Setter
    public Long getGruppe_id() { return gruppe_id; }
    public String getName() { return name; }
    public String getBeschreibung() { return beschreibung; }
    public Verein getVerein() { return verein; }
    public void setGruppe_id(Long gruppe_id) { this.gruppe_id = gruppe_id; }
    public void setName(String name) { this.name = name; }
    public void setBeschreibung(String beschreibung) { this.beschreibung = beschreibung; }
    public void setVerein(Verein verein) { this.verein = verein; }
}