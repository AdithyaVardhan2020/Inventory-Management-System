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


## Week 3 Progress

During Week 3, REST APIs, authentication modules, frontend starter pages, and API testing support were added.

## Completed Features

- Added REST API controllers
- Added authentication system
- Added user registration/login
- Added dashboard starter
- Added Postman API testing
- Added advanced reporting structure
- Added low stock API access

## New API Endpoints

- GET /products/all
- GET /products/low-stock
- GET /suppliers/all
- GET /transactions/all
- POST /auth/login

## Frontend Update

- Added basic dashboard.html starter page

## Analytics Update

Enhanced Python forecasting with:
- Category-based forecasting
- Regional demand forecasting
- Reorder suggestions

## Week 4 Progress

During Week 4, the system moved from console-only API responses to full JSON CRUD APIs backed by SQLite, with frontend dashboard integration.

## Completed Features

- Product CRUD JSON APIs (create, read, update, delete)
- Supplier CRUD JSON APIs
- Transaction list JSON API
- Low-stock alerts returned as JSON
- SQLite integration via `DatabaseConnection` in all service managers
- Frontend dashboard with summary cards and data tables
- Add product and add supplier forms
- Login form UI (`login.html` and dashboard login panel)
- Updated Postman collection with all CRUD endpoints
- CORS enabled for frontend API calls

## Project Structure (Maven)

```text
Inventory-Management-System/
├── backend-java/
│   ├── backend-java/          ← Maven root (run mvn here)
│   │   └── src/main/java/com/inventory/backend_java/
│   └── frontend/
│       ├── dashboard.html
│       └── login.html
├── database/
│   ├── inventory.db
│   └── inventory_schema.sql
├── postman/
│   └── inventory_api_collection.json
└── analytics-python/
```

## Week 4 API Endpoints

### Products
- `GET /products/all` — JSON list of all products
- `GET /products/{id}` — single product
- `POST /products/add` — add product (JSON body)
- `PUT /products/update/{id}` — update product
- `DELETE /products/delete/{id}` — delete product
- `GET /products/low-stock` — JSON low-stock alerts

### Suppliers
- `GET /suppliers/all`
- `GET /suppliers/{id}`
- `POST /suppliers/add`
- `PUT /suppliers/update/{id}`
- `DELETE /suppliers/delete/{id}`

### Transactions
- `GET /transactions/all` — JSON transaction history

### Auth (unchanged)
- `POST /auth/login` — body: `{ "username": "admin", "password": "admin123" }`
- `POST /auth/register`

### Health
- `GET /api/health`

## Run the Backend

```bash
cd backend-java/backend-java
mvn spring-boot:run
```

## Test URLs

- http://localhost:8080/products/all
- http://localhost:8080/products/low-stock
- http://localhost:8080/suppliers/all
- http://localhost:8080/transactions/all

## Frontend

Open `backend-java/frontend/login.html` or `dashboard.html` in a browser (backend must be running on port 8080).

## Postman

Import `postman/inventory_api_collection.json` and set `baseUrl` to `http://localhost:8080`.

## Week 5 Progress

During Week 5, the application was upgraded to a production-style inventory system with a professional frontend, validation, search/filter APIs, analytics, and improved error handling.

## Completed Features

### Frontend
- Professional dashboard UI with sidebar navigation
- Responsive CSS layout (`css/styles.css`)
- Dynamic product, supplier, and transaction tables
- Low-stock alerts panel
- Product add/update/delete forms with inline edit
- Supplier add/update/delete forms with inline edit
- Search and category filter for products
- Supplier search
- Analytics charts (Chart.js) — category breakdown and inventory overview
- Improved login page with client-side validation

### Backend
- JSON error responses with HTTP status codes (200, 201, 400, 404, 500)
- Global exception handler (`GlobalExceptionHandler`)
- Product and supplier validation
- Search API: `GET /products/search?keyword=&category=`
- Category filter: `GET /products/categories`
- Supplier search: `GET /suppliers/search?keyword=`
- Inventory statistics: `GET /api/statistics`

### Analytics
- Dashboard analytics cards and charts
- Improved Python forecasting with multi-panel visualization

### Documentation
- API reference: `docs/API.md`
- Screenshot guide: `docs/screenshots/`

## Week 5 API Endpoints (new)

- `GET /api/statistics` — totals and category breakdown
- `GET /products/search?keyword=&category=` — search/filter products
- `GET /products/categories` — list product categories
- `GET /suppliers/search?keyword=` — search suppliers

## Frontend Structure

```text
backend-java/frontend/
├── css/styles.css
├── js/app.js
├── login.html
└── dashboard.html
```

## Run & Test

```bash
cd backend-java/backend-java
mvn compile test
mvn spring-boot:run
```

Open `backend-java/frontend/login.html` (credentials: `admin` / `admin123`).

See `docs/API.md` for full endpoint documentation.
