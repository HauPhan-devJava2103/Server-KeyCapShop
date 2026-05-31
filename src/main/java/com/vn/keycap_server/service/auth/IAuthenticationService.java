package com.vn.keycap_server.service.auth;

import com.vn.keycap_server.dto.request.LoginGoogleRequest;
import com.vn.keycap_server.dto.request.LoginRequest;
import com.vn.keycap_server.dto.request.RegisterRequest;
import com.vn.keycap_server.dto.request.ResetPasswordRequest;
import com.vn.keycap_server.dto.request.SendOtpRequest;
import com.vn.keycap_server.dto.response.LoginResponse;

public interface IAuthenticationService {

    // Login
    LoginResponse login(LoginRequest request);

    // Login Google
    LoginResponse loginGoogle(LoginGoogleRequest request);

    // Forgot Password
    void sendOtp(SendOtpRequest request);

    void resetPassword(ResetPasswordRequest request);

    // Register
    LoginResponse register(RegisterRequest request);

    // Logout
    void logout(String token);

    boolean isTokenValid(String jwtId);

}
