package com.vn.keycap_server.service.order;

import java.util.List;

import org.springframework.stereotype.Service;

import com.vn.keycap_server.modal.OrderItem;
import com.vn.keycap_server.modal.ProductVariant;
import com.vn.keycap_server.utils.EProductStatus;
import com.vn.keycap_server.repository.ProductVariantRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderStockService {

    private final ProductVariantRepository productVariantRepository;

    public void restockOrderItems(List<OrderItem> items) {
        for (OrderItem item : items) {
            ProductVariant variant = item.getVariant();
            variant.setStockQuantity(variant.getStockQuantity() + item.getQuantity());

            if (variant.getProduct().getStatus() == EProductStatus.UNAVAILABLE) {
                variant.getProduct().setStatus(EProductStatus.AVAILABLE);
            }
        }

        List<ProductVariant> variants = items.stream()
                .map(OrderItem::getVariant)
                .toList();
        productVariantRepository.saveAll(variants);
    }
}
