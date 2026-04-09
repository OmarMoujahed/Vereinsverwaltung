package com.vereinsverwaltung.frontend.controller;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vereinsverwaltung.frontend.ApiService;
import com.vereinsverwaltung.frontend.LocalDateAdapter;
import com.vereinsverwaltung.frontend.model.Rolle;
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

public class RolleFormularController {

    @FXML private Label titelLabel;
    @FXML private TextField nameField;
    @FXML private TextArea beschreibungField;
    @FXML private CheckBox istGlobalCheck;
    @FXML private ComboBox<Verein> vereinCombo;

    private Rolle zuBearbeitendeRolle = null;

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

        istGlobalCheck.selectedProperty().addListener((obs, alt, neu) -> {
            vereinCombo.setDisable(neu);
            if (neu) vereinCombo.setValue(null);
        });
    }

    public void setRolle(Rolle rolle) {
        this.zuBearbeitendeRolle = rolle;
        titelLabel.setText("Rolle bearbeiten");
        nameField.setText(rolle.getName());
        beschreibungField.setText(rolle.getBeschreibung() != null ? rolle.getBeschreibung() : "");
        istGlobalCheck.setSelected(rolle.isIstGlobal());

        if (rolle.getVerein() != null) {
            vereinCombo.getItems().stream()
                    .filter(v -> v.getVerein_id().equals(rolle.getVerein().getVerein_id()))
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
        if (!istGlobalCheck.isSelected() && vereinCombo.getValue() == null) {
            zeigeWarnung("Bitte einen Verein auswählen oder die Rolle als global markieren.");
            return;
        }

        Rolle rolle = zuBearbeitendeRolle != null ? zuBearbeitendeRolle : new Rolle();
        rolle.setName(nameField.getText());
        rolle.setBeschreibung(beschreibungField.getText());
        rolle.setIstGlobal(istGlobalCheck.isSelected());
        rolle.setVerein(istGlobalCheck.isSelected() ? null : vereinCombo.getValue());

        try {
            String json = gson.toJson(rolle);
            HttpRequest request;

            if (zuBearbeitendeRolle != null) {
                request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/api/rollen/" + rolle.getRolle_id()))
                        .header("Content-Type", "application/json")
                        .PUT(HttpRequest.BodyPublishers.ofString(json))
                        .build();
            } else {
                request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/api/rollen"))
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