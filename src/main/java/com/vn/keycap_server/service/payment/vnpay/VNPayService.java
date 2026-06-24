package com.vn.keycap_server.service.payment.vnpay;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.SortedMap;
import java.util.TimeZone;
import java.util.TreeMap;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.vn.keycap_server.configuration.vnpay.VNPayProperties;
import com.vn.keycap_server.exception.BadRequestException;
import com.vn.keycap_server.modal.Order;
import com.vn.keycap_server.repository.OrderRepository;
import com.vn.keycap_server.service.order.event.OrderCompletedEvent;
import com.vn.keycap_server.service.orderhistorystatus.OrderHistoryService;
import com.vn.keycap_server.utils.EOrderStatus;
import com.vn.keycap_server.utils.EPaymentStatus;
import com.vn.keycap_server.utils.VnPayEncoder;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VNPayService implements IVNPayService {

    private final VNPayProperties vnPayProperties;
    private final OrderRepository orderRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final OrderHistoryService orderHistoryService;

    @Override
    public String createPaymentUrl(Long orderId, Long userId, String clientIpAddress) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BadRequestException("Đơn hàng không tồn tại"));

        // 1. Build Params
        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        String vnp_OrderInfo = "KeyCapShop-Order #" + order.getId();
        String vnp_OrderType = "other"; // Loại danh mục hàng hóa
        String vnp_TxnRef = "KEYCAP_" + order.getId() + "_" + System.currentTimeMillis();
        String vnp_ReturnUrl = "http://localhost:3000/api/payment/vnpay/ipn";
        // Amount * 100
        long amount = order.getTotalAmount().multiply(new BigDecimal(100)).longValue();

        Map<String, String> vnpParams = new TreeMap<>();
        vnpParams.put("vnp_Version", vnp_Version);
        vnpParams.put("vnp_Command", vnp_Command);
        vnpParams.put("vnp_TmnCode", vnPayProperties.getTmnCode());
        vnpParams.put("vnp_Amount", String.valueOf(amount));
        vnpParams.put("vnp_CurrCode", "VND");
        vnpParams.put("vnp_TxnRef", vnp_TxnRef);
        vnpParams.put("vnp_OrderInfo", vnp_OrderInfo);
        vnpParams.put("vnp_OrderType", vnp_OrderType);
        vnpParams.put("vnp_Locale", "vn");
        vnpParams.put("vnp_ReturnUrl", vnp_ReturnUrl);
        vnpParams.put("vnp_IpAddr", clientIpAddress);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnpParams.put("vnp_CreateDate", vnp_CreateDate);

        // Thời gian hết hạn giao dịch (15 phút)
        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnpParams.put("vnp_ExpireDate", vnp_ExpireDate);

        // 2. Build Hash Data (URL-encoded values) và Query String (URL-encoded)
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();

        for (Map.Entry<String, String> entry : vnpParams.entrySet()) {
            String fieldName = entry.getKey();
            String fieldValue = entry.getValue();

            if (fieldValue != null && fieldValue.length() > 0) {
                // URL Encode fieldValue cho cả hashData và query
                String encodedValue = URLEncoder.encode(fieldValue, StandardCharsets.UTF_8);

                if (hashData.length() > 0) {
                    hashData.append('&');
                    query.append('&');
                }

                // hashData: Sử dụng encodedValue
                hashData.append(fieldName).append('=').append(encodedValue);

                // query: Sử dụng key đã encode và encodedValue
                query.append(URLEncoder.encode(fieldName, StandardCharsets.UTF_8))
                        .append('=')
                        .append(encodedValue);
            }
        }
        // 3. HMAC-SHA512 sign
        String secureHash = VnPayEncoder.hmacSHA512(
                vnPayProperties.getHashSecret(), hashData.toString());
        // 4. Build final URL
        String paymentUrl = vnPayProperties.getApiUrl() + "?" + query
                + "&vnp_SecureHash=" + secureHash;

        // 5. Lưu txnRef để tra cứu khi callback
        order.setTransactionId(vnp_TxnRef);
        orderRepository.save(order);

        return paymentUrl;
    }

    @Override
    public void handleIpnCallBack(Map<String, String> vnpParams) {
        // 1. Trích xuất mã hash bảo mật từ VNPay gửi sang
        String receivedHash = vnpParams.get("vnp_SecureHash");

        // 2. Gom và sắp xếp các tham số còn lại theo thứ tự Alphabet
        SortedMap<String, String> sortedParams = new TreeMap<>();

        // CHỈ LỌC lấy những config bắt đầu bằng "vnp_"
        for (Map.Entry<String, String> entry : vnpParams.entrySet()) {
            String key = entry.getKey();
            if (key.startsWith("vnp_") && !key.equals("vnp_SecureHash") && !key.equals("vnp_SecureHashType")) {
                sortedParams.put(key, entry.getValue());
            }
        }

        StringBuilder hashData = new StringBuilder();

        for (Map.Entry<String, String> entry : sortedParams.entrySet()) {
            String fieldName = entry.getKey();
            String fieldValue = entry.getValue();

            // Chỉ băm các tham số thực sự có giá trị dữ liệu
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                if (hashData.length() > 0) {
                    hashData.append('&');
                }
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8));
            }
        }

        String dataToHash = hashData.toString();

        // 3. HMAC-SHA512 Verify Signature
        String expectedHash = VnPayEncoder.hmacSHA512(
                vnPayProperties.getHashSecret(), dataToHash);

        if (!expectedHash.equalsIgnoreCase(receivedHash)) {
            throw new BadRequestException("Chữ ký VNPay không hợp lệ");
        }

        // 4. Trích xuất thông tin Đơn hàng
        String txnRef = vnpParams.get("vnp_TxnRef");
        Long dbOrderId = Long.parseLong(txnRef.split("_")[1]);

        Order order = orderRepository.findById(dbOrderId)
                .orElseThrow(() -> new BadRequestException("Đơn hàng không tồn tại"));

        // 5. Cập nhật trạng thái đơn hàng dựa trên vnp_ResponseCode
        String responseCode = vnpParams.get("vnp_ResponseCode");
        if ("00".equals(responseCode)) {
            order.setPaymentStatus(EPaymentStatus.PAID);
            order.setTransactionId(vnpParams.get("vnp_TransactionNo"));
            eventPublisher.publishEvent(
                    new OrderCompletedEvent(this, order.getId(), order.getUser().getId()));
        } else {
            order.setPaymentStatus(EPaymentStatus.FAILED);
            order.setStatus(EOrderStatus.CANCELLED);

            // Ghi lịch sử: PENDING -> CANCELLED
            orderHistoryService.recordStatusChange(order, EOrderStatus.PENDING,
                    EOrderStatus.CANCELLED,
                    "Thanh toán VNPay thất bại (code: " + responseCode + ")", null);
        }
        orderRepository.save(order);
    }
}