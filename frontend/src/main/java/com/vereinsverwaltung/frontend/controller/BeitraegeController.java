package com.vereinsverwaltung.frontend.controller;

import com.vereinsverwaltung.frontend.ApiService;
import com.vereinsverwaltung.frontend.SceneManager;
import com.vereinsverwaltung.frontend.model.BeitragsStatus;
import com.vereinsverwaltung.frontend.model.Mitgliedsbeitrag;
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

public class BeitraegeController {

    @FXML private TableView<Mitgliedsbeitrag> beitraegeTabelle;
    @FXML private TableColumn<Mitgliedsbeitrag, String> idColumn;
    @FXML private TableColumn<Mitgliedsbeitrag, String> nameColumn;
    @FXML private TableColumn<Mitgliedsbeitrag, String> vereinColumn;
    @FXML private TableColumn<Mitgliedsbeitrag, String> betragColumn;
    @FXML private TableColumn<Mitgliedsbeitrag, String> zeitraumColumn;
    @FXML private TableColumn<Mitgliedsbeitrag, String> faelligkeitColumn;
    @FXML private TableColumn<Mitgliedsbeitrag, String> statusColumn;
    @FXML private TableColumn<Mitgliedsbeitrag, String> aktionenColumn;
    @FXML private TextField sucheField;
    @FXML private ComboBox<Verein> vereinFilter;
    @FXML private ComboBox<String> statusFilter;
    @FXML private Label gesamtLabel;
    @FXML private Label eingegangenLabel;
    @FXML private Label offenLabel;

    private ObservableList<Mitgliedsbeitrag> beitraegeListe = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getId() != null ? c.getValue().getId().toString() : "-"));

        nameColumn.setCellValueFactory(c -> {
            Mitgliedsbeitrag b = c.getValue();
            if (b.getMitglied() == null) return new SimpleStringProperty("-");
            return new SimpleStringProperty(b.getMitglied().getVorname() + " " + b.getMitglied().getNachname());
        });

        vereinColumn.setCellValueFactory(c -> {
            Mitgliedsbeitrag b = c.getValue();
            if (b.getMitglied() == null || b.getMitglied().getVerein() == null) return new SimpleStringProperty("-");
            return new SimpleStringProperty(b.getMitglied().getVerein().getName());
        });

        betragColumn.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getBetrag() != null ? c.getValue().getBetrag() + " €" : "-"));

        zeitraumColumn.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getZeitraum() != null ? c.getValue().getZeitraum() : "-"));

        faelligkeitColumn.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getFaelligkeitsdatum() != null ? c.getValue().getFaelligkeitsdatum().toString() : "-"));

        statusColumn.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getStatus() != null ? c.getValue().getStatus().toString() : "-"));

        aktionenColumn.setCellFactory(col -> new TableCell<>() {
            private final Button loeschenBtn = new Button("✕");
            private final Button bearbeitenBtn = new Button("✎");
            private final HBox box = new HBox(4, bearbeitenBtn, loeschenBtn);

            {
                bearbeitenBtn.getStyleClass().add("flat");
                loeschenBtn.getStyleClass().add("flat");

                bearbeitenBtn.setOnAction(e -> {
                    Mitgliedsbeitrag b = getTableView().getItems().get(getIndex());
                    beitragBearbeiten(b);
                });

                loeschenBtn.setOnAction(e -> {
                    Mitgliedsbeitrag b = getTableView().getItems().get(getIndex());
                    beitragLoeschen(b);
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
            vereinFilter.valueProperty().addListener((obs, alt, neu) -> ladeBeitraege());
        } catch (Exception e) {
            System.out.println("Fehler beim Laden der Vereine: " + e.getMessage());
        }

        statusFilter.setItems(FXCollections.observableArrayList("OFFEN", "BEZAHLT", "UEBERFAELLIG"));
        statusFilter.valueProperty().addListener((obs, alt, neu) -> ladeBeitraege());

        FilteredList<Mitgliedsbeitrag> filteredList = new FilteredList<>(beitraegeListe, p -> true);
        sucheField.textProperty().addListener((obs, alt, neu) -> {
            filteredList.setPredicate(b -> {
                if (neu == null || neu.isEmpty()) return true;
                String lower = neu.toLowerCase();
                if (b.getMitglied() == null) return false;
                return (b.getMitglied().getVorname() + " " + b.getMitglied().getNachname()).toLowerCase().contains(lower);
            });
        });
        beitraegeTabelle.setItems(filteredList);

        ladeBeitraege();
    }

    private void ladeBeitraege() {
        try {
            List<Mitgliedsbeitrag> beitraege;
            if (statusFilter.getValue() != null) {
                beitraege = ApiService.beitraegeNachStatus(statusFilter.getValue());
            } else {
                beitraege = ApiService.alleBeitraege();
            }

            if (vereinFilter.getValue() != null) {
                beitraege = beitraege.stream()
                        .filter(b -> b.getMitglied() != null &&
                                b.getMitglied().getVerein() != null &&
                                b.getMitglied().getVerein().getVerein_id().equals(vereinFilter.getValue().getVerein_id()))
                        .toList();
            }

            beitraegeListe.setAll(beitraege);
            aktualisiereStatistiken(beitraege);
        } catch (Exception e) {
            System.out.println("Fehler beim Laden der Beiträge: " + e.getMessage());
        }
    }

    private void aktualisiereStatistiken(List<Mitgliedsbeitrag> beitraege) {
        double gesamt = beitraege.stream()
                .filter(b -> b.getBetrag() != null)
                .mapToDouble(b -> b.getBetrag())
                .sum();

        double eingegangen = beitraege.stream()
                .filter(b -> b.getStatus() == BeitragsStatus.BEZAHLT && b.getBetrag() != null)
                .mapToDouble(b -> b.getBetrag())
                .sum();

        double offen = beitraege.stream()
                .filter(b -> b.getStatus() == BeitragsStatus.OFFEN && b.getBetrag() != null)
                .mapToDouble(b -> b.getBetrag())
                .sum();

        gesamtLabel.setText(String.format("%.2f €", gesamt));
        eingegangenLabel.setText(String.format("%.2f €", eingegangen));
        offenLabel.setText(String.format("%.2f €", offen));
    }

    @FXML
    private void beitragHinzufuegen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/vereinsverwaltung/frontend/beitragformular.fxml"));
            VBox root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Neuer Beitrag");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            ladeBeitraege();
        } catch (Exception e) {
            System.out.println("Fehler: " + e.getMessage());
        }
    }

    private void beitragBearbeiten(Mitgliedsbeitrag beitrag) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/vereinsverwaltung/frontend/beitragformular.fxml"));
            VBox root = loader.load();
            BeitragFormularController controller = loader.getController();
            controller.setBeitrag(beitrag);
            Stage stage = new Stage();
            stage.setTitle("Beitrag bearbeiten");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            ladeBeitraege();
        } catch (Exception e) {
            System.out.println("Fehler: " + e.getMessage());
        }
    }

    private void beitragLoeschen(Mitgliedsbeitrag beitrag) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Beitrag löschen");
        alert.setHeaderText("Beitrag löschen?");
        alert.setContentText("Diese Aktion kann nicht rückgängig gemacht werden.");

        alert.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                try {
                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create("http://localhost:8080/api/beitraege/" + beitrag.getId()))
                            .DELETE()
                            .build();
                    HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
                    ladeBeitraege();
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