package com.vn.keycap_server.dto.request.order;

import java.util.List;

import com.vn.keycap_server.utils.EPaymentMethod;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CheckoutRequest {
    @NotEmpty(message = "Danh sách sản phẩm không được rỗng")
    @Valid
    private List<CheckoutItemRequest> items;

    @NotNull(message = "Địa chỉ không được để trống")
    private Long addressId;

    @NotNull(message = "Phương thức thanh toán không được null")
    private EPaymentMethod paymentMethod;

    private List<Long> voucherIds;

    private List<Long> cartItemIds;

}
