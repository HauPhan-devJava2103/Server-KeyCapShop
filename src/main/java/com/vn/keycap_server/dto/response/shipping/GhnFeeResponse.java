package com.vn.keycap_server.dto.response.shipping;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GhnFeeResponse {
    private Integer code;
    private String message;
    private FeeData data;

    @Data
    public static class FeeData {
        private Long total; // Tổng phí ship cuối cùng
        private Long service_fee; // Phí dịch vụ cơ bản
        private Long insurance_fee;// Phí bảo hiểm hàng hóa
    }
}
