package com.tuempresa.fact_app.controller;

import com.tuempresa.fact_app.model.Cargo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class CargoController {
    @FXML private TextField txtId, txtNombre;
    @FXML private TableView<Cargo> tblCargos;
    @FXML private TableColumn<Cargo, Integer> colId;
    @FXML private TableColumn<Cargo, String> colNombre;
    @FXML private Button btnGuardar, btnEliminar;

    private static final ObservableList<Cargo> cargos = FXCollections.observableArrayList();
    private static int contadorId = 1;

    @FXML
    private void initialize() {
        tblCargos.setItems(cargos);
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));

        // Deshabilitar botón eliminar al inicio
        btnEliminar.setDisable(true);

        tblCargos.getSelectionModel().selectedItemProperty().addListener((obs, anterior, seleccionado) -> {
            if (seleccionado != null) {
                txtId.setText(String.valueOf(seleccionado.getId()));
                txtNombre.setText(seleccionado.getNombre());
                btnEliminar.setDisable(false); // Habilitar cuando hay selección
            }
        });

        // Atajos de teclado
        tblCargos.setOnKeyPressed(this::onTeclaPresionada);
    }

    private void onTeclaPresionada(KeyEvent event) {
        if (event.getCode() == KeyCode.DELETE) {
            eliminar();
            event.consume();
        }
    }

    @FXML
    private void guardar() {
        limpiarErrores();

        if (txtNombre.getText().isBlank()) {
            txtNombre.getStyleClass().add("error");
            new Alert(Alert.AlertType.WARNING, "Ingrese el nombre del cargo.").showAndWait();
            return;
        }

        Cargo seleccionado = tblCargos.getSelectionModel().getSelectedItem();

        if (seleccionado != null) {
            seleccionado.setNombre(txtNombre.getText().trim());
            tblCargos.refresh();
            new Alert(Alert.AlertType.INFORMATION, "Cargo actualizado.").showAndWait();
        } else {
            cargos.add(new Cargo(contadorId++, txtNombre.getText().trim()));
            new Alert(Alert.AlertType.INFORMATION, "Cargo agregado.").showAndWait();
        }

        limpiar();
    }

    @FXML
    private void eliminar() {
        Cargo seleccionado = tblCargos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            new Alert(Alert.AlertType.WARNING, "Selecciona un cargo para eliminar.").showAndWait();
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Eliminar '" + seleccionado.getNombre() + "'?",
                ButtonType.OK, ButtonType.CANCEL);
        if (confirmacion.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            cargos.remove(seleccionado);
            limpiar();
            new Alert(Alert.AlertType.INFORMATION, "Cargo eliminado.").showAndWait();
        }
    }

    private void limpiar() {
        tblCargos.getSelectionModel().clearSelection();
        txtId.clear();
        txtNombre.clear();
        btnEliminar.setDisable(true);
    }

    private void limpiarErrores() {
        txtNombre.getStyleClass().remove("error");
    }

    @FXML
    private void cerrar() {
        ((Stage) txtNombre.getScene().getWindow()).close();
    }
}