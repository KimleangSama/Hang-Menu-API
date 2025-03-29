package com.keakimleang.digital_menu;

import com.redis.om.spring.annotations.*;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.cache.annotation.*;
import org.springframework.scheduling.annotation.*;

@SpringBootApplication
@EnableCaching
@EnableAsync
@EnableRedisEnhancedRepositories(basePackages = {
        "com.keakimleang.digital_menu.features"
})
public class HangDigitalMenuApplication {

    public static void main(String[] args) {
        SpringApplication.run(HangDigitalMenuApplication.class, args);
    }

}
