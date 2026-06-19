package com.vn.keycap_server.service.brand;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vn.keycap_server.dto.response.BrandResponse;
import com.vn.keycap_server.mapper.BrandMapper;
import com.vn.keycap_server.modal.Brand;
import com.vn.keycap_server.repository.BrandRepository;

import lombok.RequiredArgsConstructor;

/**
 * Service xử lý nghiệp vụ thương hiệu.
 */
@Service
@RequiredArgsConstructor
public class BrandService implements IBrandService {

    private final BrandRepository brandRepository;
    private final BrandMapper brandMapper;

    /**
     * Lấy toàn bộ thương hiệu trong hệ thống theo thứ tự ổn định.
     *
     * @return danh sách BrandResponse đúng contract FE admin cần
     */
    @Override
    @Transactional(readOnly = true)
    public List<BrandResponse> getAllBrands() {
        // 1. Lấy danh sách brand từ database và sắp xếp theo tên để dropdown hiển thị
        // ổn định
        List<Brand> brands = brandRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));

        // 2. Map entity sang DTO, không trả entity trực tiếp ra ngoài API
        return brandMapper.toBrandResponseList(brands);
    }
}
