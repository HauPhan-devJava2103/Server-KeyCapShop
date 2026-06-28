# ⌨️ KeyCap E-Commerce Backend

> Hệ thống Backend REST API cho website thương mại điện tử chuyên bán keycap và bàn phím cơ. Hỗ trợ người dùng mua hàng trực tuyến và quản trị viên quản lý toàn bộ hệ thống.

---

## 📋 Giới Thiệu Dự Án

**KeyCap Server** là backend của hệ thống thương mại điện tử, được xây dựng trên nền tảng **Spring Boot 3.5** với kiến trúc RESTful API. Dự án phục vụ cho 3 nhóm người dùng chính:

- **Khách hàng (USER)**: Duyệt sản phẩm, mua hàng, thanh toán, đánh giá.
- **Nhân viên (STAFF)**: Hỗ trợ quản lý đơn hàng, sản phẩm, đánh giá.
- **Quản trị viên (ADMIN)**: Quản lý toàn bộ hệ thống, thống kê doanh thu.

### Các nghiệp vụ chính

- Xác thực & phân quyền (JWT, Google OAuth2)
- Quản lý sản phẩm, biến thể, danh mục, thương hiệu
- Giỏ hàng & quy trình đặt hàng
- Tích hợp đa cổng thanh toán (COD, MoMo, VNPay, PayPal)
- Tính phí vận chuyển qua GHN API
- Quản lý đánh giá & phản hồi
- Dashboard thống kê doanh thu
- Quản lý media qua Cloudinary
- Gửi OTP qua email (Gmail SMTP)
- Hàng đợi xử lý bất đồng bộ (RabbitMQ)

---

## 🛠️ Công Nghệ Sử Dụng

### Backend Framework

| Công nghệ               | Phiên bản | Mô tả                           |
| ----------------------- | --------- | ------------------------------- |
| Java                    | 21        | Ngôn ngữ lập trình chính        |
| Spring Boot             | 3.5.14    | Framework backend               |
| Spring Web              | -         | Xây dựng REST API               |
| Spring Security         | -         | Xác thực & phân quyền           |
| Spring Data JPA         | -         | Tầng truy cập dữ liệu           |
| Spring Data Redis       | -         | Cache & OTP storage             |
| Spring AMQP             | -         | Message queue (RabbitMQ)        |
| Spring Cloud OpenFeign  | 2025.0.0  | HTTP Client cho MoMo/PayPal API |
| Spring Mail + Thymeleaf | -         | Gửi email OTP                   |
| Hibernate               | -         | ORM mapping                     |

### Database & Cache

| Công nghệ | Mô tả                                 |
| --------- | ------------------------------------- |
| MySQL     | Cơ sở dữ liệu chính                   |
| Redis     | Cache dữ liệu, lưu OTP, rate limiting |
| RabbitMQ  | Message queue xử lý hết hạn đơn hàng  |

### Authentication & Security

| Công nghệ              | Mô tả                                    |
| ---------------------- | ---------------------------------------- |
| JWT (Nimbus JOSE)      | Token-based authentication (HMAC-SHA512) |
| OAuth2 Resource Server | Decode & validate JWT                    |
| Google API Client      | Đăng nhập qua Google OAuth2              |
| BCrypt                 | Mã hóa mật khẩu                          |

### Thanh Toán & Vận Chuyển

| Công nghệ      | Mô tả                  |
| -------------- | ---------------------- |
| MoMo Gateway   | Thanh toán qua ví MoMo |
| VNPay Sandbox  | Thanh toán qua VNPay   |
| PayPal Sandbox | Thanh toán qua PayPal  |
| GHN API        | Tính phí vận chuyển    |

### Tools & Libraries

| Công nghệ       | Mô tả                          |
| --------------- | ------------------------------ |
| Lombok          | Giảm boilerplate code          |
| MapStruct 1.6.3 | Object mapping (Entity ↔ DTO)  |
| Cloudinary      | Upload & quản lý hình ảnh      |
| Bean Validation | Validate request DTO           |
| Spring Dotenv   | Quản lý biến môi trường (.env) |
| Maven           | Build & dependency management  |

---

## 🏗️ Kiến Trúc Dự Án

Dự án được tổ chức theo kiến trúc **Layered Architecture**:

```
Client Request → Controller → Service → Repository → Database
                                ↕
                          DTO ↔ Mapper ↔ Entity
```

### Các tầng chính

