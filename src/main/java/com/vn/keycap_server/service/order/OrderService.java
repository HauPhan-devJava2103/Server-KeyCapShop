package com.vn.keycap_server.service.order;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.vn.keycap_server.dto.request.order.CheckoutRequest;
import com.vn.keycap_server.dto.request.order.PrepareOrderItemRequest;
import com.vn.keycap_server.dto.response.order.CheckoutItemResponse;
import com.vn.keycap_server.dto.response.order.CheckoutItemResponse.PrepareProductInfo;
import com.vn.keycap_server.dto.response.order.CheckoutResponse;
import com.vn.keycap_server.dto.response.order.PrepareCheckoutResponse;
import com.vn.keycap_server.exception.BadRequestException;
import com.vn.keycap_server.modal.Address;
import com.vn.keycap_server.modal.ProductImage;
import com.vn.keycap_server.modal.ProductVariant;
import com.vn.keycap_server.modal.ProductVariantAttribute;
import com.vn.keycap_server.modal.User;
import com.vn.keycap_server.repository.AddressRepository;
import com.vn.keycap_server.repository.ProductVariantRepository;
import com.vn.keycap_server.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService implements IOrderService {

    private final ProductVariantRepository productVariantRepository;
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    @Override
    public PrepareCheckoutResponse prepareOrder(List<PrepareOrderItemRequest> items, Long userId) {
        // Get VarientIds
        List<Long> varientIds = items.stream().map(PrepareOrderItemRequest::getVariantId).toList();
        // List ProductVarient
        List<ProductVariant> productVariants = productVariantRepository.findAllByWithProductAndAttributes(varientIds);

        // Map ProductVarient
        Map<Long, ProductVariant> variantMap = productVariants.stream()
                .collect(Collectors.toMap(ProductVariant::getId, Function.identity()));
        // Subtotal
        BigDecimal subtotal = BigDecimal.ZERO;

        List<CheckoutItemResponse> response = new ArrayList<>();
        for (PrepareOrderItemRequest item : items) {
            ProductVariant productVariant = variantMap.get(item.getVariantId());

            if (productVariant == null) {
                throw new BadRequestException("Sản phẩm với variantId=" + item.getVariantId() + " không tồn tại");
            }
            // Check Stock Quantity
            if (productVariant.getStockQuantity() < item.getQuantity()) {
                throw new BadRequestException(
                        "Sản phẩm '" + productVariant.getProduct().getName() + "' không đủ số lượng");
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

            response
                    .add(
                            CheckoutItemResponse.builder()
                                    .product(
                                            PrepareProductInfo.builder()
                                                    .id(productVariant.getId())
                                                    .name(productVariant.getProduct().getName())
                                                    .imageUrl(imageUrl)
                                                    .attributes(attributes)
                                                    .price(productVariant.getPrice())
                                                    .originalPrice(productVariant.getOriginalPrice())
                                                    .discountPercentage(productVariant.getPercentDiscount())
                                                    .build())
                                    .quantity(item.getQuantity())
                                    .amount(amount)
                                    .build());

        }

        // Price Shipping (Xử lý logic sau khi có API Ship)
        BigDecimal feeShip = BigDecimal.valueOf(30000);

        // Total Amount
        BigDecimal totalAmount = subtotal.add(feeShip);

        // Prepare response
        return PrepareCheckoutResponse.builder()
                .items(response)
                .subtotal(subtotal)
                .shippingFee(feeShip)
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

        return CheckoutResponse.builder()
                .paymentRequired(false)
                .orderId(null)
                .payUrl(null)
                .build();
    }

}
