package com.vn.keycap_server.dto.request.order;

import java.util.List;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PrepareCheckoutRequestWrapper {

    @NotNull(message = "items không được null")
    private List<PrepareCheckoutRequest> items;

    private Long addressId;

    @Data
    public static class PrepareCheckoutRequest {
        @NotNull(message = "variantId không được null")
        private Long variantId;

        @Min(value = 1, message = "Số lượng phải >= 1")
        private int quantity;

    }
}
