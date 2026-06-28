package com.vn.keycap_server.service.media.adapter;

import com.vn.keycap_server.dto.response.media.MediaUploadSignatureResponse;

/**
 * Interface đích cho các nhà cung cấp lưu trữ media.
 * Service nghiệp vụ phụ thuộc vào contract này thay vì phụ thuộc trực tiếp SDK
 * vendor.
 */
public interface MediaStorageAdapter {

    /**
     * Tạo chữ ký upload ngắn hạn và giữ nguyên cấu trúc response mà frontend đang
     * dùng.
     */
    MediaUploadSignatureResponse createUploadSignature();
}
