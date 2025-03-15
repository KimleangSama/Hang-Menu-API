package io.sovann.hang.api;

import com.redis.om.spring.annotations.EnableRedisEnhancedRepositories;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
@EnableRedisEnhancedRepositories(basePackages = {
        "io.sovann.hang.api.features"
})
public class HangApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(HangApiApplication.class, args);
    }

}
