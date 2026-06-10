package com.vn.keycap_server.service.order.event;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.vn.keycap_server.exception.BadRequestException;
import com.vn.keycap_server.modal.Order;
import com.vn.keycap_server.modal.User;
import com.vn.keycap_server.repository.OrderRepository;
import com.vn.keycap_server.repository.UserRepository;
import com.vn.keycap_server.service.mail.IMailService;

import lombok.RequiredArgsConstructor;

// Observer 1 - send email when order completed
@Component
@RequiredArgsConstructor
public class OrderEmailListener {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final IMailService mailService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderCompleted(OrderCompletedEvent event) {
        Order order = orderRepository.findById(event.getOrderId())
                .orElseThrow(() -> new BadRequestException("Order not found"));

        User user = userRepository.findById(event.getUserId())
                .orElseThrow(() -> new BadRequestException("User not found"));

        try {
            mailService.sendOrderConfirmation(user.getEmail(), order.getId(), order.getTotalAmount(),
                    order.getPaymentMethod());
        } catch (Exception e) {
            throw new RuntimeException("Failed to send email", e);
        }

    }

}
