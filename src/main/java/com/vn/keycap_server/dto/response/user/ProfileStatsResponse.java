package com.vn.keycap_server.dto.response.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileStatsResponse {

    private Long totalOrders;
    private Long completedOrders;
    private Long wishlistItems;
}
