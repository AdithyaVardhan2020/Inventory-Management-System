# Inventory-Management-System

## Project Overview

The Inventory Management System is designed to help businesses manage products, suppliers, stock transactions, and inventory levels. The system also includes a Python analytics module for forecasting stock usage and identifying future inventory needs.

## Week 2 Progress

During Week 2, the backend foundation of the project was developed.

## Completed Features

- Created database schema using SQLite
- Added supplier management module
- Added product inventory management module
- Added stock transaction management module
- Added low stock alert functionality
- Added Python-based stock forecasting module
- Added sample CSV dataset for analytics
- Generated inventory trend graph

## Project Structure

```text
Inventory-Management-System/
│
├── Reports/
├── analytics-python/
│   ├── sample_data.csv
│   ├── stock_forecast.py
│   └── requirements.txt
│
├── backend-java/
│   ├── lib/
│   │   └── sqlite-jdbc.jar
│   └── src/
│       ├── DatabaseConnection.java
│       ├── Product.java
│       ├── Supplier.java
│       ├── InventoryManager.java
│       ├── SupplierManager.java
│       ├── TransactionManager.java
│       └── Main.java
│
├── database/
│   ├── inventory.db
│   └── schema.sql
│
├── docs/
├── postman/
├── README.md
└── inventory_trend.png
Java Backend Features
The Java backend currently supports:
•	Adding suppliers 
•	Viewing suppliers 
•	Adding products 
•	Viewing products 
•	Updating stock quantity 
•	Deleting products 
•	Recording stock transactions 
•	Viewing transaction history 
•	Displaying low stock alerts 
Python Analytics Features
The Python analytics module supports:
•	Reading inventory usage data from CSV 
•	Calculating average monthly stock usage 
•	Forecasting next month stock usage 
•	Creating an inventory trend graph 
