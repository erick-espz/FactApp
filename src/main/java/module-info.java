module com.tuempresa.fact_app {
    requires javafx.controls;
    requires javafx.fxml;
    requires static lombok;

    opens com.tuempresa.fact_app.application to javafx.fxml, javafx.graphics;
    opens com.tuempresa.fact_app.controller to javafx.fxml;
    opens com.tuempresa.fact_app.model to javafx.base;

    exports com.tuempresa.fact_app.application;
    exports com.tuempresa.fact_app.controller;
    exports com.tuempresa.fact_app.model;
}