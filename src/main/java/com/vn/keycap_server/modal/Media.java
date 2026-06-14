package com.vn.keycap_server.modal;

import com.vn.keycap_server.utils.EMediaResourceType;
import com.vn.keycap_server.utils.EMediaStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "medias", uniqueConstraints = {
        @UniqueConstraint(name = "uk_medias_public_id", columnNames = "public_id")
})
public class Media extends AbstractEntity {

    @Column(name = "public_id", nullable = false)
    private String publicId;

    @Column(name = "secure_url", nullable = false, columnDefinition = "TEXT")
    private String secureUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "resource_type", nullable = false)
    private EMediaResourceType resourceType;

    @Column(name = "format", nullable = false, length = 50)
    private String format;

    @Column(name = "bytes", nullable = false)
    private Long bytes;

    @Column(name = "width")
    private Integer width;

    @Column(name = "height")
    private Integer height;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private EMediaStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by", nullable = false)
    private User uploadedBy;
}
