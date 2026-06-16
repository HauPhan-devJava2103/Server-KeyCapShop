package com.vn.keycap_server.service.payment.paypal;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.vn.keycap_server.client.PayPalOrderClient;
import com.vn.keycap_server.client.PaypalAuthClient;
import com.vn.keycap_server.configuration.paypal.PayPalProperties;
import com.vn.keycap_server.dto.response.payment.paypal.PayPalCaptureResponse;
import com.vn.keycap_server.dto.response.payment.paypal.PayPalCreateResponse;
import com.vn.keycap_server.exception.BadRequestException;
import com.vn.keycap_server.modal.Order;
import com.vn.keycap_server.repository.OrderRepository;
import com.vn.keycap_server.service.order.event.OrderCompletedEvent;
import com.vn.keycap_server.service.orderhistorystatus.OrderHistoryService;
import com.vn.keycap_server.utils.EOrderStatus;
import com.vn.keycap_server.utils.EPaymentStatus;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PayPalService implements IPayPalService {

        private final PayPalProperties payPalProperties;
        private final PaypalAuthClient paypalAuthClient;
        private final PayPalOrderClient payPalOrderClient;

        private final OrderRepository orderRepository;

        private final ApplicationEventPublisher eventPublisher;
        private final OrderHistoryService orderHistoryService;

        // Tỷ giá VND -> USD
        private static final BigDecimal VND_TO_USD_RATE = new BigDecimal("26310");

        // Create PayPal Order
        @Override
        public String createOrder(Long orderId, Long userId) {

                Order order = orderRepository.findById(orderId)
                                .orElseThrow(() -> new BadRequestException("Đơn hàng không tồn tại."));
                String bearerToken = getBearerToken();

                // Convert VND to USD
                BigDecimal amountUSD = order.getTotalAmount()
                                .divide(VND_TO_USD_RATE, 2, RoundingMode.HALF_UP);

                // 3. Build request body theo PayPal Orders API v2
                Map<String, Object> requestBody = Map.of(
                                "intent", "CAPTURE",
                                "purchase_units", List.of(
                                                Map.of(
                                                                "reference_id", "KEYCAP_" + order.getId(),
                                                                "description", "KeyCap Store - Order #" + order.getId(),
                                                                "amount", Map.of(
                                                                                "currency_code", "USD",
                                                                                "value", amountUSD.toString()))),
                                "application_context", Map.of(
                                                "return_url", payPalProperties.getRedirectUrl()
                                                                + "?orderId=" + order.getId(),
                                                "cancel_url", payPalProperties.getRedirectUrl()
                                                                + "?orderId=" + order.getId() + "&cancelled=true"));

                // Call PayPal Feign CLient
                PayPalCreateResponse paypalResponse = payPalOrderClient.createOrder(bearerToken, requestBody);
                if (paypalResponse == null || paypalResponse.getApproveUrl() == null) {
                        throw new BadRequestException("Tạo PayPal order thất bại");
                }

                order.setTransactionId(paypalResponse.getId());
                orderRepository.save(order);

                return paypalResponse.getApproveUrl();
        }

        // Capture - User Approve
        @Override
        public void captureOrder(String paypalOrderId) {
                // 1. Get Bearer Token
                String bearerToken = getBearerToken();
                // 2. Call Feign Client
                PayPalCaptureResponse captureResponse = payPalOrderClient.captureOrder(bearerToken, paypalOrderId);

                // 3. Update Order Status
                Order order = orderRepository.findByTransactionId(paypalOrderId)
                                .orElseThrow(() -> new BadRequestException("Đơn hàng không tồn tại"));

                if (captureResponse != null && captureResponse.isCompleted()) {
                        order.setPaymentStatus(EPaymentStatus.PAID);
                        order.setStatus(EOrderStatus.CONFIRMED);
                        eventPublisher.publishEvent(
                                        new OrderCompletedEvent(this, order.getId(), order.getUser().getId()));
                        // Ghi lịch sử: PENDING -> CONFIRMED
                        orderHistoryService.recordStatusChange(order, EOrderStatus.PENDING,
                                        EOrderStatus.CONFIRMED, "Thanh toán PayPal thành công", null);
                } else {
                        order.setPaymentStatus(EPaymentStatus.FAILED);
                        order.setStatus(EOrderStatus.CANCELLED);
                        // Ghi lịch sử: PENDING -> CANCELLED
                        orderHistoryService.recordStatusChange(order, EOrderStatus.PENDING,
                                        EOrderStatus.CANCELLED,
                                        "Thanh toán PayPal thất bại", null);
                }
                orderRepository.save(order);
        }

        // Helper methods
        private String getBearerToken() {
                // 1. Encode clientId:secret -> Base64
                String credentials = payPalProperties.getClientId() + ":" + payPalProperties.getClientSecret();
                String basicAuth = "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes());

                // 2. Call Feign Client
                Map<String, Object> response = paypalAuthClient.getAccessToken(
                                basicAuth,
                                "application/x-www-form-urlencoded",
                                Map.of("grant_type", "client_credentials"));
                // 3. Return "Bearer {access_token}"
                return "Bearer " + response.get("access_token");

        }
}
