-- Drop order_items table first as it likely has foreign keys to orders
DROP TABLE IF EXISTS order_items CASCADE;

-- Then drop the orders table
DROP TABLE IF EXISTS orders CASCADE; 