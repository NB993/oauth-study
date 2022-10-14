package com.pio.oauth.auth.info.impl;

import com.pio.oauth.auth.info.OAuthMemberInfo;
import java.util.Map;

public class KakaoMemberInfo extends OAuthMemberInfo {

    public KakaoMemberInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getMemberId() {
        return attributes.get("id").toString();
    }

    @Override
    public String getName() {
        Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");
        if (properties == null) {
            return null;
        }

        return (String) properties.get("profile_nickname"); //name은 선택 제공정보, profile_nickname은 필수 제공 정보
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("account_email");
    }

    @Override
    public String getImageUrl() {
        Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");
        if (properties == null) {
            return null;
        }

        return (String) properties.get("profile_image");
    }
}
