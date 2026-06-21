package com.vn.keycap_server.service.adminstaff;

import org.springframework.data.domain.Page;

import com.vn.keycap_server.dto.request.staff.AdminStaffListRequest;
import com.vn.keycap_server.dto.request.staff.CreateAdminStaffRequest;
import com.vn.keycap_server.dto.request.staff.UpdateAdminStaffRequest;
import com.vn.keycap_server.dto.response.staff.AdminStaffResponse;

/**
 * Interface nghiệp vụ quản lý nhân viên dành riêng cho khu vực admin.
 */
public interface IAdminStaffService {

    Page<AdminStaffResponse> getStaffs(AdminStaffListRequest request);

    AdminStaffResponse getStaffById(Long staffId);

    AdminStaffResponse createStaff(CreateAdminStaffRequest request);

    AdminStaffResponse updateStaff(Long staffId, UpdateAdminStaffRequest request);

    void deleteStaff(Long staffId, Long actorUserId);
}
