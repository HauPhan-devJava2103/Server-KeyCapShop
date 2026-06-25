package com.vn.keycap_server.dto.response.media;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response trung lập trả về cho frontend trước khi upload media trực tiếp.
 * JsonProperty giúp khóa tên field JSON để không làm lệch contract FE-BE.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaUploadSignatureResponse {

    @JsonProperty("signature")
    private String signature;

    @JsonProperty("timestamp")
    private Long timestamp;

    @JsonProperty("apiKey")
    private String apiKey;

    @JsonProperty("cloudName")
    private String cloudName;

    @JsonProperty("expiresIn")
    private Integer expiresIn;
}
