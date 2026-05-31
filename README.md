# KeyCap Server - Hướng dẫn cài đặt

## 📋 Yêu cầu hệ thống

| Công cụ | Phiên bản tối thiểu |
|---------|---------------------|
| Java JDK | 21+ |
| Maven | 3.9+ |
| MySQL | 8.0+ |
| Docker Desktop | 4.x+ |
| Git | 2.x+ |

---

## 🚀 Hướng dẫn cài đặt từng bước

### Bước 1: Clone dự án

```bash
git clone <repository-url>
cd keycap_server
```

### Bước 2: Cài đặt Docker Desktop

> Bắt buộc cài Docker để chạy Redis.

1. Tải Docker Desktop tại: https://www.docker.com/products/docker-desktop/
2. Cài đặt và khởi động Docker Desktop
3. Đảm bảo Docker đang chạy (icon Docker trên thanh taskbar)

### Bước 3: Khởi động Redis bằng Docker

Mở terminal tại thư mục `keycap_server` và chạy:

```bash
docker-compose up -d
```

Lệnh này sẽ tự động:
- Tải image `redis:7` (lần đầu tiên)
- Tạo container tên `redis`
- Mở port `6379` trên máy local

#### ✅ Kiểm tra Redis đã chạy thành công

```bash
docker exec -it redis redis-cli ping
```

Nếu trả về `PONG` là Redis đã sẵn sàng.

#### 📌 Các lệnh Docker hữu ích

| Lệnh | Mô tả |
|-------|--------|
| `docker-compose up -d` | Khởi động Redis (chạy nền) |
| `docker-compose down` | Dừng và xóa container Redis |
| `docker-compose stop` | Dừng Redis (giữ lại container) |
| `docker-compose start` | Khởi động lại Redis đã dừng |
| `docker ps` | Xem các container đang chạy |
| `docker exec -it redis redis-cli` | Mở Redis CLI để debug |

#### 🔍 Các lệnh Redis CLI thường dùng (debug)

```bash
# Kết nối vào Redis CLI
docker exec -it redis redis-cli

# Xem tất cả keys
KEYS *

# Xem giá trị của 1 key
GET otp:example@gmail.com

# Xem thời gian sống còn lại (giây)
TTL otp:example@gmail.com

# Xóa 1 key
DEL otp:example@gmail.com

# Xóa tất cả dữ liệu
FLUSHALL

# Thoát Redis CLI
EXIT
```

### Bước 4: Cài đặt MySQL

1. Tạo database (Spring Boot sẽ tự tạo nếu chưa có):
   - Database name: `keycap_server`
   - Username: `root`
   - Password: xem trong file `.env` → `DB_PASSWORD`

### Bước 5: Cấu hình biến môi trường

Tạo file `.env` tại thư mục gốc `keycap_server/` với nội dung sau:

```env
JWT_SIGNER_KEY=<your-jwt-secret-key>
DB_PASSWORD=<your-mysql-password>

GOOGLE_CLIENT_ID=<your-google-client-id>
GOOGLE_CLIENT_SECRET=<your-google-client-secret>
GOOGLE_PROJECT_ID=<your-google-project-id>
GOOGLE_AUTH_URL=https://accounts.google.com/o/oauth2/auth
GOOGLE_TOKEN_URI=https://oauth2.googleapis.com/token
GOOGLE_AUTH_PROVIDER_CERT_URL=https://www.googleapis.com/oauth2/v1/certs
GOOGLE_REDIRECT_URI=http://localhost:5173
GOOGLE_JAVASCRIPT_ORIGINS=http://localhost:5173

MAIL_USERNAME=<your-gmail>
MAIL_PASSWORD=<your-gmail-app-password>
```

> ⚠️ **Lưu ý:** `MAIL_PASSWORD` là **App Password** của Gmail, không phải mật khẩu Gmail thông thường.
> Xem hướng dẫn tạo App Password: https://myaccount.google.com/apppasswords

### Bước 6: Chạy ứng dụng

```bash
./mvnw spring-boot:run
```

Hoặc chạy trực tiếp từ IDE (IntelliJ / VS Code) bằng cách Run class `KeycapServerApplication`.

Server sẽ chạy tại: `http://localhost:3000`

---

## 📁 Cấu trúc cấu hình

```
keycap_server/
├── docker-compose.yaml    # Cấu hình Docker cho Redis
├── .env                   # Biến môi trường (KHÔNG commit lên Git)
├── src/main/resources/
│   └── application.yaml   # Cấu hình Spring Boot
└── ...
```

### Cấu hình Redis trong `application.yaml`

```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
```

### Cấu hình Docker trong `docker-compose.yaml`

```yaml
services:
  redis:
    image: redis:7
    container_name: redis
    ports:
      - "6379:6379"
```

---

## ❓ Xử lý lỗi thường gặp

### 1. `Connection refused: localhost:6379`
→ Redis chưa chạy. Chạy lại:
```bash
docker-compose up -d
```

### 2. `docker: command not found`
→ Chưa cài Docker Desktop hoặc chưa khởi động Docker.

### 3. `Port 6379 already in use`
→ Có process khác đang dùng port 6379. Kiểm tra:
```bash
# Windows
netstat -ano | findstr :6379

# Linux/Mac
lsof -i :6379
```

### 4. `APPLICATION FAILED TO START` - Bean circular reference
→ Đảm bảo đã pull code mới nhất. Lỗi này đã được fix trong commit gần nhất.

---

## 🔑 Tài khoản mặc định

Khi chạy lần đầu, hệ thống tự tạo tài khoản Admin:

| Field | Value |
|-------|-------|
| Email | `admin@gmail.com` |
| Password | `admin` |
| Role | `ADMIN` |

> ⚠️ Vui lòng đổi mật khẩu admin sau khi chạy lần đầu.
