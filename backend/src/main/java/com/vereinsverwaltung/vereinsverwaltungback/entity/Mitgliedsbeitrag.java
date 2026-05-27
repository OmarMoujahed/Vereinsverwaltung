package com.vereinsverwaltung.vereinsverwaltungback.entity;

import com.vereinsverwaltung.vereinsverwaltungback.domain.Geldbetrag;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Mitgliedsbeitrag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private Geldbetrag betrag;

    private String zeitraum;

    private LocalDate faelligkeitsdatum;

    @Enumerated(EnumType.STRING)
    private BeitragsTyp typ;


    @Enumerated(EnumType.STRING)
    private BeitragsStatus status;

    @ManyToOne
    @JoinColumn(name = "ursprungs_beitrag_id")
    private Mitgliedsbeitrag ursprungsBeitrag;

    //Beziehung zum Mitglied
    @ManyToOne
    @JoinColumn(name = "mitglied_id")
    private Mitglied mitglied;

    //Konstruktor
    public Mitgliedsbeitrag() {
    }

    public boolean istUeberfaellig() {
        return status == BeitragsStatus.OFFEN
                && faelligkeitsdatum != null
                && faelligkeitsdatum.isBefore(LocalDate.now());
    }

    public void markiereAlsBezahlt() {
        if (status == BeitragsStatus.BEZAHLT) {
            throw new IllegalStateException("Beitrag ist bereits bezahlt");
        }
        this.status = BeitragsStatus.BEZAHLT;
    }

    public void aktualisiereStatus() {
        if (istUeberfaellig()) {
            this.status = BeitragsStatus.UEBERFAELLIG;
        }
    }

    public boolean istWiederkehrend() {
        return typ == BeitragsTyp.MONATLICH || typ == BeitragsTyp.JAEHRLICH;
    }

    public LocalDate berechneNaechstesFaelligkeitsdatum() {
        LocalDate naechstesDatum = getFaelligkeitsdatum();

        if (typ == BeitragsTyp.MONATLICH) {
            naechstesDatum = naechstesDatum.plusMonths(1);
        } else if (typ == BeitragsTyp.JAEHRLICH) {
            naechstesDatum = naechstesDatum.plusYears(1);
        }

        return naechstesDatum;
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

    public BeitragsStatus getStatus() {
        return status;
    }

    public void setStatus(BeitragsStatus status) {
        this.status = status;
    }

    public BeitragsTyp getTyp() {
        return typ;
    }

    public void setTyp(BeitragsTyp typ) {
        this.typ = typ;
    }

    public Mitgliedsbeitrag getUrsprungsBeitrag() {
        return ursprungsBeitrag;
    }

    public void setUrsprungsBeitrag(Mitgliedsbeitrag ursprungsBeitrag) {
        this.ursprungsBeitrag = ursprungsBeitrag;
    }

    public Mitglied getMitglied() {
        return mitglied;
    }

    public void setMitglied(Mitglied mitglied) {
        this.mitglied = mitglied;
    }
}