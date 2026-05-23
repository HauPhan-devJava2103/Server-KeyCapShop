package com.vn.keycap_server.utils;

import org.springframework.data.domain.Page;

import com.vn.keycap_server.dto.PaginationMeta;

public class PaginationUtils {

    public static PaginationMeta buildPaginationMeta(Page<?> page, int currentPage) {
        return PaginationMeta.builder()
                .currentPage(currentPage)
                .pageSize(page.getSize())
                .totalItems(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }

}