| Tầng              | Vai trò                                                 |
| ----------------- | ------------------------------------------------------- |
| **Controller**    | Nhận request HTTP, validate input, trả response         |
| **Service**       | Xử lý business logic                                    |
| **Repository**    | Truy vấn database qua Spring Data JPA                   |
| **Entity/Modal**  | Ánh xạ bảng database (JPA Entity)                       |
| **DTO**           | Data Transfer Object — request/response riêng biệt      |
| **Mapper**        | Chuyển đổi Entity ↔ DTO (MapStruct)                     |
| **Configuration** | Cấu hình Security, Redis, RabbitMQ, Payment, Cloudinary |
| **Exception**     | Xử lý lỗi tập trung (GlobalExceptionHandler)            |

### Design Patterns áp dụng

| Pattern            | Áp dụng tại                          | Mô tả                                                          |
| ------------------ | ------------------------------------ | -------------------------------------------------------------- |
| **Factory Method** | Login (`AbstractLoginCreator`)       | Khởi tạo handler đăng nhập (Basic/Google) qua abstract creator |
| **Strategy**       | Payment (`IPaymentStrategy`)         | Chọn chiến lược thanh toán runtime (COD/MoMo/VNPay/PayPal)     |
| **Builder**        | Entity & DTO (`@Builder` Lombok)     | Khởi tạo object phức tạp (Order, LoginResponse...)             |
| **Repository**     | Spring Data JPA                      | Truy cập dữ liệu qua interface                                 |
| **DTO**            | Request/Response DTO                 | Tách biệt dữ liệu giữa client và domain                        |
| **Observer**       | Spring Event (`OrderCompletedEvent`) | Xử lý sự kiện hoàn thành đơn hàng                              |

---

## 📁 Cấu Trúc Thư Mục

```text
src/main/java/com/vn/keycap_server
├── client/                         # OpenFeign HTTP Client (MoMo, PayPal, GHN)
├── configuration/                  # Cấu hình ứng dụng
│   ├── SecurityConfig.java         #   Spring Security & JWT
│   ├── CustomJwtDecoder.java       #   JWT decoder tùy chỉnh
│   ├── ApplicationInitConfig.java  #   Khởi tạo tài khoản admin
│   ├── cloudinary/                 #   Cấu hình Cloudinary
│   ├── redis/                      #   Cấu hình Redis
│   ├── rabbitmq/                   #   Cấu hình RabbitMQ
│   ├── momo/                       #   Cấu hình MoMo
│   ├── vnpay/                      #   Cấu hình VNPay
│   ├── paypal/                     #   Cấu hình PayPal
│   └── ghn/                        #   Cấu hình GHN Shipping
├── controller/                     # REST API endpoints
│   ├── admin/                      #   API quản trị (Admin/Staff)
│   ├── AuthController.java         #   Đăng nhập, đăng ký, OTP
│   ├── ProductController.java      #   Sản phẩm
│   ├── OrderController.java        #   Đơn hàng
│   ├── CartController.java         #   Giỏ hàng
│   ├── PaymentController.java      #   Callback thanh toán
│   ├── ReviewController.java       #   Đánh giá
│   └── ...                         #   Address, Brand, Category, etc.
├── dto/                            # Data Transfer Objects
│   ├── request/                    #   Request DTO
│   └── response/                   #   Response DTO
├── exception/                      # Xử lý lỗi tập trung
├── mapper/                         # MapStruct mappers
├── modal/                          # JPA Entities (20 entities)
├── repository/                     # Spring Data JPA repositories
├── scheduler/                      # Scheduled tasks
├── service/                        # Business logic
│   ├── auth/                       #   Xác thực (Factory Method Pattern)
│   │   └── login/                  #     Login handlers & creators
│   ├── payment/                    #   Thanh toán (Strategy Pattern)
│   │   ├── impl/                   #     COD, MoMo, VNPay, PayPal strategies
│   │   ├── momo/                   #     MoMo service
│   │   ├── vnpay/                  #     VNPay service
│   │   └── paypal/                 #     PayPal service
│   ├── order/                      #   Đơn hàng & message queue
│   ├── product/                    #   Sản phẩm
│   ├── cart/                       #   Giỏ hàng
│   ├── review/                     #   Đánh giá
│   ├── dashboard/                  #   Thống kê
│   ├── shipping/                   #   Vận chuyển (GHN)
│   ├── redis/                      #   Redis service
│   ├── mail/                       #   Email service
│   └── ...                         #   Address, Brand, Category, etc.
├── utils/                          # Enums, Helpers, Encoders
└── validation/                     # Custom validators
```

