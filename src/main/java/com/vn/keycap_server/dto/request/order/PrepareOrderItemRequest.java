package com.vn.keycap_server.dto.request.order;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PrepareOrderItemRequest {
    @NotNull(message = "variantId không được null")
    private Long variantId;

    @Min(value = 1, message = "Số lượng phải >= 1")
    private int quantity;
}
