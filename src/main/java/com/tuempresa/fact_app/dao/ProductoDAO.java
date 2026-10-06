package com.tuempresa.fact_app.dao;

import com.tuempresa.fact_app.model.Categoria;
import com.tuempresa.fact_app.model.Producto;
import com.tuempresa.fact_app.util.DataBaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO {

    public void guardar(Producto p) throws SQLException {
        String sql = "INSERT INTO producto (codigo, nombre, categoria_id, precio_venta, existencia, ruta_imagen, activo) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection cn = DataBaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, p.getCodigo());
            ps.setString(2, p.getNombre());
            ps.setInt(3, p.getCategoria().getId());
            ps.setBigDecimal(4, p.getPrecioVenta());
            ps.setInt(5, p.getExistencia());
            ps.setString(6, p.getRutaImagen());
            ps.setBoolean(7, p.isActivo());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) p.setId(rs.getInt(1));
            }
        }
    }

    public List<Producto> listar() throws SQLException {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT p.*, c.nombre AS nombre_categoria, c.activo AS activo_categoria "
                + "FROM producto p JOIN categoria c ON p.categoria_id = c.id ORDER BY p.id";
        try (Connection cn = DataBaseConnection.getConnection();
             Statement st = cn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Categoria cat = new Categoria(rs.getInt("categoria_id"),
                        rs.getString("nombre_categoria"), rs.getBoolean("activo_categoria"));
                lista.add(new Producto(
                        rs.getInt("id"), rs.getString("codigo"), rs.getString("nombre"), cat,
                        rs.getBigDecimal("precio_venta"), rs.getInt("existencia"),
                        rs.getString("ruta_imagen"), rs.getBoolean("activo")));
            }
        }
        return lista;
    }

    public void actualizar(Producto p) throws SQLException {
        String sql = "UPDATE producto SET codigo=?, nombre=?, categoria_id=?, precio_venta=?, "
                + "existencia=?, ruta_imagen=?, activo=? WHERE id=?";
        try (Connection cn = DataBaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, p.getCodigo());
            ps.setString(2, p.getNombre());
            ps.setInt(3, p.getCategoria().getId());
            ps.setBigDecimal(4, p.getPrecioVenta());
            ps.setInt(5, p.getExistencia());
            ps.setString(6, p.getRutaImagen());
            ps.setBoolean(7, p.isActivo());
            ps.setInt(8, p.getId());
            ps.executeUpdate();
        }
    }

    public void eliminar(int id) throws SQLException {
        String sql = "DELETE FROM producto WHERE id = ?";
        try (Connection cn = DataBaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    // ---- NUEVOS ----

    /** Para INSERT */
    public boolean existeCodigo(String codigo) throws SQLException {
        String sql = "SELECT COUNT(*) FROM producto WHERE codigo = ?";
        try (Connection cn = DataBaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    /** Para UPDATE: excluye el producto que se está modificando */
    public boolean existeCodigo(String codigo, int idExcluir) throws SQLException {
        String sql = "SELECT COUNT(*) FROM producto WHERE codigo = ? AND id <> ?";
        try (Connection cn = DataBaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, codigo);
            ps.setInt(2, idExcluir);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }
}