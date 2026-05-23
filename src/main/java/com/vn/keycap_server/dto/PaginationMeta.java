package com.vn.keycap_server.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaginationMeta {

    private long totalItems;
    private int totalPages;
    private int currentPage;
    private int pageSize;

}
