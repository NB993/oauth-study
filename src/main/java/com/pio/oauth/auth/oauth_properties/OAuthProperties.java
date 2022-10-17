package com.pio.oauth.auth.oauth_properties;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.stereotype.Component;

@Getter
//@ConstructorBinding
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "oauth2.client")
public class OAuthProperties {

    private final Map<String, OAuthProvider> provider;

    public OAuthProvider getProvider(String name) {
        return provider.get(name);
    }
}
