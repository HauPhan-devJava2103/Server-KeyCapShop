package com.vn.keycap_server.dto.request.shipping;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GhnFeeRequest {
    private Integer service_id; // 53320 - Chuẩn

    private Long insurance_value; // Giá trị đơn hàng để GHN đền bù nếu mất

    private Integer from_district_id;

    private String from_ward_code;

    private Integer to_district_id;

    private String to_ward_code;

    private Integer weight;
}
