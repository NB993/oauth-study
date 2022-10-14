package com.pio.oauth.auth.info;

import com.pio.oauth.auth.ProviderType;
import com.pio.oauth.auth.info.impl.GoogleMemberInfo;
import com.pio.oauth.auth.info.impl.KakaoMemberInfo;
import com.pio.oauth.auth.info.impl.NaverMemberInfo;
import java.util.Map;

public class OAuthMemberInfoFactory {
    public static OAuthMemberInfo createOAuthMemberInfo(ProviderType providerType, Map<String, Object> attributes) {
        switch (providerType) {
            case GOOGLE:
                return new GoogleMemberInfo(attributes);
            case NAVER:
                return new NaverMemberInfo(attributes);
            case KAKAO:
                return new KakaoMemberInfo(attributes);
            default: throw new IllegalArgumentException("Invalid Provider Type.");
        }
    }
}
