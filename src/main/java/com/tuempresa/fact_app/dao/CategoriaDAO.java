package com.tuempresa.fact_app.dao;

import com.tuempresa.fact_app.model.Categoria;
import com.tuempresa.fact_app.util.DataBaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDAO {

    public void guardar(Categoria c) throws SQLException {
        String sql = "INSERT INTO categoria (nombre, activo) VALUES (?, ?)";
        Connection con = DataBaseConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ps.setString(1, c.getNombre());
        ps.setBoolean(2, c.isActivo());
        ps.executeUpdate();

        ResultSet rs = ps.getGeneratedKeys();
        if (rs.next()) {
            c.setId(rs.getInt(1));
        }
        rs.close();
        ps.close();
        con.close();
    }

    public List<Categoria> listar() throws SQLException {
        List<Categoria> lista = new ArrayList<>();
        String sql = "SELECT id, nombre, activo FROM categoria ORDER BY id";
        Connection con = DataBaseConnection.getConnection();
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(sql);

        while (rs.next()) {
            Categoria c = new Categoria();
            c.setId(rs.getInt("id"));
            c.setNombre(rs.getString("nombre"));
            c.setActivo(rs.getBoolean("activo"));
            lista.add(c);
        }
        rs.close();
        st.close();
        con.close();
        return lista;
    }

    public void actualizar(Categoria c) throws SQLException {
        String sql = "UPDATE categoria SET nombre = ?, activo = ? WHERE id = ?";
        Connection con = DataBaseConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, c.getNombre());
        ps.setBoolean(2, c.isActivo());
        ps.setInt(3, c.getId());
        ps.executeUpdate();
        ps.close();
        con.close();
    }

    public void eliminar(int id) throws SQLException {
        String sql = "DELETE FROM categoria WHERE id = ?";
        Connection con = DataBaseConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, id);
        ps.executeUpdate();
        ps.close();
        con.close();
    }

}
