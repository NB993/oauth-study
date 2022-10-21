package com.pio.oauth.auth.info.impl;

import com.pio.oauth.auth.ProviderType;
import com.pio.oauth.auth.info.OAuthMemberInfo;
import com.pio.oauth.core.member.entity.Member;
import java.util.Map;

public class GoogleMemberInfo extends OAuthMemberInfo {

//    private ProviderType providerType = ProviderType.valueOf("GOOGLE");

    public GoogleMemberInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getMemberId() {
        return (String) attributes.get("sub");
    }

    @Override
    public String getName() {
        return (String) attributes.get("name");
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String getProfileUrl() {
        return (String) attributes.get("picture");
    }

//    @Override
//    public ProviderType getProviderType() {
//        return providerType;
//    }
}
