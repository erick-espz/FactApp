package com.tuempresa.fact_app.controller;

import com.tuempresa.fact_app.model.Categoria;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class CategoriaController {
    @FXML private TextField txtId, txtNombre;
    @FXML private CheckBox chkActivo;
    @FXML private TableView<Categoria> tblCategorias;
    @FXML private TableColumn<Categoria, Integer> colId;
    @FXML private TableColumn<Categoria, String> colNombre;
    @FXML private TableColumn<Categoria, Boolean> colActivo;

    private final ObservableList<Categoria> categorias = FXCollections.observableArrayList();
    private int contadorId = 1;

    @FXML
    private void initialize() {
        tblCategorias.setItems(categorias);
        chkActivo.setSelected(true);

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colActivo.setCellValueFactory(new PropertyValueFactory<>("activo"));
    }

    @FXML
    private void guardar() {
        if (txtNombre.getText().isBlank()) {
            new Alert(Alert.AlertType.WARNING, "Ingrese el nombre de la categoría.", ButtonType.OK).showAndWait();
            return;
        }
        categorias.add(new Categoria(contadorId++, txtNombre.getText().trim(), chkActivo.isSelected()));
        limpiar();
    }

    @FXML
    private void limpiar() {
        txtId.clear();
        txtNombre.clear();
        chkActivo.setSelected(true);
    }

    @FXML
    private void cerrar() {
        ((Stage) txtNombre.getScene().getWindow()).close();
    }
}