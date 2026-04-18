package com.vereinsverwaltung.vereinsverwaltungback.entity;

import com.vereinsverwaltung.vereinsverwaltungback.domain.Geldbetrag;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class Mitgliedsbeitrag {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Embedded
    private Geldbetrag betrag;
    private String zeitraum;
    private LocalDate faelligkeitsdatum;

    @Enumerated(EnumType.STRING)
    private BeitragsStatus status;


    // Beziehung zum Mitglied
    @ManyToOne
    @JoinColumn(name = "mitglied_id")
    private Mitglied mitglied;

    //Konstruktor
    public Mitgliedsbeitrag(){
    }

    //Getter und Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Geldbetrag getBetrag() {
        return betrag;
    }

    public void setBetrag(Geldbetrag betrag) {
        this.betrag = betrag;
    }

    public String getZeitraum() {
        return zeitraum;
    }

    public void setZeitraum(String zeitraum) {
        this.zeitraum = zeitraum;
    }

    public LocalDate getFaelligkeitsdatum() {
        return faelligkeitsdatum;
    }

    public void setFaelligkeitsdatum(LocalDate faelligkeitsdatum) {
        this.faelligkeitsdatum = faelligkeitsdatum;
    }

    public Mitglied getMitglied() {
        return mitglied;
    }

    public void setMitglied(Mitglied mitglied) {
        this.mitglied = mitglied;
    }

    public BeitragsStatus getStatus() {
        return status;
    }

    public void setStatus(BeitragsStatus status) {
        this.status = status;
    }
}