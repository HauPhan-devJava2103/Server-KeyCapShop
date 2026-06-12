package com.vn.keycap_server.modal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "addresses")
public class Address extends AbstractEntity {

    @Column(name = "full_name", nullable = false)
    private String fullName;
    @Column(name = "phone", nullable = false)
    private String phone;
    // Province
    @Column(name = "province_code", nullable = false)
    private String provinceCode;
    @Column(name = "province_name", nullable = false)
    private String provinceName;

    // District
    @Column(name = "district_code", nullable = false)
    private String districtCode;
    @Column(name = "district_name", nullable = false)
    private String districtName;

    // Ward
    @Column(name = "ward_code", nullable = false)
    private String wardCode;
    @Column(name = "ward_name", nullable = false)
    private String wardName;

    // Detail
    @Column(name = "street")
    private String street;
    @Column(name = "full_address", columnDefinition = "TEXT")
    private String fullAddress;
    @Column(name = "latitude")
    private Double latitude;
    @Column(name = "longitude")
    private Double longitude;
    @Column(name = "is_default", nullable = false)
    private Boolean isDefault = false;

    // Relationship
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

}
