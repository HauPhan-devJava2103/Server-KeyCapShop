# Hướng Dẫn Sử Dụng Bộ Kiểm Thử Tự Động (API Test Suite)

Tài liệu này cung cấp hướng dẫn đầy đủ để khởi chạy, phát triển và đọc báo cáo từ hệ thống tự động kiểm thử API của backend.

## 1. Chuẩn Bị Môi Trường
Bộ test được thiết kế độc lập, **không ảnh hưởng** đến cơ sở dữ liệu hiện tại (development/production).
- File cấu hình cho test: `src/test/resources/application-test.yaml`.
- Hệ thống sẽ sử dụng database `keycap_server_test` (tự động tạo ra nhờ cờ `createDatabaseIfNotExist=true`). Bạn chỉ cần đảm bảo **MySQL Server đang chạy** và tài khoản/mật khẩu trong file `application-test.yaml` (hoặc biến môi trường `DB_PASSWORD`) là chính xác.
- Các dịch vụ từ bên thứ 3 (MoMo, VNPay, PayPal, GHN, Cloudinary) đều được mock lại (không gọi internet).

## 2. Cách Chạy Test
Sử dụng Maven để chạy bộ test trực tiếp. Bật terminal tại thư mục `Server-KeyCapShop`:

- **Chạy TOÀN BỘ 100% API Test:**
  ```bash
  mvn test -Ptest
  ```

- **Chạy các test của một Module cụ thể (ví dụ Auth):**
  ```bash
  mvn test -Dtest="com.vn.keycap_server.cases.auth.**" -Ptest
  ```

- **Chạy riêng một class (ví dụ AuthApiTest):**
  ```bash
  mvn test -Dtest=AuthApiTest -Ptest
  ```

- **Chạy một Test Method cụ thể (ví dụ testLoginSuccess):**
  ```bash
  mvn test -Dtest=AuthApiTest#testLoginSuccess -Ptest
  ```

## 3. Hướng Dẫn Đọc Báo Cáo
Sau khi chạy xong lệnh `mvn test`, hệ thống tự động tổng hợp kết quả (kể cả khi có vài test bị lỗi văng exception, quá trình vẫn chạy hết các test khác) và xuất ra file Markdown tại:
**`docs/reports/api-test-report.md`**

**Cách hiểu báo cáo:**
- **Passed**: Số lượng API hoạt động đúng theo đặc tả (bao gồm cả các API bị gọi thiếu tham số cố tình và báo lỗi `400 Bad Request` hay `401 Unauthorized` đúng như mong đợi).
- **Failed**: Những API hoạt động không đúng, gồm các trường hợp:
  - Bị lỗi văng `500 Internal Server Error` (NullPointer, Db Exception...).
  - Sai business logic (Truyền đúng dữ liệu nhưng trả về 404, 400).
  - Trả về mã lỗi nhưng cấu trúc Response không đúng chuẩn.

Dưới báo cáo sẽ có Stack Trace chi tiết và vị trí xảy ra lỗi của từng Failed API.

## 4. Hướng Dẫn Thêm Test Mới (Cho Developer)
Khi bạn thêm mới một Controller hoặc API, hãy tuân theo luật sau:

1. Tạo file mới tại thư mục tương ứng `src/test/java/com/vn/keycap_server/cases/<tên-module>/<Tên>ApiTest.java`.
2. Class bắt buộc phải **extends `BaseApiTest`**. Bằng việc này, MockMvc, Data Rollback, và System Reporter đều đã được gắn tự động.
3. Sử dụng `@Test` của JUnit 5 cho mỗi API.
4. **Tuyệt đối không hardcode ID**. Bạn có thể dùng `TestDataFactory` để mock dữ liệu mới rồi lấy ID, vì DB sẽ được `@Transactional` dọn dẹp sạch sẽ sau mỗi method test, chạy đi chạy lại 1000 lần sẽ không bao giờ vướng trùng lặp Duplicate Key.
5. Nếu API cần login, hãy inject JWT Token thông qua `AuthTestHelper` thay vì phải gọi login thật sự.

## 5. Tích Hợp CI/CD
Hệ thống hoàn toàn thân thiện với Jenkins, GitLab CI, GitHub Actions.
- **Lệnh chạy trong CI**: `mvn clean test -Ptest`.
- **Exit Code**: Maven sẽ tự trả về `exit 1` nếu có Assertion Failed, khiến cho CI báo đỏ (Failed Pipeline).
- CI/CD có thể được setup để lưu lại Artifact file `docs/reports/api-test-report.md` thành Report sau mỗi lần deploy.
