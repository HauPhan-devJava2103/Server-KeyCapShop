package com.vn.keycap_server.repository.projection;

import java.math.BigDecimal;

/**
 * Projection gom thông tin giá và tồn kho của biến thể theo từng sản phẩm.
 * Dùng cho danh sách admin để tránh load toàn bộ variants vào bộ nhớ.
 */
public interface ProductVariantSummaryProjection {

    /**
     * Lấy ID sản phẩm đang được gom nhóm.
     */
    Long getProductId();

    /**
     * Lấy giá thấp nhất trong các biến thể của sản phẩm.
     */
    BigDecimal getMinPrice();

    /**
     * Lấy tổng tồn kho của toàn bộ biến thể thuộc sản phẩm.
     */
    Long getTotalStockQuantity();
}
