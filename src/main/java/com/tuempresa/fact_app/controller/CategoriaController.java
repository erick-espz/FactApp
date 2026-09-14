package com.tuempresa.fact_app.controller;

import com.tuempresa.fact_app.model.Categoria;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import lombok.Getter;

public class CategoriaController {
    @FXML private TextField txtId, txtNombre;
    @FXML private CheckBox chkActivo;
    @FXML private TableView<Categoria> tblCategorias;
    @FXML private TableColumn<Categoria, Integer> colId;
    @FXML private TableColumn<Categoria, String> colNombre;
    @FXML private TableColumn<Categoria, Boolean> colActivo;
    @FXML private Button btnGuardar, btnEliminar;



    @FXML
    private void initialize() {
        tblCategorias.setItems(categorias);
        chkActivo.setSelected(true);

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colActivo.setCellValueFactory(new PropertyValueFactory<>("activo"));

        // Deshabilitar botón eliminar al inicio
        btnEliminar.setDisable(true);

        tblCategorias.getSelectionModel().selectedItemProperty().addListener((obs, anterior, seleccionada) -> {
            if (seleccionada != null) {
                txtId.setText(String.valueOf(seleccionada.getId()));
                txtNombre.setText(seleccionada.getNombre());
                chkActivo.setSelected(seleccionada.isActivo());
                btnEliminar.setDisable(false); // Habilitar cuando hay selección
            }
        });

        // Atajos de teclado
        tblCategorias.setOnKeyPressed(this::onTeclaPresionada);
    }

    private void onTeclaPresionada(KeyEvent event) {
        if (event.getCode() == KeyCode.DELETE) {
            eliminar();
            event.consume();
        }
    }

    @Getter
    private static final ObservableList<Categoria> categorias = FXCollections.observableArrayList();
    private static int contadorId = 4; // Empieza en 4 porque ya usamos 1, 2, 3

    static {
        categorias.addAll(
                new Categoria(1, "Alimentos", true),
                new Categoria(2, "Bebidas", true),
                new Categoria(3, "Limpieza", true)
        );
    }

    @FXML
    private void guardar() {
        limpiarErrores();

        if (txtNombre.getText().isBlank()) {
            txtNombre.getStyleClass().add("error");
            new Alert(Alert.AlertType.WARNING, "Ingrese el nombre de la categoría.").showAndWait();
            return;
        }

        Categoria seleccionada = tblCategorias.getSelectionModel().getSelectedItem();

        if (seleccionada != null) {
            seleccionada.setNombre(txtNombre.getText().trim());
            seleccionada.setActivo(chkActivo.isSelected());
            tblCategorias.refresh();
            new Alert(Alert.AlertType.INFORMATION, "Categoría actualizada.").showAndWait();
        } else {
            categorias.add(new Categoria(contadorId++, txtNombre.getText().trim(), chkActivo.isSelected()));
            new Alert(Alert.AlertType.INFORMATION, "Categoría agregada.").showAndWait();
        }

        limpiar();
    }

    @FXML
    private void eliminar() {
        Categoria seleccionada = tblCategorias.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            new Alert(Alert.AlertType.WARNING, "Selecciona una categoría para eliminar.").showAndWait();
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Eliminar '" + seleccionada.getNombre() + "'?",
                ButtonType.OK, ButtonType.CANCEL);
        if (confirmacion.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            categorias.remove(seleccionada);
            limpiar();
            new Alert(Alert.AlertType.INFORMATION, "Categoría eliminada.").showAndWait();
        }
    }

    private void limpiar() {
        tblCategorias.getSelectionModel().clearSelection();
        txtId.clear();
        txtNombre.clear();
        chkActivo.setSelected(true);
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