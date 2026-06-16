package com.vn.keycap_server.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vn.keycap_server.dto.ApiResponse;
import com.vn.keycap_server.dto.request.media.SaveMediaRequest;
import com.vn.keycap_server.service.media.IMediaService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;

@Validated
@RestController
@RequestMapping("/medias")
@RequiredArgsConstructor
public class MediasController {

    private final IMediaService mediaService;

    @PostMapping
    public ResponseEntity<ApiResponse> saveMedias(
            @AuthenticationPrincipal Jwt jwt,
            @Valid
            @RequestBody
            @NotEmpty(message = "Danh sách media không được để trống")
            @Size(max = 10, message = "Mỗi lần chỉ được lưu tối đa 10 media")
            List<@Valid SaveMediaRequest> requests) {
        Long userId = jwt.getClaim("userId");

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.builder()
                        .success(true)
                        .message("Lưu media thành công")
                        .data(mediaService.saveMedias(requests, userId))
                        .build());
    }
}
