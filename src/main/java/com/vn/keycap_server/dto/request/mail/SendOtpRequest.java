package com.vn.keycap_server.dto.request.mail;

import com.vn.keycap_server.utils.EOtpPurpose;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SendOtpRequest {

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    private String email;

    private EOtpPurpose purpose;
}
