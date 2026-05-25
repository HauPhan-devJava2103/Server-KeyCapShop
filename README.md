# Hướng dẫn tạo project Spring Boot trên VS Code

## 1) Cài công cụ cần thiết
- **JDK 21** (hoặc phiên bản JDK mà project yêu cầu)
- **VS Code**
- Extension trong VS Code:
  - **Extension Pack for Java**
  - **Spring Boot Extension Pack**

## 2) Tạo project Spring Boot
1. Mở VS Code, nhấn `Ctrl + Shift + P`.
2. Gõ và chọn **Spring Initializr: Create a Maven Project**.
3. Chọn phiên bản Spring Boot.
4. Nhập thông tin project:
   - `Group`: ví dụ `com.example`
   - `Artifact`: ví dụ `demo`
   - `Name`: ví dụ `demo`
   - `Package name`: ví dụ `com.example.demo`
   - `Packaging`: `Jar`
   - `Java`: `21`
5. Chọn dependencies cơ bản (ví dụ: **Spring Web**, **Spring Data JPA**, **Validation**, **MySQL Driver**).
6. Chọn thư mục lưu project, VS Code sẽ tự sinh mã nguồn.

## 3) Mở và chạy project
1. Mở thư mục project vừa tạo trong VS Code.
2. Mở terminal và chạy:

```bash
./mvnw spring-boot:run
```

> Trên Windows dùng:
>
> ```bash
> mvnw.cmd spring-boot:run
> ```

3. Khi thấy log `Started ...Application` là project chạy thành công.

## 4) Tạo nhanh một API mẫu
Tạo file `HelloController.java`:

```java
package com.example.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @GetMapping("/hello")
    public String hello() {
        return "Hello Spring Boot!";
    }
}
```

Kiểm tra: mở `http://localhost:8080/hello`.
