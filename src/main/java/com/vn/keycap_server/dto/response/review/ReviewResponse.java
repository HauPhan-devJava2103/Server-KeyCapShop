package com.vn.keycap_server.dto.response.review;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {
    private String id;
    private UserReviewInfo user;
    private Integer rating;
    private String content;
    private LocalDate createdAt;
    private List<String> imageUrls;
}
