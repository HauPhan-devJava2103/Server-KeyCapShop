package com.vn.keycap_server.service.order;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vn.keycap_server.dto.request.order.CancelOrderRequest;
import com.vn.keycap_server.dto.request.order.CheckoutItemRequest;
import com.vn.keycap_server.dto.request.order.CheckoutRequest;
import com.vn.keycap_server.dto.request.order.PrepareCheckoutRequestWrapper;
import com.vn.keycap_server.dto.request.order.PrepareCheckoutRequestWrapper.PrepareCheckoutRequest;
import com.vn.keycap_server.dto.response.order.CheckoutItemResponse;
import com.vn.keycap_server.dto.response.order.CheckoutItemResponse.PrepareProductInfo;
import com.vn.keycap_server.dto.response.order.CheckoutResponse;
import com.vn.keycap_server.dto.response.order.CheckoutResult;
import com.vn.keycap_server.dto.response.order.OrderResponse;
import com.vn.keycap_server.dto.response.order.PrepareCheckoutResponse;
import com.vn.keycap_server.exception.BadRequestException;
import com.vn.keycap_server.mapper.OrderMapper;
import com.vn.keycap_server.modal.Address;
import com.vn.keycap_server.modal.Order;
import com.vn.keycap_server.modal.OrderItem;
import com.vn.keycap_server.modal.ProductImage;
import com.vn.keycap_server.modal.ProductVariant;
import com.vn.keycap_server.modal.ProductVariantAttribute;
import com.vn.keycap_server.modal.User;
import com.vn.keycap_server.repository.AddressRepository;
import com.vn.keycap_server.repository.CartItemRepository;
import com.vn.keycap_server.repository.OrderItemRepository;
import com.vn.keycap_server.repository.OrderRepository;
import com.vn.keycap_server.repository.ProductVariantRepository;
import com.vn.keycap_server.repository.UserRepository;
import com.vn.keycap_server.service.order.message.OrderExpiryProducer;
import com.vn.keycap_server.service.orderhistorystatus.OrderHistoryService;
import com.vn.keycap_server.service.payment.IPaymentStrategy;
import com.vn.keycap_server.service.shipping.GhnShippingService;
import com.vn.keycap_server.utils.EOrderStatus;
import com.vn.keycap_server.utils.EPaymentMethod;
import com.vn.keycap_server.utils.EPaymentStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService implements IOrderService {

        private final ProductVariantRepository productVariantRepository;
        private final AddressRepository addressRepository;
        private final UserRepository userRepository;
        private final OrderRepository orderRepository;
        private final OrderItemRepository orderItemRepository;
        private final CartItemRepository cartItemRepository;
        private final OrderHistoryService orderHistoryService;

        private final GhnShippingService ghnShippingService;

        private final List<IPaymentStrategy> paymentStrategies;

        private final OrderExpiryProducer orderExpiryProducer;

        private final OrderMapper orderMapper;

        @Override
        public PrepareCheckoutResponse prepareOrder(PrepareCheckoutRequestWrapper request, Long userId) {
                List<PrepareCheckoutRequest> items = request.getItems();
                // Get VarientIds
                List<Long> varientIds = items.stream().map(PrepareCheckoutRequest::getVariantId).toList();
                // List ProductVarient
                List<ProductVariant> productVariants = productVariantRepository
                                .findAllByWithProductAndAttributes(varientIds);

                // Map ProductVarient
                Map<Long, ProductVariant> variantMap = productVariants.stream()
                                .collect(Collectors.toMap(ProductVariant::getId, Function.identity()));
                // Get Address
                Address address = request.getAddressId() == null ? null
                                : addressRepository.findById(request.getAddressId())
                                                .orElse(null);

                if (address == null) {
                        address = addressRepository.findByUserIdAndIsDefaultTrue(userId)
                                        .orElse(null);
                }
                // Subtotal
                BigDecimal subtotal = BigDecimal.ZERO;

                List<CheckoutItemResponse> response = new ArrayList<>();
                for (PrepareCheckoutRequest item : items) {
                        ProductVariant productVariant = variantMap.get(item.getVariantId());

                        if (productVariant == null) {
                                throw new BadRequestException(
                                                "Sản phẩm với variantId=" + item.getVariantId() + " không tồn tại");
                        }
                        // Check Stock Quantity
                        if (productVariant.getStockQuantity() < item.getQuantity()) {
                                throw new BadRequestException(
                                                "Sản phẩm '" + productVariant.getProduct().getName()
                                                                + "' không đủ số lượng");
                        }
                        BigDecimal amount = productVariant.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                        subtotal = subtotal.add(amount);

                        Map<String, String> attributes = productVariant.getAttributes().stream()
                                        .collect(Collectors.toMap(
                                                        ProductVariantAttribute::getName,
                                                        ProductVariantAttribute::getValue));
                        // Get Image Primary
                        String imageUrl = productVariant.getProduct().getImages()
                                        .stream()
                                        .filter(img -> Boolean.TRUE.equals(img.getPrimary()))
                                        .findFirst().map(ProductImage::getUrl)
                                        .orElse(productVariant.getProduct().getImages().isEmpty() ? null
                                                        : productVariant.getProduct().getImages().get(0).getUrl());

                        response.add(CheckoutItemResponse.builder()
                                        .product(
                                                        PrepareProductInfo.builder()
                                                                        .id(productVariant.getId())
                                                                        .name(productVariant.getProduct().getName())
                                                                        .imageUrl(imageUrl)
                                                                        .attributes(attributes)
                                                                        .price(productVariant.getPrice())
                                                                        .originalPrice(productVariant
                                                                                        .getOriginalPrice())
                                                                        .discountPercentage(productVariant
                                                                                        .getPercentDiscount())
                                                                        .build())
                                        .quantity(item.getQuantity())
                                        .amount(amount)
                                        .build());

                }

                // Total Weight
                int totalWeight = 1000; // TODO: Phát triển thêm sau này giả định 1kg

                // Shipping Fee
                BigDecimal shippingFee = BigDecimal.ZERO;
                if (address != null) {
                        shippingFee = ghnShippingService.calculateShippingFee(address, totalWeight);
                } else {
                        log.info("Address is null, shipping fee defaults to 0");
                }

                // Total Amount
                BigDecimal totalAmount = subtotal.add(shippingFee);

                // Prepare response
                return PrepareCheckoutResponse.builder()
                                .items(response)
                                .subTotal(subtotal)
                                .shippingFee(shippingFee)
                                .totalAmount(totalAmount)
                                .build();
        }

        @Override
        @Transactional
        public CheckoutResponse checkout(CheckoutRequest request, Long userId) {
                // Get User and Address
                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new BadRequestException("Không tìm thấy người dùng"));

                Address address = addressRepository.findById(request.getAddressId())
                                .orElseThrow(() -> new BadRequestException("Không tìm thấy địa chỉ"));
                if (!address.getUser().getId().equals(userId)) {
                        throw new BadRequestException("Địa chỉ không thuộc người dùng này");
                }

                // Get VarientIds
                List<Long> varientIds = request.getItems().stream().map(CheckoutItemRequest::getVariantId).toList();
                // List ProductVarient
                List<ProductVariant> productVariants = productVariantRepository
                                .findAllByWithProductAndAttributes(varientIds);

                // Map ProductVarient
                Map<Long, ProductVariant> variantMap = productVariants.stream()
                                .collect(Collectors.toMap(ProductVariant::getId, Function.identity()));

                // Subtotal
                BigDecimal subtotal = BigDecimal.ZERO;
                for (CheckoutItemRequest item : request.getItems()) {
                        ProductVariant productVariant = variantMap.get(item.getVariantId());

                        if (productVariant == null) {
                                throw new BadRequestException(
                                                "Sản phẩm với variantId=" + item.getVariantId() + " không tồn tại");
                        }
                        // Check Stock Quantity
                        if (productVariant.getStockQuantity() < item.getQuantity()) {
                                throw new BadRequestException(
                                                "Sản phẩm '" + productVariant.getProduct().getName()
                                                                + "' không đủ số lượng");
                        }
                        BigDecimal amount = productVariant.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                        subtotal = subtotal.add(amount);

                        // Delete Stock
                        productVariant.setStockQuantity(productVariant.getStockQuantity() - item.getQuantity());
                }

                // Total Weight
                int totalWeight = 1000; // TODO: Phát triển thêm sau này giả định 1kg

                // Shipping Fee
                BigDecimal shippingFee = ghnShippingService.calculateShippingFee(address, totalWeight);

                // Apply Vouchers - TODO

                // Total Amount
                BigDecimal totalAmount = subtotal.add(shippingFee);

                // Create Order
                Order order = Order.builder()
                                .user(user)
                                .address(address)
                                .totalAmount(totalAmount)
                                .status(EOrderStatus.PENDING)
                                .shippingFee(shippingFee)
                                .paymentStatus(EPaymentStatus.PENDING)
                                .paymentMethod(request.getPaymentMethod())
                                .build();
                orderRepository.save(order);

                // Ghi lịch sử trạng thái: Đơn hàng được tạo
                orderHistoryService.recordStatusChange(order, null, EOrderStatus.PENDING, "Đơn hàng được tạo", userId);

                // Order Items
                List<OrderItem> orderItems = new ArrayList<>();
                for (CheckoutItemRequest item : request.getItems()) {
                        ProductVariant productVariant = variantMap.get(item.getVariantId());
                        orderItems.add(OrderItem.builder()
                                        .order(order)
                                        .variant(productVariant)
                                        .quantity(item.getQuantity())
                                        .price(productVariant.getPrice())
                                        .build());

                }
                orderItemRepository.saveAll(orderItems);
                productVariantRepository.saveAll(productVariants);

                // If have cartIds
                if (request.getCartItemIds() != null && !request.getCartItemIds().isEmpty()) {
                        List<Long> idsToDelete = request.getCartItemIds().stream()
                                        .map(item -> item.getId())
                                        .toList();
                        cartItemRepository.deleteAllByIdInBatch(idsToDelete);
                }

                IPaymentStrategy selectedStrategy = paymentStrategies.stream()
                                .filter(strategy -> strategy.getSupportedMethod() == request.getPaymentMethod())
                                .findFirst()
                                .orElseThrow(() -> new BadRequestException(
                                                "Phương thức thanh toán " + request.getPaymentMethod()
                                                                + " chưa được hỗ trợ!"));
                CheckoutResponse response = selectedStrategy.processPayment(order, userId);

                if (request.getPaymentMethod() != EPaymentMethod.COD) {
                        orderExpiryProducer.sendExpiryCheck(
                                        order.getId(),
                                        userId,
                                        request.getPaymentMethod());
                }

                return response;
        }

        @Override
        public CheckoutResult getPaymentStatus(Long orderId) {
                Order order = orderRepository.findById(orderId)
                                .orElseThrow(() -> new BadRequestException("Đơn hàng không tồn tại"));
                return CheckoutResult.builder()
                                .orderId(order.getId())
                                .paymentMethod(order.getPaymentMethod())
                                .paymentStatus(order.getPaymentStatus())
                                .build();
        }

        @Override
        @Transactional(readOnly = true)
        public List<OrderResponse> getUserOrders(Long userId, String status) {
                List<Order> orders;
                if (status == null || status.isEmpty()) {
                        orders = orderRepository.findByUserIdAndStatusInOrderByCreatedAtDesc(userId,
                                        List.of(EOrderStatus.PENDING, EOrderStatus.PREPARING, EOrderStatus.SHIPPING));

                } else {
                        EOrderStatus orderStatus = EOrderStatus.valueOf(status.toUpperCase());
                        orders = orderRepository.findByUserIdAndStatusOrderByCreatedAtDesc(userId, orderStatus);
                }

                return orderMapper.toOrderResponseList(orders);

        }

        @Override
        public OrderResponse getOrderDetail(Long orderId, Long userId) {
                Order order = orderRepository.findById(orderId)
                                .orElseThrow(() -> new BadRequestException("Đơn hàng không tồn tại"));
                if (!order.getUser().getId().equals(userId)) {
                        throw new BadRequestException("Đơn hàng không thuộc người dùng này");
                }
                return orderMapper.toOrderResponse(order);

        }

        @Override
        public void cancelOrder(Long orderId, Long userId, CancelOrderRequest request) {

                Order order = orderRepository.findById(orderId)
                                .orElseThrow(() -> new BadRequestException("Đơn hàng không tồn tại"));
                if (!order.getUser().getId().equals(userId)) {
                        throw new BadRequestException("Đơn hàng không thuộc người dùng này");
                }
                if (order.getStatus() != EOrderStatus.PENDING) {
                        throw new BadRequestException("Chỉ có thể hủy đơn hàng ở trạng thái chờ xác nhận");
                }

                EOrderStatus fromStatus = order.getStatus();
                order.setStatus(EOrderStatus.CANCELLED);
                order.setPaymentStatus(EPaymentStatus.FAILED);
                orderRepository.save(order);

                // Ghi lịch sử trạng thái
                orderHistoryService.recordStatusChange(order, fromStatus, EOrderStatus.CANCELLED, request.getReason(),
                                userId);

                // Restock Item
                for (OrderItem item : order.getItems()) {
                        ProductVariant variant = item.getVariant();
                        variant.setStockQuantity(variant.getStockQuantity() + item.getQuantity());
                }
                productVariantRepository.saveAll(order.getItems().stream().map(OrderItem::getVariant).toList());

        }

}
