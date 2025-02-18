package io.sovann.hang.api.configs.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "loki")
public record LokiProperties(String url) {
}
