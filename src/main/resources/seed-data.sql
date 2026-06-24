-- ============================================================
-- Seed data cho KeyCapShop
-- Giả định: user_id = 5 ĐÃ TỒN TẠI trong bảng users
-- Chạy: mysql -u <user> -p <database> < seed-data.sql
-- ============================================================

-- ==========================================
-- XÓA DỮ LIỆU CŨ (DELETE - giữ cấu trúc bảng, chỉ xóa data)
-- Tắt kiểm tra FK tạm thời để tránh lỗi phụ thuộc
-- ==========================================
SET FOREIGN_KEY_CHECKS = 0;

DELETE FROM review_images;
DELETE FROM reviews;
DELETE FROM order_status_history;
DELETE FROM order_items;
DELETE FROM orders;
DELETE FROM cart_items;
DELETE FROM wishlists;
DELETE FROM addresses;
DELETE FROM product_variant_attributes;
DELETE FROM product_variants;
DELETE FROM product_specifications;
DELETE FROM product_images;
DELETE FROM products;
DELETE FROM product_types;
DELETE FROM brands;
DELETE FROM categories;

SET FOREIGN_KEY_CHECKS = 1;


-- ================== CATEGORIES ==================
INSERT INTO categories (id, name, slug, description, created_at, updated_at) VALUES
  (1, 'Bàn phím cơ', 'ban-phim-co', 'Các loại bàn phím cơ Custom và Pre-build từ nhiều thương hiệu', CURRENT_DATE, CURRENT_DATE),
  (2, 'Linh kiện', 'linh-kien', 'Switch, Plate, PCB, Foam, Stab cho phím cơ', CURRENT_DATE, CURRENT_DATE),
  (3, 'Phụ kiện', 'phu-kien', 'Deskmat, cáp xoắn, dụng cụ lube switch, nhổ keycap', CURRENT_DATE, CURRENT_DATE),
  (4, 'Keycap', 'keycap', 'Các bộ keycap PBT, ABS với nhiều profile khác nhau', CURRENT_DATE, CURRENT_DATE),
  (5, 'Chuột & Lót chuột', 'chuot-lot-chuot', 'Chuột gaming và lót chuột chất lượng cao', CURRENT_DATE, CURRENT_DATE)
AS new_data ON DUPLICATE KEY UPDATE name=new_data.name, description=new_data.description, updated_at=new_data.updated_at;

-- ================== BRANDS ==================
INSERT INTO brands (id, name, slug, image_url, description, created_at, updated_at) VALUES
  (1, 'Akko', 'akko', 'https://cdn.example.com/brand/akko.png', 'Thương hiệu Akko nổi tiếng với các bộ keycap và bàn phím rực rỡ, mức giá dễ tiếp cận.', CURRENT_DATE, CURRENT_DATE),
  (2, 'Keychron', 'keychron', 'https://cdn.example.com/brand/keychron.png', 'Thương hiệu phổ biến nhất cho người dùng Mac và dân văn phòng, hỗ trợ đa nền tảng tốt.', CURRENT_DATE, CURRENT_DATE),
  (3, 'Gateron', 'gateron', 'https://cdn.example.com/brand/gateron.png', 'Nhà sản xuất switch lớn nhất nhì thế giới, nổi bật với Gateron Yellow.', CURRENT_DATE, CURRENT_DATE),
  (4, 'Cherry', 'cherry', 'https://cdn.example.com/brand/cherry.png', 'Huyền thoại switch từ Đức, tiêu chuẩn của ngành công nghiệp phím cơ.', CURRENT_DATE, CURRENT_DATE),
  (5, 'KBDfans', 'kbdfans', 'https://cdn.example.com/brand/kbdfans.png', 'Cửa hàng custom keyboard hàng đầu, nhiều kit nhôm và nhựa chất lượng.', CURRENT_DATE, CURRENT_DATE),
  (6, 'Logitech', 'logitech', 'https://cdn.example.com/brand/logitech.png', 'Ông lớn trong ngành thiết bị ngoại vi, gaming gear.', CURRENT_DATE, CURRENT_DATE),
  (7, 'Razer', 'razer', 'https://cdn.example.com/brand/razer.png', 'Dành cho game thủ với hệ sinh thái RGB Chroma độc quyền.', CURRENT_DATE, CURRENT_DATE),
  (8, 'Womier', 'womier', 'https://cdn.example.com/brand/womier.png', 'Nổi bật với các bàn phím acrylic tản sáng LED RGB cực đẹp.', CURRENT_DATE, CURRENT_DATE)
AS new_data ON DUPLICATE KEY UPDATE name=new_data.name, image_url=new_data.image_url, description=new_data.description, updated_at=new_data.updated_at;

