package com.vereinsverwaltung.frontend.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Mitglied {

    private Long mitglied_id;
    private String vorname;
    private String nachname;
    private String email;
    private String telefon;
    private LocalDate geburtsdatum;
    private LocalDate eintrittsdatum;
    private String adresse;
    private Verein verein;
    private Rolle rolle;
    private List<Gruppe> gruppen = new ArrayList<>();

    // Getter und Setter
    public Long getMitglied_id() { return mitglied_id; }
    public String getVorname() { return vorname; }
    public String getNachname() { return nachname; }
    public String getEmail() { return email; }
    public String getTelefon() { return telefon; }
    public LocalDate getGeburtsdatum() { return geburtsdatum; }
    public LocalDate getEintrittsdatum() { return eintrittsdatum; }
    public String getAdresse() { return adresse; }
    public Verein getVerein() { return verein; }
    public Rolle getRolle() { return rolle; }
    public List<Gruppe> getGruppen() { return gruppen; }

    public void setMitglied_id(Long mitglied_id) { this.mitglied_id = mitglied_id; }
    public void setVorname(String vorname) { this.vorname = vorname; }
    public void setNachname(String nachname) { this.nachname = nachname; }
    public void setEmail(String email) { this.email = email; }
    public void setTelefon(String telefon) { this.telefon = telefon; }
    public void setGeburtsdatum(LocalDate geburtsdatum) { this.geburtsdatum = geburtsdatum; }
    public void setEintrittsdatum(LocalDate eintrittsdatum) { this.eintrittsdatum = eintrittsdatum; }
    public void setAdresse(String adresse) { this.adresse = adresse; }
    public void setVerein(Verein verein) { this.verein = verein; }
    public void setRolle(Rolle rolle) { this.rolle = rolle; }
    public void setGruppen(List<Gruppe> gruppen) { this.gruppen = gruppen; }
}