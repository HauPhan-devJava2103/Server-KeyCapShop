package com.vn.keycap_server.service.adminstaff.event;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.vn.keycap_server.service.mail.IMailService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Observer that sends account credentials only after the staff creation
 * transaction is committed successfully.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StaffAccountEmailListener {

    private final IMailService mailService;

    /**
     * Runs after commit so email failures do not roll back the saved staff
     * account. The method is async to keep the admin request responsive.
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
            log.error("Failed to send staff account email for staffId={}", event.getStaffId(), exception);
        }
    }
}
