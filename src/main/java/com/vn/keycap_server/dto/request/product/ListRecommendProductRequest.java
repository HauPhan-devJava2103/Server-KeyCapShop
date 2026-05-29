package com.vn.keycap_server.dto.request.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import lombok.NoArgsConstructor;
import java.util.List;

import javax.annotation.processing.Generated;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ListRecommendProductRequest extends ListProductRequest {
    // Các trường bổ sung cho đề xuất sản phẩm
    List<String> excludeTypes; // Danh sách slug loại sản phẩm cần loại trừ khỏi kết quả đề xuất
    int limit; // Số lượng sản phẩm đề xuất cần lấy
}
