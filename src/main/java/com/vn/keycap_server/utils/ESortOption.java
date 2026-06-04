package com.vn.keycap_server.utils;

import org.springframework.data.domain.Sort;
import lombok.Getter;

@Getter
public enum ESortOption {
    /**
     * Enum trong Java không chỉ là danh sách hằng số, nó là các Object.
     * Mỗi dòng bên trong (như PRICE_ASC) thực chất là một instance (đối tượng)
     * được khởi tạo sẵn với các tham số tương ứng.
     */
    PRICE_ASC("price", Sort.Direction.ASC), // Sắp xếp theo giá tăng dần
    PRICE_DESC("price", Sort.Direction.DESC), // Sắp xếp theo giá giảm dần
    NEWEST("createdAt", Sort.Direction.DESC), // Sắp xếp theo ngày tạo mới nhất (mặc định)
    DEFAULT("createdAt", Sort.Direction.DESC), // Tương tự NEWEST, có thể dùng để làm mặc định nếu FE không truyền sort
    A_Z("name", Sort.Direction.ASC), // Sắp xếp theo tên A-Z
    Z_A("name", Sort.Direction.DESC); // Sắp xếp theo tên Z-A

    private final String field;
    private final Sort.Direction direction;

    ESortOption(String field, Sort.Direction direction) {
        this.field = field;
        this.direction = direction;
    }

    /**
     * Chuyển đổi enum thành đối tượng Sort của Spring Data JPA
     */
    public Sort toSpringSort() {
        return Sort.by(this.direction, this.field);
    }

    /**
     * Bộ phân giải thông minh (Smart Resolver) giúp tự động ánh xạ chuỗi từ FE
     * Hỗ trợ cả chữ hoa, chữ thường, gạch ngang, gạch dưới (ví dụ: "price_asc",
     * "PRICE-ASC", "price-desc" đều được hiểu là PRICE_ASC)
     * Nếu FE truyền giá trị không hợp lệ hoặc null, sẽ trả về NEWEST làm mặc định.
     * Điều này giúp tăng tính linh hoạt và giảm lỗi do FE truyền sai giá trị sort.
     * 
     * @param value Chuỗi đại diện cho tùy chọn sắp xếp từ FE
     * @return ESortOption tương ứng hoặc NEWEST nếu không hợp lệ
     */
    public static ESortOption fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            return NEWEST;
        }

        String normalizedValue = value.trim()
                .replace("-", "_")
                .toUpperCase();
        try {
            return ESortOption.valueOf(normalizedValue);
        } catch (IllegalArgumentException e) {
            return NEWEST; // Mặc định enum NEWEST nếu FE truyền sai giá trị
        }
    }
}
