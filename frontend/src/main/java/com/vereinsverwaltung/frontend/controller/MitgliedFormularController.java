package com.vereinsverwaltung.frontend.controller;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vereinsverwaltung.frontend.ApiService;
import com.vereinsverwaltung.frontend.LocalDateAdapter;
import com.vereinsverwaltung.frontend.model.Gruppe;
import com.vereinsverwaltung.frontend.model.Mitglied;
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
import java.util.ArrayList;
import java.util.List;

public class MitgliedFormularController {

    @FXML private TextField vornameField;
    @FXML private TextField nachnameField;
    @FXML private TextField emailField;
    @FXML private TextField telefonField;
    @FXML private TextField adresseField;
    @FXML private DatePicker geburtsdatumPicker;
    @FXML private DatePicker eintrittsdatumPicker;
    @FXML private ComboBox<Verein> vereinCombo;
    @FXML private ComboBox<Rolle> rolleCombo;
    @FXML private Label titelLabel;
    @FXML private ComboBox<Gruppe> gruppenCombo;
    @FXML private ListView<String> gruppenListe;
    private boolean gruppenGeladen = false;

    private List<Gruppe> ausgewaehlteGruppen = new ArrayList<>();

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

            List<Rolle> rollen = ApiService.alleRollen();
            rolleCombo.setItems(FXCollections.observableArrayList(rollen));
            rolleCombo.setConverter(new StringConverter<>() {
                public String toString(Rolle r) { return r != null ? r.getName() : ""; }
                public Rolle fromString(String s) { return null; }
            });

            gruppenCombo.setConverter(new StringConverter<>() {
                public String toString(Gruppe g) { return g != null ? g.getName() : ""; }
                public Gruppe fromString(String s) { return null; }
            });

            vereinCombo.valueProperty().addListener((obs, alt, neu) -> {
                if (!gruppenGeladen) {
                    gruppenCombo.getItems().clear();
                }
                if (neu != null) {
                    try {
                        List<Gruppe> gruppen = ApiService.gruppenVonVerein(neu.getVerein_id());
                        gruppenCombo.setItems(FXCollections.observableArrayList(gruppen));
                    } catch (Exception e) {
                        System.out.println("Fehler: " + e.getMessage());
                    }
                }
            });

        } catch (Exception e) {
            System.out.println("Fehler beim Laden der Daten: " + e.getMessage());
        }
    }
    @FXML
    private void gruppeHinzufuegen() {
        Gruppe g = gruppenCombo.getValue();
        if (g != null) {
            boolean bereitsVorhanden = ausgewaehlteGruppen.stream()
                    .anyMatch(ag -> ag.getGruppe_id().equals(g.getGruppe_id()));
            if (!bereitsVorhanden) {
                ausgewaehlteGruppen.add(g);
                gruppenListe.getItems().add(g.getName());
            }
        }
    }

    @FXML
    private void speichern() {
        if (vornameField.getText().isBlank() || nachnameField.getText().isBlank()) {
            zeigeWarnung("Vorname und Nachname sind Pflichtfelder.");
            return;
        }
        if (vereinCombo.getValue() == null) {
            zeigeWarnung("Bitte einen Verein auswählen.");
            return;
        }
        if (rolleCombo.getValue() == null) {
            zeigeWarnung("Bitte eine Rolle auswählen.");
            return;
        }
        Mitglied mitglied = zuBearbeitendesMitglied != null ? zuBearbeitendesMitglied : new Mitglied();
        mitglied.setVorname(vornameField.getText());
        mitglied.setNachname(nachnameField.getText());
        mitglied.setEmail(emailField.getText());
        mitglied.setTelefon(telefonField.getText());
        mitglied.setAdresse(adresseField.getText());
        mitglied.setGeburtsdatum(geburtsdatumPicker.getValue());
        mitglied.setEintrittsdatum(eintrittsdatumPicker.getValue());
        mitglied.setVerein(vereinCombo.getValue());
        mitglied.setRolle(rolleCombo.getValue());

        try {
            String json = gson.toJson(mitglied);
            HttpRequest request;

            if (zuBearbeitendesMitglied != null) {
                request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/api/mitglieder/" + mitglied.getMitglied_id()))
                        .header("Content-Type", "application/json")
                        .PUT(HttpRequest.BodyPublishers.ofString(json))
                        .build();
            } else {
                request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/api/mitglieder"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(json))
                        .build();
            }

            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

            Mitglied gespeichert = gson.fromJson(response.body(), Mitglied.class);
            Long mitgliedId = gespeichert.getMitglied_id();
            for (Gruppe g : ausgewaehlteGruppen) {
                boolean schonVorhanden = zuBearbeitendesMitglied != null &&
                        zuBearbeitendesMitglied.getGruppen() != null &&
                        zuBearbeitendesMitglied.getGruppen().stream()
                                .anyMatch(vorhandene -> vorhandene.getGruppe_id().equals(g.getGruppe_id()));

                if (!schonVorhanden) {
                    HttpRequest gruppeRequest = HttpRequest.newBuilder()
                            .uri(URI.create("http://localhost:8080/api/mitglieder/" + mitgliedId + "/gruppen/" + g.getGruppe_id()))
                            .POST(HttpRequest.BodyPublishers.noBody())
                            .build();
                    HttpClient.newHttpClient().send(gruppeRequest, HttpResponse.BodyHandlers.ofString());
                }
            }

            fensterSchliessen();
        } catch (Exception e) {
            System.out.println("Fehler beim Speichern: " + e.getMessage());
            e.printStackTrace();
        }
    }
    private void zeigeWarnung(String nachricht) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Pflichtfeld fehlt");
        alert.setHeaderText(null);
        alert.setContentText(nachricht);
        alert.showAndWait();
    }

    private Mitglied zuBearbeitendesMitglied = null;

    public void setMitglied(Mitglied mitglied) {
        this.zuBearbeitendesMitglied = mitglied;
        titelLabel.setText("Mitglied bearbeiten");

        vornameField.setText(mitglied.getVorname());
        nachnameField.setText(mitglied.getNachname());
        emailField.setText(mitglied.getEmail() != null ? mitglied.getEmail() : "");
        telefonField.setText(mitglied.getTelefon() != null ? mitglied.getTelefon() : "");
        adresseField.setText(mitglied.getAdresse() != null ? mitglied.getAdresse() : "");
        geburtsdatumPicker.setValue(mitglied.getGeburtsdatum());
        eintrittsdatumPicker.setValue(mitglied.getEintrittsdatum());

        if (mitglied.getVerein() != null) {
            vereinCombo.getItems().stream()
                    .filter(v -> v.getVerein_id().equals(mitglied.getVerein().getVerein_id()))
                    .findFirst()
                    .ifPresent(vereinCombo::setValue);
        }

        gruppenGeladen = true;
        if (mitglied.getGruppen() != null) {
            ausgewaehlteGruppen.addAll(mitglied.getGruppen());
            mitglied.getGruppen().forEach(g -> gruppenListe.getItems().add(g.getName()));
        }
        gruppenGeladen = false;

        if (mitglied.getRolle() != null) {
            rolleCombo.getItems().stream()
                    .filter(r -> r.getRolle_id().equals(mitglied.getRolle().getRolle_id()))
                    .findFirst()
                    .ifPresent(rolleCombo::setValue);
        }
    }

    @FXML
    private void abbrechen() {
        fensterSchliessen();
    }

    private void fensterSchliessen() {
        Stage stage = (Stage) vornameField.getScene().getWindow();
        stage.close();
    }
}