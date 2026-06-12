# Backend Java Spring Boot - AI Coding Skill

Tài liệu này định nghĩa các bộ quy tắc (rules) bắt buộc mà mọi AI Coding Agent phải tuân thủ khi viết code cho dự án Backend `Server-KeyCapShop`. Mọi rule đều được rút ra từ kiến trúc và practice hiện tại của dự án.

---

## 1. Project Architecture Rules

### Architecture Overview
Dự án áp dụng **Layered Architecture** kết hợp Feature-based. Flow chuẩn của một request sẽ đi theo thứ tự sau:
`Client` ➔ `Controller` ➔ `Service` ➔ `Repository` ➔ `Database`.

### Dependency Rules
- **Controller Layer** CHỈ được phép gọi **Service Layer**. 
- **Service Layer** được phép gọi **Repository Layer** và các **Service khác** (nhưng cần cẩn thận circular dependency).
- **Repository Layer** CHỈ giao tiếp với Database. Tuyệt đối không gọi ngược lên Service hay Controller.
- **Entity** là POJO thuần túy, không được phép inject hoặc phụ thuộc vào bất kỳ Spring Bean nào.

---

## 2. Folder Rules

Cấu trúc package nằm trong `src/main/java/com/vn/keycap_server/`.

- **`controller/`**
  - **Được chứa:** Các class `@RestController`.
  - **Không chứa:** Business logic, truy vấn DB.
  - **Ví dụ đúng:** `OrderController.java` chỉ nhận request, gọi `orderService.checkout()`, và wrap lại thành `ApiResponse`.

- **`service/`**
  - **Được chứa:** Các Interface (`IService`) và Implementation (`ServiceImpl` hoặc class gắn `@Service`). Có thể tổ chức theo folder tính năng (ví dụ `service/order/OrderService.java`).
  - **Không chứa:** DTO mapping thủ công (phải dùng Mapper), các query SQL trực tiếp.
  - **Ví dụ đúng:** Chứa business logic phức tạp như tính toán tổng tiền, kiểm tra tồn kho.

- **`repository/`**
  - **Được chứa:** Các interface kế thừa `JpaRepository`.
  - **Không chứa:** Business logic.

- **`modal/` (Chú ý: Đây là Entity package)**
  - **Được chứa:** Các class `@Entity` map với database. Kế thừa từ `AbstractEntity.java`.
  - **Không chứa:** DTO, business logic.

- **`dto/`**
  - **Được chứa:** Các package `request` và `response`.
  - **Không chứa:** Logic, Entity. DTO chỉ dùng để transfer data.

- **`mapper/`**
  - **Được chứa:** Các interface sử dụng `@Mapper(componentModel = "spring")` của MapStruct.
  - **Không chứa:** Logic phức tạp trong mapper.

- **`exception/`**
  - **Được chứa:** `@RestControllerAdvice` (`GlobalExceptionHandler`) và các custom exception (`BadRequestException`, `ResourceNotFoundException`).

---

## 3. Coding Rules

- **Naming Convention:**
  - Controller: `<Feature>Controller`
  - Service: `I<Feature>Service` (Interface) và `<Feature>Service` hoặc `<Feature>ServiceImpl` (Class).
  - Repository: `<Entity>Repository`
  - Entity: Danh từ số ít (VD: `Order`, không phải `Orders`).
