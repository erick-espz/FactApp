package com.tuempresa.fact_app.controller;

import com.tuempresa.fact_app.model.Cargo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class CargoController {
    @FXML private TextField txtId, txtNombre;
    @FXML private TableView<Cargo> tblCargos;
    @FXML private TableColumn<Cargo, Integer> colId;
    @FXML private TableColumn<Cargo, String> colNombre;

    private final ObservableList<Cargo> cargos = FXCollections.observableArrayList();
    private int contadorId = 1;

    @FXML
    private void initialize() {
        tblCargos.setItems(cargos);
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
    }

    @FXML
    private void guardar() {
        if (txtNombre.getText().isBlank()) {
            new Alert(Alert.AlertType.WARNING, "Ingrese el nombre del cargo.", ButtonType.OK).showAndWait();
            return;
        }
        cargos.add(new Cargo(contadorId++, txtNombre.getText().trim()));
        limpiar();
    }

    @FXML
    private void limpiar() {
        txtId.clear();
        txtNombre.clear();
    }

    @FXML
    private void cerrar() {
        ((Stage) txtNombre.getScene().getWindow()).close();
    }
}