package com.inventory.backend_java.model;

public class CategoryStat {

    private String category;
    private int count;

    public CategoryStat() {
    }

    public CategoryStat(String category, int count) {
        this.category = category;
        this.count = count;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
