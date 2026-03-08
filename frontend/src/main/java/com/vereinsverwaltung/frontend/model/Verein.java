package com.vereinsverwaltung.frontend.model;

public class Verein {
    private Long verein_id;
    private String name;
    private String beschreibung;
    private String email;
    private String telefon;
    private String adresse;

    // Getter
    public Long getVerein_id() { return verein_id; }
    public String getName() { return name; }
    public String getBeschreibung() { return beschreibung; }
    public String getEmail() { return email; }
    public String getTelefon() { return telefon; }
    public String getAdresse() { return adresse; }
}