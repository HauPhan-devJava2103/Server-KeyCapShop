package com.vn.keycap_server.dto.response.payment.paypal;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PayPalCreateResponse {
    private String id;
    private String status;
    private List<Link> links;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Link {
        private String href; // URL
        private String rel; // "self", "approve", "capture"
        private String method;
    }

    // URL redirect user sang PayPal
    public String getApproveUrl() {
        if (links == null)
            return null;
        return links.stream()
                .filter(link -> "approve".equals(link.getRel()))
                .findFirst()
                .map(Link::getHref)
                .orElse(null);
    }

}
