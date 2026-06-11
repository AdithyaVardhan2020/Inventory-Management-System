# Inventory Management System — API Documentation

Base URL: `http://localhost:8080`

## Health

| Method | Endpoint | Description | Status |
|--------|----------|-------------|--------|
| GET | `/api/health` | Health check | 200 |

## Authentication

| Method | Endpoint | Body | Response |
|--------|----------|------|----------|
| POST | `/auth/login` | `{ "username", "password" }` | `Login successful` or `Invalid credentials` |
| POST | `/auth/register` | `{ "username", "password", "role" }` | Registration message |

## Statistics

| Method | Endpoint | Description | Status |
|--------|----------|-------------|--------|
| GET | `/api/statistics` | Inventory analytics summary | 200 |

**Response example:**
```json
{
  "totalProducts": 5,
  "totalSuppliers": 2,
  "totalTransactions": 3,
  "lowStockCount": 1,
  "categoryBreakdown": [
    { "category": "Electronics", "count": 3 }
  ]
}
```

## Products

| Method | Endpoint | Description | Status |
|--------|----------|-------------|--------|
| GET | `/products/all` | List all products | 200 |
| GET | `/products/{id}` | Get product by ID | 200, 404 |
| GET | `/products/search?keyword=&category=` | Search/filter products | 200 |
| GET | `/products/categories` | Distinct categories | 200 |
| GET | `/products/low-stock` | Low stock alerts | 200 |
| POST | `/products/add` | Create product | 201, 400, 404, 500 |
| PUT | `/products/update/{id}` | Update product | 200, 400, 404, 500 |
| DELETE | `/products/delete/{id}` | Delete product | 200, 404, 500 |

**Product body:**
```json
{
  "productName": "Laptop",
  "category": "Electronics",
  "quantity": 15,
  "price": 750.00,
  "reorderLevel": 5,
  "supplierId": 1
}
```

## Suppliers

| Method | Endpoint | Description | Status |
|--------|----------|-------------|--------|
| GET | `/suppliers/all` | List all suppliers | 200 |
| GET | `/suppliers/{id}` | Get supplier by ID | 200, 404 |
| GET | `/suppliers/search?keyword=` | Search suppliers | 200 |
| POST | `/suppliers/add` | Create supplier | 201, 400, 500 |
| PUT | `/suppliers/update/{id}` | Update supplier | 200, 400, 404, 500 |
| DELETE | `/suppliers/delete/{id}` | Delete supplier | 200, 404, 500 |

## Transactions

| Method | Endpoint | Description | Status |
|--------|----------|-------------|--------|
| GET | `/transactions/all` | Transaction history | 200 |

## Error Response Format

```json
{
  "status": 400,
  "message": "Product name is required",
  "timestamp": 1710000000000
}
```

## Analytics

| Method | Endpoint | Description | Status |
|--------|----------|-------------|--------|
| GET | `/api/analytics/forecast` | Category demand forecast | 200 |
| GET | `/api/analytics/reorder-suggestions` | Reorder recommendations | 200 |

## Reports

| Method | Endpoint | Description | Status |
|--------|----------|-------------|--------|
| GET | `/api/reports/inventory-summary` | Summary metrics report | 200 |
| GET | `/api/reports/low-stock` | Low stock report | 200 |
| GET | `/api/reports/suppliers` | Supplier directory report | 200 |
| GET | `/api/reports/transactions` | Transaction history report | 200 |

Reports return JSON with `columns` and `rows` for frontend display and CSV export.

## HTTP Status Codes

- **200** — Success
- **201** — Created
- **400** — Validation error / bad request
- **404** — Resource not found
- **500** — Internal server error
