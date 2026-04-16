package com.vereinsverwaltung.frontend;

import atlantafx.base.theme.PrimerDark;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Application.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet());

        SceneManager.setStage(stage);
        stage.setTitle("Vereinsverwaltung");
        stage.setWidth(1200);
        stage.setHeight(800);
        stage.show();

        SceneManager.switchTo("dashboard.fxml");
    }

    @Override
    public void stop() throws Exception {
        Process p = new ProcessBuilder("bash", "-c", "lsof -ti :8080")
                .start();
        String pid = new String(p.getInputStream().readAllBytes()).trim();
        if (!pid.isEmpty()) {
            new ProcessBuilder("kill", "-9", pid).start().waitFor();
        }
        super.stop();
    }
}