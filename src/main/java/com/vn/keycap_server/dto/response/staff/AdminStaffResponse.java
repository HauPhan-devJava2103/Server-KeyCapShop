package com.vn.keycap_server.dto.response.staff;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.vn.keycap_server.utils.ERole;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response nhân viên trả về cho FE admin.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminStaffResponse {

    private Long id;

    private String name;

    private String email;

    private String phonenumber;

    private LocalDate dob;

    private String gender;

    private BigDecimal salary;

    private LocalDate createAt;

    private ERole role;
}
