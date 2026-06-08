package com.vn.keycap_server.service.shipping;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.vn.keycap_server.configuration.ghn.GhnFeignClient;
import com.vn.keycap_server.configuration.ghn.GhnProperties;
import com.vn.keycap_server.dto.request.shipping.GhnFeeRequest;
import com.vn.keycap_server.dto.response.shipping.GhnFeeResponse;
import com.vn.keycap_server.exception.BadRequestException;
import com.vn.keycap_server.modal.Address;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GhnShippingService implements IShippingService {
    private final GhnFeignClient ghnFeignClient;
    private final GhnProperties ghnProperties;

    @Override
    public BigDecimal calculateShippingFee(Address address, int totalWeight) {
        GhnFeeRequest request = GhnFeeRequest.builder()
                .service_id(53320)
                .from_district_id(ghnProperties.getFromDistrictId())
                .from_ward_code(ghnProperties.getFromWardCode())
                .to_district_id(Integer.parseInt(address.getDistrictCode()))
                .to_ward_code(address.getWardCode())
                .weight(totalWeight)
                .build();
        GhnFeeResponse response = ghnFeignClient.getShippingFee(
                ghnProperties.getToken(), ghnProperties.getShopId(), request);
        if (response.getCode() != 200) {
            throw new BadRequestException("Không thể tính phí vận chuyển");
        }
        return BigDecimal.valueOf(response.getData().getTotal());

    }

}
