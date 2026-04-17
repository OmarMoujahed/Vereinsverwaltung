package com.vereinsverwaltung.vereinsverwaltungback.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Mitglied {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long mitglied_id;
    private String vorname;
    private String nachname;
    private String email;
    private String telefon;
    private LocalDate geburtsdatum;
    private LocalDate eintrittsdatum;
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
    // Beziehungen
    @ManyToOne
    @JoinColumn(name = "verein_id")
    private Verein verein;

    @ManyToOne
    @JoinColumn(name = "rolle_id")
    private Rolle rolle;

    public void setMitglied_id(Long mitglied_id) {
        this.mitglied_id = mitglied_id;
    }

    public void setVorname(String vorname) {
        this.vorname = vorname;
    }

    public void setNachname(String nachname) {
        this.nachname = nachname;
    }

    public void setErstelltAm(LocalDateTime erstelltAm) {
        this.erstelltAm = erstelltAm;
    }

    @ManyToMany
    @JoinTable(
            name = "mitglied_gruppe",
            joinColumns = @JoinColumn(name = "mitglied_id"),
            inverseJoinColumns = @JoinColumn(name = "gruppe_id")
    )

    private List<Gruppe> gruppen = new ArrayList<>();


    //Getter und Setter
    public Long getMitglied_id() {
        return mitglied_id;
    }

    public String getVorname() {
        return vorname;
    }

    public String getNachname() {
        return nachname;
    }

    public Verein getVerein() {
        return verein;
    }

    public void setVerein(Verein verein) {
        this.verein = verein;
    }

    public Rolle getRolle() {
        return rolle;
    }

    public void setRolle(Rolle rolle) {
        this.rolle = rolle;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefon() {
        return telefon;
    }

    public void setTelefon(String telefon) {
        this.telefon = telefon;
    }

    public LocalDate getGeburtsdatum() {
        return geburtsdatum;
    }

    public void setGeburtsdatum(LocalDate geburtsdatum) {
        this.geburtsdatum = geburtsdatum;
    }

    public LocalDate getEintrittsdatum() {
        return eintrittsdatum;
    }

    public void setEintrittsdatum(LocalDate eintrittsdatum) {
        this.eintrittsdatum = eintrittsdatum;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public List<Gruppe> getGruppen() {
        return gruppen;
    }

    public void setGruppen(List<Gruppe> gruppen) {
        this.gruppen = gruppen;
    }
}
