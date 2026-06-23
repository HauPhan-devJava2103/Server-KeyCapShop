package com.vn.keycap_server.dto.response.review;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvailableReviewResponse {
    private Long productId;
    private Integer rating;
    private String content;
    private LocalDate createdAt;
}
