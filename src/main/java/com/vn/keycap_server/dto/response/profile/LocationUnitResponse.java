package com.vn.keycap_server.dto.response.profile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response mô tả một đơn vị hành chính như tỉnh, quận/huyện hoặc phường/xã.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationUnitResponse {

    private String code;

    private String name;
}
