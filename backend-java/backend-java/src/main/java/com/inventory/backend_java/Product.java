package com.inventory.backend_java;

public class Product {

    private int productId;
    private String productName;
    private String category;
    private int quantity;
    private double price;
    private int reorderLevel;
    private int supplierId;

    public Product(String productName, String category, int quantity, double price, int reorderLevel, int supplierId) {
        this.productName = productName;
        this.category = category;
        this.quantity = quantity;
        this.price = price;
        this.reorderLevel = reorderLevel;
        this.supplierId = supplierId;
    }

    public String getProductName() {
        return productName;
    }

    public String getCategory() {
        return category;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public int getReorderLevel() {
        return reorderLevel;
    }

    public int getSupplierId() {
        return supplierId;
    }
}