package com.vn.keycap_server.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO trả về thông tin thương hiệu tối giản cho FE admin.
 * Chỉ bao gồm các trường FE đang sử dụng: id, name và slug.
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class BrandResponse {
    private Long id;
    private String name;
    private String slug;
}
