module com.vereinsverwaltung.frontend {
    requires javafx.controls;
    requires javafx.fxml;
    requires atlantafx.base;


    opens com.vereinsverwaltung.frontend to javafx.fxml;
    exports com.vereinsverwaltung.frontend;
}