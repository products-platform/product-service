CREATE TABLE products
(
    id              BIGSERIAL PRIMARY KEY,
    sku             VARCHAR(50)    NOT NULL UNIQUE,
    name            VARCHAR(200)   NOT NULL,
    brand           VARCHAR(100)   NOT NULL,
    category        VARCHAR(100)   NOT NULL,
    price           NUMERIC(15, 2) NOT NULL,
    weight_in_grams INT            NOT NULL,
    active          BOOLEAN                 DEFAULT TRUE,
    description     VARCHAR(1000),
    created_at      TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_product_name ON products (name);
CREATE INDEX idx_product_category ON products (category);
CREATE INDEX idx_product_sku ON products (sku);

INSERT INTO products (sku, name, brand, category, price, weight_in_grams, description)
VALUES ('APL-IP15-128-BLK', 'iPhone 15 128GB Black', 'Apple', 'Smartphones', 79999, 172,
        'Latest Apple iPhone 15 with A16 chip'),
       ('SMS-S23-256-GRN', 'Samsung Galaxy S23 256GB Green', 'Samsung', 'Smartphones', 74999, 168,
        'Flagship Samsung phone with AMOLED display'),
       ('SON-WH1000XM5', 'Sony WH-1000XM5 Headphones', 'Sony', 'Audio', 29999, 250,
        'Industry-leading noise cancellation'),
       ('DEL-XPS13-2024', 'Dell XPS 13 2024', 'Dell', 'Laptops', 125000, 1200, 'Ultra portable premium laptop'),
       ('HP-OMEN-16', 'HP Omen 16 Gaming Laptop', 'HP', 'Laptops', 145000, 2300, 'High performance gaming laptop'),
       ('APL-MBP-M3', 'MacBook Pro M3 14-inch', 'Apple', 'Laptops', 189000, 1400, 'Apple Silicon M3 chip laptop'),
       ('NKE-AIRMAX-2024', 'Nike Air Max 2024', 'Nike', 'Footwear', 12999, 900, 'Premium running shoes'),
       ('ADS-ULTRABOOST-23', 'Adidas Ultraboost 23', 'Adidas', 'Footwear', 13999, 850,
        'Comfortable performance running shoes'),
       ('SNY-PS5-DIG', 'PlayStation 5 Digital Edition', 'Sony', 'Gaming', 44999, 4500, 'Next-gen gaming console'),
       ('MS-XBOX-SX', 'Xbox Series X', 'Microsoft', 'Gaming', 49999, 4200, 'Powerful gaming console'),
       ('BOAT-ROCKERZ-550', 'Boat Rockerz 550', 'Boat', 'Audio', 1999, 220, 'Affordable wireless headphones'),
       ('JBL-FLIP6', 'JBL Flip 6 Bluetooth Speaker', 'JBL', 'Audio', 9999, 550, 'Portable waterproof speaker'),
       ('LG-OLED-C3-55', 'LG OLED C3 55 inch TV', 'LG', 'Television', 149999, 18000, '4K OLED Smart TV'),
       ('SNY-BRAVIA-65X90L', 'Sony Bravia 65X90L', 'Sony', 'Television', 169999, 20000, 'Premium 65 inch LED TV'),
       ('APL-IPAD-AIR5', 'iPad Air 5th Gen', 'Apple', 'Tablets', 59999, 460, 'Powerful lightweight tablet'),
       ('SMS-TAB-S9', 'Samsung Galaxy Tab S9', 'Samsung', 'Tablets', 68999, 480, 'High-end Android tablet'),
       ('PUM-RS-X', 'Puma RS-X Sneakers', 'Puma', 'Footwear', 8999, 950, 'Stylish casual sneakers'),
       ('LOG-MXMASTER3', 'Logitech MX Master 3', 'Logitech', 'Accessories', 8999, 140, 'Advanced wireless mouse'),
       ('APL-AIRPODS-PRO2', 'AirPods Pro 2', 'Apple', 'Audio', 24999, 60, 'Active noise cancellation earbuds'),
       ('SAM-T7-1TB', 'Samsung T7 1TB SSD', 'Samsung', 'Storage', 10999, 120, 'Portable high-speed SSD');