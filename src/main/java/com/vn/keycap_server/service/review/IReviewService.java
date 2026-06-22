package com.vn.keycap_server.service.review;

import com.vn.keycap_server.dto.request.review.CreateReplyRequest;
import com.vn.keycap_server.dto.request.review.CreateReviewRequest;
import org.springframework.data.domain.Page;

import com.vn.keycap_server.dto.response.review.ReviewResponse;

public interface IReviewService {
    Page<ReviewResponse> getReviewsByProductId(Long productId, int page, int pageSize);

    void createReviews(CreateReviewRequest request, Long userId);

    void replyToReview(Long reviewId, CreateReplyRequest request, Long userId);
}
