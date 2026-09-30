package com.tuempresa.fact_app.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;





public class DataBaseConnection {

    private static final String URL = "jdbc:postgresql://localhost:5433/tienda_javafx";
    private static final String USER = "postgres";
    private static final String PASSWORD = "postgre";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
    public static void main(String[] args) {
        try (Connection con = getConnection()) {
            System.out.println("Conexión exitosa: " + con);
        } catch (SQLException e) {
            System.out.println("Error de conexión: " + e.getMessage());
        }
    }
}
