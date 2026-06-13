-- ============================================================
-- Seed data cho KeyCapShop
-- Giả định: user_id = 1 ĐÃ TỒN TẠI trong bảng users
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
-- Entity: Category (id, name, slug, description, created_at, updated_at)
INSERT INTO categories (id, name, slug, description, created_at, updated_at) VALUES
  (1, 'Bàn phím cơ', 'ban-phim-co', 'Các loại bàn phím cơ Custom và Pre-build', CURRENT_DATE, CURRENT_DATE),
  (2, 'Linh kiện', 'linh-kien', 'Switch, Keycap, Plate, PCB', CURRENT_DATE, CURRENT_DATE),
  (3, 'Phụ kiện', 'phu-kien', 'Deskmat, cáp xoắn, dụng cụ lube', CURRENT_DATE, CURRENT_DATE)
AS new_data ON DUPLICATE KEY UPDATE name=new_data.name, description=new_data.description, updated_at=new_data.updated_at;

-- ================== BRANDS ==================
-- Entity: Brand (id, name, slug, image_url, description, created_at, updated_at)
INSERT INTO brands (id, name, slug, image_url, description, created_at, updated_at) VALUES
  (1, 'Akko', 'akko', 'https://cdn.example.com/brand/akko.png', 'Thương hiệu Akko nổi tiếng với các bộ keycap và bàn phím rực rỡ', CURRENT_DATE, CURRENT_DATE),
  (2, 'Keychron', 'keychron', 'https://cdn.example.com/brand/keychron.png', 'Thương hiệu phổ biến nhất cho người dùng Mac và văn phòng', CURRENT_DATE, CURRENT_DATE),
  (3, 'Gateron', 'gateron', 'https://cdn.example.com/brand/gateron.png', 'Nhà sản xuất switch lớn nhất nhì thế giới', CURRENT_DATE, CURRENT_DATE),
  (4, 'Cherry', 'cherry', 'https://cdn.example.com/brand/cherry.png', 'Huyền thoại switch từ Đức', CURRENT_DATE, CURRENT_DATE),
  (5, 'KBDfans', 'kbdfans', 'https://cdn.example.com/brand/kbdfans.png', 'Cửa hàng custom keyboard hàng đầu', CURRENT_DATE, CURRENT_DATE)
AS new_data ON DUPLICATE KEY UPDATE name=new_data.name, image_url=new_data.image_url, description=new_data.description, updated_at=new_data.updated_at;

-- ================== PRODUCT TYPES ==================
-- Entity: ProductType (id, name, slug, description, created_at, updated_at)
INSERT INTO product_types (id, name, slug, description, created_at, updated_at) VALUES
  (1, 'Keyboard Kit', 'kit-ban-phim', 'Kit bàn phím chưa bao gồm switch và keycap', CURRENT_DATE, CURRENT_DATE),
  (2, 'Pre-built Keyboard', 'ban-phim-prebuild', 'Bàn phím nguyên chiếc', CURRENT_DATE, CURRENT_DATE),
  (3, 'Switch', 'switch', 'Công tắc cơ học', CURRENT_DATE, CURRENT_DATE),
  (4, 'Keycap Set', 'keycap', 'Bộ phím bấm', CURRENT_DATE, CURRENT_DATE),
  (5, 'Deskmat', 'deskmat', 'Thảm trải bàn', CURRENT_DATE, CURRENT_DATE)
AS new_data ON DUPLICATE KEY UPDATE name=new_data.name, description=new_data.description, updated_at=new_data.updated_at;

