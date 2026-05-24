package com.vn.keycap_server.service.auth;

import java.util.Map;

import com.vn.keycap_server.dto.request.LoginGoogleRequest;
import com.vn.keycap_server.dto.request.LoginRequest;
import com.vn.keycap_server.dto.request.ResetPasswordRequest;
import com.vn.keycap_server.dto.request.SendOtpRequest;
import com.vn.keycap_server.dto.request.VerifyOtpRequest;
import com.vn.keycap_server.dto.response.LoginResponse;

public interface IAuthenticationService {

    // Login
    LoginResponse login(LoginRequest request);

    // Login Google
    LoginResponse loginGoogle(LoginGoogleRequest request);

    // Forgot Password
    void sendOtp(SendOtpRequest request);

    Map<String, String> verifyOtp(VerifyOtpRequest request);

    void resetPassword(ResetPasswordRequest request);

}
