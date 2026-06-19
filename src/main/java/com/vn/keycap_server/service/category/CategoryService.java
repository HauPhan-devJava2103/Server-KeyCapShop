package com.vn.keycap_server.service.category;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vn.keycap_server.dto.response.CategoryResponse;
import com.vn.keycap_server.mapper.CategoryMapper;
import com.vn.keycap_server.modal.Category;
import com.vn.keycap_server.repository.CategoryRepository;

import lombok.RequiredArgsConstructor;

/**
 * Service xử lý nghiệp vụ danh mục sản phẩm.
 * Hiện tại FE admin chỉ cần danh sách category tối giản để chọn khi tạo hoặc cập nhật sản phẩm.
 */
@Service
@RequiredArgsConstructor
public class CategoryService implements ICategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    /**
     * Lấy toàn bộ danh mục trong hệ thống theo thứ tự ổn định.
     *
     * @return danh sách CategoryResponse đúng contract FE admin cần
     */
    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        // 1. Lấy danh sách category từ database và sắp xếp theo tên để dropdown hiển thị ổn định
        List<Category> categories = categoryRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));

        // 2. Map entity sang DTO, không trả entity trực tiếp ra ngoài API
        return categoryMapper.toCategoryResponseList(categories);
    }
}
