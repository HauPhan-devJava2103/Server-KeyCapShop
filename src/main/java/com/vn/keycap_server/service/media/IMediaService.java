package com.vn.keycap_server.service.media;

import com.vn.keycap_server.dto.response.media.CloudinarySignatureResponse;

public interface IMediaService {

    CloudinarySignatureResponse getUploadSignature();
}