-- ================== PRODUCTS ==================
-- Entity: Product (id, name, slug, description, status[AVAILABLE|UNAVAILABLE], category_id, type_id, brand_id, created_at, updated_at)
INSERT INTO products (id, name, slug, status, description, category_id, type_id, brand_id, created_at, updated_at) VALUES
  (1, 'Keychron Q1 Pro', 'keychron-q1-pro', 'AVAILABLE', 'Bàn phím cơ không dây nhôm nguyên khối, gasket mount, hỗ trợ QMK/VIA.', 1, 2, 2, CURRENT_DATE, CURRENT_DATE),
  (2, 'Akko 3098B Multi-modes', 'akko-3098b', 'AVAILABLE', 'Bàn phím 98 phím, 3 chế độ kết nối, switch CS.', 1, 2, 1, CURRENT_DATE, CURRENT_DATE),
  (3, 'Gateron Oil King', 'gateron-oil-king', 'AVAILABLE', 'Linear switch siêu mượt, prelube từ nhà máy. Lực nhấn 55g.', 2, 3, 3, CURRENT_DATE, CURRENT_DATE),
  (4, 'Cherry MX Brown', 'cherry-mx-brown', 'AVAILABLE', 'Tactile switch huyền thoại với độ bền cao.', 2, 3, 4, CURRENT_DATE, CURRENT_DATE),
  (5, 'KBD67 Lite R4 Kit', 'kbd67-lite-r4', 'AVAILABLE', 'Kit bàn phím layout 65% bằng nhựa ABS, gasket mount.', 1, 1, 5, CURRENT_DATE, CURRENT_DATE),
  (6, 'Akko ASA Profile Keycap - Macaw', 'akko-asa-macaw', 'AVAILABLE', 'Bộ keycap nhựa PBT Double-shot profile ASA cao cấp.', 2, 4, 1, CURRENT_DATE, CURRENT_DATE),
  (7, 'Deskmat KBDfans Sakura', 'deskmat-kbdfans-sakura', 'AVAILABLE', 'Thảm trải bàn kích thước 900x400x4mm, chống trượt.', 3, 5, 5, CURRENT_DATE, CURRENT_DATE)
AS new_data ON DUPLICATE KEY UPDATE name=new_data.name, status=new_data.status, description=new_data.description, category_id=new_data.category_id, type_id=new_data.type_id, brand_id=new_data.brand_id, updated_at=new_data.updated_at;

-- ================== PRODUCT IMAGES ==================
-- Entity: ProductImage (id, product_id, url, is_primary, sort_order, created_at, updated_at)
INSERT INTO product_images (id, product_id, url, is_primary, sort_order, created_at, updated_at) VALUES
  (1, 1, 'https://cdn.keychron.com/q1-pro-1.jpg', true, 1, CURRENT_DATE, CURRENT_DATE),
  (2, 1, 'https://cdn.keychron.com/q1-pro-2.jpg', false, 2, CURRENT_DATE, CURRENT_DATE),
  (3, 2, 'https://cdn.akko.com/3098b-1.jpg', true, 1, CURRENT_DATE, CURRENT_DATE),
  (4, 3, 'https://cdn.gateron.com/oil-king-1.jpg', true, 1, CURRENT_DATE, CURRENT_DATE),
  (5, 4, 'https://cdn.cherry.com/mx-brown.jpg', true, 1, CURRENT_DATE, CURRENT_DATE),
  (6, 5, 'https://cdn.kbdfans.com/kbd67-1.jpg', true, 1, CURRENT_DATE, CURRENT_DATE),
  (7, 6, 'https://cdn.akko.com/macaw-keycap.jpg', true, 1, CURRENT_DATE, CURRENT_DATE),
  (8, 7, 'https://cdn.kbdfans.com/deskmat-sakura.jpg', true, 1, CURRENT_DATE, CURRENT_DATE)
AS new_data ON DUPLICATE KEY UPDATE product_id=new_data.product_id, url=new_data.url, is_primary=new_data.is_primary, sort_order=new_data.sort_order, updated_at=new_data.updated_at;

