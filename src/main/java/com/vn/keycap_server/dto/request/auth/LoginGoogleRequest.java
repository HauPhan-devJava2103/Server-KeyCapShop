package com.vn.keycap_server.dto.request.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginGoogleRequest {
    @NotBlank(message = "idToken không được để trống")
    private String idToken;
}
