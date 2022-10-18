package com.pio.oauth.auth.domain.properties;

import com.pio.oauth.auth.domain.provider.OAuthProvider;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "oauth2.client")
public class OAuthProperties {

    private final Map<String, OAuthProvider> provider;

    public OAuthProvider getProvider(String name) {
        return provider.get(name);
    }
}
