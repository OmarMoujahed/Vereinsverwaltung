package com.vereinsverwaltung.frontend.model;

public class Geldbetrag {
    private double betrag;
    private String waehrung;

    public Geldbetrag(double betrag, String waehrung) {
        this.betrag = betrag;
        this.waehrung = waehrung;
    }

    public double getBetrag() { return betrag; }
    public String getWaehrung() { return waehrung; }
    public void setBetrag(double betrag) { this.betrag = betrag; }
    public void setWaehrung(String waehrung) { this.waehrung = waehrung; }
}