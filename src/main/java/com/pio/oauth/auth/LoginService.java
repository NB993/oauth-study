package com.pio.oauth.auth;

import static com.pio.oauth.auth.jwt.JwtConst.ACCESS_TOKEN_EXPIRATION_PERIOD;
import static com.pio.oauth.auth.jwt.JwtConst.BEARER;
import static com.pio.oauth.auth.jwt.JwtConst.REFRESH_TOKEN_EXPIRATION_PERIOD;

import com.pio.oauth.auth.info.OAuthMemberInfo;
import com.pio.oauth.auth.info.OAuthMemberInfoFactory;
import com.pio.oauth.auth.jwt.JwtHandler;
import com.pio.oauth.auth.domain.properties.OAuthProperties;
import com.pio.oauth.auth.domain.provider.OAuthProvider;
import com.pio.oauth.auth.jwt.Token;
import com.pio.oauth.core.member.MemberRepository;
import com.pio.oauth.core.member.entity.Member;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final JwtHandler jwtHandler;
    private final OAuthProperties properties;
    private final MemberRepository memberRepository;
    private final RedisTemplate<String, String> redisTemplate;

    private static final ParameterizedTypeReference<Map<String, Object>> PARAMETERIZED_RESPONSE_TYPE =
        new ParameterizedTypeReference<>() {};

    public Token createToken(String code, String providerType) {
        OAuthProvider provider = properties.getProvider(providerType);
        OAuthAccessToken oauthAccessToken = getOauthAccessToken(code, provider);
        Map<String, Object> memberInfo = getMemberInfo(oauthAccessToken, provider);
        OAuthMemberInfo oAuthMemberInfo = OAuthMemberInfoFactory.createOAuthMemberInfo(
            ProviderType.valueOf(providerType.toUpperCase()),
            memberInfo
        );
        saveMember(oAuthMemberInfo);

        String refreshToken = jwtHandler.createRefreshToken(REFRESH_TOKEN_EXPIRATION_PERIOD);
        redisTemplate.opsForValue().set(oAuthMemberInfo.getMemberId(), refreshToken);

        return new Token(
            jwtHandler.createAccessToken(oAuthMemberInfo, ACCESS_TOKEN_EXPIRATION_PERIOD),
            refreshToken
        );
    }

    private OAuthAccessToken getOauthAccessToken(String code, OAuthProvider provider) {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        Map<String, String> header = new HashMap<>();
        header.put(HttpHeaders.ACCEPT, "application/json");
        headers.setAll(header);

        MultiValueMap<String, String> requestPayloads = createRequestPayloads(code, provider);
        HttpEntity<?> request = new HttpEntity<>(requestPayloads, headers);

        ResponseEntity<?> response = new RestTemplate().postForEntity(
            provider.getAccessTokenPath(), request, OAuthAccessToken.class);

        return (OAuthAccessToken) response.getBody();
    }

    private MultiValueMap<String, String> createRequestPayloads(String code, OAuthProvider provider) {
        MultiValueMap<String, String> requestPayloads = new LinkedMultiValueMap<>();
        Map<String, String> requestPayload = new HashMap<>();
        requestPayload.put("client_id", provider.getClientId());
        requestPayload.put("client_secret", provider.getClientSecret());
        requestPayload.put("code", code);
        requestPayload.put("grant_type", "authorization_code");
        requestPayload.put("redirect_uri", provider.getRedirectUri());
        requestPayloads.setAll(requestPayload);
        return requestPayloads;
    }

    private Map<String, Object> getMemberInfo(OAuthAccessToken OAuthAccessToken, OAuthProvider provider) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(HttpHeaders.AUTHORIZATION, BEARER + OAuthAccessToken.getAccessToken());
        HttpEntity<?> request = new HttpEntity<>(httpHeaders);

        ResponseEntity<Map<String, Object>> response = new RestTemplate().exchange(
            provider.getResourcePath(),
            HttpMethod.GET,
            request,
            PARAMETERIZED_RESPONSE_TYPE
        );
        return response.getBody();
    }

    //todo: oauth 계정의 이메일이나 이름 등의 프로필 정보가 가입 이후로 변경되었을 수 있음. 때문에 이미 가입되어 있더라도 업데이트가 필요할 듯.
    private void saveMember(OAuthMemberInfo memberInfo) {
        String memberId = memberInfo.getMemberId();
        if (!memberRepository.existsMemberByMemberId(memberId)) {
            Member member = new Member(
                memberInfo.getMemberId(),
                memberInfo.getEmail(),
                memberInfo.getName()
            );
            memberRepository.save(member);
        }
    }
}
