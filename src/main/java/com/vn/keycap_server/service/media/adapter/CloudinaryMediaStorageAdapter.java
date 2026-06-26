package com.vn.keycap_server.service.media.adapter;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.cloudinary.Cloudinary;
import com.vn.keycap_server.configuration.cloudinary.CloudinaryProperties;
import com.vn.keycap_server.dto.response.media.MediaUploadSignatureResponse;

import lombok.RequiredArgsConstructor;

/**
 * Adapter chuyển contract tạo chữ ký upload của hệ thống sang quy tắc ký riêng
 * của Cloudinary.
 */
@Component
@RequiredArgsConstructor
public class CloudinaryMediaStorageAdapter implements MediaStorageAdapter {

    private static final String TEMPORARY_TAG = "tmp";

    private final Cloudinary cloudinary;
    private final CloudinaryProperties cloudinaryProperties;

    /**
     * Chỉ ký các tham số FE thật sự gửi lên Cloudinary.
     * Nếu ký thêm tham số FE không gửi, ví dụ folder, Cloudinary sẽ từ chối chữ ký.
     */
    @Override
    public MediaUploadSignatureResponse createUploadSignature() {
        validateCloudinaryConfiguration();

        long timestamp = Instant.now().getEpochSecond();
        Map<String, Object> parametersToSign = new HashMap<>();
        parametersToSign.put("timestamp", timestamp);
        parametersToSign.put("tags", TEMPORARY_TAG);

        String signature = cloudinary.apiSignRequest(parametersToSign, cloudinaryProperties.getApiSecret());

        return MediaUploadSignatureResponse.builder()
                .signature(signature)
                .timestamp(timestamp)
                .apiKey(cloudinaryProperties.getApiKey())
                .cloudName(cloudinaryProperties.getCloudName())
                .expiresIn(cloudinaryProperties.getSignatureExpiresIn())
                .build();
    }

    private void validateCloudinaryConfiguration() {
        if (!StringUtils.hasText(cloudinaryProperties.getCloudName())
                || !StringUtils.hasText(cloudinaryProperties.getApiKey())
                || !StringUtils.hasText(cloudinaryProperties.getApiSecret())) {
            throw new IllegalStateException("Cloudinary chưa được cấu hình đầy đủ");
        }
    }
}
