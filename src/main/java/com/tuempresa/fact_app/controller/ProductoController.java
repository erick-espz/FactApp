package com.tuempresa.fact_app.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import com.tuempresa.fact_app.model.Categoria;
import com.tuempresa.fact_app.model.Producto;

public class ProductoController {

    private static final String CARPETA_IMAGENES =
            "src/main/resources/com/tuempresa/fact_app/image";

    @FXML private TextField txtCodigo, txtNombre, txtPrecio, txtExistencia;
    @FXML private ComboBox<Categoria> cmbCategoria;
    @FXML private CheckBox chkActivo;
    @FXML private ImageView imgProducto;

    @FXML private TableView<Producto> tblProductos;
    @FXML private TableColumn<Producto, String> colCodigo;
    @FXML private TableColumn<Producto, String> colNombre;
    @FXML private TableColumn<Producto, Categoria> colCategoria;
    @FXML private TableColumn<Producto, BigDecimal> colPrecio;
    @FXML private TableColumn<Producto, Integer> colExistencia;
    @FXML private TableColumn<Producto, Boolean> colActivo;

    @FXML private Button btnGuardar, btnEliminar;

    private static final ObservableList<Producto> productos = FXCollections.observableArrayList();
    private static int contadorId = 1;
    private String rutaImagen;

    @FXML
    private void initialize() {
        cmbCategoria.setItems(CategoriaController.getCategorias());

        tblProductos.setItems(productos);
        chkActivo.setSelected(true);

        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precioVenta"));
        colExistencia.setCellValueFactory(new PropertyValueFactory<>("existencia"));
        colActivo.setCellValueFactory(new PropertyValueFactory<>("activo"));

        // Deshabilitar botón eliminar al inicio
        btnEliminar.setDisable(true);

        tblProductos.getSelectionModel().selectedItemProperty().addListener((obs, anterior, seleccionado) -> {
            if (seleccionado != null) {
                txtCodigo.setText(seleccionado.getCodigo());
                txtNombre.setText(seleccionado.getNombre());
                cmbCategoria.setValue(seleccionado.getCategoria());
                txtPrecio.setText(seleccionado.getPrecioVenta().toString());
                txtExistencia.setText(String.valueOf(seleccionado.getExistencia()));
                chkActivo.setSelected(seleccionado.isActivo());

                rutaImagen = seleccionado.getRutaImagen();
                imgProducto.setImage(rutaImagen != null ? new Image(rutaImagen) : null);

                // Habilitar botón eliminar cuando hay selección
                btnEliminar.setDisable(false);
            }
        });

        // Atajos de teclado
        tblProductos.setOnKeyPressed(this::onTeclaPresionada);
    }

    private void onTeclaPresionada(KeyEvent event) {
        if (event.getCode() == javafx.scene.input.KeyCode.DELETE ||
                event.getCode() == javafx.scene.input.KeyCode.BACK_SPACE) {
            eliminar();
            event.consume();
        }
    }

    @FXML
    private void seleccionarImagen() {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg"));
        File archivoOriginal = chooser.showOpenDialog(txtCodigo.getScene().getWindow());

        if (archivoOriginal == null) {
            return;
        }

        try {
            File carpetaDestino = new File(CARPETA_IMAGENES);
            if (!carpetaDestino.exists()) {
                carpetaDestino.mkdirs();
            }

            String nombreUnico = System.currentTimeMillis() + "_" + archivoOriginal.getName();
            Path destino = carpetaDestino.toPath().resolve(nombreUnico);

            Files.copy(archivoOriginal.toPath(), destino, StandardCopyOption.REPLACE_EXISTING);

            rutaImagen = destino.toUri().toString();
            imgProducto.setImage(new Image(rutaImagen));

        } catch (IOException e) {
            mensaje(Alert.AlertType.ERROR, "No se pudo copiar la imagen: " + e.getMessage());
        }
    }

