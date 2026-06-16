package com.vn.keycap_server.dto.response.address;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressResponse {

    private Long id;
    private String fullName;
    private String phone;

    // Code - Province , District , Ward
    private LocationInfoResponse province;
    private LocationInfoResponse district;
    private LocationInfoResponse ward;

    private String street;
    private String fullAddress;
    private Double latitude;
    private Double longitude;
    private Boolean isDefault;

}
