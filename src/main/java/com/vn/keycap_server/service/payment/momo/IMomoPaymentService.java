package com.vn.keycap_server.service.payment.momo;

import com.vn.keycap_server.dto.request.payment.momo.MomoIpnRequest;
import com.vn.keycap_server.dto.response.payment.momo.MomoCreateResponse;

public interface IMomoPaymentService {

    MomoCreateResponse createPayment(Long orderId, Long userId);

    void handleIpnCallback(MomoIpnRequest ipnRequest);

}
