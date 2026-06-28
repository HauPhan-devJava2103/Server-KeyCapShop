<p align="center">
  <img src="https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=openjdk" />
  <img src="https://img.shields.io/badge/Spring_Boot-3.5-brightgreen?style=for-the-badge&logo=springboot" />
  <img src="https://img.shields.io/badge/MySQL-8.0-blue?style=for-the-badge&logo=mysql&logoColor=white" />
  <img src="https://img.shields.io/badge/Redis-Cache-red?style=for-the-badge&logo=redis&logoColor=white" />
  <img src="https://img.shields.io/badge/JWT-Security-black?style=for-the-badge&logo=jsonwebtokens" />
  <img src="https://img.shields.io/badge/RabbitMQ-Queue-FF6600?style=for-the-badge&logo=rabbitmq&logoColor=white" />
  <img src="https://img.shields.io/badge/Maven-Build-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white" />
  <img src="https://img.shields.io/badge/Cloudinary-Upload-3448C5?style=for-the-badge&logo=cloudinary&logoColor=white" />

</p>

<h1 align="center">KeyCap E-Commerce Backend ⌨️</h1>

<p align="center">
  Dự án cung cấp hệ thống REST API cho website thương mại điện tử bán Keycap / Bàn phím cơ, hỗ trợ người dùng mua hàng và quản trị viên quản lý hệ thống.
</p>

---

## 📋 Nội dung

