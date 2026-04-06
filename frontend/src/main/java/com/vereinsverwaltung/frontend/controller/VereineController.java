package com.vereinsverwaltung.frontend.controller;

import com.vereinsverwaltung.frontend.ApiService;
import com.vereinsverwaltung.frontend.SceneManager;
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

public class VereineController {

    @FXML private TableView<Verein> vereineTabelle;
    @FXML private TableColumn<Verein, String> idColumn;
    @FXML private TableColumn<Verein, String> nameColumn;
    @FXML private TableColumn<Verein, String> beschreibungColumn;
    @FXML private TableColumn<Verein, String> emailColumn;
    @FXML private TableColumn<Verein, String> telefonColumn;
    @FXML private TableColumn<Verein, String> adresseColumn;
    @FXML private TableColumn<Verein, String> aktionenColumn;
    @FXML private TableColumn<Verein, String> mitgliederColumn;
    @FXML private TextField sucheField;

    private ObservableList<Verein> vereineListe = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getVerein_id() != null ? c.getValue().getVerein_id().toString() : "-"));
        nameColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));
        beschreibungColumn.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getBeschreibung() != null ? c.getValue().getBeschreibung() : "-"));
        emailColumn.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getEmail() != null ? c.getValue().getEmail() : "-"));
        telefonColumn.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getTelefon() != null ? c.getValue().getTelefon() : "-"));
        adresseColumn.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getAdresse() != null ? c.getValue().getAdresse() : "-"));
        mitgliederColumn.setCellValueFactory(c -> {
            try {
                int anzahl = ApiService.mitgliederVonVerein(c.getValue().getVerein_id()).size();
                return new SimpleStringProperty(String.valueOf(anzahl));
            } catch (Exception e) {
                return new SimpleStringProperty("-");
            }
        });

        aktionenColumn.setCellFactory(col -> new TableCell<>() {
            private final Button loeschenBtn = new Button("✕");
            private final Button bearbeitenBtn = new Button("✎");
            private final HBox box = new HBox(4, bearbeitenBtn, loeschenBtn);

            {
                bearbeitenBtn.getStyleClass().add("flat");
                loeschenBtn.getStyleClass().add("flat");

                bearbeitenBtn.setOnAction(e -> {
                    Verein v = getTableView().getItems().get(getIndex());
                    vereinBearbeiten(v);
                });

                loeschenBtn.setOnAction(e -> {
                    Verein v = getTableView().getItems().get(getIndex());
                    vereinLoeschen(v);
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });

        FilteredList<Verein> filteredList = new FilteredList<>(vereineListe, p -> true);
        sucheField.textProperty().addListener((obs, alt, neu) -> {
            filteredList.setPredicate(v -> {
                if (neu == null || neu.isEmpty()) return true;
                String filter = neu.toLowerCase();
                return v.getName().toLowerCase().contains(filter)
                        || (v.getBeschreibung() != null && v.getBeschreibung().toLowerCase().contains(filter));
            });
        });
        vereineTabelle.setItems(filteredList);

        try {
            List<Verein> vereine = ApiService.alleVereine();
            vereineListe.setAll(vereine);
        } catch (Exception e) {
            System.out.println("Fehler beim Laden der Vereine: " + e.getMessage());
        }
    }

    private void vereinLoeschen(Verein verein) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Verein löschen");
        alert.setHeaderText(verein.getName() + " löschen?");
        alert.setContentText("Diese Aktion kann nicht rückgängig gemacht werden.");

        alert.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                try {
                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create("http://localhost:8080/api/vereine/" + verein.getVerein_id()))
                            .DELETE()
                            .build();
                    HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
                    List<Verein> vereine = ApiService.alleVereine();
                    vereineListe.setAll(vereine);
                } catch (Exception e) {
                    System.out.println("Fehler beim Löschen: " + e.getMessage());
                }
            }
        });
    }

    @FXML
    private void vereinHinzufuegen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/vereinsverwaltung/frontend/vereinformular.fxml"));
            VBox root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Neuer Verein");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            List<Verein> vereine = ApiService.alleVereine();
            vereineListe.setAll(vereine);
        } catch (Exception e) {
            System.out.println("Fehler: " + e.getMessage());
        }
    }

    private void vereinBearbeiten(Verein verein) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/vereinsverwaltung/frontend/vereinformular.fxml"));
            VBox root = loader.load();
            VereinFormularController controller = loader.getController();
            controller.setVerein(verein);
            Stage stage = new Stage();
            stage.setTitle("Verein bearbeiten");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            List<Verein> vereine = ApiService.alleVereine();
            vereineListe.setAll(vereine);
        } catch (Exception e) {
            System.out.println("Fehler: " + e.getMessage());
        }
    }

    @FXML public void zuHome() throws IOException { SceneManager.switchTo("dashboard.fxml"); }
    @FXML public void zuMitglieder() throws IOException { SceneManager.switchTo("mitglieder.fxml"); }
    @FXML public void zuVereine() throws IOException { SceneManager.switchTo("vereine.fxml"); }
    @FXML public void zuGruppen() throws IOException { SceneManager.switchTo("gruppen.fxml"); }
    @FXML public void zuRollen() throws IOException { SceneManager.switchTo("rollen.fxml"); }
    @FXML public void zuVeranstaltungen() throws IOException { SceneManager.switchTo("veranstaltungen.fxml"); }
    @FXML public void zuBeitraege() throws IOException { SceneManager.switchTo("beitraege.fxml"); }
}