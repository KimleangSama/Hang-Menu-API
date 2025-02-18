package io.sovann.hang.api.configs.properties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@AllArgsConstructor
@ConfigurationProperties(prefix = "token")
public class TokenProperties {
    private Integer accessTokenExpiresHours;
    private Integer refreshTokenExpiresHours;
    private String domain;
}