---

## 🚀 Các Chức Năng Chính

### 👤 Chức năng Khách hàng (USER)

| Nhóm           | Chức năng                                                                                                                 |
| -------------- | ------------------------------------------------------------------------------------------------------------------------- |
| **Xác thực**   | Đăng ký tài khoản (OTP email), Đăng nhập (email/password), Đăng nhập Google OAuth2, Đăng xuất, Quên mật khẩu (OTP)        |
| **Sản phẩm**   | Xem danh sách sản phẩm, Xem chi tiết, Tìm kiếm & lọc (giá, danh mục, thương hiệu, sắp xếp), Sản phẩm yêu thích (Wishlist) |
| **Giỏ hàng**   | Thêm sản phẩm, Cập nhật số lượng, Xóa sản phẩm, Xem tổng giỏ hàng                                                         |
| **Đơn hàng**   | Chuẩn bị checkout (tính phí ship), Đặt hàng, Xem lịch sử, Xem chi tiết, Hủy đơn hàng                                      |
| **Thanh toán** | COD (thanh toán khi nhận hàng), MoMo, VNPay, PayPal                                                                       |
| **Đánh giá**   | Đánh giá sản phẩm (sau khi mua), Xem đánh giá                                                                             |
| **Tài khoản**  | Quản lý thông tin cá nhân, Quản lý địa chỉ giao hàng (CRUD), Đổi mật khẩu                                                 |

### 🛡️ Chức năng Quản trị viên (ADMIN)

| Nhóm                  | Chức năng                                                                                                 |
| --------------------- | --------------------------------------------------------------------------------------------------------- |
| **Quản lý sản phẩm**  | CRUD sản phẩm, Quản lý biến thể (variant), Quản lý hình ảnh, Quản lý danh mục, thương hiệu, loại sản phẩm |
| **Quản lý đơn hàng**  | Xem danh sách đơn hàng, Tìm kiếm/lọc, Cập nhật trạng thái, Hủy đơn hàng                                   |
| **Quản lý nhân viên** | Tạo tài khoản staff, Xem danh sách, Cập nhật thông tin                                                    |
| **Quản lý đánh giá**  | Xem/Duyệt đánh giá, Phản hồi đánh giá                                                                     |
| **Dashboard**         | Thống kê doanh thu, Thống kê đơn hàng theo trạng thái, Biểu đồ doanh thu theo tháng                       |

### 👨‍💼 Chức năng Nhân viên (STAFF)

Nhân viên có quyền tương tự Admin trong việc quản lý đơn hàng, sản phẩm và đánh giá, được phân quyền qua `@PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STAFF')")`.

---

## 🔐 Phân Quyền Người Dùng

| Vai trò | Mô tả         | Chức năng chính                                |
| ------- | ------------- | ---------------------------------------------- |
| `USER`  | Khách hàng    | Mua hàng, quản lý giỏ hàng, đặt hàng, đánh giá |
| `STAFF` | Nhân viên     | Quản lý đơn hàng, sản phẩm, phản hồi đánh giá  |
| `ADMIN` | Quản trị viên | Toàn quyền: quản lý staff, dashboard thống kê  |

---

## 📡 API Chính

| Nhóm API              | Base Path                                                            | Mô tả                                     |
| --------------------- | -------------------------------------------------------------------- | ----------------------------------------- |
| Auth API              | `/login`, `/register`, `/logout`, `/otps/request`, `/reset-password` | Xác thực & đăng ký                        |
| User API              | `/users`                                                             | Quản lý thông tin cá nhân                 |
| Product API           | `/products`                                                          | Duyệt, tìm kiếm, lọc sản phẩm             |
| Category API          | `/categories`                                                        | Danh sách danh mục                        |
| Brand API             | `/brands`                                                            | Danh sách thương hiệu                     |
| Type API              | `/types`                                                             | Danh sách loại sản phẩm                   |
| Cart API              | `/carts`                                                             | Quản lý giỏ hàng                          |
| Order API             | `/orders`                                                            | Đặt hàng, xem lịch sử, hủy đơn            |
| Address API           | `/addresses`                                                         | CRUD địa chỉ giao hàng                    |
| Payment API           | `/payment`                                                           | Callback thanh toán (MoMo, VNPay, PayPal) |
| Review API            | `/reviews`                                                           | Đánh giá sản phẩm                         |
| Favorite API          | `/favorites`                                                         | Sản phẩm yêu thích                        |
| Media API             | `/medias`                                                            | Upload ảnh (Cloudinary)                   |
| **Admin** Product API | `/admin/products`                                                    | CRUD sản phẩm (Admin/Staff)               |
| **Admin** Order API   | `/admin/orders`                                                      | Quản lý đơn hàng (Admin/Staff)            |
| **Admin** Staff API   | `/admin/staff`                                                       | Quản lý nhân viên (Admin only)            |
| **Admin** Review API  | `/admin/reviews`                                                     | Duyệt & phản hồi đánh giá                 |
| Dashboard API         | `/admin/dashboard`                                                   | Thống kê doanh thu, đơn hàng              |

