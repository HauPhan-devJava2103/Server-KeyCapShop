package com.vn.keycap_server.service.media;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.cloudinary.Cloudinary;
import com.vn.keycap_server.configuration.cloudinary.CloudinaryProperties;
import com.vn.keycap_server.dto.request.media.SaveMediaRequest;
import com.vn.keycap_server.dto.response.media.CloudinarySignatureResponse;
import com.vn.keycap_server.dto.response.media.SavedMediaResponse;
import com.vn.keycap_server.exception.BadRequestException;
import com.vn.keycap_server.mapper.MediaMapper;
import com.vn.keycap_server.modal.Media;
import com.vn.keycap_server.modal.User;
import com.vn.keycap_server.repository.MediaRepository;
import com.vn.keycap_server.repository.UserRepository;
import com.vn.keycap_server.utils.EMediaResourceType;
import com.vn.keycap_server.utils.EMediaStatus;

@ExtendWith(MockitoExtension.class)
class MediaServiceTest {

    @Mock
    private Cloudinary cloudinary;

    @Mock
    private MediaRepository mediaRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MediaMapper mediaMapper;

    private CloudinaryProperties properties;
    private MediaService mediaService;

    @BeforeEach
    void setUp() {
        properties = new CloudinaryProperties();
        properties.setCloudName("keycap-shop");
        properties.setApiKey("api-key");
        properties.setApiSecret("api-secret");
        properties.setSignatureExpiresIn(3600);
        mediaService = new MediaService(cloudinary, properties, mediaRepository, userRepository, mediaMapper);
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

    @Test
    void saveMediasStoresPendingMediaOwnedByCurrentUser() {
        User user = User.builder().email("user@example.com").build();
        user.setId(7L);
        SaveMediaRequest request = createRequest("profiles/avatar-1");
        Media media = Media.builder().publicId(request.getPublicId()).secureUrl(request.getSecureUrl()).build();
        SavedMediaResponse savedResponse = new SavedMediaResponse(15L, request.getSecureUrl());

        when(userRepository.findById(7L)).thenReturn(Optional.of(user));
        when(mediaRepository.findAllByPublicIdIn(org.mockito.ArgumentMatchers.anyCollection()))
                .thenReturn(List.of());
        when(mediaMapper.toMedia(request)).thenReturn(media);
        when(mediaRepository.saveAll(org.mockito.ArgumentMatchers.anyList()))
                .thenAnswer(invocation -> {
                    List<Media> savedMedias = invocation.getArgument(0);
                    savedMedias.get(0).setId(15L);
                    return savedMedias;
                });
        when(mediaMapper.toSavedMediaResponses(org.mockito.ArgumentMatchers.anyList()))
                .thenReturn(List.of(savedResponse));

        List<SavedMediaResponse> responses = mediaService.saveMedias(List.of(request), 7L);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<Media>> mediasCaptor = ArgumentCaptor.forClass(List.class);
        verify(mediaRepository).saveAll(mediasCaptor.capture());
        Media savedMedia = mediasCaptor.getValue().get(0);
        assertThat(savedMedia.getUploadedBy()).isSameAs(user);
        assertThat(savedMedia.getStatus()).isEqualTo(EMediaStatus.PENDING);
        assertThat(savedMedia.getResourceType()).isEqualTo(EMediaResourceType.IMAGE);
        assertThat(responses).containsExactly(savedResponse);
    }

    @Test
    void saveMediasRejectsDuplicatePublicIdsInRequest() {
        User user = User.builder().email("user@example.com").build();
        SaveMediaRequest first = createRequest("profiles/avatar-1");
        SaveMediaRequest duplicate = createRequest("profiles/avatar-1");
        when(userRepository.findById(7L)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> mediaService.saveMedias(List.of(first, duplicate), 7L))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Danh sách media chứa public_id bị trùng");

        verify(mediaRepository, never()).saveAll(org.mockito.ArgumentMatchers.anyList());
    }

    @Test
    void saveMediasRejectsExistingMedia() {
        User user = User.builder().email("user@example.com").build();
        SaveMediaRequest request = createRequest("profiles/avatar-1");
        when(userRepository.findById(7L)).thenReturn(Optional.of(user));
        when(mediaRepository.findAllByPublicIdIn(org.mockito.ArgumentMatchers.anyCollection()))
                .thenReturn(List.of(Media.builder().publicId(request.getPublicId()).build()));

        assertThatThrownBy(() -> mediaService.saveMedias(List.of(request), 7L))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Media đã tồn tại trong hệ thống");

        verify(mediaRepository, never()).saveAll(org.mockito.ArgumentMatchers.anyList());
    }

    private SaveMediaRequest createRequest(String publicId) {
        return new SaveMediaRequest(
                publicId,
                "https://res.cloudinary.com/keycap-shop/image/upload/avatar.webp",
                "image",
                "webp",
                50_000L,
                500,
                500);
    }
}
