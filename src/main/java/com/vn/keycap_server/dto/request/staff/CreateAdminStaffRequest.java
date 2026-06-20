package com.vn.keycap_server.dto.request.staff;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.vn.keycap_server.utils.EGender;
import com.vn.keycap_server.validation.EnumValue;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Payload tạo nhân viên từ màn hình admin.
 */
@Data
public class CreateAdminStaffRequest {

    @NotBlank(message = "Tên nhân viên không được để trống")
    @Size(max = 100, message = "Tên nhân viên không được vượt quá 100 ký tự")
    private String name;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    @Size(max = 100, message = "Email không được vượt quá 100 ký tự")
    private String email;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^(0|\\+84)[0-9]{9}$", message = "Số điện thoại không đúng định dạng")
    private String phonenumber;

    @NotNull(message = "Ngày sinh không được để trống")
    private LocalDate dob;

    @NotNull(message = "Giới tính không được để trống")
    @EnumValue(enumClass = EGender.class)
    private EGender gender;

    @NotNull(message = "Mức lương không được để trống")
    @DecimalMin(value = "0", message = "Mức lương không được âm")
    private BigDecimal salary;
}
