package com.vn.keycap_server.dto.response.user;

import java.time.LocalDate;

import com.vn.keycap_server.utils.ERole;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {

    private Long id;
    private String email;
    private String fullName;
    private String avatar;
    private ERole role;
    private String phoneNumber;
    private LocalDate createdAt;
    private ProfileStatsResponse stats;
}