-- ================== PRODUCT TYPES ==================
INSERT INTO product_types (id, name, slug, description, created_at, updated_at) VALUES
  (1, 'Keyboard Kit', 'kit-ban-phim', 'Kit bàn phím chưa bao gồm switch và keycap, dành cho người thích tự build.', CURRENT_DATE, CURRENT_DATE),
  (2, 'Pre-built Keyboard', 'ban-phim-prebuild', 'Bàn phím nguyên chiếc, mua về dùng ngay.', CURRENT_DATE, CURRENT_DATE),
  (3, 'Switch', 'switch', 'Công tắc cơ học (Linear, Tactile, Clicky).', CURRENT_DATE, CURRENT_DATE),
  (4, 'Keycap Set', 'keycap-set', 'Bộ phím bấm thay thế.', CURRENT_DATE, CURRENT_DATE),
  (5, 'Deskmat', 'deskmat', 'Thảm trải bàn cỡ lớn.', CURRENT_DATE, CURRENT_DATE),
  (6, 'Chuột Gaming', 'chuot-gaming', 'Chuột máy tính tối ưu cho game thủ.', CURRENT_DATE, CURRENT_DATE),
  (7, 'Dụng cụ custom', 'dung-cu-custom', 'Dụng cụ hỗ trợ rã hàn, lube switch, mod stab.', CURRENT_DATE, CURRENT_DATE)
AS new_data ON DUPLICATE KEY UPDATE name=new_data.name, description=new_data.description, updated_at=new_data.updated_at;

-- ================== PRODUCTS ==================
INSERT INTO products (id, name, slug, status, description, category_id, type_id, brand_id, created_at, updated_at) VALUES
  (1, 'Keychron Q1 Pro', 'keychron-q1-pro', 'AVAILABLE', 'Bàn phím cơ không dây nhôm nguyên khối, gasket mount, hỗ trợ QMK/VIA.', 1, 2, 2, CURRENT_DATE, CURRENT_DATE),
  (2, 'Akko 3098B Multi-modes', 'akko-3098b', 'AVAILABLE', 'Bàn phím 98 phím, 3 chế độ kết nối, pin dung lượng lớn.', 1, 2, 1, CURRENT_DATE, CURRENT_DATE),
  (3, 'Gateron Oil King', 'gateron-oil-king', 'AVAILABLE', 'Linear switch siêu mượt, prelube từ nhà máy cực xịn, âm thanh trầm thock.', 2, 3, 3, CURRENT_DATE, CURRENT_DATE),
  (4, 'Cherry MX Brown', 'cherry-mx-brown', 'AVAILABLE', 'Tactile switch huyền thoại với độ bền siêu cao, phù hợp gõ văn bản.', 2, 3, 4, CURRENT_DATE, CURRENT_DATE),
  (5, 'KBD67 Lite R4 Kit', 'kbd67-lite-r4', 'AVAILABLE', 'Kit bàn phím layout 65% bằng nhựa ABS, gasket mount, âm hay dễ mod.', 1, 1, 5, CURRENT_DATE, CURRENT_DATE),
  (6, 'Akko ASA Profile Keycap - Macaw', 'akko-asa-macaw', 'AVAILABLE', 'Bộ keycap nhựa PBT Double-shot profile ASA cao cấp, chống mờ viền.', 4, 4, 1, CURRENT_DATE, CURRENT_DATE),
  (7, 'Deskmat KBDfans Sakura', 'deskmat-kbdfans-sakura', 'AVAILABLE', 'Thảm trải bàn kích thước 900x400x4mm, bề mặt speed, chống trượt.', 3, 5, 5, CURRENT_DATE, CURRENT_DATE),
  (8, 'Logitech G Pro X Superlight', 'logitech-g-pro-x-superlight', 'AVAILABLE', 'Chuột gaming không dây siêu nhẹ (dưới 63g), cảm biến HERO 25K.', 5, 6, 6, CURRENT_DATE, CURRENT_DATE),
  (9, 'Razer DeathAdder V3 Pro', 'razer-deathadder-v3-pro', 'AVAILABLE', 'Chuột thiết kế công thái học đỉnh cao, trọng lượng cực nhẹ 63g.', 5, 6, 7, CURRENT_DATE, CURRENT_DATE),
  (10, 'Womier K66', 'womier-k66', 'AVAILABLE', 'Bàn phím cơ case Acrylic tản sáng RGB cực mạnh.', 1, 2, 8, CURRENT_DATE, CURRENT_DATE),
  (11, 'Keychron K8 Pro', 'keychron-k8-pro', 'AVAILABLE', 'Phiên bản nâng cấp của K8, lót sẵn foam, stab ngon hơn.', 1, 2, 2, CURRENT_DATE, CURRENT_DATE),
  (12, 'Gateron Milky Yellow Pro', 'gateron-milky-yellow-pro', 'AVAILABLE', 'Switch linear quốc dân, lube sẵn, mượt, âm ấm áp.', 2, 3, 3, CURRENT_DATE, CURRENT_DATE),
  (13, 'Akko CS Jelly Pink', 'akko-cs-jelly-pink', 'AVAILABLE', 'Linear switch nhẹ nhàng 45g từ Akko, stem chống bụi.', 2, 3, 1, CURRENT_DATE, CURRENT_DATE),
  (14, 'Kit Akko MonsGeek M1', 'akko-monsgeek-m1', 'AVAILABLE', 'Kit nhôm gasket mount giá rẻ vô địch phân khúc, 75% núm xoay.', 1, 1, 1, CURRENT_DATE, CURRENT_DATE),
  (15, 'Keychron K Pro Banana', 'keychron-k-pro-banana', 'AVAILABLE', 'Tactile switch khấc sớm, pre-lubed từ Keychron.', 2, 3, 2, CURRENT_DATE, CURRENT_DATE),
  (16, 'Trạm Lube Switch', 'tram-lube-switch', 'AVAILABLE', 'Dụng cụ lube switch chất liệu mica, hỗ trợ 35 switch cùng lúc.', 3, 7, 5, CURRENT_DATE, CURRENT_DATE),
  (17, 'Cáp xoắn Custom', 'cap-xoan-custom', 'AVAILABLE', 'Cáp bọc lưới Paracord, đầu nối Aviator kim loại.', 3, 7, 5, CURRENT_DATE, CURRENT_DATE),
  (18, 'Razer BlackWidow V4', 'razer-blackwidow-v4', 'AVAILABLE', 'Bàn phím cơ full-size với switch quang học của Razer, led Chroma siêu sáng.', 1, 2, 7, CURRENT_DATE, CURRENT_DATE),
  (19, 'Keycap GMK Botanical Clone', 'gmk-botanical-clone', 'AVAILABLE', 'Keycap PBT Dyesub, profile Cherry, chủ đề thực vật Botanical.', 4, 4, 5, CURRENT_DATE, CURRENT_DATE),
  (20, 'Logitech G815', 'logitech-g815', 'UNAVAILABLE', 'Bàn phím cơ Low-profile sang trọng, switch GL Tactile.', 1, 2, 6, CURRENT_DATE, CURRENT_DATE)
