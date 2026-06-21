package com.vn.keycap_server.service.media;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.cloudinary.Cloudinary;
import com.vn.keycap_server.configuration.cloudinary.CloudinaryProperties;
import com.vn.keycap_server.dto.request.media.SaveMediaRequest;
import com.vn.keycap_server.dto.response.media.CloudinarySignatureResponse;
import com.vn.keycap_server.dto.response.media.SavedMediaResponse;
import com.vn.keycap_server.exception.BadRequestException;
import com.vn.keycap_server.exception.ResourceNotFoundException;
import com.vn.keycap_server.mapper.MediaMapper;
import com.vn.keycap_server.modal.Media;
import com.vn.keycap_server.modal.User;
import com.vn.keycap_server.repository.MediaRepository;
import com.vn.keycap_server.repository.UserRepository;
import com.vn.keycap_server.utils.EMediaResourceType;
import com.vn.keycap_server.utils.EMediaStatus;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MediaService implements IMediaService {

    private static final String TEMPORARY_TAG = "tmp";
    private static final long PENDING_MEDIA_TTL_HOURS = 12;

    private final Cloudinary cloudinary;
    private final CloudinaryProperties cloudinaryProperties;
    private final MediaRepository mediaRepository;
    private final UserRepository userRepository;
    private final MediaMapper mediaMapper;

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

    @Override
    @Transactional
    public List<SavedMediaResponse> saveMedias(List<SaveMediaRequest> requests, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));

        Set<String> publicIds = new HashSet<>();
        for (SaveMediaRequest request : requests) {
            if (!publicIds.add(request.getPublicId())) {
                throw new BadRequestException("Danh sách media chứa public_id bị trùng");
            }
        }

        if (!mediaRepository.findAllByPublicIdIn(publicIds).isEmpty()) {
            throw new BadRequestException("Media đã tồn tại trong hệ thống");
        }

        List<Media> medias = requests.stream()
                .map(request -> buildPendingMedia(request, user))
                .toList();

        return mediaMapper.toSavedMediaResponses(mediaRepository.saveAll(medias));
    }

    private Media buildPendingMedia(SaveMediaRequest request, User user) {
        Media media = mediaMapper.toMedia(request);
        media.setResourceType(EMediaResourceType.valueOf(request.getResourceType().toUpperCase()));
        media.setStatus(EMediaStatus.PENDING);
        media.setPendingExpiresAt(LocalDateTime.now().plusHours(PENDING_MEDIA_TTL_HOURS));
        media.setUploadedBy(user);
        return media;
    }

    private void validateCloudinaryConfiguration() {
        if (!StringUtils.hasText(cloudinaryProperties.getCloudName())
                || !StringUtils.hasText(cloudinaryProperties.getApiKey())
                || !StringUtils.hasText(cloudinaryProperties.getApiSecret())) {
            throw new IllegalStateException("Cloudinary chưa được cấu hình đầy đủ");
        }
    }
}
