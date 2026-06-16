package com.vn.keycap_server.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vn.keycap_server.dto.ApiResponse;
import com.vn.keycap_server.service.media.IMediaService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/media")
@RequiredArgsConstructor
public class MediaController {

    private final IMediaService mediaService;

    @GetMapping("/signature")
    public ResponseEntity<ApiResponse> getUploadSignature() {
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Lấy thông tin upload Cloudinary thành công")
                .data(mediaService.getUploadSignature())
                .build());
    }
}
