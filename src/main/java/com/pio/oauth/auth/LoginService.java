package com.pio.oauth.auth;

import com.pio.oauth.auth.info.OAuthMemberInfo;
import com.pio.oauth.auth.jwt.JwtProperties;
import com.pio.oauth.auth.oauth_properties.KakaoProperties;
import com.pio.oauth.auth.oauth_properties.OAuthProperties;
import com.pio.oauth.auth.oauth_properties.OAuthProvider;
import com.pio.oauth.core.member.MemberRepository;
import com.pio.oauth.core.member.entity.Member;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final MemberRepository memberRepository;

    private static final ParameterizedTypeReference<Map<String, Object>> PARAMETERIZED_RESPONSE_TYPE =
        new ParameterizedTypeReference<Map<String, Object>>() {};

    private static final String GITHUB_AUTHORIZATION_SERVER_URL = "https://github.com/login/oauth/access_token";
    private static final String GITHUB_RESOURCE_SERVER_API_URL = "https://api.github.com/user";

    public AccessToken getAccessToken(String code, OAuthProvider provider) {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        Map<String, String> header = new HashMap<>();
        header.put("Accept", "application/json");
        headers.setAll(header);

        MultiValueMap<String, String> requestPayloads = new LinkedMultiValueMap<>();
        Map<String, String> requestPayload = new HashMap<>();
        requestPayload.put("client_id", provider.getClientId());
        requestPayload.put("client_secret", provider.getClientSecret());
        requestPayload.put("code", code);
        requestPayload.put("grant_type", "authorization_code");
        requestPayload.put("redirect_uri", provider.getRedirectUri());
        requestPayloads.setAll(requestPayload);

        HttpEntity<?> request = new HttpEntity<>(requestPayloads, headers);

        ResponseEntity<?> response = new RestTemplate().postForEntity(
            provider.getAccessTokenPath(), request, AccessToken.class);

//        RestTemplate restTemplate = new RestTemplate();
//        ResponseEntity<Map<String, Object>> response = restTemplate.exchange((RequestEntity<?>) request, PARAMETERIZED_RESPONSE_TYPE);

        return (AccessToken) response.getBody();
    }

    public Map<String, Object> getMemberInfo(AccessToken accessToken, OAuthProvider provider) {
        HttpHeaders httpHeaders = new HttpHeaders();
        //github
//        httpHeaders.set(HttpHeaders.AUTHORIZATION, "token " + accessToken.getAccessToken());

        //kakao
        httpHeaders.set(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken.getAccessToken());

        HttpEntity<?> request = new HttpEntity<>(httpHeaders);
        //github
//        ResponseEntity<Map<String, Object>> response = new RestTemplate().exchange(
//            GITHUB_RESOURCE_SERVER_API_URL,
//            HttpMethod.GET,
//            request,
//            PARAMETERIZED_RESPONSE_TYPE
//        );

        //kakao
        ResponseEntity<Map<String, Object>> response = new RestTemplate().exchange(
            provider.getResourcePath(),
            HttpMethod.GET,
            request,
            PARAMETERIZED_RESPONSE_TYPE
        );
        return response.getBody();
    }

    public void saveMember(OAuthMemberInfo memberInfo) {
        String memberId = memberInfo.getMemberId();
        if (!memberRepository.existsByMemberId(memberId)) {
            Member member = new Member(memberInfo.getMemberId(), memberInfo.getEmail(), memberInfo.getName());
            memberRepository.save(member);
        }
    }

}
