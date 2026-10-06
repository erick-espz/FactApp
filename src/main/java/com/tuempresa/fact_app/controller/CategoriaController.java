package com.tuempresa.fact_app.controller;

import com.tuempresa.fact_app.dao.CategoriaDAO;
import com.tuempresa.fact_app.model.Categoria;
import com.tuempresa.fact_app.util.Alertas;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.sql.SQLException;

public class CategoriaController {
    @FXML private TextField txtId, txtNombre;
    @FXML private CheckBox chkActivo;
    @FXML private TableView<Categoria> tblCategorias;
    @FXML private TableColumn<Categoria, Integer> colId;
    @FXML private TableColumn<Categoria, String> colNombre;
    @FXML private TableColumn<Categoria, Boolean> colActivo;
    @FXML private Button btnGuardar, btnEliminar;

    private final CategoriaDAO dao = new CategoriaDAO();
    private final ObservableList<Categoria> categorias = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        tblCategorias.setItems(categorias);
        chkActivo.setSelected(true);

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colActivo.setCellValueFactory(new PropertyValueFactory<>("activo"));

        btnEliminar.setDisable(true);

        tblCategorias.getSelectionModel().selectedItemProperty().addListener((obs, anterior, sel) -> {
            if (sel != null) {
                txtId.setText(String.valueOf(sel.getId()));
                txtNombre.setText(sel.getNombre());
                chkActivo.setSelected(sel.isActivo());
                btnEliminar.setDisable(false);
            }
        });

        tblCategorias.setOnKeyPressed(this::onTeclaPresionada);
        Platform.runLater(this::cargarCategorias);
    }

    private void onTeclaPresionada(KeyEvent event) {
        if (event.getCode() == KeyCode.DELETE) {
            eliminar();
            event.consume();
        }
    }

    private void cargarCategorias() {
        try {
            categorias.setAll(dao.listar());
        } catch (SQLException e) {
            Alertas.mostrarError("Error de base de datos", "No fue posible cargar las categorías.");
            System.err.println(e.getMessage());
        }
    }

    /** Valida el nombre. Devuelve true si es correcto. */
    private boolean validarCategoria() {
        String nombre = txtNombre.getText().trim();
        if (nombre.isEmpty()) {
            txtNombre.getStyleClass().add("error");
            Alertas.mostrarError("Validación", "El nombre de la categoría es obligatorio.");
            txtNombre.requestFocus();
            return false;
        }
        return true;
    }

    @FXML
    private void guardar() {
        limpiarErrores();
        if (!validarCategoria()) return;

        String nombre = txtNombre.getText().trim();
        Categoria sel = tblCategorias.getSelectionModel().getSelectedItem();

        try {
            if (sel == null) { // INSERT
                if (dao.existeNombre(nombre)) {
                    nombreDuplicado();
                    return;
                }
                dao.guardar(new Categoria(null, nombre, chkActivo.isSelected()));
                Alertas.mostrarExito("Categoría registrada", "La categoría se guardó correctamente.");
            } else {           // UPDATE (excluye la propia categoría)
                if (dao.existeNombre(nombre, sel.getId())) {
                    nombreDuplicado();
                    return;
                }
                dao.actualizar(new Categoria(sel.getId(), nombre, chkActivo.isSelected()));
                Alertas.mostrarExito("Categoría actualizada", "Los cambios se guardaron correctamente.");
            }
            cargarCategorias();
            limpiar();
        } catch (SQLException e) {
            if ("23505".equals(e.getSQLState())) { // violación de UNIQUE
                nombreDuplicado();
            } else {
                Alertas.mostrarError("Error de base de datos", "No fue posible completar la operación.");
            }
            System.err.println(e.getMessage());
        }
    }

    private void nombreDuplicado() {
        txtNombre.getStyleClass().add("error");
        Alertas.mostrarAdvertencia("Nombre duplicado", "Ya existe una categoría con ese nombre.");
        txtNombre.requestFocus();
    }

    @FXML
    private void eliminar() {
        Categoria sel = tblCategorias.getSelectionModel().getSelectedItem();
        if (sel == null) {
            Alertas.mostrarAdvertencia("Seleccione una categoría",
                    "Debe seleccionar la categoría que desea eliminar.");
            return;
        }
        if (!Alertas.confirmar("Confirmar", "¿Eliminar '" + sel.getNombre() + "'?")) return;

        try {
            if (dao.tieneProductos(sel.getId())) {
                Alertas.mostrarAdvertencia("Operación cancelada",
                        "No puede eliminar la categoría porque tiene productos asociados.");
                return;
            }
            dao.eliminar(sel.getId());
            Alertas.mostrarExito("Categoría eliminada", "La categoría fue eliminada correctamente.");
            cargarCategorias();
            limpiar();
        } catch (SQLException e) {
            if ("23503".equals(e.getSQLState())) { // violación de llave foránea
                Alertas.mostrarAdvertencia("Operación cancelada",
                        "No puede eliminar la categoría porque tiene productos asociados.");
            } else {
                Alertas.mostrarError("Error de base de datos", "No fue posible completar la operación.");
            }
            System.err.println(e.getMessage());
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