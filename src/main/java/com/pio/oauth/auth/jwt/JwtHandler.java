package com.pio.oauth.auth.jwt;

import com.pio.oauth.auth.info.OAuthMemberInfo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JwtHandler {

    private final JwtProperties jwtProperties;

    @Autowired
    private JwtHandler(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    public String createAccessToken(OAuthMemberInfo memberInfo, long expirationPeriod) {
        return Jwts.builder()
            .setHeaderParam("typ", "JWT")
            .setIssuer(jwtProperties.getIssuer())
            .claim("userId", memberInfo.getMemberId())
            .setExpiration(Date.from(Instant.now().plusMillis(expirationPeriod)))
            .signWith(createSecretKey())
            .compact();
    }

    public String createRefreshToken(long expirationPeriod) {
        return Jwts.builder()
            .setHeaderParam("typ", "JWT")
            .setIssuer(jwtProperties.getIssuer())
            .setExpiration(Date.from(Instant.now().plusMillis(expirationPeriod)))
            .signWith(createSecretKey())
            .compact();
    }

    private SecretKey createSecretKey() {
        return Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    public Claims decodeJwt(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(createSecretKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
    }
}

