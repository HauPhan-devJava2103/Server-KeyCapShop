package com.vn.keycap_server.service.media;

import java.util.List;

import com.vn.keycap_server.dto.request.media.SaveMediaRequest;
import com.vn.keycap_server.dto.response.media.MediaUploadSignatureResponse;
import com.vn.keycap_server.dto.response.media.SavedMediaResponse;

public interface IMediaService {

    MediaUploadSignatureResponse getUploadSignature();

    List<SavedMediaResponse> saveMedias(List<SaveMediaRequest> requests, Long userId);
}
