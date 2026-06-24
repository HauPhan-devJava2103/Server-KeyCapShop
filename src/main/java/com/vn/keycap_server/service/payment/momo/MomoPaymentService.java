package com.vn.keycap_server.service.payment.momo;

import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.vn.keycap_server.client.MomoFeignClient;
import com.vn.keycap_server.configuration.momo.MomoProperties;
import com.vn.keycap_server.dto.request.payment.momo.MomoCreateRequest;
import com.vn.keycap_server.dto.request.payment.momo.MomoIpnRequest;
import com.vn.keycap_server.dto.response.payment.momo.MomoCreateResponse;
import com.vn.keycap_server.exception.BadRequestException;
import com.vn.keycap_server.modal.Order;
import com.vn.keycap_server.repository.OrderRepository;
import com.vn.keycap_server.service.order.event.OrderCompletedEvent;
import com.vn.keycap_server.service.orderhistorystatus.OrderHistoryService;
import com.vn.keycap_server.utils.EOrderStatus;
import com.vn.keycap_server.utils.EPaymentStatus;
import com.vn.keycap_server.utils.MoMoEncoder;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MomoPaymentService implements IMomoPaymentService {

        private final MomoFeignClient momoFeignClient;
        private final MomoProperties momoProperties;
        private final OrderRepository orderRepository;
        private final OrderHistoryService orderHistoryService;

        private final ApplicationEventPublisher eventPublisher;

        @Override
        public MomoCreateResponse createPayment(Long orderId, Long userId) {

                Order order = orderRepository.findById(orderId)
                                .orElseThrow(() -> new BadRequestException("Đơn hàng không tồn tại"));

                // Create Order
                String momoOrderId = "KEYCAP_" + order.getId() + "_" + System.currentTimeMillis();
                String requestId = UUID.randomUUID().toString();
                String amount = order.getTotalAmount().toBigInteger().toString();
                String extraData = "";
                String requestType = "captureWallet";

                String orderInfo = "Thanh toán đơn hàng KeyCap #" + order.getId();

                // Create raw Signature
                String rawSignature = String.format(
                                "accessKey=%s&amount=%s&extraData=%s&ipnUrl=%s&orderId=%s&orderInfo=%s"
                                                + "&partnerCode=%s&redirectUrl=%s&requestId=%s&requestType=captureWallet",
                                momoProperties.getAccessKey(), amount, extraData,
                                momoProperties.getIpnUrl(), momoOrderId, orderInfo,
                                momoProperties.getPartnerCode(), momoProperties.getRedirectUrl(),
                                requestId);
                // Sign HMAC-SHA256
                String signature = MoMoEncoder.signHmacSHA256(rawSignature, momoProperties.getSecretKey());
                // Build DTO Request
                MomoCreateRequest requestBody = MomoCreateRequest.builder()
                                .partnerCode(momoProperties.getPartnerCode())
                                .requestType(requestType)
                                .ipnUrl(momoProperties.getIpnUrl())
                                .redirectUrl(momoProperties.getRedirectUrl())
                                .orderId(momoOrderId)
                                .amount(Long.parseLong(amount))
                                .orderInfo(orderInfo)
                                .requestId(requestId)
                                .extraData(extraData)
                                .signature(signature)
                                .lang("vi")
                                .build();

                // Call Momo Feign Client
                MomoCreateResponse momoResponse = momoFeignClient.createPayment(requestBody);

                if (momoResponse == null || momoResponse.getResultCode() != 0) {
                        String msg = momoResponse != null ? momoResponse.getMessage() : "No response";
                        throw new BadRequestException("Tạo thanh toán MoMo thất bại: " + msg);
                }

                // Lưu transactionId từ MoMo (momoOrderId) để tra cứu khi IPN callback
                order.setTransactionId(momoOrderId);
                orderRepository.save(order);

                return momoResponse;
        }

        @Override
        public void handleIpnCallback(MomoIpnRequest ipn) {
                // 1. Verify signature
                String rawSignature = String.format(
                                "accessKey=%s&amount=%s&extraData=%s&message=%s&orderId=%s&orderInfo=%s"
                                                + "&orderType=%s&partnerCode=%s&payType=%s&requestId=%s"
                                                + "&responseTime=%s&resultCode=%s&transId=%s",
                                momoProperties.getAccessKey(), ipn.getAmount(), ipn.getExtraData(),
                                ipn.getMessage(), ipn.getOrderId(), ipn.getOrderInfo(),
                                ipn.getOrderType(), ipn.getPartnerCode(), ipn.getPayType(),
                                ipn.getRequestId(), ipn.getResponseTime(), ipn.getResultCode(),
                                ipn.getTransId());

                String expectedSignature = MoMoEncoder.signHmacSHA256(rawSignature, momoProperties.getSecretKey());
                if (!expectedSignature.equals(ipn.getSignature())) {
                        throw new BadRequestException("Chữ ký không hợp lệ");
                }

                String[] parts = ipn.getOrderId().split("_");
                Long dbOrderId = Long.parseLong(parts[1]);

                Order order = orderRepository.findById(dbOrderId)
                                .orElseThrow(() -> new BadRequestException("Đơn hàng không tồn tại"));

                // Update Status
                if (ipn.getResultCode() == 0) {
                        order.setPaymentStatus(EPaymentStatus.PAID);
                        order.setTransactionId(String.valueOf(ipn.getTransId()));
                        eventPublisher.publishEvent(
                                        new OrderCompletedEvent(this, order.getId(), order.getUser().getId()));
                } else {
                        order.setPaymentStatus(EPaymentStatus.FAILED);
                        order.setStatus(EOrderStatus.CANCELLED);
                        // Ghi lịch sử: PENDING -> CANCELLED
                        orderHistoryService.recordStatusChange(order, EOrderStatus.PENDING,
                                        EOrderStatus.CANCELLED,
                                        "Thanh toán MoMo thất bại", null);
                }
                orderRepository.save(order);
        }

}