-- ================== PRODUCT VARIANTS ==================
-- Entity: ProductVariant (id, product_id, sku, price, original_price, percent_discount, stock_quantity, created_at, updated_at)
INSERT INTO product_variants (id, product_id, sku, price, original_price, percent_discount, stock_quantity, created_at, updated_at) VALUES
  (1, 1, 'Q1P-BLK-RED', 4500000, 4800000, 6, 10, CURRENT_DATE, CURRENT_DATE),
  (2, 1, 'Q1P-WHT-BRN', 4500000, 4800000, 6, 15, CURRENT_DATE, CURRENT_DATE),
  (3, 2, 'AK3098B-CS-JEL', 2100000, 2300000, 8, 30, CURRENT_DATE, CURRENT_DATE),
  (4, 3, 'GAT-OK-35PCS', 525000, 525000, 0, 100, CURRENT_DATE, CURRENT_DATE),
  (5, 3, 'GAT-OK-70PCS', 1050000, 1050000, 0, 50, CURRENT_DATE, CURRENT_DATE),
  (6, 4, 'CHE-BRN-10PCS', 120000, 120000, 0, 200, CURRENT_DATE, CURRENT_DATE),
  (7, 5, 'KBD67-WHT', 2500000, 2700000, 7, 20, CURRENT_DATE, CURRENT_DATE),
  (8, 6, 'AK-MACAW-ASA', 990000, 1100000, 10, 40, CURRENT_DATE, CURRENT_DATE),
  (9, 7, 'DM-SAKURA-900', 350000, 400000, 12, 60, CURRENT_DATE, CURRENT_DATE)
AS new_data ON DUPLICATE KEY UPDATE product_id=new_data.product_id, sku=new_data.sku, price=new_data.price, original_price=new_data.original_price, percent_discount=new_data.percent_discount, stock_quantity=new_data.stock_quantity, updated_at=new_data.updated_at;

-- ================== PRODUCT VARIANT ATTRIBUTES ==================
-- Entity: ProductVariantAttribute (id, variant_id, name, value, created_at, updated_at)
INSERT INTO product_variant_attributes (id, variant_id, name, value, created_at, updated_at) VALUES
  (1, 1, 'Màu sắc', 'Đen', CURRENT_DATE, CURRENT_DATE),
  (2, 1, 'Switch', 'Keychron K Pro Red', CURRENT_DATE, CURRENT_DATE),
  (3, 2, 'Màu sắc', 'Trắng', CURRENT_DATE, CURRENT_DATE),
  (4, 2, 'Switch', 'Keychron K Pro Brown', CURRENT_DATE, CURRENT_DATE),
  (5, 3, 'Switch', 'Akko CS Jelly Pink', CURRENT_DATE, CURRENT_DATE),
  (6, 4, 'Pack', '35 Switches', CURRENT_DATE, CURRENT_DATE),
  (7, 5, 'Pack', '70 Switches', CURRENT_DATE, CURRENT_DATE),
  (8, 6, 'Pack', '10 Switches', CURRENT_DATE, CURRENT_DATE),
  (9, 7, 'Màu case', 'Trắng mờ (Translucent White)', CURRENT_DATE, CURRENT_DATE),
  (10, 8, 'Profile', 'ASA', CURRENT_DATE, CURRENT_DATE),
  (11, 9, 'Kích thước', '900x400x4mm', CURRENT_DATE, CURRENT_DATE)
AS new_data ON DUPLICATE KEY UPDATE variant_id=new_data.variant_id, name=new_data.name, value=new_data.value, updated_at=new_data.updated_at;

-- ================== PRODUCT SPECIFICATIONS ==================
-- Entity: ProductSpecification (id, product_id, name, value, sort_order, created_at, updated_at)
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
  (10, 6, 'Chất liệu', 'PBT Double-shot', 1, CURRENT_DATE, CURRENT_DATE)
AS new_data ON DUPLICATE KEY UPDATE product_id=new_data.product_id, name=new_data.name, value=new_data.value, sort_order=new_data.sort_order, updated_at=new_data.updated_at;

