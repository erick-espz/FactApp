package com.tuempresa.fact_app.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.List;

import com.tuempresa.fact_app.dao.CategoriaDAO;
import com.tuempresa.fact_app.dao.ProductoDAO;
import com.tuempresa.fact_app.model.Categoria;
import com.tuempresa.fact_app.model.Producto;
import com.tuempresa.fact_app.util.Alertas;
import javafx.application.Platform;

public class ProductoController {

    private static final String CARPETA_IMAGENES =
            "src/main/resources/com/tuempresa/fact_app/image";

    @FXML private TextField txtCodigo, txtNombre, txtPrecio, txtExistencia;
    @FXML private ComboBox<Categoria> cmbCategoria;
    @FXML private CheckBox chkActivo;
    @FXML private ImageView imgProducto;

    @FXML private TextField txtBuscar;
    @FXML private ComboBox<String> cmbFiltroEstado;
    @FXML private ComboBox<Categoria> cmbFiltroCategoria;

    @FXML private TableView<Producto> tblProductos;
    @FXML private TableColumn<Producto, String> colCodigo;
    @FXML private TableColumn<Producto, String> colNombre;
    @FXML private TableColumn<Producto, Categoria> colCategoria;
    @FXML private TableColumn<Producto, BigDecimal> colPrecio;
    @FXML private TableColumn<Producto, Integer> colExistencia;
    @FXML private TableColumn<Producto, Boolean> colActivo;

    @FXML private Button btnGuardar, btnEliminar;

    private final ObservableList<Producto> productos = FXCollections.observableArrayList();
    private final FilteredList<Producto> productosFiltrados = new FilteredList<>(productos, p -> true);
    private static final Categoria TODAS = new Categoria(null, "Todas las categorías", true);

    private final ProductoDAO productoDAO = new ProductoDAO();
    private final CategoriaDAO categoriaDAO = new CategoriaDAO();
    private String rutaImagen;

