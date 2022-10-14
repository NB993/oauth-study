package com.pio.oauth.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class AccessToken {

    @JsonProperty("access_token")
    private String accessToken;
}
