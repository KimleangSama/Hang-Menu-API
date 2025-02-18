package io.sovann.hang.api.configs.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Setter
@Getter
@ConfigurationProperties(prefix = "cors")
public class CORSProperties {
    private List<String> allowedOrigins;
}
