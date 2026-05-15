package com.inventory.backend_java;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SupplierManager {

    public void addSupplier(Supplier supplier) {
        String sql = "INSERT INTO suppliers (supplier_name, contact_person, phone, email, address) VALUES (?, ?, ?, ?, ?)";

        try {
            Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setString(1, supplier.getSupplierName());
            statement.setString(2, supplier.getContactPerson());
            statement.setString(3, supplier.getPhone());
            statement.setString(4, supplier.getEmail());
            statement.setString(5, supplier.getAddress());

            statement.executeUpdate();

            System.out.println("Supplier added successfully.");

            statement.close();
            connection.close();

        } catch (SQLException e) {
            System.out.println("Error adding supplier.");
            e.printStackTrace();
        }
    }

    public void viewSuppliers() {
        String sql = "SELECT * FROM suppliers";

        try {
            Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            System.out.println("\nSupplier List:");

            while (resultSet.next()) {
                System.out.println(
                    resultSet.getInt("supplier_id") + " | " +
                    resultSet.getString("supplier_name") + " | " +
                    resultSet.getString("contact_person") + " | " +
                    resultSet.getString("phone") + " | " +
                    resultSet.getString("email")
                );
            }

            resultSet.close();
            statement.close();
            connection.close();

        } catch (SQLException e) {
            System.out.println("Error viewing suppliers.");
            e.printStackTrace();
        }
    }
}