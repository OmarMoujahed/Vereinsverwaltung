package com.vereinsverwaltung.frontend.controller;

import com.vereinsverwaltung.frontend.ApiService;
import com.vereinsverwaltung.frontend.SceneManager;
import com.vereinsverwaltung.frontend.model.Veranstaltung;
import com.vereinsverwaltung.frontend.model.VeranstaltungStatus;
import com.vereinsverwaltung.frontend.model.Verein;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class VeranstaltungenController {

    @FXML private TableView<Veranstaltung> veranstaltungenTabelle;
    @FXML private TableColumn<Veranstaltung, String> bezeichnungColumn;
    @FXML private TableColumn<Veranstaltung, String> vereinColumn;
    @FXML private TableColumn<Veranstaltung, String> datumColumn;
    @FXML private TableColumn<Veranstaltung, String> statusColumn;
    @FXML private TableColumn<Veranstaltung, String> aktionenColumn;
    @FXML private TextField sucheField;
    @FXML private ComboBox<Verein> vereinFilter;
    @FXML private ComboBox<String> statusFilter;

    private ObservableList<Veranstaltung> veranstaltungenListe = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        bezeichnungColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));

        vereinColumn.setCellValueFactory(c -> {
            Veranstaltung v = c.getValue();
            if (v.getVerein() == null) return new SimpleStringProperty("-");
            return new SimpleStringProperty(v.getVerein().getName());
        });

        datumColumn.setCellValueFactory(c -> {
            Veranstaltung v = c.getValue();
            if (v.getDatum() == null) return new SimpleStringProperty("-");
            return new SimpleStringProperty(v.getDatum().toString());
        });

        statusColumn.setCellValueFactory(c -> {
            Veranstaltung v = c.getValue();
            if (v.getStatus() == null) return new SimpleStringProperty("-");
            return new SimpleStringProperty(v.getStatus().toString());
        });

        aktionenColumn.setCellFactory(col -> new TableCell<>() {
            private final Button loeschenBtn = new Button("✕");
            private final Button bearbeitenBtn = new Button("✎");
            private final HBox box = new HBox(4, bearbeitenBtn, loeschenBtn);

            {
                bearbeitenBtn.getStyleClass().add("flat");
                loeschenBtn.getStyleClass().add("flat");

                bearbeitenBtn.setOnAction(e -> {
                    Veranstaltung v = getTableView().getItems().get(getIndex());
                    veranstaltungBearbeiten(v);
                });

                loeschenBtn.setOnAction(e -> {
                    Veranstaltung v = getTableView().getItems().get(getIndex());
                    veranstaltungLoeschen(v);
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });

        try {
            List<Verein> vereine = ApiService.alleVereine();
            vereinFilter.setItems(FXCollections.observableArrayList(vereine));
            vereinFilter.setConverter(new javafx.util.StringConverter<>() {
                public String toString(Verein v) { return v != null ? v.getName() : "Alle"; }
                public Verein fromString(String s) { return null; }
            });
            vereinFilter.valueProperty().addListener((obs, alt, neu) -> {
                if (neu == null) {
                    veranstaltungenTabelle.setItems(veranstaltungenListe);
                } else {
                    try {
                        List<Veranstaltung> gefiltert = ApiService.veranstaltungenVonVerein(neu.getVerein_id());
                        veranstaltungenTabelle.setItems(FXCollections.observableArrayList(gefiltert));
                    } catch (Exception e) {
                        System.out.println("Fehler beim Filtern: " + e.getMessage());
                    }
                }
            });
        } catch (Exception e) {
            System.out.println("Fehler beim Laden der Vereine: " + e.getMessage());
        }

        statusFilter.setItems(FXCollections.observableArrayList("GEPLANT", "ABGESCHLOSSEN", "ABGESAGT"));
        statusFilter.valueProperty().addListener((obs, alt, neu) -> {
            if (neu == null) {
                veranstaltungenTabelle.setItems(veranstaltungenListe);
            } else {
                FilteredList<Veranstaltung> gefiltert = new FilteredList<>(veranstaltungenListe, v ->
                        v.getStatus() != null && v.getStatus().toString().equals(neu));
                veranstaltungenTabelle.setItems(gefiltert);
            }
        });

        FilteredList<Veranstaltung> filteredList = new FilteredList<>(veranstaltungenListe, p -> true);
        sucheField.textProperty().addListener((obs, alt, neu) -> {
            filteredList.setPredicate(v -> {
                if (neu == null || neu.isEmpty()) return true;
                String lower = neu.toLowerCase();
                return v.getName().toLowerCase().contains(lower) ||
                        (v.getOrt() != null && v.getOrt().toLowerCase().contains(lower));
            });
        });
        veranstaltungenTabelle.setItems(filteredList);

        try {
            List<Veranstaltung> veranstaltungen = ApiService.alleVeranstaltungen();
            veranstaltungenListe.setAll(veranstaltungen);
        } catch (Exception e) {
            System.out.println("Fehler beim Laden der Veranstaltungen: " + e.getMessage());
        }
    }

    @FXML
    private void veranstaltungHinzufuegen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/vereinsverwaltung/frontend/veranstaltungformular.fxml"));
            VBox root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Neue Veranstaltung");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            veranstaltungenListe.setAll(ApiService.alleVeranstaltungen());
            if (vereinFilter.getValue() != null) {
                veranstaltungenTabelle.setItems(FXCollections.observableArrayList(
                        ApiService.veranstaltungenVonVerein(vereinFilter.getValue().getVerein_id())));
            }
        } catch (Exception e) {
            System.out.println("Fehler: " + e.getMessage());
        }
    }

    private void veranstaltungBearbeiten(Veranstaltung veranstaltung) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/vereinsverwaltung/frontend/veranstaltungformular.fxml"));
            VBox root = loader.load();
            VeranstaltungFormularController controller = loader.getController();
            controller.setVeranstaltung(veranstaltung);
            Stage stage = new Stage();
            stage.setTitle("Veranstaltung bearbeiten");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            veranstaltungenListe.setAll(ApiService.alleVeranstaltungen());
            if (vereinFilter.getValue() != null) {
                veranstaltungenTabelle.setItems(FXCollections.observableArrayList(
                        ApiService.veranstaltungenVonVerein(vereinFilter.getValue().getVerein_id())));
            }
        } catch (Exception e) {
            System.out.println("Fehler: " + e.getMessage());
        }
    }

    private void veranstaltungLoeschen(Veranstaltung veranstaltung) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Veranstaltung löschen");
        alert.setHeaderText(veranstaltung.getName() + " löschen?");
        alert.setContentText("Diese Aktion kann nicht rückgängig gemacht werden.");

        alert.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                try {
                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create("http://localhost:8080/api/veranstaltungen/" + veranstaltung.getId()))
                            .DELETE()
                            .build();
                    HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
                    veranstaltungenListe.setAll(ApiService.alleVeranstaltungen());
                } catch (Exception e) {
                    System.out.println("Fehler beim Löschen: " + e.getMessage());
                }
            }
        });
    }

    @FXML public void zuHome() throws IOException { SceneManager.switchTo("dashboard.fxml"); }
    @FXML public void zuMitglieder() throws IOException { SceneManager.switchTo("mitglieder.fxml"); }
    @FXML public void zuVereine() throws IOException { SceneManager.switchTo("vereine.fxml"); }
    @FXML public void zuGruppen() throws IOException { SceneManager.switchTo("gruppen.fxml"); }
    @FXML public void zuRollen() throws IOException { SceneManager.switchTo("rollen.fxml"); }
    @FXML public void zuVeranstaltungen() throws IOException { SceneManager.switchTo("veranstaltungen.fxml"); }
    @FXML public void zuBeitraege() throws IOException { SceneManager.switchTo("beitraege.fxml"); }
}