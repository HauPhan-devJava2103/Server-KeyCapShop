-- Seed data for KeyCapShop (excluding users)
-- Run with: mysql -u <user> -p <database> < seed-data.sql

-- Categories
INSERT INTO categories (id, name, slug, description, created_at, updated_at) VALUES
  (1, 'Gaming', 'gaming', 'Gaming keyboards and accessories', CURRENT_DATE, CURRENT_DATE),
  (2, 'Office', 'van-phong', 'Office and productivity keyboards', CURRENT_DATE, CURRENT_DATE),
  (3, 'Accessories', 'phu-kien', 'Accessories and extras', CURRENT_DATE, CURRENT_DATE)
ON DUPLICATE KEY UPDATE
  name = VALUES(name),
  description = VALUES(description),
  updated_at = VALUES(updated_at);

-- Brands
INSERT INTO brands (id, name, slug, image_url, description, created_at, updated_at) VALUES
  (1, 'Lofree', 'lofree', 'https://cdn.example.com/brand/lofree.png', 'Lofree premium keyboards', CURRENT_DATE, CURRENT_DATE),
  (2, 'Evoworks', 'evoworks', 'https://cdn.example.com/brand/evoworks.png', 'Evoworks custom keyboards', CURRENT_DATE, CURRENT_DATE),
  (3, 'Piifox', 'piifox', 'https://cdn.example.com/brand/piifox.png', 'Piifox artisan keycaps', CURRENT_DATE, CURRENT_DATE)
ON DUPLICATE KEY UPDATE
  name = VALUES(name),
  image_url = VALUES(image_url),
  description = VALUES(description),
  updated_at = VALUES(updated_at);

-- Product types
INSERT INTO product_types (id, name, slug, description, created_at, updated_at) VALUES
  (1, 'Keyboard', 'ban-phim', 'Mechanical keyboard', CURRENT_DATE, CURRENT_DATE),
  (2, 'Switch', 'switch', 'Mechanical switch', CURRENT_DATE, CURRENT_DATE),
  (3, 'Keycap', 'keycap', 'Keycap set', CURRENT_DATE, CURRENT_DATE),
  (4, 'Deskmat', 'deskmat', 'Desk mat', CURRENT_DATE, CURRENT_DATE),
  (5, 'Cable', 'cap', 'Coiled cable', CURRENT_DATE, CURRENT_DATE)
ON DUPLICATE KEY UPDATE
  name = VALUES(name),
  description = VALUES(description),
  updated_at = VALUES(updated_at);

-- Products
INSERT INTO products (
  id, name, slug, status, description,
  category_id, type_id, brand_id, created_at, updated_at
) VALUES
  (1, 'Lofree Flow Lite', 'lofree-flow-lite', 'AVAILABLE', 'Low profile mechanical keyboard', 2, 1, 1, CURRENT_DATE, CURRENT_DATE),
  (2, 'Evoworks Neo 75', 'evoworks-neo-75', 'AVAILABLE', '75 percent layout custom keyboard', 1, 1, 2, CURRENT_DATE, CURRENT_DATE),
  (3, 'Piifox Aurora Keycap', 'piifox-aurora-keycap', 'AVAILABLE', 'PBT keycap set', 3, 3, 3, CURRENT_DATE, CURRENT_DATE),
  (4, 'Gateron Oil King', 'gateron-oil-king', 'AVAILABLE', 'Linear switch', 3, 2, 2, CURRENT_DATE, CURRENT_DATE),
  (5, 'Lofree Touch Deskmat', 'lofree-touch-deskmat', 'AVAILABLE', 'Large desk mat', 3, 4, 1, CURRENT_DATE, CURRENT_DATE),
  (6, 'Evoworks Coiled Cable', 'evoworks-coiled-cable', 'AVAILABLE', 'Type-C coiled cable', 3, 5, 2, CURRENT_DATE, CURRENT_DATE),
  (7, 'Lofree Block Retro', 'lofree-block-retro', 'AVAILABLE', 'Retro style keyboard', 1, 1, 1, CURRENT_DATE, CURRENT_DATE),
  (8, 'Piifox Artisan Set', 'piifox-artisan-set', 'AVAILABLE', 'Artisan keycap set', 3, 3, 3, CURRENT_DATE, CURRENT_DATE)
