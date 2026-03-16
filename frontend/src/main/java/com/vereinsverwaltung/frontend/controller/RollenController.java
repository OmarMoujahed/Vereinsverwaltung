package com.vereinsverwaltung.frontend.controller;

import com.vereinsverwaltung.frontend.SceneManager;
import javafx.fxml.FXML;

import java.io.IOException;

public class RollenController {
    @FXML
    public void zuHome() throws IOException {
        SceneManager.switchTo("dashboard.fxml");
    }

    @FXML
    public void zuMitglieder() throws IOException {
        SceneManager.switchTo("mitglieder.fxml");
    }

    @FXML
    public void zuVereine() throws IOException {
        SceneManager.switchTo("vereine.fxml");
    }

    @FXML
    public void zuGruppen() throws IOException {
        SceneManager.switchTo("gruppen.fxml");
    }

    @FXML
    public void zuRollen() throws IOException {
        SceneManager.switchTo("rollen.fxml");
    }

    @FXML
    public void zuVeranstaltungen() throws IOException {
        SceneManager.switchTo("veranstaltungen.fxml");
    }

    @FXML
    public void zuBeitraege() throws IOException {
        SceneManager.switchTo("beitraege.fxml");
    }

}