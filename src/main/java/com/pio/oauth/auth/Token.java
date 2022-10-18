package com.pio.oauth.auth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class Token {

    private final String accessToken;
    private final String refreshToken;
}
