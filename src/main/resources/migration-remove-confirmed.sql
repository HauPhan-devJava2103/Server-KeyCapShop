-- Cập nhật bảng orders: Chuyển các đơn hàng đang ở trạng thái CONFIRMED sang PENDING
UPDATE orders 
SET status = 'PENDING' 
WHERE status = 'CONFIRMED';

-- Cập nhật bảng order_status_history:
-- Trường hợp 1: Chuyển 'CONFIRMED' -> 'PREPARING' thành 'PENDING' -> 'PREPARING'
UPDATE order_status_history 
SET from_status = 'PENDING' 
WHERE from_status = 'CONFIRMED';

-- Trường hợp 2: Chuyển 'PENDING' -> 'CONFIRMED' thành 'PENDING' -> 'PREPARING'
UPDATE order_status_history 
SET to_status = 'PREPARING' 
WHERE to_status = 'CONFIRMED';

-- Đảm bảo không còn bản ghi nào chứa chuỗi 'CONFIRMED' trong DB
