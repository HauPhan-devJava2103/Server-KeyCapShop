package com.vn.keycap_server.scheduler;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.vn.keycap_server.repository.UserTokenRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class TokenCleanupScheduler {

    private final UserTokenRepository userTokenRepository;

    @Scheduled(fixedRate = 3600000)
    @Transactional
    public void cleanExpiredTokens() {
        log.info("Bắt đầu dọn dẹp các Token hết hạn...");

        try {
            Date now = Date.from(Instant.now());

            int deletedCount = userTokenRepository.deleteAllExpiredTokens(now);

            log.info("Đã xóa thành công {} token rác.", deletedCount);
        } catch (Exception e) {
            log.error("Có lỗi xảy ra khi dọn dẹp token: ", e);
        }
    }
}
