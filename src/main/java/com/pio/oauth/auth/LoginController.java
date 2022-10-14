package com.pio.oauth.auth;

import com.pio.oauth.auth.info.OAuthMemberInfo;
import com.pio.oauth.auth.info.OAuthMemberInfoFactory;
import com.pio.oauth.auth.jwt.JWT;
import com.pio.oauth.auth.jwt.JwtHandler;
import com.pio.oauth.auth.oauth_properties.OAuthProperties;
import com.pio.oauth.auth.oauth_properties.OAuthProvider;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
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
    private final JwtHandler jwtUtil;
    private final OAuthProperties properties;

    private static final int EXPIRED_TIME = 86_400;

    @GetMapping("/oauth/callback")
    public ResponseEntity<?> oauthLogin(@RequestParam String code, @RequestParam String providerType) {
        OAuthProvider provider = properties.getProvider(providerType);
        AccessToken accessToken = loginService.getAccessToken(code, provider);
        Map<String, Object> memberInfo = loginService.getMemberInfo(accessToken, provider);

        OAuthMemberInfo oAuthMemberInfo = OAuthMemberInfoFactory.createOAuthMemberInfo(
            ProviderType.valueOf(providerType),
            memberInfo
        );

        loginService.saveMember(oAuthMemberInfo);
        JWT jwt = jwtUtil.createToken(oAuthMemberInfo);

        ResponseCookie cookie = ResponseCookie.from("auth_token", jwt.getJwt())
            .maxAge(EXPIRED_TIME)
            .path("/")
            .build();

        return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY)
            .header(HttpHeaders.SET_COOKIE, cookie.toString())
            .header(HttpHeaders.LOCATION, "/")
            .build();
    }

    @GetMapping("/api/test")
    public ResponseEntity<?> test(@RequestParam String code) {
        return null;
    }

}
