package com.vn.keycap_server.service.auth;

import com.vn.keycap_server.dto.request.auth.LoginGoogleRequest;
import com.vn.keycap_server.dto.request.auth.LoginRequest;
import com.vn.keycap_server.dto.request.auth.RegisterRequest;
import com.vn.keycap_server.dto.request.auth.ResetPasswordRequest;
import com.vn.keycap_server.dto.request.mail.SendOtpRequest;
import com.vn.keycap_server.dto.response.auth.LoginResponse;

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
