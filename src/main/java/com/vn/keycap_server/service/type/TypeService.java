package com.vn.keycap_server.service.type;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vn.keycap_server.dto.response.TypeResponse;
import com.vn.keycap_server.mapper.TypeMapper;
import com.vn.keycap_server.modal.ProductType;
import com.vn.keycap_server.repository.ProductTypeRepository;

import lombok.RequiredArgsConstructor;

/**
 * Service xử lý nghiệp vụ loại sản phẩm.
 * Hiện tại FE admin chỉ cần danh sách type tối giản để chọn khi tạo hoặc cập nhật sản phẩm.
 */
@Service
@RequiredArgsConstructor
public class TypeService implements ITypeService {

    private final ProductTypeRepository productTypeRepository;
    private final TypeMapper typeMapper;

    /**
     * Lấy toàn bộ loại sản phẩm trong hệ thống theo thứ tự ổn định.
     *
     * @return danh sách TypeResponse đúng contract FE admin cần
     */
    @Override
    @Transactional(readOnly = true)
    public List<TypeResponse> getAllTypes() {
        // 1. Lấy danh sách type từ database và sắp xếp theo tên để dropdown hiển thị ổn định
        List<ProductType> types = productTypeRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));

        // 2. Map entity sang DTO, không trả entity trực tiếp ra ngoài API
        return typeMapper.toTypeResponseList(types);
    }
}
