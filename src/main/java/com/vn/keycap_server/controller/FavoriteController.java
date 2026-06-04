package com.vn.keycap_server.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vn.keycap_server.dto.ApiResponse;
import com.vn.keycap_server.exception.UnauthorizedException;
import com.vn.keycap_server.service.favorite.IFavoriteService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/favorites")
public class FavoriteController {

    private final IFavoriteService favoriteService;

    // Toggle Favorite Product For User
    @PostMapping("/{productId}")
    public ResponseEntity<ApiResponse> addFavorite(
            @PathVariable Long productId,
            @AuthenticationPrincipal Jwt jwt) {
        if (jwt == null) {
            throw new UnauthorizedException("Vui lòng đăng nhập để sử dụng tính năng này");
        }
        Long userId = jwt.getClaim("userId");

        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Toggle favorite successfully")
                .data(favoriteService.toggleFavorite(productId, userId))
                .build());
    }

}
