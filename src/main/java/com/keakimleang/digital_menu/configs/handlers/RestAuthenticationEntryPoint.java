package com.keakimleang.digital_menu.configs.handlers;

import jakarta.servlet.http.*;
import java.io.*;
import org.slf4j.*;
import org.springframework.security.core.*;
import org.springframework.security.web.*;
import org.springframework.stereotype.*;

@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private static final Logger logger = LoggerFactory.getLogger(RestAuthenticationEntryPoint.class);

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException e) throws IOException {
        logger.error("Responding with unauthorized error. Message - {}", e.getMessage());
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        response.getWriter().write("{\"statusCode\": 401, \"success\": false,  \"status\": \"FORBIDDEN_ACCESS_DENIED\", \"error\": \"User is not authorized or forbidden to access.\"}");
    }
}
