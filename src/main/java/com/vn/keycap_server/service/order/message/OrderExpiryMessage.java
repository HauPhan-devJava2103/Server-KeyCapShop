package com.vn.keycap_server.service.order.message;

import java.io.Serializable;

import com.vn.keycap_server.utils.EPaymentMethod;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderExpiryMessage implements Serializable {
    private Long orderId;
    private Long userId;
    private EPaymentMethod paymentMethod;
}