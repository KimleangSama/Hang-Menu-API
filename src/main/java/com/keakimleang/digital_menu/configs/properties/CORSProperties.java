package com.keakimleang.digital_menu.configs.properties;

import java.util.*;
import lombok.*;
import org.springframework.boot.context.properties.*;

@Setter
@Getter
@ConfigurationProperties(prefix = "cors")
public class CORSProperties {
    private List<String> allowedOrigins;
}
