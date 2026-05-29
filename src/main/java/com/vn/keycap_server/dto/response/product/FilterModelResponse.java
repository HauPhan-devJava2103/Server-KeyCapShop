package com.vn.keycap_server.dto.response.product;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FilterModelResponse {
    private List<FilterItemResponse> category;
    private List<FilterItemResponse> type;
    private List<FilterItemResponse> brand;
}
