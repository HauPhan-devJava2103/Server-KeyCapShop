package com.vn.keycap_server.service.media;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vn.keycap_server.dto.request.media.SaveMediaRequest;
import com.vn.keycap_server.dto.response.media.MediaUploadSignatureResponse;
import com.vn.keycap_server.dto.response.media.SavedMediaResponse;
import com.vn.keycap_server.exception.BadRequestException;
import com.vn.keycap_server.exception.ResourceNotFoundException;
import com.vn.keycap_server.mapper.MediaMapper;
import com.vn.keycap_server.modal.Media;
import com.vn.keycap_server.modal.User;
import com.vn.keycap_server.repository.MediaRepository;
import com.vn.keycap_server.repository.UserRepository;
import com.vn.keycap_server.service.media.adapter.MediaStorageAdapter;
import com.vn.keycap_server.utils.EMediaResourceType;
import com.vn.keycap_server.utils.EMediaStatus;

import lombok.RequiredArgsConstructor;

/**
 * Service xử lý bản ghi media trong hệ thống.
 * Chi tiết của nhà cung cấp lưu trữ được ủy quyền cho MediaStorageAdapter.
 */
@Service
@RequiredArgsConstructor
public class MediaService implements IMediaService {

    private static final long PENDING_MEDIA_TTL_HOURS = 12;

    private final MediaStorageAdapter mediaStorageAdapter;
    private final MediaRepository mediaRepository;
    private final UserRepository userRepository;
    private final MediaMapper mediaMapper;

    /**
     * Lấy thông tin chữ ký upload thông qua adapter.
     * Cấu trúc JSON trả về cho FE vẫn giữ nguyên: signature, timestamp, apiKey,
     * cloudName, expiresIn.
     */
    @Override
    public MediaUploadSignatureResponse getUploadSignature() {
        return mediaStorageAdapter.createUploadSignature();
    }

    /**
     * Lưu metadata media sau khi frontend upload file lên nhà cung cấp lưu trữ.
     * Media mới lưu ở trạng thái PENDING cho đến khi Product hoặc User thật sự sử
     * dụng.
     */
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
}
