package com.vn.keycap_server.service.auth;

import com.vn.keycap_server.dto.request.LoginRequest;
import com.vn.keycap_server.dto.response.LoginResponse;

public interface IAuthenticationService {

    LoginResponse login(LoginRequest request);

}