AS new_data ON DUPLICATE KEY UPDATE name=new_data.name, status=new_data.status, description=new_data.description, category_id=new_data.category_id, type_id=new_data.type_id, brand_id=new_data.brand_id, updated_at=new_data.updated_at;

-- ================== PRODUCT IMAGES ==================
INSERT INTO product_images (id, product_id, url, is_primary, sort_order, created_at, updated_at) VALUES
  (1, 1, 'https://cdn.keychron.com/q1-pro-1.jpg', true, 1, CURRENT_DATE, CURRENT_DATE),
  (2, 1, 'https://cdn.keychron.com/q1-pro-2.jpg', false, 2, CURRENT_DATE, CURRENT_DATE),
  (3, 2, 'https://cdn.akko.com/3098b-1.jpg', true, 1, CURRENT_DATE, CURRENT_DATE),
  (4, 3, 'https://cdn.gateron.com/oil-king-1.jpg', true, 1, CURRENT_DATE, CURRENT_DATE),
  (5, 4, 'https://cdn.cherry.com/mx-brown.jpg', true, 1, CURRENT_DATE, CURRENT_DATE),
  (6, 5, 'https://cdn.kbdfans.com/kbd67-1.jpg', true, 1, CURRENT_DATE, CURRENT_DATE),
  (7, 6, 'https://cdn.akko.com/macaw-keycap.jpg', true, 1, CURRENT_DATE, CURRENT_DATE),
  (8, 7, 'https://cdn.kbdfans.com/deskmat-sakura.jpg', true, 1, CURRENT_DATE, CURRENT_DATE),
  (9, 8, 'https://cdn.logitech.com/gpro-superlight-1.jpg', true, 1, CURRENT_DATE, CURRENT_DATE),
  (10, 9, 'https://cdn.razer.com/dav3-1.jpg', true, 1, CURRENT_DATE, CURRENT_DATE),
  (11, 10, 'https://cdn.womier.com/k66-1.jpg', true, 1, CURRENT_DATE, CURRENT_DATE),
  (12, 11, 'https://cdn.keychron.com/k8-pro-1.jpg', true, 1, CURRENT_DATE, CURRENT_DATE),
  (13, 12, 'https://cdn.gateron.com/milky-yellow-1.jpg', true, 1, CURRENT_DATE, CURRENT_DATE),
  (14, 13, 'https://cdn.akko.com/jelly-pink-1.jpg', true, 1, CURRENT_DATE, CURRENT_DATE),
  (15, 14, 'https://cdn.akko.com/monsgeek-m1-1.jpg', true, 1, CURRENT_DATE, CURRENT_DATE),
  (16, 15, 'https://cdn.keychron.com/kpro-banana-1.jpg', true, 1, CURRENT_DATE, CURRENT_DATE),
  (17, 16, 'https://cdn.kbdfans.com/lube-station-1.jpg', true, 1, CURRENT_DATE, CURRENT_DATE),
  (18, 17, 'https://cdn.kbdfans.com/cable-1.jpg', true, 1, CURRENT_DATE, CURRENT_DATE),
  (19, 18, 'https://cdn.razer.com/bw-v4-1.jpg', true, 1, CURRENT_DATE, CURRENT_DATE),
  (20, 19, 'https://cdn.kbdfans.com/botanical-1.jpg', true, 1, CURRENT_DATE, CURRENT_DATE),
  (21, 20, 'https://cdn.logitech.com/g815-1.jpg', true, 1, CURRENT_DATE, CURRENT_DATE),
  -- Các ảnh phụ
  (22, 14, 'https://cdn.akko.com/monsgeek-m1-2.jpg', false, 2, CURRENT_DATE, CURRENT_DATE),
  (23, 14, 'https://cdn.akko.com/monsgeek-m1-3.jpg', false, 3, CURRENT_DATE, CURRENT_DATE),
  (24, 8, 'https://cdn.logitech.com/gpro-superlight-2.jpg', false, 2, CURRENT_DATE, CURRENT_DATE),
  (25, 11, 'https://cdn.keychron.com/k8-pro-2.jpg', false, 2, CURRENT_DATE, CURRENT_DATE)
