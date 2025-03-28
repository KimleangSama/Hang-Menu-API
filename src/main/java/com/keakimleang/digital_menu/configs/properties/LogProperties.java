package com.keakimleang.digital_menu.configs.properties;

import org.springframework.boot.context.properties.*;

@ConfigurationProperties(prefix = "log")
public record LogProperties(Loki loki, Logstash logstash) {

    public record Loki(String url) {
    }

    public record Logstash(String url) {
    }
}