package com.vn.keycap_server.service.order;

import java.util.Map;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.vn.keycap_server.dto.request.order.CancelOrderRequest;
import com.vn.keycap_server.dto.request.order.UpdateOrderStatusRequest;
import com.vn.keycap_server.dto.response.order.OrderAdminResponse;
import com.vn.keycap_server.exception.BadRequestException;
import com.vn.keycap_server.exception.ResourceNotFoundException;
import com.vn.keycap_server.mapper.OrderMapper;
import com.vn.keycap_server.modal.Order;
import com.vn.keycap_server.repository.OrderRepository;
import com.vn.keycap_server.service.orderhistorystatus.OrderHistoryService;
import com.vn.keycap_server.utils.EOrderStatus;
import com.vn.keycap_server.utils.EPaymentStatus;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminOrderService implements IAdminOrderService {
    private final OrderHistoryService orderHistoryService;
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final OrderStockService orderStockService;

    @Override
    public Page<OrderAdminResponse> getOrders(int page, int limit, String keyword, EOrderStatus status) {

        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by("createdAt").descending());
        Page<Order> orderPage;
        if (keyword != null && !keyword.isBlank()) {
            orderPage = orderRepository.searchOrders(keyword, status, pageable);
        } else if (status != null) {
            orderPage = orderRepository.findByStatus(status, pageable);
        } else {
            orderPage = orderRepository.findAll(pageable);
        }

        return orderPage.map(orderMapper::toOrderAdminResponse);
    }

    @Override
    public OrderAdminResponse getOrderDetail(long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Đơn hàng #" + orderId + " không tồn tại"));
        return orderMapper.toOrderAdminResponse(order);
    }

    @Override
    @Transactional
    public OrderAdminResponse updateOrderStatus(long orderId, UpdateOrderStatusRequest request, long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Đơn hàng #" + orderId + " không tồn tại"));

        // Order History
        EOrderStatus fromStaus = order.getStatus();
        EOrderStatus toStatus = request.getStatus();

        // Validate transition
        validateStatusTransition(fromStaus, toStatus);

        order.setStatus(toStatus);
        orderRepository.save(order);

        String note = generateStatusNote(toStatus);
        orderHistoryService.recordStatusChange(order, fromStaus, toStatus, note, userId);

        return orderMapper.toOrderAdminResponse(order);
    }

    @Override
    @Transactional
    public OrderAdminResponse cancelOrder(long orderId, CancelOrderRequest request, long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Đơn hàng #" + orderId + " không tồn tại"));
        if (order.getStatus() != EOrderStatus.PENDING) {
            throw new BadRequestException("Chỉ có thể hủy đơn hàng ở trạng thái PENDING");
        }

        EOrderStatus fromStatus = order.getStatus();
        order.setStatus(EOrderStatus.CANCELLED);
        order.setPaymentStatus(EPaymentStatus.FAILED);
        orderRepository.save(order);

        orderHistoryService.recordStatusChange(
                order, fromStatus, EOrderStatus.CANCELLED, request.getReason(), userId);

        // Hoàn stock
        orderStockService.restockOrderItems(order.getItems());

        return orderMapper.toOrderAdminResponse(order);

    }

    // Helper Methods

    // Validation Transition
    private static final Map<EOrderStatus, Set<EOrderStatus>> VALID_TRANSITION = Map.of(
            EOrderStatus.PENDING, Set.of(EOrderStatus.PREPARING, EOrderStatus.CANCELLED),
            EOrderStatus.PREPARING, Set.of(EOrderStatus.SHIPPING),
            EOrderStatus.SHIPPING, Set.of(EOrderStatus.SUCCESS, EOrderStatus.RETURNED));

    public void validateStatusTransition(EOrderStatus fromStatus, EOrderStatus toStatus) {

        Set<EOrderStatus> allow = VALID_TRANSITION.get(fromStatus);
        if (allow == null || !allow.contains(toStatus)) {
            throw new BadRequestException("Không thể chuyển trạng thái từ " + fromStatus + " sang " + toStatus);
        }

    }

    // Generation note
    private String generateStatusNote(EOrderStatus toStatus) {
        return switch (toStatus) {
            case PREPARING -> "Đơn hàng đang được chuẩn bị";
            case SHIPPING -> "Đơn hàng đã được giao cho đơn vị vận chuyển";
            case SUCCESS -> "Đơn hàng đã giao thành công";
            case RETURNED -> "Đơn hàng đã được trả lại";
            case CANCELLED -> "Đơn hàng đã bị hủy bởi admin";
            default -> "Cập nhật trạng thái đơn hàng";
        };
    }
}
