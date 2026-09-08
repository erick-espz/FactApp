module com.tuempresa.fact_app {
    requires javafx.controls;
    requires javafx.fxml;
    requires static lombok;


    opens com.tuempresa.fact_app to javafx.fxml;
    exports com.tuempresa.fact_app;
}