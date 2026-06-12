package com.vn.keycap_server.dto.response.address;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LocationInfoResponse {
    private String code;
    private String name;

}
