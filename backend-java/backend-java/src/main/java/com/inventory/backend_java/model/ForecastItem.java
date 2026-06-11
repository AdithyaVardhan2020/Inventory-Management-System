package com.inventory.backend_java.model;

public class ForecastItem {

    private String category;
    private int currentStock;
    private int monthlyUsage;
    private int forecastedDemand;
    private String trend;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getCurrentStock() {
        return currentStock;
    }

    public void setCurrentStock(int currentStock) {
        this.currentStock = currentStock;
    }

    public int getMonthlyUsage() {
        return monthlyUsage;
    }

    public void setMonthlyUsage(int monthlyUsage) {
        this.monthlyUsage = monthlyUsage;
    }

    public int getForecastedDemand() {
        return forecastedDemand;
    }

    public void setForecastedDemand(int forecastedDemand) {
        this.forecastedDemand = forecastedDemand;
    }

    public String getTrend() {
        return trend;
    }

    public void setTrend(String trend) {
        this.trend = trend;
    }
}
