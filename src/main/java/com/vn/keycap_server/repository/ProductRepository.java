package com.vn.keycap_server.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    /**
     * Lấy các sản phẩm mới được cập nhật gần đây.
     * 
     * @param date     ngày cập nhật tối thiểu
     * @param pageable thông tin phân trang
     * @return Page<Product> danh sách sản phẩm
     */
    Page<Product> findByUpdatedAtAfter(LocalDate date, Pageable pageable);

    /**
     * Lấy danh sách sản phẩm theo danh sách ID.
     *
     * @param ids danh sách ID sản phẩm
     * @return danh sách Product
     */
    List<Product> findByIdIn(List<Long> ids);

    /**
     * Lấy danh sách sản phẩm thuộc một danh sách thương hiệu.
     */
    Page<Product> findByBrandIdIn(List<Long> brandIds, Pageable pageable);
}
