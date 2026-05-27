package com.inventory.backend_java.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.inventory.backend_java.DatabaseConnection;
import com.inventory.backend_java.model.StockTransaction;

public class TransactionManager {

    public boolean recordTransaction(int productId, String transactionType, int quantity, String notes) {
        String sql = "INSERT INTO stock_transactions (product_id, transaction_type, quantity, notes) VALUES (?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, productId);
            statement.setString(2, transactionType);
            statement.setInt(3, quantity);
            statement.setString(4, notes);

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error recording transaction.");
            e.printStackTrace();
            return false;
        }
    }

    public List<StockTransaction> getAllTransactions() {
        String sql = """
                SELECT t.transaction_id, t.product_id, p.product_name, t.transaction_type, t.quantity,
                       t.transaction_date, t.notes
                FROM stock_transactions t
                JOIN products p ON t.product_id = p.product_id
                ORDER BY t.transaction_date DESC
                """;

        List<StockTransaction> transactions = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                StockTransaction transaction = new StockTransaction();
                transaction.setTransactionId(resultSet.getInt("transaction_id"));
                transaction.setProductId(resultSet.getInt("product_id"));
                transaction.setProductName(resultSet.getString("product_name"));
                transaction.setTransactionType(resultSet.getString("transaction_type"));
                transaction.setQuantity(resultSet.getInt("quantity"));
                transaction.setTransactionDate(resultSet.getString("transaction_date"));
                transaction.setNotes(resultSet.getString("notes"));
                transactions.add(transaction);
            }

        } catch (SQLException e) {
            System.out.println("Error viewing transactions.");
            e.printStackTrace();
        }

        return transactions;
    }

    public void viewTransactions() {
        System.out.println("\nStock Transaction History:");
        for (StockTransaction transaction : getAllTransactions()) {
            System.out.println(
                    transaction.getTransactionId() + " | " +
                    transaction.getProductName() + " | " +
                    transaction.getTransactionType() + " | Qty: " +
                    transaction.getQuantity() + " | Date: " +
                    transaction.getTransactionDate() + " | Notes: " +
                    transaction.getNotes()
            );
        }
    }
}
