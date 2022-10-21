package com.pio.oauth.auth;

import static com.pio.oauth.auth.jwt.JwtConst.*;

import com.pio.oauth.auth.info.OAuthMemberInfo;
import com.pio.oauth.auth.jwt.JwtConst;
import com.pio.oauth.auth.jwt.JwtHandler;
import com.pio.oauth.core.member.MemberRepository;
import com.pio.oauth.core.member.entity.Member;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import java.util.NoSuchElementException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

//최초 앱 실행
//로그인
/*
* 액세스(30분), 리프레시 토큰(1주일) 발급
*   사용자가 어떤 작업을 할 때마다 엑세스 토큰을 재발급하여 로그인이 끊기지 않도록 함.
*   아무 작업도 없어서 엑세스 토큰 만료된 후 어떤 요청을 하려고 한다.
*       같이 넘어온 리프레시 토큰을 보고 만료가 안됐으면 리프레시 토큰 검증 후 엑세스 토큰 재발급.
*       리프레시 토큰도 만료 됐으면 로그인이 만료 되었씁니다. -> 재로그인 시킴.
*   로그인 할 때마다는 엑세스 토큰과 리프레시토큰을 항상 같이 재발급.
*
* 최초 로그인 이후 앱 실행(로그아웃 안 하고 끈 경우)
*
* */

@Component
@RequiredArgsConstructor
public class LoginInterceptor implements HandlerInterceptor {

    private final JwtHandler jwtHandler;
    private final RedisTemplate<String, String> redisTemplate;
    private final MemberRepository memberRepository;

    private static final String BEARER = "Bearer ";
    private static final String MEMBER_ID = "memberId";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        String parsedAccessToken = accessToken.split(JwtConst.BEARER)[1];

        try {
            Claims claims = jwtHandler.decodeJwt(parsedAccessToken);
            String memberId = claims.get(MEMBER_ID, String.class);
            Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new NoSuchElementException("no member"));

            request.setAttribute(MEMBER_ID, member.getMemberId());
        } catch(ExpiredJwtException e) {
            //엑세스 토큰 만료시 리프레시 토큰 확인
            String refreshToken = request.getHeader("Refresh-Token");
            String parsedRefreshToken = refreshToken.split(JwtConst.BEARER)[1];

            Claims claims = jwtHandler.decodeJwt(parsedRefreshToken);
            String memberId = claims.get(MEMBER_ID, String.class);
            checkRefreshToken(memberId, parsedRefreshToken); //decodeJwt()에서 ExpiredJwtException이 안터지면 아래 코드로 진행되는 거고 아니면 핸들러로 넘어가서 재로그인 시킴.
            reissueTokens(memberId, response);
        }

        return true;
    }

    private void reissueTokens(String memberId, HttpServletResponse response) {
        String reissuedAccessToken = jwtHandler.createToken(memberId, ACCESS_TOKEN_EXPIRATION_PERIOD);
        String reissuedRefreshToken = jwtHandler.createToken(memberId, REFRESH_TOKEN_EXPIRATION_PERIOD);
        response.addCookie(new Cookie("access_token", reissuedAccessToken));
        response.addCookie(new Cookie("refresh_token", reissuedRefreshToken));
    }

    private void checkRefreshToken(String memberId, String refreshToken) {
        String storedRefreshToken = redisTemplate.opsForValue().get(memberId);
        if (storedRefreshToken == null) {
            //레디스에 회원id로 등록된 리프레시 토큰이 없는 건 레디스에 저장된 리프레시 토큰의 저장 기간이 만료된 경우.
            throw new NoSuchElementException("storedRefreshToken 만료. 재로그인 요청.");
        }
        if (!storedRefreshToken.equals(refreshToken)) {
            //레디스에 회원 id로 등록된 리프레시 토큰과 넘어온 리프레시 토큰이 다를 때
            throw new IllegalArgumentException("리프레시 토큰이 일치하지 않습니다. 다시 로그인 해 주세요.");
        }
        //체크 완료 -> 돌아가서 엑세스 토큰 재발급 해줌.
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
