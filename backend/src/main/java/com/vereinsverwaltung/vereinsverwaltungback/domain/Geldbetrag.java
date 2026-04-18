package com.vereinsverwaltung.vereinsverwaltungback.domain;

import jakarta.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public final class Geldbetrag {

    private final double betrag;
    private final String waehrung;

    // JPA braucht einen leeren Konstruktor
    protected Geldbetrag() {
        this.betrag = 0;
        this.waehrung = "EUR";
    }

    public Geldbetrag(double betrag, String waehrung) {
        if (betrag < 0) {
            throw new IllegalArgumentException("Betrag darf nicht negativ sein");
        }
        if (waehrung == null || waehrung.isBlank()) {
            throw new IllegalArgumentException("Waehrung darf nicht leer sein");
        }
        this.betrag = betrag;
        this.waehrung = waehrung;
    }

    public double getBetrag() {
        return betrag;
    }

    public String getWaehrung() {
        return waehrung;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Geldbetrag)) return false;
        Geldbetrag other = (Geldbetrag) o;
        return Double.compare(other.betrag, betrag) == 0
                && Objects.equals(waehrung, other.waehrung);
    }

    @Override
    public int hashCode() {
        return Objects.hash(betrag, waehrung);
    }

    @Override
    public String toString() {
        return betrag + " " + waehrung;
    }
}