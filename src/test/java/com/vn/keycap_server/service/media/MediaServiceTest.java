package com.vn.keycap_server.service.media;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.cloudinary.Cloudinary;
import com.vn.keycap_server.configuration.cloudinary.CloudinaryProperties;
import com.vn.keycap_server.dto.response.media.CloudinarySignatureResponse;

@ExtendWith(MockitoExtension.class)
class MediaServiceTest {

    @Mock
    private Cloudinary cloudinary;

    private CloudinaryProperties properties;
    private MediaService mediaService;

    @BeforeEach
    void setUp() {
        properties = new CloudinaryProperties();
        properties.setCloudName("keycap-shop");
        properties.setApiKey("api-key");
        properties.setApiSecret("api-secret");
        properties.setSignatureExpiresIn(3600);
        mediaService = new MediaService(cloudinary, properties);
    }

    @Test
    void getUploadSignatureSignsOnlyParametersSentByFrontend() {
        when(cloudinary.apiSignRequest(org.mockito.ArgumentMatchers.anyMap(), eq("api-secret")))
                .thenReturn("signed-value");

        CloudinarySignatureResponse response = mediaService.getUploadSignature();

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<String, Object>> parametersCaptor = ArgumentCaptor.forClass(Map.class);
        verify(cloudinary).apiSignRequest(parametersCaptor.capture(), eq("api-secret"));

        Map<String, Object> signedParameters = parametersCaptor.getValue();
        assertThat(signedParameters)
                .containsEntry("tags", "tmp")
                .containsKey("timestamp")
                .doesNotContainKey("folder");
        assertThat(response.getSignature()).isEqualTo("signed-value");
        assertThat(response.getTimestamp()).isEqualTo(signedParameters.get("timestamp"));
        assertThat(response.getApiKey()).isEqualTo("api-key");
        assertThat(response.getCloudName()).isEqualTo("keycap-shop");
        assertThat(response.getExpiresIn()).isEqualTo(3600);
    }

    @Test
    void getUploadSignatureRejectsMissingCloudinaryConfiguration() {
        properties.setApiSecret("");

        assertThatIllegalStateException()
                .isThrownBy(mediaService::getUploadSignature)
                .withMessage("Cloudinary chưa được cấu hình đầy đủ");
    }
}