AS new_data ON DUPLICATE KEY UPDATE product_id=new_data.product_id, url=new_data.url, is_primary=new_data.is_primary, sort_order=new_data.sort_order, updated_at=new_data.updated_at;

-- ================== PRODUCT VARIANTS ==================
INSERT INTO product_variants (id, product_id, sku, price, original_price, percent_discount, stock_quantity, created_at, updated_at) VALUES
  (1, 1, 'Q1P-BLK-RED', 4500000, 4800000, 6, 10, CURRENT_DATE, CURRENT_DATE),
  (2, 1, 'Q1P-WHT-BRN', 4500000, 4800000, 6, 15, CURRENT_DATE, CURRENT_DATE),
  (3, 2, 'AK3098B-CS-JEL', 2100000, 2300000, 8, 30, CURRENT_DATE, CURRENT_DATE),
  (4, 2, 'AK3098B-CS-BLU', 2100000, 2300000, 8, 25, CURRENT_DATE, CURRENT_DATE),
  (5, 3, 'GAT-OK-35', 525000, 525000, 0, 100, CURRENT_DATE, CURRENT_DATE),
  (6, 3, 'GAT-OK-70', 1050000, 1050000, 0, 50, CURRENT_DATE, CURRENT_DATE),
  (7, 3, 'GAT-OK-90', 1350000, 1350000, 0, 40, CURRENT_DATE, CURRENT_DATE),
  (8, 4, 'CHE-BRN-35', 420000, 450000, 6, 80, CURRENT_DATE, CURRENT_DATE),
  (9, 5, 'KBD67-WHT', 2500000, 2700000, 7, 20, CURRENT_DATE, CURRENT_DATE),
  (10, 5, 'KBD67-BLK', 2500000, 2700000, 7, 10, CURRENT_DATE, CURRENT_DATE),
  (11, 6, 'AK-MACAW-ASA', 990000, 1100000, 10, 40, CURRENT_DATE, CURRENT_DATE),
  (12, 7, 'DM-SAKURA-900', 350000, 400000, 12, 60, CURRENT_DATE, CURRENT_DATE),
  (13, 8, 'LOGI-GPX-WHT', 2990000, 3290000, 9, 25, CURRENT_DATE, CURRENT_DATE),
  (14, 8, 'LOGI-GPX-BLK', 2990000, 3290000, 9, 35, CURRENT_DATE, CURRENT_DATE),
  (15, 9, 'RAZ-DAV3-WHT', 3200000, 3500000, 8, 15, CURRENT_DATE, CURRENT_DATE),
  (16, 10, 'WOM-K66-GAT-RED', 1500000, 1600000, 6, 20, CURRENT_DATE, CURRENT_DATE),
  (17, 11, 'KC-K8P-ALU-RED', 2600000, 2800000, 7, 18, CURRENT_DATE, CURRENT_DATE),
  (18, 12, 'GAT-MYP-35', 210000, 210000, 0, 200, CURRENT_DATE, CURRENT_DATE),
  (19, 12, 'GAT-MYP-70', 420000, 420000, 0, 150, CURRENT_DATE, CURRENT_DATE),
  (20, 13, 'AK-JEL-PNK-45', 225000, 250000, 10, 100, CURRENT_DATE, CURRENT_DATE),
  (21, 14, 'MONS-M1-BLK', 1950000, 2200000, 11, 12, CURRENT_DATE, CURRENT_DATE),
  (22, 14, 'MONS-M1-WHT', 2050000, 2300000, 10, 8, CURRENT_DATE, CURRENT_DATE),
  (23, 15, 'KC-BANANA-35', 350000, 350000, 0, 60, CURRENT_DATE, CURRENT_DATE),
  (24, 16, 'LUBE-STATION', 150000, 150000, 0, 100, CURRENT_DATE, CURRENT_DATE),
  (25, 17, 'CABLE-COILED-WHT', 300000, 350000, 14, 50, CURRENT_DATE, CURRENT_DATE),
  (26, 18, 'RAZ-BWV4-GRN', 4200000, 4500000, 6, 15, CURRENT_DATE, CURRENT_DATE),
  (27, 19, 'GMK-BOT-CLONE', 650000, 800000, 18, 40, CURRENT_DATE, CURRENT_DATE),
  (28, 20, 'LOGI-G815-TAC', 3500000, 4000000, 12, 0, CURRENT_DATE, CURRENT_DATE)