ON DUPLICATE KEY UPDATE
  name = VALUES(name),
  status = VALUES(status),
  description = VALUES(description),
  category_id = VALUES(category_id),
  type_id = VALUES(type_id),
  brand_id = VALUES(brand_id),
  updated_at = VALUES(updated_at);

INSERT INTO product_images (id, product_id, url, is_primary, sort_order, created_at, updated_at) VALUES
  (1, 1, 'https://cdn.example.com/product/lofree-flow-lite.png', true, 1, CURRENT_DATE, CURRENT_DATE),
  (2, 2, 'https://cdn.example.com/product/evoworks-neo-75.png', true, 1, CURRENT_DATE, CURRENT_DATE),
  (3, 3, 'https://cdn.example.com/product/piifox-aurora.png', true, 1, CURRENT_DATE, CURRENT_DATE),
  (4, 4, 'https://cdn.example.com/product/gateron-oil-king.png', true, 1, CURRENT_DATE, CURRENT_DATE),
  (5, 5, 'https://cdn.example.com/product/lofree-deskmat.png', true, 1, CURRENT_DATE, CURRENT_DATE),
  (6, 6, 'https://cdn.example.com/product/evoworks-cable.png', true, 1, CURRENT_DATE, CURRENT_DATE),
  (7, 7, 'https://cdn.example.com/product/lofree-block-retro.png', true, 1, CURRENT_DATE, CURRENT_DATE),
  (8, 8, 'https://cdn.example.com/product/piifox-artisan.png', true, 1, CURRENT_DATE, CURRENT_DATE)
ON DUPLICATE KEY UPDATE
  product_id = VALUES(product_id),
  url = VALUES(url),
  is_primary = VALUES(is_primary),
  sort_order = VALUES(sort_order),
  updated_at = VALUES(updated_at);

INSERT INTO product_variants (
  id, product_id, sku, price, original_price, percent_discount, stock_quantity, created_at, updated_at
) VALUES
  (1, 1, 'LOFREE-FLOW-LITE-WHITE', 2590000, 2990000, 13, 20, CURRENT_DATE, CURRENT_DATE),
  (2, 2, 'EVOWORKS-NEO75-BLACK', 3990000, 4390000, 9, 15, CURRENT_DATE, CURRENT_DATE),
  (3, 3, 'PIIFOX-AURORA-BASE', 890000, 990000, 10, 50, CURRENT_DATE, CURRENT_DATE),
  (4, 4, 'GATERON-OIL-KING-PCS', 18000, 22000, 18, 200, CURRENT_DATE, CURRENT_DATE),
  (5, 5, 'LOFREE-TOUCH-DESKMAT-L', 490000, 590000, 17, 40, CURRENT_DATE, CURRENT_DATE),
  (6, 6, 'EVOWORKS-COILED-CABLE-TYPEC', 350000, 420000, 17, 60, CURRENT_DATE, CURRENT_DATE),
  (7, 7, 'LOFREE-BLOCK-RETRO-GRAY', 3290000, 3690000, 11, 12, CURRENT_DATE, CURRENT_DATE),
  (8, 8, 'PIIFOX-ARTISAN-SET', 1290000, 1490000, 13, 25, CURRENT_DATE, CURRENT_DATE)
ON DUPLICATE KEY UPDATE
  product_id = VALUES(product_id),
  sku = VALUES(sku),
  price = VALUES(price),
  original_price = VALUES(original_price),
  percent_discount = VALUES(percent_discount),
  stock_quantity = VALUES(stock_quantity),
  updated_at = VALUES(updated_at);

INSERT INTO product_variant_attributes (id, variant_id, name, value, created_at, updated_at) VALUES
  (1, 1, 'Color', 'White', CURRENT_DATE, CURRENT_DATE),
  (2, 2, 'Color', 'Black', CURRENT_DATE, CURRENT_DATE),
  (3, 3, 'Profile', 'Cherry', CURRENT_DATE, CURRENT_DATE),
  (4, 4, 'Switch', 'Linear', CURRENT_DATE, CURRENT_DATE),
  (5, 5, 'Size', 'Large', CURRENT_DATE, CURRENT_DATE),
  (6, 6, 'Connector', 'Type-C', CURRENT_DATE, CURRENT_DATE),
  (7, 7, 'Color', 'Gray', CURRENT_DATE, CURRENT_DATE),
  (8, 8, 'Profile', 'Artisan', CURRENT_DATE, CURRENT_DATE)
