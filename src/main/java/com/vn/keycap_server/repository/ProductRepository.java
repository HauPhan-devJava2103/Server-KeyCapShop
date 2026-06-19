package com.vn.keycap_server.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
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

    /**
     * Tìm sản phẩm theo slug và nạp trước category, brand, type để tránh N+1 query.
     */
    @EntityGraph(attributePaths = { "category", "brand", "type" })
    Optional<Product> findBySlug(String slug);

    /**
     * Tìm sản phẩm liên quan dựa trên category hoặc type, loại trừ sản phẩm hiện
     * tại.
     */
    @Query("SELECT p FROM Product p WHERE (:categoryId IS NOT NULL AND p.category.id = :categoryId OR :typeId IS NOT NULL AND p.type.id = :typeId) AND p.id <> :productId AND p.status = :status")
    List<Product> findRelatedProducts(
            @Param("categoryId") Long categoryId,
            @Param("typeId") Long typeId,
            @Param("productId") Long productId,
            @Param("status") EProductStatus status,
            Pageable pageable);

    /**
     * Lấy ID sản phẩm liên quan, loại trừ sản phẩm đầu vào và sản phẩm hết hàng.
     */
    @Query("""
            SELECT DISTINCT p.id
            FROM Product p
            WHERE p.id NOT IN :sourceIds
              AND p.status = :status
              AND EXISTS (
                  SELECT 1 FROM ProductVariant variant
                  WHERE variant.product = p AND variant.stockQuantity > 0
              )
              AND (
                  p.category.id IN (
                      SELECT source.category.id FROM Product source
                      WHERE source.id IN :sourceIds AND source.category IS NOT NULL
                  )
                  OR p.type.id IN (
                      SELECT source.type.id FROM Product source
                      WHERE source.id IN :sourceIds AND source.type IS NOT NULL
                  )
                  OR p.brand.id IN (
                      SELECT source.brand.id FROM Product source
                      WHERE source.id IN :sourceIds AND source.brand IS NOT NULL
                  )
              )
            ORDER BY p.updatedAt DESC, p.id ASC
            """)
    List<Long> findRelatedProductIds(
            @Param("sourceIds") List<Long> sourceIds,
            @Param("status") EProductStatus status,
            Pageable pageable);

    /**
     * Batch load dữ liệu cần thiết để map danh sách product card.
     */
    @Query("""
            SELECT DISTINCT p
            FROM Product p
            LEFT JOIN FETCH p.type
            LEFT JOIN FETCH p.category
            LEFT JOIN FETCH p.variants
            WHERE p.id IN :ids
            """)
    List<Product> findCardProductsByIds(@Param("ids") List<Long> ids);

    /**
     * Lấy danh sách sản phẩm cho khu vực admin, có tìm kiếm và phân trang.
     * Chỉ fetch các quan hệ ManyToOne để tránh sai phân trang khi join OneToMany.
     *
     * @param search   từ khóa tìm kiếm theo tên hoặc slug
     * @param pageable thông tin phân trang và sắp xếp
     * @return trang sản phẩm kèm category, type và brand
     */
    @EntityGraph(attributePaths = { "category", "type", "brand" })
    @Query("""
            SELECT p
            FROM Product p
            WHERE (:search IS NULL
                OR LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(p.slug) LIKE LOWER(CONCAT('%', :search, '%')))
            """)
    Page<Product> findAdminProducts(@Param("search") String search, Pageable pageable);

    /**
     * Lấy chi tiết sản phẩm cho admin theo ID.
     * Chỉ fetch các quan hệ ManyToOne; danh sách ảnh, variant, specification được
     * query riêng.
     *
     * @param id ID sản phẩm cần lấy chi tiết
     * @return Product nếu tồn tại
     */
    @EntityGraph(attributePaths = { "category", "type", "brand" })
    Optional<Product> findAdminProductDetailById(Long id);
}
