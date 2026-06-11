package com.inventory.backend_java.model;

import java.util.ArrayList;
import java.util.List;

public class InventoryForecast {

    private int totalProducts;
    private int lowStockCount;
    private int forecastedMonthlyDemand;
    private List<ForecastItem> categoryForecasts = new ArrayList<>();

    public int getTotalProducts() {
        return totalProducts;
    }

    public void setTotalProducts(int totalProducts) {
        this.totalProducts = totalProducts;
    }

    public int getLowStockCount() {
        return lowStockCount;
    }

    public void setLowStockCount(int lowStockCount) {
        this.lowStockCount = lowStockCount;
    }

    public int getForecastedMonthlyDemand() {
        return forecastedMonthlyDemand;
    }

    public void setForecastedMonthlyDemand(int forecastedMonthlyDemand) {
        this.forecastedMonthlyDemand = forecastedMonthlyDemand;
    }

    public List<ForecastItem> getCategoryForecasts() {
        return categoryForecasts;
    }

    public void setCategoryForecasts(List<ForecastItem> categoryForecasts) {
        this.categoryForecasts = categoryForecasts;
    }
}
