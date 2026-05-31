package com.vn.keycap_server.service.auth.login;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.vn.keycap_server.dto.request.LoginGoogleRequest;
import com.vn.keycap_server.dto.response.LoginResponse;
import com.vn.keycap_server.exception.BadRequestException;
import com.vn.keycap_server.modal.User;
import com.vn.keycap_server.repository.UserRepository;
import com.vn.keycap_server.service.auth.TokenService;
import com.vn.keycap_server.utils.ERole;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GoogleLoginHandler implements ILoginHandler<LoginGoogleRequest> {
    private final UserRepository userRepository;
    private final TokenService tokenService;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    @Override
    public String getLoginType() {
        return "google";
    }

    @Override
    public LoginResponse login(LoginGoogleRequest request) {
        // Verify tokenId Google
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(),
                GsonFactory.getDefaultInstance())
                .setAudience(Collections.singletonList(googleClientId))
                .build();

        GoogleIdToken idToken;
        try {
            idToken = verifier.verify(request.getIdToken());

        } catch (Exception e) {
            throw new BadRequestException("Token Google không hợp lệ");

        }
        if (idToken == null) {
            throw new BadRequestException("Token Google không hợp lệ");
        }

        // Payload Google
        GoogleIdToken.Payload payload = idToken.getPayload();
        String email = payload.getEmail();
        String fullName = (String) payload.get("name");
        String avatarUrl = (String) payload.get("picture");

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .email(email)
                            .fullName(fullName)
                            .avatarUrl(avatarUrl)
                            .role(ERole.USER)
                            .password(null)
                            .build();
                    return userRepository.save(newUser);
                });

        String accessToken = tokenService.generateToken(user);
        tokenService.saveUserToken(user, accessToken);
        return LoginResponse.builder()
                .accessToken(accessToken)
                .user(user)
                .build();
    }

}
