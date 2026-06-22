package com.vn.keycap_server.dto.request.review;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateReplyRequest {

    @NotBlank(message = "Nội dung phản hồi không được để trống")
    @Size(max = 1000, message = "Nội dung phản hồi tối đa 1000 ký tự")
    private String content;
}
