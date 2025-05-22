package com.yas.webhook.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "yas.services")
public record ServiceUrlConfig(String location) {
}
