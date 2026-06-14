package com.vn.keycap_server.dto.response.media;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SavedMediaResponse {

    private Long id;
    private String url;
}
