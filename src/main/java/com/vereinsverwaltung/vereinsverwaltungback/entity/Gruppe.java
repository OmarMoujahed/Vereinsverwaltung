package com.vereinsverwaltung.vereinsverwaltungback.entity;


import jakarta.persistence.*;

@Entity
public class Gruppe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long gruppe_id;

    @Column(nullable = false)
    private String name;

    private String beschreibung;

    // Beziehung zum Verein
    @ManyToOne
    @JoinColumn(name = "verein_id")
    private Verein verein;


    //Getter und Setter
    public Long getGruppe_id() {
        return gruppe_id;
    }

    public void setGruppe_id(Long gruppe_id) {
        this.gruppe_id = gruppe_id;
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
}