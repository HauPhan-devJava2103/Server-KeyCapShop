package com.vn.keycap_server.service.user;

import com.vn.keycap_server.dto.response.user.UserProfileResponse;

public interface IUserService {

    UserProfileResponse getProfile(Long userId);
}
