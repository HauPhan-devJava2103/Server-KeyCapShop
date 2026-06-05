package com.vn.keycap_server.service.review;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vn.keycap_server.dto.response.review.ReviewResponse;
import com.vn.keycap_server.mapper.ReviewMapper;
import com.vn.keycap_server.modal.Review;
import com.vn.keycap_server.repository.ReviewRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewService implements IReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<ReviewResponse> getReviewsByProductId(Long productId, int page, int pageSize) {
        // Chuẩn hóa trang bắt đầu từ 0 cho Spring Data JPA
        int pageIndex = Math.max(0, page - 1);
        int size = Math.max(1, pageSize);

        // Đánh giá mới nhất hiển thị trước
        Pageable pageable = PageRequest.of(pageIndex, size, Sort.by("createdAt").descending());

        Page<Review> reviewPage = reviewRepository.findByProduct_Id(productId, pageable);

        List<ReviewResponse> responses = reviewPage.getContent().stream()
                .map(reviewMapper::reviewToReviewResponse)
                .collect(Collectors.toList());

        return new PageImpl<>(responses, pageable, reviewPage.getTotalElements());
    }
}
