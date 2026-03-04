package com.vereinsverwaltung.vereinsverwaltungback.entity;

import jakarta.persistence.*;

@Entity
public class Verein {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long verein_id;

    @Column(unique = true, nullable = false)
    private String name;

    private String beschreibung;
    private String email;
    private String telefon;
    private String adresse;

    //Konstruktoren
    public Verein(String newname){

        name = newname;
    }

    public Verein() {

    }

    //Getter und Setter
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