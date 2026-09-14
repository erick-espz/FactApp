package com.tuempresa.fact_app.application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class FacturacionApplication extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(
                "/com/tuempresa/fact_app/fxml/menu-principal.fxml"));

        Scene scene = new Scene(loader.load(), 900, 600);

        scene.getStylesheets().add(getClass().getResource(
                "/com/tuempresa/fact_app/css/Style.css").toExternalForm());

        stage.setTitle("Sistema de facturación");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}