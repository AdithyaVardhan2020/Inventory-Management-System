CREATE TABLE users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    full_name VARCHAR(100),
    email VARCHAR(100) UNIQUE,
    role VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE products (
    product_id INT PRIMARY KEY AUTO_INCREMENT,
    product_name VARCHAR(100),
    category VARCHAR(100),
    quantity INT,
    unit_price DECIMAL(10,2),
    reorder_level INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE vendors (
    vendor_id INT PRIMARY KEY AUTO_INCREMENT,
    vendor_name VARCHAR(100),
    contact_email VARCHAR(100),
    phone VARCHAR(20),
    supplied_category VARCHAR(100),
    rating DECIMAL(3,2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE inventory_transactions (
    transaction_id INT PRIMARY KEY AUTO_INCREMENT,
    product_id INT,
    transaction_type VARCHAR(20),
    quantity INT,
    transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES products(product_id)
);

CREATE TABLE purchase_requests (
    request_id INT PRIMARY KEY AUTO_INCREMENT,
    product_id INT,
    requested_quantity INT,
    status VARCHAR(50),
    requested_by INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES products(product_id),
    FOREIGN KEY (requested_by) REFERENCES users(user_id)
);

CREATE TABLE purchase_orders (
    order_id INT PRIMARY KEY AUTO_INCREMENT,
    request_id INT,
    vendor_id INT,
    order_status VARCHAR(50),
    total_amount DECIMAL(10,2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (request_id) REFERENCES purchase_requests(request_id),
    FOREIGN KEY (vendor_id) REFERENCES vendors(vendor_id)
);