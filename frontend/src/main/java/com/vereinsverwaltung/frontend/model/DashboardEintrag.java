package com.vereinsverwaltung.frontend.model;

public class DashboardEintrag {

    private String titel;
    private String untertitel;
    private String kategorie;
    private String erstelltAm;

    public DashboardEintrag(String titel, String untertitel, String kategorie, String erstelltAm) {
        this.titel = titel;
        this.untertitel = untertitel;
        this.kategorie = kategorie;
        this.erstelltAm = erstelltAm;
    }

    public String getTitel() { return titel; }
    public String getUntertitel() { return untertitel; }
    public String getKategorie() { return kategorie; }
    public String getErstelltAm() { return erstelltAm; }
}