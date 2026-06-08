package com.vn.keycap_server.modal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * UserAddress đại diện cho địa chỉ giao hàng đã lưu của người dùng.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_addresses", indexes = {
        @Index(name = "idx_user_addresses_user_default", columnList = "user_id,is_default")
})
public class UserAddress extends AbstractEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "phone", nullable = false)
    private String phone;

    @Column(name = "province_code", nullable = false)
    private String provinceCode;

    @Column(name = "province_name", nullable = false)
    private String provinceName;

    @Column(name = "district_code", nullable = false)
    private String districtCode;

    @Column(name = "district_name", nullable = false)
    private String districtName;

    @Column(name = "ward_code", nullable = false)
    private String wardCode;

    @Column(name = "ward_name", nullable = false)
    private String wardName;

    @Column(name = "street", nullable = false, columnDefinition = "TEXT")
    private String street;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Builder.Default
    @Column(name = "is_default", nullable = false)
    private Boolean defaultAddress = false;
}
