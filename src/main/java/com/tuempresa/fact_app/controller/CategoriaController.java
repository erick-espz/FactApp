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

    private static final ObservableList<Categoria> categorias = FXCollections.observableArrayList();
    private static int contadorId = 1;

    @FXML
    private void initialize() {
        tblCategorias.setItems(categorias);
        chkActivo.setSelected(true);

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colActivo.setCellValueFactory(new PropertyValueFactory<>("activo"));

        tblCategorias.getSelectionModel().selectedItemProperty().addListener((obs, anterior, seleccionada) -> {
            if (seleccionada != null) {
                txtId.setText(String.valueOf(seleccionada.getId()));
                txtNombre.setText(seleccionada.getNombre());
                chkActivo.setSelected(seleccionada.isActivo());
            }
        });
    }

    @FXML
    private void guardar() {
        if (txtNombre.getText().isBlank()) {
            new Alert(Alert.AlertType.WARNING, "Ingrese el nombre de la categoría.", ButtonType.OK).showAndWait();
            return;
        }

        Categoria seleccionada = tblCategorias.getSelectionModel().getSelectedItem();

        if (seleccionada != null) {
            seleccionada.setNombre(txtNombre.getText().trim());
            seleccionada.setActivo(chkActivo.isSelected());
            tblCategorias.refresh();
        } else {
            categorias.add(new Categoria(contadorId++, txtNombre.getText().trim(), chkActivo.isSelected()));
        }

        limpiar();
    }

    @FXML
    private void limpiar() {
        tblCategorias.getSelectionModel().clearSelection();
        txtId.clear();
        txtNombre.clear();
        chkActivo.setSelected(true);
    }

    @FXML
    private void cerrar() {
        ((Stage) txtNombre.getScene().getWindow()).close();
    }
    @FXML
    public static ObservableList<Categoria> getCategorias() {
        return categorias;
    }
}