package com.vn.keycap_server.service.adminstaff.event;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;

/**
 * Event published after a staff account is created.
 * Observers can react without coupling their side effects to AdminStaffService.
 */
@Getter
public class StaffCreatedEvent extends ApplicationEvent {

    private final Long staffId;
    private final String email;
    private final String fullName;
    private final String temporaryPassword;

    public StaffCreatedEvent(
            Object source,
            Long staffId,
            String email,
            String fullName,
            String temporaryPassword) {
        super(source);
        this.staffId = staffId;
        this.email = email;
        this.fullName = fullName;
        this.temporaryPassword = temporaryPassword;
    }
}
