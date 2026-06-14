package com.vn.keycap_server.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.vn.keycap_server.dto.response.user.UserProfileResponse;
import com.vn.keycap_server.modal.User;

@Mapper(componentModel = "spring")
public interface UserProfileMapper {

    @Mapping(target = "avatar", source = "avatarMedia.secureUrl")
    @Mapping(target = "phoneNumber", source = "phone")
    @Mapping(target = "stats", ignore = true)
    UserProfileResponse toUserProfileResponse(User user);
}
