package com.vn.keycap_server.service.media.adapter;

import com.vn.keycap_server.dto.response.media.MediaUploadSignatureResponse;

/**
 * Target interface for media storage providers.
 * Services depend on this contract instead of a concrete vendor SDK.
 */
public interface MediaStorageAdapter {

    /**
     * Creates a short-lived upload signature while preserving the public API shape
     * expected by the frontend.
     */
    MediaUploadSignatureResponse createUploadSignature();
}