ON DUPLICATE KEY UPDATE
  variant_id = VALUES(variant_id),
  name = VALUES(name),
  value = VALUES(value),
  updated_at = VALUES(updated_at);

INSERT INTO product_specifications (id, product_id, name, value, sort_order, created_at, updated_at) VALUES
  (1, 1, 'Layout', 'Low profile', 1, CURRENT_DATE, CURRENT_DATE),
  (2, 2, 'Layout', '75 percent', 1, CURRENT_DATE, CURRENT_DATE),
  (3, 3, 'Material', 'PBT', 1, CURRENT_DATE, CURRENT_DATE),
  (4, 4, 'Type', 'Linear switch', 1, CURRENT_DATE, CURRENT_DATE),
  (5, 5, 'Size', 'Large', 1, CURRENT_DATE, CURRENT_DATE),
  (6, 6, 'Connector', 'USB Type-C', 1, CURRENT_DATE, CURRENT_DATE),
  (7, 7, 'Style', 'Retro', 1, CURRENT_DATE, CURRENT_DATE),
  (8, 8, 'Material', 'Artisan resin', 1, CURRENT_DATE, CURRENT_DATE)
ON DUPLICATE KEY UPDATE
  product_id = VALUES(product_id),
  name = VALUES(name),
  value = VALUES(value),
  sort_order = VALUES(sort_order),
  updated_at = VALUES(updated_at);

-- Wishlists (assumes user_id = 1 exists)
INSERT INTO wishlists (user_id, product_id, created_at, updated_at) VALUES
  (1, 1, CURRENT_DATE, CURRENT_DATE),
  (1, 3, CURRENT_DATE, CURRENT_DATE),
  (1, 5, CURRENT_DATE, CURRENT_DATE)
ON DUPLICATE KEY UPDATE
  updated_at = VALUES(updated_at);

-- Orders (assumes user_id = 1 exists)
INSERT INTO orders (id, user_id, total_amount, status, shipping_address, phone_number, created_at, updated_at) VALUES
  (1, 1, 3480000, 'COMPLETED', '123 Main St, Hanoi', '0912345678', CURRENT_DATE - INTERVAL 5 DAY, CURRENT_DATE - INTERVAL 5 DAY),
  (2, 1, 4480000, 'SHIPPING', '456 Side St, HCMC', '0987654321', CURRENT_DATE - INTERVAL 2 DAY, CURRENT_DATE - INTERVAL 2 DAY),
  (3, 1, 1290000, 'PENDING', '789 Corner St, Da Nang', '0901234567', CURRENT_DATE, CURRENT_DATE)
ON DUPLICATE KEY UPDATE
  status = VALUES(status),
  total_amount = VALUES(total_amount),
  shipping_address = VALUES(shipping_address),
  phone_number = VALUES(phone_number),
  updated_at = VALUES(updated_at);

-- Order Items
INSERT INTO order_items (id, order_id, variant_id, quantity, price, created_at, updated_at) VALUES
  (1, 1, 1, 1, 2590000, CURRENT_DATE - INTERVAL 5 DAY, CURRENT_DATE - INTERVAL 5 DAY),
  (2, 1, 3, 1, 890000, CURRENT_DATE - INTERVAL 5 DAY, CURRENT_DATE - INTERVAL 5 DAY),
  (3, 2, 2, 1, 3990000, CURRENT_DATE - INTERVAL 2 DAY, CURRENT_DATE - INTERVAL 2 DAY),
  (4, 2, 5, 1, 490000, CURRENT_DATE - INTERVAL 2 DAY, CURRENT_DATE - INTERVAL 2 DAY),
  (5, 3, 8, 1, 1290000, CURRENT_DATE, CURRENT_DATE)
ON DUPLICATE KEY UPDATE
  quantity = VALUES(quantity),
  price = VALUES(price),
  updated_at = VALUES(updated_at);
