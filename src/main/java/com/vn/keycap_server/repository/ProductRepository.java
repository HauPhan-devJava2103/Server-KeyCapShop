package com.vn.keycap_server.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import com.vn.keycap_server.modal.Product;
import com.vn.keycap_server.utils.EProductStatus;

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

        /***
         * Lấy các sản phẩm mới được cập nhật gần đây và có trạng thái X và còn hàng.
         * 
         * @param date
         * @param status
         * @param pageable
         * @return
         */
        @Query("SELECT p FROM Product p WHERE p.updatedAt > :date AND p.status = :status " +
                        "AND EXISTS (SELECT 1 FROM ProductVariant pv WHERE pv.product = p AND pv.stockQuantity > 0)")
        Page<Product> findByUpdatedAtAfterAndStatusAndInStock(@Param("date") LocalDate date,
                        @Param("status") EProductStatus status,
                        Pageable pageable);

        /**
         * Lấy danh sách sản phẩm theo danh sách ID và trạng thái.
         *
         * @param ids    danh sách ID sản phẩm
         * @param status trạng thái sản phẩm
         * @return danh sách Product
         */
        List<Product> findByIdInAndStatus(List<Long> ids, EProductStatus status);

        /**
         * Lấy danh sách sản phẩm thuộc một danh sách thương hiệu và có trạng thái X.
         */
        Page<Product> findByBrandIdInAndStatus(List<Long> brandIds, EProductStatus status, Pageable pageable);
}
