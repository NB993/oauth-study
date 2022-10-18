package com.pio.oauth.auth;

import com.pio.oauth.auth.jwt.JwtConst;
import com.pio.oauth.auth.jwt.JwtHandler;
import com.pio.oauth.core.member.MemberRepository;
import com.pio.oauth.core.member.entity.Member;
import io.jsonwebtoken.Claims;
import java.util.NoSuchElementException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class LoginInterceptor implements HandlerInterceptor {

    private final JwtHandler jwtHandler;
    private final MemberRepository memberRepository;

    private static final String BEARER = "Bearer";
    private static final String MEMBER_ID = "memberId";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String userId = extractUserId(request);
        Member member = memberRepository.findByMemberId(userId)
            .orElseThrow(() -> new NoSuchElementException("no member"));

        request.setAttribute(MEMBER_ID, member.getMemberId());

        return true;
    }

    private String extractUserId(HttpServletRequest request) {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        verifyHeader(authHeader);

        Claims claims = jwtHandler.decodeJwt(authHeader.split(JwtConst.BEARER)[1]);
        return claims.get(MEMBER_ID, String.class);
    }


    private void verifyHeader(String authorization) {
        if (authorization == null || authorization.trim().isEmpty()) {
            throw new IllegalArgumentException("토큰이 전달되지 않았습니다.");
        }
        if (!authorization.startsWith(BEARER)) {
            throw new IllegalArgumentException("토큰 인증 방식은 Bearer 입니다.");
        }
    }
}
