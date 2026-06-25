package com.vn.keycap_server.dto.response.media;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Provider-neutral response returned to the frontend before direct media upload.
 * JsonProperty locks the FE-BE contract even if Java naming changes later.
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
