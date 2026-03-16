package com.vereinsverwaltung.frontend.controller;

import com.vereinsverwaltung.frontend.SceneManager;
import javafx.fxml.FXML;

import java.io.IOException;

public class DashboardController {
    @FXML
    private void zuHome() throws IOException {
        SceneManager.switchTo("dashboard.fxml");
    }

    @FXML
    private void zuMitglieder() throws IOException {
        SceneManager.switchTo("mitglieder.fxml");
    }

    @FXML
    private void zuVereine() throws IOException {
        SceneManager.switchTo("vereine.fxml");
    }

    @FXML
    private void zuGruppen() throws IOException {
        SceneManager.switchTo("gruppen.fxml");
    }

    @FXML
    private void zuRollen() throws IOException {
        SceneManager.switchTo("rollen.fxml");
    }

    @FXML
    private void zuVeranstaltungen() throws IOException {
        SceneManager.switchTo("veranstaltungen.fxml");
    }

    @FXML
    private void zuBeitraege() throws IOException {
        SceneManager.switchTo("beitraege.fxml");
    }
}