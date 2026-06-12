# Backend Java Spring Boot - AI Agent Rules

Đây là checklist ngắn gọn (< 300 dòng) dành riêng cho AI Coding Agent. **BẮT BUỘC ĐỌC VÀ KIỂM TRA CHECKLIST NÀY TRƯỚC KHI BẮT ĐẦU CODE HOẶC COMMIT CODE.**

## BEFORE CODING

### [ ] Đúng Package & Naming
- Controller nằm trong `controller/`, Service nằm trong `service/`, JPA ở `repository/`, Entity ở `modal/`.
- Dùng tên danh từ số nhiều cho REST API (`/orders`, `/products`).
- Interface Service: `I<Name>Service`, Implementation: `<Name>Service`.

### [ ] Đúng Layer Responsibility
- **Controller**: Nhận request, lấy `userId` từ JWT, gọi Service, trả về `ResponseEntity<ApiResponse>`. Không được có `if/else` business logic ở đây.
- **Service**: Xử lý logic, tính toán, kiểm tra DB, gọi Repository.
- **Repository**: Chỉ thao tác với DB.

### [ ] Đúng DTO & Mapper
- Dùng DTO cho Request và Response. Nằm ở package `dto/request/...` và `dto/response/...`.
- Entity KHÔNG BAO GIỜ được lộ trực tiếp ra ngoài API.
- Dùng MapStruct (`@Mapper`) nằm ở `mapper/` để chuyển đổi Entity <-> DTO. Không tự viết code set chay nếu MapStruct làm được.

### [ ] Đúng Transaction
- Những hàm ở **Service** có làm thay đổi dữ liệu (Create, Update, Delete) PHẢI CÓ `@Transactional`.
- KHÔNG gắn `@Transactional` cho hàm Controller.

### [ ] Đúng Exception
- Ném ra các Exception cụ thể (`BadRequestException`, `ResourceNotFoundException`) nếu logic sai.
- Không dùng `try/catch` ở Controller để bắt lỗi hiển thị, hãy để `GlobalExceptionHandler` lo việc đó.

### [ ] Đúng Security
- Lấy `userId` từ `@AuthenticationPrincipal Jwt jwt` bằng lệnh `jwt.getClaim("userId")`. Không nhận `userId` từ body/path khi thực hiện hành vi của user.

---

## BEFORE COMMIT

### [ ] Không Duplicate Logic
- Các logic query chung phải đưa vào Repository.
- Các logic tính toán chung phải đưa vào private method trong Service hoặc Utility.

### [ ] Không Hard-code
- Không hard-code các string trạng thái, payment method (PHẢI dùng `enum` trong package `utils/`).
- Không hard-code role string.

### [ ] Không N+1 (Hiệu năng)
- Kiểm tra các query lấy danh sách trong Repository. Nếu có kéo theo bảng phụ (như ProductVariant kéo theo Attribute), PHẢI dùng `JOIN FETCH` trong `@Query`.
- Không gọi hàm repository.findById trong vòng lặp `for`. Dùng `findBy...In` thay thế.
- Lưu trữ hàng loạt (batch insert/update) phải dùng `saveAll()`.

### [ ] Có Validation
- Request DTO phải có các annotation `@NotBlank`, `@NotNull`, `@Min` của Jakarta Validation.
- Phải gắn `@Valid` ở tham số Controller.

### [ ] Không Eager Fetching
- Kiểm tra lại các quan hệ `@OneToMany`, `@ManyToOne` ở lớp Entity trong package `modal/`. Chắc chắn rằng chúng đang dùng `fetch = FetchType.LAZY`.

### [ ] Chuẩn Response Format
- Dữ liệu trả về luôn phải bọc trong `ApiResponse.builder().success(...).message(...).data(...).build()`. 

---
> Ghi nhớ: Dự án này Entity nằm ở thư mục `modal/`, không phải `entity/`. Đây là chuẩn hiện tại của dự án, hãy tuân thủ! Mọi code mới phải hòa quyện hoàn hảo với style hiện tại.
