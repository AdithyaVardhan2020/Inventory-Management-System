package com.inventory.backend_java.model;

public class ReorderSuggestion {

    private int productId;
    private String productName;
    private String category;
    private int currentQuantity;
    private int reorderLevel;
    private int suggestedOrderQuantity;
    private String priority;
    private String supplierName;

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getCurrentQuantity() {
        return currentQuantity;
    }

    public void setCurrentQuantity(int currentQuantity) {
        this.currentQuantity = currentQuantity;
    }

    public int getReorderLevel() {
        return reorderLevel;
    }

    public void setReorderLevel(int reorderLevel) {
        this.reorderLevel = reorderLevel;
    }

    public int getSuggestedOrderQuantity() {
        return suggestedOrderQuantity;
    }

    public void setSuggestedOrderQuantity(int suggestedOrderQuantity) {
        this.suggestedOrderQuantity = suggestedOrderQuantity;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }
}
