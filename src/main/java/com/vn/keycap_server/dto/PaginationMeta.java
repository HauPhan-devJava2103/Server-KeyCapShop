package com.vn.keycap_server.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

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

    @JsonProperty("page") // Đổi tên trường thành "page" trong JSON
    private int currentPage;
    private int pageSize;

}
