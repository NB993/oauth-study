package com.pio.oauth.auth.info;

import com.pio.oauth.auth.ProviderType;
import com.pio.oauth.core.member.entity.Member;
import java.util.Map;

public abstract class OAuthMemberInfo {

    protected Map<String, Object> attributes;
//    protected ProviderType providerType;

    public OAuthMemberInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public abstract String getMemberId();

    public abstract String getName();

    public abstract String getEmail();

    public abstract String getImageUrl();

//    public abstract ProviderType getProviderType();
}
