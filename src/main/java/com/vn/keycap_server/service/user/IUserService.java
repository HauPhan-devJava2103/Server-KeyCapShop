package com.vn.keycap_server.service.user;

import com.vn.keycap_server.dto.request.user.UpdateProfileRequest;
import com.vn.keycap_server.dto.response.user.UserProfileResponse;

public interface IUserService {

    UserProfileResponse getProfile(Long userId);

    /**
     * Cập nhật hồ sơ của user đang đăng nhập và trả lại profile mới nhất.
     */
    UserProfileResponse updateProfile(UpdateProfileRequest request, Long userId);
}
