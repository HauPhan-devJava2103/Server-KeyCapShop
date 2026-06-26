package com.vn.keycap_server.service.adminstaff.event;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;

/**
 * Sự kiện được phát ra sau khi tài khoản nhân viên được tạo.
 * Các observer có thể xử lý tác vụ phụ mà không làm AdminStaffService phụ thuộc
 * trực tiếp vào chúng.
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
