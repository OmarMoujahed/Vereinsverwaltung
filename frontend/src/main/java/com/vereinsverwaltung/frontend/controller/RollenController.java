package com.vereinsverwaltung.frontend.controller;

import com.vereinsverwaltung.frontend.ApiService;
import com.vereinsverwaltung.frontend.SceneManager;
import com.vereinsverwaltung.frontend.model.Rolle;
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

public class RollenController {

    @FXML private TableView<Rolle> rollenTabelle;
    @FXML private TableColumn<Rolle, String> nameColumn;
    @FXML private TableColumn<Rolle, String> vereinColumn;
    @FXML private TableColumn<Rolle, String> mitgliederColumn;
    @FXML private TableColumn<Rolle, String> aktionenColumn;
    @FXML private TextField sucheField;
    @FXML private ComboBox<Verein> vereinFilter;
    @FXML private TableColumn<Rolle, String> idColumn;
    @FXML private TableColumn<Rolle, String> beschreibungColumn;
    @FXML private TableColumn<Rolle, String> istGlobalColumn;

    private ObservableList<Rolle> rollenListe = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));

        vereinColumn.setCellValueFactory(c -> {
            Rolle r = c.getValue();
            if (r.isIstGlobal()) return new SimpleStringProperty("Global");
            if (r.getVerein() == null) return new SimpleStringProperty("-");
            return new SimpleStringProperty(r.getVerein().getName());
        });

        mitgliederColumn.setCellValueFactory(c -> {
            try {
                long anzahl = ApiService.alleMitglieder().stream()
                        .filter(m -> m.getRolle() != null && m.getRolle().getRolle_id().equals(c.getValue().getRolle_id()))
                        .count();
                return new SimpleStringProperty(String.valueOf(anzahl));
            } catch (Exception e) {
                return new SimpleStringProperty("-");
            }
        });
        idColumn.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getRolle_id() != null ? c.getValue().getRolle_id().toString() : "-"));

        beschreibungColumn.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getBeschreibung() != null ? c.getValue().getBeschreibung() : "-"));

        istGlobalColumn.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().isIstGlobal() ? "Ja" : "Nein"));

        aktionenColumn.setCellFactory(col -> new TableCell<>() {
            private final Button loeschenBtn = new Button("✕");
            private final Button bearbeitenBtn = new Button("✎");
            private final HBox box = new HBox(4, bearbeitenBtn, loeschenBtn);

            {
                bearbeitenBtn.getStyleClass().add("flat");
                loeschenBtn.getStyleClass().add("flat");

                bearbeitenBtn.setOnAction(e -> {
                    Rolle r = getTableView().getItems().get(getIndex());
                    rolleBearbeiten(r);
                });

                loeschenBtn.setOnAction(e -> {
                    Rolle r = getTableView().getItems().get(getIndex());
                    rolleLoeschen(r);
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
                    rollenTabelle.setItems(rollenListe);
                } else {
                    try {
                        List<Rolle> gefiltert = ApiService.rollenVonVerein(neu.getVerein_id());
                        rollenTabelle.setItems(FXCollections.observableArrayList(gefiltert));
                    } catch (Exception e) {
                        System.out.println("Fehler beim Filtern: " + e.getMessage());
                    }
                }
            });
        } catch (Exception e) {
            System.out.println("Fehler beim Laden der Vereine: " + e.getMessage());
        }

        FilteredList<Rolle> filteredList = new FilteredList<>(rollenListe, p -> true);
        sucheField.textProperty().addListener((obs, alt, neu) -> {
            filteredList.setPredicate(r -> {
                if (neu == null || neu.isEmpty()) return true;
                return r.getName().toLowerCase().contains(neu.toLowerCase());
            });
        });
        rollenTabelle.setItems(filteredList);

        try {
            List<Rolle> rollen = ApiService.alleRollen();
            rollenListe.setAll(rollen);
        } catch (Exception e) {
            System.out.println("Fehler beim Laden der Rollen: " + e.getMessage());
        }
    }

    @FXML
    private void rolleHinzufuegen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/vereinsverwaltung/frontend/rollenformular.fxml"));
            VBox root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Neue Rolle");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            List<Rolle> rollen = ApiService.alleRollen();
            rollenListe.setAll(rollen);
            if (vereinFilter.getValue() != null) {
                List<Rolle> gefiltert = ApiService.rollenVonVerein(vereinFilter.getValue().getVerein_id());
                rollenTabelle.setItems(FXCollections.observableArrayList(gefiltert));
            }
        } catch (Exception e) {
            System.out.println("Fehler: " + e.getMessage());
        }
    }

    private void rolleBearbeiten(Rolle rolle) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/vereinsverwaltung/frontend/rollenformular.fxml"));
            VBox root = loader.load();
            RolleFormularController controller = loader.getController();
            controller.setRolle(rolle);
            Stage stage = new Stage();
            stage.setTitle("Rolle bearbeiten");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            List<Rolle> rollen = ApiService.alleRollen();
            rollenListe.setAll(rollen);
            if (vereinFilter.getValue() != null) {
                List<Rolle> gefiltert = ApiService.rollenVonVerein(vereinFilter.getValue().getVerein_id());
                rollenTabelle.setItems(FXCollections.observableArrayList(gefiltert));
            }
        } catch (Exception e) {
            System.out.println("Fehler: " + e.getMessage());
        }
    }

    private void rolleLoeschen(Rolle rolle) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Rolle löschen");
        alert.setHeaderText(rolle.getName() + " löschen?");
        alert.setContentText("Diese Aktion kann nicht rückgängig gemacht werden.");

        alert.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                try {
                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create("http://localhost:8080/api/rollen/" + rolle.getRolle_id()))
                            .DELETE()
                            .build();
                    HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
                    List<Rolle> rollen = ApiService.alleRollen();
                    rollenListe.setAll(rollen);
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