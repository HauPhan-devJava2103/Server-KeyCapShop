package com.vn.keycap_server.repository.projection;

/**
 * Projection gom điểm đánh giá trung bình theo từng sản phẩm.
 * Dùng cho danh sách admin để tránh query rating từng sản phẩm.
 */
public interface ProductRatingSummaryProjection {

    /**
     * Lấy ID sản phẩm đang được gom nhóm.
     */
    Long getProductId();

    /**
     * Lấy điểm đánh giá trung bình của sản phẩm.
     */
    Double getRating();
}
