module com.vereinsverwaltung.frontend {
    requires javafx.controls;
    requires javafx.fxml;
    requires atlantafx.base;
    requires com.google.gson;
    requires java.net.http;

    exports com.vereinsverwaltung.frontend;
    exports com.vereinsverwaltung.frontend.controller to javafx.fxml;
    opens com.vereinsverwaltung.frontend to javafx.fxml;
    opens com.vereinsverwaltung.frontend.controller to javafx.fxml;
}