package com.vereinsverwaltung.frontend.controller;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vereinsverwaltung.frontend.LocalDateAdapter;
import com.vereinsverwaltung.frontend.model.Verein;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;

public class VereinFormularController {

    @FXML private Label titelLabel;
    @FXML private TextField nameField;
    @FXML private TextArea beschreibungField;
    @FXML private TextField emailField;
    @FXML private TextField telefonField;
    @FXML private TextField adresseField;

    private Verein zuBearbeitenderVerein = null;

    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create();

    public void setVerein(Verein verein) {
        this.zuBearbeitenderVerein = verein;
        titelLabel.setText("Verein bearbeiten");

        nameField.setText(verein.getName());
        beschreibungField.setText(verein.getBeschreibung() != null ? verein.getBeschreibung() : "");
        emailField.setText(verein.getEmail() != null ? verein.getEmail() : "");
        telefonField.setText(verein.getTelefon() != null ? verein.getTelefon() : "");
        adresseField.setText(verein.getAdresse() != null ? verein.getAdresse() : "");
    }

    @FXML
    private void speichern() {
        if (nameField.getText().isBlank()) {
            zeigeWarnung("Name ist ein Pflichtfeld.");
            return;
        }

        Verein verein = zuBearbeitenderVerein != null ? zuBearbeitenderVerein : new Verein();
        verein.setName(nameField.getText());
        verein.setBeschreibung(beschreibungField.getText());
        verein.setEmail(emailField.getText());
        verein.setTelefon(telefonField.getText());
        verein.setAdresse(adresseField.getText());

        try {
            String json = gson.toJson(verein);
            HttpRequest request;

            if (zuBearbeitenderVerein != null) {
                request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/api/vereine/" + verein.getVerein_id()))
                        .header("Content-Type", "application/json")
                        .PUT(HttpRequest.BodyPublishers.ofString(json))
                        .build();
            } else {
                request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/api/vereine"))
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