AS new_data ON DUPLICATE KEY UPDATE product_id=new_data.product_id, sku=new_data.sku, price=new_data.price, original_price=new_data.original_price, percent_discount=new_data.percent_discount, stock_quantity=new_data.stock_quantity, updated_at=new_data.updated_at;

-- ================== PRODUCT VARIANT ATTRIBUTES ==================
INSERT INTO product_variant_attributes (id, variant_id, name, value, created_at, updated_at) VALUES
  (1, 1, 'Màu sắc', 'Đen', CURRENT_DATE, CURRENT_DATE),
  (2, 1, 'Switch', 'Keychron K Pro Red', CURRENT_DATE, CURRENT_DATE),
  (3, 2, 'Màu sắc', 'Trắng', CURRENT_DATE, CURRENT_DATE),
  (4, 2, 'Switch', 'Keychron K Pro Brown', CURRENT_DATE, CURRENT_DATE),
  (5, 3, 'Switch', 'Akko CS Jelly Pink', CURRENT_DATE, CURRENT_DATE),
  (6, 4, 'Switch', 'Akko CS Jelly Blue', CURRENT_DATE, CURRENT_DATE),
  (7, 5, 'Pack', '35 Switches', CURRENT_DATE, CURRENT_DATE),
  (8, 6, 'Pack', '70 Switches', CURRENT_DATE, CURRENT_DATE),
  (9, 7, 'Pack', '90 Switches', CURRENT_DATE, CURRENT_DATE),
  (10, 8, 'Pack', '35 Switches', CURRENT_DATE, CURRENT_DATE),
  (11, 9, 'Màu case', 'Trắng mờ (Translucent White)', CURRENT_DATE, CURRENT_DATE),
  (12, 10, 'Màu case', 'Đen nhám', CURRENT_DATE, CURRENT_DATE),
  (13, 11, 'Profile', 'ASA', CURRENT_DATE, CURRENT_DATE),
  (14, 12, 'Kích thước', '900x400x4mm', CURRENT_DATE, CURRENT_DATE),
  (15, 13, 'Màu sắc', 'Trắng', CURRENT_DATE, CURRENT_DATE),
  (16, 14, 'Màu sắc', 'Đen', CURRENT_DATE, CURRENT_DATE),
  (17, 15, 'Màu sắc', 'Trắng', CURRENT_DATE, CURRENT_DATE),
  (18, 16, 'Switch', 'Gateron Red', CURRENT_DATE, CURRENT_DATE),
  (19, 17, 'Phiên bản', 'Khung Nhôm - K Pro Red', CURRENT_DATE, CURRENT_DATE),
  (20, 18, 'Pack', '35 Switches', CURRENT_DATE, CURRENT_DATE),
  (21, 19, 'Pack', '70 Switches', CURRENT_DATE, CURRENT_DATE),
  (22, 20, 'Pack', '45 Switches', CURRENT_DATE, CURRENT_DATE),
  (23, 21, 'Màu sắc', 'Đen (Black)', CURRENT_DATE, CURRENT_DATE),
  (24, 22, 'Màu sắc', 'Trắng (White)', CURRENT_DATE, CURRENT_DATE),
  (25, 23, 'Pack', '35 Switches', CURRENT_DATE, CURRENT_DATE),
  (26, 24, 'Chất liệu', 'Mica (Acrylic)', CURRENT_DATE, CURRENT_DATE),
  (27, 25, 'Màu sắc', 'Trắng', CURRENT_DATE, CURRENT_DATE),
  (28, 26, 'Switch', 'Razer Green (Clicky)', CURRENT_DATE, CURRENT_DATE),
  (29, 27, 'Profile', 'Cherry', CURRENT_DATE, CURRENT_DATE),
  (30, 28, 'Switch', 'GL Tactile', CURRENT_DATE, CURRENT_DATE)
AS new_data ON DUPLICATE KEY UPDATE variant_id=new_data.variant_id, name=new_data.name, value=new_data.value, updated_at=new_data.updated_at;

