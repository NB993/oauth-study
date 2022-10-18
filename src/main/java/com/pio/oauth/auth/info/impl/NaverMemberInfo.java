package com.pio.oauth.auth.info.impl;

import com.pio.oauth.auth.ProviderType;
import com.pio.oauth.auth.info.OAuthMemberInfo;
import java.util.Map;

public class NaverMemberInfo extends OAuthMemberInfo {

//    private ProviderType providerType = ProviderType.valueOf("NAVER");

    public NaverMemberInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getMemberId() {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        if (response == null) {
            return null;
        }

        return (String) response.get("id");
    }

    @Override
    public String getName() {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");
        if (response == null) {
            return null;
        }

        return (String) response.get("nickname");
    }

    @Override
    public String getEmail() {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");
        if (response == null) {
            return null;
        }

        return (String) response.get("email");
    }

    @Override
    public String getImageUrl() {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");
        if (response == null) {
            return null;
        }

        return (String) response.get("profile_image");
    }

//    @Override
//    public ProviderType getProviderType() {
//        return providerType;
//    }
}
