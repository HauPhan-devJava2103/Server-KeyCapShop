package com.vn.keycap_server.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.vn.keycap_server.dto.request.auth.RegisterRequest;
import com.vn.keycap_server.dto.response.auth.UserResponse;
import com.vn.keycap_server.modal.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "fullName", source = "email", qualifiedByName = "emailToFullName")
    @Mapping(target = "dateOfBirth", ignore = true)
    @Mapping(target = "gender", ignore = true)
    @Mapping(target = "phone", ignore = true)
    @Mapping(target = "avatarMedia", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "tokens", ignore = true)
    @Mapping(target = "addresses", ignore = true)
    User registerRequestToUser(RegisterRequest request);

    @Mapping(target = "avatar", source = "avatarMedia.secureUrl")
    UserResponse toUserResponse(User user);

    @Named("emailToFullName")
    default String emailToFullName(String email) {
        return email.split("@")[0];
    }

}
