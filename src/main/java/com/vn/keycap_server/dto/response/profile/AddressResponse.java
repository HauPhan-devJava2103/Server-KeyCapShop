package com.vn.keycap_server.dto.response.profile;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response chứa địa chỉ giao hàng mặc định của người dùng.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressResponse {

    private String id;

    private String fullName;

    private String phone;

    private LocationUnitResponse province;

    private LocationUnitResponse district;

    private LocationUnitResponse ward;

    private String street;

    private String fullAddress;

    private Double latitude;

    private Double longitude;

    @JsonProperty("isDefault")
    private boolean defaultAddress;
}
