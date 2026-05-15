package com.inventory.backend_java;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TransactionManager {

    public void recordTransaction(int productId, String transactionType, int quantity, String notes) {
        String sql = "INSERT INTO stock_transactions (product_id, transaction_type, quantity, notes) VALUES (?, ?, ?, ?)";

        try {
            Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setInt(1, productId);
            statement.setString(2, transactionType);
            statement.setInt(3, quantity);
            statement.setString(4, notes);

            statement.executeUpdate();

            System.out.println("Stock transaction recorded successfully.");

            statement.close();
            connection.close();

        } catch (SQLException e) {
            System.out.println("Error recording transaction.");
            e.printStackTrace();
        }
    }

    public void viewTransactions() {
        String sql = """
                SELECT t.transaction_id, p.product_name, t.transaction_type, t.quantity, t.transaction_date, t.notes
                FROM stock_transactions t
                JOIN products p ON t.product_id = p.product_id
                ORDER BY t.transaction_date DESC
                """;

        try {
            Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            System.out.println("\nStock Transaction History:");

            while (resultSet.next()) {
                System.out.println(
                    resultSet.getInt("transaction_id") + " | " +
                    resultSet.getString("product_name") + " | " +
                    resultSet.getString("transaction_type") + " | Qty: " +
                    resultSet.getInt("quantity") + " | Date: " +
                    resultSet.getString("transaction_date") + " | Notes: " +
                    resultSet.getString("notes")
                );
            }

            resultSet.close();
            statement.close();
            connection.close();

        } catch (SQLException e) {
            System.out.println("Error viewing transactions.");
            e.printStackTrace();
        }
    }
}