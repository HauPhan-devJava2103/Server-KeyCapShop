package com.vn.keycap_server.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;

import com.vn.keycap_server.dto.ApiResponse;
import com.vn.keycap_server.dto.request.media.SaveMediaRequest;
import com.vn.keycap_server.dto.response.media.SavedMediaResponse;
import com.vn.keycap_server.service.media.IMediaService;

@ExtendWith(MockitoExtension.class)
class MediasControllerTest {

    @Mock
    private IMediaService mediaService;

    @Mock
    private Jwt jwt;

    @InjectMocks
    private MediasController mediasController;

    @Test
    void saveMediasReturnsCreatedResponseWithLongId() {
        SaveMediaRequest request = new SaveMediaRequest(
                "profiles/avatar-1",
                "https://res.cloudinary.com/keycap-shop/image/upload/avatar.webp",
                "image",
                "webp",
                50_000L,
                500,
                500);
        SavedMediaResponse savedMedia = new SavedMediaResponse(15L, request.getSecureUrl());
        when(jwt.getClaim("userId")).thenReturn(7L);
        when(mediaService.saveMedias(List.of(request), 7L)).thenReturn(List.of(savedMedia));

        ResponseEntity<ApiResponse> response = mediasController.saveMedias(jwt, List.of(request));

        assertThat(response.getStatusCode().value()).isEqualTo(201);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isTrue();
        assertThat(response.getBody().getMessage()).isEqualTo("Lưu media thành công");
        assertThat(response.getBody().getData()).isEqualTo(List.of(savedMedia));
        assertThat(savedMedia.getId()).isInstanceOf(Long.class);
    }
}
