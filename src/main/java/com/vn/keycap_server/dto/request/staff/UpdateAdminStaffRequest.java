package com.vn.keycap_server.dto.request.staff;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vn.keycap_server.utils.EGender;
import com.vn.keycap_server.validation.EnumValue;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Payload cập nhật nhân viên từ màn hình admin.
 * Các field để nullable vì FE dùng PATCH và model update là partial.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateAdminStaffRequest {

    @Size(max = 100, message = "Tên nhân viên không được vượt quá 100 ký tự")
    private String name;

    @Email(message = "Email không hợp lệ")
    @Size(max = 100, message = "Email không được vượt quá 100 ký tự")
    private String email;

    @Pattern(
            regexp = "^(0|\\+84)[0-9]{9}$",
            message = "Số điện thoại không đúng định dạng")
    private String phonenumber;

    private LocalDate dob;

    @EnumValue(enumClass = EGender.class)
    private EGender gender;

    @DecimalMin(value = "0", message = "Mức lương không được âm")
    private BigDecimal salary;
}
