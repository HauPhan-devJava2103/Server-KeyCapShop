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

    // Code For Province , District , Ward
    private LocationInfo province;
    private LocationInfo district;
    private LocationInfo ward;

    private String street;
    private String fullAddress;
    private Double latitude;
    private Double longitude;
    private Boolean isDefault;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class LocationInfo {
        private String code;
        private String name;

    }

}
