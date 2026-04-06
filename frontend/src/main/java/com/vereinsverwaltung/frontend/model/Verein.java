package com.vereinsverwaltung.frontend.model;

import com.google.gson.annotations.SerializedName;

public class Verein {
    private Long verein_id;
    private String name;
    private String beschreibung;
    private String email;
    private String telefon;
    private String adresse;
    @SerializedName("erstelltAm")
    private String erstelltAm;

    public String getErstelltAm() { return erstelltAm; }

    public void setVerein_id(Long verein_id) {
        this.verein_id = verein_id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBeschreibung(String beschreibung) {
        this.beschreibung = beschreibung;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setTelefon(String telefon) {
        this.telefon = telefon;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    // Getter
    public Long getVerein_id() { return verein_id; }
    public String getName() { return name; }
    public String getBeschreibung() { return beschreibung; }
    public String getEmail() { return email; }
    public String getTelefon() { return telefon; }
    public String getAdresse() { return adresse; }
}