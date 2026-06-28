package com.vn.keycap_server.service.adminstaff.event;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.vn.keycap_server.service.mail.IMailService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Observer gửi thông tin tài khoản sau khi transaction tạo nhân viên commit
 * thành công.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StaffAccountEmailListener {

    private final IMailService mailService;

    /**
     * Chạy sau commit để lỗi gửi email không rollback tài khoản nhân viên đã lưu.
     * Method chạy bất đồng bộ để request tạo nhân viên phản hồi nhanh hơn.
     */
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleStaffCreated(StaffCreatedEvent event) {
        try {
            mailService.sendStaffAccountEmail(
                    event.getEmail(),
                    event.getFullName(),
                    event.getTemporaryPassword());
        } catch (Exception exception) {
            log.error("Gửi email tài khoản nhân viên thất bại, staffId={}", event.getStaffId(), exception);
        }
    }
}
