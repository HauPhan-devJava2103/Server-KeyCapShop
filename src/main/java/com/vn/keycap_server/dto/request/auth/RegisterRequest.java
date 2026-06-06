package com.vn.keycap_server.dto.request.auth;

import com.vn.keycap_server.utils.EOtpPurpose;
import com.vn.keycap_server.validation.EnumValue;
import com.vn.keycap_server.validation.StrongPassword;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    private String email;

    @NotBlank(message = "OTP không được để trống")
    @Size(min = 6, max = 6, message = "OTP phải có 6 chữ số")
    private String otp;

    @NotNull(message = "Mục đích OTP không được để trống")
    @EnumValue(enumClass = EOtpPurpose.class)
    private EOtpPurpose otpPurpose;

    @NotBlank(message = "Mật khẩu không được để trống")
    @StrongPassword
    private String password;

    @NotBlank(message = "Xác nhận mật khẩu không được để trống")
    private String confirmPassword;
}