-- ================== ADDRESSES ==================
-- Entity: Address (id, user_id, recipient_name, phone_number, province_code, province_name,
--                  district_code, district_name, ward_code, ward_name,
--                  street, full_address, latitude, longitude, is_default, created_at, updated_at)
-- Mã district_code / ward_code theo GHN API thực tế
INSERT INTO addresses (id, user_id, recipient_name, phone_number,
  province_code, province_name, district_code, district_name,
  ward_code, ward_name, street, full_address,
  latitude, longitude, is_default, created_at, updated_at) VALUES
  (1, 1, 'Nguyễn Văn Test', '0987654321',
    '201', 'Hà Nội', '1482', 'Quận Cầu Giấy',
    '11007', 'Phường Dịch Vọng', 'Số 1 Xuân Thủy',
    'Số 1 Xuân Thủy, Phường Dịch Vọng, Quận Cầu Giấy, Hà Nội',
    21.0285, 105.7823, true, CURRENT_DATE, CURRENT_DATE),
  (2, 1, 'Nguyễn Văn Test', '0987654321',
    '202', 'Hồ Chí Minh', '1442', 'Quận 1',
    '20101', 'Phường Bến Nghé', '2 Nguyễn Đình Chiểu',
    '2 Nguyễn Đình Chiểu, Phường Bến Nghé, Quận 1, Hồ Chí Minh',
    10.7769, 106.7009, false, CURRENT_DATE, CURRENT_DATE)
AS new_data ON DUPLICATE KEY UPDATE recipient_name=new_data.recipient_name, phone_number=new_data.phone_number,
  province_code=new_data.province_code, province_name=new_data.province_name,
  district_code=new_data.district_code, district_name=new_data.district_name,
  ward_code=new_data.ward_code, ward_name=new_data.ward_name,
  street=new_data.street, full_address=new_data.full_address,
  latitude=new_data.latitude, longitude=new_data.longitude,
  is_default=new_data.is_default, updated_at=new_data.updated_at;

-- ================== WISHLISTS ==================
-- Entity: Wishlist (id, user_id, product_id, created_at, updated_at)
INSERT INTO wishlists (id, user_id, product_id, created_at, updated_at) VALUES
  (1, 1, 1, CURRENT_DATE, CURRENT_DATE),
  (2, 1, 3, CURRENT_DATE, CURRENT_DATE),
  (3, 1, 7, CURRENT_DATE, CURRENT_DATE)
AS new_data ON DUPLICATE KEY UPDATE updated_at=new_data.updated_at;

-- ================== CART ITEMS ==================
-- Entity: CartItem (id, user_id, variant_id, quantity, created_at, updated_at)
INSERT INTO cart_items (id, user_id, variant_id, quantity, created_at, updated_at) VALUES
  (1, 1, 4, 2, CURRENT_DATE, CURRENT_DATE),
  (2, 1, 9, 1, CURRENT_DATE, CURRENT_DATE)
AS new_data ON DUPLICATE KEY UPDATE quantity=new_data.quantity, updated_at=new_data.updated_at;

-- ================== ORDERS ==================
-- Entity: Order (id, user_id, total_amount, status, payment_method, payment_status,
--                shipping_fee, transaction_id, address_id, created_at, updated_at)
INSERT INTO orders (id, user_id, total_amount, status, shipping_fee, payment_status,
  payment_method, transaction_id, address_id, created_at, updated_at) VALUES
  -- Đơn 1: SUCCESS - COD - đã giao thành công
  (1, 1, 4530000, 'SUCCESS', 30000, 'PAID',
    'COD', NULL, 1,
    CURRENT_DATE - INTERVAL 14 DAY, CURRENT_DATE - INTERVAL 10 DAY),
  -- Đơn 2: SHIPPING - MOMO - đang giao
  (2, 1, 3125000, 'SHIPPING', 35000, 'PAID',
    'MOMO', 'MOMO_TXN_123456', 2,
    CURRENT_DATE - INTERVAL 3 DAY, CURRENT_DATE - INTERVAL 2 DAY),
  -- Đơn 3: CONFIRMED - VNPAY - đã xác nhận, chờ chuẩn bị
  (3, 1, 2535000, 'CONFIRMED', 35000, 'PAID',
    'VNPAY', 'VNPAY_TXN_789012', 1,
    CURRENT_DATE - INTERVAL 1 DAY, CURRENT_DATE - INTERVAL 1 DAY),
  -- Đơn 4: PENDING - COD - chờ xác nhận
  (4, 1, 1385000, 'PENDING', 35000, 'PENDING',
    'COD', NULL, 2,
    CURRENT_DATE, CURRENT_DATE),
  -- Đơn 5: CANCELLED - MOMO - đã hủy
  (5, 1, 4500000, 'CANCELLED', 30000, 'FAILED',
    'MOMO', 'MOMO_TXN_CANCEL_001', 1,
    CURRENT_DATE - INTERVAL 7 DAY, CURRENT_DATE - INTERVAL 6 DAY),
  -- Đơn 6: PREPARING - COD - đang chuẩn bị hàng
  (6, 1, 990000, 'PREPARING', 30000, 'PENDING',
    'COD', NULL, 1,
    CURRENT_DATE - INTERVAL 2 DAY, CURRENT_DATE - INTERVAL 1 DAY)
