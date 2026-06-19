package com.vn.keycap_server.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.vn.keycap_server.dto.response.BrandResponse;
import com.vn.keycap_server.modal.Brand;

/**
 * Mapper chuyển đổi dữ liệu thương hiệu giữa Entity và DTO.
 * Mapper chỉ map field theo contract, không chứa logic nghiệp vụ.
 */
@Mapper(componentModel = "spring")
public interface BrandMapper {

    /**
     * Chuyển một Brand entity sang BrandResponse.
     *
     * @param brand entity thương hiệu lấy từ database
     * @return DTO thương hiệu trả về cho FE
     */
    BrandResponse toBrandResponse(Brand brand);

    /**
     * Chuyển danh sách Brand entity sang danh sách BrandResponse.
     *
     * @param brands danh sách entity thương hiệu
     * @return danh sách DTO thương hiệu
     */
    List<BrandResponse> toBrandResponseList(List<Brand> brands);
}
