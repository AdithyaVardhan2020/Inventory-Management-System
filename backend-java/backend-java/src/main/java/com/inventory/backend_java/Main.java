package com.inventory.backend_java;

public class Main {

    public static void main(String[] args) {

        SupplierManager supplierManager = new SupplierManager();
        InventoryManager inventoryManager = new InventoryManager();
        TransactionManager transactionManager = new TransactionManager();

        Supplier supplier = new Supplier(
                 "ABC Wholesale",
                "John Smith",
                "1234567890",
                "abc@example.com",
                "Denver, CO"
        );

        supplierManager.addSupplier(supplier);

        Product product = new Product(
                "Laptop",
                "Electronics",
                15,
                750.00,
                5,
                1
        );

        inventoryManager.addProduct(product);

        transactionManager.recordTransaction(
                1,
                "Stock In",
                15,
                "Initial stock added"
        );

        supplierManager.viewSuppliers();

        inventoryManager.viewProducts();

        transactionManager.viewTransactions();

        inventoryManager.showLowStockAlerts();

        inventoryManager.updateStock(1, 3);

        inventoryManager.showLowStockAlerts();
    }
}