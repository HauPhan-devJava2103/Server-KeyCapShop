package com.vn.keycap_server.dto.request.order;

import com.vn.keycap_server.utils.EOrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateOrderStatusRequest {
    @NotNull(message = "Trạng thái mới không được để trống")
    private EOrderStatus status;

}
