package com.vereinsverwaltung.vereinsverwaltungback.domain;

import java.time.LocalDate;

public class JahrIntervallStrategie implements BeitragsIntervallStrategie {

    @Override
    public LocalDate berechneNaechstesFaelligkeitsdatum(LocalDate aktuell) {
        return aktuell.plusYears(1);
    }
}