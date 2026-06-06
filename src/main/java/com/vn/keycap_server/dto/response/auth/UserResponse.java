package com.vn.keycap_server.dto.response.auth;

import com.vn.keycap_server.utils.ERole;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {

    private Long id;
    private String email;
    private String fullName;
    private String avatar;
    private ERole role;

}
