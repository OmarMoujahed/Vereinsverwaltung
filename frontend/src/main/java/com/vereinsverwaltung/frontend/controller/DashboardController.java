package com.vereinsverwaltung.frontend.controller;

import com.vereinsverwaltung.frontend.ApiService;
import com.vereinsverwaltung.frontend.SceneManager;
import com.vereinsverwaltung.frontend.model.DashboardEintrag;
import com.vereinsverwaltung.frontend.model.Mitglied;
import com.vereinsverwaltung.frontend.model.Veranstaltung;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DashboardController {

    @FXML private Label anzahlMitglieder;
    @FXML private Label anzahlVereine;
    @FXML private Label anzahlVeranstaltungen;
    @FXML private VBox naechsteVeranstaltungenBox;
    @FXML private VBox neuesteMitgliederBox;

    @FXML
    public void initialize() {
        try {
            List<DashboardEintrag> alleEintraege = new ArrayList<>();

            ApiService.alleMitglieder().forEach(m -> {
                alleEintraege.add(new DashboardEintrag(
                        m.getVorname() + " " + m.getNachname(),
                        m.getVerein() != null ? m.getVerein().getName() : "-",
                        "Mitglied",
                        m.getErstelltAm()));
            });

            ApiService.alleVereine().forEach(v -> alleEintraege.add(new DashboardEintrag(
                    v.getName(),
                    v.getBeschreibung() != null ? v.getBeschreibung() : "-",
                    "Verein",
                    v.getErstelltAm())));

            ApiService.alleGruppen().forEach(g -> alleEintraege.add(new DashboardEintrag(
                    g.getName(),
                    g.getVerein() != null ? g.getVerein().getName() : "-",
                    "Gruppe",
                    g.getErstelltAm())));

            ApiService.alleRollen().forEach(r -> alleEintraege.add(new DashboardEintrag(
                    r.getName(),
                    r.isIstGlobal() ? "Global" : (r.getVerein() != null ? r.getVerein().getName() : "-"),
                    "Rolle",
                    r.getErstelltAm())));

            ApiService.alleVeranstaltungen().forEach(v -> alleEintraege.add(new DashboardEintrag(
                    v.getName(),
                    v.getVerein() != null ? v.getVerein().getName() : "-",
                    "Veranstaltung",
                    v.getErstelltAm())));

            alleEintraege.stream()
                    .filter(e -> e.getErstelltAm() != null)
                    .sorted((a, b) -> b.getErstelltAm().compareTo(a.getErstelltAm()))
                    .limit(3)
                    .forEach(e -> neuesteMitgliederBox.getChildren().add(
                            erstelleEintrag(e.getTitel(), e.getUntertitel(), e.getKategorie())));

            anzahlMitglieder.setText(String.valueOf(ApiService.alleMitglieder().size()));

        } catch (Exception e) {
            System.out.println("Fehler: " + e.getMessage());
        }

        try {
            anzahlVereine.setText(String.valueOf(ApiService.alleVereine().size()));
        } catch (Exception e) {
            anzahlVereine.setText("-");
        }

        try {
            List<Veranstaltung> veranstaltungen = ApiService.alleVeranstaltungen();
            anzahlVeranstaltungen.setText(String.valueOf(veranstaltungen.size()));

            int von = Math.max(0, veranstaltungen.size() - 3);
            List<Veranstaltung> naechste = veranstaltungen.subList(von, veranstaltungen.size());
            for (Veranstaltung v : naechste) {
                String verein = v.getVerein() != null ? v.getVerein().getName() : "-";
                String datum = v.getDatum() != null ? v.getDatum().toString() : "-";
                naechsteVeranstaltungenBox.getChildren().add(erstelleEintrag(v.getName(), verein, datum));
            }
        } catch (Exception e) {
            anzahlVeranstaltungen.setText("-");
        }
    }

    private HBox erstelleEintrag(String titel, String untertitel, String rechts) {
        HBox zeile = new HBox();
        zeile.setAlignment(Pos.CENTER_LEFT);

        VBox info = new VBox(2);
        Label titelLabel = new Label(titel);
        Label untertitelLabel = new Label(untertitel);
        untertitelLabel.getStyleClass().add("text-muted");
        info.getChildren().addAll(titelLabel, untertitelLabel);

        Pane spacer = new Pane();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label rechtsLabel = new Label(rechts);
        rechtsLabel.getStyleClass().add("text-muted");

        zeile.getChildren().addAll(info, spacer, rechtsLabel);
        return zeile;
    }

    @FXML private void zuHome() throws IOException { SceneManager.switchTo("dashboard.fxml"); }
    @FXML private void zuMitglieder() throws IOException { SceneManager.switchTo("mitglieder.fxml"); }
    @FXML private void zuVereine() throws IOException { SceneManager.switchTo("vereine.fxml"); }
    @FXML private void zuGruppen() throws IOException { SceneManager.switchTo("gruppen.fxml"); }
    @FXML private void zuRollen() throws IOException { SceneManager.switchTo("rollen.fxml"); }
    @FXML private void zuVeranstaltungen() throws IOException { SceneManager.switchTo("veranstaltungen.fxml"); }
    @FXML private void zuBeitraege() throws IOException { SceneManager.switchTo("beitraege.fxml"); }
}