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
 * Adapter that translates the application's media signature contract to
 * Cloudinary-specific signing rules.
 */
@Component
@RequiredArgsConstructor
public class CloudinaryMediaStorageAdapter implements MediaStorageAdapter {

    private static final String TEMPORARY_TAG = "tmp";

    private final Cloudinary cloudinary;
    private final CloudinaryProperties cloudinaryProperties;

    /**
     * Signs only the upload parameters sent by the frontend. Adding parameters
     * here that FE does not submit, such as folder, would make Cloudinary reject
     * the upload signature.
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
            throw new IllegalStateException("Cloudinary is not fully configured");
        }
    }
}
