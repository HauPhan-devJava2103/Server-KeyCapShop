package com.vn.keycap_server.dto.request.address;

import com.vn.keycap_server.validation.PhoneNumber;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CreateAddressRequest {

    @NotBlank(message = "Tên người nhận không được để trống")
    @Size(max = 100, message = "Tên người nhận tối đa 100 ký tự")
    private String recipientName;

    @NotBlank(message = "Số điện thoại không được để trống")
    @PhoneNumber
    private String phone;

    @NotBlank(message = "Mã tỉnh/thành phố không được để trống")
    private String provinceCode;

    @NotBlank(message = "Tên tỉnh/thành phố không được để trống")
    private String provinceName;

    @NotBlank(message = "Mã quận/huyện không được để trống")
    private String districtCode;

    @NotBlank(message = "Tên quận/huyện không được để trống")
    private String districtName;

    @NotBlank(message = "Mã phường/xã không được để trống")
    private String wardCode;

    @NotBlank(message = "Tên phường/xã không được để trống")
    private String wardName;

    private String street;

    private String fullAddress;

    private Double latitude;

    private Double longitude;

    private Boolean isDefault;
}
