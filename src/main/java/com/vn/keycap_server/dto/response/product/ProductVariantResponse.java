package com.vn.keycap_server.dto.response.product;

import java.math.BigDecimal;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductVariantResponse {
    private Long id;
    private String sku;
    private Map<String, String> attributes;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private BigDecimal percentDiscount;
    private int stockQuantity;
}