AS new_data ON DUPLICATE KEY UPDATE status=new_data.status, total_amount=new_data.total_amount,
  shipping_fee=new_data.shipping_fee, payment_status=new_data.payment_status,
  payment_method=new_data.payment_method, transaction_id=new_data.transaction_id,
  address_id=new_data.address_id, updated_at=new_data.updated_at;

-- ================== ORDER ITEMS ==================
-- Entity: OrderItem (id, order_id, variant_id, quantity, price, created_at, updated_at)
INSERT INTO order_items (id, order_id, variant_id, quantity, price, created_at, updated_at) VALUES
  -- Đơn 1 (SUCCESS): Keychron Q1 Pro Black
  (1, 1, 1, 1, 4500000, CURRENT_DATE - INTERVAL 14 DAY, CURRENT_DATE - INTERVAL 14 DAY),
  -- Đơn 2 (SHIPPING): Akko 3098B + Keycap Macaw
  (2, 2, 3, 1, 2100000, CURRENT_DATE - INTERVAL 3 DAY, CURRENT_DATE - INTERVAL 3 DAY),
  (3, 2, 8, 1, 990000, CURRENT_DATE - INTERVAL 3 DAY, CURRENT_DATE - INTERVAL 3 DAY),
  -- Đơn 3 (CONFIRMED): KBD67 Lite Kit
  (4, 3, 7, 1, 2500000, CURRENT_DATE - INTERVAL 1 DAY, CURRENT_DATE - INTERVAL 1 DAY),
  -- Đơn 4 (PENDING): Gateron Oil King 70pcs + Cherry MX Brown 10pcs
  (5, 4, 5, 1, 1050000, CURRENT_DATE, CURRENT_DATE),
  (6, 4, 6, 2, 120000, CURRENT_DATE, CURRENT_DATE),
  -- Đơn 5 (CANCELLED): Keychron Q1 Pro White
  (7, 5, 2, 1, 4500000, CURRENT_DATE - INTERVAL 7 DAY, CURRENT_DATE - INTERVAL 7 DAY),
  -- Đơn 6 (PREPARING): Keycap Macaw
  (8, 6, 8, 1, 990000, CURRENT_DATE - INTERVAL 2 DAY, CURRENT_DATE - INTERVAL 2 DAY)
AS new_data ON DUPLICATE KEY UPDATE quantity=new_data.quantity, price=new_data.price, updated_at=new_data.updated_at;

