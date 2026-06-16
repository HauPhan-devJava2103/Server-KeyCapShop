package com.vn.keycap_server.dto.response.media;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CloudinarySignatureResponse {

    private String signature;
    private Long timestamp;
    private String apiKey;
    private String cloudName;
    private Integer expiresIn;
}
