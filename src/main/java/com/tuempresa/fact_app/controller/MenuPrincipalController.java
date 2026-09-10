package com.tuempresa.fact_app.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import com.tuempresa.fact_app.util.SceneManager;
import java.io.IOException;

public class MenuPrincipalController {
    @FXML
    private void abrirProductos() {
        try {
            // Nota: Verifica que tu archivo se llame producto-view.fxml (sin doble punto)
            SceneManager.abrirVentana(
                    "/com/tuempresa/fact_app/fxml/producto-view.fxml",
                    "Gestión de productos");
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR,
                    "No fue posible abrir Productos: " + e.getMessage()).showAndWait();
        }
    }

    @FXML
    private void salir() {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Desea cerrar la aplicación?", ButtonType.OK, ButtonType.CANCEL);
        if (a.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK)
            Platform.exit();
    }
}