package com.vn.keycap_server.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import com.vn.keycap_server.dto.request.RegisterRequest;
import com.vn.keycap_server.modal.User;

@Mapper(componentModel = "spring")
@Component
public interface UserMapper {

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "fullName", source = "email", qualifiedByName = "emailToFullName")
    User registerRequestToUser(RegisterRequest request);

    @Named("emailToFullName")
    default String emailToFullName(String email) {
        return email.split("@")[0];
    }

}