    @FXML
    private void initialize() {
        Platform.runLater(this::cargarDatos);

        tblProductos.setItems(productosFiltrados);
        chkActivo.setSelected(true);

        cmbFiltroEstado.setItems(FXCollections.observableArrayList("Todos", "Activos", "Inactivos"));
        cmbFiltroEstado.setValue("Todos");

        txtBuscar.textProperty().addListener((obs, a, n) -> aplicarFiltros());
        cmbFiltroEstado.valueProperty().addListener((obs, a, n) -> aplicarFiltros());
        cmbFiltroCategoria.valueProperty().addListener((obs, a, n) -> aplicarFiltros());

        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precioVenta"));
        colExistencia.setCellValueFactory(new PropertyValueFactory<>("existencia"));
        colActivo.setCellValueFactory(new PropertyValueFactory<>("activo"));

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

                btnEliminar.setDisable(false);
            }
        });

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

    private Producto obtenerProductoFormulario() {
        String codigo = txtCodigo.getText().trim();
        String nombre = txtNombre.getText().trim();

        if (codigo.isEmpty()) {
            txtCodigo.getStyleClass().add("error");
            txtCodigo.requestFocus();
            throw new IllegalArgumentException("El código es obligatorio.");
        }
        if (nombre.isEmpty()) {
            txtNombre.getStyleClass().add("error");
            txtNombre.requestFocus();
            throw new IllegalArgumentException("El nombre es obligatorio.");
        }

        Categoria categoria = cmbCategoria.getSelectionModel().getSelectedItem();
        if (categoria == null) {
            cmbCategoria.getStyleClass().add("error");
            cmbCategoria.requestFocus();
            throw new IllegalArgumentException("Debe seleccionar una categoría.");
        }

        BigDecimal precio;
        try {
            precio = new BigDecimal(txtPrecio.getText().trim());
        } catch (NumberFormatException e) {
            txtPrecio.getStyleClass().add("error");
            txtPrecio.requestFocus();
            throw new IllegalArgumentException("El precio debe ser un valor numérico.");
        }
        if (precio.compareTo(BigDecimal.ZERO) <= 0) {
            txtPrecio.getStyleClass().add("error");
            txtPrecio.requestFocus();
            throw new IllegalArgumentException("El precio debe ser mayor que cero.");
        }

        int existencia;
        try {
            existencia = Integer.parseInt(txtExistencia.getText().trim());
        } catch (NumberFormatException e) {
            txtExistencia.getStyleClass().add("error");
            txtExistencia.requestFocus();
            throw new IllegalArgumentException("La existencia debe ser un número entero.");
        }
        if (existencia < 0) {
            txtExistencia.getStyleClass().add("error");
            txtExistencia.requestFocus();
            throw new IllegalArgumentException("La existencia no puede ser negativa.");
        }

        return new Producto(null, codigo, nombre, categoria, precio, existencia,
                rutaImagen, chkActivo.isSelected());
    }

    @FXML
    private void guardar() {
        limpiarErrores();
        try {
            Producto producto = obtenerProductoFormulario();
            Producto sel = tblProductos.getSelectionModel().getSelectedItem();

            if (sel == null) { // INSERT
                if (productoDAO.existeCodigo(producto.getCodigo())) {
                    codigoDuplicado();
                    return;
                }
                productoDAO.guardar(producto);
                Alertas.mostrarExito("Producto registrado", "La información fue almacenada correctamente.");
            } else {           // UPDATE (nunca crea uno nuevo)
                producto.setId(sel.getId());
                if (productoDAO.existeCodigo(producto.getCodigo(), sel.getId())) {
                    codigoDuplicado();
                    return;
                }
                productoDAO.actualizar(producto);
                Alertas.mostrarExito("Producto actualizado", "Los cambios se guardaron correctamente.");
            }
            cargarProductos();
            limpiar();

        } catch (IllegalArgumentException e) {
            Alertas.mostrarAdvertencia("Validación", e.getMessage());
        } catch (SQLException e) {
            if ("23505".equals(e.getSQLState())) {
                codigoDuplicado();
            } else {
                Alertas.mostrarError("Error de base de datos", "No fue posible completar la operación.");
            }
            System.err.println(e.getMessage());
        }
    }

    private void codigoDuplicado() {
        txtCodigo.getStyleClass().add("error");
        Alertas.mostrarAdvertencia("Código duplicado", "Ya existe un producto con ese código.");
        txtCodigo.requestFocus();
    }

    @FXML
    private void eliminar() {
        Producto sel = tblProductos.getSelectionModel().getSelectedItem();
        if (sel == null) {
            Alertas.mostrarAdvertencia("Seleccione un producto",
                    "Debe seleccionar el producto que desea eliminar.");
            return;
        }
        if (!Alertas.confirmar("Confirmar", "¿Eliminar '" + sel.getNombre() + "'?")) return;

        try {
            productoDAO.eliminar(sel.getId());
            Alertas.mostrarExito("Producto eliminado", "El producto fue eliminado correctamente.");
            cargarProductos();
            limpiar();
        } catch (SQLException e) {
            Alertas.mostrarError("Error de base de datos", "No fue posible completar la operación.");
            System.err.println(e.getMessage());
        }
    }

    @FXML
    private void nuevo() {
        limpiarErrores();
        limpiar();
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

    private void aplicarFiltros() {
        String texto = txtBuscar.getText() == null ? "" : txtBuscar.getText().trim().toLowerCase();
        String estado = cmbFiltroEstado.getValue();
        Categoria catFiltro = cmbFiltroCategoria.getValue();

        productosFiltrados.setPredicate(p -> {
            boolean coincideTexto = texto.isEmpty()
                    || p.getCodigo().toLowerCase().contains(texto)
                    || p.getNombre().toLowerCase().contains(texto)
                    || p.getCategoria().getNombre().toLowerCase().contains(texto);

            boolean coincideEstado = estado == null || estado.equals("Todos")
                    || (estado.equals("Activos") && p.isActivo())
                    || (estado.equals("Inactivos") && !p.isActivo());

            boolean coincideCategoria = catFiltro == null || catFiltro.getId() == null
                    || catFiltro.getId().equals(p.getCategoria().getId());

            return coincideTexto && coincideEstado && coincideCategoria;
        });
    }

    private void cargarDatos() {
        try {
            List<Categoria> cats = categoriaDAO.listar();
            cmbCategoria.setItems(FXCollections.observableArrayList(cats));

            ObservableList<Categoria> conTodas = FXCollections.observableArrayList(cats);
            conTodas.add(0, TODAS);
            cmbFiltroCategoria.setItems(conTodas);
            cmbFiltroCategoria.setValue(TODAS);

            productos.setAll(productoDAO.listar());
        } catch (SQLException e) {
            Alertas.mostrarError("Error de base de datos", "No fue posible cargar la información.");
            System.err.println(e.getMessage());
        }
    }

    private void cargarProductos() {
        try {
            productos.setAll(productoDAO.listar());
        } catch (SQLException e) {
            Alertas.mostrarError("Error de base de datos", "No fue posible cargar los productos.");
            System.err.println(e.getMessage());
        }
    }
}