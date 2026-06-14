package com.vn.keycap_server.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import com.vn.keycap_server.dto.ApiResponse;
import com.vn.keycap_server.dto.response.media.CloudinarySignatureResponse;
import com.vn.keycap_server.service.media.IMediaService;

@ExtendWith(MockitoExtension.class)
class MediaControllerTest {

    @Mock
    private IMediaService mediaService;

    @InjectMocks
    private MediaController mediaController;

    @Test
    void getUploadSignatureReturnsExpectedApiResponse() {
        CloudinarySignatureResponse signatureResponse = CloudinarySignatureResponse.builder()
                .signature("signed-value")
                .timestamp(1_781_431_200L)
                .apiKey("api-key")
                .cloudName("keycap-shop")
                .expiresIn(3600)
                .build();
        when(mediaService.getUploadSignature()).thenReturn(signatureResponse);

        ResponseEntity<ApiResponse> response = mediaController.getUploadSignature();

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isTrue();
        assertThat(response.getBody().getMessage()).isEqualTo("Lấy thông tin upload Cloudinary thành công");
        assertThat(response.getBody().getData()).isSameAs(signatureResponse);
    }
}