    @FXML
    private void guardar() {
        // Limpiar errores previos
        limpiarErrores();

        boolean hayError = false;

        // Validar campos vacíos
        if (txtCodigo.getText().isBlank()) {
            txtCodigo.getStyleClass().add("error");
            hayError = true;
        }
        if (txtNombre.getText().isBlank()) {
            txtNombre.getStyleClass().add("error");
            hayError = true;
        }
        if (cmbCategoria.getValue() == null) {
            cmbCategoria.getStyleClass().add("error");
            hayError = true;
        }
        if (txtPrecio.getText().isBlank()) {
            txtPrecio.getStyleClass().add("error");
            hayError = true;
        }
        if (txtExistencia.getText().isBlank()) {
            txtExistencia.getStyleClass().add("error");
            hayError = true;
        }

        if (hayError) {
            mensaje(Alert.AlertType.WARNING, "Complete los campos vacios.");
            return;
        }

        // Validar valores numéricos
        try {
            BigDecimal precio = new BigDecimal(txtPrecio.getText().trim());
            int existencia = Integer.parseInt(txtExistencia.getText().trim());

            if (precio.signum() <= 0) {
                txtPrecio.getStyleClass().add("error");
                mensaje(Alert.AlertType.WARNING, "Precio debe ser mayor a cero.");
                return;
            }

            if (existencia < 0) {
                txtExistencia.getStyleClass().add("error");
                mensaje(Alert.AlertType.WARNING, "Existencia no puede ser negativa.");
                return;
            }

            Producto seleccionado = tblProductos.getSelectionModel().getSelectedItem();

            if (seleccionado != null) {
                seleccionado.setCodigo(txtCodigo.getText().trim());
                seleccionado.setNombre(txtNombre.getText().trim());
                seleccionado.setCategoria(cmbCategoria.getValue());
                seleccionado.setPrecioVenta(precio);
                seleccionado.setExistencia(existencia);
                seleccionado.setRutaImagen(rutaImagen);
                seleccionado.setActivo(chkActivo.isSelected());
                tblProductos.refresh();
                mensaje(Alert.AlertType.INFORMATION, "Producto actualizado correctamente.");
            } else {
                productos.add(new Producto(
                        contadorId++,
                        txtCodigo.getText().trim(),
                        txtNombre.getText().trim(),
                        cmbCategoria.getValue(),
                        precio,
                        existencia,
                        rutaImagen,
                        chkActivo.isSelected()
                ));
                mensaje(Alert.AlertType.INFORMATION, "Producto agregado correctamente.");
            }

            limpiar();
        } catch (NumberFormatException e) {
            txtPrecio.getStyleClass().add("error");
            txtExistencia.getStyleClass().add("error");
            mensaje(Alert.AlertType.ERROR, "Precio o existencia no válidos.");
        }
    }

    @FXML
    private void eliminar() {
        Producto seleccionado = tblProductos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mensaje(Alert.AlertType.WARNING, "Selecciona un producto para eliminar.");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Eliminar '" + seleccionado.getNombre() + "'?",
                ButtonType.OK, ButtonType.CANCEL);
        if (confirmacion.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            productos.remove(seleccionado);
            limpiar();
            mensaje(Alert.AlertType.INFORMATION, "Producto eliminado.");
        }
    }

    @FXML
    private void cerrar() {
        ((Stage) txtCodigo.getScene().getWindow()).close();
    }

    private void limpiar() {
        tblProductos.getSelectionModel().clearSelection();
        txtCodigo.clear();
        txtNombre.clear();
        txtPrecio.clear();
        txtExistencia.clear();
        cmbCategoria.getSelectionModel().clearSelection();
        chkActivo.setSelected(true);
        imgProducto.setImage(null);
        rutaImagen = null;

        // Deshabilitar botón eliminar cuando limpias
        btnEliminar.setDisable(true);
    }

    private void limpiarErrores() {
        txtCodigo.getStyleClass().remove("error");
        txtNombre.getStyleClass().remove("error");
        cmbCategoria.getStyleClass().remove("error");
        txtPrecio.getStyleClass().remove("error");
        txtExistencia.getStyleClass().remove("error");
    }

    private void mensaje(Alert.AlertType tipo, String texto) {
        new Alert(tipo, texto, ButtonType.OK).showAndWait();
    }
}