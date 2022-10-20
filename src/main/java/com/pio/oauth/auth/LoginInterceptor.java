package com.pio.oauth.auth;

import com.pio.oauth.auth.jwt.JwtConst;
import com.pio.oauth.auth.jwt.JwtHandler;
import com.pio.oauth.core.member.MemberRepository;
import com.pio.oauth.core.member.entity.Member;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import java.util.NoSuchElementException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class LoginInterceptor implements HandlerInterceptor {

    private final JwtHandler jwtHandler;
    private final RedisTemplate<String, String> redisTemplate;
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

        Claims claims = null;
        try {
            claims = jwtHandler.decodeJwt(authHeader.split(JwtConst.BEARER)[1]);
        } catch (ExpiredJwtException e) {
            //넘어온 리프레시토큰과 레디스의 리프레시 토큰을 비교해야 함.
            //레디스의 리프레시 토큰 키값이 memberId값임.

            //1. access token에 원래 memberId가 claim으로 들어가 있는데, 기간 만료로 인해서 추출하지 못하였음.
            //그럼 리프레시토큰에서 memberId를 claim으로 꺼냄.
            //비교 가능.

            String refreshToken = request.getHeader("CustomHeader");
            Claims refreshTokenClaims = jwtHandler.decodeJwt(refreshToken.split(JwtConst.BEARER)[1]);

            //2. 리프레시 토큰도 만료됐다? 여기서 에러 빵
            //이때는 에러 리턴하고 재로그인 유도

            String memberId = refreshTokenClaims.get(MEMBER_ID, String.class);
            String storedToken = redisTemplate.opsForValue().get(String.valueOf(memberId));

            if (refreshToken.equals(storedToken)) {
                //엑세스 토큰 재발급
            }
        }
        return claims.get(MEMBER_ID, String.class); //access token
    }

    //todo: 이 예외케이스들이 docdeJwt() -> parseClaimsJws()에서 발생할 예외들인 것 같음. 확인 후 맞다면 제거.
    private void verifyHeader(String authorization) {
        if (authorization == null || authorization.trim().isEmpty()) {
            throw new IllegalArgumentException("토큰이 전달되지 않았습니다.");
        }
        if (!authorization.startsWith(BEARER)) {
            throw new IllegalArgumentException("토큰 인증 방식은 Bearer 입니다.");
        }
    }
}
