package com.vereinsverwaltung.vereinsverwaltungback.domain;

import java.time.LocalDate;

public interface BeitragsIntervallStrategie {
    LocalDate berechneNaechstesFaelligkeitsdatum(LocalDate aktuell);
}