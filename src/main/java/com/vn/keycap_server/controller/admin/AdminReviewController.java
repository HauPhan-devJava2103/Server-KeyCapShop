package com.vn.keycap_server.controller.admin;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.vn.keycap_server.dto.ApiResponse;
import com.vn.keycap_server.dto.request.review.CreateReplyRequest;
import com.vn.keycap_server.service.review.IReviewService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import com.vn.keycap_server.utils.JwtUtils;

@Validated
@RestController
@RequestMapping("/admin/reviews")
@PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
@RequiredArgsConstructor
public class AdminReviewController {

    private final IReviewService reviewService;

    @PostMapping("/{reviewId}/reply")
    public ResponseEntity<ApiResponse> replyToReview(
            @PathVariable Long reviewId,
            @RequestBody @Valid CreateReplyRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        Long userId = JwtUtils.getUserId(jwt);
        reviewService.replyToReview(reviewId, request, userId);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Gửi phản hồi thành công!")
                .build());
    }
}
