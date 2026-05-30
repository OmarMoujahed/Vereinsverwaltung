package com.vereinsverwaltung.vereinsverwaltungback.domain;

import java.time.LocalDate;

public class EinmaligIntervallStrategie implements BeitragsIntervallStrategie {

    @Override
    public LocalDate berechneNaechstesFaelligkeitsdatum(LocalDate aktuell) {
        return aktuell;
    }
}