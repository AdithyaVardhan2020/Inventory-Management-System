-- Week 6 demo seed data (run after inventory_schema.sql)
-- sqlite3 database/inventory.db < database/week6_demo_seed.sql

INSERT INTO suppliers (supplier_name, contact_person, phone, email, address) VALUES
('ABC Wholesale', 'John Smith', '303-555-0101', 'john@abcwholesale.com', 'Denver, CO'),
('Tech Supplies Inc', 'Jane Doe', '303-555-0102', 'jane@techsupplies.com', 'Boulder, CO'),
('Office Depot Pro', 'Mike Johnson', '303-555-0103', 'mike@officedepot.com', 'Aurora, CO'),
('Global Electronics', 'Sarah Lee', '303-555-0104', 'sarah@globalelec.com', 'Fort Collins, CO'),
('Fresh Foods Co', 'Tom Wilson', '303-555-0105', 'tom@freshfoods.com', 'Colorado Springs, CO');

INSERT INTO products (product_name, category, quantity, price, reorder_level, supplier_id) VALUES
('Laptop Pro 15', 'Electronics', 45, 899.99, 10, 2),
('Wireless Mouse', 'Electronics', 3, 29.99, 15, 2),
('USB-C Hub', 'Electronics', 8, 49.99, 12, 4),
('Office Chair', 'Furniture', 22, 199.99, 5, 3),
('Standing Desk', 'Furniture', 2, 449.99, 8, 3),
('Notebook Pack', 'Office', 120, 12.99, 50, 3),
('Printer Paper', 'Office', 5, 8.99, 20, 3),
('Organic Coffee', 'Groceries', 35, 14.99, 10, 5),
('Energy Bars', 'Groceries', 4, 2.49, 25, 5),
('Bottled Water', 'Groceries', 200, 0.99, 50, 5),
('HD Monitor 27', 'Electronics', 18, 329.99, 6, 4),
('Keyboard Mechanical', 'Electronics', 1, 89.99, 10, 2),
('Filing Cabinet', 'Furniture', 7, 159.99, 5, 3),
('Stapler Heavy Duty', 'Office', 45, 18.99, 10, 3),
('Tea Assortment', 'Groceries', 6, 9.99, 15, 5);

INSERT INTO stock_transactions (product_id, transaction_type, quantity, notes) VALUES
(1, 'Stock In', 50, 'Initial inventory'),
(2, 'Stock Out', 12, 'Office order #1001'),
(3, 'Stock In', 20, 'Restock shipment'),
(4, 'Stock Out', 3, 'Furniture delivery'),
(5, 'Stock Out', 6, 'Standing desk orders'),
(6, 'Stock Out', 30, 'Bulk office supply'),
(7, 'Stock Out', 15, 'Paper restock needed'),
(8, 'Stock In', 40, 'Coffee delivery'),
(9, 'Stock Out', 21, 'Cafeteria order'),
(10, 'Stock Out', 50, 'Water distribution'),
(11, 'Stock In', 20, 'Monitor shipment'),
(12, 'Stock Out', 9, 'IT department'),
(13, 'Stock Out', 2, 'Office renovation'),
(14, 'Stock In', 50, 'Stapler bulk order'),
(15, 'Stock Out', 9, 'Break room restock');