-- ================== ORDER STATUS HISTORY ==================
-- Entity: OrderStatusHistory (id, order_id, from_status, to_status, note, created_by, created_at)
INSERT INTO order_status_history (id, order_id, from_status, to_status, note, created_by, created_at) VALUES
  -- Đơn 1: PENDING -> CONFIRMED -> PREPARING -> SHIPPING -> SUCCESS
  (1, 1, NULL, 'PENDING', 'Đơn hàng được tạo', 1, CURRENT_DATE - INTERVAL 14 DAY),
  (2, 1, 'PENDING', 'CONFIRMED', 'Xác nhận đơn COD', 1, CURRENT_DATE - INTERVAL 13 DAY),
  (3, 1, 'CONFIRMED', 'PREPARING', 'Đang chuẩn bị hàng', NULL, CURRENT_DATE - INTERVAL 12 DAY),
  (4, 1, 'PREPARING', 'SHIPPING', 'Đã giao cho vận chuyển', NULL, CURRENT_DATE - INTERVAL 11 DAY),
  (5, 1, 'SHIPPING', 'SUCCESS', 'Giao hàng thành công', NULL, CURRENT_DATE - INTERVAL 10 DAY),
  -- Đơn 2: PENDING -> CONFIRMED -> PREPARING -> SHIPPING
  (6, 2, NULL, 'PENDING', 'Đơn hàng được tạo', 1, CURRENT_DATE - INTERVAL 3 DAY),
  (7, 2, 'PENDING', 'CONFIRMED', 'Thanh toán MoMo thành công', 1, CURRENT_DATE - INTERVAL 3 DAY),
  (8, 2, 'CONFIRMED', 'PREPARING', 'Đang chuẩn bị hàng', NULL, CURRENT_DATE - INTERVAL 2 DAY),
  (9, 2, 'PREPARING', 'SHIPPING', 'Đã giao cho vận chuyển', NULL, CURRENT_DATE - INTERVAL 2 DAY),
  -- Đơn 3: PENDING -> CONFIRMED
  (10, 3, NULL, 'PENDING', 'Đơn hàng được tạo', 1, CURRENT_DATE - INTERVAL 1 DAY),
  (11, 3, 'PENDING', 'CONFIRMED', 'Thanh toán VNPay thành công', 1, CURRENT_DATE - INTERVAL 1 DAY),
  -- Đơn 4: PENDING
  (12, 4, NULL, 'PENDING', 'Đơn hàng COD mới tạo', 1, CURRENT_DATE),
  -- Đơn 5: PENDING -> CANCELLED
  (13, 5, NULL, 'PENDING', 'Đơn hàng được tạo', 1, CURRENT_DATE - INTERVAL 7 DAY),
  (14, 5, 'PENDING', 'CANCELLED', 'Thanh toán MoMo thất bại, hủy đơn', NULL, CURRENT_DATE - INTERVAL 6 DAY),
  -- Đơn 6: PENDING -> CONFIRMED -> PREPARING
  (15, 6, NULL, 'PENDING', 'Đơn hàng được tạo', 1, CURRENT_DATE - INTERVAL 2 DAY),
  (16, 6, 'PENDING', 'CONFIRMED', 'Xác nhận đơn COD', 1, CURRENT_DATE - INTERVAL 2 DAY),
  (17, 6, 'CONFIRMED', 'PREPARING', 'Đang chuẩn bị hàng', NULL, CURRENT_DATE - INTERVAL 1 DAY)
AS new_data ON DUPLICATE KEY UPDATE from_status=new_data.from_status, to_status=new_data.to_status,
  note=new_data.note, created_by=new_data.created_by, created_at=new_data.created_at;

-- ================== REVIEWS ==================
-- Entity: Review (id, product_id, user_id, rating, content, created_at, updated_at)
-- Bảng phụ: review_images (review_id, image_url)
INSERT INTO reviews (id, product_id, user_id, rating, content, created_at, updated_at) VALUES
  (1, 1, 1, 5, 'Bàn phím rất chất lượng, đánh máy rất sướng tay. Gasket mount mềm mại, âm thanh thock!', CURRENT_DATE - INTERVAL 5 DAY, CURRENT_DATE - INTERVAL 5 DAY),
  (2, 3, 1, 4, 'Switch mượt, lube sẵn từ nhà máy rất tiện. Tuy nhiên spring hơi nặng so với mình.', CURRENT_DATE - INTERVAL 2 DAY, CURRENT_DATE - INTERVAL 2 DAY)
AS new_data ON DUPLICATE KEY UPDATE rating=new_data.rating, content=new_data.content, updated_at=new_data.updated_at;

INSERT INTO review_images (review_id, image_url) VALUES
  (1, 'https://cdn.example.com/reviews/q1-pro-review-1.jpg'),
  (1, 'https://cdn.example.com/reviews/q1-pro-review-2.jpg');
