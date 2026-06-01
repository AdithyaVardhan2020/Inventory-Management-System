package com.inventory.backend_java.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.inventory.backend_java.DatabaseConnection;
import com.inventory.backend_java.model.Supplier;

public class SupplierManager {

    public boolean addSupplier(Supplier supplier) {
        String sql = "INSERT INTO suppliers (supplier_name, contact_person, phone, email, address) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, supplier.getSupplierName());
            statement.setString(2, supplier.getContactPerson());
            statement.setString(3, supplier.getPhone());
            statement.setString(4, supplier.getEmail());
            statement.setString(5, supplier.getAddress());

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error adding supplier.");
            e.printStackTrace();
            return false;
        }
    }

    public List<Supplier> getAllSuppliers() {
        String sql = "SELECT * FROM suppliers";
        List<Supplier> suppliers = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                suppliers.add(mapSupplier(resultSet));
            }

        } catch (SQLException e) {
            System.out.println("Error viewing suppliers.");
            e.printStackTrace();
        }

        return suppliers;
    }

    public Supplier getSupplierById(int supplierId) {
        String sql = "SELECT * FROM suppliers WHERE supplier_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, supplierId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapSupplier(resultSet);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error fetching supplier.");
            e.printStackTrace();
        }

        return null;
    }

    public List<Supplier> searchSuppliers(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return getAllSuppliers();
        }

        String sql = """
                SELECT * FROM suppliers
                WHERE LOWER(supplier_name) LIKE ?
                   OR LOWER(contact_person) LIKE ?
                   OR LOWER(email) LIKE ?
                ORDER BY supplier_name
                """;
        String pattern = "%" + keyword.toLowerCase() + "%";
        List<Supplier> suppliers = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, pattern);
            statement.setString(2, pattern);
            statement.setString(3, pattern);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    suppliers.add(mapSupplier(resultSet));
                }
            }

        } catch (SQLException e) {
            System.out.println("Error searching suppliers.");
            e.printStackTrace();
        }

        return suppliers;
    }

    public boolean updateSupplier(int supplierId, Supplier supplier) {
        String sql = """
                UPDATE suppliers
                SET supplier_name = ?, contact_person = ?, phone = ?, email = ?, address = ?
                WHERE supplier_id = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, supplier.getSupplierName());
            statement.setString(2, supplier.getContactPerson());
            statement.setString(3, supplier.getPhone());
            statement.setString(4, supplier.getEmail());
            statement.setString(5, supplier.getAddress());
            statement.setInt(6, supplierId);

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error updating supplier.");
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteSupplier(int supplierId) {
        String sql = "DELETE FROM suppliers WHERE supplier_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, supplierId);
            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error deleting supplier.");
            e.printStackTrace();
            return false;
        }
    }

    public void viewSuppliers() {
        System.out.println("\nSupplier List:");
        for (Supplier supplier : getAllSuppliers()) {
            System.out.println(
                    supplier.getSupplierId() + " | " +
                    supplier.getSupplierName() + " | " +
                    supplier.getContactPerson() + " | " +
                    supplier.getPhone() + " | " +
                    supplier.getEmail()
            );
        }
    }

    private Supplier mapSupplier(ResultSet resultSet) throws SQLException {
        Supplier supplier = new Supplier();
        supplier.setSupplierId(resultSet.getInt("supplier_id"));
        supplier.setSupplierName(resultSet.getString("supplier_name"));
        supplier.setContactPerson(resultSet.getString("contact_person"));
        supplier.setPhone(resultSet.getString("phone"));
        supplier.setEmail(resultSet.getString("email"));
        supplier.setAddress(resultSet.getString("address"));
        return supplier;
    }
}