1. [Giới thiệu](#-giới-thiệu)
2. [Chức năng](#-chức-năng)
3. [Công nghệ sử dụng](#️-công-nghệ-sử-dụng)
4. [Cấu trúc thư mục](#-cấu-trúc-thư-mục)
5. [Yêu cầu môi trường](#️-yêu-cầu-môi-trường)
6. [Cài đặt](#-cài-đặt)
7. [Cấu hình môi trường](#-cấu-hình-môi-trường)
8. [Chạy dự án](#️-chạy-dự-án)
9. [Liên kết với Frontend](#-liên-kết-với-frontend)
10. [Thành viên](#-thành-viên)

---

## 🚀 Giới thiệu

**KeyCap Server** là backend REST API cho hệ thống thương mại điện tử chuyên bán keycap và bàn phím cơ, được xây dựng trên nền tảng **Spring Boot 3.5** với **Java 21**.

- Backend phục vụ cho giao diện **Client** (người dùng mua hàng) và trang **Admin** (quản trị hệ thống).
- Hỗ trợ quản lý người dùng, sản phẩm, danh mục, giỏ hàng, đơn hàng, thanh toán đa cổng, đánh giá sản phẩm và dashboard thống kê.
- Tích hợp các dịch vụ bên thứ ba: **MoMo**, **VNPay**, **PayPal** (thanh toán), **GHN** (vận chuyển), **Cloudinary** (upload ảnh), **Gmail SMTP** (gửi OTP).
- Dự án được xây dựng nhằm mô phỏng một hệ thống backend thương mại điện tử thực tế, phục vụ mục đích học tập môn **Design Patterns**.

---

## ✨ Chức năng

### 👤 Người dùng (USER)

- Đăng ký tài khoản (xác thực OTP qua email)
- Đăng nhập bằng email/mật khẩu hoặc Google OAuth2
- Xác thực phiên đăng nhập bằng JWT
- Xem danh sách sản phẩm, xem chi tiết sản phẩm
- Tìm kiếm và lọc sản phẩm (theo giá, danh mục, thương hiệu, sắp xếp)
- Thêm sản phẩm vào danh sách yêu thích (Wishlist)
- Quản lý địa chỉ giao hàng (CRUD)
- Thêm, cập nhật số lượng, xóa sản phẩm trong giỏ hàng
- Đặt hàng với tính phí vận chuyển tự động (GHN)
- Thanh toán qua COD, MoMo, VNPay, PayPal
- Xem lịch sử đơn hàng, chi tiết đơn hàng, hủy đơn hàng
- Đánh giá sản phẩm sau khi mua
- Quản lý thông tin cá nhân, đổi mật khẩu, quên mật khẩu (OTP)

### 🛡️ Quản trị viên (ADMIN)

- Quản lý sản phẩm (CRUD), biến thể sản phẩm, hình ảnh sản phẩm
- Quản lý danh mục, thương hiệu, loại sản phẩm
- Quản lý đơn hàng: xem danh sách, tìm kiếm/lọc, cập nhật trạng thái, hủy đơn
- Quản lý nhân viên: tạo tài khoản Staff, xem danh sách, cập nhật thông tin
- Quản lý đánh giá: duyệt đánh giá, phản hồi đánh giá
- Dashboard thống kê: doanh thu, đơn hàng theo trạng thái, biểu đồ doanh thu theo tháng

### 👨‍💼 Nhân viên (STAFF)

- Quản lý đơn hàng: xem, xử lý, cập nhật trạng thái
- Hỗ trợ quản lý sản phẩm
- Phản hồi đánh giá khách hàng

> Nhân viên được phân quyền qua `@PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STAFF')")`.

---

## 🛠️ Công nghệ sử dụng

| Nhóm            | Công nghệ                                                 |
| --------------- | --------------------------------------------------------- |
| Ngôn ngữ        | Java 21                                                   |
| Framework       | Spring Boot 3.5.14                                        |
| API             | Spring Web, RESTful API                                   |
| Bảo mật         | Spring Security, JWT (Nimbus JOSE), Google OAuth2, BCrypt |
| Database        | MySQL                                                     |
| ORM             | Spring Data JPA, Hibernate                                |
| Cache           | Redis (lưu OTP, cache dữ liệu)                            |
| Message Queue   | RabbitMQ (xử lý hết hạn đơn hàng)                         |
| HTTP Client     | Spring Cloud OpenFeign 2025.0.0                           |
| Mapping         | MapStruct 1.6.3                                           |
| Build Tool      | Maven                                                     |
| Thư viện hỗ trợ | Lombok, Bean Validation, Spring Dotenv, Thymeleaf         |
| Thanh toán      | MoMo, VNPay, PayPal (Sandbox)                             |
| Vận chuyển      | GHN API                                                   |
| Upload ảnh      | Cloudinary                                                |
| Email           | Spring Mail (Gmail SMTP)                                  |

---

## 📁 Cấu trúc thư mục

```text
src/main/java/com/vn/keycap_server
├── client/
├── configuration/
│   ├── cloudinary/
│   ├── ghn/
│   ├── momo/
│   ├── paypal/
│   ├── rabbitmq/
│   ├── redis/
│   └── vnpay/
├── controller/
│   └── admin/
├── dto/
│   ├── request/
│   └── response/
├── exception/
├── mapper/
├── modal/
├── repository/
├── scheduler/
├── service/
│   ├── auth/
│   │   └── login/
│   ├── payment/
│   │   ├── impl/
│   │   ├── momo/
│   │   ├── paypal/
│   │   └── vnpay/
│   ├── order/
│   ├── product/
│   ├── cart/
│   ├── review/
│   ├── dashboard/
│   ├── shipping/
│   ├── redis/
│   ├── mail/
│   └── ...
├── utils/
└── validation/
```

| Thư mục         | Vai trò                                                                |
| --------------- | ---------------------------------------------------------------------- |
| `client`        | OpenFeign HTTP Client gọi API bên thứ ba (MoMo, PayPal, GHN)           |
| `configuration` | Cấu hình hệ thống: Security, Redis, RabbitMQ, Payment, Cloudinary, GHN |
| `controller`    | Tiếp nhận HTTP request từ client và trả response                       |
| `dto`           | Chứa Request DTO và Response DTO                                       |
| `exception`     | Xử lý lỗi tập trung (GlobalExceptionHandler)                           |
| `mapper`        | Chuyển đổi giữa Entity và DTO (MapStruct)                              |
| `modal`         | Chứa các JPA Entity ánh xạ với bảng database (20 entities)             |
| `repository`    | Làm việc với database thông qua Spring Data JPA                        |
| `scheduler`     | Các tác vụ chạy theo lịch (Scheduled tasks)                            |
| `service`       | Xử lý logic nghiệp vụ chính                                            |
| `utils`         | Chứa Enum, Helper, Encoder (JWT, VNPay, MoMo)                          |
| `validation`    | Custom validators cho request                                          |

---

## ⚙️ Yêu cầu môi trường

Trước khi chạy dự án, cần cài đặt:

- **JDK 21** trở lên
- **Maven 3.8+**
- **MySQL 8.0+**
- **Redis**
- **RabbitMQ**
- IntelliJ IDEA hoặc VS Code
- Postman để kiểm thử API

---

## 📦 Cài đặt

### 1. Clone project

```bash
git clone <repository-url>
cd keycap_server
```

### 2. Build project

```bash
mvn clean install -DskipTests
```

### 3. Tạo database

```sql
CREATE DATABASE keycap_server;
```

> Ứng dụng tự tạo database nếu chưa tồn tại nhờ cấu hình `createDatabaseIfNotExist=true`.

### 4. Import dữ liệu mẫu (tùy chọn)

```bash
mysql -u root -p keycap_server < src/main/resources/seed-data.sql
```

File `seed-data.sql` chứa dữ liệu mẫu bao gồm: danh mục, thương hiệu, sản phẩm, biến thể và hình ảnh.

### 5. Tài khoản mặc định

Khi khởi động lần đầu, hệ thống tự động tạo tài khoản Admin:

| Role  | Email           | Password |
| ----- | --------------- | -------- |
| ADMIN | admin@gmail.com | admin    |

---

## 🔧 Cấu hình môi trường

Dự án sử dụng file `.env` tại thư mục gốc để quản lý biến môi trường (thông qua thư viện `spring-dotenv`).

File cấu hình chính: `src/main/resources/application.yaml`

Tạo file `.env` với nội dung sau:

```properties
# Database
DB_PASSWORD=your_mysql_password

# JWT
JWT_SIGNER_KEY=your_secret_key_base64

# Google OAuth2
GOOGLE_CLIENT_ID=your_google_client_id
GOOGLE_CLIENT_SECRET=your_google_client_secret
GOOGLE_PROJECT_ID=your_project_id
GOOGLE_AUTH_URL=https://accounts.google.com/o/oauth2/auth
GOOGLE_TOKEN_URI=https://oauth2.googleapis.com/token
GOOGLE_AUTH_PROVIDER_CERT_URL=https://www.googleapis.com/oauth2/v1/certs
GOOGLE_REDIRECT_URI=http://localhost:5173
GOOGLE_JAVASCRIPT_ORIGINS=http://localhost:5173

# Mail (Gmail SMTP)
MAIL_USERNAME=your_email@gmail.com
MAIL_PASSWORD=your_app_password

# MoMo Payment
MOMO_PARTNER_CODE=your_partner_code
MOMO_ACCESS_KEY=your_access_key
MOMO_SECRET_KEY=your_secret_key
MOMO_API_URL=https://test-payment.momo.vn/v2/gateway/api/create
MOMO_REDIRECT_URL=http://localhost:5173/order/checkout/result
MOMO_IPN_URL=https://your-domain/api/payment/momo/ipn

# VNPay Payment
VNPAY_TMN_CODE=your_tmn_code
VNPAY_HASH_SECRET=your_hash_secret
VNPAY_API_URL=https://sandbox.vnpayment.vn/paymentv2/vpcpay.html
VNPAY_REDIRECT_URL=http://localhost:5173/order/checkout/result

# PayPal Payment
PAYPAL_CLIENT_ID=your_paypal_client_id
PAYPAL_CLIENT_SECRET=your_paypal_client_secret
PAYPAL_API_URL=https://api-m.sandbox.paypal.com
PAYPAL_REDIRECT_URL=http://localhost:5173/order/checkout/result

# GHN Shipping
GHN_TOKEN=your_ghn_token
GHN_SHOP_ID=your_shop_id
GHN_API_URL=https://dev-online-gateway.ghn.vn/shiip/public-api/v2/shipping-order/fee
GHN_FROM_DISTRICT_ID=your_district_id
GHN_FROM_WARD_CODE=your_ward_code

# Cloudinary
CLOUDINARY_CLOUD_NAME=your_cloud_name
CLOUDINARY_API_KEY=your_api_key
CLOUDINARY_API_SECRET=your_api_secret
```

Redis và RabbitMQ mặc định chạy trên localhost:

```yaml
# Redis (application.yaml)
spring.data.redis.host: localhost
spring.data.redis.port: 6379

# RabbitMQ (application.yaml)
spring.rabbitmq.host: localhost
spring.rabbitmq.port: 5672
```

> ⚠️ **Không commit mật khẩu database, JWT secret hoặc API key thật lên GitHub.** Thêm `.env` vào `.gitignore`.

---

## ▶️ Chạy dự án

### Chạy bằng Maven

```bash
mvn spring-boot:run
```

### Hoặc chạy file JAR

```bash
java -jar target/keycap_server-0.0.1-SNAPSHOT.jar
```

Sau khi chạy thành công, backend khởi chạy tại:

```
http://localhost:3000
```

---

## 🔗 Liên kết với Frontend

Backend cung cấp REST API cho frontend Client và Admin. Frontend cần cấu hình base URL trỏ về backend:

```env
VITE_API_BASE_URL=http://localhost:3000
```

Repository Frontend: [Web_ShopKeyCap](https://github.com/chuonghoai/Web_ShopKeyCap)

---

## 👥 Thành viên

| STT | Họ tên             | Vai trò            |
| --- | ------------------ | ------------------ |
| 1   | Phan Phúc Hậu      | Backend Developer  |
| 2   | Trương Hoài Chương | Frontend Developer |
| 3   | Lê Hữu Văn         | Backend Developer  |
| 4   | Phạm Thị Kim Ngân  | Tester / Document  |

---

> 🎓 **Dự án được phát triển phục vụ mục đích học tập môn Design Patterns.**
