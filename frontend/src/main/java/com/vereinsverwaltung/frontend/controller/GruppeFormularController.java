package com.vereinsverwaltung.frontend.controller;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vereinsverwaltung.frontend.ApiService;
import com.vereinsverwaltung.frontend.LocalDateAdapter;
import com.vereinsverwaltung.frontend.model.Gruppe;
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

public class GruppeFormularController {

    @FXML private Label titelLabel;
    @FXML private TextField nameField;
    @FXML private TextArea beschreibungField;
    @FXML private ComboBox<Verein> vereinCombo;

    private Gruppe zuBearbeitendeGruppe = null;

    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create();

    @FXML
    public void initialize() {
        try {
            List<Verein> vereine = ApiService.alleVereine();
            vereinCombo.setItems(FXCollections.observableArrayList(vereine));
            vereinCombo.setConverter(new StringConverter<>() {
                public String toString(Verein v) { return v != null ? v.getName() : ""; }
                public Verein fromString(String s) { return null; }
            });
        } catch (Exception e) {
            System.out.println("Fehler beim Laden der Vereine: " + e.getMessage());
        }
    }

    public void setGruppe(Gruppe gruppe) {
        this.zuBearbeitendeGruppe = gruppe;
        titelLabel.setText("Gruppe bearbeiten");
        nameField.setText(gruppe.getName());
        beschreibungField.setText(gruppe.getBeschreibung() != null ? gruppe.getBeschreibung() : "");

        if (gruppe.getVerein() != null) {
            vereinCombo.getItems().stream()
                    .filter(v -> v.getVerein_id().equals(gruppe.getVerein().getVerein_id()))
                    .findFirst()
                    .ifPresent(vereinCombo::setValue);
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

        Gruppe gruppe = zuBearbeitendeGruppe != null ? zuBearbeitendeGruppe : new Gruppe();
        gruppe.setName(nameField.getText());
        gruppe.setBeschreibung(beschreibungField.getText());
        gruppe.setVerein(vereinCombo.getValue());

        try {
            String json = gson.toJson(gruppe);
            HttpRequest request;

            if (zuBearbeitendeGruppe != null) {
                request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/api/gruppen/" + gruppe.getGruppe_id()))
                        .header("Content-Type", "application/json")
                        .PUT(HttpRequest.BodyPublishers.ofString(json))
                        .build();
            } else {
                request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/api/gruppen"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(json))
                        .build();
            }

            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            fensterSchliessen();
        } catch (Exception e) {
            System.out.println("Fehler: " + e.getMessage());
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