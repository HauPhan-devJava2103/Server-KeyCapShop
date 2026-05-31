package com.vn.keycap_server.service.auth.login;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.vn.keycap_server.dto.request.LoginRequest;
import com.vn.keycap_server.dto.response.LoginResponse;
import com.vn.keycap_server.exception.BadRequestException;
import com.vn.keycap_server.modal.User;
import com.vn.keycap_server.repository.UserRepository;
import com.vn.keycap_server.service.auth.TokenService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BasicLoginHandler implements ILoginHandler<LoginRequest> {

    private final TokenService tokenService;
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public String getLoginType() {
        return "basic";
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        // Find User
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("Email hoặc mật khẩu không chính xác"));

        // Password Match
        boolean isPasswordMatch = passwordEncoder.matches(request.getPassword(), user.getPassword());
        if (!isPasswordMatch) {
            throw new BadRequestException("Mật khẩu không chính xác");
        }

        // Generate Token
        String accessToken = tokenService.generateToken(user);
        tokenService.saveUserToken(user, accessToken);

        return LoginResponse.builder()
                .accessToken(accessToken)
                .user(user)
                .build();
    }

}
