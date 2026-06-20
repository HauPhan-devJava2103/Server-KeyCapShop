package com.vn.keycap_server.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.vn.keycap_server.dto.response.staff.AdminStaffResponse;
import com.vn.keycap_server.modal.User;

/**
 * Mapper chuyển User role STAFF sang DTO đúng contract màn quản lý nhân viên.
 */
@Mapper(componentModel = "spring")
public interface AdminStaffMapper {

    /**
     * Map các field đang đặt tên khác nhau giữa Entity và FE model.
     *
     * @param user entity người dùng có role STAFF
     * @return response nhân viên
     */
    @Mapping(source = "fullName", target = "name")
    @Mapping(source = "phone", target = "phonenumber")
    @Mapping(source = "dateOfBirth", target = "dob")
    @Mapping(source = "createdAt", target = "createAt")
    AdminStaffResponse toAdminStaffResponse(User user);
}
