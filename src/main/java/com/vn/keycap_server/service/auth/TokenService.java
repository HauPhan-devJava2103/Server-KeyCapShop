package com.vn.keycap_server.service.auth;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.vn.keycap_server.exception.UnauthorizedException;
import com.vn.keycap_server.modal.User;
import com.vn.keycap_server.modal.UserToken;
import com.vn.keycap_server.repository.UserTokenRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TokenService {

    @Value("${jwt.signerKey}")
    private String signerKey;

    private final UserTokenRepository userTokenRepository;

    public String generateToken(User user) {
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

    public SignedJWT verifyToken(String token) {

        try {
            JWSVerifier verifier = new MACVerifier(signerKey.getBytes());
            SignedJWT signedJWT = SignedJWT.parse(token);
            Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

            var verified = signedJWT.verify(verifier);

            // Check chữ ký + hết hạn
            if (!verified) {
                throw new UnauthorizedException("Chữ ký token không hợp lệ");
            }
            if (!expiryTime.after(new Date())) {
                throw new UnauthorizedException("Token đã hết hạn");
            }
            // Check token có trong whitelist và chưa bị revoke
            String jwtId = signedJWT.getJWTClaimsSet().getJWTID();
            if (!userTokenRepository.existsByRefreshTokenAndIsRevokedFalse(jwtId)) {
                throw new UnauthorizedException("Token đã bị vô hiệu hóa");
            }

            return signedJWT;
        } catch (ParseException | JOSEException e) {
            throw new UnauthorizedException("Token không hợp lệ");
        }
    }

    public void saveUserToken(User user, String accessToken) {
        try {
            // Parse token
            SignedJWT signedJWT = SignedJWT.parse(accessToken);
            JWTClaimsSet claims = signedJWT.getJWTClaimsSet();
            // Lấy jwtId
            String jwtId = claims.getJWTID();
            // Lấy expiresAt
            Date expiresAt = claims.getExpirationTime();
            // Tạo UserToken record
            UserToken userToken = UserToken.builder()
                    .user(user)
                    .refreshToken(jwtId) // Lưu jwtId vào field refreshToken
                    .isRevoked(false) // Token đang hoạt động
                    .expiresAt(expiresAt) // Thời hạn token
                    .build();
            // Lưu vào DB
            userTokenRepository.save(userToken);
        } catch (ParseException e) {
            throw new RuntimeException("Không thể parse token", e);
        }
    }

    private String buildScope(User user) {
        if (user.getRole() != null) {
            return "ROLE_" + user.getRole().name();
        }
        return "ROLE_USER";
    }

}
