package com.vn.keycap_server.utils;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum EGender {
    @JsonProperty("male")
    MALE,
    @JsonProperty("female")
    FEMALE,
    @JsonProperty("other")
    OTHER;
}
