package com.vn.keycap_server.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vn.keycap_server.dto.request.payment.momo.MomoIpnRequest;
import com.vn.keycap_server.exception.BadRequestException;
import com.vn.keycap_server.service.payment.momo.IMomoPaymentService;
import com.vn.keycap_server.service.payment.paypal.IPayPalService;
import com.vn.keycap_server.service.payment.vnpay.IVNPayService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final IMomoPaymentService momoPaymentService;
    private final IVNPayService vnPayService;
    private final IPayPalService payPalService;

    @PostMapping("/momo/ipn")
    public ResponseEntity<Void> handleIpn(@RequestBody MomoIpnRequest ipnRequest) {
        momoPaymentService.handleIpnCallback(ipnRequest);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/vnpay/ipn")
    public ResponseEntity<Map<String, String>> handleVnPayIpn(@RequestParam Map<String, String> vnpParams) {
        try {
            vnPayService.handleIpnCallBack(vnpParams);

            Map<String, String> response = new HashMap<>();
            response.put("RspCode", "00");
            response.put("Message", "Confirm Success");
            return ResponseEntity.ok(response);
        } catch (BadRequestException e) {
            Map<String, String> response = new HashMap<>();
            response.put("RspCode", "01"); // Đơn hàng không tìm thấy hoặc lỗi checksum
            response.put("Message", e.getMessage());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("RspCode", "99");
            response.put("Message", "Unknown Error");
            return ResponseEntity.ok(response);
        }
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