-- ================== PRODUCT SPECIFICATIONS ==================
INSERT INTO product_specifications (id, product_id, name, value, sort_order, created_at, updated_at) VALUES
  (1, 1, 'Layout', '75%', 1, CURRENT_DATE, CURRENT_DATE),
  (2, 1, 'Kết nối', 'Bluetooth 5.1 / Type-C', 2, CURRENT_DATE, CURRENT_DATE),
  (3, 1, 'Mounting', 'Double-Gasket', 3, CURRENT_DATE, CURRENT_DATE),
  (4, 2, 'Layout', '1800 Compact (98 phím)', 1, CURRENT_DATE, CURRENT_DATE),
  (5, 2, 'Kết nối', '2.4Ghz / Bluetooth 5.0 / Type-C', 2, CURRENT_DATE, CURRENT_DATE),
  (6, 3, 'Loại Switch', 'Linear', 1, CURRENT_DATE, CURRENT_DATE),
  (7, 3, 'Lực nhấn (Bottom Out)', '65g', 2, CURRENT_DATE, CURRENT_DATE),
  (8, 4, 'Loại Switch', 'Tactile', 1, CURRENT_DATE, CURRENT_DATE),
  (9, 5, 'Layout', '65%', 1, CURRENT_DATE, CURRENT_DATE),
  (10, 6, 'Chất liệu', 'PBT Double-shot', 1, CURRENT_DATE, CURRENT_DATE),
  (11, 8, 'Trọng lượng', '< 63g', 1, CURRENT_DATE, CURRENT_DATE),
  (12, 8, 'Cảm biến', 'HERO 25K', 2, CURRENT_DATE, CURRENT_DATE),
  (13, 9, 'Trọng lượng', '63g', 1, CURRENT_DATE, CURRENT_DATE),
  (14, 10, 'Case', 'Acrylic Layered', 1, CURRENT_DATE, CURRENT_DATE),
  (15, 11, 'Layout', 'TKL (80%)', 1, CURRENT_DATE, CURRENT_DATE),
  (16, 12, 'Lực nhấn', '50g', 1, CURRENT_DATE, CURRENT_DATE),
  (17, 14, 'Chất liệu vỏ', 'Nhôm CNC nguyên khối', 1, CURRENT_DATE, CURRENT_DATE),
  (18, 14, 'Kết nối', 'Chỉ có dây (Type-C)', 2, CURRENT_DATE, CURRENT_DATE)
AS new_data ON DUPLICATE KEY UPDATE product_id=new_data.product_id, name=new_data.name, value=new_data.value, sort_order=new_data.sort_order, updated_at=new_data.updated_at;

-- ================== ADDRESSES ==================
INSERT INTO addresses (id, user_id, recipient_name, phone_number,
  province_code, province_name, district_code, district_name,
  ward_code, ward_name, street, full_address,
  latitude, longitude, is_default, created_at, updated_at) VALUES
  (1, 5, 'Nguyễn Văn Test', '0987654321',
    '201', 'Hà Nội', '1482', 'Quận Cầu Giấy',
    '11007', 'Phường Dịch Vọng', 'Số 1 Xuân Thủy',
    'Số 1 Xuân Thủy, Phường Dịch Vọng, Quận Cầu Giấy, Hà Nội',
    21.0285, 105.7823, true, CURRENT_DATE, CURRENT_DATE),
  (2, 5, 'Nguyễn Văn Test', '0987654321',
    '202', 'Hồ Chí Minh', '1442', 'Quận 1',
    '20101', 'Phường Bến Nghé', '2 Nguyễn Đình Chiểu',
    '2 Nguyễn Đình Chiểu, Phường Bến Nghé, Quận 1, Hồ Chí Minh',
    10.7769, 106.7009, false, CURRENT_DATE, CURRENT_DATE),
  (3, 5, 'Nguyễn Văn Test (VP)', '0987654321',
    '203', 'Đà Nẵng', '1521', 'Quận Hải Châu',
    '30101', 'Phường Hải Châu 1', '50 Lê Duẩn',
    '50 Lê Duẩn, Phường Hải Châu 1, Quận Hải Châu, Đà Nẵng',
    16.0721, 108.2215, false, CURRENT_DATE, CURRENT_DATE)
AS new_data ON DUPLICATE KEY UPDATE recipient_name=new_data.recipient_name, phone_number=new_data.phone_number,
  province_code=new_data.province_code, province_name=new_data.province_name,
  district_code=new_data.district_code, district_name=new_data.district_name,
  ward_code=new_data.ward_code, ward_name=new_data.ward_name,
  street=new_data.street, full_address=new_data.full_address,
  latitude=new_data.latitude, longitude=new_data.longitude,
  is_default=new_data.is_default, updated_at=new_data.updated_at;

-- ================== WISHLISTS ==================
INSERT INTO wishlists (id, user_id, product_id, created_at, updated_at) VALUES
  (1, 5, 1, CURRENT_DATE, CURRENT_DATE),
  (2, 5, 3, CURRENT_DATE, CURRENT_DATE),
  (3, 5, 7, CURRENT_DATE, CURRENT_DATE),
  (4, 5, 14, CURRENT_DATE, CURRENT_DATE),
  (5, 5, 19, CURRENT_DATE, CURRENT_DATE)
AS new_data ON DUPLICATE KEY UPDATE updated_at=new_data.updated_at;

-- ================== CART ITEMS ==================
INSERT INTO cart_items (id, user_id, variant_id, quantity, created_at, updated_at) VALUES
  (1, 5, 4, 1, CURRENT_DATE, CURRENT_DATE),
  (2, 5, 14, 1, CURRENT_DATE, CURRENT_DATE),
  (3, 5, 21, 1, CURRENT_DATE, CURRENT_DATE)
AS new_data ON DUPLICATE KEY UPDATE quantity=new_data.quantity, updated_at=new_data.updated_at;

