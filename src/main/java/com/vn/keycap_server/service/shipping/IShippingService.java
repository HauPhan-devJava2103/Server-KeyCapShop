package com.vn.keycap_server.service.shipping;

import java.math.BigDecimal;

import com.vn.keycap_server.modal.Address;

public interface IShippingService {

    BigDecimal calculateShippingFee(Address address, int totalWeight);
}
