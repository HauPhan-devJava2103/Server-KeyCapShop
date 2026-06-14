package com.vn.keycap_server.dto.response.order;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.vn.keycap_server.dto.response.address.AddressResponse;
import com.vn.keycap_server.utils.EOrderStatus;
import com.vn.keycap_server.utils.EPaymentMethod;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {
    private Long id;
    private BigDecimal totalAmount;
    private BigDecimal shippingFee;
    private EOrderStatus status;
    private EPaymentMethod paymentMethod;
    private LocalDate createdAt;
    private AddressResponse address;
    private List<OrderStatusHistoryResponse> statusHistory;
    private List<OrderItemResponse> items;
}
