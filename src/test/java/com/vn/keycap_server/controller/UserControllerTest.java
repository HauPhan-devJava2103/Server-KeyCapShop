package com.vn.keycap_server.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;

import com.vn.keycap_server.dto.ApiResponse;
import com.vn.keycap_server.dto.response.user.UserProfileResponse;
import com.vn.keycap_server.service.user.IUserService;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private IUserService userService;

    @Mock
    private Jwt jwt;

    @InjectMocks
    private UserController userController;

    @Test
    void getProfileUsesAuthenticatedUserIdAndReturnsExpectedResponse() {
        UserProfileResponse profile = UserProfileResponse.builder()
                .id(7L)
                .email("user@example.com")
                .build();
        when(jwt.getClaim("userId")).thenReturn(7L);
        when(userService.getProfile(7L)).thenReturn(profile);

        ResponseEntity<ApiResponse> response = userController.getProfile(jwt);

        verify(userService).getProfile(7L);
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isTrue();
        assertThat(response.getBody().getMessage()).isEqualTo("Lấy hồ sơ người dùng thành công");
        assertThat(response.getBody().getData()).isSameAs(profile);
    }
}