-- ================== ORDERS ==================
INSERT INTO orders (id, user_id, total_amount, status, shipping_fee, payment_status,
  payment_method, transaction_id, address_id, created_at, updated_at) VALUES
  (1, 5, 4530000, 'SUCCESS', 30000, 'PAID', 'COD', NULL, 1, CURRENT_DATE - INTERVAL 30 DAY, CURRENT_DATE - INTERVAL 25 DAY),
  (2, 5, 2535000, 'SUCCESS', 35000, 'PAID', 'VNPAY', 'VNPAY_12301', 2, CURRENT_DATE - INTERVAL 15 DAY, CURRENT_DATE - INTERVAL 12 DAY),
  (3, 5, 3125000, 'SHIPPING', 35000, 'PAID', 'MOMO', 'MOMO_12302', 1, CURRENT_DATE - INTERVAL 3 DAY, CURRENT_DATE - INTERVAL 1 DAY),
  (4, 5, 1385000, 'PREPARING', 35000, 'PENDING', 'COD', NULL, 3, CURRENT_DATE - INTERVAL 1 DAY, CURRENT_DATE - INTERVAL 1 DAY),
  (5, 5, 5250000, 'PENDING', 30000, 'PAID', 'VNPAY', 'VNPAY_12303', 1, CURRENT_DATE - INTERVAL 1 DAY, CURRENT_DATE - INTERVAL 1 DAY),
  (6, 5, 990000, 'PENDING', 30000, 'PENDING', 'COD', NULL, 1, CURRENT_DATE, CURRENT_DATE),
  (7, 5, 4500000, 'CANCELLED', 30000, 'FAILED', 'MOMO', 'MOMO_FAIL_01', 2, CURRENT_DATE - INTERVAL 5 DAY, CURRENT_DATE - INTERVAL 5 DAY),
  (8, 5, 420000, 'SUCCESS', 25000, 'PAID', 'COD', NULL, 3, CURRENT_DATE - INTERVAL 60 DAY, CURRENT_DATE - INTERVAL 55 DAY),
  (9, 5, 3800000, 'SUCCESS', 40000, 'PAID', 'VNPAY', 'VNPAY_OLD_09', 1, CURRENT_DATE - INTERVAL 90 DAY, CURRENT_DATE - INTERVAL 86 DAY),
  (10, 5, 650000, 'CANCELLED', 25000, 'PENDING', 'COD', NULL, 1, CURRENT_DATE - INTERVAL 10 DAY, CURRENT_DATE - INTERVAL 9 DAY)
AS new_data ON DUPLICATE KEY UPDATE status=new_data.status, total_amount=new_data.total_amount,
  shipping_fee=new_data.shipping_fee, payment_status=new_data.payment_status,
  payment_method=new_data.payment_method, transaction_id=new_data.transaction_id,
  address_id=new_data.address_id, updated_at=new_data.updated_at;

-- ================== ORDER ITEMS ==================
INSERT INTO order_items (id, order_id, variant_id, quantity, price, created_at, updated_at) VALUES
  (1, 1, 1, 1, 4500000, CURRENT_DATE - INTERVAL 30 DAY, CURRENT_DATE - INTERVAL 30 DAY),
  (2, 2, 9, 1, 2500000, CURRENT_DATE - INTERVAL 15 DAY, CURRENT_DATE - INTERVAL 15 DAY),
  (3, 3, 3, 1, 2100000, CURRENT_DATE - INTERVAL 3 DAY, CURRENT_DATE - INTERVAL 3 DAY),
  (4, 3, 11, 1, 990000, CURRENT_DATE - INTERVAL 3 DAY, CURRENT_DATE - INTERVAL 3 DAY),
  (5, 4, 6, 1, 1050000, CURRENT_DATE - INTERVAL 1 DAY, CURRENT_DATE - INTERVAL 1 DAY),
  (6, 4, 25, 1, 300000, CURRENT_DATE - INTERVAL 1 DAY, CURRENT_DATE - INTERVAL 1 DAY),
  (7, 5, 21, 1, 1950000, CURRENT_DATE - INTERVAL 1 DAY, CURRENT_DATE - INTERVAL 1 DAY),
  (8, 5, 26, 1, 3270000, CURRENT_DATE - INTERVAL 1 DAY, CURRENT_DATE - INTERVAL 1 DAY),
  (9, 6, 11, 1, 990000, CURRENT_DATE, CURRENT_DATE),
  (10, 7, 2, 1, 4500000, CURRENT_DATE - INTERVAL 5 DAY, CURRENT_DATE - INTERVAL 5 DAY),
  (11, 8, 19, 1, 420000, CURRENT_DATE - INTERVAL 60 DAY, CURRENT_DATE - INTERVAL 60 DAY),
  (12, 9, 13, 1, 2990000, CURRENT_DATE - INTERVAL 90 DAY, CURRENT_DATE - INTERVAL 90 DAY),
  (13, 9, 27, 1, 650000, CURRENT_DATE - INTERVAL 90 DAY, CURRENT_DATE - INTERVAL 90 DAY),
  (14, 10, 27, 1, 650000, CURRENT_DATE - INTERVAL 10 DAY, CURRENT_DATE - INTERVAL 10 DAY)
