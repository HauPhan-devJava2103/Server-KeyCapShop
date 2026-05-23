package com.vn.keycap_server.utils;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ERole {
    @JsonProperty("admin")
    ADMIN,

    @JsonProperty("staff")
    STAFF,

    @JsonProperty("user")
    USER;
}
