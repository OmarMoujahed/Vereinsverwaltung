package com.vereinsverwaltung.frontend.controller;

import com.vereinsverwaltung.frontend.ApiService;
import com.vereinsverwaltung.frontend.SceneManager;
import com.vereinsverwaltung.frontend.model.Gruppe;
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

public class GruppenController {

    @FXML private TableView<Gruppe> gruppenTabelle;
    @FXML private TableColumn<Gruppe, String> nameColumn;
    @FXML private TableColumn<Gruppe, String> vereinColumn;
    @FXML private TableColumn<Gruppe, String> mitgliederColumn;
    @FXML private TableColumn<Gruppe, String> aktionenColumn;
    @FXML private TextField sucheField;
    @FXML private ComboBox<Verein> vereinFilter;

    private ObservableList<Gruppe> gruppenListe = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));

        vereinColumn.setCellValueFactory(c -> {
            Gruppe g = c.getValue();
            if (g.getVerein() == null) return new SimpleStringProperty("-");
            return new SimpleStringProperty(g.getVerein().getName());
        });

        mitgliederColumn.setCellValueFactory(c -> {
            try {
                int anzahl = ApiService.mitgliederVonVerein(c.getValue().getVerein().getVerein_id())
                        .stream()
                        .filter(m -> m.getGruppen() != null && m.getGruppen().stream()
                                .anyMatch(gr -> gr.getGruppe_id().equals(c.getValue().getGruppe_id())))
                        .toList().size();
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
                    Gruppe g = getTableView().getItems().get(getIndex());
                    gruppeBearbeiten(g);
                });

                loeschenBtn.setOnAction(e -> {
                    Gruppe g = getTableView().getItems().get(getIndex());
                    gruppeLoeschen(g);
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
                    gruppenTabelle.setItems(gruppenListe);
                } else {
                    try {
                        List<Gruppe> gefiltert = ApiService.gruppenVonVerein(neu.getVerein_id());
                        gruppenTabelle.setItems(FXCollections.observableArrayList(gefiltert));
                    } catch (Exception e) {
                        System.out.println("Fehler beim Filtern: " + e.getMessage());
                    }
                }
            });
        } catch (Exception e) {
            System.out.println("Fehler beim Laden der Vereine: " + e.getMessage());
        }

        FilteredList<Gruppe> filteredList = new FilteredList<>(gruppenListe, p -> true);
        sucheField.textProperty().addListener((obs, alt, neu) -> {
            filteredList.setPredicate(g -> {
                if (neu == null || neu.isEmpty()) return true;
                return g.getName().toLowerCase().contains(neu.toLowerCase());
            });
        });
        gruppenTabelle.setItems(filteredList);

        try {
            List<Gruppe> gruppen = ApiService.alleGruppen();
            gruppenListe.setAll(gruppen);
        } catch (Exception e) {
            System.out.println("Fehler beim Laden der Gruppen: " + e.getMessage());
        }
    }

    @FXML
    private void gruppeHinzufuegen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/vereinsverwaltung/frontend/gruppenformular.fxml"));
            VBox root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Neue Gruppe");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            List<Gruppe> gruppen = ApiService.alleGruppen();
            gruppenListe.setAll(gruppen);

            if (vereinFilter.getValue() != null) {
                List<Gruppe> gefiltert = ApiService.gruppenVonVerein(vereinFilter.getValue().getVerein_id());
                gruppenTabelle.setItems(FXCollections.observableArrayList(gefiltert));
            }
        } catch (Exception e) {
            System.out.println("Fehler: " + e.getMessage());
        }
    }

    private void gruppeBearbeiten(Gruppe gruppe) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/vereinsverwaltung/frontend/gruppenformular.fxml"));
            VBox root = loader.load();
            GruppeFormularController controller = loader.getController();
            controller.setGruppe(gruppe);
            Stage stage = new Stage();
            stage.setTitle("Gruppe bearbeiten");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            List<Gruppe> gruppen = ApiService.alleGruppen();
            gruppenListe.setAll(gruppen);

            if (vereinFilter.getValue() != null) {
                List<Gruppe> gefiltert = ApiService.gruppenVonVerein(vereinFilter.getValue().getVerein_id());
                gruppenTabelle.setItems(FXCollections.observableArrayList(gefiltert));
            }
        } catch (Exception e) {
            System.out.println("Fehler: " + e.getMessage());
        }
    }

    private void gruppeLoeschen(Gruppe gruppe) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Gruppe löschen");
        alert.setHeaderText(gruppe.getName() + " löschen?");
        alert.setContentText("Diese Aktion kann nicht rückgängig gemacht werden.");

        alert.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                try {
                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create("http://localhost:8080/api/gruppen/" + gruppe.getGruppe_id()))
                            .DELETE()
                            .build();
                    HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
                    List<Gruppe> gruppen = ApiService.alleGruppen();
                    gruppenListe.setAll(gruppen);
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