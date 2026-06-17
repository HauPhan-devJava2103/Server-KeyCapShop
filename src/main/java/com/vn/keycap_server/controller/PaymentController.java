package com.vn.keycap_server.controller;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vn.keycap_server.configuration.vnpay.VNPayProperties;
import com.vn.keycap_server.dto.request.payment.momo.MomoIpnRequest;
import com.vn.keycap_server.service.payment.momo.IMomoPaymentService;
import com.vn.keycap_server.service.payment.paypal.IPayPalService;
import com.vn.keycap_server.service.payment.vnpay.IVNPayService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final IMomoPaymentService momoPaymentService;
    private final IVNPayService vnPayService;
    private final IPayPalService payPalService;
    private final VNPayProperties vnPayProperties;

    @PostMapping("/momo/ipn")
    public ResponseEntity<Void> handleIpn(@RequestBody MomoIpnRequest ipnRequest) {
        momoPaymentService.handleIpnCallback(ipnRequest);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/vnpay/ipn")
    public ResponseEntity<?> handleVnPayIpn(@RequestParam Map<String, String> vnpParams) {
        String orderId = "";
        String status = "failed";

        try {
            String txnRef = vnpParams.get("vnp_TxnRef");
            if (txnRef != null) {
                orderId = txnRef.split("_")[1];
            }

            vnPayService.handleIpnCallBack(vnpParams);

            String responseCode = vnpParams.get("vnp_ResponseCode");
            status = "00".equals(responseCode) ? "success" : "failed";

        } catch (Exception e) {
            log.error("VNPay IPN processing error: {}", e.getMessage());
        }

        // Redirect browser về frontend hiển thị kết quả
        String redirectUrl = vnPayProperties.getRedirectUrl()
                + "?orderId=" + orderId + "&status=" + status;

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(redirectUrl))
                .build();
    }

    @PostMapping("/paypal/capture")
    public ResponseEntity<Map<String, String>> capturePayPal(
            @RequestParam("token") String paypalOrderId) {
        try {
            payPalService.captureOrder(paypalOrderId);
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Thanh toán PayPal thành công");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
}