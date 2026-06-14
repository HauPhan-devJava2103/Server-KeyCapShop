package com.vn.keycap_server.service.media;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.cloudinary.Cloudinary;
import com.vn.keycap_server.configuration.cloudinary.CloudinaryProperties;
import com.vn.keycap_server.dto.response.media.CloudinarySignatureResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MediaService implements IMediaService {

    private static final String TEMPORARY_TAG = "tmp";

    private final Cloudinary cloudinary;
    private final CloudinaryProperties cloudinaryProperties;

    @Override
    public CloudinarySignatureResponse getUploadSignature() {
        validateCloudinaryConfiguration();
        long timestamp = Instant.now().getEpochSecond();

        // Chỉ ký các tham số mà FE gửi khi upload trực tiếp lên Cloudinary.
        Map<String, Object> parametersToSign = new HashMap<>();
        parametersToSign.put("timestamp", timestamp);
        parametersToSign.put("tags", TEMPORARY_TAG);

        String signature = cloudinary.apiSignRequest(parametersToSign, cloudinaryProperties.getApiSecret());

        return CloudinarySignatureResponse.builder()
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
