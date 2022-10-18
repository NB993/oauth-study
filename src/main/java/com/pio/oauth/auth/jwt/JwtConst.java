package com.pio.oauth.auth.jwt;

import java.time.Duration;

public class JwtConst {

    public static final String BEARER = "Bearer ";
    public static final long ACCESS_TOKEN_EXPIRED_TIME = Duration.ofMinutes(30).toMillis();
    public static final long REFRESH_TOKEN_EXPIRED_TIME = Duration.ofDays(7).toMillis();
}
