package com.vereinsverwaltung.frontend.controller;

import com.vereinsverwaltung.frontend.ApiService;
import com.vereinsverwaltung.frontend.SceneManager;
import com.vereinsverwaltung.frontend.model.Mitglied;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import javafx.scene.layout.HBox;

public class MitgliederController {

    @FXML private TableView<Mitglied> mitgliederTabelle;
    @FXML private TableColumn<Mitglied, String> idColumn;
    @FXML private TableColumn<Mitglied, String> nachnameColumn;
    @FXML private TableColumn<Mitglied, String> vornameColumn;
    @FXML private TableColumn<Mitglied, String> emailColumn;
    @FXML private TableColumn<Mitglied, String> telefonColumn;
    @FXML private TableColumn<Mitglied, String> adresseColumn;
    @FXML private TableColumn<Mitglied, String> geburtsdatumColumn;
    @FXML private TableColumn<Mitglied, String> eintrittsdatumColumn;
    @FXML private TableColumn<Mitglied, String> rolleColumn;
    @FXML private TableColumn<Mitglied, String> vereinColumn;
    @FXML private TableColumn<Mitglied, String> gruppeColumn;
    @FXML private TableColumn<Mitglied, String> aktionenColumn;
    @FXML private TextField sucheField;

    private ObservableList<Mitglied> mitgliederListe = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getMitglied_id() != null ? c.getValue().getMitglied_id().toString() : "-"));

        nachnameColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNachname()));
        vornameColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getVorname()));

        emailColumn.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getEmail() != null ? c.getValue().getEmail() : "-"));

        telefonColumn.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getTelefon() != null ? c.getValue().getTelefon() : "-"));
        adresseColumn.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getAdresse() != null ? c.getValue().getAdresse() : "-"));

        geburtsdatumColumn.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getGeburtsdatum() != null ? c.getValue().getGeburtsdatum().toString() : "-"));

        eintrittsdatumColumn.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getEintrittsdatum() != null ? c.getValue().getEintrittsdatum().toString() : "-"));

        vereinColumn.setCellValueFactory(c -> {
            Mitglied m = c.getValue();
            if (m.getVerein() == null) return new SimpleStringProperty("-");
            return new SimpleStringProperty(m.getVerein().getName());
        });

        gruppeColumn.setCellValueFactory(c -> {
            Mitglied m = c.getValue();
            if (m.getGruppen() == null || m.getGruppen().isEmpty()) return new SimpleStringProperty("-");
            String gruppen = m.getGruppen().stream()
                    .map(g -> g.getName())
                    .collect(java.util.stream.Collectors.joining(", "));
            return new SimpleStringProperty(gruppen);
        });

        rolleColumn.setCellValueFactory(c -> {
            Mitglied m = c.getValue();
            if (m.getRolle() == null) return new SimpleStringProperty("-");
            return new SimpleStringProperty(m.getRolle().getName());
        });

        aktionenColumn.setCellFactory(col -> new TableCell<>() {
            private final Button loeschenBtn = new Button("✕");
            private final Button bearbeitenBtn = new Button("✎");
            private final HBox box = new HBox(4, bearbeitenBtn, loeschenBtn);

            {
                bearbeitenBtn.getStyleClass().add("flat");
                loeschenBtn.getStyleClass().add("flat");

                bearbeitenBtn.setOnAction(e -> {
                    Mitglied m = getTableView().getItems().get(getIndex());
                    mitgliedBearbeiten(m);
                });

                loeschenBtn.setOnAction(e -> {
                    Mitglied m = getTableView().getItems().get(getIndex());
                    mitgliedLoeschen(m);
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });

        FilteredList<Mitglied> filteredList = new FilteredList<>(mitgliederListe, p -> true);
        sucheField.textProperty().addListener((obs, alt, neu) -> {
            filteredList.setPredicate(m -> {
                if (neu == null || neu.isEmpty()) return true;
                String filter = neu.toLowerCase();
                return m.getNachname().toLowerCase().contains(filter)
                        || m.getVorname().toLowerCase().contains(filter)
                        || (m.getMitglied_id() != null && m.getMitglied_id().toString().contains(filter));
            });
        });
        mitgliederTabelle.setItems(filteredList);

        try {
            List<Mitglied> mitglieder = ApiService.alleMitglieder();
            mitgliederListe.setAll(mitglieder);
        } catch (Exception e) {
            System.out.println("Fehler beim Laden der Mitglieder: " + e.getMessage());
        }
    }
    @FXML
    private void mitgliedHinzufuegen() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/vereinsverwaltung/frontend/mitgliederformular.fxml"));
        VBox root = loader.load();

        Stage stage = new Stage();
        stage.setTitle("Neues Mitglied");
        stage.setScene(new Scene(root));
        stage.showAndWait();

        List<Mitglied> mitglieder = ApiService.alleMitglieder();
        mitgliederListe.setAll(mitglieder);
    }
    private void mitgliedBearbeiten(Mitglied mitglied) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/vereinsverwaltung/frontend/mitgliederformular.fxml"));
            VBox root = loader.load();

            MitgliedFormularController controller = loader.getController();
            controller.setMitglied(mitglied);

            Stage stage = new Stage();
            stage.setTitle("Mitglied bearbeiten");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            List<Mitglied> mitglieder = ApiService.alleMitglieder();
            mitgliederListe.setAll(mitglieder);
        } catch (Exception e) {
            System.out.println("Fehler beim Öffnen: " + e.getMessage());
        }
    }

    private void mitgliedLoeschen(Mitglied mitglied) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Mitglied löschen");
        alert.setHeaderText(mitglied.getVorname() + " " + mitglied.getNachname() + " löschen?");
        alert.setContentText("Diese Aktion kann nicht rückgängig gemacht werden.");

        alert.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                try {
                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create("http://localhost:8080/api/mitglieder/" + mitglied.getMitglied_id()))
                            .DELETE()
                            .build();
                    HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

                    List<Mitglied> mitglieder = ApiService.alleMitglieder();
                    mitgliederListe.setAll(mitglieder);
                } catch (Exception e) {
                    System.out.println("Fehler beim Löschen: " + e.getMessage());
                }
            }
        });
    }

    @FXML private void zuHome() throws IOException { SceneManager.switchTo("dashboard.fxml"); }
    @FXML private void zuMitglieder() throws IOException { SceneManager.switchTo("mitglieder.fxml"); }
    @FXML private void zuVereine() throws IOException { SceneManager.switchTo("vereine.fxml"); }
    @FXML private void zuGruppen() throws IOException { SceneManager.switchTo("gruppen.fxml"); }
    @FXML private void zuRollen() throws IOException { SceneManager.switchTo("rollen.fxml"); }
    @FXML private void zuVeranstaltungen() throws IOException { SceneManager.switchTo("veranstaltungen.fxml"); }
    @FXML private void zuBeitraege() throws IOException { SceneManager.switchTo("beitraege.fxml"); }
}