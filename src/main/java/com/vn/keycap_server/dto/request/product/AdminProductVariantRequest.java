package com.vn.keycap_server.dto.request.product;

import java.math.BigDecimal;
import java.util.Map;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO nhận một biến thể sản phẩm khi admin tạo sản phẩm.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminProductVariantRequest {

    // SKU biến thể; nếu FE gửi rỗng thì BE sẽ tự sinh từ slug và attributes
    private String sku;

    // Bộ thuộc tính tạo nên biến thể, ví dụ: {"Màu sắc": "Đen", "Switch": "Red"}
    @NotEmpty(message = "Thuộc tính biến thể không được để trống")
    private Map<@NotNull(message = "Tên thuộc tính không được null") String,
            @NotNull(message = "Giá trị thuộc tính không được null") String> attributes;

    // Giá bán hiện tại của biến thể
    @NotNull(message = "Giá bán không được để trống")
    @DecimalMin(value = "0.01", message = "Giá bán phải lớn hơn 0")
    private BigDecimal price;

    // Giá gốc của biến thể
    @NotNull(message = "Giá gốc không được để trống")
    @DecimalMin(value = "0.01", message = "Giá gốc phải lớn hơn 0")
    private BigDecimal originalPrice;

    // Phần trăm giảm giá của biến thể
    @Min(value = 0, message = "Phần trăm giảm giá không được âm")
    private Integer percentDiscount;

    // Tồn kho của biến thể
    @NotNull(message = "Tồn kho không được để trống")
    @Min(value = 0, message = "Tồn kho không được âm")
    private Integer stockQuantity;
}