- **Constructor Injection:** Bắt buộc sử dụng `@RequiredArgsConstructor` từ Lombok kết hợp `private final` thay vì `@Autowired` field injection.
- **Lombok Usage:** Khuyến khích dùng `@Getter`, `@Setter`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor` trên Entity và DTO.
- **Logging Strategy:** Sử dụng `@Slf4j` từ Lombok nếu cần log. Tránh `System.out.println`.

---

## 4. Business Logic Rules

Mọi logic nghiệp vụ PHẢI nằm ở Service layer.
- **Validation logic cơ bản (Format, Null, NotBlank):** Nằm ở DTO thông qua `@Valid` và Jakarta Validation annotations.
- **Permission logic:** Nằm ở Controller thông qua `@PreAuthorize` hoặc JWT Claim (`@AuthenticationPrincipal Jwt jwt`).
- **Logic kiểm tra dữ liệu DB (Ví dụ: tồn kho, voucher, tính giá):** Nằm ở Service (VD: Kiểm tra `stockQuantity` trong `OrderService`).
- **Mapping Entity <-> DTO:** Thực hiện ở Controller hoặc Service thông qua class Mapper (VD: `AddressMapper.toAddressResponseList`).

---

## 5. Database Rules

- **Entity Design:**
  - Kế thừa `AbstractEntity` nếu có các trường chung (created_at, updated_at).
  - Khai báo mapping `@Table(name = "table_name")` rõ ràng.
- **Repository Usage:** Ưu tiên sử dụng Query method của Spring Data JPA.
- **Lazy/Eager Loading:** Các quan hệ `@ManyToOne` và `@OneToMany` phải mặc định sử dụng `fetch = FetchType.LAZY` (VD: `@ManyToOne(fetch = FetchType.LAZY)` trong `Order.java`).
- **Transaction Usage:** Gắn `@Transactional` ở method Service nếu có sự thay đổi dữ liệu (CUD - Create, Update, Delete). Không gắn `@Transactional` cho Controller.

---

## 6. API Rules

- **REST Endpoint Naming:** Sử dụng danh từ số nhiều, chữ thường, cách nhau bằng gạch ngang (kebab-case). VD: `/orders`, `/products`, `/products/hot-brand`.
- **Response Format:** Mọi API bắt buộc trả về qua `ResponseEntity<ApiResponse>`.
  - Thành công: `ApiResponse.builder().success(true).message("...").data(...).build()`
  - Phân trang: Bổ sung thêm `.pagination(meta)` vào `ApiResponse` (sử dụng `PaginationUtils.buildPaginationMeta`).
- **Error Format:** Do `GlobalExceptionHandler` tự động xử lý và trả về `ApiResponse` có `success = false`.

---

## 7. Security Rules

- **Authentication:** Ứng dụng dùng OAuth2 Resource Server (JWT Bearer Token). `userId` lấy từ JWT:
  ```java
  Long userId = jwt.getClaim("userId");
  ```
- **Authorization:** Tránh hardcode role.

---

## 8. Performance Rules

- **Tránh N+1 Query:** Khi lấy ra danh sách Entity có chứa Lazy Loading data, PHẢI dùng `@Query` với `JOIN FETCH` (VD: `ProductVariantRepository.findAllByWithProductAndAttributes`).
- **Batch Update/Insert:** Khi cần lưu nhiều items (như `orderItems`), sử dụng `repository.saveAll(list)` thay vì loop và gọi `.save()` nhiều lần.
- **Native Query:** Hạn chế dùng Native Query (`@Query(nativeQuery=true)`). Chỉ dùng cho các logic đặc thù không thể viết bằng JPQL (VD: UPSERT trong `CartItemRepository`).

---

## 9. Testing Rules

- Hiện tại hệ thống chưa yêu cầu chặt chẽ, nhưng nếu sinh code Test, phải mock các Service layer khi test Controller, và mock Repository khi test Service.

---

## 10. Forbidden Rules (Tuyệt Đối Cấm)

- ❌ **CẤM** đặt Business logic (tính toán, kiểm tra tồn kho, v.v.) bên trong Controller.
- ❌ **CẤM** Repository gọi ngược lên Service hoặc Controller.
- ❌ **CẤM** Mapper chứa business logic (chỉ dùng để map field to field).
- ❌ **CẤM** Entity chứa dependency injection (Spring Bean, Service, Repository).
- ❌ **CẤM** viết Native Query nếu tác vụ có thể xử lý dễ dàng bằng JPQL hoặc Spring Data Method.
- ❌ **CẤM** sử dụng Eager Loading (`FetchType.EAGER`) cho mọi quan hệ Entity trừ phi thực sự cần thiết và có lý do chính đáng.
- ❌ **CẤM** field injection (`@Autowired` trên property). Phải dùng Constructor injection.
- ❌ **CẤM** trả về raw data object (List, Entity) trực tiếp từ Controller mà không wrap bởi `ApiResponse`.