---

## ⚙️ Hướng Dẫn Cài Đặt

### Yêu cầu môi trường

- **JDK 21** trở lên
- **Maven 3.8+**
- **MySQL 8.0+**
- **Redis 7.0+**
- **RabbitMQ 3.12+**
- IDE: IntelliJ IDEA hoặc VS Code
- Postman (để test API)

### 1. Clone project

```bash
git clone <repository-url>
cd keycap_server
```

### 2. Cấu hình Database

```sql
CREATE DATABASE keycap_server;
```

> Ứng dụng tự tạo database nếu chưa tồn tại nhờ `createDatabaseIfNotExist=true`.

### 3. Cấu hình biến môi trường

Tạo file `.env` tại thư mục gốc dự án:

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

### 4. Cài đặt Redis & RabbitMQ

```bash
# Redis (Docker)
docker run -d --name redis -p 6379:6379 redis

# RabbitMQ (Docker)
docker run -d --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:management
```

### 5. Chạy project

```bash
# Build project
mvn clean install -DskipTests

# Chạy ứng dụng
mvn spring-boot:run
```

Hoặc:

```bash
java -jar target/keycap_server-0.0.1-SNAPSHOT.jar
```

Server khởi chạy tại: `http://localhost:3000`

### 6. Import dữ liệu mẫu (tùy chọn)

```bash
mysql -u root -p keycap_server < src/main/resources/seed-data.sql
```

---

## 🗄️ Dữ Liệu Mẫu & Tài Khoản Test

Khi khởi động lần đầu, hệ thống tự động tạo tài khoản Admin thông qua `ApplicationInitConfig`:

| Role  | Email           | Password |
| ----- | --------------- | -------- |
| ADMIN | admin@gmail.com | admin    |

> ⚠️ Vui lòng đổi mật khẩu admin sau khi đăng nhập lần đầu.

File `seed-data.sql` chứa dữ liệu mẫu bao gồm: danh mục, thương hiệu, sản phẩm, biến thể, hình ảnh và tài khoản test.

---

## 🔒 Lưu Ý Bảo Mật

> [!CAUTION]
> **Không push thông tin nhạy cảm lên GitHub:**
>
> - ❌ Mật khẩu database
> - ❌ JWT Secret Key
> - ❌ API Key thanh toán (MoMo, VNPay, PayPal)
> - ❌ Google Client Secret
> - ❌ Mail App Password
> - ❌ Cloudinary API Secret

Sử dụng file `.env` và thêm vào `.gitignore`:

```gitignore
.env
```

---

## 📝 Ghi Chú Phát Triển

- Đây là dự án học tập và thực hành xây dựng hệ thống backend thương mại điện tử.
- Các cổng thanh toán (MoMo, VNPay, PayPal) đang sử dụng **môi trường Sandbox/Test**, không phải production.
- Hệ thống sử dụng `ddl-auto: update` — Hibernate tự tạo/cập nhật bảng, phù hợp cho môi trường development.
- Đơn hàng thanh toán online có cơ chế **tự động hết hạn sau 15 phút** nếu chưa thanh toán (qua RabbitMQ delayed message).
- Một số chức năng có thể tiếp tục mở rộng: voucher/mã giảm giá, chat hỗ trợ, thông báo realtime.

---

## 📊 Sơ Đồ Database (Entities)

Hệ thống gồm **20 entities** chính:

```
User ─── UserToken, Address, CartItem, Order, Review, Wishlist
Product ─── ProductVariant, ProductImage, ProductSpecification
ProductVariant ─── ProductVariantAttribute, OrderItem
Order ─── OrderItem, OrderStatusHistory
Category, Brand, ProductType
Media, ReviewReply
```

---

> 🎓 **Dự án được phát triển phục vụ mục đích học tập môn Design Patterns.**
