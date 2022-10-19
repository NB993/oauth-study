package com.pio.oauth.auth;

import static com.pio.oauth.auth.jwt.JwtConst.ACCESS_TOKEN_EXPIRATION_PERIOD;
import static com.pio.oauth.auth.jwt.JwtConst.REFRESH_TOKEN_EXPIRATION_PERIOD;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    @GetMapping("/oauth/callback")
    public ResponseEntity<?> oauthLogin(@RequestParam String code, @RequestParam String providerType) {
        Token token = loginService.createToken(code, providerType);

        ResponseCookie cookie = ResponseCookie.from("access_token", token.getAccessToken())
            .maxAge(ACCESS_TOKEN_EXPIRATION_PERIOD)
            .path("/")
            .httpOnly(true)
            .build();

        ResponseCookie cookie2 = ResponseCookie.from("refresh_token", token.getRefreshToken())
            .maxAge(REFRESH_TOKEN_EXPIRATION_PERIOD)
            .path("/")
            .httpOnly(true)
            .build();

        return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY)
            .header(HttpHeaders.SET_COOKIE, cookie.toString())
            .header(HttpHeaders.SET_COOKIE, cookie2.toString())
            .header(HttpHeaders.LOCATION, "/api/test")
            .build();
    }

    @GetMapping("/api/test")
    public String test() {
        return "hi there";
    }

}
