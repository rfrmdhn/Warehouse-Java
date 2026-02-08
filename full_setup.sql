-- Nuke and Rebuild
DROP DATABASE IF EXISTS warehouse;
CREATE DATABASE warehouse;
USE warehouse;

-- Create Tables
CREATE TABLE items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255)
);

CREATE TABLE variants (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    item_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    price DECIMAL(38,2) NOT NULL,
    stock_quantity INT NOT NULL,
    CONSTRAINT fk_item FOREIGN KEY (item_id) REFERENCES items(id) ON DELETE CASCADE
);

-- Insert Data
INSERT INTO items (name, description) VALUES 
('Gaming Laptop', 'High performance laptop'),
('Cotton T-Shirt', 'Comfortable daily wear');

INSERT INTO variants (item_id, name, price, stock_quantity) VALUES 
(1, '16GB RAM / 512GB SSD', 1200.00, 10),
(1, '32GB RAM / 1TB SSD', 1500.00, 5),
(2, 'Size M - Black', 15.00, 100),
(2, 'Size L - White', 15.00, 80);

-- Verify
SELECT * FROM items;
SELECT * FROM variants;