AS new_data ON DUPLICATE KEY UPDATE quantity=new_data.quantity, price=new_data.price, updated_at=new_data.updated_at;

-- ================== ORDER STATUS HISTORY ==================
INSERT INTO order_status_history (id, order_id, from_status, to_status, note, created_by, created_at) VALUES
  (1, 1, NULL, 'PENDING', 'Đơn hàng được tạo', 5, CURRENT_DATE - INTERVAL 30 DAY),
  (3, 1, 'PENDING', 'PREPARING', 'Đang chuẩn bị hàng', NULL, CURRENT_DATE - INTERVAL 28 DAY),
  (4, 1, 'PREPARING', 'SHIPPING', 'Đã giao cho vận chuyển', NULL, CURRENT_DATE - INTERVAL 27 DAY),
  (5, 1, 'SHIPPING', 'SUCCESS', 'Giao hàng thành công', NULL, CURRENT_DATE - INTERVAL 25 DAY),
  
  (6, 2, NULL, 'PENDING', 'Đơn hàng được tạo', 5, CURRENT_DATE - INTERVAL 15 DAY),
  (8, 2, 'PENDING', 'SHIPPING', 'Giao cho vận chuyển GHN', NULL, CURRENT_DATE - INTERVAL 14 DAY),
  (9, 2, 'SHIPPING', 'SUCCESS', 'Đã nhận hàng', NULL, CURRENT_DATE - INTERVAL 12 DAY),
  
  (10, 3, NULL, 'PENDING', 'Đơn hàng được tạo', 5, CURRENT_DATE - INTERVAL 3 DAY),
  (12, 3, 'PENDING', 'PREPARING', 'Đang chuẩn bị', NULL, CURRENT_DATE - INTERVAL 2 DAY),
  (13, 3, 'PREPARING', 'SHIPPING', 'Bàn giao Shipper', NULL, CURRENT_DATE - INTERVAL 1 DAY),
  
  (14, 4, NULL, 'PENDING', 'Đơn hàng COD', 5, CURRENT_DATE - INTERVAL 1 DAY),
  (16, 4, 'PENDING', 'PREPARING', 'Đóng gói sản phẩm', NULL, CURRENT_DATE),
  
  (17, 5, NULL, 'PENDING', 'Đơn hàng được tạo', 5, CURRENT_DATE - INTERVAL 1 DAY),
  
  (19, 6, NULL, 'PENDING', 'Đơn hàng mới tạo', 5, CURRENT_DATE),
  
  (20, 7, NULL, 'PENDING', 'Đơn hàng được tạo', 5, CURRENT_DATE - INTERVAL 5 DAY),
  (21, 7, 'PENDING', 'CANCELLED', 'Giao dịch MoMo bị hủy', NULL, CURRENT_DATE - INTERVAL 5 DAY),

  (22, 10, NULL, 'PENDING', 'Đơn hàng COD', 5, CURRENT_DATE - INTERVAL 10 DAY),
  (23, 10, 'PENDING', 'CANCELLED', 'Khách hàng yêu cầu hủy', 5, CURRENT_DATE - INTERVAL 9 DAY)
AS new_data ON DUPLICATE KEY UPDATE from_status=new_data.from_status, to_status=new_data.to_status,
  note=new_data.note, created_by=new_data.created_by, created_at=new_data.created_at;

-- ================== REVIEWS ==================
INSERT INTO reviews (id, product_id, user_id, rating, content, created_at, updated_at) VALUES
  (1, 1, 5, 5, 'Bàn phím rất chất lượng, đánh máy rất sướng tay. Gasket mount mềm mại, âm thanh thock!', CURRENT_DATE - INTERVAL 20 DAY, CURRENT_DATE - INTERVAL 20 DAY),
  (2, 5, 5, 4, 'Kit build tốt nhưng stab đi kèm hơi sạn, cần mod lại một chút mới ngon được.', CURRENT_DATE - INTERVAL 10 DAY, CURRENT_DATE - INTERVAL 10 DAY),
  (3, 12, 5, 5, 'Switch Gateron Milky Yellow Pro quốc dân rồi, lube sẵn cực mượt, âm trầm ấm.', CURRENT_DATE - INTERVAL 45 DAY, CURRENT_DATE - INTERVAL 45 DAY),
  (4, 8, 5, 5, 'Chuột siêu nhẹ, flick tâm CS:GO cực chuẩn. Pin trâu bò dùng 2 tuần chưa hết.', CURRENT_DATE - INTERVAL 80 DAY, CURRENT_DATE - INTERVAL 80 DAY)
AS new_data ON DUPLICATE KEY UPDATE rating=new_data.rating, content=new_data.content, updated_at=new_data.updated_at;

INSERT INTO review_images (review_id, image_url) VALUES
  (1, 'https://cdn.example.com/reviews/q1-pro-review-1.jpg'),
  (1, 'https://cdn.example.com/reviews/q1-pro-review-2.jpg'),
  (2, 'https://cdn.example.com/reviews/kbd67-review-1.jpg'),
  (4, 'https://cdn.example.com/reviews/gpro-review-1.jpg');
