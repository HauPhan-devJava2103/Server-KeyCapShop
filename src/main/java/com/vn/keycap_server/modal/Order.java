package com.vn.keycap_server.modal;

import java.math.BigDecimal;

import com.vn.keycap_server.utils.EOrderStatus;
import com.vn.keycap_server.utils.EPaymentMethod;
import com.vn.keycap_server.utils.EPaymentStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Order là Entity đại diện cho bảng 'orders' trong database.
 * Lưu trữ thông tin một đơn hàng do người dùng đặt, bao gồm trạng thái,
 * tổng tiền và thông tin giao hàng.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orders")
public class Order extends AbstractEntity {

    // Người dùng đặt đơn hàng này
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Tổng giá trị của đơn hàng
    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount;

    // Trạng thái đơn hàng (ví dụ: PENDING, SHIPPING, DELIVERED, CANCELLED)
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private EOrderStatus status;

    // Địa chỉ giao hàng của người dùng
    @Column(name = "shipping_address", columnDefinition = "TEXT")
    private String shippingAddress;

    // Số điện thoại liên hệ giao hàng
    @Column(name = "phone_number")
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method")
    private EPaymentMethod paymentMethod;

    // Trạng thái thanh toán (PENDING, PAID, FAILED)
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status")
    private EPaymentStatus paymentStatus;

    @Column(name = "transaction_id")
    private String transactionId;

    // Địa chỉ giao hàng
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id")
    private Address address;

    // Danh sách sản phẩm trong đơn hàng
    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
    private java.util.List<OrderItem> items;
}
