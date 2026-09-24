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
        Connection con = DataBaseConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ps.setString(1, p.getCodigo());
        ps.setString(2, p.getNombre());
        ps.setInt(3, p.getCategoria().getId());
        ps.setBigDecimal(4, p.getPrecioVenta());
        ps.setInt(5, p.getExistencia());
        ps.setString(6, p.getRutaImagen());
        ps.setBoolean(7, p.isActivo());
        ps.executeUpdate();

        ResultSet rs = ps.getGeneratedKeys();
        if (rs.next()) {
            p.setId(rs.getInt(1));
        }
        rs.close();
        ps.close();
        con.close();
    }

    public List<Producto> listar() throws SQLException {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT p.*, c.nombre AS nombre_categoria, c.activo AS activo_categoria "
                + "FROM producto p JOIN categoria c ON p.categoria_id = c.id ORDER BY p.id";
        Connection con = DataBaseConnection.getConnection();
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(sql);

        while (rs.next()) {
            Categoria cat = new Categoria();
            cat.setId(rs.getInt("categoria_id"));
            cat.setNombre(rs.getString("nombre_categoria"));
            cat.setActivo(rs.getBoolean("activo_categoria"));

            Producto p = new Producto();
            p.setId(rs.getInt("id"));
            p.setCodigo(rs.getString("codigo"));
            p.setNombre(rs.getString("nombre"));
            p.setCategoria(cat);
            p.setPrecioVenta(rs.getBigDecimal("precio_venta"));
            p.setExistencia(rs.getInt("existencia"));
            p.setRutaImagen(rs.getString("ruta_imagen"));
            p.setActivo(rs.getBoolean("activo"));
            lista.add(p);
        }
        rs.close();
        st.close();
        con.close();
        return lista;
    }

    public void actualizar(Producto p) throws SQLException {
        String sql = "UPDATE producto SET codigo=?, nombre=?, categoria_id=?, precio_venta=?, existencia=?, ruta_imagen=?, activo=? WHERE id=?";
        Connection con = DataBaseConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, p.getCodigo());
        ps.setString(2, p.getNombre());
        ps.setInt(3, p.getCategoria().getId());
        ps.setBigDecimal(4, p.getPrecioVenta());
        ps.setInt(5, p.getExistencia());
        ps.setString(6, p.getRutaImagen());
        ps.setBoolean(7, p.isActivo());
        ps.setInt(8, p.getId());
        ps.executeUpdate();
        ps.close();
        con.close();
    }

    public void eliminar(int id) throws SQLException {
        String sql = "DELETE FROM producto WHERE id = ?";
        Connection con = DataBaseConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, id);
        ps.executeUpdate();
        ps.close();
        con.close();
    }
}
