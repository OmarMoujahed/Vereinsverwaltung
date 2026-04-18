package com.vereinsverwaltung.frontend.controller;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vereinsverwaltung.frontend.ApiService;
import com.vereinsverwaltung.frontend.LocalDateAdapter;
import com.vereinsverwaltung.frontend.model.BeitragsStatus;
import com.vereinsverwaltung.frontend.model.Geldbetrag;
import com.vereinsverwaltung.frontend.model.Mitglied;
import com.vereinsverwaltung.frontend.model.Mitgliedsbeitrag;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.List;

public class BeitragFormularController {

    @FXML private Label titelLabel;
    @FXML private ComboBox<Mitglied> mitgliedCombo;
    @FXML private TextField betragField;
    @FXML private TextField zeitraumField;
    @FXML private DatePicker faelligkeitPicker;
    @FXML private ComboBox<BeitragsStatus> statusCombo;

    private Mitgliedsbeitrag zuBearbeitenderBeitrag = null;

    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create();

    @FXML
    public void initialize() {
        statusCombo.setItems(FXCollections.observableArrayList(BeitragsStatus.values()));

        try {
            List<Mitglied> mitglieder = ApiService.alleMitglieder();
            mitgliedCombo.setItems(FXCollections.observableArrayList(mitglieder));
            mitgliedCombo.setConverter(new StringConverter<>() {
                public String toString(Mitglied m) {
                    return m != null ? m.getVorname() + " " + m.getNachname() : "";
                }
                public Mitglied fromString(String s) { return null; }
            });
        } catch (Exception e) {
            System.out.println("Fehler beim Laden der Mitglieder: " + e.getMessage());
        }
    }

    public void setBeitrag(Mitgliedsbeitrag beitrag) {
        this.zuBearbeitenderBeitrag = beitrag;
        titelLabel.setText("Beitrag bearbeiten");

        if (beitrag.getMitglied() != null) {
            mitgliedCombo.getItems().stream()
                    .filter(m -> m.getMitglied_id().equals(beitrag.getMitglied().getMitglied_id()))
                    .findFirst()
                    .ifPresent(mitgliedCombo::setValue);
        }

        betragField.setText(beitrag.getBetrag() != null ? String.valueOf(beitrag.getBetrag().getBetrag()) : "");
        zeitraumField.setText(beitrag.getZeitraum() != null ? beitrag.getZeitraum() : "");
        faelligkeitPicker.setValue(beitrag.getFaelligkeitsdatum());
        statusCombo.setValue(beitrag.getStatus());
    }

    @FXML
    private void speichern() {
        if (mitgliedCombo.getValue() == null) {
            zeigeWarnung("Bitte ein Mitglied auswählen.");
            return;
        }
        if (betragField.getText().isBlank()) {
            zeigeWarnung("Betrag ist ein Pflichtfeld.");
            return;
        }

        double betrag;
        try {
            betrag = Double.parseDouble(betragField.getText().replace(",", "."));
        } catch (NumberFormatException e) {
            zeigeWarnung("Betrag muss eine Zahl sein.");
            return;
        }

        Mitgliedsbeitrag beitrag = zuBearbeitenderBeitrag != null ? zuBearbeitenderBeitrag : new Mitgliedsbeitrag();
        beitrag.setMitglied(mitgliedCombo.getValue());
        beitrag.setBetrag(new Geldbetrag(betrag, "EUR"));
        beitrag.setZeitraum(zeitraumField.getText());
        beitrag.setFaelligkeitsdatum(faelligkeitPicker.getValue());
        beitrag.setStatus(statusCombo.getValue());

        try {
            String json = gson.toJson(beitrag);
            HttpRequest request;

            if (zuBearbeitenderBeitrag != null) {
                request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/api/beitraege/" + beitrag.getId()))
                        .header("Content-Type", "application/json")
                        .PUT(HttpRequest.BodyPublishers.ofString(json))
                        .build();
            } else {
                request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/api/beitraege"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(json))
                        .build();
            }

            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            fensterSchliessen();
        } catch (Exception e) {
            System.out.println("Fehler beim Speichern: " + e.getMessage());
        }
    }

    @FXML
    private void abbrechen() {
        fensterSchliessen();
    }

    private void fensterSchliessen() {
        Stage stage = (Stage) betragField.getScene().getWindow();
        stage.close();
    }

    private void zeigeWarnung(String nachricht) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Pflichtfeld fehlt");
        alert.setHeaderText(null);
        alert.setContentText(nachricht);
        alert.showAndWait();
    }
}