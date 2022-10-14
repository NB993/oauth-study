package com.pio.oauth.auth.info.impl;

import com.pio.oauth.auth.info.OAuthMemberInfo;
import com.pio.oauth.core.member.entity.Member;
import java.util.Map;

public class GoogleMemberInfo extends OAuthMemberInfo {

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
    public String getImageUrl() {
        return (String) attributes.get("picture");
    }
}
