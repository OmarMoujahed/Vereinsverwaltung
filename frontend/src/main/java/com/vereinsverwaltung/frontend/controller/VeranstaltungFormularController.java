package com.vereinsverwaltung.frontend.controller;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vereinsverwaltung.frontend.ApiService;
import com.vereinsverwaltung.frontend.LocalDateAdapter;
import com.vereinsverwaltung.frontend.model.Gruppe;
import com.vereinsverwaltung.frontend.model.Veranstaltung;
import com.vereinsverwaltung.frontend.model.VeranstaltungStatus;
import com.vereinsverwaltung.frontend.model.Verein;
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

public class VeranstaltungFormularController {

    @FXML private Label titelLabel;
    @FXML private TextField nameField;
    @FXML private DatePicker datumPicker;
    @FXML private TextField ortField;
    @FXML private TextArea beschreibungField;
    @FXML private ComboBox<Verein> vereinCombo;
    @FXML private ComboBox<Gruppe> gruppeCombo;
    @FXML private ComboBox<VeranstaltungStatus> statusCombo;

    private Veranstaltung zuBearbeitendeVeranstaltung = null;

    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create();

    @FXML
    public void initialize() {
        statusCombo.setItems(FXCollections.observableArrayList(VeranstaltungStatus.values()));

        try {
            List<Verein> vereine = ApiService.alleVereine();
            vereinCombo.setItems(FXCollections.observableArrayList(vereine));
            vereinCombo.setConverter(new StringConverter<>() {
                public String toString(Verein v) { return v != null ? v.getName() : ""; }
                public Verein fromString(String s) { return null; }
            });

            gruppeCombo.setConverter(new StringConverter<>() {
                public String toString(Gruppe g) { return g != null ? g.getName() : ""; }
                public Gruppe fromString(String s) { return null; }
            });

            vereinCombo.valueProperty().addListener((obs, alt, neu) -> {
                gruppeCombo.getItems().clear();
                if (neu != null) {
                    try {
                        List<Gruppe> gruppen = ApiService.gruppenVonVerein(neu.getVerein_id());
                        gruppeCombo.setItems(FXCollections.observableArrayList(gruppen));
                    } catch (Exception e) {
                        System.out.println("Fehler beim Laden der Gruppen: " + e.getMessage());
                    }
                }
            });
        } catch (Exception e) {
            System.out.println("Fehler beim Laden der Daten: " + e.getMessage());
        }
    }

    public void setVeranstaltung(Veranstaltung veranstaltung) {
        this.zuBearbeitendeVeranstaltung = veranstaltung;
        titelLabel.setText("Veranstaltung bearbeiten");
        nameField.setText(veranstaltung.getName());
        datumPicker.setValue(veranstaltung.getDatum());
        ortField.setText(veranstaltung.getOrt() != null ? veranstaltung.getOrt() : "");
        beschreibungField.setText(veranstaltung.getBeschreibung() != null ? veranstaltung.getBeschreibung() : "");

        if (veranstaltung.getStatus() != null) {
            statusCombo.setValue(veranstaltung.getStatus());
        }

        if (veranstaltung.getVerein() != null) {
            vereinCombo.getItems().stream()
                    .filter(v -> v.getVerein_id().equals(veranstaltung.getVerein().getVerein_id()))
                    .findFirst()
                    .ifPresent(vereinCombo::setValue);
        }

        if (veranstaltung.getGruppe() != null) {
            gruppeCombo.getItems().stream()
                    .filter(g -> g.getGruppe_id().equals(veranstaltung.getGruppe().getGruppe_id()))
                    .findFirst()
                    .ifPresent(gruppeCombo::setValue);
        }
    }

    @FXML
    private void speichern() {
        if (nameField.getText().isBlank()) {
            zeigeWarnung("Name ist ein Pflichtfeld.");
            return;
        }
        if (vereinCombo.getValue() == null) {
            zeigeWarnung("Bitte einen Verein auswählen.");
            return;
        }

        Veranstaltung veranstaltung = zuBearbeitendeVeranstaltung != null ? zuBearbeitendeVeranstaltung : new Veranstaltung();
        veranstaltung.setName(nameField.getText());
        veranstaltung.setDatum(datumPicker.getValue());
        veranstaltung.setOrt(ortField.getText());
        veranstaltung.setBeschreibung(beschreibungField.getText());
        veranstaltung.setVerein(vereinCombo.getValue());
        veranstaltung.setGruppe(gruppeCombo.getValue());
        veranstaltung.setStatus(statusCombo.getValue());

        try {
            String json = gson.toJson(veranstaltung);
            HttpRequest request;

            if (zuBearbeitendeVeranstaltung != null) {
                request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/api/veranstaltungen/" + veranstaltung.getId()))
                        .header("Content-Type", "application/json")
                        .PUT(HttpRequest.BodyPublishers.ofString(json))
                        .build();
            } else {
                request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/api/veranstaltungen"))
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
        Stage stage = (Stage) nameField.getScene().getWindow();
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