package com.vn.keycap_server.service.orderhistorystatus;

import org.springframework.stereotype.Service;

import com.vn.keycap_server.modal.Order;
import com.vn.keycap_server.modal.OrderStatusHistory;
import com.vn.keycap_server.repository.OrderStatusHistoryRepository;
import com.vn.keycap_server.utils.EOrderStatus;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderHistoryService implements IOderHistoryService {

    private final OrderStatusHistoryRepository orderStatusHistoryRepository;

    @Override
    public void recordStatusChange(Order order, EOrderStatus fromStatus, EOrderStatus toStatus, String note,
            Long createdBy) {
        OrderStatusHistory history = new OrderStatusHistory();
        history.setOrder(order);
        history.setFromStatus(fromStatus);
        history.setToStatus(toStatus);
        history.setNote(note);
        history.setCreatedBy(createdBy);
        orderStatusHistoryRepository.save(history);
    }

}
