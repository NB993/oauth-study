package com.pio.oauth.auth;

import static com.pio.oauth.auth.jwt.JwtConst.ACCESS_TOKEN_EXPIRATION_PERIOD;
import static com.pio.oauth.auth.jwt.JwtConst.REFRESH_TOKEN_EXPIRATION_PERIOD;

import com.pio.oauth.auth.jwt.Token;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

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
            .build();

        ResponseCookie cookie2 = ResponseCookie.from("refresh_token", token.getRefreshToken())
            .maxAge(REFRESH_TOKEN_EXPIRATION_PERIOD)
            .path("/")
            .build();

        return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY)
            .header(HttpHeaders.SET_COOKIE, cookie.toString())
            .header(HttpHeaders.SET_COOKIE, cookie2.toString())
            .header(HttpHeaders.LOCATION, "/test2")
            .build();
    }

    @GetMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        //todo: 엑세스토큰과 리프레시토큰 쿠키를 지우고, 레디스에 저장된 리프레시 토큰도 삭제한다.
        String token = request.getHeader("Refresh-Token");
        String parsedRefreshToken = token.split("Bearer ")[1];

        ResponseCookie cookie = ResponseCookie.from("access_token", null)
            .maxAge(0)
            .path("/")
            .build();

        ResponseCookie cookie2 = ResponseCookie.from("refresh_token", null)
            .maxAge(0)
            .path("/")
            .build();

        return null;
    }

    @GetMapping("/test2")
    public ModelAndView page() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("test.html");
        return modelAndView;
    }

    @GetMapping("/api/test")
    public String test() {
        return "hi there";
    }
}
