package com.vn.keycap_server.dto.response.order;

import java.math.BigDecimal;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CheckoutItemResponse {

    private PrepareProductInfo product;

    private int quantity;

    private BigDecimal amount;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PrepareProductInfo {
        private Long id; // ProductVariant
        private String name;
        private String imageUrl;
        private Map<String, String> attributes;
        private BigDecimal price;
        private BigDecimal originalPrice;
        private Integer discountPercentage;

    }

}
