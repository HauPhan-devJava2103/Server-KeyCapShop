package com.vn.keycap_server.service.auth;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.vn.keycap_server.dto.request.LoginGoogleRequest;
import com.vn.keycap_server.dto.request.LoginRequest;
import com.vn.keycap_server.dto.response.LoginResponse;
import com.vn.keycap_server.exception.BadRequestException;
import com.vn.keycap_server.modal.User;
import com.vn.keycap_server.repository.UserRepository;
import com.vn.keycap_server.utils.ERole;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService implements IAuthenticationService {

    private final UserRepository userRepository;

    @Value("${jwt.signerKey}")
    private String signerKey;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    @Override
    public LoginResponse login(LoginRequest request) {

        // Password Encoder
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

        // Find User
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("Email hoặc mật khẩu không chính xác"));

        // Password Match
        boolean isPasswordMatch = passwordEncoder.matches(request.getPassword(), user.getPassword());
        if (!isPasswordMatch) {
            throw new BadRequestException("Mật khẩu không chính xác");
        }

        // Generate Token
        var accessToken = generateToken(user);

        return LoginResponse.builder()
                .accessToken(accessToken)
                .user(user)
                .build();
    }

    @Override
    public LoginResponse loginGoogle(LoginGoogleRequest request) {
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
                            .password(null) // Không có password vì login bằng Google
                            .build();
                    return userRepository.save(newUser);
                });

        String accessToken = generateToken(user);
        return LoginResponse.builder()
                .accessToken(accessToken)
                .user(user)
                .build();
    }

    // HELPER METHODS
    // Generate Token
    private String generateToken(User user) {
        // Header: MATH HS512
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        // Date Time
        Date issueTime = new Date();
        Date expiryTime = new Date(Instant.ofEpochMilli(issueTime.getTime())
                .plus(1, ChronoUnit.HOURS)
                .toEpochMilli());
        // Payload
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getEmail()) // Ai đang đăng nhập
                .issuer("keycap-server") // Ai phát hành token
                .issueTime(issueTime) // Phát hành lúc nào
                .expirationTime(expiryTime) // Hết hạn lúc nào
                .jwtID(UUID.randomUUID().toString()) // ID duy nhất của token
                .claim("scope", buildScope(user)) // Quyền: ROLE_USER, ROLE_ADMIN
                .claim("userId", user.getId()) // ID user trong DB
                .build();

        // Sign Token By Secret Key
        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload);
        try {
            jwsObject.sign(new MACSigner(signerKey.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException("Can not generate token", e);
        }
    }

    private String buildScope(User user) {
        if (user.getRole() != null) {
            return "ROLE_" + user.getRole().name();
        }
        return "ROLE_USER";
    }

}
