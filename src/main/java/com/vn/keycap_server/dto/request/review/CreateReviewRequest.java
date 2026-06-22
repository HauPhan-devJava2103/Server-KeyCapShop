package com.vn.keycap_server.dto.request.review;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.List;

@Data
public class CreateReviewRequest {

    @NotNull(message = "Mã đơn hàng không được để trống")
    private Long orderId;

    @NotEmpty(message = "Danh sách đánh giá không được rỗng")
    @Valid
    private List<ReviewItemRequest> reviews;

    @Data
    public static class ReviewItemRequest {
        @NotNull(message = "Mã sản phẩm không được để trống")
        private Long productId;

        @NotNull(message = "Số sao không được để trống")
        @Min(value = 1, message = "Đánh giá tối thiểu là 1 sao")
        @Max(value = 5, message = "Đánh giá tối đa là 5 sao")
        private Integer rating;

        @NotBlank(message = "Nội dung đánh giá không được để trống")
        @Size(max = 1000, message = "Nội dung đánh giá không được vượt quá 1000 ký tự")
        private String content;

        private List<String> imageUrls;
    }
}
