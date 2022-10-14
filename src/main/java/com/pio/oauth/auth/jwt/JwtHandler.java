package com.pio.oauth.auth.jwt;

import com.pio.oauth.auth.info.OAuthMemberInfo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
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

    //todo: 공통 UserInfo를 입력받도록 변경
    public JWT createToken(OAuthMemberInfo memberInfo) {
        String jwt = Jwts.builder()
            .setHeaderParam("typ", "JWT")
            .setIssuer(jwtProperties.getIssuer())
            .claim("userId", memberInfo.getMemberId())
            .claim("userName", memberInfo.getName())
            .claim("userEmail", memberInfo.getEmail())
            .signWith(createSecretKey())
            .compact();
        return new JWT(jwt);
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

