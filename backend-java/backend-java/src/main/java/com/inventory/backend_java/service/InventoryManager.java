package com.inventory.backend_java.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.inventory.backend_java.DatabaseConnection;
import com.inventory.backend_java.model.Product;

public class InventoryManager {

    public boolean addProduct(Product product) {
        String sql = "INSERT INTO products (product_name, category, quantity, price, reorder_level, supplier_id) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, product.getProductName());
            statement.setString(2, product.getCategory());
            statement.setInt(3, product.getQuantity());
            statement.setDouble(4, product.getPrice());
            statement.setInt(5, product.getReorderLevel());
            statement.setInt(6, product.getSupplierId());

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error adding product.");
            e.printStackTrace();
            return false;
        }
    }

    public List<Product> getAllProducts() {
        String sql = """
                SELECT p.product_id, p.product_name, p.category, p.quantity, p.price, p.reorder_level,
                       p.supplier_id, s.supplier_name
                FROM products p
                LEFT JOIN suppliers s ON p.supplier_id = s.supplier_id
                """;

        List<Product> products = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                products.add(mapProduct(resultSet));
            }

        } catch (SQLException e) {
            System.out.println("Error viewing products.");
            e.printStackTrace();
        }

        return products;
    }

    public Product getProductById(int productId) {
        String sql = """
                SELECT p.product_id, p.product_name, p.category, p.quantity, p.price, p.reorder_level,
                       p.supplier_id, s.supplier_name
                FROM products p
                LEFT JOIN suppliers s ON p.supplier_id = s.supplier_id
                WHERE p.product_id = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, productId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapProduct(resultSet);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error fetching product.");
            e.printStackTrace();
        }

        return null;
    }

    public boolean updateProduct(int productId, Product product) {
        String sql = """
                UPDATE products
                SET product_name = ?, category = ?, quantity = ?, price = ?, reorder_level = ?, supplier_id = ?
                WHERE product_id = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, product.getProductName());
            statement.setString(2, product.getCategory());
            statement.setInt(3, product.getQuantity());
            statement.setDouble(4, product.getPrice());
            statement.setInt(5, product.getReorderLevel());
            statement.setInt(6, product.getSupplierId());
            statement.setInt(7, productId);

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error updating product.");
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteProduct(int productId) {
        String sql = "DELETE FROM products WHERE product_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, productId);
            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error deleting product.");
            e.printStackTrace();
            return false;
        }
    }

    public List<Product> searchProducts(String keyword, String category) {
        StringBuilder sql = new StringBuilder("""
                SELECT p.product_id, p.product_name, p.category, p.quantity, p.price, p.reorder_level,
                       p.supplier_id, s.supplier_name
                FROM products p
                LEFT JOIN suppliers s ON p.supplier_id = s.supplier_id
                WHERE 1=1
                """);

        List<String> params = new ArrayList<>();

        if (keyword != null && !keyword.isBlank()) {
            sql.append(" AND (LOWER(p.product_name) LIKE ? OR LOWER(p.category) LIKE ?)");
            String pattern = "%" + keyword.toLowerCase() + "%";
            params.add(pattern);
            params.add(pattern);
        }
        if (category != null && !category.isBlank()) {
            sql.append(" AND LOWER(p.category) = ?");
            params.add(category.toLowerCase());
        }

        sql.append(" ORDER BY p.product_name");

        List<Product> products = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                statement.setString(i + 1, params.get(i));
            }

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    products.add(mapProduct(resultSet));
                }
            }

        } catch (SQLException e) {
            System.out.println("Error searching products.");
            e.printStackTrace();
        }

        return products;
    }

    public List<String> getCategories() {
        String sql = "SELECT DISTINCT category FROM products WHERE category IS NOT NULL ORDER BY category";
        List<String> categories = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                categories.add(resultSet.getString("category"));
            }

        } catch (SQLException e) {
            System.out.println("Error fetching categories.");
            e.printStackTrace();
        }

        return categories;
    }

    public List<com.inventory.backend_java.model.CategoryStat> getCategoryBreakdown() {
        String sql = "SELECT category, COUNT(*) AS count FROM products GROUP BY category ORDER BY count DESC";
        List<com.inventory.backend_java.model.CategoryStat> stats = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                stats.add(new com.inventory.backend_java.model.CategoryStat(
                        resultSet.getString("category"),
                        resultSet.getInt("count")
                ));
            }

        } catch (SQLException e) {
            System.out.println("Error fetching category breakdown.");
            e.printStackTrace();
        }

        return stats;
    }

    public int countProducts() {
        return getAllProducts().size();
    }

    public int countLowStock() {
        return getLowStockProducts().size();
    }

    public boolean productExists(int productId) {
        return getProductById(productId) != null;
    }

    public List<Product> getLowStockProducts() {
        String sql = """
                SELECT p.product_id, p.product_name, p.category, p.quantity, p.price, p.reorder_level,
                       p.supplier_id, s.supplier_name
                FROM products p
                LEFT JOIN suppliers s ON p.supplier_id = s.supplier_id
                WHERE p.quantity <= p.reorder_level
                """;

        List<Product> products = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                products.add(mapProduct(resultSet));
            }

        } catch (SQLException e) {
            System.out.println("Error checking low stock alerts.");
            e.printStackTrace();
        }

        return products;
    }

    public void viewProducts() {
        System.out.println("\nProduct List:");
        for (Product product : getAllProducts()) {
            System.out.println(
                    product.getProductId() + " | " +
                    product.getProductName() + " | " +
                    product.getCategory() + " | Qty: " +
                    product.getQuantity() + " | Price: $" +
                    product.getPrice() + " | Reorder Level: " +
                    product.getReorderLevel() + " | Supplier: " +
                    product.getSupplierName()
            );
        }
    }

    public void updateStock(int productId, int newQuantity) {
        String sql = "UPDATE products SET quantity = ? WHERE product_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, newQuantity);
            statement.setInt(2, productId);
            statement.executeUpdate();
            System.out.println("Stock updated successfully.");

        } catch (SQLException e) {
            System.out.println("Error updating stock.");
            e.printStackTrace();
        }
    }

    public void showLowStockAlerts() {
        System.out.println("\nLow Stock Alerts:");
        List<Product> lowStock = getLowStockProducts();

        if (lowStock.isEmpty()) {
            System.out.println("No low stock products found.");
            return;
        }

        for (Product product : lowStock) {
            System.out.println(
                    "ALERT: " + product.getProductName() +
                    " has only " + product.getQuantity() +
                    " units left. Reorder level is " + product.getReorderLevel()
            );
        }
    }

    private Product mapProduct(ResultSet resultSet) throws SQLException {
        Product product = new Product();
        product.setProductId(resultSet.getInt("product_id"));
        product.setProductName(resultSet.getString("product_name"));
        product.setCategory(resultSet.getString("category"));
        product.setQuantity(resultSet.getInt("quantity"));
        product.setPrice(resultSet.getDouble("price"));
        product.setReorderLevel(resultSet.getInt("reorder_level"));
        product.setSupplierId(resultSet.getInt("supplier_id"));
        product.setSupplierName(resultSet.getString("supplier_name"));
        return product;
    }
}
