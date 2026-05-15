package com.inventory.backend_java;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String DB_URL = "jdbc:sqlite:database/inventory.db";

    public static Connection getConnection() {
        Connection connection = null;

        try {
            connection = DriverManager.getConnection(DB_URL);
            System.out.println("Database connected successfully.");
        } catch (SQLException e) {
            System.out.println("Database connection failed.");
            e.printStackTrace();
        }

        return connection;
    }
}