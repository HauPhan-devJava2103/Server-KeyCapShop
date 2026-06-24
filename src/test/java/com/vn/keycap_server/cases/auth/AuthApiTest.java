package com.vn.keycap_server.cases.auth;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.vn.keycap_server.base.BaseApiTest;
import com.vn.keycap_server.dto.request.auth.LoginRequest;
import com.vn.keycap_server.dto.request.auth.RegisterRequest;
import com.vn.keycap_server.helpers.TestDataFactory;
import com.vn.keycap_server.utils.ERole;
import com.vn.keycap_server.modal.User;
import com.vn.keycap_server.utils.EOtpPurpose;

public class AuthApiTest extends BaseApiTest {

    @Autowired
    private TestDataFactory testDataFactory;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("Login Success - 200 OK")
    public void testLoginSuccess() throws Exception {
        User user = testDataFactory.createCustomUser("login_success@test.com", "Test User", "0123456789", ERole.USER);

        LoginRequest request = new LoginRequest("login_success@test.com", "Password123!");

        mockMvc.perform(MockMvcRequestBuilders.post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.accessToken").exists());
    }

    @Test
    @DisplayName("Login Failed - Incorrect Password - 400 Bad Request")
    public void testLoginIncorrectPassword() throws Exception {
        testDataFactory.createCustomUser("wrong_pass@test.com", "Test User", "0123456789", ERole.USER);

        LoginRequest request = new LoginRequest("wrong_pass@test.com", "WrongPass123!");

        mockMvc.perform(MockMvcRequestBuilders.post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Login Validation - Empty Email - 400 Bad Request")
    public void testLoginEmptyEmail() throws Exception {
        LoginRequest request = new LoginRequest("", "Password123!");

        mockMvc.perform(MockMvcRequestBuilders.post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @DisplayName("Register Validation - Missing OTP - 400 Bad Request")
    public void testRegisterMissingOtp() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("newuser@test.com");
        request.setPassword("Password123!");
        request.setConfirmPassword("Password123!");
        request.setOtpPurpose(EOtpPurpose.REGISTER);

        mockMvc.perform(MockMvcRequestBuilders.post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}
