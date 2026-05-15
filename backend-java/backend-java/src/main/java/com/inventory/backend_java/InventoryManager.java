package com.inventory.backend_java;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class InventoryManager {

    public void addProduct(Product product) {
        String sql = "INSERT INTO products (product_name, category, quantity, price, reorder_level, supplier_id) VALUES (?, ?, ?, ?, ?, ?)";

        try {
            Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setString(1, product.getProductName());
            statement.setString(2, product.getCategory());
            statement.setInt(3, product.getQuantity());
            statement.setDouble(4, product.getPrice());
            statement.setInt(5, product.getReorderLevel());
            statement.setInt(6, product.getSupplierId());

            statement.executeUpdate();

            System.out.println("Product added successfully.");

            statement.close();
            connection.close();

        } catch (SQLException e) {
            System.out.println("Error adding product.");
            e.printStackTrace();
        }
    }

    public void viewProducts() {
        String sql = """
                SELECT p.product_id, p.product_name, p.category, p.quantity, p.price, p.reorder_level, s.supplier_name
                FROM products p
                LEFT JOIN suppliers s ON p.supplier_id = s.supplier_id
                """;

        try {
            Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            System.out.println("\nProduct List:");

            while (resultSet.next()) {
                System.out.println(
                    resultSet.getInt("product_id") + " | " +
                    resultSet.getString("product_name") + " | " +
                    resultSet.getString("category") + " | Qty: " +
                    resultSet.getInt("quantity") + " | Price: $" +
                    resultSet.getDouble("price") + " | Reorder Level: " +
                    resultSet.getInt("reorder_level") + " | Supplier: " +
                    resultSet.getString("supplier_name")
                );
            }

            resultSet.close();
            statement.close();
            connection.close();

        } catch (SQLException e) {
            System.out.println("Error viewing products.");
            e.printStackTrace();
        }
    }

    public void updateStock(int productId, int newQuantity) {
        String sql = "UPDATE products SET quantity = ? WHERE product_id = ?";

        try {
            Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setInt(1, newQuantity);
            statement.setInt(2, productId);

            statement.executeUpdate();

            System.out.println("Stock updated successfully.");

            statement.close();
            connection.close();

        } catch (SQLException e) {
            System.out.println("Error updating stock.");
            e.printStackTrace();
        }
    }

    public void deleteProduct(int productId) {
        String sql = "DELETE FROM products WHERE product_id = ?";

        try {
            Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setInt(1, productId);
            statement.executeUpdate();

            System.out.println("Product deleted successfully.");

            statement.close();
            connection.close();

        } catch (SQLException e) {
            System.out.println("Error deleting product.");
            e.printStackTrace();
        }
    }

    public void showLowStockAlerts() {
        String sql = "SELECT product_id, product_name, quantity, reorder_level FROM products WHERE quantity <= reorder_level";

        try {
            Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            System.out.println("\nLow Stock Alerts:");

            boolean found = false;

            while (resultSet.next()) {
                found = true;
                System.out.println(
                    "ALERT: " +
                    resultSet.getString("product_name") +
                    " has only " +
                    resultSet.getInt("quantity") +
                    " units left. Reorder level is " +
                    resultSet.getInt("reorder_level")
                );
            }

            if (!found) {
                System.out.println("No low stock products found.");
            }

            resultSet.close();
            statement.close();
            connection.close();

        } catch (SQLException e) {
            System.out.println("Error checking low stock alerts.");
            e.printStackTrace();
        }
    }
}