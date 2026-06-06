package com.vn.keycap_server.service.auth;

import java.text.ParseException;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jwt.SignedJWT;
import com.vn.keycap_server.dto.request.auth.LoginGoogleRequest;
import com.vn.keycap_server.dto.request.auth.LoginRequest;
import com.vn.keycap_server.dto.request.auth.RegisterRequest;
import com.vn.keycap_server.dto.request.auth.ResetPasswordRequest;
import com.vn.keycap_server.dto.request.mail.SendOtpRequest;
import com.vn.keycap_server.dto.response.auth.LoginResponse;
import com.vn.keycap_server.exception.BadRequestException;
import com.vn.keycap_server.exception.UnauthorizedException;
import com.vn.keycap_server.mapper.UserMapper;
import com.vn.keycap_server.modal.User;
import com.vn.keycap_server.modal.UserToken;
import com.vn.keycap_server.repository.UserRepository;
import com.vn.keycap_server.repository.UserTokenRepository;
import com.vn.keycap_server.service.auth.login.LoginHandlerFactory;
import com.vn.keycap_server.service.mail.IMailService;
import com.vn.keycap_server.utils.EOtpPurpose;
import com.vn.keycap_server.utils.ERole;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService implements IAuthenticationService {

    private final LoginHandlerFactory loginHandlerFactory;

    private final TokenService tokenService;

    private final UserRepository userRepository;
    private final UserTokenRepository userTokenRepository;

    private final RedisTemplate<String, String> redisTemplate;
    private final IMailService mailService;
    private final ObjectMapper objectMapper;
    private final UserMapper userMapper;

    private final PasswordEncoder passwordEncoder;

    @Override
    public LoginResponse login(LoginRequest request) {
        return loginHandlerFactory.<LoginRequest>getHandler("basic").login(request);
    }

    @Override
    public LoginResponse loginGoogle(LoginGoogleRequest request) {
        return loginHandlerFactory.<LoginGoogleRequest>getHandler("google").login(request);
    }

    @Override
    public void sendOtp(SendOtpRequest request) {

        String email = request.getEmail();
        EOtpPurpose purpose = request.getPurpose();

        if (purpose == EOtpPurpose.FORGOT_PASSWORD) {
            userRepository.findByEmail(email)
                    .orElseThrow(() -> new BadRequestException("Email không tồn tại trong hệ thống"));
        }
        if (purpose == EOtpPurpose.REGISTER && userRepository.findByEmail(email).isPresent()) {
            throw new BadRequestException("Email đã tồn tại trong hệ thống");
        }

        // Rate Limit
        String cooldownKey = "cooldown:otp:" + email;
        if (Boolean.TRUE.equals(redisTemplate.hasKey(cooldownKey))) {
            throw new BadRequestException("Vui lòng đợi 60 giây trước khi gửi lại OTP");
        }

        // Generate OTP
        String otp = String.format("%06d", new Random().nextInt(999999));

        // Save OTP Redis
        try {
            String otpKey = "otp:" + email;
            Map<String, Object> otpData = Map.of(
                    "otp", otp,
                    "attempts", 0,
                    "purpose", purpose);
            redisTemplate.opsForValue().set(otpKey, objectMapper.writeValueAsString(otpData), 5, TimeUnit.MINUTES);

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Lỗi lưu OTP", e);
        }
        redisTemplate.opsForValue().set(cooldownKey, "1", 60, TimeUnit.SECONDS);
        // Send OTP email
        mailService.sendOtpEmail(email, otp);

    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {
        // Verify OTP and Purpose
        verifyOtp(request.getEmail(), request.getOtp(),
                EOtpPurpose.FORGOT_PASSWORD, request.getOtpPurpose());

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("Mật khẩu xác nhận không khớp");
        }

        // Get user
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("Không tìm thấy tài khoản"));

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    public LoginResponse register(RegisterRequest request) {
        // Verify OTP
        verifyOtp(request.getEmail(), request.getOtp(), EOtpPurpose.REGISTER, request.getOtpPurpose());
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BadRequestException("Email đã được sử dụng");
        }
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("Mật khẩu xác nhận không khớp");
        }

        User user = userMapper.registerRequestToUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(ERole.USER);
        userRepository.save(user);

        // Login -> Register Success
        String accessToken = tokenService.generateToken(user);
        tokenService.saveUserToken(user, accessToken);
        return LoginResponse.builder()
                .accessToken(accessToken)
                .user(userMapper.toUserResponse(user))
                .build();

    }

    @Override
    public void logout(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new BadRequestException("Token không tồn tại hoặc sai định dạng");
        }

        String token = authHeader.substring(7);
        SignedJWT signedJWT = tokenService.verifyToken(token);

        try {
            String jwtId = signedJWT.getJWTClaimsSet().getJWTID();

            UserToken userToken = userTokenRepository.findByRefreshToken(jwtId)
                    .orElseThrow(() -> new UnauthorizedException("Token không tồn tại"));

            userToken.setRevoked(true);
            userTokenRepository.save(userToken);
        } catch (ParseException e) {
            throw new UnauthorizedException("Token không hợp lệ");
        }

    }

    @Override
    public boolean isTokenValid(String jwtId) {
        return userTokenRepository.existsByRefreshTokenAndIsRevokedFalse(jwtId);

    }

    // HELPER METHODS
    private void verifyOtp(String email, String inputOtp, EOtpPurpose expectedPurpose,
            EOtpPurpose actualPurpose) {

        if (actualPurpose != expectedPurpose) {
            throw new BadRequestException("Mục đích sử dụng OTP không hợp lệ");
        }
        String otpKey = "otp:" + email;

        // Get OTP Redis
        String otpJson = redisTemplate.opsForValue().get(otpKey);
        if (otpJson == null) {
            throw new BadRequestException("OTP không tồn tại hoặc đã hết hạn");
        }

        try {

            // Parse JSON
            Map<String, Object> otpData = objectMapper.readValue(otpJson, new TypeReference<>() {
            });
            String savedOtp = (String) otpData.get("otp");
            int attempts = (int) otpData.get("attempts");
            String savedPurpose = (String) otpData.get("purpose");

            if (!expectedPurpose.name().equals(savedPurpose)) {
                throw new BadRequestException("OTP này không dùng cho mục đích " + expectedPurpose.name());
            }
            // Check attempts
            if (attempts >= 5) {
                redisTemplate.delete(otpKey);
                throw new BadRequestException("Nhập sai quá 5 lần. Vui lòng lấy lại OTP mới");
            }

            // Match OTP
            if (!savedOtp.equals(inputOtp)) {
                otpData.put("attempts", attempts + 1);
                Long ttl = redisTemplate.getExpire(otpKey, TimeUnit.SECONDS);
                redisTemplate.opsForValue().set(otpKey, objectMapper.writeValueAsString(otpData), ttl,
                        TimeUnit.SECONDS);
                int remaining = 4 - attempts;
                throw new BadRequestException("OTP không chính xác. Còn " + remaining + " lần thử");
            }
            // OTP Correct
            String resetToken = UUID.randomUUID().toString();
            redisTemplate.opsForValue().set("reset-token:" + resetToken, email, 10, TimeUnit.MINUTES);

            // Delete OTP
            redisTemplate.delete(otpKey);

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Lỗi khi đọc OTP", e);
        }

    }
}
