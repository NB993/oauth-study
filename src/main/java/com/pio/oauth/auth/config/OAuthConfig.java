package com.pio.oauth.auth.config;

import com.pio.oauth.auth.oauth_properties.OAuthProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@RequiredArgsConstructor
@PropertySource(value = {"application-local.yml"}, factory = YmlLoadFactory.class)
@EnableConfigurationProperties(OAuthProperties.class)
public class OAuthConfig {

    private final OAuthProperties properties;
}
