package com.vn.keycap_server.dto.response.product;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ProductCardResponse là DTO dùng để hiển thị thông tin rút gọn của sản phẩm
 * trên giao diện danh sách (dạng thẻ/card).
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductCardResponse {

    // Mã định danh duy nhất của sản phẩm
    private Long id;

    // Tên sản phẩm
    private String name;

    // Link ảnh đại diện
    private String imageUrl;

    // Loại sản phẩm (ví dụ: Linear, Tactile, Clicky)
    private String typeName;

    // Trạng thái yêu thích của user hiện tại
    @JsonProperty("isFavorite") // Đổi tên trường thành "isFavorite" trong JSON
    private boolean favorite;

    // Giá thấp nhất trong các biến thể của sản phẩm, được sử dụng để hiển thị giá
    // trên thẻ sản phẩm
    // Phải xủ lý ở Service để tìm ra min giữa các biến thể
    private BigDecimal minPrice;

    // Đường dẫn slug thân thiện với SEO
    // Quy tắc:
    /**
     * Đường dẫn thân thiện của sản phẩm, dùng để xây dựng URL và lấy dữ liệu sản
     * phẩm bằng slug
     * Quy tắc: Toàn bộ là chữ thường, không dấu, cách nhau bởi dấu gạch ngang (-)
     * Ví dụ:
     * - sản phẩm tên Bàn phím cơ custom Akko 3068B Plus
     * - slug: ban-phim-co-custom-akko-3068b-plus
     * 
     * Lưu ý:
     * - Nếu xây dựng api tạo sản phẩm, tên sản phẩm có thể trùng, nhưng slug tuyệt
     * đối không được trùng.
     * - Ví dụ: 2 sản phẩm đều tên là keyboard, thì phải có 2 slug là keyboard-1 và
     * keyboard-2
     */
    private String slug;
}