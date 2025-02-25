package io.sovann.hang.api.configs.securities;

public class UnRestrictedEndpoint {
    public static final String[] FREE_URLS = {
            "/api/v1/auth/**",
            "/error/**",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/swagger-resources/**",
            "/api-docs/**",
            "/aggregate/**",
            "/actuator/prometheus",
            "/actuator/health/**",
            "/api/v1/files/**",
            "/api/v1/menus/list/**",
            "/api/v1/categories/list/**",
            "/api/v1/stores/{slug}/**",
            "/api/v1/orders/create",
            "/api/v1/orders/{id}/menus",
    };

    public static final String LOGOUT_URL = "/api/v1/auth/logout";
}

