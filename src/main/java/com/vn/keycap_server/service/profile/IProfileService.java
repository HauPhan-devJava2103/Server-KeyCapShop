package com.vn.keycap_server.service.profile;

import com.vn.keycap_server.dto.response.profile.DeliveryInfoResponse;

/**
 * IProfileService định nghĩa các nghiệp vụ liên quan đến hồ sơ người dùng.
 */
public interface IProfileService {

    /**
     * GET /profile/address/default-with-shipping
     * @returns DeliveryInfoModel
     *
     * Mô tả: Lấy địa chỉ mặc định của người dùng và thời gian giao hàng dự kiến
     *  - address: Địa chỉ mặc định đã lưu trong database
     *  - shippingTime: Khoảng thời gian giao hàng dự kiến theo earliestDay và latestDay
     */
    DeliveryInfoResponse getDefaultAddressAndShippingTime();
}
