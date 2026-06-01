package com.inventory.backend_java.model;

import java.util.ArrayList;
import java.util.List;

public class InventoryStats {

    private int totalProducts;
    private int totalSuppliers;
    private int totalTransactions;
    private int lowStockCount;
    private List<CategoryStat> categoryBreakdown = new ArrayList<>();

    public int getTotalProducts() {
        return totalProducts;
    }

    public void setTotalProducts(int totalProducts) {
        this.totalProducts = totalProducts;
    }

    public int getTotalSuppliers() {
        return totalSuppliers;
    }

    public void setTotalSuppliers(int totalSuppliers) {
        this.totalSuppliers = totalSuppliers;
    }

    public int getTotalTransactions() {
        return totalTransactions;
    }

    public void setTotalTransactions(int totalTransactions) {
        this.totalTransactions = totalTransactions;
    }

    public int getLowStockCount() {
        return lowStockCount;
    }

    public void setLowStockCount(int lowStockCount) {
        this.lowStockCount = lowStockCount;
    }

    public List<CategoryStat> getCategoryBreakdown() {
        return categoryBreakdown;
    }

    public void setCategoryBreakdown(List<CategoryStat> categoryBreakdown) {
        this.categoryBreakdown = categoryBreakdown;
    }
}
