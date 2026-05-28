package com.vn.keycap_server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.vn.keycap_server.modal.Product;

/**
 * ProductRepository quản lý các truy vấn và thao tác dữ liệu trực tiếp
 * với bảng 'products' trong Database. Kế thừa JpaSpecificationExecutor
 * để hỗ trợ tìm kiếm và lọc động bằng Specification.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    // Sử dụng Specification để lọc động, nên một số câu truy vấn cần điệu kiện phức
    // tạp không cần tạo câu truy vấn riêng trong Repository chỉ kế thừa các method
    // mặc định. Nếu có câu truy vấn đặc biệt khác, ta có thể viết thêm bằng @Query.
}
