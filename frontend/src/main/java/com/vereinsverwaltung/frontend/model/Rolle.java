package com.vereinsverwaltung.frontend.model;

public class Rolle {

    private Long rolle_id;
    private String name;
    private String beschreibung;
    private boolean istGlobal;
    private Verein verein;


    public Long getRolle_id() { return rolle_id; }
    public String getName() { return name; }
    public String getBeschreibung() { return beschreibung; }
    public boolean isIstGlobal() { return istGlobal; }
    public Verein getVerein() { return verein; }
    public void setRolle_id(Long rolle_id) { this.rolle_id = rolle_id; }
    public void setName(String name) { this.name = name; }
    public void setBeschreibung(String beschreibung) { this.beschreibung = beschreibung; }
    public void setIstGlobal(boolean istGlobal) { this.istGlobal = istGlobal; }
    public void setVerein(Verein verein) { this.verein = verein; }
}