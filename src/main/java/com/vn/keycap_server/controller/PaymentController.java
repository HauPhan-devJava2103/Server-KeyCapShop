package com.vn.keycap_server.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vn.keycap_server.dto.request.payment.momo.MomoIpnRequest;
import com.vn.keycap_server.service.payment.momo.IMomoPaymentService;
import com.vn.keycap_server.service.payment.vnpay.IVNPayService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final IMomoPaymentService momoPaymentService;
    private final IVNPayService vnPayService;

    @PostMapping("/momo/ipn")
    public ResponseEntity<Void> handleIpn(@RequestBody MomoIpnRequest ipnRequest) {
        momoPaymentService.handleIpnCallback(ipnRequest);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/vnpay/ipn")
    public ResponseEntity<Void> handleVnPayReturn(
            @RequestParam Map<String, String> vnpParams) {
        vnPayService.handleIpnCallBack(vnpParams);
        return ResponseEntity.noContent().build();

    }
}
