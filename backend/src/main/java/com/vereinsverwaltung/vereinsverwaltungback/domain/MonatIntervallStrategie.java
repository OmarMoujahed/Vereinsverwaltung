package com.vereinsverwaltung.vereinsverwaltungback.domain;

import java.time.LocalDate;

public class MonatIntervallStrategie implements BeitragsIntervallStrategie {

    @Override
    public LocalDate berechneNaechstesFaelligkeitsdatum(LocalDate aktuell) {
        return aktuell.plusMonths(1);
    }
}