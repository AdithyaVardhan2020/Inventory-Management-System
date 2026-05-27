INSERT INTO suppliers (supplier_name, contact_person, phone, email, address)
VALUES ('ABC Wholesale', 'John Smith', '1234567890', 'abc@example.com', 'Denver, CO');

INSERT INTO products (product_name, category, quantity, price, reorder_level, supplier_id)
VALUES ('Laptop', 'Electronics', 15, 750.00, 5, 1);

INSERT INTO stock_transactions (product_id, transaction_type, quantity, notes)
VALUES (1, 'Stock In', 15, 'Initial stock added');
