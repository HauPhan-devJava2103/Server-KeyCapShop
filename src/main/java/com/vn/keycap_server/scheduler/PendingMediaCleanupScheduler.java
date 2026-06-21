package com.vn.keycap_server.scheduler;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.vn.keycap_server.modal.Media;
import com.vn.keycap_server.repository.MediaRepository;
import com.vn.keycap_server.utils.EMediaStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class PendingMediaCleanupScheduler {

    private static final String CLOUDINARY_RESULT_OK = "ok";
    private static final String CLOUDINARY_RESULT_NOT_FOUND = "not found";

    private final MediaRepository mediaRepository;
    private final Cloudinary cloudinary;

    @Scheduled(fixedDelay = 3600000, initialDelay = 300000)
    public void cleanExpiredPendingMedias() {
        List<Media> expiredMedias = mediaRepository
                .findTop100ByStatusAndPendingExpiresAtLessThanEqualOrderByPendingExpiresAtAsc(
                        EMediaStatus.PENDING,
                        LocalDateTime.now());

        if (expiredMedias.isEmpty()) {
            return;
        }

        int deletedCount = 0;
        for (Media media : expiredMedias) {
            if (deleteCloudinaryResource(media)) {
                mediaRepository.delete(media);
                deletedCount++;
            }
        }

        log.info("Deleted {} expired pending media records.", deletedCount);
    }

    private boolean deleteCloudinaryResource(Media media) {
        try {
            Map<?, ?> result = cloudinary.uploader().destroy(
                    media.getPublicId(),
                    ObjectUtils.asMap(
                            "resource_type", media.getResourceType().name().toLowerCase(),
                            "invalidate", true));

            Object status = result.get("result");
            return CLOUDINARY_RESULT_OK.equals(status) || CLOUDINARY_RESULT_NOT_FOUND.equals(status);
        } catch (Exception exception) {
            log.warn("Cannot delete expired pending media publicId={}: {}",
                    media.getPublicId(), exception.getMessage());
            return false;
        }
    }
}
