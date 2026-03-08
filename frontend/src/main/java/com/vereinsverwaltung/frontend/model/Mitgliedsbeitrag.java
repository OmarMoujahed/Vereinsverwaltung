package com.vereinsverwaltung.frontend.model;

import java.time.LocalDate;

public class Mitgliedsbeitrag {

    private Long id;
    private Double betrag;
    private String zeitraum;
    private LocalDate faelligkeitsdatum;
    private BeitragsStatus status;
    private Mitglied mitglied;

    // Getter
    public Long getId() { return id; }
    public Double getBetrag() { return betrag; }
    public String getZeitraum() { return zeitraum; }
    public LocalDate getFaelligkeitsdatum() { return faelligkeitsdatum; }
    public BeitragsStatus getStatus() { return status; }
    public Mitglied getMitglied() { return mitglied; }
    // Setter
    public void setId(Long id) { this.id = id; }
    public void setBetrag(Double betrag) { this.betrag = betrag; }
    public void setZeitraum(String zeitraum) { this.zeitraum = zeitraum; }
    public void setFaelligkeitsdatum(LocalDate faelligkeitsdatum) { this.faelligkeitsdatum = faelligkeitsdatum; }
    public void setStatus(BeitragsStatus status) { this.status = status; }
    public void setMitglied(Mitglied mitglied) { this.mitglied = mitglied; }
}