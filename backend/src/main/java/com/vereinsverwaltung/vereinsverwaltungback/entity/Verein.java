package com.vereinsverwaltung.vereinsverwaltungback.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Verein {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long verein_id;

    @Column(unique = true, nullable = false)
    private String name;

    private String beschreibung;
    private String email;
    private String telefon;
    private String adresse;
    @Column(updatable = false)
    private LocalDateTime erstelltAm;

    @PrePersist
    protected void onCreate() {
        erstelltAm = LocalDateTime.now();
    }

    public LocalDateTime getErstelltAm() {
        return erstelltAm;
    }

    //Konstruktor
    public Verein() {

    }

    //Getter und Setter

    public Long getVerein_id() {
        return verein_id;
    }

    public String getName() {
        return name;
    }

    public String getBeschreibung() {
        return beschreibung;
    }

    public String getEmail() {
        return email;
    }

    public String getTelefon() {
        return telefon;
    }

    public String getAdresse() {
        return adresse;
    }
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
}