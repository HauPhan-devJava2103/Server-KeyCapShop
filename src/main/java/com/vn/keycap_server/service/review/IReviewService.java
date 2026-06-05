package com.vn.keycap_server.service.review;

import org.springframework.data.domain.Page;

import com.vn.keycap_server.dto.response.review.ReviewResponse;

public interface IReviewService {
    Page<ReviewResponse> getReviewsByProductId(Long productId, int page, int pageSize);
}
