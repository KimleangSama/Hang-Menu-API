package com.keakimleang.digital_menu;

import org.springframework.boot.builder.*;
import org.springframework.boot.web.servlet.support.*;

public class ServletInitializer extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(HangDigitalMenuApplication.class);
    }

}
