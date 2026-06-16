package com.vn.keycap_server.service.orderhistorystatus;

import com.vn.keycap_server.modal.Order;
import com.vn.keycap_server.utils.EOrderStatus;

public interface IOderHistoryService {
    void recordStatusChange(Order order, EOrderStatus fromStatus,
            EOrderStatus toStatus, String note, Long createdBy);

}
